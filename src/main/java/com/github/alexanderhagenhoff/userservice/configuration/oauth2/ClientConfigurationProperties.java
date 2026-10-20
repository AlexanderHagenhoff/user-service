package com.github.alexanderhagenhoff.userservice.configuration.oauth2;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ClientConfigurationProperties {

    private static final String KEY_SECRET = "secret";
    private static final String KEY_SCOPES = "scopes";
    private static final String ROOT_CLIENTS = "clients";

    private final String propertiesPath;

    public ClientConfigurationProperties(@Value("${client.config.path:secret/clients.yaml}") String propertiesPath) {
        this.propertiesPath = propertiesPath;
    }

    @SuppressWarnings("unchecked")
    public Map<String, ClientDefinition> loadClients() throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesPath)) {
            raiseExceptionOnMissingFile(inputStream);

            Yaml yaml = new Yaml();
            Map<String, Object> root = yaml.load(inputStream);

            if (root == null || !root.containsKey(ROOT_CLIENTS)) {
                return Collections.emptyMap();
            }

            Map<String, Map<String, Object>> clientsMap = (Map<String, Map<String, Object>>) root.get(ROOT_CLIENTS);

            return clientsMap.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey, ClientConfigurationProperties::getClientDefinitionFor
                    ));
        }
    }

    private static @NonNull ClientDefinition getClientDefinitionFor(Map.Entry<String, Map<String, Object>> entry) {
        Map<String, Object> details = entry.getValue();
        String secret = (String) details.get(KEY_SECRET);
        List<String> scopes = (List<String>) details.get(KEY_SCOPES);

        return new ClientDefinition(secret, scopes);
    }

    private void raiseExceptionOnMissingFile(InputStream inputStream) throws FileNotFoundException {
        if (inputStream == null) {
            throw new FileNotFoundException(
                    "OAuth2 client configuration not found. Please create %s".formatted(propertiesPath)
            );
        }
    }
}
