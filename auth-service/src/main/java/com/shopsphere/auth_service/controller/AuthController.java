package com.shopsphere.auth_service.controller;

import com.shopsphere.auth_service.dto.AuthResponse;
import com.shopsphere.auth_service.dto.LoginRequest;
import com.shopsphere.auth_service.model.User;
import com.shopsphere.auth_service.service.AuthService;
// import org.apache.coyote.Response;
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
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        // The service does all the work. If an error happens, the GlobalExceptionHandler catches it!
        authService.registerUser(user);
        return ResponseEntity.ok("Registration Successful");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        // Get the token string from the service
        String token = authService.loginUser(loginRequest);

        // Wrap it in our DTO so it looks like { "token": "eyJhbG..." } in Postman
        return ResponseEntity.ok(new AuthResponse(token));
    }


}