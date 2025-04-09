package com.github.alexanderhagenhoff.userservice.configuration.oauth2;

import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

@Component
public class JwkKeyPairProvider {
    private static final String ALGORITHM = "RSA";
    private static final String MISSING_ALGORITHM_MESSAGE = "Error generating RSA key pair for OAuth2. Algorithm %s not found.".formatted(ALGORITHM);

    private static final int KEY_SIZE = 2048;

    public KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM);
            generator.initialize(KEY_SIZE);

            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(MISSING_ALGORITHM_MESSAGE, e);
        }
    }
}
