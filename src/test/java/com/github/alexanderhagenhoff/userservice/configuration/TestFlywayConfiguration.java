package com.github.alexanderhagenhoff.userservice.configuration;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

import static com.github.alexanderhagenhoff.userservice.TestProfile.INTEGRATION_TEST;

@TestConfiguration
@Profile(INTEGRATION_TEST)
public class TestFlywayConfiguration {

    private static final String SCHEMA_NAME = "user-service";

    @Bean
    public Flyway getFlyway(DataSource dataSource) {
        FluentConfiguration flywayConfiguration = Flyway.configure()
                .dataSource(dataSource)
                .schemas(SCHEMA_NAME)
                .defaultSchema(SCHEMA_NAME);

        Flyway flyway = flywayConfiguration.load();
        flyway.migrate();

        return flyway;
    }
}
