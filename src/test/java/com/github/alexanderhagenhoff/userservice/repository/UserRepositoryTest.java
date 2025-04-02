package com.github.alexanderhagenhoff.userservice.repository;

import com.github.alexanderhagenhoff.userservice.entity.User;
import org.hibernate.annotations.processing.SQL;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@ActiveProfiles("integrationtest")
@Sql(scripts = "classpath:db/delete_users_content.sql")
class UserRepositoryTest {

    private static final String EXPECTED_EMAIL = "expected_email_for_test";
    private static final String EXPECTED_FIRST_NAME = "expected_first_name";
    private static final String EXPECTED_LAST_NAME = "expected_last_name";

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveUser() {
        User newUser = new User(EXPECTED_FIRST_NAME, EXPECTED_LAST_NAME, EXPECTED_EMAIL);

        User savedUser = userRepository.save(newUser);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent(), "User should be found");
        assertEquals(EXPECTED_FIRST_NAME, foundUser.get().getFirstName());
        assertEquals(EXPECTED_LAST_NAME, foundUser.get().getLastName());
        assertEquals(EXPECTED_EMAIL, foundUser.get().getEmail());
        assertNotNull(foundUser.get().getCreatedAt(), "Created date should be set");
        assertNotNull(foundUser.get().getLastModifiedAt(), "Modified date should be set");
    }

    @Test
    void shouldCheckEmailExistence() {
        User user = createTestUser();

        assertTrue(userRepository.existsByEmail(EXPECTED_EMAIL),
                "Email should exist");
        assertFalse(userRepository.existsByEmail("unknown@example.com"),
                "Unknown email should not exist");
    }

    @Test
    void shouldFindUserByEmail() {
        User expectedUser = createTestUser();

        User actualUser = userRepository.findByEmail(EXPECTED_EMAIL);

        assertNotNull(actualUser, "User should be found");
        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(EXPECTED_FIRST_NAME, actualUser.getFirstName());
        assertEquals(EXPECTED_LAST_NAME, actualUser.getLastName());
    }

    @Test
    void shouldDeleteUser() {
        User user = createTestUser();
        assertEquals(1, userRepository.count(), "Should have 1 user before deletion");

        userRepository.delete(user);

        assertEquals(0, userRepository.count(), "Should have 0 users after deletion");
        assertFalse(userRepository.existsById(user.getId()),
                "User should not exist anymore");
    }

    @Test
    void shouldUpdateUser() {
        String expectedFirstName = "Updated";
        String expectedSurName = "Name";
        String expectedEmail = "email";

        User user = userRepository.save(new User("original_first_name", "original_sur_name", "original_mail"));
        ZonedDateTime originalModifiedDate = user.getLastModifiedAt();

        user.setFirstName(expectedFirstName);
        user.setLastName(expectedSurName);
        user.setEmail(expectedEmail);
        User updatedUser = userRepository.save(user);

        assertEquals(expectedFirstName, updatedUser.getFirstName());
        assertEquals(expectedSurName, updatedUser.getLastName());
        assertTrue(updatedUser.getLastModifiedAt().isAfter(originalModifiedDate),
                "Last modified date should be updated");
    }

    @NotNull
    private User createTestUser() {
        User testUser = new User(EXPECTED_FIRST_NAME, EXPECTED_LAST_NAME, EXPECTED_EMAIL);

        return userRepository.save(testUser);
    }
}
