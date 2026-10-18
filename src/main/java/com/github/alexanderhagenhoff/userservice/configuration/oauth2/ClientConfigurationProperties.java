package com.github.alexanderhagenhoff.userservice.configuration.oauth2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
public class ClientConfigurationProperties {

    private final String propertiesPath;

    public ClientConfigurationProperties(@Value("${client.config.path:secret/clients.properties}") String propertiesPath) {
        this.propertiesPath = propertiesPath;
    }

    public Properties loadClients() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesPath)) {
            if (inputStream == null) {
                throw new FileNotFoundException(
                        "OAuth2 client configuration not found. Please create %s".formatted(propertiesPath)
                );
            }
            properties.load(inputStream);
        }

        return properties;
    }
}
