package com.tech.order_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tech.order_service.dto.CartDTO;
import com.tech.order_service.service.CartService;

@RestController
@RequestMapping("/cart")
public class CartController {
	
	@Autowired
	private  CartService cartService;
	
	@PostMapping
	public void addToCart(@RequestHeader("X-USER-ID") Integer customerId,@RequestHeader("X-USERNAME") String customerName,
			@RequestBody CartDTO cartRequest) {
		cartService.addToCart(customerId,customerName ,cartRequest);
	}
	
    @GetMapping
    public List<CartDTO> getCartItems(@RequestHeader("X-USER-ID") Integer customerId) {
        return cartService.getCartItems(customerId);
    }

}
