package com.tech.payment_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tech.payment_service.dto.PaymentResponse;
import com.tech.payment_service.service.PaymentService;
@RestController
@RequestMapping("/api")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/pay")
    public PaymentResponse pay(@RequestParam String orderNumber, @RequestParam String cardNumber) {
        return paymentService.processPayment(orderNumber, cardNumber);
    }
}

