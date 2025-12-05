package com.tech.order_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public OrderPlacedResponse placeOrder(OrderDTO orderDto, Integer customerId, String customerName) {

        String correlationId = MDC.get("correlationId");
        String orderNumber = UUID.randomUUID().toString();

        Orders order = new Orders();
        order.setOrderNumber(orderNumber);
        order.setCustomerName(customerName);
        order.setCustomerId(customerId);
        order.setAddress(orderDto.getAddress());
        order.setOrderedAt(LocalDateTime.now());

        List<OrderItems> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemsDTO itemDto : orderDto.getOrderItems()) {

            ProductResponse product = externalServiceValidation.validateProduct(
                    itemDto.getSkuCode(), correlationId);

            InventoryResponse inventory = externalServiceValidation.validateInventory(
                    itemDto.getSkuCode(), itemDto.getQuantity(), correlationId);

            OrderItems orderItem = new OrderItems();
            orderItem.setSkuCode(itemDto.getSkuCode());
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setPrice(product.getPrice());
            items.add(orderItem);

            totalAmount = totalAmount.add(
                    product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()))
            );
        }

        order.setOrderItems(items);
        order.setStatus("CREATED");
        orderRepository.save(order);

        String eventId = UUID.randomUUID().toString();
        OrderLogTable event = OrderLogTable.builder()
                .eventId(eventId)
                .orderNumber(orderNumber)
                .customerId(customerId)
                .eventType("ORDER_CREATED")
                .details("Order created with " + orderDto.getOrderItems().size() + " item(s)")
                .processedAt(LocalDateTime.now())
                .build();
        eventRepository.save(event);

        OrderEventMessage eventMessage = new OrderEventMessage(
                customerId, orderNumber, eventId, "ORDER_CREATED",
                totalAmount, customerName, correlationId
        );
        producer.sendOrderEvent(eventMessage);

        List<OrderItemsDTO> responseItems = items.stream().map(oi -> {
            OrderItemsDTO dto = new OrderItemsDTO();
            dto.setSkuCode(oi.getSkuCode());
            dto.setQuantity(oi.getQuantity());
            dto.setPrice(oi.getPrice());
            return dto;
        }).toList();

        OrderPlacedResponse response = new OrderPlacedResponse();
        response.setOrderNumber(orderNumber);
        response.setStatus(order.getStatus());
        response.setOrderItems(responseItems);
        response.setTotalPrice(totalAmount);
        response.setMessage("Order created successfully. Proceed to payment.");

        return response;
    }


    public List<OrderResponse> getAllOrders(Integer customerId) {
        log.info("Getting confirmed order details for customer {}", customerId);
        
        
        List<String> allowedStatuses = List.of("ORDER_CONFIRMED", "ORDER_SHIPPED");

        List<Orders> orders = orderRepository.findByCustomerId(customerId).stream()
                .filter(order -> allowedStatuses.contains(order.getStatus()))
                .toList();

        return orders.stream()
                .map(order -> {
                    OrderResponse dto = new OrderResponse();
                    dto.setOrderNumber(order.getOrderNumber());
                    dto.setStatus(order.getStatus());
                    dto.setTransactionId(order.getTransactionId());
                    dto.setTrackingNumber(order.getTrackingNumber());
                    dto.setOrderedAt(order.getOrderedAt());
                    return dto;
                })
                .toList();
    }



}
