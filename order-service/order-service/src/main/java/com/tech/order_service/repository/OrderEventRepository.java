package com.tech.order_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tech.order_service.model.OrderEvent;

@Repository
public interface OrderEventRepository extends JpaRepository<OrderEvent, Long> {
    boolean existsByEventId(String eventId);
}
