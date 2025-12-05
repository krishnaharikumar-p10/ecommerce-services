package com.tech.inventory_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

	@Cacheable(value="inventory", key="#skuCode", unless="#result == null")
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

	@CachePut(value = "inventory", key = "#skuCode", unless="#result == null")
	public InventoryResponse reserveStock(String skuCode, Integer quantity) throws SKUNotFoundException {
		Inventory inventory = inventoryrepository.findByskuCode(skuCode)
                .orElseThrow(() -> new SKUNotFoundException(skuCode));
		 inventory.setAvailableQuantity(inventory.getAvailableQuantity()-quantity);
		 inventory.setReservedQuantity(inventory.getReservedQuantity()+quantity);
		 inventoryrepository.save(inventory);
		 InventoryResponse response = new InventoryResponse();
		  response.setId(inventory.getId());
		    response.setSkuCode(inventory.getSkuCode());
		    response.setAvailableQuantity(inventory.getAvailableQuantity());
		    response.setReservedQuantity(inventory.getReservedQuantity());
		    response.setTotalQuantity(inventory.getTotalQuantity());
		    
		  logger.info("Reserved {} units of SKU {}", quantity, skuCode);

		  return response;
	}

	@CachePut(value = "inventory", key = "#skuCode", unless="#result == null")
	public InventoryResponse reduceReservedStock(String skuCode, Integer quantity) {
		Inventory inventory = inventoryrepository.findByskuCode(skuCode)
                .orElseThrow(() -> new SKUNotFoundException(skuCode));
		 inventory.setTotalQuantity(inventory.getTotalQuantity()-quantity);
		 inventory.setReservedQuantity(inventory.getReservedQuantity()-quantity);
		 inventoryrepository.save(inventory);
		 
		 logger.info("Reduced {} units of SKU {}", quantity, skuCode);
		 
		 InventoryResponse response = new InventoryResponse();
		    response.setId(inventory.getId());
		    response.setSkuCode(inventory.getSkuCode());
		    response.setAvailableQuantity(inventory.getAvailableQuantity());
		    response.setReservedQuantity(inventory.getReservedQuantity());
		    response.setTotalQuantity(inventory.getTotalQuantity());

		    return response;
	}

	@CachePut(value = "inventory", key = "#skuCode", unless="#result == null")
    public InventoryResponse increaseStock(String skuCode, int quantity) throws SKUNotFoundException {
        Inventory inventory = inventoryrepository.findByskuCode(skuCode)
                .orElseThrow(() -> new SKUNotFoundException(skuCode));

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);
        inventory.setTotalQuantity(inventory.getTotalQuantity() + quantity);

        inventoryrepository.save(inventory);
        logger.info("Increased {} units for SKU {}", quantity, skuCode);

        InventoryResponse response = new InventoryResponse();
        response.setId(inventory.getId());
        response.setSkuCode(inventory.getSkuCode());
        response.setAvailableQuantity(inventory.getAvailableQuantity());
        response.setReservedQuantity(inventory.getReservedQuantity());
        response.setTotalQuantity(inventory.getTotalQuantity());

        return response;
    }




	
	
}
