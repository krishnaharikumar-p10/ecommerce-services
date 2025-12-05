package com.tech.order_service.service;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.tech.order_service.client.InventoryServiceClient;
import com.tech.order_service.client.ProductServiceClient;
import com.tech.order_service.dto.InventoryResponse;
import com.tech.order_service.dto.ProductResponse;
import com.tech.order_service.exceptions.OutOfStockException;
import com.tech.order_service.exceptions.ProductUnavailableException;

import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExternalServiceValidation {

    private final ProductServiceClient productServiceClient;
    private final InventoryServiceClient inventoryServiceClient;

    @Retry(name = "productServiceRetry", fallbackMethod = "cartProductFallback")
    @CircuitBreaker(name = "productServiceCB", fallbackMethod = "cartProductFallback")
    public ProductResponse validateProductForCart(String skuCode, String correlationId) {
        try {       
            return productServiceClient.getProduct(skuCode, correlationId);
        } catch (Exception ex) {   
            throw ex;
        }
    }

    public ProductResponse cartProductFallback(String skuCode, String correlationId, Throwable t) {
       
        ProductResponse fallback = new ProductResponse();
        fallback.setSkuCode(skuCode);
        fallback.setName("Product info unavailable");
        fallback.setPrice(null); 
        return fallback;
    }
    
    @Retry(name = "productServiceRetry", fallbackMethod = "productFallback")
    @CircuitBreaker(name = "productServiceCB", fallbackMethod = "productFallback")
    public ProductResponse validateProduct(String skuCode, String correlationId) {
    	ProductResponse product;
        try {
            product = productServiceClient.getProduct(skuCode, correlationId);
        } catch (feign.FeignException.NotFound ex) {
            // Business exception, don't retry
            throw new ProductUnavailableException("Product unavailable: " + skuCode);
        } catch (Exception ex) {
            // Only retry/fallback on real service errors
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Unable to fetch product information at the moment. Please try again later.", ex);
        }

        if (product == null) {
            throw new ProductUnavailableException("Product unavailable: " + skuCode);
        }

        return product;
    }

    public ProductResponse productFallback(String skuCode, String correlationId, Throwable t) {
        if (t instanceof ProductUnavailableException) {        
            throw (ProductUnavailableException) t;
        }
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
        		"Unable to fetch product information at the moment. Please try again later.", t);
    }

    @Retry(name = "inventoryServiceRetry", fallbackMethod = "inventoryFallback")
    @CircuitBreaker(name = "inventoryServiceCB", fallbackMethod = "inventoryFallback")
    public InventoryResponse validateInventory(String skuCode, int quantity, String correlationId) {
        InventoryResponse inventory;
        try {
            inventory = inventoryServiceClient.getInventory(skuCode, correlationId);
        } catch (Exception ex) {
            // Only catch real service errors
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Unable to process your request right now. Please try again later.", ex);
        }

        if (inventory == null || inventory.getAvailableQuantity() < quantity) {
            throw new OutOfStockException("Out of stock: " + skuCode);
        }

        return inventory;
    }

    // Fallback is only for Resilience4j errors like network/service failures
    public InventoryResponse inventoryFallback(String skuCode, int quantity, String correlationId, Throwable t) {
        if (t instanceof OutOfStockException) {
            throw (OutOfStockException) t;
        }
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Unable to process your request right now. Please try again later.", t);
    }

    
}
