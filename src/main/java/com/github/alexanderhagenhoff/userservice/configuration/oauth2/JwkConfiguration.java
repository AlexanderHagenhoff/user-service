package com.github.alexanderhagenhoff.userservice.configuration.oauth2;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
public class JwkConfiguration {
    private static final String KEY_ID = UUID.randomUUID().toString();

    @Bean
    public JWKSource<SecurityContext> jwkSource(JwkKeyPairProvider keyPairProvider) {
        KeyPair keyPair = keyPairProvider.generateKeyPair();
        RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey((RSAPrivateKey) keyPair.getPrivate())
                .keyID(KEY_ID)
                .build();

        return new ImmutableJWKSet<>(new JWKSet(rsaKey));
    }
}