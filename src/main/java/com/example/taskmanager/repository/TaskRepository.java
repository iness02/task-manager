package com.example.taskmanager.repository;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;

import java.util.List;

public interface TaskRepository {
    List<Task> findAll();

    Task findById(long id);

    Task findByName(String name);

    List<Task> findTaskByUser(User user);

    void save(Task task);

    void deleteById(Long id);

    void update(Task task);

    List<Task> findByStatus(String status);
}
