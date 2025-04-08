package com.github.alexanderhagenhoff.userservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.nio.file.Path;

@Configuration
public class JwtDecoderConfiguration {

    @Value("${inter-service.jwt.public-key-path}")
    private Path publicKeyPath;

    @Bean
    public JwtDecoder jwtDecoder(RsaPublicKeyLoader keyLoader) throws Exception {
        return NimbusJwtDecoder.withPublicKey(keyLoader.load(publicKeyPath)).build();
    }
}
