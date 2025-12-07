# 6️⃣ Parameterized Tests (VERY USEFUL! 🔥)

> **Test files:** 
> - [Part5_ParameterizedTestsTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/service/Part5_ParameterizedTestsTest.java)
> - [Part6_CsvFileSourceTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/service/Part6_CsvFileSourceTest.java)

---

## 🎯 Parameterized Tests Enti?

**Same test logic, different input values**. DRY principle follow cheyyatam!

### Without Parameterized (BAD):
```java
@Test void testIsPositive_1() { assertTrue(isPositive(1)); }
@Test void testIsPositive_5() { assertTrue(isPositive(5)); }
@Test void testIsPositive_10() { assertTrue(isPositive(10)); }
// 100 values ante 100 methods! 😱
```

### With Parameterized (GOOD):
```java
@ParameterizedTest
@ValueSource(ints = {1, 5, 10, 100, 999})
void testIsPositive(int number) {
    assertTrue(isPositive(number));
}
// Okate method, 5 times run avtundi! 🎉
```

---

## 📋 All Parameter Sources

| Source | Use Case |
|--------|----------|
| `@ValueSource` | Simple single values |
| `@NullSource` | Null value |
| `@EmptySource` | Empty string/collection |
| `@NullAndEmptySource` | Both null and empty |
| `@EnumSource` | Enum values |
| `@CsvSource` | Inline CSV (multiple params) |
| `@CsvFileSource` | External CSV file |
| `@MethodSource` | Static method returns values |
| `@ArgumentsSource` | Custom provider class |

---

## 1️⃣ `@ValueSource` - Simple Values

### Supported Types
- `ints`, `longs`, `shorts`, `bytes`
- `doubles`, `floats`
- `chars`, `strings`, `classes`

### Examples

```java
// ints
@ParameterizedTest
@ValueSource(ints = {1, 5, 10, 100})
void testPositive(int number) { }

// strings
@ParameterizedTest
@ValueSource(strings = {"hello", "world"})
void testProcess(String input) { }

// doubles
@ParameterizedTest
@ValueSource(doubles = {1.1, 2.2, 3.3})
void testDecimal(double value) { }
```

---

## 2️⃣ `@NullSource`, `@EmptySource`, `@NullAndEmptySource`

### Edge Case Testing

```java
// Only null
@ParameterizedTest
@NullSource
void testNullInput(String input) {
    assertNull(input);
}

// Only empty
@ParameterizedTest
@EmptySource
void testEmptyInput(String input) {
    assertEquals("", input);
}

// Both null AND empty
@ParameterizedTest
@NullAndEmptySource
void testNullOrEmpty(String input) { }

// Combine with @ValueSource!
@ParameterizedTest
@NullAndEmptySource
@ValueSource(strings = {"  ", "\t"})  // Whitespace too
void testAllEdgeCases(String input) { }
```

---

## 3️⃣ `@EnumSource` - Enum Values

### All Enum Values
```java
@ParameterizedTest
@EnumSource(DayOfWeek.class)
void testAllDays(DayOfWeek day) { }
```

### Specific Values Only
```java
@ParameterizedTest
@EnumSource(value = DayOfWeek.class, names = {"SATURDAY", "SUNDAY"})
void testWeekends(DayOfWeek day) { }
```

### Exclude Values
```java
@ParameterizedTest
@EnumSource(value = DayOfWeek.class, 
            mode = EnumSource.Mode.EXCLUDE, 
            names = {"SATURDAY", "SUNDAY"})
void testWeekdays(DayOfWeek day) { }
```

### Regex Pattern
```java
@ParameterizedTest
@EnumSource(value = DayOfWeek.class, 
            mode = EnumSource.Mode.MATCH_ALL, 
            names = "^S.*")  // Starts with S
void testDaysStartingWithS(DayOfWeek day) { }
```

---

## 4️⃣ `@CsvSource` - Multiple Parameters (Inline)

### Basic Usage
```java
@ParameterizedTest
@CsvSource({
    "1, 2, 3",      // a=1, b=2, expected=3
    "5, 5, 10",
    "-3, 3, 0"
})
void testAddition(int a, int b, int expected) {
    assertEquals(expected, calculator.add(a, b));
}
```

### With Strings
```java
@ParameterizedTest
@CsvSource({
    "95, A",
    "85, B",
    "50, F"
})
void testGrade(int score, String expectedGrade) { }
```

### Custom Delimiter
```java
@ParameterizedTest
@CsvSource(value = {
    "2 | 3 | 6",
    "5 | 5 | 25"
}, delimiter = '|')
void testMultiply(int a, int b, int expected) { }
```

### Handling Null
```java
@CsvSource(value = {
    "hello, HELLO",
    "NULL, null"     // NULL becomes null
}, nullValues = "NULL")
```

---

## 5️⃣ `@CsvFileSource` - External CSV File

### CSV File Location
```
src/test/resources/test-data/grades.csv
```

### CSV Content
```csv
score,expectedGrade,description
95,A,Excellent
85,B,Good
50,F,Fail
```

### Usage
```java
@ParameterizedTest
@CsvFileSource(resources = "/test-data/grades.csv", numLinesToSkip = 1)
void testGrades(int score, String grade, String description) { }
```

### Options
| Option | Purpose |
|--------|---------|
| `resources` | File path (from src/test/resources) |
| `numLinesToSkip` | Skip header row(s) |
| `delimiter` | Column separator |
| `encoding` | File encoding (default: UTF-8) |

---

## 6️⃣ `@MethodSource` - From Static Method

### Simple - Single Parameter
```java
@ParameterizedTest
@MethodSource("provideNumbers")
void testEven(int number) { }

static Stream<Integer> provideNumbers() {
    return Stream.of(2, 4, 6, 8, 10);
}
```

### Multiple Parameters
```java
@ParameterizedTest
@MethodSource("provideTestData")
void testSum(int a, int b, int expected) { }

static Stream<Arguments> provideTestData() {
    return Stream.of(
        Arguments.of(1, 2, 3),
        Arguments.of(5, 5, 10)
    );
}
```

---

## 7️⃣ `@ArgumentsSource` - Custom Provider Class

### Reusable Across Tests
```java
@ParameterizedTest
@ArgumentsSource(FactorialProvider.class)
void testFactorial(int n, int expected) { }

// Separate class - can be in its own file!
class FactorialProvider implements ArgumentsProvider {
    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
            Arguments.of(0, 1),
            Arguments.of(5, 120)
        );
    }
}
```

---

## 📋 When to Use What?

```
╔══════════════════════════════════════════════════════════════════╗
║  PARAMETERIZED SOURCES - WHEN TO USE                            ║
╠══════════════════════════════════════════════════════════════════╣
║  Single simple values?    → @ValueSource                        ║
║  Null/Empty edge cases?   → @NullAndEmptySource                 ║
║  Enum values?             → @EnumSource                         ║
║  Multiple params (small)? → @CsvSource                          ║
║  Large dataset?           → @CsvFileSource                      ║
║  Complex/computed data?   → @MethodSource                       ║
║  Reusable across tests?   → @ArgumentsSource                    ║
╚══════════════════════════════════════════════════════════════════╝
```

---

## 📎 Related Files

- [Part5_ParameterizedTestsTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/service/Part5_ParameterizedTestsTest.java)
- [Part6_CsvFileSourceTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/service/Part6_CsvFileSourceTest.java)
- [grades.csv](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/resources/test-data/grades.csv)
