package com.tech.order_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.kafka.common.Uuid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.order_service.dto.OrderItemEventDTO;
import com.tech.order_service.dto.OrderPaymentSuccessMessage;
import com.tech.order_service.dto.PaymentEventMessage;
import com.tech.order_service.model.OrderLogTable;
import com.tech.order_service.model.Orders;
import com.tech.order_service.repository.OrderEventRepository;
import com.tech.order_service.repository.OrderRepository;

import jakarta.transaction.Transactional;

@Service
public class PaymentResponseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PaymentResponseConsumer.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderEventRepository eventRepository;
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Transactional
    @KafkaListener(topics = "payment-response-topic", groupId = "order-group")
    public void consumePaymentEvent(String message) {
        try {

            PaymentEventMessage event = objectMapper.readValue(message, PaymentEventMessage.class);
            logger.info("Received payment event: " + event);

            MDC.put("correlationId", event.getEventId());

            boolean alreadyProcessed = eventRepository.existsByEventId(event.getEventId());
            if (alreadyProcessed) {
                logger.info("Skipping duplicate payment event: " + event.getEventId());
                return;
            }

            Orders order = orderRepository.findByOrderNumber(event.getOrderNumber());
            if (order != null) {

              
                String finalStatus;
                if ("PAYMENT_SUCCESS".equals(event.getStatus())) {
                    finalStatus = "ORDER_CONFIRMED";
                    order.setTransactionId(event.getTransactionId());
                } else if ("PAYMENT_FAILED".equals(event.getStatus())) {
                    finalStatus = "ORDER_FAILED";
                    order.setTransactionId(null);
           
                } else {
                    logger.warn("Unknown payment status: " + event.getStatus());
                    return;
                }

                order.setStatus(finalStatus);
                orderRepository.save(order);

                OrderLogTable log = new OrderLogTable();
                log.setEventId(event.getEventId());
                log.setOrderNumber(event.getOrderNumber());
                log.setEventType(finalStatus);
                log.setDetails("Order status updated to: " + finalStatus);
                log.setProcessedAt(LocalDateTime.now());
                eventRepository.save(log);

                logger.info("Order updated with final status: " + finalStatus);


                if ("ORDER_CONFIRMED".equals(finalStatus)) {

                    String newEventId = Uuid.randomUuid().toString();

                    OrderPaymentSuccessMessage newEvent = new OrderPaymentSuccessMessage();
                    newEvent.setOrderNumber(order.getOrderNumber());
                    newEvent.setCustomerName(order.getCustomerName());
                    newEvent.setAddress(order.getAddress());
                    newEvent.setEventId(newEventId);
                    newEvent.setStatus(finalStatus);

                    List<OrderItemEventDTO> itemsDto = order.getOrderItems().stream()
                            .map(item -> new OrderItemEventDTO(
                                    item.getSkuCode(),
                                    item.getPrice(),
                                    item.getQuantity()
                            ))
                            .toList();

                    newEvent.setOrderItems(itemsDto);

                    String eventJson = objectMapper.writeValueAsString(newEvent);
                    kafkaTemplate.send("order-comfirmed", eventJson);

                    logger.info("Sent order-confirmed event " + newEvent.getOrderNumber());
                }

            } else {
                logger.warn("Order not found for orderNumber: " + event.getOrderNumber());
            }

        } catch (Exception e) {
            logger.error("Failed to process payment event", e);
        } finally {
            MDC.clear();
        }
    }
}