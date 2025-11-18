package com.tech.product_service.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tech.product_service.dto.ProductRequest;
import com.tech.product_service.dto.ProductResponse;
import com.tech.product_service.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {
	
	private final ProductService productservice;
	
	//private static final Logger logger= LoggerFactory.getLogger(ProductController.class);
	
	@PostMapping
	public void createProduct(@RequestBody ProductRequest productrequest) {
		productservice.createProduct(productrequest);
	}
	
	@GetMapping("/{skuCode}")
	public Optional<ProductResponse> getProductById(@PathVariable String skuCode) {
		return productservice.getProductbyId(skuCode);
	}
	
	@GetMapping
	public Page<ProductResponse> getProductPages(
			@RequestParam(defaultValue="0") int page,
			@RequestParam(defaultValue="10") int size,
			@RequestParam(defaultValue= "name") String sortBy,
			@RequestParam(defaultValue= "asc") String sortDir){	
		
		return productservice.getProduct(page,size,sortBy,sortDir);
	}
	
}
