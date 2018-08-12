package com.mahdari.jwtspring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.mahdari.jwtspring.entity.ApplicationRole;
import com.mahdari.jwtspring.entity.ApplicationUser;
import com.mahdari.jwtspring.entity.Task;
import com.mahdari.jwtspring.service.AccountService;
import com.mahdari.jwtspring.service.ITaskService;

@SpringBootApplication
public class SpringSecurityJwtApplication implements CommandLineRunner {

	@Autowired
	private ITaskService taskService;
	
	@Autowired
	private AccountService accountService;
	
	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityJwtApplication.class, args);
	}
	
	@Bean
	public BCryptPasswordEncoder getBPE() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void run(String... args) throws Exception {
		accountService.saveUser(new ApplicationUser(null, "admin", "admin", null));
		accountService.saveUser(new ApplicationUser(null, "user", "user", null));
		accountService.saveRole(new ApplicationRole(null, "ADMIN"));
		accountService.saveRole(new ApplicationRole(null, "USER"));
		accountService.addRoleToUser("admin", "ADMIN");
		accountService.addRoleToUser("admin", "USER");
		accountService.addRoleToUser("user", "USER");
		taskService.save(new Task(null, "T1"));
		taskService.save(new Task(null, "T2"));
		taskService.save(new Task(null, "T3"));
	}
}
