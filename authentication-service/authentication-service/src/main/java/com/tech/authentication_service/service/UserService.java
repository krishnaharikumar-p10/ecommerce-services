package com.tech.authentication_service.service;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.tech.authentication_service.dto.SignupRequest;
import com.tech.authentication_service.model.Role;
import com.tech.authentication_service.model.Users;
import com.tech.authentication_service.repository.RoleRepository;
import com.tech.authentication_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
	private final RedisTemplate<String, Object> redisTemplate;
	
	private final JWTService JwtService;

	private final PasswordEncoder passwordEncoder; 
	
	private final AuthenticationManager authmanager;
	
	private final UserRepository userRepository;
	
	private final RoleRepository roleRepository;
	
	public String verify(Users user) {
		
		Authentication authentication = 
				authmanager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword()));
		
		if (authentication.isAuthenticated()) {
			
	        Set<String> roles = authentication.getAuthorities()
	                .stream()
	                .map(GrantedAuthority::getAuthority)
	                .collect(Collectors.toSet());
	        
	        Users dbUser = userRepository.findByEmail(user.getEmail());
	        
	        String jwt= JwtService.generateToken(
	        		dbUser.getId(),
	        		dbUser.getUsername(),
	        		roles);
	        
	        redisTemplate.opsForValue().set(jwt, dbUser.getId(), 30, TimeUnit.MINUTES);

	        
	        return jwt;

	        }

		return "Failed ";
		
	}

	public void logout(String token) {
		
		redisTemplate.delete(token);
		
	}

	public String register(SignupRequest request) {
		
        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email already exists";
        }
        
        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Role not found: CUSTOMER"));
        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(customerRole));

        userRepository.save(user);
        return "User registered successfully";
    }
}
	

