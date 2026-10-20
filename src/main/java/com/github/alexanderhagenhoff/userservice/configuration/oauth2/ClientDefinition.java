package com.github.alexanderhagenhoff.userservice.configuration.oauth2;

import java.util.List;

public record ClientDefinition(String secret, List<String> scopes) {
    public ClientDefinition {
        if (scopes == null) {
            scopes = List.of();
        }
    }
}
