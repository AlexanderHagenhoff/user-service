package com.github.alexanderhagenhoff.userservice.configuration;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.time.Duration.ofHours;
import static java.util.UUID.randomUUID;
import static org.springframework.security.oauth2.core.ClientAuthenticationMethod.CLIENT_SECRET_BASIC;

@Configuration
public class AppConfig {

    private static final int RSA_KEY_SIZE = 2048;
    private static final int OAUTH_TOKEN_VALID_FOR_HOURS = 6;

    private static final String CRYPTO_ALGORITHM = "RSA";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(PasswordEncoder passwordEncoder) throws IOException {
        Properties clientProps = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("secret/clients.properties")) {
            if (is == null)
                throw new FileNotFoundException("secret/clients.properties not found. You have to configure clients.properties to allow oauth2 access.");
            clientProps.load(is);
        }

        List<RegisteredClient> clients = loadRegisteredClients(clientProps, passwordEncoder);

        return new InMemoryRegisteredClientRepository(clients);
    }

    private List<RegisteredClient> loadRegisteredClients(Properties clientProperties, PasswordEncoder passwordEncoder) {
        List<RegisteredClient> clients = new ArrayList<>();
        clientProperties.stringPropertyNames().forEach(clientId -> {
            String clientSecret = clientProperties.getProperty(clientId);
            RegisteredClient client = RegisteredClient.withId(randomUUID().toString())
                    .clientId(clientId)
                    .clientSecret(passwordEncoder.encode(clientSecret))
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .tokenSettings(TokenSettings.builder()
                            .accessTokenTimeToLive(ofHours(OAUTH_TOKEN_VALID_FOR_HOURS))
                            .build())
                    .clientAuthenticationMethod(CLIENT_SECRET_BASIC)
                    .build();
            clients.add(client);
        });

        return clients;
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(CRYPTO_ALGORITHM);
        generator.initialize(RSA_KEY_SIZE);

        KeyPair keyPair = generator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(String.valueOf(randomUUID()))
                .build();

        JWKSet jwkSet = new JWKSet(rsaKey);

        return new ImmutableJWKSet<>(jwkSet);
    }
}
