package com.shopsphere.auth_service.service;

import com.shopsphere.auth_service.dto.LoginRequest;
import com.shopsphere.auth_service.exception.InvalidCredentialsException;
import com.shopsphere.auth_service.exception.InvalidEmailFormatException;
import com.shopsphere.auth_service.exception.UserAlreadyExistsException;
import com.shopsphere.auth_service.model.User;
import com.shopsphere.auth_service.repository.UserRepository;
import com.shopsphere.auth_service.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public void registerUser(User user) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (user.getEmail() == null || !user.getEmail().matches(emailRegex)) {
            throw new InvalidEmailFormatException("Error: Please provide a valid email address!");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Error: Email is already registered!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public String loginUser(LoginRequest loginRequest) {
        User existingUser = userRepository.findByUsername(loginRequest.getUsername());
        if (existingUser == null || !passwordEncoder.matches(loginRequest.getPassword(), existingUser.getPassword())) {
            throw new InvalidCredentialsException("Invalid user credentials");
        }
        return jwtUtil.generateToken(loginRequest.getUsername());
    }

    // NEW — get user by ID (used by analytics-service via Feign)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User getUserByUsername(String userName) {
        User user = userRepository.findByUsername(userName);
        if (user == null) {
            throw new RuntimeException("Username not Found");
        }
        return user;
    }

    public User updateUserById(Long id, User userDetails) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User ID not found " + id));
        existingUser.setName(userDetails.getName());
        existingUser.setAddress(userDetails.getAddress());
        existingUser.setGender(userDetails.getGender());
        if (userDetails.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Cannot Delete. User not found");
        }
        userRepository.deleteById(id);
    }
}