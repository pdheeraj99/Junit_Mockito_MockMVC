# ❓ Integration Testing - Common Doubts & Clarifications

> **Nee doubts anni ikkada documented! Future reference ki use chesko mawa!**

---

## 🤔 Doubt 1: Controller tests ki Web Environment kavala, Service tests ki vadda?

### ✅ Answer: Correct!

```
Controller Test:
┌──────────────────────────────────────────────────────────────┐
│  MockMvc → HTTP Request → Controller → Service → DB         │
│                                                               │
│  Web environment KAVALI - HTTP calls simulate cheyyali!      │
│  @SpringBootTest (default = MOCK web environment)            │
│  @AutoConfigureMockMvc                                        │
└──────────────────────────────────────────────────────────────┘

Service Test:
┌──────────────────────────────────────────────────────────────┐
│  Test → userService.register() → Repository → DB            │
│                                                               │
│  Web environment AVASARAM LEDU - direct method call!         │
│  @SpringBootTest(webEnvironment = WebEnvironment.NONE)       │
└──────────────────────────────────────────────────────────────┘
```

### Code Examples:

```java
// Controller Test - WEB ENVIRONMENT needed
@SpringBootTest  // Default: webEnvironment = MOCK
@AutoConfigureMockMvc
class UserControllerIntegrationTest {
    @Autowired MockMvc mockMvc;  // HTTP simulation
}

// Service Test - NO WEB ENVIRONMENT
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class UserServiceIntegrationTest {
    @Autowired UserService userService;  // Direct call
}
```

---

## 🤔 Doubt 2: @SpringBootTest lo Beans - REAL or MOCKED?

### ✅ Answer: REAL BEANS!

```java
@SpringBootTest
class MyTest {
    @Autowired UserService userService;     // ✅ REAL
    @Autowired UserRepository userRepository; // ✅ REAL
    @Autowired UserController userController; // ✅ REAL
}
```

**@SpringBootTest = Production laga REAL beans load chestundi!**

### Then @MockBean enduku?

**@MockBean** = Specific bean ni mock tho REPLACE chestundi.

```java
@SpringBootTest
class MyTest {
    @Autowired UserService userService;   // ✅ REAL
    @MockBean EmailService emailService;  // 🔴 FAKE (replaced!)
}
```

### Visual:

```
WITHOUT @MockBean:
┌─────────────────────────────────────────────────┐
│ Spring Context                                  │
│   UserController  ──→ REAL                      │
│   UserService     ──→ REAL                      │
│   UserRepository  ──→ REAL                      │
│   EmailService    ──→ REAL (sends real email!)  │
└─────────────────────────────────────────────────┘

WITH @MockBean EmailService:
┌─────────────────────────────────────────────────┐
│ Spring Context                                  │
│   UserController  ──→ REAL                      │
│   UserService     ──→ REAL                      │
│   UserRepository  ──→ REAL                      │
│   EmailService    ──→ MOCK (fake, no email!)    │
└─────────────────────────────────────────────────┘
```

### Key Point:
- **@SpringBootTest** alone = ALL REAL
- **@SpringBootTest + @MockBean** = ALL REAL except explicitly mocked ones

---

## 🤔 Doubt 3: Replace.ANY vs Replace.NONE - Enti?

### Background:

```properties
# Your application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
```

Spring's default test behavior: **"Nee MySQL vadakudadu, H2 vaddam!"**

### Options Explained:

| Option | Meaning (Telugu) |
|--------|------------------|
| `Replace.ANY` | **"Nee DB edhaina, H2 tho replace!"** (DEFAULT) |
| `Replace.NONE` | **"Replace cheyaku, nee config vaadu!"** |

### Visual:

```
Replace.ANY (DEFAULT):
┌──────────────────────────────────────────────────────────────┐
│  Your Config: MySQL → Spring: "Nahh, H2 vaddam!"             │
│  Test → Repository → H2 (In-Memory) ← IGNORES Testcontainer! │
└──────────────────────────────────────────────────────────────┘

Replace.NONE:
┌──────────────────────────────────────────────────────────────┐
│  Your Config: MySQL → Spring: "OK, MySQL ye vaddam!"         │
│  Test → Repository → MySQL (Testcontainer) ← USES your DB!  │
└──────────────────────────────────────────────────────────────┘
```

### Code Example:

```java
// ❌ WRONG: Uses H2, ignores Testcontainer!
@DataJpaTest
class UserRepositoryTest { }

// ✅ CORRECT: Uses YOUR MySQL Testcontainer
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class UserRepositoryTest { }
```

### Terms Clarified:

| Term | Meaning |
|------|---------|
| **Embedded DB** | H2, HSQLDB - in-memory, no Docker |
| **Configured Datasource** | YOUR config (Testcontainers MySQL) |
| **Replace.ANY** | Use H2, ignore your config |
| **Replace.NONE** | Use your config, no replacement |

### 🎯 Memory Trick:

```
Replace.ANY  = "ANY DB needhaina - H2 ki replace!"
Replace.NONE = "NONE replace - nee config vaadu!"
```

---

## 📋 Quick Reference Summary

### When to use what:

| Scenario | Annotation |
|----------|------------|
| Controller + HTTP test | `@SpringBootTest` + `@AutoConfigureMockMvc` |
| Service + DB test | `@SpringBootTest(webEnvironment=NONE)` |
| Repository only test | `@DataJpaTest` + `Replace.NONE` |
| Controller only (no DB) | `@WebMvcTest` |

### Beans behavior:

| Annotation | Beans are... |
|------------|--------------|
| `@SpringBootTest` | ALL REAL |
| `@SpringBootTest` + `@MockBean X` | All real EXCEPT X |
| `@WebMvcTest` | Only Controller, others need @MockBean |
| `@DataJpaTest` | Only JPA layer |

### Database behavior:

| Annotation | Database used |
|------------|---------------|
| `@DataJpaTest` (default) | H2 (embedded) |
| `@DataJpaTest` + `Replace.NONE` | Your Testcontainer |
| `@SpringBootTest` | Your Testcontainer |

---

> **Inka doubts unte add avuthay mawa! Keep learning! 🚀**
