package com.mahdari.jwtspring.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahdari.jwtspring.dao.TaskRepository;
import com.mahdari.jwtspring.entity.Task;

@Service
public class TaskService implements ITaskService {

	@Autowired
	private TaskRepository taskRepository;
	
	@Override
	public Task save(Task task) {
		return taskRepository.save(task);
	}

	@Override
	public void delete(Long id) {
		taskRepository.deleteById(id);
	}

	@Override
	public List<Task> findAll() {
		return taskRepository.findAll();
	}

}
