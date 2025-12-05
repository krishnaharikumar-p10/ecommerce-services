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

	        // Existing sample inventory
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

	        // Matching inventory for your product list
	        Inventory i3 = new Inventory();
	        i3.setSkuCode("SKU789");
	        i3.setTotalQuantity(100);
	        i3.setReservedQuantity(0);
	        i3.setAvailableQuantity(100);
	        inventoryRepository.save(i3);

	        Inventory i4 = new Inventory();
	        i4.setSkuCode("SKU778");
	        i4.setTotalQuantity(80);
	        i4.setReservedQuantity(0);
	        i4.setAvailableQuantity(80);
	        inventoryRepository.save(i4);

	        Inventory i5 = new Inventory();
	        i5.setSkuCode("SKU555");
	        i5.setTotalQuantity(60);
	        i5.setReservedQuantity(0);
	        i5.setAvailableQuantity(60);
	        inventoryRepository.save(i5);

	        Inventory i6 = new Inventory();
	        i6.setSkuCode("SKU321");
	        i6.setTotalQuantity(40);
	        i6.setReservedQuantity(0);
	        i6.setAvailableQuantity(40);
	        inventoryRepository.save(i6);

	        Inventory i7 = new Inventory();
	        i7.setSkuCode("SKU111");
	        i7.setTotalQuantity(70);
	        i7.setReservedQuantity(0);
	        i7.setAvailableQuantity(70);
	        inventoryRepository.save(i7);

	        Inventory i8 = new Inventory();
	        i8.setSkuCode("SKU987");
	        i8.setTotalQuantity(90);
	        i8.setReservedQuantity(0);
	        i8.setAvailableQuantity(90);
	        inventoryRepository.save(i8);

	        Inventory i9 = new Inventory();
	        i9.setSkuCode("SKU654");
	        i9.setTotalQuantity(55);
	        i9.setReservedQuantity(0);
	        i9.setAvailableQuantity(55);
	        inventoryRepository.save(i9);

	        Inventory i10 = new Inventory();
	        i10.setSkuCode("SKU852");
	        i10.setTotalQuantity(120);
	        i10.setReservedQuantity(0);
	        i10.setAvailableQuantity(120);
	        inventoryRepository.save(i10);

	        System.out.println("Sample inventory added to database!");
	    };
	}
	*/

}
