package com.github.alexanderhagenhoff.userservice.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.CONFLICT;

@ResponseStatus(CONFLICT)
public class EmailHashAlreadyInUseException extends IllegalArgumentException {

    private static final String MESSAGE_TEMPLATE = "A User with emailHash [%s].";

    public EmailHashAlreadyInUseException(String emailHash) {
        super(format(MESSAGE_TEMPLATE, emailHash));
    }
}
