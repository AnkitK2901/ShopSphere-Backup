package com.shopsphere.auth_service.repository;

import com.shopsphere.auth_service.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setName("John Doe");
        testUser.setUsername("johndoe");
        testUser.setEmail("john@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setAddress("123 Main St");
        testUser.setGender("Male");
        entityManager.persistAndFlush(testUser);
    }

    // ======================== findByUsername Tests ========================

    @Test
    void findByUsername_existingUsername_returnsUser() {
        User found = userRepository.findByUsername("johndoe");

        assertNotNull(found);
        assertEquals("johndoe", found.getUsername());
        assertEquals("John Doe", found.getName());
        assertEquals("john@example.com", found.getEmail());
    }

    @Test
    void findByUsername_nonExistingUsername_returnsNull() {
        User found = userRepository.findByUsername("nonexistent");

        assertNull(found);
    }

    @Test
    void findByUsername_caseSensitive_matchesCaseInsensitiveInMySQL() {
        // MySQL default collation is case-insensitive, so "JohnDoe" matches "johndoe"
        User found = userRepository.findByUsername("JohnDoe");

        assertNotNull(found);
        assertEquals("johndoe", found.getUsername());
    }

    // ======================== existsByUsername Tests ========================

    @Test
    void existsByUsername_existingUsername_returnsTrue() {
        boolean exists = userRepository.existsByUsername("johndoe");

        assertTrue(exists);
    }

    @Test
    void existsByUsername_nonExistingUsername_returnsFalse() {
        boolean exists = userRepository.existsByUsername("unknown");

        assertFalse(exists);
    }

    // ======================== existsByEmail Tests ========================

    @Test
    void existsByEmail_existingEmail_returnsTrue() {
        boolean exists = userRepository.existsByEmail("john@example.com");

        assertTrue(exists);
    }

    @Test
    void existsByEmail_nonExistingEmail_returnsFalse() {
        boolean exists = userRepository.existsByEmail("nobody@example.com");

        assertFalse(exists);
    }

    // ======================== CRUD Operation Tests ========================

    @Test
    void save_newUser_persistsSuccessfully() {
        User newUser = new User();
        newUser.setName("Jane Smith");
        newUser.setUsername("janesmith");
        newUser.setEmail("jane@example.com");
        newUser.setPassword("password");
        newUser.setAddress("456 Oak Ave");
        newUser.setGender("Female");

        User saved = userRepository.save(newUser);

        assertNotNull(saved.getId());
        assertEquals("janesmith", saved.getUsername());
    }

    @Test
    void findById_existingUser_returnsUser() {
        Long userId = testUser.getId();

        assertTrue(userRepository.findById(userId).isPresent());
        assertEquals("johndoe", userRepository.findById(userId).get().getUsername());
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        assertFalse(userRepository.findById(999L).isPresent());
    }

    @Test
    void deleteById_existingUser_removesUser() {
        Long userId = testUser.getId();
        userRepository.deleteById(userId);
        entityManager.flush();

        assertFalse(userRepository.findById(userId).isPresent());
    }

    @Test
    void existsById_existingUser_returnsTrue() {
        assertTrue(userRepository.existsById(testUser.getId()));
    }

    @Test
    void existsById_nonExistingUser_returnsFalse() {
        assertFalse(userRepository.existsById(999L));
    }
}