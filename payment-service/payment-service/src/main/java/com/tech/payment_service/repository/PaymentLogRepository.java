package com.tech.payment_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tech.payment_service.model.Payment;
import com.tech.payment_service.model.PaymentLogTable;

@Repository
public interface PaymentLogRepository extends JpaRepository<PaymentLogTable, Long> {
    Payment findByOrderNumber(String orderNumber);

	boolean existsByEventId(String eventId);
}
