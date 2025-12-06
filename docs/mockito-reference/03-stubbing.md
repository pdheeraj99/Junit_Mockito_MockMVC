# 3️⃣ Stubbing - when().thenReturn() & More

> **Test file:** [Part2_StubbingTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/mockito/Part2_StubbingTest.java)

---

## 🎯 Stubbing Enti?

**Stubbing = "Mock ki what to return cheppatam"**

```java
when(mock.method()).thenReturn(value);
//    ↑              ↑
//    Method call    Value to return
```

---

## 📋 All Stubbing Methods

| Method | Purpose | Example |
|--------|---------|---------|
| `thenReturn(value)` | Return specific value | `when(repo.find(1)).thenReturn(user)` |
| `thenThrow(exception)` | Throw exception | `when(service.call()).thenThrow(new RuntimeException())` |
| `thenAnswer(Answer)` | Custom logic | `when(repo.save(any())).thenAnswer(inv -> ...)` |
| `thenCallRealMethod()` | Call real implementation | For partial mocks |
| `doReturn(value).when(mock)` | Alternative syntax | For void methods and spies |
| `doThrow(exception).when(mock)` | Throw for void methods | `doThrow(...).when(service).voidMethod()` |
| `doNothing().when(mock)` | Do nothing (void) | `doNothing().when(service).delete()` |

---

## 1️⃣ thenReturn() - Simple Return Value

### Single Value

```java
User user = new User(1L, "Ramesh", "ramesh@email.com");
when(userRepository.findById(1L)).thenReturn(Optional.of(user));

// Now mock returns this user
Optional<User> result = userRepository.findById(1L);
assertEquals("Ramesh", result.get().getName());
```

### Multiple Values (consecutive calls)

```java
// First call returns "A", second returns "B", third returns "C"
when(service.getValue()).thenReturn("A", "B", "C");

assertEquals("A", service.getValue());  // 1st call
assertEquals("B", service.getValue());  // 2nd call
assertEquals("C", service.getValue());  // 3rd call
assertEquals("C", service.getValue());  // 4th+ call: repeats last value
```

---

## 2️⃣ thenThrow() - Simulate Exceptions

### Throw Exception

```java
when(userRepository.findById(999L))
    .thenThrow(new RuntimeException("Database error"));

// Now calling this throws exception
assertThrows(RuntimeException.class, () -> {
    userRepository.findById(999L);
});
```

### Exception Class Only

```java
when(service.call()).thenThrow(RuntimeException.class);
// Mockito creates default exception
```

---

## 3️⃣ thenAnswer() - Custom Logic

### Use When You Need:
- Access to method arguments
- Dynamic return values
- Complex logic

### Basic Answer

```java
when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
    // Get the argument passed to save()
    User userArg = invocation.getArgument(0);
    
    // Modify it (simulate DB assigning ID)
    userArg.setId(System.currentTimeMillis());
    
    // Return it
    return userArg;
});
```

### With InvocationOnMock

```java
when(service.process(anyString())).thenAnswer(inv -> {
    String input = inv.getArgument(0);  // First argument
    return input.toUpperCase();         // Return modified value
});

assertEquals("HELLO", service.process("hello"));
assertEquals("WORLD", service.process("world"));
```

---

## 4️⃣ doReturn/doThrow/doNothing - Alternative Syntax

### When to Use This Syntax?

1. **Void methods** (can't use `when()` with void)
2. **Spy objects** (real objects can throw when called)

### doReturn

```java
// Standard way (works for non-void)
when(mock.method()).thenReturn(value);

// Alternative (works for void too, required for spies)
doReturn(value).when(mock).method();
```

### doThrow for Void Methods

```java
// Can't do: when(service.delete(1L)).thenThrow(...)  ← COMPILE ERROR!
// Because delete() returns void!

// Use this instead:
doThrow(new RuntimeException("Cannot delete"))
    .when(service).delete(1L);

assertThrows(RuntimeException.class, () -> service.delete(1L));
```

### doNothing for Void Methods

```java
// Do nothing when this void method is called
doNothing().when(emailService).sendEmail(any());

// Now calling it does nothing
emailService.sendEmail("test@email.com");  // No exception, just nothing happens
```

---

## 5️⃣ Stubbing with Argument Matchers

```java
// Specific argument
when(repo.findById(1L)).thenReturn(Optional.of(user1));
when(repo.findById(2L)).thenReturn(Optional.of(user2));

// Any argument
when(repo.findById(anyLong())).thenReturn(Optional.of(defaultUser));

// Matching condition
when(repo.findById(argThat(id -> id > 100))).thenThrow(new RuntimeException());
```

**Note:** If you use matchers, ALL arguments must be matchers!

```java
// ❌ WRONG - mixing matcher and literal
when(service.method(anyString(), "literal")).thenReturn(value);

// ✅ CORRECT - all matchers
when(service.method(anyString(), eq("literal"))).thenReturn(value);
```

---

## 6️⃣ Chained Stubbing

```java
// First call: return value, Second call: throw exception
when(service.call())
    .thenReturn("Success")
    .thenThrow(new RuntimeException("Failed on second call"));

assertEquals("Success", service.call());  // 1st call
assertThrows(RuntimeException.class, () -> service.call());  // 2nd call
```

---

## 📋 Quick Reference

```
╔════════════════════════════════════════════════════════════════════╗
║  STUBBING QUICK REFERENCE                                         ║
╠════════════════════════════════════════════════════════════════════╣
║  when(mock.method()).thenReturn(value)                            ║
║     → Return specific value                                       ║
║                                                                    ║
║  when(mock.method()).thenThrow(exception)                         ║
║     → Throw exception                                             ║
║                                                                    ║
║  when(mock.method()).thenAnswer(inv -> { ... })                   ║
║     → Custom logic with access to arguments                       ║
║                                                                    ║
║  doReturn(value).when(mock).voidMethod()                          ║
║     → For void methods and spies                                  ║
║                                                                    ║
║  doThrow(exception).when(mock).voidMethod()                       ║
║     → Throw from void methods                                     ║
║                                                                    ║
║  doNothing().when(mock).voidMethod()                              ║
║     → Do nothing (useful for void methods you want to skip)       ║
╚════════════════════════════════════════════════════════════════════╝
```

---

## 📎 Related Files

- **Test examples:** [Part2_StubbingTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/mockito/Part2_StubbingTest.java)
