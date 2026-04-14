package com.shopsphere.auth_service.controller;

import com.shopsphere.auth_service.model.User;
import com.shopsphere.auth_service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User & GDPR Management", description = "APIs for user profiles and GDPR compliance actions")
public class UserController {

    private final AuthService authService;

    @Operation(summary = "Get user profile details")
    @GetMapping("/{userName}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String userName){
        log.info("REST request to get user: {}", userName);
        User user = authService.getUserByUsername(userName);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Update user profile")
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUserById(@PathVariable Long id, @RequestBody User userDetails){
        log.info("REST request to update user ID: {}", id);
        User user = authService.updateUserById(id, userDetails);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "GDPR 'Right to be Forgotten' - Delete Account")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        log.info("REST request for GDPR deletion of user ID: {}", id);
        authService.deleteUser(id);
        return ResponseEntity.ok("GDPR: User data successfully purged from the system.");
    }
}