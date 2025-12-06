# 🔗 Integration Testing Visual Guide - Tenglish Edition

> **Unit Test vs Integration Test vs E2E Test - Clarity Time!**

---

## 🎯 Testing Pyramid - Big Picture

```mermaid
pyramid
    title Testing Pyramid
    "E2E Tests" : 10
    "Integration Tests" : 30  
    "Unit Tests" : 60
```

```mermaid
flowchart TB
    subgraph PYRAMID["🔺 Testing Pyramid"]
        E2E["🔝 E2E Tests\n─────────────\n• Full browser/API flow\n• Slowest, Expensive\n• Few in number"]
        
        INT["🔷 Integration Tests\n─────────────\n• Components together\n• Real DB, Mocked external\n• Medium speed"]
        
        UNIT["🟢 Unit Tests\n─────────────\n• Single class/method\n• All mocked\n• Fast, Many"]
    end
    
    E2E --> INT --> UNIT
    
    style E2E fill:#f44336,color:white
    style INT fill:#2196F3,color:white
    style UNIT fill:#4CAF50,color:white
```

---

## 🤔 Unit Test vs Integration Test - Decision Matrix

```mermaid
flowchart TD
    START["🧪 Test Type\nDecision"] --> Q1{"Database\nReal Kavala?"}
    
    Q1 -->|"No - Mock it"| Q2{"External Services\n(Email, Payment)"}
    Q1 -->|"Yes - Real DB"| INT["Integration Test"]
    
    Q2 -->|"Mock everything"| UNIT["Unit Test"]
    Q2 -->|"Some Real"| INT
    
    INT --> Q3{"HTTP Layer\nTest Cheyyala?"}
    Q3 -->|"No - Only Service"| SL["Service Layer Integration"]
    Q3 -->|"Yes - Full API"| Q4{"Real Server?"}
    
    Q4 -->|"MockMvc (Simulated)"| WL["Web Layer Integration"]
    Q4 -->|"Real HTTP"| E2E["Full Integration / E2E"]
    
    style UNIT fill:#4CAF50,color:white
    style INT fill:#2196F3,color:white
    style E2E fill:#FF9800,color:white
```

### Quick Comparison Table:

| Aspect | Unit Test | Integration Test | E2E Test |
|--------|-----------|------------------|----------|
| **Scope** | Single class | Multiple components | Whole system |
| **Database** | Mocked | Real (H2/Testcontainers) | Real |
| **External APIs** | Mocked | Mocked | Real or Mocked |
| **Speed** | ⚡ Milliseconds | 🔄 Seconds | 🐢 Minutes |
| **Quantity** | Many (100s) | Medium (10s) | Few (5-10) |
| **Purpose** | Logic correct? | Components work together? | User flow works? |

---

## 🏗️ Integration Test Layers

### What Can You Integration Test?

```mermaid
flowchart TB
    subgraph LAYERS["Integration Test Layers"]
        direction LR
        
        FULL["🌐 Full Stack\n─────────────\n@SpringBootTest\n+ Testcontainers\n+ Real HTTP\n\nController → Service → Repo → DB"]
        
        WEB["🔵 Web Layer\n─────────────\n@SpringBootTest\n+ MockMvc\n+ Real DB\n\nHTTP → Controller → Service → Repo"]
        
        SERVICE["⚙️ Service Layer\n─────────────\n@SpringBootTest\n+ Real DB\n+ @MockBean (external)\n\nService → Repo → DB"]
        
        REPO["💾 Repository Layer\n─────────────\n@DataJpaTest\n+ Real DB\n\nRepo → DB\nCustom queries verify"]
    end
    
    FULL --> WEB --> SERVICE --> REPO
    
    style FULL fill:#FF9800,color:white
    style WEB fill:#2196F3,color:white
    style SERVICE fill:#9C27B0,color:white
    style REPO fill:#4CAF50,color:white
```

---

## 🔷 Layer-Wise Testing Guide

### 1️⃣ Repository Layer Integration

**Purpose:** Custom queries, JPQL, relationships working?

```mermaid
flowchart LR
    TEST["@DataJpaTest"] --> REPO["UserRepository"]
    REPO --> DB["Real MySQL\n(Testcontainers)"]
    
    style TEST fill:#4CAF50,color:white
```

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Testcontainers
class UserRepositoryIntegrationTest {
    
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");
    
    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void shouldFindActiveUsersWithCustomQuery() {
        // Given - data setup
        userRepository.save(new User("Active", "a@test.com", true));
        userRepository.save(new User("Inactive", "i@test.com", false));
        
        // When - custom query
        List<User> activeUsers = userRepository.findAllActiveUsers();
        
        // Then
        assertEquals(1, activeUsers.size());
        assertEquals("Active", activeUsers.get(0).getName());
    }
}
```

---

### 2️⃣ Service Layer Integration

**Purpose:** Business logic + Real DB, but no HTTP layer

```mermaid
flowchart LR
    TEST["@SpringBootTest"] --> SVC["UserService"]
    SVC --> REPO["UserRepository"]
    REPO --> DB["Real MySQL"]
    SVC --> EMAIL["@MockBean\nEmailService"]
    
    style TEST fill:#9C27B0,color:white
    style EMAIL fill:#f44336,color:white
```

```java
@SpringBootTest(webEnvironment = WebEnvironment.NONE)  // No web!
@Testcontainers
class UserServiceIntegrationTest {
    
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");
    
    // ... DynamicPropertySource ...
    
    @Autowired
    private UserService userService;  // Real service
    
    @Autowired
    private UserRepository userRepository;  // Real repo
    
    @MockBean
    private EmailService emailService;  // Mock external!
    
    @Test
    void shouldRegisterAndSaveToDatabase() {
        // When - call service directly (no HTTP)
        User result = userService.registerUser("John", "john@test.com", "pass123");
        
        // Then - verify in real DB
        assertNotNull(result.getId());
        assertTrue(userRepository.existsByEmail("john@test.com"));
        
        // Verify email mock was called
        verify(emailService).sendWelcomeEmail("john@test.com");
    }
}
```

---

### 3️⃣ Web Layer Integration

**Purpose:** Full API flow with MockMvc + Real DB

```mermaid
flowchart LR
    TEST["@SpringBootTest\n+ MockMvc"] --> CTRL["Controller"]
    CTRL --> SVC["Service"]
    SVC --> REPO["Repository"]
    REPO --> DB["Real MySQL"]
    SVC --> EMAIL["@MockBean\nEmailService"]
    
    style TEST fill:#2196F3,color:white
    style EMAIL fill:#f44336,color:white
```

```java
@SpringBootTest  // webEnvironment = MOCK (default)
@AutoConfigureMockMvc
@Testcontainers
class UserControllerIntegrationTest {
    
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");
    
    // ... DynamicPropertySource ...
    
    @Autowired
    private MockMvc mockMvc;  // Simulated HTTP
    
    @Autowired
    private UserRepository userRepository;
    
    @MockBean
    private EmailService emailService;
    
    @Test
    void shouldCreateUserViaAPI() throws Exception {
        // When - HTTP request via MockMvc
        mockMvc.perform(post("/api/users")
                .contentType(APPLICATION_JSON)
                .content("""
                    {"name": "John", "email": "john@test.com", "password": "pass123"}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("John"));
        
        // Then - verify persisted in real DB
        assertTrue(userRepository.existsByEmail("john@test.com"));
    }
}
```

---

### 4️⃣ Full Stack Integration (E2E-like)

**Purpose:** Real HTTP + Real DB + Real Server

```mermaid
flowchart LR
    TEST["TestRestTemplate\nReal HTTP"] --> SERVER["Real Tomcat\nRANDOM_PORT"]
    SERVER --> CTRL["Controller"]
    CTRL --> SVC["Service"]
    SVC --> REPO["Repository"]
    REPO --> DB["Real MySQL"]
    
    style TEST fill:#FF9800,color:white
    style SERVER fill:#FF9800,color:white
```

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)  // Real server!
@Testcontainers
class FullIntegrationTest {
    
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");
    
    // ... DynamicPropertySource ...
    
    @Autowired
    private TestRestTemplate restTemplate;  // Real HTTP client
    
    @LocalServerPort
    private int port;  // Actual port
    
    @MockBean
    private EmailService emailService;  // Still mock external
    
    @Test
    void shouldCompleteFullUserFlow() {
        // Create user - Real HTTP call
        var createRequest = new UserRequest("John", "john@test.com", "pass123");
        ResponseEntity<User> createResponse = restTemplate.postForEntity(
            "/api/users", createRequest, User.class);
        
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        Long userId = createResponse.getBody().getId();
        
        // Get user - Real HTTP call
        ResponseEntity<User> getResponse = restTemplate.getForEntity(
            "/api/users/" + userId, User.class);
        
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("John", getResponse.getBody().getName());
    }
}
```

---

## 🐳 Testcontainers Best Practices

### Reusable Base Test Class

```mermaid
flowchart TB
    BASE["AbstractIntegrationTest\n─────────────\n• Container setup\n• @DynamicPropertySource\n• Common utilities"]
    
    BASE --> T1["UserServiceIntegrationTest"]
    BASE --> T2["OrderServiceIntegrationTest"]
    BASE --> T3["PaymentIntegrationTest"]
    
    style BASE fill:#2196F3,color:white
```

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
public abstract class AbstractIntegrationTest {
    
    @Container
    protected static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test")
        .withReuse(true);  // Reuse container across tests!
    
    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }
    
    @Autowired
    protected TestRestTemplate restTemplate;
    
    // Helper methods
    protected void cleanDatabase() {
        // Cleanup logic
    }
}
```

### Container Reuse (Faster Tests!)

```properties
# ~/.testcontainers.properties
testcontainers.reuse.enable=true
```

```java
@Container
static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
    .withReuse(true);  // Don't stop after test class!
```

---

## 🎯 Decision Guide - Which Integration Layer?

```mermaid
flowchart TD
    Q1{"Emi verify\ncheyyali?"} --> A1["Custom SQL queries\nJPQL working?"]
    Q1 --> A2["Business logic +\nDB transactions?"]
    Q1 --> A3["API request/response\n+ DB?"]
    Q1 --> A4["Full user flow\nend-to-end?"]
    
    A1 --> R1["@DataJpaTest\n+ Testcontainers"]
    A2 --> R2["@SpringBootTest(web=NONE)\n+ Testcontainers"]
    A3 --> R3["@SpringBootTest + MockMvc\n+ Testcontainers"]
    A4 --> R4["@SpringBootTest(web=RANDOM_PORT)\n+ TestRestTemplate"]
    
    style R1 fill:#4CAF50,color:white
    style R2 fill:#9C27B0,color:white
    style R3 fill:#2196F3,color:white
    style R4 fill:#FF9800,color:white
```

| Question | Answer | Use |
|----------|--------|-----|
| Repository queries correct? | @DataJpaTest | Only JPA layer |
| Service + DB together? | @SpringBootTest(NONE) | No web, just service |
| API + DB together? | @SpringBootTest + MockMvc | Simulated HTTP |
| Full real HTTP flow? | @SpringBootTest(RANDOM_PORT) | Real server |

---

## 🚫 What to Always Mock in Integration Tests

```mermaid
flowchart LR
    subgraph MOCK["Always @MockBean"]
        E1["📧 EmailService"]
        E2["💳 PaymentGateway"]
        E3["📱 SMSService"]
        E4["🌐 External APIs"]
        E5["📊 Analytics"]
    end
    
    subgraph REAL["Keep Real"]
        R1["💾 Database"]
        R2["🔧 Your Services"]
        R3["📦 Repositories"]
        R4["🎯 Controllers"]
    end
    
    style MOCK fill:#f44336,color:white
    style REAL fill:#4CAF50,color:white
```

**Rule:** External systems mock cheyyali, Internal components real ga test cheyyali!

---

## 📋 Integration Test Checklist

```
PRE-TEST:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
□ Testcontainers dependency added?
□ Docker running?
□ @Testcontainers annotation added?
□ @Container for DB container?
□ @DynamicPropertySource configured?

DATABASE:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
□ Each test independent? (clean data)
□ @BeforeEach cleanup or @Transactional?
□ Test data setup correct?

EXTERNAL SERVICES:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
□ EmailService mocked?
□ PaymentGateway mocked?
□ All external APIs mocked?

ASSERTIONS:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
□ Response verified?
□ Database state verified?
□ Mock interactions verified?
```

---

## 🚀 Complete Integration Test Example

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(OrderAnnotation.class)
class UserFlowIntegrationTest extends AbstractIntegrationTest {
    
    @Autowired private TestRestTemplate restTemplate;
    @Autowired private UserRepository userRepository;
    
    @MockBean private EmailService emailService;
    @MockBean private PaymentGateway paymentGateway;
    
    private static Long createdUserId;
    
    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }
    
    @Test
    @Order(1)
    @DisplayName("1️⃣ Should register new user")
    void shouldRegisterUser() {
        // Given
        var request = new UserRequest("John", "john@test.com", "secure123");
        
        // When
        ResponseEntity<User> response = restTemplate.postForEntity(
            "/api/users", request, User.class);
        
        // Then - HTTP Response
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().getId());
        createdUserId = response.getBody().getId();
        
        // Then - Database
        User inDb = userRepository.findById(createdUserId).orElseThrow();
        assertEquals("John", inDb.getName());
        
        // Then - Email sent
        verify(emailService).sendWelcomeEmail("john@test.com");
    }
    
    @Test
    @Order(2)
    @DisplayName("2️⃣ Should retrieve registered user")
    void shouldGetUser() {
        // First create a user
        userRepository.save(new User("Jane", "jane@test.com", "pass123"));
        
        // When
        ResponseEntity<User> response = restTemplate.getForEntity(
            "/api/users/email/jane@test.com", User.class);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Jane", response.getBody().getName());
    }
    
    @Test
    @Order(3)
    @DisplayName("3️⃣ Should reject duplicate email")
    void shouldRejectDuplicateEmail() {
        // Given - user exists
        userRepository.save(new User("Existing", "exists@test.com", "pass123"));
        
        // When - try to create with same email
        var request = new UserRequest("New", "exists@test.com", "pass456");
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
            "/api/users", request, ErrorResponse.class);
        
        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("already exists"));
        
        // Then - no email sent
        verify(emailService, never()).sendWelcomeEmail(any());
    }
}
```

---

## 🎯 Quick Memory Tips

```
INTEGRATION TEST LAYERS:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Repository only    → @DataJpaTest
Service + DB       → @SpringBootTest(NONE)
Controller + DB    → @SpringBootTest + MockMvc
Full HTTP + DB     → @SpringBootTest(RANDOM_PORT)

ALWAYS MOCK:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Email, SMS, Payment, External APIs

ALWAYS REAL:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Database (via Testcontainers)
Your Services, Repos, Controllers

TESTCONTAINERS:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
@Testcontainers     → Enable
@Container          → Declare container
@DynamicPropertySource → Configure Spring
.withReuse(true)    → Faster tests!
```
