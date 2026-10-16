package com.github.alexanderhagenhoff.userservice.repository;

import com.github.alexanderhagenhoff.userservice.configuration.TestFlywayConfiguration;
import com.github.alexanderhagenhoff.userservice.entity.User;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

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
@Import(TestFlywayConfiguration.class)
class UserRepositoryIT {

    private static final String EXPECTED_EMAIL = "expected_email_for_test";
    private static final String EXPECTED_EXTERNAL_SUBJECT_ID = "expected_external_subject_id";

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveUser() {
        User newUser = new User(EXPECTED_EMAIL, EXPECTED_EXTERNAL_SUBJECT_ID);
        assertNull(newUser.getId(), "Not saved user should not have a UUID");

        User savedUser = userRepository.save(newUser);
        assertNotNull(savedUser.getId(), "Saved user should have a UUID");

        Optional<User> optionalFoundUser = userRepository.findById(savedUser.getId());

        assertTrue(optionalFoundUser.isPresent(), "User should be found");

        User foundUser = optionalFoundUser.get();
        assertEquals(EXPECTED_EMAIL, foundUser.getEmailHash());
        assertNotNull(foundUser.getCreatedAt(), "Created date should be set");
        assertNotNull(foundUser.getLastModifiedAt(), "Modified date should be set");
        assertNotNull(foundUser.getId(), "Loaded user should have a UUID");
    }

    @Test
    void shouldCheckEmailExistence() {
        User user = createTestUser();

        assertTrue(userRepository.existsByEmailHash(EXPECTED_EMAIL),
                "Email should exist");
        assertFalse(userRepository.existsByEmailHash("unknown@example.com"),
                "Unknown emailHash should not exist");
    }

    @Test
    void shouldFindUserByEmail() {
        User expectedUser = createTestUser();

        User actualUser = userRepository.findByEmailHash(EXPECTED_EMAIL);

        assertNotNull(actualUser, "User should be found");
        assertEquals(expectedUser.getId(), actualUser.getId());
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
        String expectedEmail = "emailHash";

        User user = userRepository.save(new User("original_mail", "original_external_subject_id"));
        ZonedDateTime originalModifiedDate = user.getLastModifiedAt();

        user.setEmailHash(expectedEmail);
        User updatedUser = userRepository.save(user);

        assertTrue(updatedUser.getLastModifiedAt().isAfter(originalModifiedDate),
                "Last modified date should be updated");
    }

    @NotNull
    private User createTestUser() {
        User testUser = new User(EXPECTED_EMAIL, EXPECTED_EXTERNAL_SUBJECT_ID);

        return userRepository.save(testUser);
    }
}
