package com.github.alexanderhagenhoff.userservice.configuration.oauth2;

import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.security.oauth2.core.AuthorizationGrantType.CLIENT_CREDENTIALS;
import static org.springframework.security.oauth2.core.ClientAuthenticationMethod.CLIENT_SECRET_BASIC;

@Component
public class RegisteredClientProvider {
    private static final Duration ACCESS_TOKEN_TTL = Duration.ofHours(6);

    public List<RegisteredClient> createClients(@NonNull Map<String, ClientDefinition> clientsMap, PasswordEncoder encoder) {
        return clientsMap.entrySet().stream()
                .map(entry -> createClient(entry.getKey(), entry.getValue(), encoder))
                .toList();
    }

    private RegisteredClient createClient(String clientId, ClientDefinition definition, PasswordEncoder encoder) {
        String uuidString = UUID.randomUUID().toString();

        return RegisteredClient.withId(uuidString)
                .clientId(clientId)
                .clientSecret(definition.secret())
                //.clientSecret(encoder.encode(definition.secret())) todo secret should be encoded
                .clientAuthenticationMethod(CLIENT_SECRET_BASIC)
                .authorizationGrantType(CLIENT_CREDENTIALS)
                .scopes(scopes -> scopes.addAll(definition.scopes()))
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(ACCESS_TOKEN_TTL)
                        .build())
                .build();
    }
}
