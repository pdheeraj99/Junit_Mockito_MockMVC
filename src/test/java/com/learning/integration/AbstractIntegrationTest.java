package com.learning.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ ABSTRACT INTEGRATION TEST - The "Real World" Foundation ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 * 
 * This class handles the DOCKER container setup for MySQL.
 * All integration tests should extend this class.
 * 
 * CONCEPTS:
 * 1. @SpringBootTest: Loads the full Spring application context (Controller,
 * Service, Repository)
 * 2. @Testcontainers: Manages lifecycle of Docker containers (start/stop)
 * 3. @Container: Marks a field as a container to be managed
 * 4. @DynamicPropertySource: Overrides properties (like DB URL) with values
 * from the running container
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest extends AbstractContainerBaseTest {
    // Inherits container setup from AbstractContainerBaseTest
    // Adds @SpringBootTest for Full Context
}
