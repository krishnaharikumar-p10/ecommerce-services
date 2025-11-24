package com.tech.inventory_service.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tech.inventory_service.dto.InventoryRequest;
import com.tech.inventory_service.dto.InventoryResponse;
import com.tech.inventory_service.dto.ShippedItemDTO;
import com.tech.inventory_service.exceptions.SKUNotFoundException;
import com.tech.inventory_service.model.Inventory;
import com.tech.inventory_service.repository.InventoryRepository;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {

	private final InventoryRepository inventoryrepository;
	
	private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

	@Cacheable(value="inventory" , key="#skuCode")
	public InventoryResponse checkStock(String skuCode) throws SKUNotFoundException {
		
		Inventory inventory= inventoryrepository.findByskuCode(skuCode).orElseThrow(() -> new SKUNotFoundException(skuCode));
		
		InventoryResponse dto = new InventoryResponse();
		dto.setId(inventory.getId());
		dto.setSkuCode(inventory.getSkuCode());
		dto.setAvailableQuantity(inventory.getAvailableQuantity());
		dto.setReservedQuantity(inventory.getReservedQuantity());
		dto.setTotalQuantity(inventory.getTotalQuantity());
		return dto;
	}

	public void reserveStock(String skuCode, Integer quantity) throws SKUNotFoundException {
		Inventory inventory = inventoryrepository.findByskuCode(skuCode)
                .orElseThrow(() -> new SKUNotFoundException(skuCode));
		 inventory.setAvailableQuantity(inventory.getAvailableQuantity()-quantity);
		 inventory.setReservedQuantity(inventory.getReservedQuantity()+quantity);
		 inventoryrepository.save(inventory);
		 
		  logger.info("Reserved {} units of SKU {}", quantity, skuCode);

		
	}


	public void reduceReservedStock(String skuCode, Integer quantity) {
		Inventory inventory = inventoryrepository.findByskuCode(skuCode)
                .orElseThrow(() -> new SKUNotFoundException(skuCode));
		 inventory.setTotalQuantity(inventory.getTotalQuantity()-quantity);
		 inventory.setReservedQuantity(inventory.getReservedQuantity()-quantity);
		 inventoryrepository.save(inventory);
		 
		 logger.info("Reduced {} units of SKU {}", quantity, skuCode);
	}




	
	/*
	@CachePut(value="inventory" , key="#skuCode")
	public InventoryResponse increaseStocks(String skuCode, InventoryRequest request) throws SKUNotFoundException {
		
		Inventory inventory=inventoryrepository.findByskuCode(skuCode).orElseThrow(() -> new SKUNotFoundException(skuCode));
		Integer newQuantity= request.getQuantity()+ inventory.getQuantity();
		inventory.setQuantity(newQuantity);
		inventoryrepository.save(inventory);
		
		InventoryResponse response= new InventoryResponse();
		response.setId(inventory.getId());
		response.setSkuCode(skuCode);
		response.setQuantity(newQuantity);
		logger.info("Increased stock for {} " + skuCode);
		return response;
		
	}
 
    */
	
}
