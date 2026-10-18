package com.github.alexanderhagenhoff.userservice.service;

import com.github.alexanderhagenhoff.userservice.entity.User;
import com.github.alexanderhagenhoff.userservice.exception.EmailHashAlreadyInUseException;
import com.github.alexanderhagenhoff.userservice.exception.UserNotFoundException;
import com.github.alexanderhagenhoff.userservice.repository.UserRepository;
import com.github.alexanderhagenhoff.userservice.service.dto.CreateUserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static com.github.alexanderhagenhoff.userservice.TestProfile.INTEGRATION_TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles(INTEGRATION_TEST)
@Sql(scripts = "classpath:db/delete_users_content.sql")
class UserServiceIT {

    private static final String TEST_EMAIL_HASH = "test_email_hash";
    private static final String TEST_EXTERNAL_SUBJECT_ID = "test_external_subject_id";
    private static final String NEW_EMAIL = "new_email@example.com";

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        User createdUser = userService.getOrCreateUser(TEST_EXTERNAL_SUBJECT_ID, TEST_EMAIL_HASH);
        userId = createdUser.getId();
    }

    @Test
    void shouldRetrieveUserById() throws Exception {
        User user = userService.getUser(userId);
        assertNotNull(user);
        assertEquals(TEST_EMAIL_HASH, user.getEmailHash());
    }

    @Test
    void shouldRetrieveUserByEmail() {
        User user = userService.getUserByEmailHash(TEST_EMAIL_HASH);
        assertNotNull(user);
        assertEquals(userId, user.getId());
    }

    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        User userToCreate = new User(NEW_EMAIL, TEST_EXTERNAL_SUBJECT_ID);
        User createdUser = userService.createUser(userToCreate);
        UUID userId = createdUser.getId();

        assertNotNull(userId);
        assertEquals(NEW_EMAIL, createdUser.getEmailHash());

        User userFromDb = userService.getUser(userId);
        assertNotNull(userFromDb);
        assertEquals(NEW_EMAIL, userFromDb.getEmailHash());
    }

    @Test
    void shouldThrowExceptionWhenEmailExists() {
        User duplicateUser = new User(TEST_EMAIL_HASH, TEST_EXTERNAL_SUBJECT_ID);
        assertThrows(EmailHashAlreadyInUseException.class, () -> userService.createUser(duplicateUser));
    }

    @Test
    void shouldDeleteUser() {
        userService.deleteUser(userId);
        assertFalse(userRepository.existsById(userId));

        assertThrows(UserNotFoundException.class, () -> userService.getUser(userId));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        assertThrows(UserNotFoundException.class, () -> userService.getUser(nonExistentId));
    }
}
