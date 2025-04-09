package com.github.alexanderhagenhoff.userservice.configuration.oauth2;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Properties;

import static com.github.alexanderhagenhoff.userservice.TestProfile.INTEGRATION_TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles(INTEGRATION_TEST)
class ClientConfigurationPropertiesIT{

    @Autowired
    private ClientConfigurationProperties clientConfig;

    @Test
    void shouldLoadClientsFromTestProperties() throws IOException {
        Properties properties = clientConfig.loadClients();

        assertNotNull(properties, "Properties should not be null");
        assertEquals(2, properties.size(), "Should load 2 test clients");
        assertEquals("test-secret-1", properties.getProperty("test-client-1"));
        assertEquals("test-secret-2", properties.getProperty("test-client-2"));
    }
}
