# 🎯 Integration Testing - Complete Beginner's Guide (Tenglish)

> **Mawa, ee guide okka sari chadivthe Integration Testing mottam artham avthundi!**
> Ground zero nunchi start cheddam. No prior knowledge assumed.

---

## 📚 Table of Contents

1. [What is Integration Testing?](#1-what-is-integration-testing)
2. [Why Do We Need It?](#2-why-do-we-need-it)
3. [Types of Tests - Simple Comparison](#3-types-of-tests---simple-comparison)
4. [Integration Testing ki Required Dependencies](#4-integration-testing-ki-required-dependencies)
5. [All Annotations Explained](#5-all-annotations-explained---complete-reference)
6. [4 Types of Integration Tests](#6-4-types-of-integration-tests)
7. [Your Project Code - See It In Action](#7-your-project-code---see-it-in-action)
8. [Quick Reference Card](#8-quick-reference-card)

---

## 1. What is Integration Testing?

### Simple Definition:

> **Integration Testing =** Multiple components ni kalisi REAL ga test cheyadam

### Real-World Analogy:

```
🏠 Building a House:

Unit Test = Oka brick strong ga unda check cheyadam
Integration Test = Bricks + Cement + Foundation anni kalisi wall strong ga unda check cheyadam
E2E Test = Complete house lo family comfortable ga unda check cheyadam
```

### In Code Terms:

```
Unit Test:       UserService.java - alone test
Integration Test: UserController → UserService → UserRepository → Database (together)
```

---

## 2. Why Do We Need It?

### The Problem:

```java
// Unit Test - Everything is MOCKED
@Test
void unitTest() {
    when(mockRepository.save(any())).thenReturn(user);  // FAKE!
    
    User result = userService.register(user);
    
    // This passes, but...
    // What if the REAL database rejects the data?
    // What if there's a constraint violation?
    // Unit test won't catch these!
}
```

### The Solution - Integration Test:

```java
// Integration Test - REAL Database
@Test
void integrationTest() {
    // No mocking! Real database connection
    User result = userService.register(user);
    
    // This ACTUALLY saves to database
    // If there's any DB issue, this test WILL FAIL
    assertTrue(userRepository.existsByEmail(user.getEmail()));
}
```

### Key Point:

| ❌ Unit Test | ✅ Integration Test |
|--------------|---------------------|
| Mocked DB | Real DB (Docker) |
| Fast but incomplete | Slower but thorough |
| Tests logic only | Tests actual connections |
| Doesn't catch DB errors | Catches everything |

---

## 3. Types of Tests - Simple Comparison

```
┌──────────────────────────────────────────────────────────────────────┐
│                        TESTING PYRAMID                                │
├──────────────────────────────────────────────────────────────────────┤
│                                                                       │
│    △ E2E Tests (Few)                                                 │
│   ╱ ╲   - Selenium, Cypress                                          │
│  ╱   ╲  - Full browser testing                                       │
│ ╱     ╲ - Very slow, expensive                                       │
│─────────                                                              │
│                                                                       │
│    ████ Integration Tests (Some)                                     │
│    ████   - @SpringBootTest + Testcontainers                         │
│    ████   - Real DB, Real services                                   │
│    ████   - Medium speed                                             │
│                                                                       │
│    ████████████ Unit Tests (Many)                                    │
│    ████████████   - @Mock, @InjectMocks                              │
│    ████████████   - Everything mocked                                │
│    ████████████   - Super fast                                       │
│                                                                       │
└──────────────────────────────────────────────────────────────────────┘
```

---

## 4. Integration Testing ki Required Dependencies

### pom.xml lo add cheyyali:

```xml
<!-- 1. Spring Boot Test - Already included with spring-boot-starter-test -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- 2. Testcontainers - For Real Database in Docker -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <scope>test</scope>
</dependency>

<!-- 3. Testcontainers MySQL (or PostgreSQL) -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>mysql</artifactId>
    <scope>test</scope>
</dependency>

<!-- 4. Testcontainers JUnit 5 Integration -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

### What Each Dependency Does:

| Dependency | Purpose |
|------------|---------|
| `spring-boot-starter-test` | JUnit 5, MockMvc, Assertions - anni include |
| `testcontainers` | Docker containers manage cheyyad |
| `mysql` (or `postgresql`) | Specific DB container support |
| `junit-jupiter` | Testcontainers + JUnit 5 integration |

---

## 5. All Annotations Explained - COMPLETE Reference

### 🏷️ @SpringBootTest

```java
@SpringBootTest
class MyTest { }
```

**What it does:**
- Mottam Spring application context load chestundi
- All beans, configurations, properties - anni ready

**When to use:**
- Full integration test kavali appudu
- Multiple layers test cheyyali appudu

**Options:**

```java
// Option 1: No web server (fastest for service tests)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)

// Option 2: Mock web environment (for MockMvc tests)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)  // This is DEFAULT

// Option 3: Real web server on random port (for real HTTP tests)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
```

**Positives:**
- ✅ Full application context - real-world scenario
- ✅ All beans autowired available

**Negatives:**
- ❌ Slow - full context load
- ❌ Heavy - more memory usage

---

### 🏷️ @AutoConfigureMockMvc

```java
@SpringBootTest
@AutoConfigureMockMvc
class MyTest {
    @Autowired
    private MockMvc mockMvc;  // Now available!
}
```

**What it does:**
- MockMvc bean ni create chestundi
- Real HTTP request laga simulate chestunam

**When to use:**
- API endpoints test cheyyali appudu
- HTTP status codes, JSON responses verify cheyyali appudu

**Positives:**
- ✅ No real server needed
- ✅ Fast - no network overhead
- ✅ Easy to write JSON assertions

**Negatives:**
- ❌ Not 100% real HTTP (some edge cases miss avvochu)

---

### 🏷️ @Testcontainers

```java
@Testcontainers
class MyTest { }
```

**What it does:**
- Docker containers ni manage chestundi
- Test start appude container start, test end appude stop

**When to use:**
- Real database kavali appudu
- Real Redis, Kafka, etc. kavali appudu

**Pre-requisite:**
- ⚠️ Docker must be running on your machine!

**Positives:**
- ✅ Real database - production-like testing
- ✅ Automatic cleanup

**Negatives:**
- ❌ Docker required
- ❌ Slower than H2/in-memory

---

### 🏷️ @Container

```java
@Container
static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
    .withDatabaseName("testdb")
    .withUsername("test")
    .withPassword("test");
```

**What it does:**
- Ee variable oka Docker container ani mark chestundi
- @Testcontainers tho kalisi panichestundi

**When to use:**
- Any external service ni dockerized ga run cheyyali appudu

**Why `static`?**
- Class level lo once create, all tests share
- Each test ki new container unte very slow avthundi

**Positives:**
- ✅ Easy container declaration
- ✅ Fluent API - configuration easy

**Negatives:**
- ❌ First test slow (container start time)

---

### 🏷️ @DynamicPropertySource

```java
@DynamicPropertySource
static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", mysql::getJdbcUrl);
    registry.add("spring.datasource.username", mysql::getUsername);
    registry.add("spring.datasource.password", mysql::getPassword);
}
```

**What it does:**
- Docker container random port lo start avthundi
- Aa port Spring properties ki dynamically inject chestundi

**Why needed?**

```
Problem:
- MySQL container port 33060 lo start avvachu
- Next time 33061 lo avvachu (random!)
- application.properties lo hardcode cheyyalekapotam

Solution:
- @DynamicPropertySource container start ayyaka
- Actual port teesukoni Spring ki cheptundi
```

**When to use:**
- Testcontainers tho always use cheyyali

**Positives:**
- ✅ No hardcoded ports
- ✅ Works with any container configuration

**Negatives:**
- ❌ Must be static method

---

### 🏷️ @DataJpaTest

```java
@DataJpaTest
class UserRepositoryTest { }
```

**What it does:**
- Only JPA related beans load chestundi
- Entity, Repository, EntityManager - only these

**When to use:**
- Repository layer only test cheyyali appudu
- Custom queries correct ga work avthunnaya check cheyyali appudu

**What it DOES NOT load:**
- ❌ Controllers
- ❌ Services
- ❌ Web layer

**Positives:**
- ✅ Super fast - minimal beans
- ✅ @Transactional by default - auto rollback

**Negatives:**
- ❌ By default H2 use chestundi (need to override for real DB)

**For Real DB:**
```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)  // Don't replace with H2
@Testcontainers
class UserRepositoryTest { }
```

---

### 🏷️ @AutoConfigureTestDatabase

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class MyTest { }
```

**What it does:**
- Default ga Spring H2 (in-memory) vadutundi tests ki
- `replace = NONE` ante "don't replace, use my configured DB"

**Options:**

| Value | Meaning |
|-------|---------|
| `Replace.ANY` | Replace with embedded DB (default) |
| `Replace.NONE` | Use configured datasource (Testcontainer) |
| `Replace.AUTO_CONFIGURED` | Replace only auto-configured datasource |

**When to use:**
- Real database test cheyyali appudu
- @DataJpaTest + Testcontainers combine cheyyali appudu

---

### 🏷️ @WebMvcTest

```java
@WebMvcTest(UserController.class)
class UserControllerTest { }
```

**What it does:**
- Only specified controller load chestundi
- MVC infrastructure (MockMvc) ready

**What it loads:**
- ✅ Controller
- ✅ @ControllerAdvice
- ✅ Filters
- ✅ Converters

**What it DOES NOT load:**
- ❌ Service layer
- ❌ Repository layer
- ❌ Database

**When to use:**
- Controller logic only test cheyyali appudu
- HTTP status codes, validations matrame check cheyyali appudu

**Positives:**
- ✅ Fastest - only web layer
- ✅ No DB needed

**Negatives:**
- ❌ Must @MockBean all dependencies

---

### 🏷️ @MockBean

```java
@SpringBootTest
class MyTest {
    @MockBean
    private EmailService emailService;  // Replaced in Spring context!
}
```

**What it does:**
- Spring context lo aa bean ni mock tho replace chestundi
- @Mock (Mockito) tho different!

**@Mock vs @MockBean:**

| @Mock | @MockBean |
|-------|-----------|
| Mockito level | Spring level |
| Unit tests lo | Integration tests lo |
| Manual injection | Auto-injected into beans |

**When to use:**
- External services mock cheyyali appudu (Email, SMS, Payment)
- Third-party APIs mock cheyyali appudu
- Services that should NOT be called in tests

**Positives:**
- ✅ Seamlessly integrates with Spring
- ✅ Auto-injected everywhere that bean is used

**Negatives:**
- ❌ Slower than @Mock (Spring context manipulation)
- ❌ Creates new Spring context if different mocks

---

### 🏷️ @Transactional (in tests)

```java
@Test
@Transactional
void myTest() {
    userRepository.save(user);
    // After test, this INSERT is ROLLED BACK!
}
```

**What it does:**
- Test end lo all DB changes rollback
- Each test gets clean database

**When to use:**
- Database state clean ga unchukovalante
- Tests isolated untayi - one test affects other vaddu ante

**Positives:**
- ✅ No manual cleanup needed
- ✅ Tests truly independent

**Negatives:**
- ❌ Hides some lazy-loading issues
- ❌ Transaction behavior slightly different from production

---

### 🏷️ @Sql

```java
@Test
@Sql("/test-data.sql")  // Runs before test
void myTest() { }

@Test
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
void anotherTest() { }
```

**What it does:**
- SQL files run chestundi before/after tests

**When to use:**
- Complex test data setup kavali appudu
- Legacy data scenarios test cheyyali appudu

**Positives:**
- ✅ Easy data setup
- ✅ Reusable SQL files

**Negatives:**
- ❌ SQL files maintain cheyyali
- ❌ Schema changes lo break avthundi

---

## 6. 4 Types of Integration Tests

### Quick Overview:

```
┌────────────────────────────────────────────────────────────────────────┐
│                    INTEGRATION TEST TYPES                              │
├────────────────────────────────────────────────────────────────────────┤
│                                                                        │
│  1. FULL STACK TEST                                                    │
│     [MockMvc] → [Controller] → [Service] → [Repository] → [MySQL]     │
│     @SpringBootTest + @AutoConfigureMockMvc + @Testcontainers          │
│                                                                        │
├────────────────────────────────────────────────────────────────────────┤
│                                                                        │
│  2. SERVICE LAYER TEST                                                 │
│     [Test] → [Service] → [Repository] → [MySQL]                       │
│     @SpringBootTest(webEnvironment=NONE) + @Testcontainers             │
│                                                                        │
├────────────────────────────────────────────────────────────────────────┤
│                                                                        │
│  3. REPOSITORY LAYER TEST                                              │
│     [Test] → [Repository] → [MySQL]                                   │
│     @DataJpaTest + @Testcontainers                                     │
│                                                                        │
├────────────────────────────────────────────────────────────────────────┤
│                                                                        │
│  4. WEB LAYER TEST (Controller Only)                                   │
│     [MockMvc] → [Controller] → [Service (MOCKED)]                     │
│     @WebMvcTest - NO DATABASE!                                         │
│                                                                        │
└────────────────────────────────────────────────────────────────────────┘
```

---

### Type 1: Full Stack Integration Test

**Purpose:** Complete API flow test with real database

**Annotations Used:**
```java
@SpringBootTest                  // Full application context
@AutoConfigureMockMvc           // MockMvc for HTTP simulation
@Testcontainers                 // Docker container management
```

**Code Flow:**
```
HTTP Request → Controller → Service → Repository → MySQL Container
                                                         ↓
HTTP Response ← Controller ← Service ← Repository ←  Data
```

**When to Use:**
- ✅ API endpoints complete ga test cheyyali
- ✅ End-to-end data flow verify cheyyali
- ✅ Validation + Business Logic + DB - anni together

**Complete Code Example:**

```java
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class UserControllerIntegrationTest {
    
    // 1. Container Setup
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
        .withDatabaseName("testdb");
    
    // 2. Dynamic Properties
    @DynamicPropertySource
    static void configureDB(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }
    
    // 3. Inject Required Beans
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;
    
    // 4. Mock External Services (Email, Payment, etc.)
    @MockBean
    private EmailService emailService;
    
    @Test
    void shouldCreateUser() throws Exception {
        // WHEN - Send HTTP POST
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name": "John", "email": "john@test.com", "password": "secret123"}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("John"));
        
        // THEN - Verify in REAL Database
        assertTrue(userRepository.existsByEmail("john@test.com"));
    }
}
```

---

### Type 2: Service Layer Integration Test

**Purpose:** Business logic + Real DB, without HTTP layer

**Annotations Used:**
```java
@SpringBootTest(webEnvironment = WebEnvironment.NONE)  // No web!
@Testcontainers
```

**Code Flow:**
```
Test Code → Service → Repository → MySQL Container
```

**When to Use:**
- ✅ Complex business logic test cheyyali
- ✅ Controller testing avasaram ledu
- ✅ Faster than full stack (no web layer)

**Complete Code Example:**

```java
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Testcontainers
class UserServiceIntegrationTest {
    
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");
    
    @DynamicPropertySource
    static void configureDB(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }
    
    @Autowired
    private UserService userService;  // REAL service
    
    @Autowired
    private UserRepository userRepository;  // REAL repository
    
    @MockBean
    private EmailService emailService;  // MOCK external
    
    @Test
    void shouldRegisterUser() {
        // Direct service call - no HTTP!
        User user = userService.register("John", "john@test.com", "pass123");
        
        // Verify in REAL database
        assertNotNull(user.getId());
        assertTrue(userRepository.existsByEmail("john@test.com"));
        
        // Verify mock was called
        verify(emailService).sendWelcomeEmail("john@test.com");
    }
}
```

---

### Type 3: Repository Layer Integration Test

**Purpose:** Test custom queries, JPQL, native queries

**Annotations Used:**
```java
@DataJpaTest                                    // Only JPA layer
@AutoConfigureTestDatabase(replace = Replace.NONE)  // Don't use H2
@Testcontainers
```

**Code Flow:**
```
Test Code → Repository → MySQL Container
```

**When to Use:**
- ✅ Custom @Query methods test cheyyali
- ✅ Complex JPQL verify cheyyali
- ✅ Fastest integration test (only DB layer)

**Complete Code Example:**

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Testcontainers
class UserRepositoryIntegrationTest {
    
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");
    
    @DynamicPropertySource
    static void configureDB(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void shouldFindByEmail() {
        // Setup
        User user = new User("John", "john@test.com", "pass123");
        userRepository.save(user);
        
        // Test custom query
        Optional<User> found = userRepository.findByEmail("john@test.com");
        
        // Verify
        assertTrue(found.isPresent());
        assertEquals("John", found.get().getName());
    }
    
    @Test
    void shouldCheckEmailExists() {
        // Setup
        userRepository.save(new User("Jane", "jane@test.com", "pass"));
        
        // Test
        boolean exists = userRepository.existsByEmail("jane@test.com");
        boolean notExists = userRepository.existsByEmail("nobody@test.com");
        
        // Verify
        assertTrue(exists);
        assertFalse(notExists);
    }
}
```

---

### Type 4: Web Layer Test (Controller Only)

**Purpose:** Test HTTP layer only - NO DATABASE

**Annotations Used:**
```java
@WebMvcTest(UserController.class)  // Only controller
// NO @Testcontainers - no DB needed!
```

**Code Flow:**
```
MockMvc → Controller → Service (MOCKED) → Repository (MOCKED)
```

**When to Use:**
- ✅ HTTP status codes test cheyyali
- ✅ Request validation test cheyyali
- ✅ Response format verify cheyyali
- ✅ Fastest - no DB, no Spring Boot full context

**Complete Code Example:**

```java
@WebMvcTest(UserController.class)
class UserControllerWebLayerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;  // MOCK everything!
    
    @Test
    void shouldReturn201_WhenUserCreated() throws Exception {
        // Setup mock
        User mockUser = new User("John", "john@test.com", "pass123");
        mockUser.setId(1L);
        when(userService.register(any(), any(), any())).thenReturn(mockUser);
        
        // Test
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name": "John", "email": "john@test.com", "password": "pass123"}
                    """))
            .andExpect(status().isCreated());
    }
    
    @Test
    void shouldReturn400_WhenValidationFails() throws Exception {
        // Test validation - no mock needed!
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name": "", "email": "invalid", "password": "123"}
                    """))
            .andExpect(status().isBadRequest());
    }
}
```

---

## 7. Your Project Code - See It In Action

### 📁 Your Test Files:

| File | Type | Location |
|------|------|----------|
| `UserControllerIntegrationTest.java` | Full Stack | `src/test/java/com/learning/integration/` |
| `UserServiceIntegrationTest.java` | Service Layer | `src/test/java/com/learning/integration/` |
| `UserRepositoryTest.java` | Repository Layer | `src/test/java/com/learning/repository/` |
| `UserControllerWebLayerTest.java` | Web Layer Only | `src/test/java/com/learning/controller/` |
| `AbstractIntegrationTest.java` | Base Class | `src/test/java/com/learning/integration/` |

### 🔗 How They Connect:

```
AbstractIntegrationTest.java (Base Class)
├── @SpringBootTest
├── @Testcontainers
├── @Container MySQLContainer
└── @DynamicPropertySource

        ↓ extends

UserControllerIntegrationTest.java
├── @AutoConfigureMockMvc
├── Uses: MockMvc
└── Tests: Full API flow


UserServiceIntegrationTest.java
├── webEnvironment = NONE
├── Uses: UserService directly
└── Tests: Business logic + DB
```

### 🎬 Run and See:

```bash
# Run ALL integration tests
mvn test -Dtest=*IntegrationTest

# Run specific test
mvn test -Dtest=UserControllerIntegrationTest

# Run with output
mvn test -Dtest=UserControllerIntegrationTest -DtrimStackTrace=false
```

---

## 8. Quick Reference Card

### 🏷️ Annotation Cheat Sheet:

```
╔══════════════════════════════════════════════════════════════════════╗
║                    INTEGRATION TEST ANNOTATIONS                       ║
╠══════════════════════════════════════════════════════════════════════╣
║                                                                        ║
║  FULL CONTEXT:                                                         ║
║  @SpringBootTest ──────────── Full application load                   ║
║  @SpringBootTest(NONE) ────── No web layer                            ║
║  @SpringBootTest(RANDOM_PORT) ─ Real server                          ║
║                                                                        ║
║  SLICE TESTS:                                                          ║
║  @WebMvcTest ──────────────── Controller only                         ║
║  @DataJpaTest ─────────────── Repository only                         ║
║                                                                        ║
║  MOCK MVC:                                                             ║
║  @AutoConfigureMockMvc ────── Enable MockMvc                          ║
║                                                                        ║
║  TESTCONTAINERS:                                                       ║
║  @Testcontainers ──────────── Enable container management             ║
║  @Container ───────────────── Mark as container                       ║
║  @DynamicPropertySource ───── Inject container properties             ║
║                                                                        ║
║  MOCKING:                                                              ║
║  @MockBean ────────────────── Mock in Spring context                  ║
║                                                                        ║
║  DATABASE:                                                             ║
║  @AutoConfigureTestDatabase(replace=NONE) ── Use real DB             ║
║  @Transactional ───────────── Auto-rollback after test               ║
║  @Sql ─────────────────────── Run SQL before/after test              ║
║                                                                        ║
╚══════════════════════════════════════════════════════════════════════╝
```

### 🎯 When to Use What:

```
┌─────────────────────────────────────────────────────────────────────┐
│ QUESTION                           │ ANSWER                         │
├─────────────────────────────────────────────────────────────────────┤
│ API + DB together test cheyyala?   │ @SpringBootTest + @AutoCon... │
│ Service + DB only?                 │ @SpringBootTest(NONE)          │
│ Repository queries only?           │ @DataJpaTest                   │
│ Controller + validations only?     │ @WebMvcTest                    │
│ Real HTTP (not simulated)?         │ @SpringBootTest(RANDOM_PORT)   │
└─────────────────────────────────────────────────────────────────────┘
```

### 🚫 Common Mistakes:

```
❌ WRONG: @DataJpaTest with Testcontainers without Replace.NONE
   → Uses H2 instead of MySQL!

❌ WRONG: @WebMvcTest trying to use real database
   → Service not available, needs @MockBean!

❌ WRONG: @SpringBootTest without @MockBean for EmailService
   → Tries to send real emails!

❌ WRONG: Non-static @Container
   → Won't be managed properly!

❌ WRONG: Forgetting @DynamicPropertySource
   → Spring can't connect to Docker container!
```

---

## 🎓 Summary - What You Learned

1. **Integration Testing** = Multiple components together with REAL database
2. **4 Types** exist - Full Stack, Service, Repository, Web Layer
3. **Testcontainers** = Real database in Docker for testing
4. **@SpringBootTest** = Full context, **@DataJpaTest** = Only JPA
5. **@MockBean** = Mock external services (Email, Payment)
6. **@DynamicPropertySource** = Connect Docker container to Spring

---

> **Mawa, ippudu nee project lo unna integration tests open chesi, ee guide chaduvuthu code choodu. Anni crystal clear avtayi!** 🚀

**Files to Open:**
1. [UserControllerIntegrationTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/integration/UserControllerIntegrationTest.java)
2. [AbstractIntegrationTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/integration/AbstractIntegrationTest.java)
3. [UserServiceIntegrationTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/integration/UserServiceIntegrationTest.java)
