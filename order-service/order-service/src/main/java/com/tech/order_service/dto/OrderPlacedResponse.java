package com.tech.order_service.dto;

import java.math.BigDecimal;
import java.util.List;

public class OrderPlacedResponse {

	private String orderNumber;
	private String status;
	private List<OrderItemsDTO> orderItems;
	private String message;
	private BigDecimal totalPrice;
	
	public OrderPlacedResponse() {}
	
	

	public OrderPlacedResponse(String orderNumber, String status, List<OrderItemsDTO> orderItems, String message,
			BigDecimal totalPrice) {
		super();
		this.orderNumber = orderNumber;
		this.status = status;
		this.orderItems = orderItems;
		this.message = message;
		this.totalPrice = totalPrice;
	}



	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<OrderItemsDTO> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItemsDTO> orderItems) {
		this.orderItems = orderItems;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	
}