package com.tech.payment_service.dto;

public class PaymentEventMessage {
	
	private String eventId;
	private String orderNumber;
	private String status;
	private String transactionId;
	
	

	public PaymentEventMessage() {}
	
	public PaymentEventMessage(String eventId, String orderNumber, String status,String transactionId) {
		super();
		this.eventId = eventId;
		this.orderNumber = orderNumber;
		this.status = status;
		this.transactionId=transactionId;
	}
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
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
	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

}
