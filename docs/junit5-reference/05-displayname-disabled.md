# 5️⃣ DisplayName & Disabled Tests

> **Test file:** [Part3_DisplayNameAndDisabledTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/service/Part3_DisplayNameAndDisabledTest.java)

---

## 🎯 What These Do

| Annotation | Purpose |
|------------|---------|
| `@DisplayName` | Custom readable test name |
| `@Disabled` | Skip/disable a test |
| `@TestMethodOrder` | Control test execution order |

---

## 1️⃣ `@DisplayName` - Readable Test Names

### Problem Without It

```java
@Test
void testAdditionOfTwoPositiveNumbersShouldReturnCorrectSum() { }
// Test report shows: testAdditionOfTwoPositiveNumbersShouldReturnCorrectSum
// Hard to read!
```

### Solution

```java
@Test
@DisplayName("➕ Addition: Two positive numbers should return correct sum")
void testAddition() { }
// Test report shows: ➕ Addition: Two positive numbers should return correct sum
// Easy to read! Supports emojis!
```

### On Class Level

```java
@DisplayName("📚 Calculator Service Tests")
class CalculatorTest { }
```

---

## 2️⃣ `@Disabled` - Skip Tests

### Why Disable Tests?

- Feature not yet implemented
- Known bug waiting for fix
- Test temporarily broken

### Usage

```java
@Test
@Disabled("Feature coming in Sprint 5")
void testFutureFeature() {
    // This test will be SKIPPED, not failed
}

@Test
@Disabled("Bug #1234 - waiting for fix")
void testWithBug() { }
```

### Disable Entire Class

```java
@Disabled("All tests broken due to refactoring")
class BrokenTests { }
```

---

## 3️⃣ `@TestMethodOrder` - Control Execution Order

### Available Orderers

| Orderer | Behavior |
|---------|----------|
| `OrderAnnotation.class` | By `@Order` annotation |
| `MethodName.class` | Alphabetically by method name |
| `DisplayName.class` | Alphabetically by display name |
| `Random.class` | Random order |

### Using `@Order`

```java
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderedTests {
    
    @Test @Order(1) void firstTest() { }
    @Test @Order(2) void secondTest() { }
    @Test @Order(3) void thirdTest() { }
}
```

---

## 📎 Related Files

- **Test examples:** [Part3_DisplayNameAndDisabledTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/service/Part3_DisplayNameAndDisabledTest.java)
