package com.tech.inventory_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tech.inventory_service.model.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long> {

	 Optional<Inventory>findByskuCode(String skuCode);

}
