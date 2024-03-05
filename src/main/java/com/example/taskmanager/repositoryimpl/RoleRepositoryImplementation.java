package com.example.taskmanager.repositoryimpl;

import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.Roles;
import com.example.taskmanager.repository.RoleRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RoleRepositoryImplementation implements RoleRepository {
    private final String FILE_PATH = "C:\\Users\\User\\Downloads\\task-manager\\task-manager\\src\\main\\resources\\role.json";
    private final File jsonFile = new File(FILE_PATH);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Role findById(Integer id) {
        List<Role> roles = loadRolesFromFile();
        for (Role role : roles) {
            if (role.getId().equals(id)) {
                return role;
            }
        }
        return null; // Role not found
    }

    @Override
    public Role findByName(Roles name) {
        List<Role> roles = loadRolesFromFile();
        for (Role role : roles) {
            if (role.getRole().equals(name)) {
                return role;
            }
        }
        return null; // Role not found
    }

    @Override
    public List<Role> findAll() {
        return loadRolesFromFile();
    }

    @Override
    public void save(Role role) {
        List<Role> roles = loadRolesFromFile();
        roles.add(role);
        saveRolesToFile(roles);
    }

    private List<Role> loadRolesFromFile() {
        try {
            // Check if the file exists and is not empty
            if (jsonFile.exists() && jsonFile.length() > 0) {
                return objectMapper.readValue(jsonFile, new TypeReference<List<Role>>() {
                });
            } else {
                return new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveRolesToFile(List<Role> roles) {
        try {
            objectMapper.writeValue(jsonFile, roles);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
