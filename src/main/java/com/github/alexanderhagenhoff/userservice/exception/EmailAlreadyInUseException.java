package com.github.alexanderhagenhoff.userservice.exception;

import static java.lang.String.format;

public class EmailAlreadyInUseException extends IllegalArgumentException {
    public EmailAlreadyInUseException(String email) {
        super(format("A User with email [%s] already exists.", email));
    }
}
