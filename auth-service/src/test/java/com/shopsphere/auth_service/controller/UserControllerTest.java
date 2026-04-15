package com.shopsphere.auth_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.auth_service.config.SecurityConfig;
import com.shopsphere.auth_service.exception.GlobalExceptionHandler;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class UserControllerTest {

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

    @BeforeEach
    void setUp() throws Exception {
        doAnswer(invocation -> {
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(jwtFilter).doFilter(any(), any(), any());

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setUsername("johndoe");
        testUser.setEmail("john@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setAddress("123 Main St");
        testUser.setGender("Male");
    }

    // ======================== GET /api/users/{userName} ========================

    @Test
    @WithMockUser
    void getUserByUsername_existingUser_returns200WithUser() throws Exception {
        when(authService.getUserByUsername("johndoe")).thenReturn(testUser);

        mockMvc.perform(get("/api/users/johndoe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @WithMockUser
    void getUserByUsername_nonExistingUser_returns500() throws Exception {
        when(authService.getUserByUsername("unknown"))
                .thenThrow(new RuntimeException("Username not Found"));

        mockMvc.perform(get("/api/users/unknown"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getUserByUsername_unauthenticated_returns401Or403() throws Exception {
        doAnswer(invocation -> {
            jakarta.servlet.http.HttpServletResponse response = invocation.getArgument(1);
            response.setStatus(403);
            return null;
        }).when(jwtFilter).doFilter(any(), any(), any());

        mockMvc.perform(get("/api/users/johndoe"))
                .andExpect(status().is4xxClientError());
    }

    // ======================== PUT /api/users/{id} ========================

    @Test
    @WithMockUser
    void updateUserById_validRequest_returns200WithUpdatedUser() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Jane Doe");
        updatedUser.setUsername("johndoe");
        updatedUser.setEmail("john@example.com");
        updatedUser.setAddress("456 Oak Ave");
        updatedUser.setGender("Female");

        when(authService.updateUserById(eq(1L), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.address").value("456 Oak Ave"))
                .andExpect(jsonPath("$.gender").value("Female"));
    }

    @Test
    @WithMockUser
    void updateUserById_nonExistingUser_returns500() throws Exception {
        when(authService.updateUserById(eq(99L), any(User.class)))
                .thenThrow(new RuntimeException("User ID not found 99"));

        mockMvc.perform(put("/api/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateUserById_unauthenticated_returns401Or403() throws Exception {
        doAnswer(invocation -> {
            jakarta.servlet.http.HttpServletResponse response = invocation.getArgument(1);
            response.setStatus(403);
            return null;
        }).when(jwtFilter).doFilter(any(), any(), any());

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().is4xxClientError());
    }

    // ======================== DELETE /api/users/{id} ========================

    @Test
    @WithMockUser
    void deleteUser_existingUser_returns200() throws Exception {
        doNothing().when(authService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("User Deleted Successfully"));

        verify(authService).deleteUser(1L);
    }

    @Test
    @WithMockUser
    void deleteUser_nonExistingUser_returns500() throws Exception {
        doThrow(new RuntimeException("Cannot Delete. User not found"))
                .when(authService).deleteUser(99L);

        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteUser_unauthenticated_returns401Or403() throws Exception {
        doAnswer(invocation -> {
            jakarta.servlet.http.HttpServletResponse response = invocation.getArgument(1);
            response.setStatus(403);
            return null;
        }).when(jwtFilter).doFilter(any(), any(), any());

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().is4xxClientError());
    }
}