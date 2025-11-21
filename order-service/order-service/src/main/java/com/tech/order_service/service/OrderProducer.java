package com.tech.order_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.order_service.dto.OrderEventMessage;

@Service
public class OrderProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendOrderEvent(OrderEventMessage eventMessage) {
        try {
            String message = objectMapper.writeValueAsString(eventMessage);
            kafkaTemplate.send("order-topic", message);
            System.out.println("Sent order event: " + message);
        } catch (Exception e) {
            // handle failure, log error
            System.err.println("Failed to send order event: " + e.getMessage());
        }
    }
}
