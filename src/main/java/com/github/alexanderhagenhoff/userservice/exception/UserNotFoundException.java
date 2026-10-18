package com.github.alexanderhagenhoff.userservice.exception;

import java.util.UUID;

import static java.lang.String.format;

public class UserNotFoundException extends Exception {

    private static final String MESSAGE_TEMPLATE = "User with id [%s] not found.";

    public UserNotFoundException(UUID uuid) {
        super(format(MESSAGE_TEMPLATE, uuid));
    }
}
