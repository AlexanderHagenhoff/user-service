package com.github.alexanderhagenhoff.userservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.security.interfaces.RSAPublicKey;

@Configuration
public class JwtDecoderConfiguration {

    @Bean
    public JwtDecoder jwtDecoder(RSAPublicKey publicKey) throws Exception {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }
}
