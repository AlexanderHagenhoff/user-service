package com.github.alexanderhagenhoff.userservice.configuration.oauth2;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClientConfigurationPropertiesTest {

    @Test
    void shouldLoadPropertiesWhenFileExists() throws IOException {
        var loader = new ClientConfigurationProperties("notsecretfortest/test-clients.properties");

        Properties props = loader.loadClients();

        assertThat(props).isNotEmpty();
        assertThat(props.getProperty("test-client-1")).isEqualTo("$2a$12$NbCKtL5MYQuKAADt48/hAu9Yv4kA8YpkFMlqeXkf7m/XujEEtOwjW");
        assertThat(props.getProperty("test-client-2")).isEqualTo("test-client-id-2");
    }

    @Test
    void shouldThrowFileNotFoundExceptionWhenFileDoesNotExist() {
        var loader = new ClientConfigurationProperties("non-existent.properties");

        assertThatThrownBy(loader::loadClients)
                .isInstanceOf(FileNotFoundException.class)
                .hasMessage("OAuth2 client configuration not found. Please create non-existent.properties");
    }
}
