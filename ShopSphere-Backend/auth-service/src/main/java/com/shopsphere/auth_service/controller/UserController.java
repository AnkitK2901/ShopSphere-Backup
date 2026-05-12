package com.shopsphere.auth_service.controller;

import com.shopsphere.auth_service.model.User;
import com.shopsphere.auth_service.repository.UserRepository;
import com.shopsphere.auth_service.service.AuthService;
import com.shopsphere.auth_service.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
// THE FIX: Removed the class-level RequestMapping so we can serve multiple base paths!
public class UserController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil; 
    private final AuthService authService;

    // Injected AuthService so we can use its smart "Email OR Username" search logic
    public UserController(UserRepository userRepository, JwtUtil jwtUtil, AuthService authService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    // ==========================================
    // 1. FRONTEND PROFILE ENDPOINTS
    // ==========================================
    @GetMapping("/api/auth/profile/me")
    public ResponseEntity<User> getMyProfile(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        
        // Smart fallback logic
        User user = userRepository.findByUsername(username);
        if (user == null) {
            user = userRepository.findByEmail(username);
        }

        if (user != null) {
            user.setPassword(null); // Protect the password hash
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/api/auth/profile/update")
    public ResponseEntity<User> updateMyProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody User updatedDetails) {
            
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        
        User user = userRepository.findByUsername(username);
        if (user == null) {
            user = userRepository.findByEmail(username);
        }

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

    // ==========================================
    // 2. BACKEND FEIGN CLIENT ENDPOINT (THE FIX!)
    // ==========================================
    @GetMapping("/api/users/{userName}")
    public ResponseEntity<User> getUserByUserName(@PathVariable String userName) {
        // The Order Service uses this to securely fetch the Customer ID via Feign!
        User user = authService.getUserByUsername(userName);
        if (user != null) {
            user.setPassword(null); // Never send the hash over the network!
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }
}