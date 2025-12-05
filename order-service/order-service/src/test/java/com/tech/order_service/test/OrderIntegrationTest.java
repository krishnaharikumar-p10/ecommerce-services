package com.tech.order_service.test;

import com.tech.order_service.dto.*;
import com.tech.order_service.exceptions.OutOfStockException;
import com.tech.order_service.exceptions.ProductUnavailableException;
import com.tech.order_service.model.Orders;
import com.tech.order_service.repository.OrderRepository;
import com.tech.order_service.service.OrderProducer;
import com.tech.order_service.service.ExternalServiceValidation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @MockitoBean
    private OrderProducer orderProducer;

    @MockitoBean
    private ExternalServiceValidation externalServiceValidation;

    private static final String API_ENDPOINT = "/api/order";
    private static final String ORDER_JSON = """
            {
              "address":"Delivery address",
              "orderItems":[{"skuCode": "SKU123","quantity": 3}]
            }
            """;
            
    private static final String TEST_USER_ID = "101"; 
    private static final String TEST_USERNAME = "testuser_via_header"; 

    @AfterEach
    void cleanup() {
        orderRepository.deleteAll();
    }

    @Test
    void placeOrder_Success_ShouldCreateOrder() throws Exception {

        BigDecimal expectedPrice = BigDecimal.valueOf(499.99);
        ProductResponse productResponse = ProductResponse.builder()
                .skuCode("SKU123")
                .price(expectedPrice)
                .name("Test Product")
                .build();
                
        when(externalServiceValidation.validateProduct(anyString(), anyString()))
                .thenReturn(productResponse);

        InventoryResponse inventoryResponse = new InventoryResponse();
        inventoryResponse.setSkuCode("SKU123");
        inventoryResponse.setAvailableQuantity(50); 
        
        when(externalServiceValidation.validateInventory(anyString(), anyInt(), anyString()))
                .thenReturn(inventoryResponse);

        doNothing().when(orderProducer).sendOrderEvent(any(OrderEventMessage.class));
        mockMvc.perform(post(API_ENDPOINT)
                        .header("X-Correlation-Id", "test-corr-id")
                        .header("X-USER-ID", TEST_USER_ID)        
                        .header("X-USERNAME", TEST_USERNAME)      
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ORDER_JSON))
                
       
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.orderItems[0].price").value(499.99))
                .andExpect(jsonPath("$.message").value("Order created successfully. Proceed to payment."));

       
        verify(externalServiceValidation, times(1)).validateProduct(eq("SKU123"), anyString());
        verify(externalServiceValidation, times(1)).validateInventory(eq("SKU123"), eq(3), anyString());

   
        verify(orderProducer, times(1)).sendOrderEvent(any(OrderEventMessage.class));

   
        List<Orders> orders = orderRepository.findAll();
        assertThat(orders).hasSize(1);
        
        assertThat(orders.get(0).getCustomerId()).isEqualTo(Integer.valueOf(TEST_USER_ID));
        assertThat(orders.get(0).getCustomerName()).isEqualTo(TEST_USERNAME);
        assertThat(orders.get(0).getStatus()).isEqualTo("CREATED");
    }
    
    @Test
    void placeOrder_ProductUnavailable_ShouldReturnBadRequest() throws Exception {

    
        when(externalServiceValidation.validateProduct(anyString(), anyString()))
                .thenThrow(new ProductUnavailableException("Product unavailable: SKU777"));

        mockMvc.perform(post(API_ENDPOINT)
                        .header("X-Correlation-Id", "test-corr-id")
                        .header("X-USER-ID", TEST_USER_ID)
                        .header("X-USERNAME", TEST_USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ORDER_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Product Unavailable"))
                .andExpect(jsonPath("$.detail").value("Product unavailable: SKU777"))
                .andExpect(jsonPath("$.instance").value(API_ENDPOINT));

        List<Orders> orders = orderRepository.findAll();
        assertThat(orders).isEmpty();

        verify(orderProducer, never()).sendOrderEvent(any());
    }
    
    @Test
    void placeOrder_OutOfStock_ShouldReturnBadRequest() throws Exception {

        
        BigDecimal expectedPrice = BigDecimal.valueOf(499.99);
        ProductResponse productResponse = ProductResponse.builder()
                .skuCode("SKU123")
                .price(expectedPrice)
                .name("Test Product")
                .build();
        when(externalServiceValidation.validateProduct(anyString(), anyString()))
                .thenReturn(productResponse);

    
        when(externalServiceValidation.validateInventory(anyString(), anyInt(), anyString()))
                .thenThrow(new OutOfStockException("Out of stock: SKU123"));

        mockMvc.perform(post(API_ENDPOINT)
                        .header("X-Correlation-Id", "test-corr-id")
                        .header("X-USER-ID", TEST_USER_ID)
                        .header("X-USERNAME", TEST_USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ORDER_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Out of Stock"))
                .andExpect(jsonPath("$.detail").value("Out of stock: SKU123"))
                .andExpect(jsonPath("$.instance").value(API_ENDPOINT));

    
        List<Orders> orders = orderRepository.findAll();
        assertThat(orders).isEmpty();
        verify(orderProducer, never()).sendOrderEvent(any());
    }


}