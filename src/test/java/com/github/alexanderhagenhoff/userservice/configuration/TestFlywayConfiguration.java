package com.github.alexanderhagenhoff.userservice.configuration;

import org.flywaydb.core.Flyway;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

import static com.github.alexanderhagenhoff.userservice.TestProfile.INTEGRATION_TEST;

@TestConfiguration
@Profile(INTEGRATION_TEST)
public class TestFlywayConfiguration {

    @Bean
    public Flyway getFlyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .load();
    }
}
