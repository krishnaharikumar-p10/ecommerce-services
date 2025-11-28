package com.tech.shipping_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tech.shipping_service.model.Shipping;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping,Long>{

	Shipping findByOrderNumber(String orderNumber);

	
}
