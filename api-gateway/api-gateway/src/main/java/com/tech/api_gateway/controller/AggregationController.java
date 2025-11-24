package com.tech.api_gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.tech.api_gateway.responses.InventoryResponse;
import com.tech.api_gateway.responses.ProductInventoryResponse;
import com.tech.api_gateway.responses.ProductResponse;


import reactor.core.publisher.Mono;

@RestController
public class AggregationController {
	
	@Autowired
	private  WebClient.Builder webClientBuilder;
	
	@GetMapping("/product-info/{skuCode}")
	 public Mono<ProductInventoryResponse> getProductWithStock(@PathVariable String skuCode) {

        
        
        Mono<ProductResponse> productMono = webClientBuilder.build().get().uri("http://product-service/api/product/get/{skuCode}",skuCode)
        		.retrieve().bodyToMono(ProductResponse.class);
        
        Mono<InventoryResponse> inventoryMono =  webClientBuilder.build().get().uri("http://inventory-service/api/inventory/check/{skuCode}",skuCode)
        		.retrieve().bodyToMono(InventoryResponse.class);
        
        return  Mono.zip(productMono, inventoryMono,
        		(product ,inventory) -> new ProductInventoryResponse(product,inventory));
        
        
	}
	
	
}