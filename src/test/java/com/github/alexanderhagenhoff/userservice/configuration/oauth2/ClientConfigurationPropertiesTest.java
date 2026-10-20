package com.github.alexanderhagenhoff.userservice.configuration.oauth2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClientConfigurationPropertiesTest {

    @Test
    void shouldLoadPropertiesWhenFileExists() throws IOException {
        var loader = new ClientConfigurationProperties("notsecretfortest/test-clients.yaml");

        Map<String, ClientDefinition> clientDefinitions = loader.loadClients();

        ClientDefinition clientDefinition1 = clientDefinitions.get("test-client-1");
        Assertions.assertNotNull(clientDefinition1);

        ClientDefinition clientDefinition2 = clientDefinitions.get("test-client-2");
        Assertions.assertNotNull(clientDefinition1);

        Assertions.assertEquals("$2a$12$NbCKtL5MYQuKAADt48/hAu9Yv4kA8YpkFMlqeXkf7m/XujEEtOwjW", clientDefinition1.secret());
        Assertions.assertEquals(asList("api:read", "api:write"), clientDefinition1.scopes());

        Assertions.assertEquals("test_secret", clientDefinition2.secret());
        Assertions.assertEquals(asList("api:read"), clientDefinition2.scopes());
    }

    @Test
    void shouldThrowFileNotFoundExceptionWhenFileDoesNotExist() {
        var loader = new ClientConfigurationProperties("non-existent.properties");

        assertThatThrownBy(loader::loadClients)
                .isInstanceOf(FileNotFoundException.class)
                .hasMessage("OAuth2 client configuration not found. Please create non-existent.properties");
    }
}
