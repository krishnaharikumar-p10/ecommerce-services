package com.tech.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.tech.order_service.dto.InventoryResponse;

@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryServiceClient {
	@GetMapping("/api/inventory/check/{skuCode}")
    InventoryResponse getInventory(@PathVariable String skuCode,
                                   @RequestHeader("X-Correlation-Id") String correlationId);
	
}
