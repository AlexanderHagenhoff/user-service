package com.github.alexanderhagenhoff.userservice.exception;

import java.util.UUID;

import static java.lang.String.format;

public class NotFoundException extends Exception {

    public NotFoundException(UUID uuid) {
        super(format("User with id [%s] not found.", uuid));
    }
}
