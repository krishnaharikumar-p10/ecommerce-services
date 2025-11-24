package com.tech.inventory_service.dto;


import java.util.List;

public class OrderShippedEvent {

	private  String orderNumber;
	private String trackingNumber;
	private String eventId;
	private List<ShippedItemDTO> items;
	
	
	public OrderShippedEvent() {}

	

	public OrderShippedEvent(String orderNumber, String trackingNumber, String eventId, List<ShippedItemDTO> items) {
		super();
		this.orderNumber = orderNumber;
		this.trackingNumber = trackingNumber;
		this.eventId = eventId;
		this.items = items;
	}



	public String getOrderNumber() {
		return orderNumber;
	}


	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}


	public String getTrackingNumber() {
		return trackingNumber;
	}


	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}


	public String getEventId() {
		return eventId;
	}


	public void setEventId(String eventId) {
		this.eventId = eventId;
	}


	public List<ShippedItemDTO> getItems() {
		return items;
	}


	public void setItems(List<ShippedItemDTO> items) {
		this.items = items;
	}
	
	
}