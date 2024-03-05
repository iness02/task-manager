package com.example.taskmanager.service;

import com.example.taskmanager.model.Role;
import com.example.taskmanager.repositoryimpl.RoleRepositoryImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    private final RoleRepositoryImplementation roleRepository;

    @Autowired
    public RoleService(RoleRepositoryImplementation roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public void saveRole(Role role) {
        List<Role> roles = getAllRoles();
        roles.add(role);
        for (Role r : roles)
            roleRepository.save(r);
    }
}
