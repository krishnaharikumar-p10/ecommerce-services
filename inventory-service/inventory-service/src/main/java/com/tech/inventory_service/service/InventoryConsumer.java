package com.tech.inventory_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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
            MDC.put("correlationId" ,event.getCorrelationid());
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
        }finally {
        	MDC.clear();
        }
    }
	
	
	
	@KafkaListener(topics = "order-shipped", groupId = "inventory-shipped-group")
    public void consumeOrderShipped(String message) {
        try {
            OrderShippedEvent event = objectMapper.readValue(message, OrderShippedEvent.class);
            MDC.put("correlationId", event.getCorrelationid());
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
        }finally {
        	MDC.clear();
        }
    }
	
	
	
}


