package com.mahdari.jwtspring.service;

import com.mahdari.jwtspring.entity.ApplicationRole;
import com.mahdari.jwtspring.entity.ApplicationUser;

public interface IAccountService {

	public ApplicationUser saveUser(ApplicationUser user);
	public ApplicationRole saveRole(ApplicationRole role);
	public void addRoleToUser(String username, String roleName);
	public ApplicationUser findByUsername(String username);
	
}
