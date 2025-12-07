# Spring Boot Testing Annotations Glossary

## 1. Setup & Configuration

| Annotation | Description | Enterprise Use Case |
|------------|-------------|---------------------|
| `@SpringBootTest` | Loads complete Spring context (DB, web, security). | Integration testing E2E flows. |
| `@TestConfiguration` | Defines beans ONLY for testing. | Overriding a real Bean (e.g., specific TimeService) for tests. |
| `@TestPropertySource` | Overrides `application.properties`. | Setting `server.port=0` or `feature.flag=true`. |
| `@ActiveProfiles("test")` | Activates `application-test.properties`. | Using H2 for unit tests, Testcontainers for integration. |
| `@DynamicPropertySource` | Injects properties at runtime. | Connecting Spring to a random port Docker container. |

## 2. Web Layer

| Annotation | Description | Enterprise Use Case |
|------------|-------------|---------------------|
| `@AutoConfigureMockMvc` | Configures `MockMvc` bean. | Testing Controllers within `@SpringBootTest`. |
| `@WebMvcTest` | Slices context to ONLY Web layer. | Fast unit testing of Controllers (must mock Services). |
| `@MockBean` | Mocks a Bean in the Spring Context. | Replacing `EmailService` with a Mock in a Controller test. |
| `@SpyBean` | Spies on a Bean in the Spring Context. | Verifying `AuditService` was called while keeping real behavior. |

## 3. Database & Transactions

| Annotation | Description | Enterprise Use Case |
|------------|-------------|---------------------|
| `@DataJpaTest` | Slices context to ONLY JPA layer. | Testing complex custom Queries / Repository methods. |
| `@Transactional` | Rolls back DB changes after test. | Keeping DB clean for next test methods. |
| `@AutoConfigureTestDatabase` | Configures test DB. | `replace=NONE` to use real DB instead of H2. |
| `@Sql` | Runs SQL script before test. | Seeding reference data (Countries, Currencies). |

## 4. Testcontainers

| Annotation | Description | Enterprise Use Case |
|------------|-------------|---------------------|
| `@Testcontainers` | JUnit 5 specific integration. | Managing container lifecycle automatically. |
| `@Container` | Marks a container field. | Defining `MySQLContainer` or `KafkaContainer`. |
| `@ServiceConnection` | (Boot 3.1+) Auto-configures connection. | Removing need for `@DynamicPropertySource`. |

## 5. JUnit 5 (Quick Ref)

| Annotation | Description |
|------------|-------------|
| `@ExtendWith(MockitoExtension.class)` | Enables Mockito annotations. |
| `@DisplayName` | Readable test names. |
| `@Nested` | Grouping tests. |
| `@Tag` | Filtering (e.g., "slow", "integration"). |
