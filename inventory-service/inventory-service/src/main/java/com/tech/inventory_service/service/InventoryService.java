package com.tech.inventory_service.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tech.inventory_service.dto.InventoryEventMessage;
import com.tech.inventory_service.dto.InventoryRequest;
import com.tech.inventory_service.dto.InventoryResponse;
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

	public boolean isinStock(String skuCode) throws SKUNotFoundException {
		
		Inventory item=  inventoryrepository.findByskuCode(skuCode).orElseThrow(() -> new SKUNotFoundException(skuCode));
		if (item ==null) {
			return false;
		}
		return item.getQuantity()>0;
	}
	
	
	
	@CachePut(value="inventory" , key="#skuCode")
	public InventoryResponse reduceStock(String skuCode, Integer quantity) throws SKUNotFoundException {
		
		Inventory inventory = inventoryrepository.findByskuCode(skuCode).orElseThrow(() -> new SKUNotFoundException(skuCode));
		
		Integer new_quantity =inventory.getQuantity() - quantity;

		
		inventory.setQuantity(new_quantity);
		inventoryrepository.save(inventory);
		
		InventoryResponse response= new InventoryResponse();
		response.setId(inventory.getId());
		response.setSkuCode(inventory.getSkuCode());
		response.setQuantity(new_quantity);
		logger.info("Reduced stock for {} " + skuCode);
	
		return response;	
		
	}

	@Cacheable(value="inventory" , key="#skuCode")
	public InventoryResponse checkStock(String skuCode) throws SKUNotFoundException {
		
		Inventory inventory= inventoryrepository.findByskuCode(skuCode).orElseThrow(() -> new SKUNotFoundException(skuCode));
		
		InventoryResponse dto = new InventoryResponse();
		dto.setId(inventory.getId());
		dto.setSkuCode(inventory.getSkuCode());
		dto.setQuantity(inventory.getQuantity());
		return dto;
	}

	
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
 
    
	
}
