package com.example.taskmanager.controller;

import com.example.taskmanager.jwt.JwtRequest;
import com.example.taskmanager.jwt.JwtResponse;
import com.example.taskmanager.jwt.JwtTokUtil;
import com.example.taskmanager.jwt.JwtUserDetailService;
import com.example.taskmanager.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    private final JwtTokUtil jwtTokUtil;
    private final JwtUserDetailService userDetailsService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtTokUtil jwtTokUtil,
                                       JwtUserDetailService userDetailsService, AuthenticationService authenticationService) {
        this.jwtTokUtil = jwtTokUtil;
        this.userDetailsService = userDetailsService;
        this.authenticationService = authenticationService;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest)  {
         try {
             authenticationService.authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
             final UserDetails userDetails = userDetailsService
                     .loadUserByUsername(authenticationRequest.getUsername());
             System.out.println(userDetails);
             final String token = jwtTokUtil.generateToken(userDetails.getUsername());

             return ResponseEntity.ok(new JwtResponse(token));
         } catch (Exception e) {
             return ResponseEntity.badRequest().body("Invalid login or password");
         }


    }
}