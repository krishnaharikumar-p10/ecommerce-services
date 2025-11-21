package com.tech.inventory_service.dto;

public class OrderEventMessage {
    private String orderNumber;
    private String eventId;
    private String eventType; 
    private OrderDTO orderDto;
    
   public OrderEventMessage() {}
    
   public OrderEventMessage(String orderNumber, String eventId, String eventType, OrderDTO orderDto) {
		super();
		this.orderNumber = orderNumber;
		this.eventId = eventId;
		this.eventType = eventType;
		this.orderDto = orderDto;
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
	public OrderDTO getOrderDto() {
		return orderDto;
	}
	public void setOrderDto(OrderDTO orderDto) {
		this.orderDto = orderDto;
	}
}
