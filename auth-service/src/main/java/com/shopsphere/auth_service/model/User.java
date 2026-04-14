package com.shopsphere.auth_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_full_name")
    private String name;

    @Column(name = "user_name")
    private String username;

    @Column(name = "user_email")
    private String email;

    @Column(name = "user_password")
    private String password;

    @Column(name = "user_address")
    private String address;

    @Column(name = "user_gender")
    private String gender;

    // --- LLD REQUIRED: GDPR Compliance ---
    @Column(name = "data_consent")
    private Boolean dataConsent;
    // -------------------------------------
}