package com.tech.order_service.client;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.tech.order_service.dto.ProductResponse;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductServiceClient {

    @GetMapping("/api/product/get/{skuCode}")
    ProductResponse getProduct(@PathVariable String skuCode,
                               @RequestHeader("X-Correlation-Id") String correlationId);
}