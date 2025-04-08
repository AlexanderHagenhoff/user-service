package com.github.alexanderhagenhoff.userservice;

import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.github.alexanderhagenhoff.userservice.TestProfile.INTEGRATION_TEST;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
@Profile(INTEGRATION_TEST)
public class TokenUtils {

    private static final String PARAMETER_CLIENT_ID = "clientId";
    private static final String PARAMETER_CLIENT_SECRET = "clientSecret";
    private static final String PARAMETER_GRANT_TYPE = "grantType";
    private static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
    private static final String JSON_PATH_FOR_TOKEN = "$.access_token";
    private static final String MISSING_MOCK_MVC_MESSAGE = "MockMvc is not available in this context. You might want to use '@AutoConfigureMockMvc' to provide it.";

    private static final String TOKEN_URL = "/token";

    private final ObjectProvider<MockMvc> mockMvcProvider;

    public TokenUtils(ObjectProvider<MockMvc> mockMvcProvider) {
        this.mockMvcProvider = mockMvcProvider;
    }

    public String getJwtToken(String clientId, String clientSecret) throws Exception {
        MockMvc mockMvc = getMockMvc();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(TOKEN_URL)
                        .param(PARAMETER_CLIENT_ID, clientId)
                        .param(PARAMETER_CLIENT_SECRET, clientSecret)
                        .param(PARAMETER_GRANT_TYPE, GRANT_TYPE_CLIENT_CREDENTIALS)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        return JsonPath.read(jsonResponse, JSON_PATH_FOR_TOKEN);
    }

    private MockMvc getMockMvc() {
        try {
            return mockMvcProvider.getIfAvailable();
        } catch (NoSuchBeanDefinitionException e) {
            throw new IllegalStateException(MISSING_MOCK_MVC_MESSAGE);
        }
    }
}
