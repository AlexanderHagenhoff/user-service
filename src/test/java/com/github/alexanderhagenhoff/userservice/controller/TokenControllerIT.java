package com.github.alexanderhagenhoff.userservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.alexanderhagenhoff.userservice.TestProfile.INTEGRATION_TEST;
import static com.github.alexanderhagenhoff.userservice.controller.TokenController.PUBLIC_TOKEN_ENDPOINT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(INTEGRATION_TEST)
class TokenControllerIT {

    private static final String PARAMETER_CLIENT_ID = "clientId";
    private static final String PARAMETER_CLIENT_SECRET = "clientSecret";

    @Value("${inter-service.allowed_service.client_id}")
    private String validClientId;

    @Value("${inter-service.allowed_service.client_secret}")
    private String validClientSecret;

    @Value("${inter-service.jwt.valid_for_seconds:3600}")
    private int expectedValidity;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void issueTokenShouldReturnTokenForValidCredentials() throws Exception {
        mockMvc.perform(get(PUBLIC_TOKEN_ENDPOINT)
                        .param(PARAMETER_CLIENT_ID, validClientId)
                        .param(PARAMETER_CLIENT_SECRET, validClientSecret))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").isString())
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andExpect(jsonPath("$.expires_in").value(expectedValidity));
    }

    @Test
    void issueTokenShouldReturnUnauthorizedForValidClientIdInvalidSecret() throws Exception {
        mockMvc.perform(get(PUBLIC_TOKEN_ENDPOINT)
                        .param(PARAMETER_CLIENT_ID, validClientId)
                        .param(PARAMETER_CLIENT_SECRET, "wrong"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void issueTokenShouldReturnUnauthorizedForInvalidClientIdValidSecret() throws Exception {
        mockMvc.perform(get(PUBLIC_TOKEN_ENDPOINT)
                        .param(PARAMETER_CLIENT_ID, "wrong")
                        .param(PARAMETER_CLIENT_SECRET, validClientSecret))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void issueTokenShouldReturnUnauthorizedForInvalidCredentials() throws Exception {
        mockMvc.perform(get(PUBLIC_TOKEN_ENDPOINT)
                        .param(PARAMETER_CLIENT_ID, "invalid")
                        .param(PARAMETER_CLIENT_SECRET, "wrong"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void issueTokenShouldReturnBadRequestWhenClientIdIsEmpty() throws Exception {
        mockMvc.perform(get(PUBLIC_TOKEN_ENDPOINT)
                        .param(PARAMETER_CLIENT_ID, "")
                        .param(PARAMETER_CLIENT_SECRET, validClientSecret))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void issueTokenShouldReturnBadRequestWhenClientSecretIsEmpty() throws Exception {
        mockMvc.perform(get(PUBLIC_TOKEN_ENDPOINT)
                        .param(PARAMETER_CLIENT_ID, validClientId)
                        .param(PARAMETER_CLIENT_SECRET, ""))
                .andExpect(status().isUnauthorized());
    }
}
