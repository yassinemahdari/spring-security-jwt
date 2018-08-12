package com.mahdari.jwtspring.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahdari.jwtspring.entity.Task;
import com.mahdari.jwtspring.service.ITaskService;

@RestController
public class TaskController {

	@Autowired
	private ITaskService taskService;
	
	@GetMapping(value="/find-all-task")
	public List<Task> findAll() {
		return taskService.findAll();
	}
	
}
