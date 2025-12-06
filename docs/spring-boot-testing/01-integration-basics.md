# Spring Boot Integration Testing & Testcontainers

## 1. What is Integration Testing?
Unit tests (Mockito) test **classes in isolation**.
Integration tests verify that **different layers working together** (e.g., Service + Repository + Database).

## 2. Key Annotations (`@SpringBootTest`)
In Unit Tests, we didn't start Spring. In Integration Tests, we MUST start Spring.

- **`@SpringBootTest`**:
  - Bootstraps the entire Spring Application Context.
  - Scans for all `@Component`, `@Service`, `@Repository`, `@Controller`.
  - Simulates a real running application.
  - `webEnvironment = RANDOM_PORT`: Starts a real Tomcat server on a random port (avoid conflict with 8080).

---

## 3. Testcontainers: The "Real World" Standard
Instead of mocking the database (H2) or requiring a pre-installed DB, we use **Docker Containers**.

### How it works:
1. **JUnit starts**: Finds `@Testcontainers` annotation.
2. **Container creates**: Pulls `mysql:8.0` image and starts a container.
3. **Spring starts**: Wired to connect to this temporary container.
4. **Tests run**: Real SQL inserted into real MySQL.
5. **Cleanup**: Container is destroyed (via Ryuk sidecar).

### Code Pattern:
```java
@SpringBootTest
@Testcontainers
class MyIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        // Tells Spring: "Connect to the dynamic port of this container"
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
    }
}
```

---

## 4. Concepts Explained
- **`@Container`**: JUnit 5 extension to manage container lifecycle.
- **`@DynamicPropertySource`**: Powerful feature to inject properties (like DB URL/Password) *after* the container starts but *before* Spring starts.
- **`Singleton Container Pattern`**: Defining the container as `static` inside a Base Class ensures it starts **ONCE** for all tests, saving massive time (vs starting per test method).

## 5. MockMVC (Next Step)
While `@SpringBootTest` tests the backend, **MockMVC** allows us to test the **Controller Layer (API)** without starting a full HTTP server, or in conjunction with it to verify full end-to-end flows.

---

## 6. Troubleshooting Common Issues 🛠️

### Windows: "Could not find a valid Docker environment" (Status 400)
If you see `BadRequestException` connecting to `npipe:////./pipe/docker_cli`:
1.  **Expose Daemon (Fix #1)**: Docker Desktop > Settings > General > Check "Expose daemon on tcp://localhost:2375".
2.  **Reset Docker**: Docker Desktop > Troubleshoot > Reset to factory defaults.
3.  **Check Permissions**: Ensure your user has access to Docker groups.
4.  **Java Version**: Ensure you are using JDK 17+ compatible with Testcontainers.
