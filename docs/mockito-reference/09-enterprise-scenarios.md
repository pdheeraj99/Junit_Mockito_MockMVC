# 9️⃣ Enterprise Scenarios & Best Practices

> **Test file:** [Part8_EnterpriseScenariosTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/mockito/Part8_EnterpriseScenariosTest.java)

---

## 🏢 Enterprise Patterns

| Pattern | When to Use |
|---------|-------------|
| `lenient().when()` | Avoid UnnecessaryStubbingException in setup methods |
| `RETURNS_DEEP_STUBS` | Chain method calls (fluent APIs, builders) |
| `reset(mock)` | Clear state between test phases (use sparingly!) |
| `timeout(ms)` | Async/callback verification |
| `MockedStatic<>` | Static methods (UUID, System, Instant) |
| `RETURNS_SELF` | Builder pattern mocks |
| `withSettings().name()` | Better error messages |

---

## 🔌 Spring Boot Integration

```java
@SpringBootTest
class ServiceIntegrationTest {

    @MockBean  // Mock Spring bean in context
    private UserRepository userRepository;
    
    @SpyBean   // Partial mock Spring bean
    private EmailService emailService;
    
    @Autowired
    private UserService userService;  // Uses mocked beans!
}
```

---

## 📋 Coverage Checklist (99% Enterprise Scenarios)

| Category | Patterns | Status |
|----------|----------|--------|
| **Basic Mocking** | @Mock, @InjectMocks | ✅ |
| **Stubbing** | when/thenReturn/Throw/Answer | ✅ |
| **do* Methods** | doReturn/Throw/Nothing/Answer | ✅ |
| **Verification** | verify/times/never/inOrder | ✅ |
| **Matchers** | any/eq/argThat/contains | ✅ |
| **Spies** | @Spy, partial mocking | ✅ |
| **Captors** | @Captor, getAllValues | ✅ |
| **BDD** | given/willReturn, then/should | ✅ |
| **Static** | MockedStatic, try-with-resources | ✅ |
| **Async** | timeout(), after() | ✅ |
| **Settings** | deep stubs, lenient, smart nulls | ✅ |
| **Spring** | @MockBean, @SpyBean | ✅ (doc) |

---

## 📎 Related Files

- **Test examples:** [Part8_EnterpriseScenariosTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/mockito/Part8_EnterpriseScenariosTest.java)
