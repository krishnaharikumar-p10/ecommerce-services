package com.tech.order_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderResponse {

    private String orderNumber;
    private String status;
    private String transactionId;   
    private String trackingNumber; 
}
