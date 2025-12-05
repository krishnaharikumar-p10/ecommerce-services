package com.tech.authentication_service.service;

import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tech.authentication_service.model.Users;
import com.tech.authentication_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class MyUserService  implements UserDetailsService{
	
	private final UserRepository userrepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	 
		Users user= userrepository.findByEmail(email);
		
		if (user==null) {
			throw new UsernameNotFoundException("User not found");
		}
		
		return User.builder()
				.username(user.getUsername())
				.password(user.getPassword())
				.authorities(user.getRoles().stream()
						.map(role -> new SimpleGrantedAuthority("ROLE_"+role.getName()))
						.collect(Collectors.toList()))
				.build();
				
	}

}
