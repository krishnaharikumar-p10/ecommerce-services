package com.tech.inventory_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tech.inventory_service.dto.InventoryRequest;
import com.tech.inventory_service.dto.InventoryResponse;
import com.tech.inventory_service.exceptions.SKUNotFoundException;
import com.tech.inventory_service.service.InventoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
	
	private final Logger logger= LoggerFactory.getLogger(InventoryController.class);
	
	private final InventoryService inventoryservice;

	
	@GetMapping("/check/{skuCode}")
	public InventoryResponse checkStock(@PathVariable String skuCode) throws SKUNotFoundException {
		logger.info("Returning stock details of  {} " + skuCode);
		return inventoryservice.checkStock(skuCode);
	}
	
	/*
	@PutMapping("increase/{skuCode}")
	public InventoryResponse increaseStocks(@PathVariable String skuCode) throws SKUNotFoundException {
		return inventoryservice.increaseStocks(skuCode);
	}
	
	*/
}