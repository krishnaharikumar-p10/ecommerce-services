package com.tech.order_service.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartResponse {

	private String skuCode;
	private String productName;
	private BigDecimal productPrice;
	private Integer quantity;
	private BigDecimal totalPrice;
}
