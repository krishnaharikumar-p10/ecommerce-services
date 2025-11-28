package com.tech.shipping_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tech.shipping_service.dto.ShippingResponse;
import com.tech.shipping_service.service.ShippingService;

@RestController
@RequestMapping("/shipping")
public class ShippingController {

    @Autowired
    private  ShippingService shippingService;

    @PostMapping("/ship")
    public ShippingResponse markShipped(@RequestParam String orderNumber) {
        return shippingService.markOrderShipped(orderNumber);
    }
    
    @GetMapping("/{orderNumber}")
    public String getStatus(@PathVariable String orderNumber) {
    	return shippingService.getStatus(orderNumber);
    }
}