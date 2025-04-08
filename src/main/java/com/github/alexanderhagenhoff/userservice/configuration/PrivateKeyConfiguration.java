package com.github.alexanderhagenhoff.userservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.security.interfaces.RSAPrivateKey;

@Configuration
public class PrivateKeyConfiguration {

    @Value("${inter-service.jwt.private-key-path}")
    private Path privateKeyPath;

    @Bean
    public RSAPrivateKey loadPrivateKey(RsaKeyLoader keyLoader) throws Exception {
        return keyLoader.loadPrivateKey(privateKeyPath);
    }
}
