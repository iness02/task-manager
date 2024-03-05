package com.example.taskmanager.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    /* private final UserService userService;
     private final PasswordEncoder passwordEncoder;

     @Autowired
     public AuthenticationService(UserService userService, PasswordEncoder passwordEncoder) {
         this.userService = userService;
         this.passwordEncoder = passwordEncoder;
     }

     public boolean authenticateUser(AuthenticationRequest loginRequest) {
         // Retrieve the user by email
         User user = userService.getUserByEmail(loginRequest.getEmail());
         if (user == null) {
             // User with the provided email does not exist
             return false;
         }

         // Check if the provided password matches the stored password
         return passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());

     }

     //TODO we can add jwt token*/
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void authenticate(String username, String password) throws Exception {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("User has been disabled", e);
        } catch (BadCredentialsException e) {
            throw new Exception("Invalid credentials", e);
        }
    }
}


