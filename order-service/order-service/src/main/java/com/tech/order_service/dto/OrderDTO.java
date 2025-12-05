package com.tech.order_service.dto;
import java.util.List;

import com.tech.order_service.model.OrderItems;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
	
	@NotBlank(message = "Address cannot be empty")
	private String address;

	@NotEmpty(message = "Order must contain at least one item")
	@Valid
	private List<OrderItemsDTO> orderItems;
}