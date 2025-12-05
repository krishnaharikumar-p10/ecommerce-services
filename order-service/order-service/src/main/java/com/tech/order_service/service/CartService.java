package com.tech.order_service.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import com.tech.order_service.dto.CartRequest;
import com.tech.order_service.dto.CartResponse;
import com.tech.order_service.dto.ProductResponse;
import com.tech.order_service.model.Cart;
import com.tech.order_service.repository.CartRepository;

@Service
public class CartService {

	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ExternalServiceValidation  externalServiceValidation;
	
	private final Logger log= LoggerFactory.getLogger(CartService.class);

	public void updateCartItem(Integer userId, String customerName, String skuCode, int quantityChange) {
	   
	    Cart existingCart = cartRepository.findByCustomerIdAndSkuCode(userId, skuCode);

	    if (existingCart != null) {
	       
	        int newQuantity = existingCart.getQuantity() + quantityChange;

	        if (newQuantity > 0) {
	            existingCart.setQuantity(newQuantity);
	            cartRepository.save(existingCart);
	        } else {
	            
	            cartRepository.delete(existingCart);
	        }
	    } else {
	      
	        if (quantityChange > 0) {
	            Cart cart = new Cart();
	            cart.setCustomerId(userId);
	            cart.setCustomerName(customerName);
	            cart.setSkuCode(skuCode);
	            cart.setQuantity(quantityChange);
	            cartRepository.save(cart);
	        } else {
	            throw new IllegalArgumentException(
	                "Cannot reduce quantity for a product that is not in the cart"
	            );
	        }
	    }
	}


	public List<CartResponse> getCartItems(Integer customerId) {
        List<Cart> carts = cartRepository.findByCustomerId(customerId);
        List<CartResponse> result = new ArrayList<>();
        String correlationId = MDC.get("correlationId");

        for (Cart c : carts) {
            ProductResponse product = externalServiceValidation.validateProductForCart(c.getSkuCode(), correlationId);

            CartResponse dto = new CartResponse();
            dto.setSkuCode(c.getSkuCode());
            dto.setProductName(product.getName());
            dto.setProductPrice(product.getPrice()); 
            dto.setQuantity(c.getQuantity());
            dto.setTotalPrice(product.getPrice() != null
                    ? product.getPrice().multiply(BigDecimal.valueOf(c.getQuantity()))
                    : null); 

            result.add(dto);
        }
        return result;
    }


	
}
