package com.tech.inventory_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tech.inventory_service.dto.InventoryRequest;
import com.tech.inventory_service.dto.InventoryResponse;
import com.tech.inventory_service.service.InventoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
	
	
	private final InventoryService inventoryservice;

	@GetMapping("/{skuCode}")
	public  boolean isinStock(@PathVariable String skuCode) {
		return inventoryservice.isinStock(skuCode);
	}
	
	
	@GetMapping("/check/{skuCode}")
	public InventoryResponse checkStock(@PathVariable String skuCode) {

		return inventoryservice.checkStock(skuCode);
	}
	
	@PutMapping("increase/{skuCode}")
	public InventoryResponse increaseStocks(@PathVariable String skuCode,@RequestBody InventoryRequest request) {
		return inventoryservice.increaseStocks(skuCode,request);
	}
	
	
}