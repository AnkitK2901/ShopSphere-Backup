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

    public User registerUser(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Error: Username cannot be empty!");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Error: Password cannot be empty!");
        }
        if (user.getPassword().length() < 6) {
            throw new IllegalArgumentException("Error: Password must be at least 6 characters!");
        }

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

        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("ROLE_CUSTOMER"); // Standardized to your app logic
        } else if (!user.getRole().startsWith("ROLE_")) {
            user.setRole("ROLE_" + user.getRole().toUpperCase());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public String loginUser(LoginRequest loginRequest) {
        if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
            throw new InvalidCredentialsException("Error: Username/Email cannot be empty!");
        }
        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            throw new InvalidCredentialsException("Error: Password cannot be empty!");
        }

        // FIX ADDED: Try to find the user by Username first...
        User existingUser = userRepository.findByUsername(loginRequest.getUsername());
        
        // FIX ADDED: ...If it fails, try to find them by Email!
        if (existingUser == null) {
            existingUser = userRepository.findByEmail(loginRequest.getUsername());
        }

        // If it is STILL null after checking both, the user doesn't exist.
        if (existingUser == null || !passwordEncoder.matches(loginRequest.getPassword(), existingUser.getPassword())) {
            throw new InvalidCredentialsException("Invalid user credentials");
        }

        return jwtUtil.generateToken(existingUser.getUsername(), existingUser.getRole());
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User getUserByUsername(String userName) {
        // First try searching exactly by username
        User user = userRepository.findByUsername(userName);
        
        // If not found, use the email fallback (crucial for JWTs that store email)
        if (user == null) {
            user = userRepository.findByEmail(userName);
        }
        
        // If still null, the user legitimately does not exist
        if (user == null) {
            throw new RuntimeException("Username or Email not Found: " + userName);
        }
        
        return user;
    }

    public User updateUserById(Long id, User userDetails) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User ID not found " + id));
        existingUser.setName(userDetails.getName());
        existingUser.setAddress(userDetails.getAddress());
        existingUser.setGender(userDetails.getGender());

        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
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