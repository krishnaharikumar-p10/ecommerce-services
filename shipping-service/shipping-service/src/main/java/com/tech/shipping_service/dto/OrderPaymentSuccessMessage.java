package com.tech.shipping_service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPaymentSuccessMessage {
    private String orderNumber;
    private String eventId;
    private String status; 
    private String customerName;
    private String address;
    private List<OrderItemEventDTO> orderItems;
}