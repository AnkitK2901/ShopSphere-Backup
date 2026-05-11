package com.shopsphere.auth_service.controller;

import com.shopsphere.auth_service.model.User;
import com.shopsphere.auth_service.repository.UserRepository;
import com.shopsphere.auth_service.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/profile")
public class UserController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil; // FIX: We inject JwtUtil to decode the token manually

    public UserController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(@RequestHeader("Authorization") String authHeader) {
        // FIX: Extract the username directly from the raw Bearer token
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setPassword(null); // Protect the password hash
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/update")
    public ResponseEntity<User> updateMyProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody User updatedDetails) {
            
        // FIX: Extract the username directly from the raw Bearer token
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setName(updatedDetails.getName());
            user.setAddress(updatedDetails.getAddress());
            user.setGender(updatedDetails.getGender());
            
            User saved = userRepository.save(user);
            saved.setPassword(null); 
            return ResponseEntity.ok(saved);
        }
        return ResponseEntity.notFound().build();
    }
}