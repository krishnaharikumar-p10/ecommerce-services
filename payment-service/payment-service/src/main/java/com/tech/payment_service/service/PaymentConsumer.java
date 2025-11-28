package com.tech.payment_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.payment_service.dto.OrderEventMessage;


@Service
public class PaymentConsumer {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentService paymentService;

    private static final Logger logger = LoggerFactory.getLogger(PaymentConsumer.class);

    @KafkaListener(topics = "payment-request-topic", groupId = "payment-group")
    public void consumeOrderCreated(String message) { 
        try {
            
            OrderEventMessage event = objectMapper.readValue(message, OrderEventMessage.class);

            logger.info("Received message in Payment Service: " + event);

            MDC.put("correlationId", event.getCorrelationId());
            paymentService.createPendingPayment(event);
        } catch (Exception e) {
            logger.error("Failed to parse Kafka message", e);
        } finally {
            MDC.clear();
        }
    }
}
