package com.github.alexanderhagenhoff.userservice.service;

import com.github.alexanderhagenhoff.userservice.entity.User;
import com.github.alexanderhagenhoff.userservice.exception.EmailAlreadyInUseException;
import com.github.alexanderhagenhoff.userservice.exception.NotFoundException;
import com.github.alexanderhagenhoff.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final UUID TEST_ID = UUID.randomUUID();
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_FIRST_NAME = "Test";
    private static final String TEST_LAST_NAME = "User";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL);
        testUser.setId(TEST_ID);
    }

    @Test
    void shouldRetrieveUserById() throws Exception {
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(testUser));

        User foundUser = userService.getUser(TEST_ID);
        assertNotNull(foundUser);
        assertEquals(TEST_ID, foundUser.getId());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUser(TEST_ID));
    }

    @Test
    void shouldRetrieveUserByEmail() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(testUser);

        User foundUser = userService.getUserByEmail(TEST_EMAIL);
        assertNotNull(foundUser);
        assertEquals(TEST_EMAIL, foundUser.getEmail());
    }

    @Test
    void shouldCreateUserSuccessfully() {
        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        when(userRepository.save(testUser)).thenReturn(testUser);

        User createdUser = userService.createUser(testUser);
        assertNotNull(createdUser);
        assertEquals(TEST_EMAIL, createdUser.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(true);

        assertThrows(EmailAlreadyInUseException.class, () -> userService.createUser(testUser));
    }

    @Test
    void shouldDeleteUser() {
        doNothing().when(userRepository).deleteById(TEST_ID);

        assertDoesNotThrow(() -> userService.deleteUser(TEST_ID));
        verify(userRepository, times(1)).deleteById(TEST_ID);
    }
}
