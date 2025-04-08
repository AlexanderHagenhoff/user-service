package com.github.alexanderhagenhoff.userservice.service;

import com.github.alexanderhagenhoff.userservice.configuration.RsaKeyLoader;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.Path;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static com.github.alexanderhagenhoff.userservice.TestProfile.INTEGRATION_TEST;
import static io.jsonwebtoken.Jwts.SIG.RS256;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(INTEGRATION_TEST)
public class JwtTestKeysIT {

    private static final String EXPECTED_SUBJECT = "test_subject";

    @Autowired
    private RsaKeyLoader rsaKeyLoader;

    @Value("${inter-service.jwt.private-key-path}")
    private Path privateKeyPath;

    @Value("${inter-service.jwt.public-key-path}")
    private Path publicKeyPath;

    @Test
    void testKeysMatch() throws Exception {
        RSAPublicKey publicKey = rsaKeyLoader.loadPublicKey(publicKeyPath);
        RSAPrivateKey privateKey = rsaKeyLoader.loadPrivateKey(privateKeyPath);

        String token = Jwts.builder()
                .subject(EXPECTED_SUBJECT)
                .signWith(privateKey, RS256)
                .compact();

        Jwt parsedJwt = NimbusJwtDecoder.withPublicKey(publicKey).build().decode(token);
        assertNotNull(parsedJwt);
        assertEquals(EXPECTED_SUBJECT, parsedJwt.getSubject());
    }
}
