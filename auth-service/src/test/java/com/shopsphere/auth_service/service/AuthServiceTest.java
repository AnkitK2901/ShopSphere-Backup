package com.shopsphere.auth_service.service;

import com.shopsphere.auth_service.dto.LoginRequest;
import com.shopsphere.auth_service.exception.InvalidCredentialsException;
import com.shopsphere.auth_service.exception.InvalidEmailFormatException;
import com.shopsphere.auth_service.exception.UserAlreadyExistsException;
import com.shopsphere.auth_service.model.User;
import com.shopsphere.auth_service.repository.UserRepository;
import com.shopsphere.auth_service.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setUsername("johndoe");
        testUser.setEmail("john@example.com");
        testUser.setPassword("password123");
        testUser.setAddress("123 Main St");
        testUser.setGender("Male");
        testUser.setRole("ROLE_BUYER"); // Added role for testing

        loginRequest = new LoginRequest();
        loginRequest.setUsername("johndoe");
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("password123");
    }

    // ======================== registerUser Tests ========================

    @Test
    void registerUser_validUser_savesSuccessfully() {
        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        authService.registerUser(testUser);

        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(testUser);
        assertEquals("encodedPassword", testUser.getPassword());
        assertEquals("ROLE_BUYER", testUser.getRole()); // Verify default role logic
    }

    @Test
    void registerUser_nullEmail_throwsInvalidEmailFormatException() {
        testUser.setEmail(null);

        InvalidEmailFormatException ex = assertThrows(InvalidEmailFormatException.class,
                () -> authService.registerUser(testUser));

        assertEquals("Error: Please provide a valid email address!", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_invalidEmailFormat_throwsInvalidEmailFormatException() {
        testUser.setEmail("not-an-email");

        InvalidEmailFormatException ex = assertThrows(InvalidEmailFormatException.class,
                () -> authService.registerUser(testUser));

        assertEquals("Error: Please provide a valid email address!", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_emailMissingDomain_throwsInvalidEmailFormatException() {
        testUser.setEmail("john@");

        assertThrows(InvalidEmailFormatException.class, () -> authService.registerUser(testUser));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_emailMissingTld_throwsInvalidEmailFormatException() {
        testUser.setEmail("john@example");

        assertThrows(InvalidEmailFormatException.class, () -> authService.registerUser(testUser));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_duplicateUsername_throwsUserAlreadyExistsException() {
        when(userRepository.existsByUsername("johndoe")).thenReturn(true);

        UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class,
                () -> authService.registerUser(testUser));

        assertEquals("Error: Username is already taken!", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_duplicateEmail_throwsUserAlreadyExistsException() {
        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class,
                () -> authService.registerUser(testUser));

        assertEquals("Error: Email is already registered!", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_encodesPasswordBeforeSaving() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encoded");

        authService.registerUser(testUser);

        assertEquals("$2a$10$encoded", testUser.getPassword());
        verify(userRepository).save(testUser);
    }

    // ======================== loginUser Tests ========================

    @Test
    void loginUser_validCredentials_returnsToken() {
        User storedUser = new User();
        storedUser.setUsername("johndoe");
        storedUser.setPassword("encodedPassword");
        storedUser.setRole("ROLE_BUYER"); // Ensure role is set on stored user

        when(userRepository.findByUsername("johndoe")).thenReturn(storedUser);
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        // FIX: Expecting two parameters for generateToken
        when(jwtUtil.generateToken("johndoe", "ROLE_BUYER")).thenReturn("jwt-token-123");

        String token = authService.loginUser(loginRequest);

        assertEquals("jwt-token-123", token);
        verify(jwtUtil).generateToken("johndoe", "ROLE_BUYER");
    }

    @Test
    void loginUser_userNotFound_throwsInvalidCredentialsException() {
        when(userRepository.findByUsername("johndoe")).thenReturn(null);

        InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class,
                () -> authService.loginUser(loginRequest));

        assertEquals("Invalid user credentials", ex.getMessage());
        // FIX: Expecting two parameters for the never() check
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    void loginUser_wrongPassword_throwsInvalidCredentialsException() {
        User storedUser = new User();
        storedUser.setUsername("johndoe");
        storedUser.setPassword("encodedPassword");

        when(userRepository.findByUsername("johndoe")).thenReturn(storedUser);
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(false);

        InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class,
                () -> authService.loginUser(loginRequest));

        assertEquals("Invalid user credentials", ex.getMessage());
        // FIX: Expecting two parameters for the never() check
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    // ======================== getUserByUsername Tests ========================

    @Test
    void getUserByUsername_existingUser_returnsUser() {
        when(userRepository.findByUsername("johndoe")).thenReturn(testUser);

        User result = authService.getUserByUsername("johndoe");

        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
        assertEquals("John Doe", result.getName());
    }

    @Test
    void getUserByUsername_nonExistingUser_throwsRuntimeException() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.getUserByUsername("unknown"));

        assertEquals("Username not Found", ex.getMessage());
    }

    // ======================== updateUserById Tests ========================

    @Test
    void updateUserById_existingUser_updatesFieldsSuccessfully() {
        User updatedDetails = new User();
        updatedDetails.setName("Jane Doe");
        updatedDetails.setAddress("456 Oak Ave");
        updatedDetails.setGender("Female");
        updatedDetails.setPassword(null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = authService.updateUserById(1L, updatedDetails);

        assertEquals("Jane Doe", result.getName());
        assertEquals("456 Oak Ave", result.getAddress());
        assertEquals("Female", result.getGender());
        // Password should remain unchanged when null is passed
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void updateUserById_withNewPassword_encodesAndUpdatesPassword() {
        User updatedDetails = new User();
        updatedDetails.setName("Jane Doe");
        updatedDetails.setAddress("456 Oak Ave");
        updatedDetails.setGender("Female");
        updatedDetails.setPassword("newPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = authService.updateUserById(1L, updatedDetails);

        assertEquals("encodedNewPassword", result.getPassword());
        verify(passwordEncoder).encode("newPassword");
    }

    @Test
    void updateUserById_nonExistingUser_throwsRuntimeException() {
        User updatedDetails = new User();
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.updateUserById(99L, updatedDetails));

        assertTrue(ex.getMessage().contains("User ID not found"));
        verify(userRepository, never()).save(any());
    }

    // ======================== deleteUser Tests ========================

    @Test
    void deleteUser_existingUser_deletesSuccessfully() {
        when(userRepository.existsById(1L)).thenReturn(true);

        authService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_nonExistingUser_throwsRuntimeException() {
        when(userRepository.existsById(99L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.deleteUser(99L));

        assertEquals("Cannot Delete. User not found", ex.getMessage());
        verify(userRepository, never()).deleteById(anyLong());
    }
}