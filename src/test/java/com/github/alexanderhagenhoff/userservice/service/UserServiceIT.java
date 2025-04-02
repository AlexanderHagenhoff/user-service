package com.github.alexanderhagenhoff.userservice.service;

import com.github.alexanderhagenhoff.userservice.entity.User;
import com.github.alexanderhagenhoff.userservice.exception.EmailAlreadyInUseException;
import com.github.alexanderhagenhoff.userservice.exception.NotFoundException;
import com.github.alexanderhagenhoff.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static com.github.alexanderhagenhoff.userservice.TestProfile.INTEGRATION_TEST;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles(INTEGRATION_TEST)
@Sql(scripts = "classpath:db/delete_users_content.sql")
class UserServiceIT {

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_FIRST_NAME = "Test";
    private static final String TEST_LAST_NAME = "User";
    private static final String NEW_EMAIL = "new@example.com";
    private static final String NEW_FIRST_NAME = "New";
    private static final String NEW_LAST_NAME = "User";

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        User user = new User(TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL);
        User savedUser = userService.createUser(user);
        userId = savedUser.getId();
    }

    @Test
    void shouldRetrieveUserById() throws Exception{
        User user = userService.getUser(userId);
        assertNotNull(user);
        assertEquals(TEST_EMAIL, user.getEmail());
    }

    @Test
    void shouldRetrieveUserByEmail() {
        User user = userService.getUserByEmail(TEST_EMAIL);
        assertNotNull(user);
        assertEquals(userId, user.getId());
    }

    @Test
    void shouldCreateUserSuccessfully() {
        User newUser = new User(NEW_FIRST_NAME, NEW_LAST_NAME, NEW_EMAIL);
        User savedUser = userService.createUser(newUser);

        assertNotNull(savedUser.getId());
        assertEquals(NEW_FIRST_NAME, savedUser.getFirstName());
        assertEquals(NEW_LAST_NAME, savedUser.getLastName());
        assertEquals(NEW_EMAIL, savedUser.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenEmailExists() {
        User duplicateUser = new User(TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL);
        assertThrows(EmailAlreadyInUseException.class, () -> userService.createUser(duplicateUser));
    }

    @Test
    void shouldDeleteUser() {
        userService.deleteUser(userId);
        assertFalse(userRepository.existsById(userId));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        assertThrows(NotFoundException.class, () -> userService.getUser(nonExistentId));
    }
}
