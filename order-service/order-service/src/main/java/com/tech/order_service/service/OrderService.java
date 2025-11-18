package com.tech.order_service.service;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tech.order_service.client.InventoryServiceClient;
import com.tech.order_service.client.ProductServiceClient;
import com.tech.order_service.dto.InventoryResponse;
import com.tech.order_service.dto.OrderDTO;
import com.tech.order_service.dto.OrderItemsDTO;
import com.tech.order_service.dto.OrderPlacedResponse;
import com.tech.order_service.dto.ProductResponse;
import com.tech.order_service.model.OrderItems;
import com.tech.order_service.model.Orders;
import com.tech.order_service.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	
	private static final Logger log = LoggerFactory.getLogger(OrderService.class);
	
	private final OrderRepository orderrepository;
	private final OrderProducer producer;
    private final InventoryServiceClient inventoryServiceClient;
    private final ProductServiceClient productServiceClient;
    
    @Transactional
    @PreAuthorize("hasRole('CUSTOMER')")
    public OrderPlacedResponse placeOrder(OrderDTO orderdto) {

        log.info("Received order request for {} items", orderdto.getOrderItems().size());

        String correlationId = MDC.get("correlationId");

        Orders order = new Orders();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderItems> items = orderdto.getOrderItems().stream()
                .map(this::mapDTOtoEntity)
                .collect(Collectors.toList());
        order.setOrderItems(items);

      
        for (OrderItemsDTO item : orderdto.getOrderItems()) {
            String failMessage = validateItem(item, correlationId, order);
            if (failMessage != null) {
                return buildFailedResponse(order, failMessage);
            }
        }

        
        try {
            log.info("Order {} confirmed and sent to Kafka", order.getOrderNumber());
            producer.sendOrder(orderdto);
            order.setStatus("CONFIRMED");
            orderrepository.save(order);

            return new OrderPlacedResponse(
                    order.getOrderNumber(),
                    "SUCCESS",
                    orderdto.getOrderItems(),
                    "Thank you! Your order is confirmed."
            );
        } catch (JsonProcessingException e) {
            log.error("Order {} placed but failed to send to Kafka", order.getOrderNumber(), e);
            order.setStatus("FAILED");
            orderrepository.save(order);
            return buildFailedResponse(order, "We could not process your order. Please try again.");
        }
    }

  
    private String validateItem(OrderItemsDTO item,  String correlationId, Orders order) {
        try {
            log.info("Checking product SKU: {}", item.getSkuCode());
            Optional<ProductResponse> product = productServiceClient.getProduct(
                    item.getSkuCode(), correlationId);

            if (product.isEmpty()) {
                return markOrderFailed(order, "Sorry, one or more items are unavailable.");
            }

            InventoryResponse inventory = inventoryServiceClient.getInventory(
                    item.getSkuCode(), correlationId);

            if (inventory == null || inventory.getQuantity() < item.getQuantity()) {
                return markOrderFailed(order, "Some items are out of stock.");
            }

        } catch (Exception e) {
            log.error("Error checking SKU {}: {}", item.getSkuCode(), e.getMessage(), e);
            return markOrderFailed(order, "Order could not be placed. Please try again later.");
        }
        return null; 
    }

    private String markOrderFailed(Orders order, String message) {
        order.setStatus("FAILED");
        orderrepository.save(order);
        return message;
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

    private OrderItems mapDTOtoEntity(OrderItemsDTO dto) {
        OrderItems orderitem = new OrderItems();
        orderitem.setSkuCode(dto.getSkuCode());
        orderitem.setPrice(dto.getPrice());
        orderitem.setQuantity(dto.getQuantity());
        return orderitem;
    }

		
}

	