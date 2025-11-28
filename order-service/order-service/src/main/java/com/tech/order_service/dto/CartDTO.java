package com.tech.order_service.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartDTO {
	private String skuCode;
	private BigDecimal price;
	private Integer quantity;
}
