package com.tech.shipping_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tech.shipping_service.model.ShippingLog;

public interface ShippingLogRepository extends JpaRepository<ShippingLog,Long>{

	boolean existsByEventId(String eventId);

}
