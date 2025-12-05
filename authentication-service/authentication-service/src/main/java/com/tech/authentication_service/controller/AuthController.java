package com.tech.authentication_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.tech.authentication_service.dto.SignupRequest;
import com.tech.authentication_service.model.Users;
import com.tech.authentication_service.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final Logger logger= LoggerFactory.getLogger(AuthController.class);

    
	private final UserService userservice;
	
	@PostMapping("/login")
	public String login(@RequestBody Users user) {
		return userservice.verify(user);
		
	}
	
	@PostMapping("/logout")
	public String logout(@RequestHeader("Authorization") String authHeader) {
	    if (authHeader != null && authHeader.startsWith("Bearer ")) {
	        String token = authHeader.substring(7);
	        userservice.logout(token);
	    }
	    return "Logged out";
	}
	
	
	@PostMapping("/signup")
	public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
	    return ResponseEntity.ok(userservice.register(request));
	}




}
