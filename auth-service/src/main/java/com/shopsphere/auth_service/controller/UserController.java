package com.shopsphere.auth_service.controller;

import com.shopsphere.auth_service.model.User;
import com.shopsphere.auth_service.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AuthService authService;

    public UserController (AuthService authService){
        this.authService = authService;
    }

    @GetMapping("/{userName}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String userName){
        User user = authService.getUserByUsername(userName);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUserById(@PathVariable Long id, @RequestBody User userDetails){
        User user = authService.updateUserById(id, userDetails);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        authService.deleteUser(id);
        return ResponseEntity.ok("User Deleted Successfully");
    }
}
