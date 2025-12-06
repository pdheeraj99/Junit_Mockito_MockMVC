# 7️⃣ Nested & Repeated Tests - Detailed Guide

> **Test files:** 
> - [Part7_NestedTestsTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/service/Part7_NestedTestsTest.java)
> - [Part8_RepeatedTestsTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/service/Part8_RepeatedTestsTest.java)

---

# Part A: @Nested Tests

---

## 🎯 @Nested Enti? Enduku Kavali?

**Problem chuddam first:**

Okka test class lo 50 tests unte, anni flat ga undi confusing ga untundi:

```java
class CalculatorTest {
    void testAddPositive() { }
    void testAddNegative() { }
    void testAddZero() { }
    void testSubtractPositive() { }
    void testSubtractNegative() { }
    void testDivideValid() { }
    void testDivideByZero() { }
    // ... 50 more tests - MESSY!
}
```

**Test report:**
```
CalculatorTest
  ✓ testAddPositive
  ✓ testAddNegative
  ✓ testAddZero
  ✓ testSubtractPositive
  ... (hard to read!)
```

**Solution: @Nested**

Related tests ni **group** cheyochu, like folders in your computer:

```java
class CalculatorTest {
    
    @Nested
    class AdditionTests {
        void testPositive() { }
        void testNegative() { }
        void testZero() { }
    }
    
    @Nested
    class SubtractionTests {
        void testPositive() { }
        void testNegative() { }
    }
    
    @Nested
    class DivisionTests {
        void testValid() { }
        void testByZero() { }
    }
}
```

**Test report ippudu:**
```
CalculatorTest
  └── AdditionTests
      ├── ✓ testPositive
      ├── ✓ testNegative
      └── ✓ testZero
  └── SubtractionTests
      ├── ✓ testPositive
      └── ✓ testNegative
  └── DivisionTests
      ├── ✓ testValid
      └── ✓ testByZero
```

**Beautiful and organized!** 🎉

---

## 📋 @Nested Rules - Important Points

| Rule | Explanation |
|------|-------------|
| **Must be non-static** | `@Nested` classes cannot be `static` |
| **Inner class** | `@Nested` class must be inside another test class |
| **Can nest multiple levels** | Nested lo inka nested pettochu (unlimited depth) |
| **Inherits parent's @BeforeEach** | Parent class setup automatically applies |
| **No @BeforeAll by default** | `@BeforeAll` in nested needs special config |

---

## 🔄 Lifecycle in Nested Classes - IMPORTANT!

**Edi order lo run avtundi:**

```java
class OuterTest {
    
    @BeforeEach
    void outerSetup() {
        System.out.println("1. OUTER @BeforeEach");
    }
    
    @AfterEach
    void outerTeardown() {
        System.out.println("4. OUTER @AfterEach");
    }
    
    @Nested
    class InnerTest {
        
        @BeforeEach
        void innerSetup() {
            System.out.println("2. INNER @BeforeEach");
        }
        
        @AfterEach
        void innerTeardown() {
            System.out.println("3. INNER @AfterEach");
        }
        
        @Test
        void myTest() {
            System.out.println("   TEST running...");
        }
    }
}
```

**Output:**
```
1. OUTER @BeforeEach    ← Parent first
2. INNER @BeforeEach    ← Then child
   TEST running...
3. INNER @AfterEach     ← Child cleanup first
4. OUTER @AfterEach     ← Then parent cleanup
```

**Key Point:** Parent setup ALWAYS runs before child setup!

---

## 🎭 BDD Style Testing - Given-When-Then

**BDD = Behavior Driven Development**

Test ni story laga raastam:
- **Given** = Starting condition
- **When** = Action performed
- **Then** = Expected result

### Example Without BDD (Hard to understand):

```java
@Test
void testFactorialNegativeThrows() { }

@Test
void testFactorialZeroReturnsOne() { }

@Test
void testFactorialFiveReturns120() { }
```

### Example With BDD (Clear story!):

```java
@Nested
class Given_ValidInput {
    
    @Nested
    class When_InputIsZero {
        
        @Test
        void Then_ReturnOne() {
            assertEquals(1, calculator.factorial(0));
        }
    }
    
    @Nested
    class When_InputIsPositive {
        
        @Test
        void Then_ReturnCorrectFactorial() {
            assertEquals(120, calculator.factorial(5));
        }
    }
}

@Nested
class Given_InvalidInput {
    
    @Nested
    class When_InputIsNegative {
        
        @Test
        void Then_ThrowIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                () -> calculator.factorial(-1));
        }
    }
}
```

**Test Report:**
```
FactorialTest
  └── Given_ValidInput
      ├── When_InputIsZero
      │   └── ✓ Then_ReturnOne
      └── When_InputIsPositive
          └── ✓ Then_ReturnCorrectFactorial
  └── Given_InvalidInput
      └── When_InputIsNegative
          └── ✓ Then_ThrowIllegalArgumentException
```

**Like reading a story! Easy to understand!** 📖

---

## ⚠️ @BeforeAll in Nested - Tricky!

**By default, @BeforeAll in nested class DOESN'T WORK!**

```java
@Nested
class InnerTest {
    
    @BeforeAll
    static void setup() {  // ❌ ERROR! Nested can't be static
        // This won't work!
    }
}
```

**Solution: Use @TestInstance(PER_CLASS)**

```java
@Nested
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InnerTest {
    
    @BeforeAll
    void setup() {  // ✅ Now works! (no static needed)
        // Setup code
    }
}
```

---

## 🏠 Real-World Use Cases

### 1. Group by Feature

```java
class UserServiceTest {
    
    @Nested class Registration { }
    @Nested class Login { }
    @Nested class PasswordReset { }
    @Nested class ProfileUpdate { }
}
```

### 2. Group by Scenario

```java
class PaymentTest {
    
    @Nested class SuccessfulPayment { }
    @Nested class FailedPayment { }
    @Nested class RefundScenarios { }
}
```

### 3. Group by Input Type

```java
class ValidationTest {
    
    @Nested class ValidInputs { }
    @Nested class InvalidInputs { }
    @Nested class EdgeCases { }
}
```

---

# Part B: @RepeatedTest

---

## 🎯 @RepeatedTest Enti? Enduku Kavali?

**Same test ni multiple times run chestam!**

### Use Cases:

| Use Case | Why? |
|----------|------|
| **Flaky test detection** | Sometimes pass, sometimes fail - find bugs! |
| **Random data testing** | Different random values each run |
| **Consistency check** | Same input should ALWAYS give same output |
| **Stress testing** | Run many times to check stability |

---

## 📝 Basic Syntax

```java
@RepeatedTest(5)  // Run this test 5 times
void myTest() {
    assertEquals(5, calculator.add(2, 3));
}
```

**Output:**
```
myTest() :: repetition 1 of 5 ✓
myTest() :: repetition 2 of 5 ✓
myTest() :: repetition 3 of 5 ✓
myTest() :: repetition 4 of 5 ✓
myTest() :: repetition 5 of 5 ✓
```

---

## 🎨 Custom Display Names

### Available Placeholders

| Placeholder | Value |
|-------------|-------|
| `{displayName}` | Test method @DisplayName |
| `{currentRepetition}` | 1, 2, 3... |
| `{totalRepetitions}` | Total count |

### Examples

```java
// Default name
@RepeatedTest(3)
void test1() { }
// Output: repetition 1 of 3, repetition 2 of 3, repetition 3 of 3

// Custom: "Run #1"
@RepeatedTest(value = 3, name = "Run #{currentRepetition}")
void test2() { }
// Output: Run #1, Run #2, Run #3

// Custom with display name
@RepeatedTest(value = 3, name = "{displayName} - Attempt {currentRepetition}/{totalRepetitions}")
@DisplayName("Addition Test")
void test3() { }
// Output: Addition Test - Attempt 1/3, Addition Test - Attempt 2/3, Addition Test - Attempt 3/3
```

---

## 📊 RepetitionInfo - Access Repetition Details

**Test method lo current repetition info access cheyochu!**

```java
@RepeatedTest(5)
void testWithInfo(RepetitionInfo info) {
    int current = info.getCurrentRepetition();  // 1, 2, 3, 4, 5
    int total = info.getTotalRepetitions();     // 5
    
    System.out.println("Running repetition " + current + " of " + total);
    
    // Use repetition number as test data!
    int testNumber = current * 10;  // 10, 20, 30, 40, 50
    assertTrue(calculator.isPositive(testNumber));
}
```

### Different Scenarios Per Repetition

```java
@RepeatedTest(4)
void testDifferentScenarios(RepetitionInfo info) {
    switch (info.getCurrentRepetition()) {
        case 1 -> assertEquals(5, add(2, 3));
        case 2 -> assertEquals(0, add(-5, 5));
        case 3 -> assertEquals(-10, add(-3, -7));
        case 4 -> assertEquals(1000000, add(500000, 500000));
    }
}
```

---

## 🔍 Real-World Examples

### 1. Random Data Testing

```java
@RepeatedTest(100)
void testRandomEvenNumbers() {
    // Generate random even number
    int randomEven = (int)(Math.random() * 500) * 2;
    
    assertTrue(calculator.isEven(randomEven),
        "Failed for: " + randomEven);
}
```

### 2. Flaky Test Detection

```java
@RepeatedTest(50)  // Run 50 times
void testShouldNeverFail() {
    // If this fails even ONCE in 50 runs,
    // there's a bug in the code!
    assertEquals(120, factorial(5));
}
```

### 3. Performance Check

```java
@RepeatedTest(10)
void testPerformance() {
    long start = System.currentTimeMillis();
    
    for (int i = 0; i < 1000; i++) {
        calculator.complexOperation();
    }
    
    long duration = System.currentTimeMillis() - start;
    assertTrue(duration < 100, "Too slow: " + duration + "ms");
}
```

---

## 📋 @RepeatedTest vs @ParameterizedTest

| Aspect | @RepeatedTest | @ParameterizedTest |
|--------|--------------|-------------------|
| **Purpose** | Same test, same data, multiple times | Same test, different data |
| **Data source** | None (or use RepetitionInfo) | @ValueSource, @CsvSource, etc. |
| **Use case** | Consistency, flaky tests | Testing multiple inputs |

**Example comparison:**

```java
// RepeatedTest - same input 5 times
@RepeatedTest(5)
void testAdd() {
    assertEquals(5, add(2, 3));  // Same every time
}

// ParameterizedTest - different inputs
@ParameterizedTest
@CsvSource({"1,2,3", "5,5,10", "0,0,0"})
void testAdd(int a, int b, int expected) {
    assertEquals(expected, add(a, b));  // Different each time
}
```

---

## 📎 Related Files

- **@Nested examples:** [Part7_NestedTestsTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/service/Part7_NestedTestsTest.java)
- **@RepeatedTest examples:** [Part8_RepeatedTestsTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/service/Part8_RepeatedTestsTest.java)

**Run chesi chudu:**
```powershell
.\mvnw.cmd test -Dtest=Part7_NestedTestsTest
.\mvnw.cmd test -Dtest=Part8_RepeatedTestsTest
```
