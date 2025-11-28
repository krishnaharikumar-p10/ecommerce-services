package com.tech.payment_service.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PaymentLogTable {
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer customerId;
    private String eventId;   
    private String orderNumber;
    private String eventType;     
    private String details;  
    private LocalDateTime processedAt;
    
    
    public PaymentLogTable() {}


    
	public PaymentLogTable(Long id, Integer customerId, String eventId, String orderNumber, String eventType,
			String details, LocalDateTime processedAt) {
		super();
		this.id = id;
		this.customerId = customerId;
		this.eventId = eventId;
		this.orderNumber = orderNumber;
		this.eventType = eventType;
		this.details = details;
		this.processedAt = processedAt;
	}



	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Integer getCustomerId() {
		return customerId;
	}


	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
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


	public String getEventType() {
		return eventType;
	}


	public void setEventType(String eventType) {
		this.eventType = eventType;
	}


	public String getDetails() {
		return details;
	}


	public void setDetails(String details) {
		this.details = details;
	}


	public LocalDateTime getProcessedAt() {
		return processedAt;
	}


	public void setProcessedAt(LocalDateTime processedAt) {
		this.processedAt = processedAt;
	}
    
}
