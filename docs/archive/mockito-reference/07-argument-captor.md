# 7️⃣ ArgumentCaptor - Capturing Arguments

> **Test file:** [Part6_ArgumentCaptorTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/mockito/Part6_ArgumentCaptorTest.java)

---

## 🎯 ArgumentCaptor Enti?

**Mock method ki pass chesina argument ni capture chesi inspect cheyatam**

```java
// Verify sendEmail was called, AND capture what was passed
ArgumentCaptor<Email> captor = ArgumentCaptor.forClass(Email.class);
verify(emailService).send(captor.capture());

// Now inspect the captured value
Email sentEmail = captor.getValue();
assertEquals("Hello Subject", sentEmail.getSubject());
assertEquals("user@email.com", sentEmail.getTo());
```

---

## 🤔 When to Use?

| Use Case | Why? |
|----------|------|
| **Complex objects** | Verify object properties after call |
| **Dynamic values** | Check timestamp, IDs, etc. |
| **Multiple calls** | Capture all values from multiple calls |
| **Detailed assertion** | More than just "was called" |

---

## 📝 Basic Usage

```java
@Captor
ArgumentCaptor<User> userCaptor;

@Test
void testCapture() {
    // ACT
    userService.registerUser("Ramesh", "r@email.com", "pass");
    
    // CAPTURE
    verify(userRepository).save(userCaptor.capture());
    
    // INSPECT
    User savedUser = userCaptor.getValue();
    assertEquals("Ramesh", savedUser.getName());
    assertTrue(savedUser.getPassword().length() > 0);
}
```

---

## 📋 Multiple Captures

```java
// Capture all calls to method
verify(emailService, times(3)).send(emailCaptor.capture());

// Get all captured values
List<Email> allEmails = emailCaptor.getAllValues();
assertEquals(3, allEmails.size());
```

---

## 📎 Related Files

- **Test examples:** [Part6_ArgumentCaptorTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/mockito/Part6_ArgumentCaptorTest.java)
