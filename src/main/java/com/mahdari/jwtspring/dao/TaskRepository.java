package com.mahdari.jwtspring.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mahdari.jwtspring.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

}
