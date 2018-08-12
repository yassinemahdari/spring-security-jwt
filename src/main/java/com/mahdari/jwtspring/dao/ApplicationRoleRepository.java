package com.mahdari.jwtspring.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mahdari.jwtspring.entity.ApplicationRole;

public interface ApplicationRoleRepository extends JpaRepository<ApplicationRole, Long> {

	public ApplicationRole findByName(String name);
	
}
