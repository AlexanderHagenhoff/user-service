package com.github.alexanderhagenhoff.userservice.service;

import com.github.alexanderhagenhoff.userservice.entity.User;
import com.github.alexanderhagenhoff.userservice.exception.NotFoundException;
import com.github.alexanderhagenhoff.userservice.repository.UserRepository;
import com.github.alexanderhagenhoff.userservice.service.dto.UserDto;
import com.github.alexanderhagenhoff.userservice.service.mapper.UserDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final UUID TEST_ID = UUID.randomUUID();
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_FIRST_NAME = "Test";
    private static final String TEST_LAST_NAME = "User";

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDtoMapper userDtoMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUser = new User(TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL);
        testUserDto = new UserDto(TEST_ID, TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL, ZonedDateTime.now(), ZonedDateTime.now());
    }

    @Test
    void shouldRetrieveUserById() throws NotFoundException {
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(testUser));
        when(userDtoMapper.toDto(testUser)).thenReturn(testUserDto);

        UserDto foundUser = userService.getUser(TEST_ID);
        assertNotNull(foundUser);
        assertEquals(TEST_ID, foundUser.id());
    }
}
