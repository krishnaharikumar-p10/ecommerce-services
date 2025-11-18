package com.tech.authentication_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tech.authentication_service.model.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token,Long> {

	Token findByUserId(int id);

	void deleteByUserId(int userId);

}
