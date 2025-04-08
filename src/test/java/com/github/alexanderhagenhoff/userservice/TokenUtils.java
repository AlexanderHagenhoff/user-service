package com.github.alexanderhagenhoff.userservice;


import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private MockMvc mockMvc;

    private static final String TOKEN_URL = "/token";

    public String getJwtToken(String clientId, String clientSecret) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(TOKEN_URL)
                        .param("clientId", clientId)
                        .param("clientSecret", clientSecret)
                        .param("grantType", "client_credentials")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        return JsonPath.read(jsonResponse, "$.access_token");
    }
}
