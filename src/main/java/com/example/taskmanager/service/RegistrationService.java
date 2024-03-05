package com.example.taskmanager.service;

import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.Roles;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repositoryimpl.RoleRepositoryImplementation;
import com.example.taskmanager.requests.RegisterRequest;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class RegistrationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepositoryImplementation roleRepository;
    private static long ID = 1;

    // Constructor injection
    public RegistrationService(UserService userService, PasswordEncoder passwordEncoder, RoleRepositoryImplementation repositoryImplementation, RoleRepositoryImplementation roleRepository) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public ResponseEntity<String> registerUser(RegisterRequest registerRequest) {
        // Check if user already exists
        ResponseEntity<String> passwordCheckResult = isPasswordStrong(registerRequest.getPassword());
        if (userService.getUserByEmail(registerRequest.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already in use");
        } else if (registerRequest.getFirstName().isEmpty() || registerRequest.getLastName().isEmpty() ||
                registerRequest.getEmail().isEmpty() || registerRequest.getPassword().isEmpty() ||
                registerRequest.getConfirmPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("All fields are required");
        } else if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Passwords do not match");
        } else if (passwordCheckResult.getStatusCode() != HttpStatus.OK) {
            return passwordCheckResult;
        } else if (!isValidEmailAddress(registerRequest.getEmail())) {
            return new ResponseEntity<>("The email address you provided doesn't exist", HttpStatus.BAD_REQUEST);
        }

        Long nextUserId = userService.getHighestUserId() + 1;

        // Register the user
        User user = new User();
        user.setId(nextUserId);
        Role role = roleRepository.findByName(Roles.USER);
        user.setRole(role);
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userService.saveUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }


    public ResponseEntity<String> isPasswordStrong(String password) {
        // Password must be at least 8 characters long
        if (password.length() < 8) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password must be at least 8 characters long.");
        }
        // Check for presence of uppercase letters
        else if (!Pattern.compile("[A-Z]").matcher(password).find()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password must contain at least one uppercase letter.");
        }
        // Check for presence of lowercase letters
        else if (!Pattern.compile("[a-z]").matcher(password).find()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password must contain at least one lowercase letter.");
        }
        // Check for presence of numbers
        else if (!Pattern.compile("[0-9]").matcher(password).find()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password must contain at least one number.");
        }
        // Check for presence of symbols
        else if (!Pattern.compile("[^A-Za-z0-9]").matcher(password).find()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password must contain at least one symbol.");
        }

        return ResponseEntity.ok("Password is strong!");
    }

    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

}


