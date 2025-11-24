package com.tech.order_service.exceptions;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.ProblemDetail;

@ControllerAdvice
public class GlobalExceptionalHandler {

    @ExceptionHandler(ProductUnavailableException.class)
    public ResponseEntity<ProblemDetail> handleProductUnavailable(ProductUnavailableException ex, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Product Unavailable");
        problem.setDetail(ex.getMessage());
        problem.setInstance(URI.create(((ServletWebRequest) request).getRequest().getRequestURI()));
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<ProblemDetail> handleOutOfStock(OutOfStockException ex, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Out of Stock");
        problem.setDetail(ex.getMessage());
        problem.setInstance(URI.create(((ServletWebRequest) request).getRequest().getRequestURI()));
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleSystemError(Exception ex, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Internal Server Error");
        problem.setDetail("Unexpected error while processing order. Please try again later.");
        problem.setInstance(URI.create(((ServletWebRequest) request).getRequest().getRequestURI()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }
}
