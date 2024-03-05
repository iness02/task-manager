package com.example.taskmanager.service;

import com.example.taskmanager.model.User;
import com.example.taskmanager.repositoryimpl.UserRepositoryImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepositoryImplementation userRepository;

    @Autowired
    public UserService(UserRepositoryImplementation userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public long getHighestUserId() {
        long highestId = 0;
        List<User> users = getAllUsers(); // Assuming userService has a method to retrieve all users
        for (User user : users) {
            if (user.getId() > highestId) {
                highestId = user.getId();
            }
        }
        return highestId;
    }
}
