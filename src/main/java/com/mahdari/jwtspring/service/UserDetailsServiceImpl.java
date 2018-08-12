package com.mahdari.jwtspring.service;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mahdari.jwtspring.entity.ApplicationUser;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private AccountService accountService;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		ApplicationUser user = accountService.findByUsername(username);
		if(user==null)
			throw new UsernameNotFoundException(username);
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		user.getRoles().forEach(role->{
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		});
		return new User(user.getUsername(), user.getPassword(), authorities);
	}

}
