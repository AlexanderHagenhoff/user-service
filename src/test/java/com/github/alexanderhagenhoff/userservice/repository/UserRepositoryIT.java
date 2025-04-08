package com.github.alexanderhagenhoff.userservice.repository;

import com.github.alexanderhagenhoff.userservice.entity.User;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.ZonedDateTime;
import java.util.Optional;

import static com.github.alexanderhagenhoff.userservice.TestProfile.INTEGRATION_TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles(INTEGRATION_TEST)
@Sql(scripts = "classpath:db/delete_users_content.sql")
class UserRepositoryIT {

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
        assertNull(newUser.getId(), "Not saved user should not have a UUID");

        User savedUser = userRepository.save(newUser);
        assertNotNull(savedUser.getId(), "Saved user should have a UUID");

        Optional<User> optionalFoundUser = userRepository.findById(savedUser.getId());

        assertTrue(optionalFoundUser.isPresent(), "User should be found");

        User foundUser = optionalFoundUser.get();
        assertEquals(EXPECTED_FIRST_NAME, foundUser.getFirstName());
        assertEquals(EXPECTED_LAST_NAME, foundUser.getLastName());
        assertEquals(EXPECTED_EMAIL, foundUser.getEmail());
        assertNotNull(foundUser.getCreatedAt(), "Created date should be set");
        assertNotNull(foundUser.getLastModifiedAt(), "Modified date should be set");
        assertNotNull(foundUser.getId(), "Loaded user should have a UUID");
    }

    @Test
    void shouldCheckEmailExistence() {
        User user = createTestUser();

        assertNotNull(user);
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
