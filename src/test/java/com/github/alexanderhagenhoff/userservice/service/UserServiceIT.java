package com.github.alexanderhagenhoff.userservice.service;

import com.github.alexanderhagenhoff.userservice.exception.EmailAlreadyInUseException;
import com.github.alexanderhagenhoff.userservice.exception.NotFoundException;
import com.github.alexanderhagenhoff.userservice.repository.UserRepository;
import com.github.alexanderhagenhoff.userservice.service.dto.CreateUserDto;
import com.github.alexanderhagenhoff.userservice.service.dto.UserDto;
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

    private static final String TEST_EMAIL = "test_email@example.com";
    private static final String TEST_FIRST_NAME = "Test";
    private static final String TEST_LAST_NAME = "User";
    private static final String NEW_EMAIL = "new_email@example.com";
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
        CreateUserDto createUserDto = new CreateUserDto(TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL);
        UserDto createdUserDto = userService.createUser(createUserDto);
        userId = createdUserDto.id();
    }

    @Test
    void shouldRetrieveUserById() throws Exception {
        UserDto userDto = userService.getUser(userId);
        assertNotNull(userDto);
        assertEquals(TEST_EMAIL, userDto.email());
    }

    @Test
    void shouldRetrieveUserByEmail() {
        UserDto userDto = userService.getUserByEmail(TEST_EMAIL);
        assertNotNull(userDto);
        assertEquals(userId, userDto.id());
    }

    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        CreateUserDto createUserDto = new CreateUserDto(NEW_FIRST_NAME, NEW_LAST_NAME, NEW_EMAIL);
        UserDto createdUserDto = userService.createUser(createUserDto);

        assertNotNull(createdUserDto.id());
        assertEquals(NEW_FIRST_NAME, createdUserDto.firstName());
        assertEquals(NEW_LAST_NAME, createdUserDto.lastName());
        assertEquals(NEW_EMAIL, createdUserDto.email());

        UserDto userFromDb = userService.getUser(createdUserDto.id());
        assertNotNull(userFromDb);
        assertEquals(NEW_FIRST_NAME, userFromDb.firstName());
        assertEquals(NEW_LAST_NAME, userFromDb.lastName());
        assertEquals(NEW_EMAIL, userFromDb.email());
    }

    @Test
    void shouldThrowExceptionWhenEmailExists() {
        CreateUserDto duplicateUserDto = new CreateUserDto(TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL);
        assertThrows(EmailAlreadyInUseException.class, () -> userService.createUser(duplicateUserDto));
    }

    @Test
    void shouldDeleteUser() {
        userService.deleteUser(userId);
        assertFalse(userRepository.existsById(userId));

        assertThrows(NotFoundException.class, () -> userService.getUser(userId));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        assertThrows(NotFoundException.class, () -> userService.getUser(nonExistentId));
    }
}
