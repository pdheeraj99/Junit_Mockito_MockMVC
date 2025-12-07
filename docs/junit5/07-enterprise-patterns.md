# 🏢 JUnit 5 Enterprise Patterns

> **Mawa, real-world projects lo ela tests raastaro - best practices anni ikkada unnay!**

---

## 🎯 Enterprise Testing Principles

```
1. Tests should be FAST (unit tests < 100ms)
2. Tests should be ISOLATED (no shared state)
3. Tests should be REPEATABLE (same result every time)
4. Tests should be SELF-VALIDATING (pass/fail clear)
5. Tests should be TIMELY (write with code)

= F.I.R.S.T Principles
```

---

## 📊 Test Organization Pattern

### Real-World Project Structure:

```
src/test/java/com/company/
├── unit/                    ← Fast, isolated tests
│   ├── service/
│   │   ├── UserServiceTest.java
│   │   └── OrderServiceTest.java
│   └── util/
│       └── ValidationUtilTest.java
│
├── integration/             ← Slower, DB tests
│   ├── repository/
│   │   └── UserRepositoryIT.java
│   └── api/
│       └── UserControllerIT.java
│
├── e2e/                     ← Slowest, full flow
│   └── UserRegistrationE2E.java
│
└── common/                  ← Shared utilities
    ├── TestData.java
    └── AbstractIntegrationTest.java
```

---

## 💻 Pattern 1: Test Data Builders

### ❌ Bad: Scattered test data
```java
@Test void test1() {
    User user = new User("John", "john@test.com", "pass123", true, ...);
}

@Test void test2() {
    User user = new User("Jane", "jane@test.com", "pass456", false, ...);
}
```

### ✅ Good: Builder pattern
```java
public class TestUserBuilder {
    private String name = "Default User";
    private String email = "default@test.com";
    private String password = "password123";
    private boolean active = true;
    
    public static TestUserBuilder aUser() {
        return new TestUserBuilder();
    }
    
    public TestUserBuilder withName(String name) {
        this.name = name;
        return this;
    }
    
    public TestUserBuilder withEmail(String email) {
        this.email = email;
        return this;
    }
    
    public TestUserBuilder inactive() {
        this.active = false;
        return this;
    }
    
    public User build() {
        return new User(name, email, password, active);
    }
}

// Usage - Clean!
@Test void testActiveUser() {
    User user = aUser().withName("John").build();
}

@Test void testInactiveUser() {
    User user = aUser().inactive().build();
}
```

---

## 💻 Pattern 2: Custom Assertions

### Problem: Repetitive assertions
```java
@Test void test1() {
    User user = service.create(...);
    assertNotNull(user);
    assertNotNull(user.getId());
    assertTrue(user.isActive());
    assertNotNull(user.getCreatedAt());
}

@Test void test2() {
    User user = service.find(...);
    assertNotNull(user);          // Same checks!
    assertNotNull(user.getId());
    assertTrue(user.isActive());
}
```

### Solution: Domain-specific assertions
```java
public class UserAssertions {
    
    public static void assertValidUser(User user) {
        assertAll("Valid User",
            () -> assertNotNull(user, "User should not be null"),
            () -> assertNotNull(user.getId(), "ID should be set"),
            () -> assertFalse(user.getName().isBlank(), "Name not blank"),
            () -> assertTrue(user.getEmail().contains("@"), "Valid email")
        );
    }
    
    public static void assertUserEquals(User expected, User actual) {
        assertAll("User equality",
            () -> assertEquals(expected.getName(), actual.getName()),
            () -> assertEquals(expected.getEmail(), actual.getEmail())
        );
    }
}

// Usage
@Test void testCreateUser() {
    User user = service.create("John", "john@test.com");
    UserAssertions.assertValidUser(user);  // One line!
}
```

---

## 💻 Pattern 3: Test Fixtures

### @TempDir - Temporary file testing
```java
class FileServiceTest {
    
    @TempDir
    Path tempDir;  // JUnit creates & cleans up!
    
    @Test
    void shouldWriteFile() throws IOException {
        Path file = tempDir.resolve("test.txt");
        fileService.write(file, "Hello");
        
        assertTrue(Files.exists(file));
        assertEquals("Hello", Files.readString(file));
    }
    
    @Test
    void shouldReadFile() throws IOException {
        Path file = tempDir.resolve("data.txt");
        Files.writeString(file, "Test Data");
        
        String content = fileService.read(file);
        assertEquals("Test Data", content);
    }
}
```

---

## 💻 Pattern 4: Test Ordering

### Controlled execution order
```java
@TestMethodOrder(OrderAnnotation.class)
class OrderedTest {
    
    private static Long createdId;
    
    @Test
    @Order(1)
    void step1_create() {
        createdId = service.create("Test").getId();
        assertNotNull(createdId);
    }
    
    @Test
    @Order(2)
    void step2_read() {
        User user = service.findById(createdId).orElseThrow();
        assertEquals("Test", user.getName());
    }
    
    @Test
    @Order(3)
    void step3_delete() {
        service.delete(createdId);
        assertTrue(service.findById(createdId).isEmpty());
    }
}
```

---

## 💻 Pattern 5: Extension Model

### Custom test extension
```java
// Create extension
public class LoggingExtension implements BeforeEachCallback, AfterEachCallback {
    
    @Override
    public void beforeEach(ExtensionContext context) {
        System.out.println("▶️ Starting: " + context.getDisplayName());
    }
    
    @Override
    public void afterEach(ExtensionContext context) {
        System.out.println("✅ Finished: " + context.getDisplayName());
    }
}

// Use extension
@ExtendWith(LoggingExtension.class)
class MyTest {
    @Test void test1() { }  // Logs before and after
    @Test void test2() { }  // Logs before and after
}
```

### Timing extension
```java
public class TimingExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
    
    private static final String START_TIME = "start_time";
    
    @Override
    public void beforeTestExecution(ExtensionContext ctx) {
        ctx.getStore(GLOBAL).put(START_TIME, System.currentTimeMillis());
    }
    
    @Override
    public void afterTestExecution(ExtensionContext ctx) {
        long start = ctx.getStore(GLOBAL).get(START_TIME, Long.class);
        long duration = System.currentTimeMillis() - start;
        System.out.println("⏱️ " + ctx.getDisplayName() + ": " + duration + "ms");
    }
}
```

---

## 💻 Pattern 6: Parallel Execution

### Enable parallel tests
```properties
# junit-platform.properties
junit.jupiter.execution.parallel.enabled=true
junit.jupiter.execution.parallel.mode.default=concurrent
junit.jupiter.execution.parallel.mode.classes.default=concurrent
```

### Thread-safe tests
```java
@Execution(ExecutionMode.CONCURRENT)  // Run in parallel
class ParallelTest {
    
    // ⚠️ Each test must be independent!
    @Test void test1() { }
    @Test void test2() { }
    @Test void test3() { }
}

// Force sequential for stateful tests
@Execution(ExecutionMode.SAME_THREAD)
class SequentialTest { }
```

---

## 💻 Pattern 7: CI/CD Configuration

### Maven Surefire configuration
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <!-- Run unit tests only by default -->
        <groups>unit</groups>
        <excludedGroups>integration,slow</excludedGroups>
    </configuration>
</plugin>

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <configuration>
        <!-- Run integration tests -->
        <groups>integration</groups>
    </configuration>
</plugin>
```

### GitHub Actions workflow
```yaml
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Unit Tests
        run: mvn test -Dgroups="unit"
      
      - name: Integration Tests
        run: mvn verify -Dgroups="integration"
```

---

## 📊 Test Pyramid Summary

```
          ╱╲
         ╱  ╲     E2E Tests (Few)
        ╱────╲    - @Tag("e2e")
       ╱      ╲   - Selenium, Testcontainers
      ╱────────╲  
     ╱          ╲  Integration Tests (Some)
    ╱────────────╲ - @Tag("integration")
   ╱              ╲- @SpringBootTest
  ╱────────────────╲
 ╱                  ╲ Unit Tests (Many)
╱────────────────────╲- @Tag("unit")
                       - @ExtendWith(MockitoExtension.class)
```

---

## 🎯 Quick Reference

| Pattern | When to Use |
|---------|-------------|
| Test Builders | Complex domain objects |
| Custom Assertions | Repeated validation logic |
| @TempDir | File I/O testing |
| @Order | Integration test sequences |
| Extensions | Cross-cutting concerns |
| Parallel | Speed up test suite |
| Tags | CI/CD pipeline stages |

---

## 😂 Final Wisdom

```
"Tests are like bodyguards 💪
- Unit tests: Close protection (fast, everywhere)
- Integration tests: Perimeter check (slower, strategic)
- E2E tests: Full security sweep (slowest, rare)

More guards = More confidence, but also More cost!"
```

---

## 🔗 Related Topics

- [All JUnit Topics](./README.md) - Full index
- [Mockito](../mockito/README.md) - Mocking for unit tests
- [Spring Testing](../spring-testing/README.md) - Integration tests
