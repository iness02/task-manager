package com.example.taskmanager.repository;

import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.Roles;

import java.util.List;

public interface RoleRepository {
    Role findById(Integer id);

    Role findByName(Roles name);

    List<Role> findAll();

    void save(Role role);
}
