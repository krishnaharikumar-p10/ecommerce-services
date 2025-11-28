package com.tech.order_service.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.order_service.repository.OrderEventRepository;
import com.tech.order_service.repository.OrderRepository;

import jakarta.transaction.Transactional;

import com.tech.order_service.dto.OrderShippedEvent;
import com.tech.order_service.model.OrderLogTable;
import com.tech.order_service.model.Orders;

@Service
public class OrderShippedConsumer{
	
	@Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderEventRepository logRepository;

    private static final Logger logger = LoggerFactory.getLogger(OrderShippedConsumer.class);

    @Transactional
    @KafkaListener(topics = "order-shipped", groupId = "order-shipped-group")
    public void consumeOrderShipped(String message) {
        try {
            OrderShippedEvent event = objectMapper.readValue(message, OrderShippedEvent.class);
            
            MDC.put("correlationId", event.getCorrelationid());
            logger.info("Received OrderShippedEvent for order: {}", event.getOrderNumber());

      
            boolean alreadyProcessed = logRepository.existsByEventId(event.getEventId());
            if (alreadyProcessed) {
                logger.info("Skipping duplicate shipped event: {}", event.getEventId());
                return;
            }
            
            Orders order= orderRepository.findByOrderNumber(event.getOrderNumber());
            order.setStatus("ORDER_SHIPPED");
            order.setTrackingNumber(event.getTrackingNumber());
            orderRepository.save(order);
            
            OrderLogTable log = new OrderLogTable();
            log.setEventId(event.getEventId());
            log.setCustomerId(order.getCustomerId());
            log.setOrderNumber(event.getOrderNumber());
            log.setEventType("ORDER_SHIPPED");
            log.setDetails("Shipped order for order number " + order.getOrderNumber());
            log.setProcessedAt(LocalDateTime.now());
            logRepository.save(log);
          
            
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
        	MDC.clear();        }
        
    }
}
