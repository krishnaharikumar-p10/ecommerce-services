package com.tech.payment_service.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.tech.payment_service.dto.OrderEventMessage;
import com.tech.payment_service.dto.PaymentEventMessage;
import com.tech.payment_service.dto.PaymentResponse;
import com.tech.payment_service.model.Payment;
import com.tech.payment_service.model.PaymentLogTable;
import com.tech.payment_service.repository.PaymentLogRepository;
import com.tech.payment_service.repository.PaymentRepository;

import tools.jackson.databind.ObjectMapper;


@Service
public class PaymentService {
	private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final PaymentLogRepository paymentLogRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository,
                          PaymentLogRepository paymentLogRepository,
                          KafkaTemplate<String, String> kafkaTemplate,
                          ObjectMapper objectMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentLogRepository = paymentLogRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    
    
	public PaymentResponse processPayment(String orderNumber,String cardNumber) {
		
		 Payment payment = paymentRepository.findByOrderNumber(orderNumber);
		 if (payment.getStatus().equalsIgnoreCase("PAYMENT_SUCCESS")) {
			    return new PaymentResponse(
			        payment.getOrderNumber(),
			        payment.getTransactionId(),
			        "Payment already processed"
			    );
			}
		    
		 boolean success = cardNumber != null && cardNumber.length() == 16;
		 
		 String TransactionId=UUID.randomUUID().toString();
		 payment.setStatus(success ? "PAYMENT_SUCCESS" : "PAYMENT_FAILED");
		 payment.setTransactionId(TransactionId);
	     paymentRepository.save(payment);
	     
	     String message = "Payment attempt " + payment.getStatus() + " for order " 
                 + payment.getOrderNumber();
	     
	     String event_Id= UUID.randomUUID().toString();
	     PaymentLogTable log = new PaymentLogTable();
	     log.setEventId(event_Id); 
	     log.setOrderNumber(payment.getOrderNumber());
	     log.setEventType(payment.getStatus());
	     log.setDetails(message);
	     log.setProcessedAt(LocalDateTime.now());
	     paymentLogRepository.save(log);
	     
	     PaymentEventMessage event = new PaymentEventMessage();
	     event.setStatus(payment.getStatus());
	     event.setEventId(event_Id);
	     event.setOrderNumber(payment.getOrderNumber());
	     event.setTransactionId(TransactionId);
	     
	     String eventJson = objectMapper.writeValueAsString(event);
	     
	     kafkaTemplate.send("payment-response-topic", eventJson);
	     String response = success ? "Payment Successful. Thank you for ordering!" 
                 : "Payment Failed. Please try again.";
	     
	     return new PaymentResponse(payment.getOrderNumber(), payment.getTransactionId(), response);
		 


	     
	}
	
	public void createPendingPayment(OrderEventMessage event) {
		
		boolean alreadyProcessed = paymentLogRepository.existsByEventId(event.getEventId());
		if (alreadyProcessed) {
	        return; 
	    }
		logger.info("hello");
		Payment payment = paymentRepository.findByOrderNumber(event.getOrderNumber());
		payment = new Payment();
        payment.setCustomerName(event.getCustomerName());
        payment.setOrderNumber(event.getOrderNumber());
        payment.setTotalAmount(event.getTotalAmount());
        payment.setStatus("PAYMENT_PENDING");
        payment.setPaymentMethod("CARD");
        payment.setTransactionId(null);
        paymentRepository.save(payment);
        
        PaymentLogTable log = new PaymentLogTable();
        log.setEventId(event.getEventId());     
        log.setOrderNumber(event.getOrderNumber());
        log.setEventType("PAYMENT_PENDING");
        log.setDetails("Pending payment created");
        log.setProcessedAt(LocalDateTime.now());
        paymentLogRepository.save(log);
	}




}
