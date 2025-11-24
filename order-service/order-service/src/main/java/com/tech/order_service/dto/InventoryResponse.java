package com.tech.order_service.dto;

public class InventoryResponse {
	private Long id;
	private String skuCode;
	private Integer totalQuantity;
	private Integer reservedQuantity;
	private Integer availableQuantity;

	public InventoryResponse() {}
	
	public InventoryResponse(Long id, String skuCode, Integer totalQuantity, Integer reservedQuantity,
			Integer availableQuantity) {
		super();
		this.id = id;
		this.skuCode = skuCode;
		this.totalQuantity = totalQuantity;
		this.reservedQuantity = reservedQuantity;
		this.availableQuantity = availableQuantity;
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
	public Integer getTotalQuantity() {
		return totalQuantity;
	}
	public void setTotalQuantity(Integer totalQuantity) {
		this.totalQuantity = totalQuantity;
	}
	public Integer getReservedQuantity() {
		return reservedQuantity;
	}
	public void setReservedQuantity(Integer reservedQuantity) {
		this.reservedQuantity = reservedQuantity;
	}
	public Integer getAvailableQuantity() {
		return availableQuantity;
	}
	public void setAvailableQuantity(Integer availableQuantity) {
		this.availableQuantity = availableQuantity;
	}
}
	
	
	