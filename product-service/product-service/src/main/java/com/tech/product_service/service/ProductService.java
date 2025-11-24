package com.tech.product_service.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tech.product_service.dto.ProductResponse;
import com.tech.product_service.exceptions.ProductNotFoundException;
import com.tech.product_service.model.Product;
import com.tech.product_service.repository.ProductRepository;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
	
	private final ProductRepository productrepository;
	
	private static final Logger logger= LoggerFactory.getLogger(ProductService.class);
	
	public Optional<ProductResponse> getProductbyId(String skuCode) {
	    return productrepository.findByskuCode(skuCode)
	            .map(this::mapEntitytodto);
	}
	

	public Page<ProductResponse> getProduct(int page, int size, String sortBy , String sortDir) {
		
		Sort sort= sortDir.equalsIgnoreCase("desc")
				?Sort.by(sortBy).descending()
				:Sort.by(sortBy).ascending();
		
		Page<Product> productPage = productrepository.findAll(PageRequest.of(page, size,sort));
		logger.info("Fetching Product Details");
		
		return productPage.map(product -> mapEntitytodto(product));
	}

	@Cacheable(value = "products", key = "#skuCode")
	public ProductResponse getProductDetail(String skuCode) {
	    Product product = productrepository.findByskuCode(skuCode)
	            .orElseThrow(() -> new ProductNotFoundException("Product not found for SKU: " + skuCode));
	    return mapEntitytodto(product);
	}

	

	private ProductResponse mapEntitytodto(Product product) {
		
		ProductResponse productresponse= ProductResponse.builder()
				.id(product.getId())
				.name(product.getName())
				.description(product.getDescription())
				.price(product.getPrice())
				.skuCode(product.getSkuCode())
				.build();
		return productresponse;
	
	}
	
}
