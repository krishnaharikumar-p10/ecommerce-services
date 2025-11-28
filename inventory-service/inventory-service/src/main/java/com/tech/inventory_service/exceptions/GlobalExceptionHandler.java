package com.tech.inventory_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;

import org.springframework.http.ProblemDetail;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ProblemDetail> handleAllExceptions(Exception ex, WebRequest request) {
	    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
	    problem.setTitle("Internal Server Error");
	    problem.setDetail(ex.getMessage());
	    problem.setInstance(URI.create(((ServletWebRequest) request).getRequest().getRequestURI()));
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
	}
	@ExceptionHandler(SKUNotFoundException.class)
	public ResponseEntity<ProblemDetail> handleSKUNotFound(SKUNotFoundException ex, WebRequest request) {
	    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
	    problem.setTitle("SKU Not Found");
	    problem.setDetail(ex.getMessage());
	    problem.setInstance(URI.create(((ServletWebRequest) request).getRequest().getRequestURI()));
	    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
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

	
	
	
}