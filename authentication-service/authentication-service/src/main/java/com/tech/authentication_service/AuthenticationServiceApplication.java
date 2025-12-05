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
        	// Create roles
        	Role customerRole = new Role();
        	customerRole.setName("CUSTOMER");
        	roleRepository.save(customerRole);

        	Role catalogManagerRole = new Role();
        	catalogManagerRole.setName("CATALOG_MANAGER");
        	roleRepository.save(catalogManagerRole);

        	Role shippingStaffRole = new Role();
        	shippingStaffRole.setName("SHIPPING_STAFF");
        	roleRepository.save(shippingStaffRole);

        	Role inventoryManagerRole = new Role();
        	inventoryManagerRole.setName("INVENTORY_MANAGER");
        	roleRepository.save(inventoryManagerRole);

        	Users taylor = new Users();
        	taylor.setUsername("Taylor");
        	taylor.setEmail("taylor@gmail.com");
        	taylor.setPassword(passwordEncoder.encode("t@123"));
        	taylor.setRoles(Set.of(customerRole));
        	usersRepository.save(taylor);

        	Users henry = new Users();
        	henry.setUsername("Henry");
        	henry.setEmail("henry@gmail.com");
        	henry.setPassword(passwordEncoder.encode("h@123"));
        	henry.setRoles(Set.of(catalogManagerRole));
        	usersRepository.save(henry);

        	Users alex = new Users();
        	alex.setUsername("Alex");
        	alex.setEmail("alex@gmail.com");
        	alex.setPassword(passwordEncoder.encode("a@123"));
        	alex.setRoles(Set.of(shippingStaffRole));
        	usersRepository.save(alex);

        	Users nick = new Users();
        	nick.setUsername("Nick");
        	nick.setEmail("nick@gmail.com");
        	nick.setPassword(passwordEncoder.encode("n@123"));
        	nick.setRoles(Set.of(inventoryManagerRole));
        	usersRepository.save(nick);

            System.out.println("Customer role and user added to database!");
        };
    }
    
*/
}
