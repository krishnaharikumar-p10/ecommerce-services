package com.tech.api_gateway.responses;


public class ProductInventoryResponse {

	private ProductResponse productResponse;
	private InventoryResponse inventoryResponse;
	
	public ProductInventoryResponse() {}
	
	public ProductInventoryResponse(ProductResponse productResponse,InventoryResponse inventoryResponse) {
		this.inventoryResponse=inventoryResponse;
		this.productResponse=productResponse;
	}

	public ProductResponse getProductResponse() {
		return productResponse;
	}

	public void setProductResponse(ProductResponse productResponse) {
		this.productResponse = productResponse;
	}

	public InventoryResponse getInventoryResponse() {
		return inventoryResponse;
	}

	public void setInventoryResponse(InventoryResponse inventoryResponse) {
		this.inventoryResponse = inventoryResponse;
	}
	

}
