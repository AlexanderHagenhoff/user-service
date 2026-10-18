package com.github.alexanderhagenhoff.userservice.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.assertj.core.api.Assertions.assertThat;

class EmailHashAlreadyInUseExceptionTest {

    @Test
    void shouldFormatMessageWithEmailHash() {
        String hash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

        EmailHashAlreadyInUseException exception = new EmailHashAlreadyInUseException(hash);

        assertThat(exception.getMessage())
                .isEqualTo("A User with emailHash [e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855].");
    }

    @Test
    void shouldHandleNullEmailHashWithoutThrowing() {
        EmailHashAlreadyInUseException exception = new EmailHashAlreadyInUseException(null);

        assertThat(exception.getMessage())
                .isEqualTo("A User with emailHash [null].");
    }

    @Test
    void shouldHaveConflictResponseStatusAnnotation() {
        ResponseStatus annotation = EmailHashAlreadyInUseException.class.getAnnotation(ResponseStatus.class);

        assertThat(annotation).isNotNull();
        assertThat(annotation.value()).isEqualTo(HttpStatus.CONFLICT);
    }
}
