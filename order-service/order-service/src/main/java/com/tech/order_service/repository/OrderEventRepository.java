package com.tech.order_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tech.order_service.model.OrderLogTable;

@Repository
public interface OrderEventRepository extends JpaRepository<OrderLogTable, Long> {
    boolean existsByEventId(String eventId);
}
