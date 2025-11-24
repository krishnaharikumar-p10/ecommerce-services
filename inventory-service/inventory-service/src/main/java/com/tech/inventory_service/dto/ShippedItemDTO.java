package com.tech.inventory_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShippedItemDTO {
    private String skuCode;
    private Integer quantity;
}