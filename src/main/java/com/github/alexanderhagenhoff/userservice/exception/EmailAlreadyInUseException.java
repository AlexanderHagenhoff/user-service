package com.github.alexanderhagenhoff.userservice.exception;

import static java.lang.String.format;

public class EmailAlreadyInUseException extends IllegalArgumentException {

    private static final String MESSAGE_TEMPLATE = "A User with email [%s] already exists.";

    public EmailAlreadyInUseException(String email) {
        super(format(MESSAGE_TEMPLATE, email));
    }
}
