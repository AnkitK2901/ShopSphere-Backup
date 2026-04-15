package com.shopsphere.auth_service.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    // ======================== generateToken Tests ========================

    @Test
    void generateToken_validUsername_returnsNonNullToken() {
        String token = jwtUtil.generateToken("testuser");

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void generateToken_validUsername_tokenContainsThreeParts() {
        String token = jwtUtil.generateToken("testuser");

        // JWT format: header.payload.signature
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);
    }

    @Test
    void generateToken_differentUsernames_returnsDifferentTokens() {
        String token1 = jwtUtil.generateToken("user1");
        String token2 = jwtUtil.generateToken("user2");

        assertNotEquals(token1, token2);
    }

    // ======================== extractUsername Tests ========================

    @Test
    void extractUsername_validToken_returnsCorrectUsername() {
        String token = jwtUtil.generateToken("johndoe");

        String username = jwtUtil.extractUsername(token);

        assertEquals("johndoe", username);
    }

    @Test
    void extractUsername_differentUsers_returnsMatchingUsername() {
        String token1 = jwtUtil.generateToken("alice");
        String token2 = jwtUtil.generateToken("bob");

        assertEquals("alice", jwtUtil.extractUsername(token1));
        assertEquals("bob", jwtUtil.extractUsername(token2));
    }

    @Test
    void extractUsername_invalidToken_throwsException() {
        assertThrows(Exception.class, () -> jwtUtil.extractUsername("invalid.token.value"));
    }

    @Test
    void extractUsername_tamperedToken_throwsException() {
        String token = jwtUtil.generateToken("testuser");
        // Tamper with the signature
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";

        assertThrows(Exception.class, () -> jwtUtil.extractUsername(tampered));
    }

    @Test
    void extractUsername_expiredToken_throwsException() {
        // Build a token that expired in the past
        byte[] keyBytes = Decoders.BASE64.decode(JwtUtil.SECRET);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        String expiredToken = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date(System.currentTimeMillis() - 3600000))
                .setExpiration(new Date(System.currentTimeMillis() - 1800000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        assertThrows(Exception.class, () -> jwtUtil.extractUsername(expiredToken));
    }

    // ======================== Round-Trip Tests ========================

    @Test
    void generateAndExtract_roundTrip_preservesUsername() {
        String username = "roundTripUser";
        String token = jwtUtil.generateToken(username);
        String extracted = jwtUtil.extractUsername(token);

        assertEquals(username, extracted);
    }

    @Test
    void generateToken_tokenNotExpiredImmediately_extractsSuccessfully() {
        String token = jwtUtil.generateToken("testuser");

        // Should not throw — token was just created and has 30 min expiry
        assertDoesNotThrow(() -> jwtUtil.extractUsername(token));
    }
}
