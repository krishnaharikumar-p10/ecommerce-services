package com.tech.order_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.order_service.dto.OrderDTO;

@Service
public class OrderProducer {

	@Autowired
	private KafkaTemplate<String,String> kafkaTemplate;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	public void sendOrder(OrderDTO order) throws JsonProcessingException {
		String message = objectMapper.writeValueAsString(order);
		kafkaTemplate.send("order-topic", message);
        System.out.println("Sent order: " + message);
	}
}
