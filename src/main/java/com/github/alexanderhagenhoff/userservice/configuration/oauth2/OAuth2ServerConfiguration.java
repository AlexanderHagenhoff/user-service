package com.github.alexanderhagenhoff.userservice.configuration.oauth2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class OAuth2ServerConfiguration {

    @Bean
    public RegisteredClientRepository registeredClientRepository(
            ClientConfigurationProperties clientConfig,
            RegisteredClientProvider clientProvider,
            PasswordEncoder encoder
    ) throws IOException {
        Properties properties = clientConfig.loadClients();
        return new InMemoryRegisteredClientRepository(clientProvider.createClients(properties, encoder));
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }
}
