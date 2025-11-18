package com.tech.authentication_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tech.authentication_service.model.Role;



@Repository
public interface RoleRepository extends JpaRepository<Role,Integer> {

}

