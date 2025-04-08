package com.github.alexanderhagenhoff.userservice.configuration;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Path;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class RsaKeyLoaderTest {

    private final Path publicKeyPath = getPath("keyLoaderTest/test_public.pem");
    private final Path privateKeyPath = getPath("keyLoaderTest/test_private.pem");

    @InjectMocks
    private RsaKeyLoader rsaKeyLoader;

    @Test
    public void shouldLoadPublicKeyCorrectly() throws Exception {
        RSAPublicKey publicKey = rsaKeyLoader.loadPublicKey(publicKeyPath);

        assertNotNull(publicKey);
    }

    @Test
    public void shouldLoadPrivateKeyCorrectly() throws Exception {
        RSAPrivateKey privateKey = rsaKeyLoader.loadPrivateKey(privateKeyPath);

        assertNotNull(privateKey);
    }

    @NotNull
    private static Path getPath(String filePath) throws RuntimeException {
        try {
            return Path.of(new ClassPathResource(filePath).getURI());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
