package com.shopsphere.auth_service.controller;

import com.shopsphere.auth_service.dto.AuthResponse;
import com.shopsphere.auth_service.dto.LoginRequest;
import com.shopsphere.auth_service.dto.RegisterResponse;
import com.shopsphere.auth_service.model.User;
import com.shopsphere.auth_service.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody User user) {
        User savedUser = authService.registerUser(user);
        RegisterResponse response = new RegisterResponse(
                "Registration Successful! Welcome to ShopSphere, " + savedUser.getUsername() + "!",
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getName()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        String token = authService.loginUser(loginRequest);
        User user = authService.getUserByUsername(loginRequest.getUsername());
        return ResponseEntity.ok(new AuthResponse(token, user.getId()));
    }
}