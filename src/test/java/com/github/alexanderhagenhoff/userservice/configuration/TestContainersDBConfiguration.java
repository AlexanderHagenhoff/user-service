package com.github.alexanderhagenhoff.userservice.configuration;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;

@TestConfiguration
@Profile("integrationtest")
public class TestContainersDBConfiguration {

    private static final String POSTGRES_VERSION = "postgres:16.3";
    private static final String DATABASE_NAME = "user-service";
    private static final String DATABASE_USER_NAME = "integrationtestuser";
    private static final String DATABASE_PASSWORD = "integrationtestpass";
    private static final String DATABASE_TYPE = "postgres";

    @Bean
    public PostgreSQLContainer<?> postgreSQLContainer() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>(
                DockerImageName.parse(POSTGRES_VERSION)
                        .asCompatibleSubstituteFor(DATABASE_TYPE))
                .withDatabaseName(DATABASE_NAME)
                .withUsername(DATABASE_USER_NAME)
                .withPassword(DATABASE_PASSWORD);

        container.start();
        return container;
    }

    @Bean
    @Primary
    public DataSource dataSource(PostgreSQLContainer<?> postgreSQLContainer) {
        return DataSourceBuilder.create()
                .url(postgreSQLContainer.getJdbcUrl())
                .username(postgreSQLContainer.getUsername())
                .password(postgreSQLContainer.getPassword())
                .build();
    }
}
