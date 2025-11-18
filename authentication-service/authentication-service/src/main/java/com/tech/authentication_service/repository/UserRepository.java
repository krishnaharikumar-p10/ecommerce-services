package com.tech.authentication_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tech.authentication_service.model.Users;

@Repository
public interface UserRepository  extends JpaRepository<Users,Integer > {

	Users findByUsername(String username);

}
