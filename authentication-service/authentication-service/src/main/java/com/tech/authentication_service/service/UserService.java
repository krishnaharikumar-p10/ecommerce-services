package com.tech.authentication_service.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import com.tech.authentication_service.model.Users;
import com.tech.authentication_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
	private final JWTService JwtService;

	private final AuthenticationManager authmanager;
	
	private final UserRepository userRepository;
	
	public String verify(Users user) {
		
		
		
		Authentication authentication = 
				authmanager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));
		
		if (authentication.isAuthenticated()) {
			
	        Set<String> roles = authentication.getAuthorities()
	                .stream()
	                .map(GrantedAuthority::getAuthority)
	                .collect(Collectors.toSet());
	        
	        Users dbUser= userRepository.findByUsername(user.getUsername());
	        
	        
	        String jwt= JwtService.generateToken(
	        		dbUser.getId(),
	        		dbUser.getUsername(),
	        		roles);
	        
	        return jwt;

	        }

		return "Failed ";
		
	}

}