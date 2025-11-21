package com.tech.inventory_service.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.tech.inventory_service.dto.InventoryEventMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryProducer {

    private final KafkaTemplate<String, InventoryEventMessage> template;

    private static final String TOPIC = "inventory-topic";

    public void sendInventoryEvent(InventoryEventMessage message) {
        template.send(TOPIC, message);
    }
}
