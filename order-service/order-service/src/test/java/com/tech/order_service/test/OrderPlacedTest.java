package com.tech.order_service.test;

import com.tech.order_service.client.InventoryServiceClient;
import com.tech.order_service.client.ProductServiceClient;
import com.tech.order_service.dto.InventoryResponse;
import com.tech.order_service.dto.OrderItemsDTO;
import com.tech.order_service.dto.OrderPlacedResponse;
import com.tech.order_service.dto.ProductResponse;
import com.tech.order_service.model.Orders;
import com.tech.order_service.repository.OrderRepository;
import com.tech.order_service.service.OrderProducer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderPlacedTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @MockitoBean
    private OrderProducer mockProducer;

    @MockitoBean
    private ProductServiceClient productServiceClient;

    @MockitoBean
    private InventoryServiceClient inventoryServiceClient;

    private static final String SKU_CODE = "SKU456";
    private static final String ORDER_JSON =
            "{ \"orderItems\": [{\"skuCode\":\"" + SKU_CODE + "\",\"price\":40000.0,\"quantity\":1}] }";

    @AfterEach
    void teardown() {
        orderRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"CUSTOMER"})
    void placeOrder_Success_ShouldConfirmOrderAndCallKafka() throws Exception {

        ProductResponse product = new ProductResponse();
        product.setId("P1002");
        product.setName("OnePlus 12");
        product.setDescription("Fast and smooth user experience");
        product.setPrice(new BigDecimal("40000.0"));
        product.setSkuCode(SKU_CODE);

        when(productServiceClient.getProduct(anyString(), anyString()))
                .thenReturn(Optional.of(product));

      
        InventoryResponse inventory = new InventoryResponse();
        inventory.setId(1L);
        inventory.setSkuCode(SKU_CODE);
        inventory.setQuantity(30);

        when(inventoryServiceClient.getInventory(anyString(), anyString()))
                .thenReturn(inventory);

     
        doNothing().when(mockProducer).sendOrderEvent(any());

      
        mockMvc.perform(post("/api/order")
                        .header("Authorization", "Bearer test-token")
                        .header("X-Correlation-Id", "test-corr-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ORDER_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Thank you! Your order is confirmed."));

    
        verify(mockProducer, times(1)).sendOrderEvent(any());


        Orders savedOrder = orderRepository.findAll().get(0);
        Assertions.assertEquals("CONFIRMED", savedOrder.getStatus());
    }
    
    
    @Test
    @WithMockUser(username = "testuser", roles = {"CUSTOMER"})
    void placeOrder_Failed_WhenInventoryOutOfStock() throws Exception {

        ProductResponse product = new ProductResponse();
        product.setId("P1002");
        product.setName("OnePlus 12");
        product.setDescription("Fast and smooth user experience");
        product.setPrice(new BigDecimal("40000.0"));
        product.setSkuCode(SKU_CODE);

        when(productServiceClient.getProduct(anyString(), anyString()))
                .thenReturn(Optional.of(product));

        InventoryResponse inventory = new InventoryResponse();
        inventory.setId(1L);
        inventory.setSkuCode(SKU_CODE);
        inventory.setQuantity(0);  // Out of stock

        when(inventoryServiceClient.getInventory(anyString(),  anyString()))
                .thenReturn(inventory);

        doNothing().when(mockProducer).sendOrderEvent(any());

        mockMvc.perform(post("/api/order")
                        .header("Authorization", "Bearer test-token")
                        .header("X-Correlation-Id", "test-corr-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ORDER_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"))
        		.andExpect(jsonPath("$.message").value("Some items are out of stock."));

        verify(mockProducer, never()).sendOrderEvent(any());
        
        Orders savedOrder = orderRepository.findAll().get(0);
        Assertions.assertEquals("FAILED", savedOrder.getStatus());
    }
    
    
    
    @Test
    @WithMockUser(username = "testuser", roles = {"CUSTOMER"})
    void placeOrder_Failed_WhenProductNotAvailable() throws Exception {

        // Product service returns empty, meaning product not found
        when(productServiceClient.getProduct(anyString(),anyString()))
                .thenReturn(Optional.empty());

        InventoryResponse inventory = new InventoryResponse();
        inventory.setId(1L);
        inventory.setSkuCode(SKU_CODE);
        inventory.setQuantity(30);

        when(inventoryServiceClient.getInventory(anyString(), anyString()))
                .thenReturn(inventory);

        doNothing().when(mockProducer).sendOrderEvent(any());

        mockMvc.perform(post("/api/order")
                        .header("Authorization", "Bearer test-token")
                        .header("X-Correlation-Id", "test-corr-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ORDER_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.message").value("Sorry, one or more items are unavailable."));

        verify(mockProducer, never()).sendOrderEvent(any());
        
        Orders savedOrder = orderRepository.findAll().get(0);
        Assertions.assertEquals("FAILED", savedOrder.getStatus());
    }
    
    
}