package com.tech.order_service.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tech.order_service.dto.OrderDTO;
import com.tech.order_service.dto.OrderPlacedResponse;
import com.tech.order_service.service.OrderProducer;
import com.tech.order_service.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
	
	private final OrderService service;
	

	@PostMapping
	public OrderPlacedResponse placeOrder(@RequestBody OrderDTO orderdto) {
		
		return service.placeOrder(orderdto);
	
	}
}
