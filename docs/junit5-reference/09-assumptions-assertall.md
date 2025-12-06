# 9️⃣ assertAll & Assumptions - Detailed Guide

> **Test file:** [Part10_RemainingConceptsTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/service/Part10_RemainingConceptsTest.java)

---

# Part A: assertAll - Grouped Assertions

---

## 🎯 assertAll Enti? Enduku Kavali?

### Problem Without assertAll

```java
@Test
void testUserCreation() {
    User user = userService.createUser("Ramesh", "ramesh@email.com");
    
    assertEquals("Ramesh", user.getName());         // If fails HERE...
    assertEquals("ramesh@email.com", user.getEmail()); // ...this NEVER runs
    assertNotNull(user.getId());                    // ...this NEVER runs
    assertTrue(user.isActive());                    // ...this NEVER runs
}
```

**Problem:** First failure stops execution. You only see ONE error at a time!

**Multiple test runs to find all bugs:**
```
Run 1: Name assertion failed
Run 2: (fix name) Email assertion failed  
Run 3: (fix email) ID assertion failed
Run 4: (fix ID) Active assertion failed
Run 5: Finally all pass!
```

😤 **4 test runs to find 4 bugs!**

---

### Solution: assertAll

```java
@Test
void testUserCreation() {
    User user = userService.createUser("Ramesh", "ramesh@email.com");
    
    assertAll("User creation validation",
        () -> assertEquals("Ramesh", user.getName()),
        () -> assertEquals("ramesh@email.com", user.getEmail()),
        () -> assertNotNull(user.getId()),
        () -> assertTrue(user.isActive())
    );
}
```

**Now:** ALL assertions run, ALL failures reported at once!

```
org.opentest4j.MultipleFailuresError: User creation validation (3 failures)
    org.opentest4j.AssertionFailedError: expected: <Ramesh> but was: <null>
    org.opentest4j.AssertionFailedError: expected: <ramesh@email.com> but was: <null>
    java.lang.AssertionError: expected not null
```

🎉 **One test run, see all 3 bugs!**

---

## 📝 assertAll Syntax

### Basic

```java
assertAll(
    () -> assertTrue(condition1),
    () -> assertEquals(expected, actual),
    () -> assertNotNull(object)
);
```

### With Heading (Recommended)

```java
assertAll("Heading shown in error message",
    () -> assertTrue(condition1),
    () -> assertEquals(expected, actual)
);
```

### Nested Groups

```java
assertAll("Complete User Validation",
    () -> assertAll("Name validation",
        () -> assertNotNull(user.getFirstName()),
        () -> assertNotNull(user.getLastName()),
        () -> assertTrue(user.getFullName().length() > 0)
    ),
    () -> assertAll("Contact validation",
        () -> assertNotNull(user.getEmail()),
        () -> assertTrue(user.getEmail().contains("@")),
        () -> assertNotNull(user.getPhone())
    ),
    () -> assertAll("Account status",
        () -> assertTrue(user.isActive()),
        () -> assertFalse(user.isBlocked())
    )
);
```

**Error shows hierarchy:**
```
MultipleFailuresError: Complete User Validation (2 failures)
    MultipleFailuresError: Name validation (1 failure)
        expected not null: lastName
    MultipleFailuresError: Contact validation (1 failure)
        expected not null: phone
```

---

## 🏠 Real-World Use Cases

### 1. API Response Validation

```java
@Test
void testApiResponse() {
    ApiResponse response = callApi();
    
    assertAll("API Response",
        () -> assertEquals(200, response.getStatusCode()),
        () -> assertEquals("success", response.getStatus()),
        () -> assertNotNull(response.getData()),
        () -> assertTrue(response.getTimestamp() > 0)
    );
}
```

### 2. Object Equality Check

```java
@Test
void testObjectClone() {
    Product original = new Product("Phone", 999.99);
    Product clone = original.clone();
    
    assertAll("Clone should match original",
        () -> assertEquals(original.getName(), clone.getName()),
        () -> assertEquals(original.getPrice(), clone.getPrice()),
        () -> assertNotSame(original, clone)  // Different objects!
    );
}
```

### 3. Form Validation

```java
@Test
void testFormValidation() {
    ValidationResult result = validator.validate(invalidForm);
    
    assertAll("Form should have 3 errors",
        () -> assertFalse(result.isValid()),
        () -> assertEquals(3, result.getErrors().size()),
        () -> assertTrue(result.hasError("name")),
        () -> assertTrue(result.hasError("email")),
        () -> assertTrue(result.hasError("phone"))
    );
}
```

---

## ⚠️ Important: Lambda Syntax Required

```java
// ❌ WRONG - assertions run immediately, no grouping!
assertAll("Test",
    assertEquals(5, add(2, 3)),    // Runs NOW
    assertTrue(isPositive(1))       // Runs NOW
);

// ✅ CORRECT - lambdas delay execution!
assertAll("Test",
    () -> assertEquals(5, add(2, 3)),    // Deferred
    () -> assertTrue(isPositive(1))       // Deferred
);
```

---

# Part B: Assumptions

---

## 🎯 Assumptions Enti? Enduku Kavali?

**Assumptions = Conditions for test to run**

If assumption **fails** → Test is **SKIPPED** (not failed!)

---

## 🆚 Assertions vs Assumptions

| Aspect | Assertion | Assumption |
|--------|-----------|------------|
| If false | Test **FAILS** ❌ | Test **SKIPS** ⏭️ |
| Purpose | Verify correctness | Check preconditions |
| Example | `assertTrue(result == 5)` | `assumeTrue(isWindows())` |
| Meaning | "This MUST be true" | "Only run IF this is true" |

---

## 📝 Three Assumption Methods

### 1. assumeTrue - Skip if FALSE

```java
@Test
void windowsOnlyTest() {
    // If NOT Windows, skip this test (no failure!)
    assumeTrue(System.getProperty("os.name").contains("Windows"),
               "This test requires Windows");
    
    // Only runs on Windows
    testWindowsSpecificFeature();
}
```

**Results:**
- On Windows: Test RUNS ✅
- On Mac/Linux: Test SKIPPED ⏭️ (not failed!)

### 2. assumeFalse - Skip if TRUE

```java
@Test
void skipInCI() {
    // Skip this test if running in CI
    assumeFalse("true".equals(System.getenv("CI")),
                "Skipping in CI environment");
    
    // Only runs locally
    testLocalFeature();
}
```

### 3. assumingThat - Conditional Part

```java
@Test
void mixedTest() {
    // This part ONLY runs if condition is true
    assumingThat(isWindows(), () -> {
        System.out.println("Windows-specific test");
        testWindowsRegistry();
    });
    
    // This part ALWAYS runs, regardless of above condition!
    assertEquals(5, calculator.add(2, 3));
    System.out.println("This always runs!");
}
```

---

## 🏠 Real-World Use Cases

### 1. Environment-Specific Tests

```java
@Test
void testDeveloperFeatures() {
    assumeTrue("dev".equals(System.getenv("ENVIRONMENT")),
               "Only run in dev environment");
    
    // Developer-only features
    testDebugMode();
}
```

### 2. Skip Slow Tests Locally

```java
@Test
void testHeavyOperation() {
    assumeTrue("true".equals(System.getenv("RUN_SLOW_TESTS")),
               "Set RUN_SLOW_TESTS=true to enable");
    
    // Takes 5 minutes
    heavyIntegrationTest();
}
```

### 3. Optional Dependency

```java
@Test
void testWithRedis() {
    assumeTrue(isRedisAvailable(),
               "Redis not available, skipping");
    
    // Redis-dependent test
    testRedisCache();
}

boolean isRedisAvailable() {
    // Check if Redis is running
}
```

### 4. Feature Flag

```java
@Test
void testNewFeature() {
    assumeTrue("true".equals(System.getProperty("feature.newUI")),
               "New UI feature not enabled");
    
    testNewUIComponent();
}
```

---

# Part C: @TestInstance

---

## 🎯 @TestInstance Enti?

**Controls how JUnit creates test class instances.**

---

## 📋 Two Modes

### PER_METHOD (Default)

```java
class MyTest {  // No annotation = PER_METHOD
    
    private int counter = 0;
    
    @Test void test1() { 
        counter++;  // counter = 1
    }
    
    @Test void test2() { 
        counter++;  // counter = 1 (NEW instance!)
    }
}
```

**Each test gets FRESH instance → counter resets!**

### PER_CLASS

```java
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MyTest {
    
    private int counter = 0;
    
    @Test void test1() { 
        counter++;  // counter = 1
    }
    
    @Test void test2() { 
        counter++;  // counter = 2 (SAME instance!)
    }
}
```

**Same instance for all tests → state preserved!**

---

## 📝 Key Differences

| Aspect | PER_METHOD (default) | PER_CLASS |
|--------|---------------------|-----------|
| **Instance** | New for each test | Same for all tests |
| **State** | Isolated | Shared |
| **@BeforeAll** | MUST be static | Can be non-static! |
| **@AfterAll** | MUST be static | Can be non-static! |
| **Memory** | More objects created | Single object |
| **Independence** | Tests fully independent | Tests may affect each other |

---

## 🤔 When to Use PER_CLASS?

### 1. Non-static @BeforeAll

```java
@TestInstance(Lifecycle.PER_CLASS)
class MyTest {
    
    private Database db;  // Instance field
    
    @BeforeAll
    void setUp() {  // NOT static!
        db = new Database();
        db.connect();
    }
    
    @Test void test1() { db.query(); }
    @Test void test2() { db.query(); }
}
```

### 2. Expensive Setup

```java
@TestInstance(Lifecycle.PER_CLASS)
class IntegrationTest {
    
    private Container container;
    
    @BeforeAll
    void startContainer() {
        container = new Container();
        container.start();  // 30 seconds - only once!
    }
    
    @Test void test1() { }
    @Test void test2() { }
    // 50 tests share same container
}
```

### 3. @Nested with @BeforeAll

```java
class OuterTest {
    
    @Nested
    @TestInstance(Lifecycle.PER_CLASS)  // Required for @BeforeAll in nested!
    class InnerTest {
        
        @BeforeAll
        void setUp() { }  // Works now!
    }
}
```

---

## ⚠️ Warning: State Pollution

```java
@TestInstance(Lifecycle.PER_CLASS)
class DangerousTest {
    
    private List<String> items = new ArrayList<>();
    
    @Test void test1() {
        items.add("test1");  // List: [test1]
    }
    
    @Test void test2() {
        // ⚠️ List still has "test1"!
        // Test order matters now!
        assertEquals(0, items.size());  // ❌ FAILS!
    }
}
```

**Solution:** Clean up in @BeforeEach

```java
@BeforeEach
void cleanUp() {
    items.clear();  // Reset state before each test
}
```

---

## 📎 Related Files

- **Examples:** [Part10_RemainingConceptsTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/service/Part10_RemainingConceptsTest.java)

**Run chesi chudu:**
```powershell
.\mvnw.cmd test -Dtest=Part10_RemainingConceptsTest
```
