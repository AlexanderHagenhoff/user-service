package com.github.alexanderhagenhoff.userservice.controller;

import com.github.alexanderhagenhoff.userservice.configuration.oauth2.OAuth2TestHelper;
import com.github.alexanderhagenhoff.userservice.entity.User;
import com.github.alexanderhagenhoff.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static com.github.alexanderhagenhoff.userservice.TestProfile.INTEGRATION_TEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(INTEGRATION_TEST)
public class UserControllerIT {

    private static final String USERS_ENDPOINT = "/users";
    private static final String USERS_BY_ID = "/users/{id}";
    private static final String TEST_EMAIL = "test_email_hash";
    private static final String TEST_FIRST_NAME = "test_first_name";
    private static final String TEST_LAST_NAME = "test_last_name";
    private static final String TEST_EXTERNAL_SUBJECT_ID = "test_external_subject_id";

    private static final String CREATE_JSON_TEMPLATE = """
            {
                "emailHash": "%s",
                "externalSubjectId": "%s"
            }
            """;
    private static final String EXPECTED_JSON_TEMPLATE = """
            {
                "id": "%s",
            }
            """;


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OAuth2TestHelper oauthHelper;

    private UUID userId;

    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        User user = getTestUser();
        user = userRepository.save(user);
        userId = user.getId();

        accessToken = oauthHelper.getValidToken();
    }

    @Test
    void getUserByIdShouldReturnUserWhenUserExists() throws Exception {
        String expectedJson = EXPECTED_JSON_TEMPLATE.formatted(userId);
        mockMvc.perform(withAuth(get(USERS_BY_ID, userId)))
                .andExpect(status().isOk()); //validate json
    }

    @Test
    void getUserByIdShouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        mockMvc.perform(withAuth(get(USERS_BY_ID, UUID.randomUUID())))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUserShouldReturnCreatedWhenUserIsValid() throws Exception {
        String newUserEmail = "new_email";
        String newUserJson = CREATE_JSON_TEMPLATE.formatted("New", "User", newUserEmail,  "externalSubjectId");

        mockMvc.perform(withAuth(put(USERS_ENDPOINT)
                        .contentType(APPLICATION_JSON)
                        .content(newUserJson)))
                .andExpect(status().isOk());
    }

    @Test
    void createUser_ShouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        String duplicateUserJson = CREATE_JSON_TEMPLATE.formatted("changed_email", TEST_EXTERNAL_SUBJECT_ID);

        mockMvc.perform(withAuth(put(USERS_ENDPOINT))
                        .contentType(APPLICATION_JSON)
                        .content(duplicateUserJson))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteUserShouldReturnNoContentWhenUserExists() throws Exception {
        mockMvc.perform(withAuth(delete(USERS_BY_ID, userId)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUserShouldReturnNoContentWhenUserDoesNotExist() throws Exception {
        mockMvc.perform(withAuth(delete(USERS_BY_ID, UUID.randomUUID())))
                .andExpect(status().isNoContent());
    }

    private MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder builder) {
        return builder.header("Authorization", "Bearer " + accessToken);
    }

    private User getTestUser() {
        User user = new User();
        user.setFirstName(TEST_FIRST_NAME);
        user.setLastName(TEST_LAST_NAME);
        user.setEmail(TEST_EMAIL);
        user.setExternalSubjectId(TEST_EXTERNAL_SUBJECT_ID);

        return user;
    }
}
