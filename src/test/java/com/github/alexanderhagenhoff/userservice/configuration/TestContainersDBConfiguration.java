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

    @Bean
    public PostgreSQLContainer<?> postgreSQLContainer() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>(
                DockerImageName.parse("postgres:16.3")
                        .asCompatibleSubstituteFor("postgres"))
                .withDatabaseName("user-service")
                .withUsername("integrationtestuser")
                .withPassword("integrationtestpass");

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
