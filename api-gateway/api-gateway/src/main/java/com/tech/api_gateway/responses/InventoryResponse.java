package com.tech.api_gateway.responses;

public class InventoryResponse {

	private boolean inStock;
	
	public InventoryResponse() {}
	
	public InventoryResponse(boolean inStock) {
		super();
		this.inStock = inStock;
	}

	public boolean isInStock() {
	        return inStock;
	    }

	public void setInStock(boolean inStock) {
	        this.inStock = inStock;
	    }
}
