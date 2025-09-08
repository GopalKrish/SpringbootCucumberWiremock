package com.dreamzlancer.cucumbersample.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for User entity
 */
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testNoArgsConstructor() {
        assertNotNull(user, "User should be created with no-args constructor");
        assertNull(user.getId(), "ID should be null for new user");
        assertNull(user.getName(), "Name should be null for new user");
        assertNull(user.getEmail(), "Email should be null for new user");
    }

    @Test
    void testAllArgsConstructor() {
        User user = new User(1L, "John Doe", "john.doe@example.com");

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john.doe@example.com", user.getEmail());
    }

    @Test
    void testConstructorWithoutId() {
        User user = new User("Jane Smith", "jane.smith@example.com");

        assertNotNull(user);
        assertNull(user.getId());
        assertEquals("Jane Smith", user.getName());
        assertEquals("jane.smith@example.com", user.getEmail());
    }

    @Test
    void testSettersAndGetters() {
        // Test setters
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        // Test getters
        assertEquals(1L, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john.doe@example.com", user.getEmail());
    }

    @Test
    void testToString() {
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        String toStringResult = user.toString();

        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("John Doe"));
        assertTrue(toStringResult.contains("john.doe@example.com"));
        assertTrue(toStringResult.contains("1"));
    }


    @Test
    void testFieldAnnotations() {
        // This test verifies that Lombok annotations are working
        // We can't directly test JPA annotations in unit tests, but we can verify behavior

        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");

        assertDoesNotThrow(() -> {
            // Test that setters don't throw exceptions for valid data
            user.setName("A".repeat(100)); // Max length
            user.setEmail("test.email@example-domain.com");
        });
    }

    @Test
    void testEdgeCases() {
        // Test with empty strings
        user.setName("");
        user.setEmail("");
        assertEquals("", user.getName());
        assertEquals("", user.getEmail());

        // Test with long values
        String longName = "A".repeat(100);
        String longEmail = "a".repeat(150 - 10) + "@test.com";
        user.setName(longName);
        user.setEmail(longEmail);
        assertEquals(longName, user.getName());
        assertEquals(longEmail, user.getEmail());
    }

    @Test
    void testLombokFunctionality() {
        // Test that Lombok's @Getter and @Setter are working
        user.setId(42L);
        assertEquals(42L, user.getId());

        user.setName("Lombok Test");
        assertEquals("Lombok Test", user.getName());

        user.setEmail("lombok@test.com");
        assertEquals("lombok@test.com", user.getEmail());
    }
}