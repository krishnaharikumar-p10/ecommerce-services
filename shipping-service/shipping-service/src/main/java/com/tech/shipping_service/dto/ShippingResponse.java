package com.tech.shipping_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShippingResponse {

	private  String orderNumber;
	private String trackingNumber;
	private String message;
	
}