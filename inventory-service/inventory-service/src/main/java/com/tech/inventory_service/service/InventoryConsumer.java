package com.tech.inventory_service.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.inventory_service.dto.OrderItemsDTO;
import com.tech.inventory_service.dto.InventoryEventMessage;
import com.tech.inventory_service.dto.OrderEventMessage;
import com.tech.inventory_service.model.InventoryEvent;
import com.tech.inventory_service.repository.InventoryEventRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryConsumer {

    private static final Logger logger = LoggerFactory.getLogger(InventoryConsumer.class);

    private final InventoryService inventoryService;
    private final InventoryEventRepository eventRepository;
    private final ObjectMapper objectMapper;
    private final InventoryProducer inventoryProducer;

    @KafkaListener(topics = "order-topic", groupId = "inventory-group")
    public void consume(String message) throws Exception {

        OrderEventMessage event = objectMapper.readValue(message, OrderEventMessage.class);

        if (eventRepository.existsByEventId(event.getEventId())) {
            logger.info("Skipping duplicate event: {}", event.getEventId());
            return;
        }

        InventoryEvent orderPlacedEvent = InventoryEvent.builder()
                .eventId(event.getEventId())
                .orderNumber(event.getOrderNumber())
                .eventType("ORDER_PLACED")
                .details("Order placed with " + event.getOrderDto().getOrderItems().size() + " item(s)")
                .processedAt(LocalDateTime.now())
                .build();
        eventRepository.save(orderPlacedEvent);
        logger.info("Logged ORDER_PLACED event {} for order {}", event.getEventId(), event.getOrderNumber());

        
        for (OrderItemsDTO item : event.getOrderDto().getOrderItems()) {
            inventoryService.reduceStock(item.getSkuCode(), item.getQuantity());

            InventoryEvent stockReducedEvent = InventoryEvent.builder()
                    .eventId(event.getEventId() + "-" + item.getSkuCode())
                    .orderNumber(event.getOrderNumber())
                    .eventType("STOCK_REDUCED")
                    .details("Reduced " + item.getQuantity() + " units of " + item.getSkuCode())
                    .processedAt(LocalDateTime.now())
                    .build();
            eventRepository.save(stockReducedEvent);

            logger.info("Reduced stock and logged STOCK_REDUCED for {} qty {}", item.getSkuCode(), item.getQuantity());
            
            
            InventoryEventMessage inventoryEventMessage = new InventoryEventMessage(
                    UUID.randomUUID().toString(),
                    event.getOrderNumber(),
                    "INVENTORY_CONFIRMED"
            );
            
            inventoryProducer.sendInventoryEvent(inventoryEventMessage);

            logger.info("Sent INVENTORY_CONFIRMED event for order {}", event.getOrderNumber());
            
        }
    }
}
