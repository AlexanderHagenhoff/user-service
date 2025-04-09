package com.github.alexanderhagenhoff.userservice.configuration.oauth2;

import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
public class ClientConfigurationProperties {
    private static final String CLIENTS_PROPERTIES_PATH = "secret/clients.properties";
    private static final String MISSING_FILE_WARNING_MESSAGE = "OAuth2 client configuration not found. Please create %s".formatted(CLIENTS_PROPERTIES_PATH);

    private static void raiseExceptionOnMissingFile(InputStream is) throws FileNotFoundException {
        if (is == null) {
            throw new FileNotFoundException(MISSING_FILE_WARNING_MESSAGE);
        }
    }

    public Properties loadClients() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CLIENTS_PROPERTIES_PATH)) {
            raiseExceptionOnMissingFile(inputStream);
            properties.load(inputStream);
        }
        return properties;
    }
}
