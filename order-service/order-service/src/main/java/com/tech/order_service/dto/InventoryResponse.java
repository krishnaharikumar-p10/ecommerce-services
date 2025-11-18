package com.tech.order_service.dto;

public class InventoryResponse {
	private Long id;
	private String skuCode;
	private Integer quantity;
	
	public InventoryResponse() {}
	
	public InventoryResponse(Long id, String skuCode, Integer quantity) {
		super();
		this.id = id;
		this.skuCode = skuCode;
		this.quantity = quantity;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSkuCode() {
		return skuCode;
	}
	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	@Override
	public String toString() {
		return "InventoryResponse [id=" + id + ", skuCode=" + skuCode + ", quantity=" + quantity + "]";
	}
	
}
