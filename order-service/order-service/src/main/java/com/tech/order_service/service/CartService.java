package com.tech.order_service.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tech.order_service.dto.CartDTO;
import com.tech.order_service.model.Cart;
import com.tech.order_service.repository.CartRepository;

@Service
public class CartService {

	@Autowired
	private CartRepository cartRepository;
	public void addToCart(Integer userId, String customerName,CartDTO cartRequest) {

		Cart cart= new Cart();
		cart.setCustomerId(userId);
		cart.setCustomerName(customerName);
		cart.setPrice(cartRequest.getPrice());
		cart.setQuantity(cartRequest.getQuantity());
		cart.setSkuCode(cartRequest.getSkuCode());
		cartRepository.save(cart);
		
		
	}
	public List<CartDTO> getCartItems(Integer customerId) {
		
		List<Cart> carts = cartRepository.findByCustomerId(customerId);
		

	    List<CartDTO> result = new ArrayList<>();

	    for (Cart c : carts) {
	        CartDTO dto = new CartDTO();
	        dto.setSkuCode(c.getSkuCode());
	        dto.setPrice(c.getPrice());
	        dto.setQuantity(c.getQuantity());
	        result.add(dto);
	    }

	    return result;
	}


	
}
