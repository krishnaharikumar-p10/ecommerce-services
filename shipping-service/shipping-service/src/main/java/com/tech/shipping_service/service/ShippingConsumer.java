package com.tech.shipping_service.service;

import java.time.LocalDateTime;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.shipping_service.dto.OrderPaymentSuccessMessage;
import com.tech.shipping_service.model.ShippingLog;
import com.tech.shipping_service.repository.ShippingLogRepository;

@Service
public class ShippingConsumer {

	@Autowired
	private ShippingService shippingService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ShippingLogRepository shippingLogRepository;


    @KafkaListener(topics = "order-comfirmed", groupId = "shipping-group")
    public void consumeOrderConfirmed(String message) {
        try {
       
            OrderPaymentSuccessMessage event = objectMapper.readValue(message, OrderPaymentSuccessMessage.class);
            MDC.put("correlationId",event.getCorrelationId());

            boolean alreadyProcessed = shippingLogRepository.existsByEventId(event.getEventId());
            if (alreadyProcessed) {
                return;
            }
            
            shippingService.prepareShipping(event);
            
            ShippingLog log = new ShippingLog();
            log.setEventId(event.getEventId());
            log.setOrderNumber(event.getOrderNumber());
            log.setEventType("PREPARING");
            log.setDetails("Shipping preparing started for order " + event.getOrderNumber());
            log.setProcessedAt(LocalDateTime.now());
            shippingLogRepository.save(log);
            
        } catch (Exception e) {
          
        }finally{
        	MDC.clear();
        }
    }
}
    

