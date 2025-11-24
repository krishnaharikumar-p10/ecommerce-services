package com.tech.order_service.dto;

import java.math.BigDecimal;

public class OrderEventMessage {
	
	
	private String orderNumber;
    private String eventId;
    private String eventType; 
    private BigDecimal totalAmount;
    private String customerName;
    private String correlationId;

    public OrderEventMessage() {}
    
    
	public OrderEventMessage(String orderNumber, String eventId, String eventType, BigDecimal totalAmount,
			String customerName, String correlationId) {
		super();
		this.orderNumber = orderNumber;
		this.eventId = eventId;
		this.eventType = eventType;
		this.totalAmount = totalAmount;
		this.customerName = customerName;
		this.correlationId = correlationId;
	}
	public String getCorrelationId() {
		return correlationId;
	}
	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
}
   