package com.tech.inventory_service;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.tech.inventory_service.model.Inventory;
import com.tech.inventory_service.repository.InventoryRepository;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);

		
	}
/*
	@Bean
	public CommandLineRunner loadInventoryData(InventoryRepository inventoryRepository) {
	    return args -> {

	        Inventory i1 = new Inventory();
	        i1.setSkuCode("SKU123");
	        i1.setTotalQuantity(100);
	        i1.setReservedQuantity(0);
	        i1.setAvailableQuantity(100);
	        inventoryRepository.save(i1);

	        Inventory i2 = new Inventory();
	        i2.setSkuCode("SKU456");
	        i2.setTotalQuantity(50);
	        i2.setReservedQuantity(0);
	        i2.setAvailableQuantity(50);
	        inventoryRepository.save(i2);

	        System.out.println("Sample inventory added to database!");
	    };
	}
	*/
	
}
