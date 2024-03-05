package com.example.taskmanager.repositoryimpl;

import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImplementation implements UserRepository {

    private String FILE_PATH = "C:\\Users\\User\\Downloads\\task-manager\\task-manager\\src\\main\\resources\\users.json";
    private final File jsonFile = new File(FILE_PATH);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public User findByEmail(String email) {
        List<User> users = loadUsersFromFile();
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null; // User not found
    }

    @Override
    public List<User> findAll() {
        return loadUsersFromFile();
    }

    @Override
    public void save(User user) {
        List<User> users = loadUsersFromFile();
        users.add(user);
        saveUsersToFile(users);
    }

    @Override
    public void update(User user) {
        List<User> users = loadUsersFromFile();
        for (int i = 0; i < users.size(); i++) {
            User existingUser = users.get(i);
            if (existingUser.getId().equals(user.getId())) {
                existingUser.setEmail(user.getEmail());
                existingUser.setFirstName(user.getFirstName());
                existingUser.setLastName(user.getLastName());
                existingUser.setPassword(user.getPassword());
                existingUser.setPhoto(user.getPhoto());
                existingUser.setRole(user.getRole());
                existingUser.setTaskIds(user.getTaskIds());

            }
        }
        saveUsersToFile(users);
    }

    @Override
    public Optional<User> findByID(Long id) {
        List<User> users = loadUsersFromFile();
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Long> getTaskIds(String email) {
        User user = findByEmail(email);
        if (user != null) {
            return user.getTaskIds();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<User> findByTaskId(Long taskId) {
        List<User> usersWithTask = new ArrayList<>();
        for (User user : loadUsersFromFile()) {
            if (user.getTaskIds().contains(taskId)) {
                usersWithTask.add(user);
            }
        }
        return usersWithTask;
    }

    private List<User> loadUsersFromFile() {
        try {
            // Check if the file exists and is not empty
            if (jsonFile.exists() && jsonFile.length() > 0) {
                return objectMapper.readValue(jsonFile, new TypeReference<List<User>>() {
                });
            } else {
                return new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveUsersToFile(List<User> users) {
        try {
            objectMapper.writeValue(jsonFile, users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
