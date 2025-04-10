package com.github.alexanderhagenhoff.userservice.configuration.oauth2;

import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Base64;

import static com.github.alexanderhagenhoff.userservice.TestProfile.INTEGRATION_TEST;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
@Profile(INTEGRATION_TEST)
public class OAuth2TestHelper {

    private static final String OAUTH2_TOKEN_ENDPOINT = "/oauth2/token";
    private static final String JSON_PATH_ACCESS_TOKEN = "$.access_token";
    private static final String PARAMETER_GRANT_TYPE = "grant_type";
    private static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
    private static final String MISSING_MOCK_MVC_MESSAGE = "MockMvc is not available in this context. You might want to use '@AutoConfigureMockMvc' to provide it.";
    private static final String ID_TEST_CLIENT_1 = "test-client-1";
    private static final String SECRET_TEST_CLIENT_1 = "test-secret-1";

    private final ObjectProvider<MockMvc> mockMvcProvider;

    private String cachedToken;

    public OAuth2TestHelper(ObjectProvider<MockMvc> mockMvcProvider) {
        this.mockMvcProvider = mockMvcProvider;
    }

    public String getValidToken() throws Exception {
        if (cachedToken == null) {
            cachedToken = fetchNewToken();
        }
        return cachedToken;
    }

    private String fetchNewToken() throws Exception {
        MockMvc mockMvc = getMockMvc();

        String credentials = ID_TEST_CLIENT_1 + ":" + SECRET_TEST_CLIENT_1;
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

        MvcResult result = mockMvc.perform(
                        post(OAUTH2_TOKEN_ENDPOINT)
                                .header(AUTHORIZATION, basicAuth)
                                .contentType(APPLICATION_FORM_URLENCODED)
                                .param(PARAMETER_GRANT_TYPE, GRANT_TYPE_CLIENT_CREDENTIALS)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ACCESS_TOKEN).exists())
                .andReturn();

        String accessToken = JsonPath.read(result.getResponse().getContentAsString(), JSON_PATH_ACCESS_TOKEN);
        assertNotNull(accessToken);
        assertFalse(accessToken.isEmpty(), "Test access token for OAuth2 has to have length greater 0");

        return accessToken;
    }

    private MockMvc getMockMvc() {
        try {
            return mockMvcProvider.getIfAvailable();
        } catch (NoSuchBeanDefinitionException e) {
            throw new IllegalStateException(MISSING_MOCK_MVC_MESSAGE);
        }
    }
}
