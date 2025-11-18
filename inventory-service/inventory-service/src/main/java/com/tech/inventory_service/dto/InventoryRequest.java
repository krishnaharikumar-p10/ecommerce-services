package com.tech.inventory_service.dto;

public class InventoryRequest {
	
	private Integer quantity;

	public InventoryRequest() {}
	
	public InventoryRequest(Integer quantity) {
		super();
		this.quantity = quantity;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

}
