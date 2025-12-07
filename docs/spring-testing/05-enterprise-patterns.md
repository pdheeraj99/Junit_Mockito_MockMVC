# 🏢 Spring Testing Enterprise Patterns

> **Mawa, real enterprise projects lo testing best practices - all in one place!**

---

## 📊 Test Organization

```
src/test/java/com/company/
├── unit/                    ← Fast, no Spring
│   ├── service/
│   └── util/
├── integration/             ← Spring + Testcontainers
│   ├── api/
│   └── repository/
├── e2e/                     ← Full flow tests
└── common/                  ← Shared utilities
    ├── AbstractIntegrationTest.java
    └── TestDataBuilder.java
```

---

## 🎯 Pattern 1: Abstract Base Test

```java
// Reusable across all integration tests
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
public abstract class AbstractIntegrationTest {
    
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");
    
    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }
    
    @Autowired
    protected MockMvc mockMvc;
    
    @Autowired
    protected ObjectMapper objectMapper;
    
    // Helper method
    protected String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
}
```

---

## 🎯 Pattern 2: Test Data Builders

```java
public class TestData {
    
    public static User validUser() {
        return new User("John", "john@test.com", "password123");
    }
    
    public static User invalidUser() {
        return new User("", "invalid", "123");
    }
    
    public static UserRequest createUserRequest() {
        return new UserRequest("John", "john@test.com", "Password123!");
    }
}

// Usage
@Test
void test() {
    User user = TestData.validUser();
    // ...
}
```

---

## 🎯 Pattern 3: @Sql for Test Data

```java
@SpringBootTest
@Sql(scripts = "/test-data.sql", executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = "/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
class UserServiceTest {
    
    @Test
    void testWithPrepopulatedData() {
        // Data from test-data.sql is available!
        User user = service.findById(1L).orElseThrow();
        assertEquals("TestUser", user.getName());
    }
}
```

---

## 🎯 Pattern 4: Test Profiles

```yaml
# application-test.yml
spring:
  jpa:
    show-sql: true
  mail:
    host: localhost
    port: 3025  # GreenMail for email testing
```

```java
@SpringBootTest
@ActiveProfiles("test")
class MyTest { }
```

---

## 🎯 Pattern 5: Mock External Services

```java
@SpringBootTest
class PaymentTest {
    
    @MockBean
    PaymentGateway paymentGateway;  // Don't call real payment!
    
    @MockBean
    EmailService emailService;  // Don't send real emails!
    
    @Test
    void testPayment() {
        when(paymentGateway.charge(any())).thenReturn(receipt);
        
        orderService.placeOrder(order);
        
        verify(emailService).sendConfirmation(any());
    }
}
```

---

## 🎯 Pattern 6: CI/CD Configuration

### Maven Surefire:
```xml
<plugin>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <groups>unit</groups>  <!-- Run unit only by default -->
    </configuration>
</plugin>

<plugin>
    <artifactId>maven-failsafe-plugin</artifactId>
    <configuration>
        <groups>integration</groups>
    </configuration>
</plugin>
```

### GitHub Actions:
```yaml
- name: Unit Tests
  run: mvn test -Dgroups=unit

- name: Integration Tests
  run: mvn verify -Dgroups=integration
```

---

## 📊 Test Pyramid

```
      Few E2E Tests
     /              \
    /  Integration   \
   /    Tests         \
  /                    \
 /   Many Unit Tests    \
/________________________\

Unit: Fast, isolated, many
Integration: Slower, realistic, some
E2E: Slowest, full system, few
```

---

## 🎯 Quick Reference

| Type | Annotation | Speed | DB |
|------|------------|-------|-----|
| Unit | `@ExtendWith(Mockito)` | ⚡⚡⚡ | None |
| Web Slice | `@WebMvcTest` | ⚡⚡ | None |
| DB Slice | `@DataJpaTest` | ⚡⚡ | H2 |
| Integration | `@SpringBootTest` + Testcontainers | ⚡ | Real |

---

## 😂 Final Wisdom

```
"Test like you deploy!"
- Local: Unit + fast slices
- PR: + Integration tests
- Deploy: + E2E smoke tests

"Mock what you don't own!"
- Mock: External APIs, Email, Payment
- Real: Your DB, Your services
```

---

## 🔗 Related Topics

- [Annotations](./01-annotations.md)
- [Testcontainers](./04-testcontainers.md)
- [JUnit Patterns](../junit5/07-enterprise-patterns.md)
