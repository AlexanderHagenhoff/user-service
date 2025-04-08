package com.github.alexanderhagenhoff.userservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class PublicKeyConfiguration {

    @Value("${inter-service.jwt.public-key-path}")
    private Path publicKeyPath;

    @Bean
    public RSAPublicKey loadPublicKey(RsaKeyLoader keyLoader) throws Exception {
        return keyLoader.loadPublicKey(publicKeyPath);
    }
}
