package com.tech.inventory_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.inventory_service.dto.OrderItemEventDTO;
import com.tech.inventory_service.dto.OrderPaymentSuccessMessage;
import com.tech.inventory_service.dto.OrderShippedEvent;
import com.tech.inventory_service.dto.ShippedItemDTO;
import com.tech.inventory_service.model.InventoryEvent;
import com.tech.inventory_service.repository.InventoryEventRepository;

import lombok.RequiredArgsConstructor;

@Service
public class InventoryConsumer {
	
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private InventoryService inventoryService; 
    @Autowired
    private InventoryEventRepository eventRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(InventoryConsumer.class);
	
	@KafkaListener(topics = "order-comfirmed", groupId = "inventory-order-group")
    public void consume(String message) throws Exception {
		try {
            
            OrderPaymentSuccessMessage event = objectMapper.readValue(message, OrderPaymentSuccessMessage.class);
            logger.info("Received order confirmed event: " + event.getOrderNumber());

         
            boolean alreadyProcessed = eventRepository.existsByEventId(event.getEventId());
            if (alreadyProcessed) {
                logger.info("Skipping duplicate order event: " + event.getEventId());
                return;
            }

         
            List<OrderItemEventDTO> items = event.getOrderItems();
            if (items != null) {
                for (OrderItemEventDTO item : items) {
                    inventoryService.reserveStock(item.getSkuCode(), item.getQuantity());

                    InventoryEvent log = new InventoryEvent();
                    log.setEventId(event.getEventId() + "-" + item.getSkuCode());
                    log.setQuantity(item.getQuantity());
                    log.setSkuCode(item.getSkuCode());
                    log.setOrderNumber(event.getOrderNumber());
                    log.setEventType("STOCK_RESERVED");
                    log.setDetails("Reserved " + item.getQuantity() + " units for order " + event.getOrderNumber());
                    log.setProcessedAt(LocalDateTime.now());
                    eventRepository.save(log);

                    logger.info("Reserved stock for SKU {}: {}", item.getSkuCode(), item.getQuantity());
                }
            }

        } catch (Exception e) {
            logger.error("Failed to process order confirmed event", e);
        }
    }
	
	
	
	@KafkaListener(topics = "order-shipped", groupId = "inventory-shipped-group")
    public void consumeOrderShipped(String message) {
        try {
            OrderShippedEvent event = objectMapper.readValue(message, OrderShippedEvent.class);
            logger.info("Received OrderShippedEvent for order: {}", event.getOrderNumber());

       
            boolean alreadyProcessed = eventRepository.existsByEventId(event.getEventId());
            if (alreadyProcessed) {
                logger.info("Skipping duplicate shipped event: {}", event.getEventId());
                return;
            }
            List<ShippedItemDTO> items = event.getItems(); 
            
            if (items != null) {
                for (ShippedItemDTO item : items) {
                    inventoryService.reduceReservedStock(item.getSkuCode(), item.getQuantity());

                    InventoryEvent log = InventoryEvent.builder()
                            .eventId(event.getEventId())
                            .skuCode(item.getSkuCode())
                            .quantity(item.getQuantity())
                            .orderNumber(event.getOrderNumber())
                            .eventType("STOCK_REDUCED")
                            .details("Reduced " + item.getQuantity() + " units after shipment for order " + event.getOrderNumber())
                            .processedAt(LocalDateTime.now())
                            .build();
                    eventRepository.save(log);
                }
            }
           


            logger.info("Reduced reserved stock and logged event for order: {}", event.getOrderNumber());

        } catch (Exception e) {
            logger.error("Failed to process OrderShippedEvent", e);
        }
    }
	
	
	
}




	
/*
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
    */

