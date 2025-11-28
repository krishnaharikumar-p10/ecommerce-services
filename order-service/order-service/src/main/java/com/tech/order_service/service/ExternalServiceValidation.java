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

    @Retry(name = "productServiceRetry", fallbackMethod = "productFallback")
    @CircuitBreaker(name = "productServiceCB", fallbackMethod = "productFallback")
    public ProductResponse validateProduct(String skuCode, String correlationId) {
        try {
            return productServiceClient.getProduct(skuCode, correlationId)
                    .orElseThrow(() -> new ProductUnavailableException("Product not found: " + skuCode));
        } catch (ProductUnavailableException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("System failure", ex);
        }
    }

     public ProductResponse productFallback(String skuCode, String correlationId, Throwable t) {
         if (t instanceof ProductUnavailableException) {
             throw (ProductUnavailableException) t; 
         }
         throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                 "Product service unavailable for SKU: " + skuCode, t); 
     }

     @Retry(name = "inventoryServiceRetry", fallbackMethod = "inventoryFallback")
     @CircuitBreaker(name = "inventoryServiceCB", fallbackMethod = "inventoryFallback")
     public InventoryResponse validateInventory(String skuCode, int quantity, String correlationId) {
         try {
             InventoryResponse inventory = inventoryServiceClient.getInventory(skuCode, correlationId);
             if (inventory == null || inventory.getAvailableQuantity() < quantity) {
                 throw new OutOfStockException("Out of stock: " + skuCode);
             }
             return inventory;
         } catch (OutOfStockException ex) {
             throw ex; 
         } catch (Exception ex) {
             throw new RuntimeException("System failure", ex); 
         }
     }

     public InventoryResponse inventoryFallback(String skuCode, int quantity, String correlationId, Throwable t) {
         if (t instanceof OutOfStockException) {
             throw (OutOfStockException) t;
         }
         throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                 "Inventory service unavailable for SKU: " + skuCode, t);
     }

    
}
