package com.github.alexanderhagenhoff.userservice.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.CONFLICT;

@ResponseStatus(CONFLICT)
public class EmailAlreadyInUseException extends IllegalArgumentException {

    private static final String MESSAGE_TEMPLATE = "A User with email [%s] and same externalSubjectId already exists.";

    public EmailAlreadyInUseException(String email) {
        super(format(MESSAGE_TEMPLATE, email));
    }
}
