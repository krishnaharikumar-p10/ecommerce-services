package com.tech.inventory_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.inventory_service.dto.OrderDTO;
import com.tech.inventory_service.dto.OrderItemsDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryConsumer {
	
	private static final Logger logger= LoggerFactory.getLogger(InventoryConsumer.class);
	
	private final InventoryService inventoryService;
	
	private final ObjectMapper objectMapper;
	
	@KafkaListener(topics ="order-topic", groupId ="inventory-group")
	
	public void consume(String orderMessage) throws JsonMappingException, JsonProcessingException {
		OrderDTO order= objectMapper.readValue(orderMessage, OrderDTO.class);
		
		for( OrderItemsDTO item : order.getOrderItems()) {
			
			logger.info("Reducing stock for "+ item.getSkuCode()+",Quantity:" +item.getQuantity());
			
			inventoryService.reduceStock(item.getSkuCode(), item.getQuantity());
			
		}
	}
	

	

}

