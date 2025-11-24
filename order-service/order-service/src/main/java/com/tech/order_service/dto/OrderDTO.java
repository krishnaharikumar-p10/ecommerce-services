package com.tech.order_service.dto;
import java.util.List;

import com.tech.order_service.model.OrderItems;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
	private String address;
	private List<OrderItemsDTO> orderItems;
}
