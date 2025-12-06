# 2️⃣ @Mock & @InjectMocks - Core Annotations

> **Test file:** [Part1_MockBasicsTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/mockito/Part1_MockBasicsTest.java)

---

## 🎯 @Mock - Fake Object Create Cheyyatam

### What is @Mock?

**@Mock = "Idi fake object, nenu control chestanu"**

```java
@Mock
private UserRepository userRepository;  // This is NOT real!
```

Edi real `UserRepository` kaadu. Idi:
- Database ki connect avvadu
- Real data return cheyadu
- **Neeku tell chesindi ey chestundi**

---

### How Mock Works

```java
@Mock
private UserRepository userRepository;

@Test
void testExample() {
    // By default, mock returns:
    // - null for objects
    // - 0 for numbers
    // - false for booleans
    // - empty collections
    
    User user = userRepository.findById(1L);  // Returns null!
    boolean exists = userRepository.existsByEmail("a@b.com");  // Returns false!
    List<User> users = userRepository.findAll();  // Returns empty list!
}
```

### Telling Mock What To Return (Stubbing)

```java
@Test
void testWithStubbing() {
    // ARRANGE: Tell mock what to return
    User fakeUser = new User(1L, "Ramesh", "ramesh@email.com");
    when(userRepository.findById(1L)).thenReturn(Optional.of(fakeUser));
    
    // ACT: Call the mock
    Optional<User> result = userRepository.findById(1L);
    
    // ASSERT: Got what we told it to return!
    assertTrue(result.isPresent());
    assertEquals("Ramesh", result.get().getName());
}
```

---

## 🎯 @InjectMocks - Auto Injection

### What is @InjectMocks?

**@InjectMocks = "Idi real object, ikkada mocks inject cheyyi"**

```java
@Mock
private UserRepository userRepository;  // Fake

@Mock
private EmailService emailService;  // Fake

@InjectMocks
private UserService userService;  // REAL, but uses fake dependencies!
```

### How It Works

```
UserService (Real)
├── userRepository → @Mock UserRepository (Fake)
└── emailService   → @Mock EmailService (Fake)
```

Mockito automatically:
1. `UserService` instance create chestundi
2. `@Mock` annotated fields ni inject chestundi
3. Constructor injection or field injection use chestundi

---

## 📝 Complete Test Setup

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)  // Required for @Mock to work!
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private EmailService emailService;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void testRegisterUser() {
        // Arrange
        when(userRepository.existsByEmail("test@email.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });
        
        // Act
        User result = userService.registerUser("Test", "test@email.com", "password123");
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test", result.getName());
        
        // Verify mocks were called
        verify(userRepository).existsByEmail("test@email.com");
        verify(userRepository).save(any(User.class));
        verify(emailService).sendWelcomeEmail("test@email.com", "Test");
    }
}
```

---

## 🔄 Injection Methods

### 1. Constructor Injection (Recommended)

```java
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    // Constructor - Mockito injects here!
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }
}
```

### 2. Setter Injection

```java
public class UserService {
    private UserRepository userRepository;
    
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

### 3. Field Injection (Works but not recommended)

```java
public class UserService {
    @Autowired
    private UserRepository userRepository;
}
```

---

## ⚠️ Common Mistakes

### Mistake 1: Forgetting @ExtendWith

```java
// ❌ WRONG - Mocks are NULL!
class UserServiceTest {
    @Mock
    private UserRepository userRepository;  // NULL!
}

// ✅ CORRECT
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;  // Works!
}
```

### Mistake 2: Creating mock manually AND @InjectMocks

```java
// ❌ WRONG - Double injection!
@InjectMocks
private UserService userService = new UserService(mockRepo, mockEmail);

// ✅ CORRECT - Let Mockito create it
@InjectMocks
private UserService userService;
```

### Mistake 3: Mixing @Mock and @InjectMocks on same class

```java
// ❌ WRONG - Confusing!
@Mock
@InjectMocks  // Which one??
private UserService userService;

// ✅ CORRECT - Separate concerns
@Mock
private UserRepository userRepository;  // Dependency

@InjectMocks
private UserService userService;  // Class under test
```

---

## 📊 @Mock vs @InjectMocks Summary

| Aspect | @Mock | @InjectMocks |
|--------|-------|--------------|
| **What it creates** | Fake object | Real object |
| **Purpose** | Dependency to mock | Class to test |
| **Behavior** | Returns null/default | Real logic executes |
| **You control** | What it returns | Nothing (it's real) |
| **Number per test** | Many (all dependencies) | One (class under test) |

---

## 📎 Related Files

- **Test examples:** [Part1_MockBasicsTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/mockito/Part1_MockBasicsTest.java)
