# ❓ FAQ: Why AbstractIntegrationTest?

> **Asala enduku ee abstract class?**

---

## ✅ Answer: DRY Principle - Don't Repeat Yourself!

---

## ❌ WITHOUT Abstract Class (BAD):

```java
// Test 1
@SpringBootTest @Testcontainers
class UserControllerIT {
    @Container static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");
    @DynamicPropertySource static void config(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", mysql::getJdbcUrl);
        // ... more config
    }
}

// Test 2 - SAME CODE COPY-PASTE! 😱
@SpringBootTest @Testcontainers
class UserServiceIT {
    @Container static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");
    @DynamicPropertySource static void config(DynamicPropertyRegistry r) { ... }
}

// Test 3, 4, 5... - REPEAT! 😱😱😱
```

---

## ✅ WITH Abstract Class (GOOD):

```java
// AbstractContainerBaseTest.java - ONE PLACE!
@Testcontainers
public abstract class AbstractContainerBaseTest {
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");
    
    @DynamicPropertySource
    static void config(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", mysql::getJdbcUrl);
    }
}

// All tests just extend - CLEAN!
class UserControllerIT extends AbstractIntegrationTest { }
class UserServiceIT extends AbstractIntegrationTest { }
class OrderIT extends AbstractIntegrationTest { }
```

---

## 📊 Visual:

```
AbstractContainerBaseTest
├── @Testcontainers
├── @Container MySQLContainer
└── @DynamicPropertySource
        │
        ▼ extends
AbstractIntegrationTest
├── @SpringBootTest(RANDOM_PORT)
        │
        ▼ extends
┌────────────────────────────────┐
│ UserControllerIT               │
│ UserServiceIT                  │ ← Clean! Only test logic!
│ OrderIT                        │
└────────────────────────────────┘
```

---

## 💡 Benefits:

| Benefit | Explanation |
|---------|-------------|
| **No Duplicate Code** | Container config ONE place |
| **Easy Changes** | MySQL → PostgreSQL? One file! |
| **Consistency** | All tests same DB settings |
| **Clean Tests** | Test files lo only test logic |

---

## 🎯 Analogy:

```
Abstract Class = Kitchen Gas Stove

Test 1 = Making Tea   → Uses stove
Test 2 = Making Coffee → Uses same stove
Test 3 = Making Maggi → Uses same stove

Stove setup oka sari, anni tests reuse!
```
