package com.mahdari.jwtspring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mahdari.jwtspring.dao.ApplicationRoleRepository;
import com.mahdari.jwtspring.dao.ApplicationUserRepository;
import com.mahdari.jwtspring.entity.ApplicationRole;
import com.mahdari.jwtspring.entity.ApplicationUser;

@Service
@Transactional
public class AccountService implements IAccountService {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private ApplicationUserRepository userRepository;
	
	@Autowired
	private ApplicationRoleRepository roleRepository;
	
	@Override
	public ApplicationUser saveUser(ApplicationUser user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	@Override
	public ApplicationRole saveRole(ApplicationRole role) {
		return roleRepository.save(role);
	}

	@Override
	public void addRoleToUser(String username, String roleName) {
		ApplicationUser user = userRepository.findByUsername(username);
		ApplicationRole role = roleRepository.findByName(roleName);
		user.getRoles().add(role);
	}

	@Override
	public ApplicationUser findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

}
