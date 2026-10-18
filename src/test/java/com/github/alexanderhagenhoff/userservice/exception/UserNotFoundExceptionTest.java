package com.github.alexanderhagenhoff.userservice.exception;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserNotFoundExceptionTest {

    @Test
    void shouldFormatMessageWithGivenUuid() {
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        UserNotFoundException exception = new UserNotFoundException(id);

        assertThat(exception.getMessage())
                .isEqualTo("User with id [123e4567-e89b-12d3-a456-426614174000] not found.");
    }

    @Test
    void shouldHandleNullUuidWithoutThrowing() {
        UserNotFoundException exception = new UserNotFoundException(null);

        assertThat(exception.getMessage())
                .isEqualTo("User with id [null] not found.");
    }
}
