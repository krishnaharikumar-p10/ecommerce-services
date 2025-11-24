package com.tech.payment_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.tech.payment_service.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	Payment findByOrderNumber(String orderNumber);

}
