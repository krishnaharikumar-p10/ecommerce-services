package com.tech.authentication_service;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tech.authentication_service.model.Role;
import com.tech.authentication_service.model.Users;
import com.tech.authentication_service.repository.RoleRepository;
import com.tech.authentication_service.repository.UserRepository;

@SpringBootApplication
public class AuthenticationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationServiceApplication.class, args);
	}
	
	/*
    @Bean
    public CommandLineRunner loadData(RoleRepository roleRepository, UserRepository usersRepository,PasswordEncoder passwordEncoder) {
        return args -> {
            // Create CUSTOMER role
            Role customerRole = new Role();
            customerRole.setName("CUSTOMER");
            roleRepository.save(customerRole);

            // Create a user with CUSTOMER role
            Users user = new Users();
            user.setUsername("Joshi");
            user.setPassword(passwordEncoder.encode("j@123")); // store plain for now; in real life, hash it
            user.setRoles(Set.of(customerRole));
            usersRepository.save(user);

            System.out.println("Customer role and user added to database!");
        };
    }
    */

}
