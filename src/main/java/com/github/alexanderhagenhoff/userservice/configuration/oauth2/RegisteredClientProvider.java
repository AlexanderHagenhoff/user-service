package com.github.alexanderhagenhoff.userservice.configuration.oauth2;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static org.springframework.security.oauth2.core.AuthorizationGrantType.CLIENT_CREDENTIALS;
import static org.springframework.security.oauth2.core.ClientAuthenticationMethod.CLIENT_SECRET_BASIC;

@Component
public class RegisteredClientProvider {
    private static final Duration ACCESS_TOKEN_TTL = Duration.ofHours(6);

    public List<RegisteredClient> createClients(Properties clientProperties, PasswordEncoder encoder) {
        return clientProperties.stringPropertyNames().stream()
                .map(clientId -> createClient(clientId, clientProperties.getProperty(clientId), encoder))
                .toList();
    }

    private RegisteredClient createClient(String clientId, String clientSecret, PasswordEncoder encoder) {
        String uuidString = UUID.randomUUID().toString();

        return RegisteredClient.withId(uuidString)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .clientAuthenticationMethod(CLIENT_SECRET_BASIC)
                .authorizationGrantType(CLIENT_CREDENTIALS)
                .scopes(strings -> {
                    strings.add("api:read");  //todo load from config
                    strings.add("api:write");
                })
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(ACCESS_TOKEN_TTL)
                        .build())
                .build();
    }
}
