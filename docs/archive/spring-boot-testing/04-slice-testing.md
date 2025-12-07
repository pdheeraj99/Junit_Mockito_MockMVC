# Spring Boot Slice Testing Guide 🍰

**Slice Testing** focuses on testing *one layer* of your application in isolation, rather than the entire stack. This is faster and more focused than full `@SpringBootTest`.

---

## 1. Repository Layer (`@DataJpaTest`)
Tests your JPA mappings and Database interactions (SQL) without loading Controllers or Services.

### Key Concepts
- **@DataJpaTest**: Loads only JPA components (`EntityManager`, `DataSource`).
- **Transactional**: Rolled back automatically after each test.
- **Fast**: Does not start the web server.

### Enterprise Pattern: Use Real DB (Testcontainers)
By default, `@DataJpaTest` uses H2 (in-memory). For enterprise apps, we often want to test with the **REAL** database (MySQL/Postgres) using Testcontainers.

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE) // Disable H2 replacement
class UserRepositoryTest extends AbstractContainerBaseTest { // Extend your Container setup
    
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindActiveUsers() {
        // ...
    }
}
```

---

## 2. Web Layer (`@WebMvcTest`)
Tests your Controllers, JSON serialization, and HTTP logic without loading the Service or Database.

### Key Concepts
- **@WebMvcTest(UserController.class)**: Loads only the managed Controller.
- **@MockBean**: You **MUST** mock the Service layer, as it's not loaded.
- **MockMVC**: Auto-configured for you.

```java
@WebMvcTest(UserController.class)
class UserControllerWebLayerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService; // Dependency replaced with Mockito Mock

    @Test
    void shouldReturn200() throws Exception {
        // Mock the service
        given(userService.findById(1L)).willReturn(Optional.of(user));

        // Hit the endpoint
        mockMvc.perform(get("/api/users/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("Test User"));
    }
}
```

---

## Summary Comparison

| Feature | @SpringBootTest | @DataJpaTest | @WebMvcTest |
|---------|-----------------|--------------|-------------|
| **Scope** | Full App Context | JPA/DB only | Controller only |
| **Speed** | Slow (Starts everything) | Fast | Very Fast |
| **Use Case**| E2E Integration | Complex SQL/Queries | API Contract/Validation |
| **Mocks** | Optional | N/A | Essential (@MockBean) |
