package com.tech.inventory_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tech.inventory_service.model.InventoryEvent;

@Repository
public interface InventoryEventRepository extends JpaRepository<InventoryEvent, Long> {
    boolean existsByEventId(String eventId);
}
