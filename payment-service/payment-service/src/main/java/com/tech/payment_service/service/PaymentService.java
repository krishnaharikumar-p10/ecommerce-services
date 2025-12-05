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
import com.tech.payment_service.exception.PaymentNotFoundException;
import com.tech.payment_service.model.Payment;
import com.tech.payment_service.model.PaymentLogTable;
import com.tech.payment_service.repository.PaymentLogRepository;
import com.tech.payment_service.repository.PaymentRepository;

import jakarta.servlet.http.HttpServletRequest;
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
    
    
	public PaymentResponse processPayment(String orderNumber,String cardNumber,Integer customerId) {
		
		logger.info("PAYMENT METHOD");
		Payment payment = paymentRepository.findByOrderNumberAndCustomerId(orderNumber, customerId);
		if (payment == null) {
		    throw new PaymentNotFoundException("Payment not found for this customer");
		}

		 
		 if (payment.getStatus().equalsIgnoreCase("PAYMENT_SUCCESS")) {
			    return new PaymentResponse(
			        payment.getOrderNumber(),
			        payment.getTransactionId(),
			        "Payment already processed"
			    );
			}
		 
	     String correlationId = MDC.get("correlationId");

	     boolean success = validateTestCard(cardNumber);
		 
		 String TransactionId=success ? UUID.randomUUID().toString() : null;
		 payment.setStatus(success ? "PAYMENT_SUCCESS" : "PAYMENT_FAILED");
		 payment.setTransactionId(TransactionId);
	     paymentRepository.save(payment);
	     
	     String message = "Payment attempt " + payment.getStatus() + " for order " 
                 + payment.getOrderNumber();
	     
	     String event_Id= UUID.randomUUID().toString();
	     PaymentLogTable log = new PaymentLogTable();
	     log.setEventId(event_Id); 
	     log.setCustomerId(customerId);
	     log.setOrderNumber(payment.getOrderNumber());
	     log.setEventType(payment.getStatus());
	     log.setDetails(message);
	     log.setProcessedAt(LocalDateTime.now());
	     paymentLogRepository.save(log);
	     
	     PaymentEventMessage event = new PaymentEventMessage();
	     event.setStatus(payment.getStatus());
	     event.setEventId(event_Id);
	     event.setCustomerId(customerId);
	     event.setOrderNumber(payment.getOrderNumber());
	     event.setTransactionId(TransactionId);
	     event.setCorrelationId(correlationId);
	     
	     String eventJson = objectMapper.writeValueAsString(event);
	     
	     kafkaTemplate.send("payment-response-topic", eventJson);
	     String response = success ? "Payment Successful. Thank you for ordering!" 
                 : "Payment Failed. Please try again.";
	     
	     return new PaymentResponse(payment.getOrderNumber(), payment.getTransactionId(), response);
		 

	}
	
	private boolean validateTestCard(String card) {
	    if (card == null || card.length() != 16) {
	        return false;
	    }

	    // Visa – starts with 4
	    if (card.startsWith("4")) {
	        return true;
	    }

	    // MasterCard – starts with 51 to 55
	    if (card.startsWith("51") || card.startsWith("52") || 
	        card.startsWith("53") || card.startsWith("54") || 
	        card.startsWith("55")) {
	        return true;
	    }

	    // RuPay – starts with 60, 65, 81
	    if (card.startsWith("60") || card.startsWith("65") || card.startsWith("81")) {
	        return true;
	    }

	    return false;
	}

	
	public void createPendingPayment(OrderEventMessage event) {
		
		boolean alreadyProcessed = paymentLogRepository.existsByEventId(event.getEventId());
		if (alreadyProcessed) {
	        return; 
	    }
		logger.info("Entering payment pending status");
		
		Payment payment = new Payment();
        payment.setCustomerName(event.getCustomerName());
        payment.setCustomerId(event.getCustomerId());
        payment.setOrderNumber(event.getOrderNumber());
        payment.setTotalAmount(event.getTotalAmount());
        payment.setStatus("PAYMENT_PENDING");
        payment.setPaymentMethod("CARD");
        payment.setTransactionId(null);
        paymentRepository.save(payment);
        
        PaymentLogTable log = new PaymentLogTable();
        log.setEventId(event.getEventId());     
        log.setCustomerId(event.getCustomerId());
        log.setOrderNumber(event.getOrderNumber());
        log.setEventType("PAYMENT_PENDING");
        log.setDetails("Pending payment created");
        log.setProcessedAt(LocalDateTime.now());
        paymentLogRepository.save(log);
	}




}
