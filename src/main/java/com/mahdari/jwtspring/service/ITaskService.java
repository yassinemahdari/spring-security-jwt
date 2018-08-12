package com.mahdari.jwtspring.service;

import java.util.List;

import com.mahdari.jwtspring.entity.Task;

public interface ITaskService {

	public Task save(Task task);
	public void delete(Long id);
	public List<Task> findAll();
	
}
