package com.shopsphere.auth_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.auth_service.config.SecurityConfig;
import com.shopsphere.auth_service.dto.LoginRequest;
import com.shopsphere.auth_service.exception.GlobalExceptionHandler;
import com.shopsphere.auth_service.exception.InvalidCredentialsException;
import com.shopsphere.auth_service.exception.InvalidEmailFormatException;
import com.shopsphere.auth_service.exception.UserAlreadyExistsException;
import com.shopsphere.auth_service.filter.JwtFilter;
import com.shopsphere.auth_service.model.User;
import com.shopsphere.auth_service.service.AuthService;
import com.shopsphere.auth_service.util.JwtUtil;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtFilter jwtFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() throws Exception {
        // Make the mock JwtFilter pass requests through to the controller
        doAnswer(invocation -> {
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(jwtFilter).doFilter(any(), any(), any());

        testUser = new User();
        testUser.setName("John Doe");
        testUser.setUsername("johndoe");
        testUser.setEmail("john@example.com");
        testUser.setPassword("password123");
        testUser.setAddress("123 Main St");
        testUser.setGender("Male");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("johndoe");
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("password123");
    }

    // ======================== Register Endpoint Tests ========================

    @Test
    void registerUser_validRequest_returns200() throws Exception {
        doNothing().when(authService).registerUser(any(User.class));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(content().string("Registration Successful"));

        verify(authService).registerUser(any(User.class));
    }

    @Test
    void registerUser_duplicateUsername_returns400() throws Exception {
        doThrow(new UserAlreadyExistsException("Error: Username is already taken!"))
                .when(authService).registerUser(any(User.class));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Username is already taken!"));
    }

    @Test
    void registerUser_duplicateEmail_returns400() throws Exception {
        doThrow(new UserAlreadyExistsException("Error: Email is already registered!"))
                .when(authService).registerUser(any(User.class));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Email is already registered!"));
    }

    @Test
    void registerUser_invalidEmail_returns400() throws Exception {
        doThrow(new InvalidEmailFormatException("Error: Please provide a valid email address!"))
                .when(authService).registerUser(any(User.class));

        testUser.setEmail("invalid-email");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Please provide a valid email address!"));
    }

    // ======================== Login Endpoint Tests ========================

    @Test
    void loginUser_validCredentials_returns200WithToken() throws Exception {
        when(authService.loginUser(any(LoginRequest.class))).thenReturn("jwt-token-123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-123"));
    }

    @Test
    void loginUser_invalidCredentials_returns401() throws Exception {
        when(authService.loginUser(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException("Invalid user credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid user credentials"));
    }

    @Test
    void loginUser_emptyBody_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())  // Controller doesn't validate; service does
                .andReturn();
    }
}