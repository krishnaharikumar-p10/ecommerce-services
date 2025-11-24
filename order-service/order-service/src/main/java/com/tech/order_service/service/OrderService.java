package com.tech.order_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tech.order_service.client.InventoryServiceClient;
import com.tech.order_service.client.ProductServiceClient;
import com.tech.order_service.dto.InventoryResponse;
import com.tech.order_service.dto.OrderDTO;
import com.tech.order_service.dto.OrderItemsDTO;
import com.tech.order_service.dto.OrderPlacedResponse;
import com.tech.order_service.dto.OrderResponse;
import com.tech.order_service.dto.OrderEventMessage;
import com.tech.order_service.dto.ProductResponse;
import com.tech.order_service.exceptions.OutOfStockException;
import com.tech.order_service.exceptions.ProductUnavailableException;
import com.tech.order_service.model.OrderLogTable;
import com.tech.order_service.model.OrderItems;
import com.tech.order_service.model.Orders;
import com.tech.order_service.repository.OrderEventRepository;
import com.tech.order_service.repository.OrderRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderEventRepository eventRepository;
    private final OrderProducer producer;
    private final ExternalServiceValidation  externalServiceValidation;
    
    @Transactional
    public OrderPlacedResponse placeOrder(OrderDTO orderDto, HttpServletRequest request) {

        String correlationId = MDC.get("correlationId");
        
        String orderNumber = UUID.randomUUID().toString();
        String customerName = request.getHeader("X-USERNAME");

        Orders order = new Orders();
        order.setOrderNumber(orderNumber);
        order.setCustomerName(customerName);
        order.setAddress(orderDto.getAddress());

        List<OrderItems> items = orderDto.getOrderItems().stream()
                .map(this::mapDTOtoEntity)
                .collect(Collectors.toList());
        order.setOrderItems(items);

        for (OrderItemsDTO item : orderDto.getOrderItems()) {
            ProductResponse product = externalServiceValidation.validateProduct(item.getSkuCode(), correlationId);
            InventoryResponse inventory = externalServiceValidation.validateInventory(
                    item.getSkuCode(), item.getQuantity(), correlationId);
        }


        order.setStatus("CREATED");
        orderRepository.save(order);

        String event_Id = UUID.randomUUID().toString();
        
    	OrderLogTable event = OrderLogTable.builder()
                .eventId(event_Id)
                .orderNumber(orderNumber)
                .eventType("ORDER_CREATED")
                .details("Order created with " + orderDto.getOrderItems().size() + " item(s)")
                .processedAt(LocalDateTime.now())
                .build();
    	
        eventRepository.save(event);
       
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemsDTO orderItem : orderDto.getOrderItems()) {
            totalAmount = totalAmount.add(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        }

     
        OrderEventMessage eventMessage = new OrderEventMessage(
                orderNumber,event_Id, "ORDER_CREATED", totalAmount, customerName, correlationId);
        producer.sendOrderEvent(eventMessage);

       
        OrderPlacedResponse response = new OrderPlacedResponse();
        response.setOrderNumber(orderNumber);
        response.setStatus(order.getStatus());
        response.setOrderItems(orderDto.getOrderItems());
        response.setMessage("Order created successfully. Proceed to payment.");

        return response;
    }


    private OrderItems mapDTOtoEntity(OrderItemsDTO dto) {
        OrderItems orderItem = new OrderItems();
        orderItem.setSkuCode(dto.getSkuCode());
        orderItem.setPrice(dto.getPrice());
        orderItem.setQuantity(dto.getQuantity());
        return orderItem;
    }


	public List<OrderResponse> getAllOrders(HttpServletRequest request) {
		String customerName = request.getHeader("X-USERNAME");
		List<Orders> orders = orderRepository.findByCustomerName(customerName);
	    return orders.stream()
	            .map(order -> {
	                OrderResponse dto = new OrderResponse();
	                dto.setOrderNumber(order.getOrderNumber());
	                dto.setStatus(order.getStatus());
	                dto.setTransactionId(order.getTransactionId());
	                dto.setTrackingNumber(order.getTrackingNumber());
	                return dto;
	            })
	            .toList();
		
	}
}
