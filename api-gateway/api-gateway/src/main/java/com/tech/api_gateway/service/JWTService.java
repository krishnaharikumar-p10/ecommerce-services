package com.tech.api_gateway.service;


import java.security.Key;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
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

    
	public boolean validateToken(String token) {
	    try {
	        Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
	        return true;
	    } catch (Exception e) {
	        return false;
	    }
	}


	public String extractUsername(String token) {
	    return Jwts.parserBuilder()
	            .setSigningKey(getKey())
	            .build()
	            .parseClaimsJws(token)
	            .getBody()
	            .getSubject();
	}

	public Set<String> extractRoles(String token) {
	    Claims claims = Jwts.parserBuilder()
	            .setSigningKey(getKey())
	            .build()
	            .parseClaimsJws(token)
	            .getBody();
	    List<String> rolesList = (List<String>) claims.get("roles");
	    return new HashSet<>(rolesList); 
	}
}
