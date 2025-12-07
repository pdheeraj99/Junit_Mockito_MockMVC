# 4️⃣ Verification - verify() Methods

> **Test file:** [Part3_VerificationTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/mockito/Part3_VerificationTest.java)

---

## 🎯 Verification Enti?

**Verification = "Mock method call ayinda leda ani check cheyyatam"**

Stubbing lo mock ki **what to return** cheptam.
Verification lo mock **called ayyinda** ani check chestam.

```java
// Stubbing - BEFORE calling
when(mock.method()).thenReturn(value);

// Your code runs...
service.doSomething();

// Verification - AFTER code runs
verify(mock).method();  // Was method() called?
```

---

## 📋 All Verification Methods

| Method | Purpose |
|--------|---------|
| `verify(mock).method()` | Method was called (exactly once) |
| `verify(mock, times(n))` | Called exactly n times |
| `verify(mock, never())` | NEVER called |
| `verify(mock, atLeast(n))` | Called at least n times |
| `verify(mock, atMost(n))` | Called at most n times |
| `verify(mock, atLeastOnce())` | Called 1 or more times |
| `verifyNoMoreInteractions(mock)` | No other methods called |
| `verifyNoInteractions(mock)` | Nothing called on mock |
| `inOrder(...).verify(...)` | Called in specific order |

---

## 1️⃣ Basic Verification

```java
// After running your code...
verify(userRepository).findById(1L);  // Was findById(1L) called?
verify(emailService).sendWelcomeEmail("test@email.com", "Test");
```

---

## 2️⃣ Verification Modes

### times(n) - Exact count

```java
verify(mock, times(3)).method();  // Called exactly 3 times
```

### never() - Not called

```java
verify(mock, never()).method();  // NEVER called
verify(emailService, never()).sendEmail(any());  // Common pattern!
```

### atLeast/atMost

```java
verify(mock, atLeast(2)).method();   // Called 2 or more times
verify(mock, atMost(5)).method();    // Called 5 or fewer times
verify(mock, atLeastOnce()).method(); // Called 1+ times
```

---

## 3️⃣ Verification with Argument Matchers

```java
// Specific argument
verify(repo).findById(1L);
verify(repo).findById(eq(1L));  // Same thing

// Any argument
verify(repo).findById(anyLong());
verify(email).sendEmail(anyString());

// Custom condition
verify(repo).save(argThat(user -> user.getName().equals("Ramesh")));
```

---

## 4️⃣ InOrder - Verify Call Order

```java
InOrder inOrder = inOrder(repository, emailService);

inOrder.verify(repository).save(any());     // First this
inOrder.verify(emailService).sendEmail(any()); // Then this

// If order is wrong, test fails!
```

---

## 5️⃣ verifyNoInteractions / verifyNoMoreInteractions

```java
// No methods called on mock at all
verifyNoInteractions(emailService);

// After verifying specific calls, no OTHER calls happened
verify(repo).findById(1L);
verifyNoMoreInteractions(repo);  // Nothing else was called
```

---

## 📋 Quick Reference

```
╔════════════════════════════════════════════════════════════════════╗
║  VERIFICATION QUICK REFERENCE                                     ║
╠════════════════════════════════════════════════════════════════════╣
║  verify(mock).method()           → Called once                    ║
║  verify(mock, times(3)).method() → Called exactly 3 times         ║
║  verify(mock, never()).method()  → NEVER called                   ║
║  verify(mock, atLeast(2))        → Called 2+ times                ║
║  verify(mock, atMost(5))         → Called max 5 times             ║
║  verifyNoInteractions(mock)      → Nothing called                 ║
║  verifyNoMoreInteractions(mock)  → No OTHER calls                 ║
╚════════════════════════════════════════════════════════════════════╝
```

---

## 📎 Related Files

- **Test examples:** [Part3_VerificationTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/mockito/Part3_VerificationTest.java)
