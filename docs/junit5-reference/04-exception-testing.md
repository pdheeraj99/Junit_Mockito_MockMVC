# 4️⃣ Exception Testing

> **Test file:** [Part2_ExceptionTestingTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/service/Part2_ExceptionTestingTest.java)

---

## 🎯 Exception Testing Enti?

**Code error throw chestunte, aa error correct ga throw avtunda** ani test cheyyatam.

**Example:** Division by zero aite `ArithmeticException` ravali!

---

## 📋 Three Main Methods

| Method | Purpose |
|--------|---------|
| `assertThrows` | Exception throw avvali (subclass OK) |
| `assertThrowsExactly` | **Exact** exception type throw avvali |
| `assertDoesNotThrow` | Exception throw avvakudadu |

---

## 1️⃣ `assertThrows` - Exception Expect Chestunnam

### Basic Usage

```java
// Syntax
assertThrows(ExpectedException.class, () -> {
    codeToTest();
});
```

### Examples

```java
// Division by zero
assertThrows(ArithmeticException.class, () -> {
    calculator.divide(10, 0);
});

// Invalid input
assertThrows(IllegalArgumentException.class, () -> {
    calculator.factorial(-5);
});
```

### Capturing Exception for Further Checks

```java
// Capture the exception
ArithmeticException exception = assertThrows(
    ArithmeticException.class,
    () -> calculator.divide(10, 0)
);

// Now verify message
assertEquals("Cannot divide by zero!", exception.getMessage());
```

### Subclass Behavior

```java
// ArithmeticException extends RuntimeException
// So this ALSO passes:
assertThrows(RuntimeException.class, () -> calculator.divide(10, 0));
// ✅ PASS - ArithmeticException IS-A RuntimeException
```

---

## 2️⃣ `assertThrowsExactly` - Exact Type Only

### Difference from `assertThrows`

| Aspect | `assertThrows` | `assertThrowsExactly` |
|--------|---------------|----------------------|
| Subclass matches? | ✅ Yes | ❌ No |
| Exact type matches? | ✅ Yes | ✅ Yes |

### Example

```java
// Code throws: ArithmeticException

// assertThrows - subclass OK
assertThrows(RuntimeException.class, () -> divide(10, 0));      // ✅ PASS
assertThrows(ArithmeticException.class, () -> divide(10, 0));   // ✅ PASS

// assertThrowsExactly - EXACT type only
assertThrowsExactly(ArithmeticException.class, () -> divide(10, 0)); // ✅ PASS
assertThrowsExactly(RuntimeException.class, () -> divide(10, 0));    // ❌ FAIL!
```

### Eppudu Use Chestam?

- **Strict testing** kavali aite → `assertThrowsExactly`
- **General testing** (subclass OK) → `assertThrows`

---

## 3️⃣ `assertDoesNotThrow` - No Exception Expected

### Usage

```java
// Valid operations should NOT throw
assertDoesNotThrow(() -> calculator.divide(10, 2));
assertDoesNotThrow(() -> calculator.factorial(5));
```

### Capturing Return Value

```java
// Run and capture result
double result = assertDoesNotThrow(() -> calculator.divide(10, 2));
assertEquals(5.0, result);
```

---

## 📋 Quick Reference

```
╔══════════════════════════════════════════════════════════════════╗
║  EXCEPTION TESTING QUICK REFERENCE                              ║
╠══════════════════════════════════════════════════════════════════╣
║  assertThrows(Exception.class, () -> code)                      ║
║     → Exception or subclass throw avvali                        ║
║                                                                  ║
║  assertThrowsExactly(Exception.class, () -> code)               ║
║     → EXACT exception type throw avvali (no subclass!)          ║
║                                                                  ║
║  assertDoesNotThrow(() -> code)                                 ║
║     → Exception throw avvakudadu                                ║
╚══════════════════════════════════════════════════════════════════╝
```

---

## 📎 Related Files

- **Test examples:** [Part2_ExceptionTestingTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/service/Part2_ExceptionTestingTest.java)
