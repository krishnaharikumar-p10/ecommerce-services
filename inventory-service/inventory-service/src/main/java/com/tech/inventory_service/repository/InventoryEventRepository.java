package com.tech.inventory_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tech.inventory_service.model.InventoryEvent;

public interface InventoryEventRepository extends JpaRepository<InventoryEvent, Long> {
    boolean existsByEventId(String eventId);
}
