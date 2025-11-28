package com.tech.order_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tech.order_service.model.Orders;

@Repository
public interface OrderRepository extends JpaRepository<Orders,Long>{

	int deleteByStatus(String string);

	Orders findByOrderNumber(String orderNumber);

	List<Orders> findByCustomerId(Integer customerId);

}
