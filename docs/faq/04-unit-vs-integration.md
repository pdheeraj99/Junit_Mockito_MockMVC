# ❓ FAQ: Unit Test vs Integration Test

> **Mockito use chesi test chestunnam, mari Integration test lo ela?**

---

## 🆚 Side by Side:

### Unit Test (Mockito):

```java
@ExtendWith(MockitoExtension.class)  // Only Mockito, NO Spring!
class UserServiceTest {
    @Mock UserRepository userRepository;    // FAKE
    @Mock EmailService emailService;        // FAKE
    @InjectMocks UserService userService;
}
```

### Integration Test (Spring + Testcontainers):

```java
class UserServiceIntegrationTest extends AbstractIntegrationTest {
    @Autowired UserService userService;     // REAL
    @Autowired UserRepository userRepository; // REAL → MySQL
    @MockBean EmailService emailService;    // Only external mocked
}
```

---

## 📊 Visual:

```
UNIT TEST:
Test → UserService → [FAKE Repo] → ❌ No DB
                  → [FAKE Email]

INTEGRATION TEST:
Test → UserService → [REAL Repo] → ✅ MySQL
                  → [MOCK Email]
```

---

## 🔑 Key Differences:

| Aspect | Unit Test | Integration Test |
|--------|-----------|------------------|
| Annotation | `@ExtendWith(MockitoExtension)` | `@SpringBootTest` |
| Repository | `@Mock` (FAKE) | `@Autowired` (REAL) |
| Database | ❌ None | ✅ MySQL in Docker |
| Speed | ⚡️ Milliseconds | 🐢 Seconds |

---

## 🎯 When to Use:

| Use... | When... |
|--------|---------|
| Unit Test | Logic correct aa verify cheyyali |
| Integration Test | DB tho kalisi work avthunda verify cheyyali |

**Both needed!** Unit = Fast feedback, Integration = Confidence! 💪
