package com.shopsphere.auth_service.service;

import com.shopsphere.auth_service.dto.LoginRequest;
import com.shopsphere.auth_service.exception.InvalidCredentialsException;
import com.shopsphere.auth_service.exception.InvalidEmailFormatException;
import com.shopsphere.auth_service.exception.UserAlreadyExistsException;
import com.shopsphere.auth_service.model.User;
import com.shopsphere.auth_service.repository.UserRepository;
import com.shopsphere.auth_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public void registerUser(User user) {
        log.info("Attempting to register new user: {}", user.getUsername());

        // --- LLD REQUIRED: GDPR Consent Validation ---
        if (user.getDataConsent() == null || !user.getDataConsent()) {
            log.error("Registration failed: GDPR data consent not provided by {}", user.getUsername());
            throw new IllegalArgumentException("GDPR Error: Explicit data processing consent is required to register.");
        }
        // ---------------------------------------------

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
        log.info("Successfully registered user: {}", user.getUsername());
    }

    public String loginUser(LoginRequest loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getUsername());
        User existingUser = userRepository.findByUsername(loginRequest.getUsername());

        if (existingUser == null || !passwordEncoder.matches(loginRequest.getPassword(), existingUser.getPassword())) {
            log.warn("Failed login attempt for user: {}", loginRequest.getUsername());
            throw new InvalidCredentialsException("Invalid user credentials");
        }

        log.info("Successful login for user: {}", loginRequest.getUsername());
        return jwtUtil.generateToken(loginRequest.getUsername());
    }

    public User getUserByUsername(String userName){
        log.info("Fetching user details for: {}", userName);
        User user = userRepository.findByUsername(userName);
        if(user == null){
            throw new RuntimeException("Username not Found");
        }
        return user;
    }

    public User updateUserById(Long id, User userDetails){
        log.info("Updating details for User ID: {}", id);
        User existingUser = userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User ID not found " + id));
        existingUser.setName(userDetails.getName());
        existingUser.setAddress(userDetails.getAddress());
        existingUser.setGender(userDetails.getGender());

        if(userDetails.getPassword() != null){
            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        return userRepository.save(existingUser);
    }

    // --- LLD REQUIRED: GDPR Right to be Forgotten ---
    public void deleteUser(Long id){
        log.info("GDPR Action: Executing Right to be Forgotten for User ID: {}", id);
        if(!userRepository.existsById(id)){
            throw new RuntimeException("Cannot Delete. User not found");
        }
        userRepository.deleteById(id);
        log.info("GDPR Action: Successfully purged User ID: {} from system", id);
    }
}