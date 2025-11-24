package com.tech.order_service.service;

import org.springframework.stereotype.Service;

import com.tech.order_service.client.InventoryServiceClient;
import com.tech.order_service.client.ProductServiceClient;
import com.tech.order_service.dto.InventoryResponse;
import com.tech.order_service.dto.ProductResponse;

import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExternalServiceValidation {

    private final ProductServiceClient productServiceClient;
    private final InventoryServiceClient inventoryServiceClient;

    @Retry(name = "productServiceRetry")
    @CircuitBreaker(name = "productServiceCB", fallbackMethod = "productFallback")
    public ProductResponse validateProduct(String skuCode, String correlationId) {
        return productServiceClient.getProduct(skuCode, correlationId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + skuCode));
    }

    @Retry(name = "inventoryServiceRetry")
    @CircuitBreaker(name = "inventoryServiceCB", fallbackMethod = "inventoryFallback")
    public InventoryResponse validateInventory(String skuCode, int quantity, String correlationId) {
        InventoryResponse inventory = inventoryServiceClient.getInventory(skuCode, correlationId);
        if (inventory == null || inventory.getAvailableQuantity() < quantity) {
            throw new RuntimeException("Out of stock: " + skuCode);
        }
        return inventory;
    }

    public ProductResponse productFallback(String skuCode, String correlationId, Throwable t) {
       
        throw new RuntimeException("Product service unavailable for SKU: " + skuCode);
    }

    public InventoryResponse inventoryFallback(String skuCode, int quantity, String correlationId, Throwable t) {
      
        throw new RuntimeException("Inventory service unavailable for SKU: " + skuCode);
    }
}
