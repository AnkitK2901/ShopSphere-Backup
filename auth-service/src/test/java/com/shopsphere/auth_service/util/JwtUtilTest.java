package com.shopsphere.auth_service.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    // Injecting the secret directly from application.properties just for the expired token test
    @Value("${jwt.secret}")
    private String secret;

    // ======================== generateToken Tests ========================

    @Test
    void generateToken_validUsername_returnsNonNullToken() {
        String token = jwtUtil.generateToken("testuser", "ROLE_BUYER");

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void generateToken_validUsername_tokenContainsThreeParts() {
        String token = jwtUtil.generateToken("testuser", "ROLE_BUYER");

        // JWT format: header.payload.signature
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);
    }

    @Test
    void generateToken_differentUsernames_returnsDifferentTokens() {
        String token1 = jwtUtil.generateToken("user1", "ROLE_BUYER");
        String token2 = jwtUtil.generateToken("user2", "ROLE_BUYER");

        assertNotEquals(token1, token2);
    }

    // ======================== extractUsername Tests ========================

    @Test
    void extractUsername_validToken_returnsCorrectUsername() {
        String token = jwtUtil.generateToken("johndoe", "ROLE_BUYER");
        String username = jwtUtil.extractUsername(token);

        assertEquals("johndoe", username);
    }

    @Test
    void extractUsername_differentUsers_returnsMatchingUsername() {
        String token1 = jwtUtil.generateToken("alice", "ROLE_BUYER");
        String token2 = jwtUtil.generateToken("bob", "ROLE_BUYER");

        assertEquals("alice", jwtUtil.extractUsername(token1));
        assertEquals("bob", jwtUtil.extractUsername(token2));
    }

    @Test
    void extractUsername_invalidToken_throwsException() {
        assertThrows(Exception.class, () -> jwtUtil.extractUsername("invalid.token.value"));
    }

    @Test
    void extractUsername_tamperedToken_throwsException() {
        String token = jwtUtil.generateToken("testuser", "ROLE_BUYER");
        // Tamper with the signature
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";

        assertThrows(Exception.class, () -> jwtUtil.extractUsername(tampered));
    }

    @Test
    void extractUsername_expiredToken_throwsException() {
        // Build a token that expired in the past using the injected secret
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        String expiredToken = Jwts.builder()
                .setSubject("testuser")
                .claim("role", "ROLE_BUYER")
                .setIssuedAt(new Date(System.currentTimeMillis() - 3600000))
                .setExpiration(new Date(System.currentTimeMillis() - 1800000)) // Expired 30 mins ago
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        assertThrows(ExpiredJwtException.class, () -> jwtUtil.extractUsername(expiredToken));
    }

    // ======================== Round-Trip Tests ========================

    @Test
    void generateAndExtract_roundTrip_preservesUsername() {
        String username = "roundTripUser";
        String token = jwtUtil.generateToken(username, "ROLE_BUYER");
        String extracted = jwtUtil.extractUsername(token);

        assertEquals(username, extracted);
    }

    @Test
    void generateToken_tokenNotExpiredImmediately_extractsSuccessfully() {
        String token = jwtUtil.generateToken("testuser", "ROLE_BUYER");

        // Should not throw — token was just created and has 30 min expiry
        assertDoesNotThrow(() -> jwtUtil.extractUsername(token));
    }
}