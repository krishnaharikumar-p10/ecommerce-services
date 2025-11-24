package com.tech.inventory_service.exceptions;


public class SKUNotFoundException extends RuntimeException {
	
	public SKUNotFoundException(String skuCode) {
		super("No product found with SKU '" + skuCode + "'. Please check the SKU and try again.");
	}

}
