package com.tech.payment_service.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.kafka.core.KafkaTemplate;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;
import com.tech.payment_service.dto.PaymentResponse;
import com.tech.payment_service.model.Payment;
import com.tech.payment_service.repository.PaymentLogRepository;
import com.tech.payment_service.repository.PaymentRepository;


@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentLogRepository paymentLogRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PaymentService paymentService;
    
    static Stream<Arguments> cardProvider() {
        return Stream.of(
            Arguments.of("4111111111111111", true),  // Visa
            Arguments.of("5212345678901234", true),  // MasterCard
            Arguments.of("6012345678901234", true),  // RuPay
            Arguments.of("1234567890123456", false)  // Invalid
        );
    }

    @ParameterizedTest
    @MethodSource("cardProvider")
    void testProcessPayment_AllCards(String cardNumber, boolean expectedSuccess) throws Exception {
        String orderNumber = "ORD123";
        Integer customerId = 10;

        Payment savedPayment = new Payment();
        savedPayment.setOrderNumber(orderNumber);
        savedPayment.setCustomerId(customerId);
        savedPayment.setStatus("PAYMENT_PENDING");

        when(paymentRepository.findByOrderNumberAndCustomerId(orderNumber, customerId))
                .thenReturn(savedPayment);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        PaymentResponse response = paymentService.processPayment(orderNumber, cardNumber, customerId);

        if (expectedSuccess) {
            assertEquals("Payment Successful. Thank you for ordering!", response.getMessage());
            assertNotNull(response.getTransactionId());
        } else {
            assertEquals("Payment Failed. Please try again.", response.getMessage());
            assertEquals(null, response.getTransactionId());
        }

        verify(paymentRepository).save(any(Payment.class));
        verify(kafkaTemplate).send(eq("payment-response-topic"), anyString());
    }

    
    @Test
    void testProcessPayment_AlreadyProcessed() throws Exception {
        String orderNumber = "ORD127";
        String card = "4111111111111111";
        Integer customerId = 14;

        Payment savedPayment = new Payment();
        savedPayment.setOrderNumber(orderNumber);
        savedPayment.setCustomerId(customerId);
        savedPayment.setStatus("PAYMENT_SUCCESS");
        savedPayment.setTransactionId("TXN123");

        when(paymentRepository.findByOrderNumberAndCustomerId(orderNumber, customerId))
                .thenReturn(savedPayment);

        PaymentResponse response = paymentService.processPayment(orderNumber, card, customerId);

        assertEquals("Payment already processed", response.getMessage());
        assertEquals("TXN123", response.getTransactionId());
    }


    
}


