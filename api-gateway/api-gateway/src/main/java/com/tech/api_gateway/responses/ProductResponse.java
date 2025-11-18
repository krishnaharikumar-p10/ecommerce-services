package com.tech.api_gateway.responses;

import java.math.BigDecimal;

public class ProductResponse {
	
	public ProductResponse() {}
	
	 public ProductResponse(String name, String description, BigDecimal price, String skuCode) {
		super();
		this.name = name;
		this.description = description;
		this.price = price;
		this.skuCode = skuCode;
	}

	 private String name;
	 private String description;
	 private BigDecimal price;
	 private String skuCode;
	 
	 public String getName() {
	        return name;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }

	    public String getDescription() {
	        return description;
	    }

	    public void setDescription(String description) {
	        this.description = description;
	    }

	    public BigDecimal getPrice() {
	        return price;
	    }

	    public void setPrice(BigDecimal price) {
	        this.price = price;
	    }

	    public String getSkuCode() {
	        return skuCode;
	    }

	    public void setSkuCode(String skuCode) {
	        this.skuCode = skuCode;
	    }


}
