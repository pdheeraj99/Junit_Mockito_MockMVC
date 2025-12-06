# 2️⃣ Assertions - Basic

> **Test file:** [Part1_LifecycleAndBasicAssertionsTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/service/Part1_LifecycleAndBasicAssertionsTest.java)

---

## 🎯 Assertions Enti?

**Assertion = Verification = Check**

Test lo neeku expected result vachinda leda ani **verify** cheyyatam. Correct aite test **PASS**, wrong aite **FAIL**.

```java
// Syntax: assertXxx(expected, actual)
assertEquals(5, calculator.add(2, 3));  // 5 expect chestunnam
```

---

## 📋 All Assertions at a Glance

| Assertion | Emi Check Chestundi | Example |
|-----------|-------------------|---------|
| `assertEquals` | Two values **equal** aa? | `assertEquals(5, result)` |
| `assertNotEquals` | Two values **different** aa? | `assertNotEquals(0, result)` |
| `assertTrue` | Condition **true** aa? | `assertTrue(isPositive)` |
| `assertFalse` | Condition **false** aa? | `assertFalse(isEmpty)` |
| `assertNull` | Value **null** aa? | `assertNull(response)` |
| `assertNotNull` | Value **not null** aa? | `assertNotNull(user)` |
| `assertSame` | **Same object** (reference) aa? | `assertSame(obj1, obj2)` |
| `assertNotSame` | **Different objects** aa? | `assertNotSame(obj1, obj2)` |
| `assertArrayEquals` | **Arrays equal** aa? | `assertArrayEquals(arr1, arr2)` |
| `assertIterableEquals` | **Lists equal** aa? | `assertIterableEquals(list1, list2)` |

---

## 📝 Detailed Explanations (Telugu lo)

### `assertEquals` - Equal aa Check Cheyyi

**Most commonly used assertion!**

```java
// Basic usage
assertEquals(expected, actual);

// With message (if fails, ee message show avtundi)
assertEquals(expected, actual, "Custom error message");

// With lazy message (efficient - only creates string if fails)
assertEquals(expected, actual, () -> "Computed message: " + someValue);
```

**Examples:**
```java
// Primitives
assertEquals(5, calculator.add(2, 3));
assertEquals(3.14, radius, 0.001);  // doubles ki delta kavali

// Objects (uses .equals() method)
assertEquals("HELLO", str.toUpperCase());
assertEquals(expectedList, actualList);
```

---

### `assertNotEquals` - Different aa Check Cheyyi

```java
assertNotEquals(0, calculator.divide(10, 2));  // 5 return avtundi, 0 kaadu
assertNotEquals(oldValue, newValue);  // Values different ani confirm
```

**Eppudu Use Chestam?**
- Value oka specific value **kaadu** ani guarantee kavali
- After update, old value kaadu ani verify

---

### `assertTrue` / `assertFalse` - Boolean Conditions

```java
// assertTrue - condition TRUE avvali
assertTrue(calculator.isPositive(5));
assertTrue(list.isEmpty());
assertTrue(age >= 18, "User must be adult");

// assertFalse - condition FALSE avvali  
assertFalse(calculator.isPositive(-5));
assertFalse(errors.hasErrors());
```

**Real-World Examples:**
```java
// User validation
assertTrue(user.isActive());
assertFalse(user.isBlocked());

// Collection checks
assertTrue(results.size() > 0);
assertFalse(errors.isEmpty());
```

---

### `assertNull` / `assertNotNull` - Null Checks

```java
// assertNull - value NULL avvali
assertNull(calculator.processInput(null));
assertNull(repository.findById(-1));  // Not found returns null

// assertNotNull - value NULL kaakudadu
assertNotNull(user.getName());
assertNotNull(response.getBody());
```

**Common Use Cases:**
```java
// Optional empty check
assertNull(optional.orElse(null));

// Service response validation
UserDto user = userService.findById(1);
assertNotNull(user, "User should exist");
assertNotNull(user.getEmail(), "Email is required");
```

---

### `assertSame` / `assertNotSame` - Reference Check

**IMPORTANT:** Ivi **same object in memory** aa ani check chestyi, **content** kaadu!

```java
// assertSame - Same reference (memory location)
String s1 = "hello";  // String pool
String s2 = "hello";  // Same pool reference
assertSame(s1, s2);   // ✅ PASS - same object

// assertNotSame - Different references
String s3 = new String("hello");
String s4 = new String("hello");
assertNotSame(s3, s4);  // ✅ PASS - different objects
assertEquals(s3, s4);    // ✅ PASS - same content though!
```

**Difference from assertEquals:**
| Assertion | Checks | Primitives | Objects |
|-----------|--------|------------|---------|
| `assertEquals` | **Value/Content** | Works | Uses `.equals()` |
| `assertSame` | **Memory Reference** | Works | Uses `==` |

---

### `assertArrayEquals` - Array Content Check

**⚠️ IMPORTANT:** Arrays ki `assertEquals` use cheyyakudadu!

```java
int[] expected = {1, 2, 3};
int[] actual = calculator.getSquares(1, 2, 3);

// ❌ WRONG - compares references!
assertEquals(expected, actual);  // FAILS even if content same!

// ✅ CORRECT - compares content
assertArrayEquals(expected, actual);
```

**Enduku assertEquals fail avtundi?**
- Arrays lo `.equals()` override cheyaledu
- Default `Object.equals()` uses `==` (reference comparison)

---

### `assertIterableEquals` - List/Collection Check

```java
List<Integer> expected = Arrays.asList(1, 2, 3);
List<Integer> actual = calculator.getMultiples(1, 3);

assertIterableEquals(expected, actual);
```

**Note:** `assertEquals` kuda Lists ki work chestundi because List interface `.equals()` properly override chesindi.

```java
// Both work for Lists:
assertEquals(list1, list2);        // ✅ Works
assertIterableEquals(list1, list2); // ✅ Also works
```

---

## 🎯 Custom Messages

**Test fail aite readable message kavali:**

```java
// Simple message
assertEquals(5, result, "Addition should return 5");

// Lazy message (only computed if test fails)
assertEquals(expected, actual, 
    () -> "Expected " + expected + " but got " + actual);
```

**Output when fails:**
```
org.opentest4j.AssertionFailedError: Addition should return 5
Expected: 5
Actual: 3
```

---

## 📎 Quick Reference Card

```
╔══════════════════════════════════════════════════════════════╗
║  ASSERTION QUICK REFERENCE                                   ║
╠══════════════════════════════════════════════════════════════╣
║  assertEquals(expected, actual)     → Values same?           ║
║  assertNotEquals(unexpected, actual)→ Values different?      ║
║  assertTrue(condition)              → Is true?               ║
║  assertFalse(condition)             → Is false?              ║
║  assertNull(object)                 → Is null?               ║
║  assertNotNull(object)              → Is NOT null?           ║
║  assertSame(obj1, obj2)             → Same reference?        ║
║  assertArrayEquals(arr1, arr2)      → Arrays equal?          ║
║  assertIterableEquals(iter1, iter2) → Iterables equal?       ║
╚══════════════════════════════════════════════════════════════╝
```

---

## 📎 Related Files

- **Test examples:** [Part1_LifecycleAndBasicAssertionsTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/service/Part1_LifecycleAndBasicAssertionsTest.java)
- **Primitives vs Objects (IMPORTANT!):** [03-assertions-primitives-vs-objects.md](./03-assertions-primitives-vs-objects.md)
