package com.tech.shipping_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.kafka.common.Uuid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.shipping_service.dto.OrderPaymentSuccessMessage;
import com.tech.shipping_service.dto.OrderShippedEvent;
import com.tech.shipping_service.dto.ShippedItemDTO;
import com.tech.shipping_service.dto.ShippingResponse;
import com.tech.shipping_service.exception.ShippingNotFoundException;
import com.tech.shipping_service.model.Shipping;
import com.tech.shipping_service.model.ShippingItems;
import com.tech.shipping_service.model.ShippingLog;
import com.tech.shipping_service.repository.ShippingLogRepository;
import com.tech.shipping_service.repository.ShippingRepository;

@Service
public class ShippingService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;
    
	@Autowired
	private ShippingRepository shippingRepository;
	
    @Autowired
    private ShippingLogRepository shippingLogRepository;
    
    private final Logger logger= LoggerFactory.getLogger(ShippingService.class);

	public void prepareShipping(OrderPaymentSuccessMessage event) {
		
		logger.info("IN PREPARE SHIPPING METHOD");
	    Shipping shipping = new Shipping();
	    shipping.setOrderNumber(event.getOrderNumber());
	    shipping.setCustomerName(event.getCustomerName());
	    shipping.setAddress(event.getAddress());
	    shipping.setStatus("PENDING_PREPARATION"); 
	    shipping.setTrackingNumber(null);
	    
	    List<ShippingItems> items = event.getOrderItems().stream().map(i -> {
	        ShippingItems si = new ShippingItems();
	        si.setSkuCode(i.getSkuCode());
	        si.setPrice(i.getPrice());
	        si.setQuantity(i.getQuantity());
	        return si;
	    }).toList();

	    shipping.setShippingItems(items);
	    shippingRepository.save(shipping);
	}

	public ShippingResponse markOrderShipped(String orderNumber) {
		
		Shipping shippingOrder= shippingRepository.findByOrderNumber(orderNumber);
		
        if (shippingOrder == null) {
        	 throw new ShippingNotFoundException("Shipping record not found for order: " + orderNumber);        }
        
        if ("ORDER_SHIPPED".equalsIgnoreCase(shippingOrder.getStatus())) {
            
            return new ShippingResponse(
                shippingOrder.getOrderNumber(),
                shippingOrder.getTrackingNumber(),
                "Order already shipped"
            );
        }
        
        String correlationId = MDC.get("correlationId");
        logger.info("ORDER SHIPPED METHOD");
        shippingOrder.setStatus("ORDER_SHIPPED");
        String TrackingNumber= Uuid.randomUuid().toString();
        shippingOrder.setTrackingNumber(TrackingNumber);
        shippingRepository.save(shippingOrder);
        
        String event_Id = Uuid.randomUuid().toString();
        ShippingLog log = new ShippingLog();
        log.setOrderNumber(orderNumber);
        log.setEventId(event_Id);
        log.setEventType("ORDER_SHIPPED");
        log.setDetails("Order shipped for orderNumber : " + orderNumber);
        log.setProcessedAt(LocalDateTime.now());
        shippingLogRepository.save(log);
        


        try {
            OrderShippedEvent shippedEvent = new OrderShippedEvent();
            shippedEvent.setOrderNumber(orderNumber);
            shippedEvent.setTrackingNumber(TrackingNumber);
            shippedEvent.setEventId(event_Id);
            shippedEvent.setCorrelationid(correlationId);
            
            List<ShippedItemDTO> shippedItems = shippingOrder.getShippingItems().stream().map(si -> {
                ShippedItemDTO dto = new ShippedItemDTO();
                dto.setSkuCode(si.getSkuCode());
                dto.setQuantity(si.getQuantity());
                return dto;
            }).toList();
            
            
            shippedEvent.setItems(shippedItems);

            String message = objectMapper.writeValueAsString(shippedEvent);
            kafkaTemplate.send("order-shipped", message);
        } catch (Exception e) {
            System.err.println("Failed to send OrderShippedEvent: " + e.getMessage());
        }
        return new ShippingResponse(
                shippingOrder.getOrderNumber(),
                shippingOrder.getTrackingNumber(),
                "Order Shipped Successfully ");
          
		
	}

	public String getStatus(String orderNumber) {
		Shipping shippingOrder = shippingRepository.findByOrderNumber(orderNumber);
		if (shippingOrder == null) {
		    throw new ShippingNotFoundException("Shipping record not found for order: " + orderNumber);
		}
		return shippingOrder.getStatus();
	}

}
