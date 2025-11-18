package com.tech.product_service.service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.tech.product_service.controller.ProductController;
import com.tech.product_service.dto.ProductRequest;
import com.tech.product_service.dto.ProductResponse;
import com.tech.product_service.model.Product;
import com.tech.product_service.repository.ProductRepository;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
	
	private final ProductRepository productrepository;
	
	private static final Logger logger= LoggerFactory.getLogger(ProductService.class);

	@Autowired
	private WebClient.Builder webClientBuilder;
	

	
	public void createProduct(ProductRequest productRequest) {
	  
	    Boolean inStock = webClientBuilder.build()
	            .get()
	            .uri("http://inventory-service/api/inventory/{skuCode}", productRequest.getSkuCode())
	            .retrieve()
	            .bodyToMono(Boolean.class)
	            .timeout(Duration.ofSeconds(5))
	            .block(); 

	    if (inStock) {
	        Product product = Product.builder()
	                .id(productRequest.getId())
	                .name(productRequest.getName())
	                .description(productRequest.getDescription())
	                .price(productRequest.getPrice())
	                .skuCode(productRequest.getSkuCode())
	                .build();

	        productrepository.save(product); 
	        logger.info("Product added: {}", product.getSkuCode());
	    } else {
	        logger.warn("Cannot add product {}: stock not available", productRequest.getSkuCode());
	        
	    }
	}
	
	
	@Cacheable(value = "products", key = "#skuCode")
	public Optional<ProductResponse> getProductbyId(String skuCode) {
	    return productrepository.findByskuCode(skuCode)
	            .map(this::mapEntitytodto);
	}
	
	
	
	
	public Page<ProductResponse> getProduct(int page, int size, String sortBy , String sortDir) {
		
		Sort sort= sortDir.equalsIgnoreCase("desc")
				?Sort.by(sortBy).descending()
				:Sort.by(sortBy).ascending();
		
		Page<Product> productPage = productrepository.findAll(PageRequest.of(page, size,sort));
		
		return productPage.map(product -> mapEntitytodto(product));
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
