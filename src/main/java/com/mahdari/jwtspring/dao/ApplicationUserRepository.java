package com.mahdari.jwtspring.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mahdari.jwtspring.entity.ApplicationUser;

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {

	public ApplicationUser findByUsername(String username);
	
}
