package com.learning.integration;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ ABSTRACT CONTAINER BASE TEST - Shared Docker Infrastructure ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 * 
 * This class handles the DOCKER container setup for MySQL.
 * It is AGNOSTIC of Spring Boot Test Slices.
 * 
 * Usages:
 * 1. Full Integration Test: Extend `AbstractIntegrationTest` (which extends
 * this + adds @SpringBootTest)
 * 2. Repository Slice Test: Extend this + add @DataJpaTest
 */
@Testcontainers
public abstract class AbstractContainerBaseTest {

    // Define MySQL Container
    @Container
    @SuppressWarnings("resource")
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);

        // Ensure Hibernate creates tables
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }
}
