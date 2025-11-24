package com.tech.order_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	

	@PostMapping
	public OrderPlacedResponse placeOrder(@RequestBody OrderDTO orderdto,HttpServletRequest request) {
		
		return service.placeOrder(orderdto,request);
	
	}
	
	@GetMapping("/myorders")
	public List<OrderResponse> getMyOrders(HttpServletRequest request) {
	    return service.getAllOrders(request);
	}

}
