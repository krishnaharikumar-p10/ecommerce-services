package com.tech.authentication_service.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


@Service 
public class JWTService {
	
    @Value("${jwt.secret}")
    private String secretKey; 

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
	public String generateToken(int id,String username,Set<String> roles) {
		
	
		Map<String, Object> claims= new HashMap<>();
		 claims.put("roles", roles);
		 claims.put("userId", id);
		
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(username)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 30 ))
				.signWith(getKey())
				.compact();
	}
}