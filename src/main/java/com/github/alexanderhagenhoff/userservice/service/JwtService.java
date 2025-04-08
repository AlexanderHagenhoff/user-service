package com.github.alexanderhagenhoff.userservice.service;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static io.jsonwebtoken.SignatureAlgorithm.RS512;

@Service
public class JwtService {

    private static final String SERVICE_NAME = "user-service";

    private static final int SECONDS_IN_HOUR = 3600;

    private final PrivateKey privateKey;

    public JwtService(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public String generateToken(String clientId) {
        Instant now = Instant.now();
        Instant oneHourInFuture = now.plusSeconds(SECONDS_IN_HOUR);

        return Jwts.builder()
                .subject(clientId)
                .issuer(SERVICE_NAME)
                .issuedAt(Date.from(now))
                .expiration(Date.from(oneHourInFuture))
                .id(UUID.randomUUID().toString())
                .signWith(privateKey, RS512)
                .compact();
    }
}
