package com.example.taskmanager.repository;

import com.example.taskmanager.model.User;

import java.util.List;
import java.util.Optional;


public interface UserRepository {
    User findByEmail(String email);

    List<User> findAll();

    void save(User user);

    void update(User user);

    Optional<User> findByID(Long id);

    List<Long> getTaskIds(String email);

    List<User> findByTaskId(Long taskId);
}
