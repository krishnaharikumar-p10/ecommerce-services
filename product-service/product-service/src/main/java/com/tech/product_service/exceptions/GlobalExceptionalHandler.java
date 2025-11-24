package com.tech.product_service.exceptions;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionalHandler {
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ProblemDetail> handleAllExceptions(Exception ex, WebRequest request) {
	    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
	    problem.setTitle("Internal Server Error");
	    problem.setDetail(ex.getMessage());
	    problem.setInstance(URI.create(((ServletWebRequest) request).getRequest().getRequestURI()));
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
	}
	
	@ExceptionHandler(ProductNotFoundException.class)
	public ResponseEntity<ProblemDetail> handleProductNotFound(ProductNotFoundException ex, WebRequest request) {
	    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
	    problem.setTitle("Product Not Found");
	    problem.setDetail(ex.getMessage());
	    problem.setInstance(URI.create(((ServletWebRequest) request).getRequest().getRequestURI()));
	    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
	}

	
	

}
