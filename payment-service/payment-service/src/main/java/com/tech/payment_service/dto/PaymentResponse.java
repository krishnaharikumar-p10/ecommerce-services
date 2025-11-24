package com.tech.payment_service.dto;

public class PaymentResponse {
    private String orderNumber;
    private String transactionId;
    private String message;
    
    public PaymentResponse(){}
    
	public PaymentResponse(String orderNumber, String transactionId, String message) {
		super();
		this.orderNumber = orderNumber;
		this.transactionId = transactionId;
		this.message = message;
	}
	public String getOrderNumber(){
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
