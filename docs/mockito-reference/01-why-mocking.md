# 1️⃣ Why Mocking? - The Real Problem

> **Test file:** [Part1_MockBasicsTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/mockito/Part1_MockBasicsTest.java)

---

## 🎯 The Problem - Testing Without Mocking

### Real World Scenario

Neeku `UserService` class undi, idi user registration handle chestundi:

```java
public class UserService {
    
    private UserRepository userRepository;  // Database
    private EmailService emailService;      // External email
    
    public User registerUser(String name, String email, String password) {
        // 1. Check if email exists in DATABASE
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("Email exists!");
        }
        
        // 2. Save to DATABASE
        User user = userRepository.save(new User(name, email, password));
        
        // 3. Send REAL email
        emailService.sendWelcomeEmail(email, name);
        
        return user;
    }
}
```

### Problems When Testing Directly

| Problem | Impact |
|---------|--------|
| **Database needed** | Test ki actual database kavali, start cheyyali |
| **Data pollution** | Test data database lo remain avtundi |
| **Slow tests** | Database calls slow |
| **Email sending** | Test run chesthe REAL emails velthyi! 📧 |
| **Network dependency** | Internet lekapothe test fail |
| **Cost** | Some services charge per API call |
| **Flaky tests** | External service down → test fail |

```
❌ Test running...
→ Connecting to database... (2 sec)
→ Inserting test user... (500ms)  
→ Sending REAL email to ramesh@email.com! 😱
→ Cleaning up database... (500ms)
Total: 3+ seconds for ONE test!
```

---

## 🎯 The Solution - Mocking!

### What is Mocking?

**Mock = Fake Object**

Real object ni **replace** chesi, neeku control undey fake object tho:

```
REAL WORLD:
UserService → UserRepository → MySQL Database
UserService → EmailService → SendGrid API

WITH MOCKING:
UserService → MOCK UserRepository → Returns what you tell it!
UserService → MOCK EmailService → Does nothing, we just verify it was called
```

### Benefits

| Benefit | Explanation |
|---------|-------------|
| **No database** | Mock returns fake data instantly |
| **No emails** | Mock emailService does nothing |
| **Fast** | No network calls, milliseconds per test |
| **Isolated** | Only testing YOUR service logic |
| **Reliable** | No external failures affect test |
| **Controlled** | You decide what dependencies return |

```
✅ Test running...
→ Mock returns fake user... (1ms)
→ Mock "sends" email... (0ms, does nothing)
→ Verify service logic worked correctly
Total: 5 milliseconds!
```

---

## 🧪 Without Mocking vs With Mocking

### WITHOUT Mocking (Integration Test)

```java
@Test
void testRegisterUser() {
    // Need real database connection!
    // Need real email service!
    
    User user = userService.registerUser("Ramesh", "ramesh@email.com", "pass123");
    
    // Check database directly
    User fromDb = realDatabase.findByEmail("ramesh@email.com");
    assertNotNull(fromDb);
    
    // Check email was really sent?? HOW??
    // Can't verify easily without checking mailbox!
}
```

**Problems:**
- Slow (database calls)
- Real email sent 
- Database needs cleanup
- Can't verify email content

### WITH Mocking (Unit Test)

```java
@Mock
private UserRepository userRepository;  // Fake!

@Mock
private EmailService emailService;  // Fake!

@InjectMocks
private UserService userService;  // Real, with mocks injected

@Test
void testRegisterUser() {
    // Tell mock what to return
    when(userRepository.existsByEmail("ramesh@email.com")).thenReturn(false);
    when(userRepository.save(any(User.class))).thenAnswer(inv -> {
        User u = inv.getArgument(0);
        u.setId(1L);
        return u;
    });
    
    // Run the service method
    User user = userService.registerUser("Ramesh", "ramesh@email.com", "pass123");
    
    // Verify
    assertNotNull(user);
    assertEquals("Ramesh", user.getName());
    
    // Verify email was "sent" (actually just verify the method was called!)
    verify(emailService).sendWelcomeEmail("ramesh@email.com", "Ramesh");
}
```

**Benefits:**
- Fast (no real DB/email)
- Email verified without sending
- No cleanup needed
- Full control over mock behavior

---

## 🏗️ Mockito Terminology

| Term | Meaning | Example |
|------|---------|---------|
| **Mock** | Fake object | `@Mock UserRepository` |
| **Stub** | Define mock behavior | `when(mock.method()).thenReturn(value)` |
| **Verify** | Check method was called | `verify(mock).method()` |
| **Inject** | Put mocks into class | `@InjectMocks UserService` |
| **Matcher** | Match any argument | `any()`, `eq("value")` |
| **Spy** | Partial mock (real + mock) | `@Spy ArrayList<>` |
| **Captor** | Capture arguments | `@Captor ArgumentCaptor<User>` |

---

## 📊 What to Mock? Decision Chart

```
╔════════════════════════════════════════════════════════════════════╗
║  Should I Mock This?                                               ║
╠════════════════════════════════════════════════════════════════════╣
║                                                                    ║
║  Database Repository?                                              ║
║  └── ✅ YES! Always mock in unit tests                            ║
║                                                                    ║
║  External API (Email, Payment, SMS)?                               ║
║  └── ✅ YES! Don't hit real APIs in tests                         ║
║                                                                    ║
║  Another Service in your app?                                      ║
║  └── ✅ Usually yes, to isolate test                              ║
║                                                                    ║
║  Simple POJO/DTO/Model?                                            ║
║  └── ❌ NO! Use real objects                                      ║
║                                                                    ║
║  Utility class with static methods?                                ║
║  └── ❌ Usually no (or use PowerMock if needed)                   ║
║                                                                    ║
║  The class you're testing?                                         ║
║  └── ❌ NEVER! That's what you want to test!                      ║
║                                                                    ║
╚════════════════════════════════════════════════════════════════════╝
```

---

## 📋 Mockito Setup

### Maven Dependency (Already included in Spring Boot)

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>

<!-- JUnit 5 integration -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

### Test Class Setup

```java
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)  // Enable Mockito!
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private EmailService emailService;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void testSomething() {
        // Tests go here
    }
}
```

---

## 🎯 Our Real-World Example

### What We're Testing

```java
UserService
├── registerUser()    → Validates, saves, sends email
├── findById()        → Finds user from DB
├── deactivateUser()  → Updates user status
└── updateProfile()   → Updates user details

OrderService
├── createOrder()     → Creates order with items
├── processPayment()  → Charges payment, updates status
├── cancelOrder()     → Refunds payment, cancels order
└── shipOrder()       → Updates status, sends notification
```

### What We're Mocking

```
UserRepository    → All database operations
OrderRepository   → All database operations  
EmailService      → Email sending
PaymentGateway    → Payment processing
```

---

## 📎 Related Files

- **Models:** [User.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/main/java/com/learning/model/User.java), [Order.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/main/java/com/learning/model/Order.java)
- **Repositories:** [UserRepository.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/main/java/com/learning/repository/UserRepository.java)
- **Services:** [UserService.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/main/java/com/learning/service/UserService.java), [OrderService.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/main/java/com/learning/service/OrderService.java)
- **Test examples:** [Part1_MockBasicsTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/mockito/Part1_MockBasicsTest.java)
