package com.tech.order_service.exceptions;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

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

    
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ProblemDetail> handleMethodNotSupported(
	        HttpRequestMethodNotSupportedException ex, WebRequest request) {

	    HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();

	    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.METHOD_NOT_ALLOWED);
	    problem.setTitle("Method Not Allowed");
	    problem.setDetail(ex.getMessage());
	    problem.setInstance(java.net.URI.create(servletRequest.getRequestURI()));

	    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(problem);
	}
    
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();
        HttpStatusCode statusCode = ex.getStatusCode(); 

        ProblemDetail problem = ProblemDetail.forStatus(statusCode.value()); 
        problem.setTitle(statusCode.toString());
        problem.setDetail(ex.getReason());
        problem.setInstance(URI.create(servletRequest.getRequestURI()));

        return ResponseEntity.status(statusCode.value()).body(problem);
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
