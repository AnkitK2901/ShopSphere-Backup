package com.shopsphere.auth_service.repository;

import com.shopsphere.auth_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    
    // FIX ADDED: This teaches the database how to find a user by their email
    User findByEmail(String email); 
    
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}