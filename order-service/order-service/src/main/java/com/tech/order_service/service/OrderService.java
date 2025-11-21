package com.tech.order_service.service;

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
import com.tech.order_service.dto.OrderEventMessage;
import com.tech.order_service.dto.OrderItemsDTO;
import com.tech.order_service.dto.OrderPlacedResponse;
import com.tech.order_service.dto.ProductResponse;
import com.tech.order_service.model.OrderEvent;
import com.tech.order_service.model.OrderItems;
import com.tech.order_service.model.Orders;
import com.tech.order_service.repository.OrderEventRepository;
import com.tech.order_service.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderEventRepository eventRepository;
    private final OrderProducer producer;
    private final InventoryServiceClient inventoryServiceClient;
    private final ProductServiceClient productServiceClient;

    @Transactional
    @PreAuthorize("hasRole('CUSTOMER')")
    public OrderPlacedResponse placeOrder(OrderDTO orderDto) {

        String correlationId = MDC.get("correlationId");
        String orderNumber = UUID.randomUUID().toString();
        Orders order = new Orders();
        order.setOrderNumber(orderNumber);
        List<OrderItems> items = orderDto.getOrderItems().stream()
                .map(this::mapDTOtoEntity)
                .collect(Collectors.toList());
        order.setOrderItems(items);

   
        logOrderEvent(orderNumber, "ORDER_PLACED", 
                "Order received with " + orderDto.getOrderItems().size() + " item(s)");

      
        for (OrderItemsDTO item : orderDto.getOrderItems()) {
            try {
                Optional<ProductResponse> product = productServiceClient.getProduct(item.getSkuCode(), correlationId);
                if (product.isEmpty()) {
                    order.setStatus("FAILED");
                    orderRepository.save(order);

                    logOrderEvent(orderNumber, "ORDER_FAILED",
                            "Product unavailable: " + item.getSkuCode());

                    return buildFailedResponse(order, 
                            "Sorry, One or more products are not available: " + item.getSkuCode());
                }

                InventoryResponse inventory = inventoryServiceClient.getInventory(item.getSkuCode(), correlationId);
                if (inventory == null || inventory.getQuantity() < item.getQuantity()) {
                    order.setStatus("FAILED");
                    orderRepository.save(order);

                    logOrderEvent(orderNumber, "ORDER_FAILED",
                            "Out of stock: " + item.getSkuCode());

                    return buildFailedResponse(order, 
                            "Sorry, Some products are out of stock: " + item.getSkuCode());
                }

            } catch (Exception e) {
                order.setStatus("FAILED");
                orderRepository.save(order);

                logOrderEvent(orderNumber, "ORDER_FAILED",
                        "Error processing SKU " + item.getSkuCode() + ": " + e.getMessage());

                return buildFailedResponse(order, 
                        "Order could not be placed. Please try again later.");
            }
        }

  
        order.setStatus("CONFIRMED");
        orderRepository.save(order);

        logOrderEvent(orderNumber, "ORDER_CONFIRMED", 
                "Order confirmed with " + orderDto.getOrderItems().size() + " item(s)");

    
        String eventId = UUID.randomUUID().toString();
        OrderEventMessage eventMessage = new OrderEventMessage(
                orderNumber,
                eventId,
                "ORDER_PLACED",
                orderDto
        );
        producer.sendOrderEvent(eventMessage);

        return new OrderPlacedResponse(
                orderNumber,
                "SUCCESS",
                orderDto.getOrderItems(),
                "Thank you! Your order is confirmed."
        );
    }

    private void logOrderEvent(String orderNumber, String eventType, String details) {
        OrderEvent event = OrderEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .orderNumber(orderNumber)
                .eventType(eventType)
                .details(details)
                .processedAt(LocalDateTime.now())
                .build();
        eventRepository.save(event);
    }

    private OrderItems mapDTOtoEntity(OrderItemsDTO dto) {
        OrderItems orderItem = new OrderItems();
        orderItem.setSkuCode(dto.getSkuCode());
        orderItem.setPrice(dto.getPrice());
        orderItem.setQuantity(dto.getQuantity());
        return orderItem;
    }

    private OrderPlacedResponse buildFailedResponse(Orders order, String message) {
        return new OrderPlacedResponse(
                order.getOrderNumber(),
                "FAILED",
                order.getOrderItems().stream()
                        .map(o -> new OrderItemsDTO(o.getSkuCode(), o.getPrice(), o.getQuantity()))
                        .collect(Collectors.toList()),
                message
        );
    }
}
