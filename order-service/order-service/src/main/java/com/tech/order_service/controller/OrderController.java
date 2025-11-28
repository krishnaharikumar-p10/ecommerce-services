package com.tech.order_service.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.tech.order_service.dto.OrderDTO;
import com.tech.order_service.dto.OrderPlacedResponse;
import com.tech.order_service.dto.OrderResponse;
import com.tech.order_service.service.OrderProducer;
import com.tech.order_service.service.OrderService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
	
	private final OrderService service;
	private static final Logger log = LoggerFactory.getLogger(OrderController.class);

	@PostMapping
	public OrderPlacedResponse placeOrder(@RequestBody OrderDTO orderdto,
			@RequestHeader("X-USER-ID") Integer customerId,
			@RequestHeader("X-USERNAME") String customerName) {
		
		return service.placeOrder(orderdto,customerId, customerName);
	
	}
	
	@GetMapping("/myorders")
	public List<OrderResponse> getMyOrders(@RequestHeader("X-USER-ID") Integer customerId) {

			return service.getAllOrders(customerId);
		

	}

}