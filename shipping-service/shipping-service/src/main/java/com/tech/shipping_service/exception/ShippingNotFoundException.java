package com.tech.shipping_service.exception;

public class ShippingNotFoundException extends RuntimeException {
    public ShippingNotFoundException(String message) {
        super(message);
    }
}
