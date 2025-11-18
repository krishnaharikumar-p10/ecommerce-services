package com.tech.product_service.service;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import com.tech.product_service.dto.ProductRequest;
import com.tech.product_service.model.Product;
import com.tech.product_service.repository.ProductRepository;

public class testService {
	
	@Mock
	ProductRepository repo;
	
	@Autowired
	ProductService service;
	
	@Test
	public void testCreateProduct() {
		
		
		ProductRequest product = ProductRequest.builder()
				.id("P1")
				.name("Iphone")
				.description("Iphone")
				.price(new BigDecimal(500000))
				.build();
		
	
}
}