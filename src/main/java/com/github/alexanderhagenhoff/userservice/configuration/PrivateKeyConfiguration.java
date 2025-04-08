package com.github.alexanderhagenhoff.userservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Configuration
public class PrivateKeyConfiguration {

    private static final String RSA_KEY_FILE_PREFIX = "-----BEGIN PRIVATE KEY-----";
    private static final String RSA_KEY_FILE_POSTFIX = "-----END PRIVATE KEY-----";

    private static final String ENCODER_ALGORITHM = "RSA";

    @Value("${inter-service.jwt.private-key-path}")
    private Path privateKeyPath;

    @Bean
    public PrivateKey loadPrivateKey() throws Exception {
        String pemContent = Files.readString(privateKeyPath)
                .replace(RSA_KEY_FILE_PREFIX, "")
                .replace(RSA_KEY_FILE_POSTFIX, "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(pemContent);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ENCODER_ALGORITHM);

        return keyFactory.generatePrivate(keySpec);
    }
}
