package com.github.alexanderhagenhoff.userservice.controller;

import com.github.alexanderhagenhoff.userservice.TokenUtils;
import com.github.alexanderhagenhoff.userservice.entity.User;
import com.github.alexanderhagenhoff.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.github.alexanderhagenhoff.userservice.TestProfile.INTEGRATION_TEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(INTEGRATION_TEST)
public class UserControllerIT {

    private static final String USERS_ENDPOINT = "/users";
    private static final String USERS_BY_ID = "/users/{id}";
    private static final String USERS_BY_EMAIL = "/users/email?email={email}";
    private static final String TEST_EMAIL = "test_email";
    private static final String TEST_FIRST_NAME = "test_first_name";
    private static final String TEST_LAST_NAME = "test_last_name";

    private static final String CREATE_JSON_TEMPLATE = """
            {
                "firstName": "%s",
                "lastName": "%s",
                "email": "%s"
            }
            """;
    private static final String EXPECTED_JSON_TEMPLATE = """
            {
                "id": "%s",
                "firstName": "%s",
                "lastName": "%s",
                "email": "%s"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenUtils tokenUtils;

    private UUID userId;

    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        User user = new User();
        user.setFirstName(TEST_FIRST_NAME);
        user.setLastName(TEST_LAST_NAME);
        user.setEmail(TEST_EMAIL);
        user = userRepository.save(user);
        userId = user.getId();

        jwtToken = tokenUtils.getJwtToken("dummy_client_id_for_test", "dummy_client_secret_for_test");
        userRepository.deleteAll();
    }

    @Test
    void getUserByIdShouldReturnUserWhenUserExists() throws Exception {
        String expectedJson = EXPECTED_JSON_TEMPLATE.formatted(userId, TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL);
        mockMvc.perform(
                        get(USERS_BY_ID, userId)
                                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void getUserByIdShouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        mockMvc.perform(
                        get(USERS_BY_ID, UUID.randomUUID())
                                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserByEmailShouldReturnUserWhenUserExists() throws Exception {
        mockMvc.perform(get(USERS_BY_EMAIL, TEST_EMAIL)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().json(EXPECTED_JSON_TEMPLATE.formatted(userId, TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL)));
    }

    @Test
    void getUserByEmailShouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get(USERS_BY_EMAIL, "notfound@example.com")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUserShouldReturnCreatedWhenUserIsValid() throws Exception {
        String newUserEmail = "new_email";
        String newUserJson = CREATE_JSON_TEMPLATE.formatted("New", "User", newUserEmail);

        mockMvc.perform(post(USERS_ENDPOINT)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isCreated());
    }

    @Test
    void createUser_ShouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        String duplicateUserJson = EXPECTED_JSON_TEMPLATE.formatted(UUID.randomUUID(), TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL);

        mockMvc.perform(post(USERS_ENDPOINT)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(APPLICATION_JSON)
                        .content(duplicateUserJson))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteUserShouldReturnNoContentWhenUserExists() throws Exception {
        mockMvc.perform(delete(USERS_BY_ID, userId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUserShouldReturnNoContentWhenUserDoesNotExist() throws Exception {
        mockMvc.perform(delete(USERS_BY_ID, UUID.randomUUID())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());
    }
}
