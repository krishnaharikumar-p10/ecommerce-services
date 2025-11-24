package com.tech.order_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tech.order_service.model.Orders;

public interface OrderRepository extends JpaRepository<Orders,Long>{

	int deleteByStatus(String string);

	Orders findByOrderNumber(String orderNumber);

	List<Orders> findByCustomerName(String customerName);

}
