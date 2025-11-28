package com.tech.order_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tech.order_service.model.Cart;

public interface CartRepository extends JpaRepository<Cart, Long>{

	List<Cart> findByCustomerId(Integer customerId);

}
