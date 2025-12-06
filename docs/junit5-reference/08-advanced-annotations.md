# 8️⃣ Advanced Annotations - Detailed Guide

> **Test file:** [Part9_AdvancedAnnotationsTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/service/Part9_AdvancedAnnotationsTest.java)

---

# 1️⃣ @TestFactory - Dynamic Test Generation

---

## 🎯 @TestFactory Enti?

**Normal tests** - Compile time lo fix:
```java
@Test void test1() { }
@Test void test2() { }
// Compile time lo define chesav
```

**@TestFactory** - Runtime lo generate!
```java
@TestFactory
Stream<DynamicTest> generateTests() {
    // Run time lo decide chestav - how many tests, what data
    return dataSource.stream()
        .map(data -> dynamicTest("Test " + data, () -> verify(data)));
}
```

---

## 🤔 Enduku @TestFactory Use Chestam?

### Problem 1: Dynamic number of tests

**Scenario:** Test data database lo undi, count every day change avtundi.

```java
// ❌ Hard-coded tests - every day update cheyyali!
@Test void testCase1() { }
@Test void testCase2() { }
@Test void testCase3() { }
// Tomorrow database lo 5 more cases add chesaru - code change!
```

```java
// ✅ Dynamic - automatically picks up new data!
@TestFactory
Stream<DynamicTest> generateTests() {
    return fetchTestCasesFromDB().stream()
        .map(tc -> dynamicTest(tc.getName(), () -> verify(tc)));
}
```

### Problem 2: Tests based on configuration

```java
@TestFactory
Collection<DynamicTest> environmentTests() {
    List<String> environments = config.getEnabledEnvironments();
    // ["dev", "staging", "prod"] or just ["dev"] based on config!
    
    return environments.stream()
        .map(env -> dynamicTest("Test in " + env, 
            () -> testInEnvironment(env)))
        .toList();
}
```

---

## 📝 @TestFactory Syntax Options

### Return Types Allowed

| Return Type | Example |
|-------------|---------|
| `Stream<DynamicTest>` | `return Stream.of(...)` |
| `Collection<DynamicTest>` | `return List.of(...)` |
| `Iterable<DynamicTest>` | `return myIterable` |
| `Iterator<DynamicTest>` | `return list.iterator()` |
| `DynamicNode[]` | `return new DynamicTest[]{...}` |

### Creating DynamicTest

```java
// Basic
DynamicTest test = dynamicTest("Test name", () -> {
    // Test code here
    assertEquals(expected, actual);
});

// With URI (shows in IDE)
DynamicTest test = dynamicTest("Test name", 
    URI.create("file://path/to/source"),
    () -> assertEquals(expected, actual));
```

---

## 🏠 Real-World Examples

### Example 1: Testing Multiple Formats

```java
@TestFactory
Stream<DynamicTest> testFileFormats() {
    List<String> formats = List.of("json", "xml", "csv", "yaml");
    
    return formats.stream()
        .map(format -> dynamicTest(
            "Parse " + format.toUpperCase() + " file",
            () -> {
                Parser parser = ParserFactory.getParser(format);
                assertNotNull(parser);
                Object result = parser.parse(getSampleData(format));
                assertNotNull(result);
            }
        ));
}
```

### Example 2: Data-Driven from List

```java
@TestFactory
Stream<DynamicTest> testGrades() {
    record GradeCase(int score, String grade, String desc) {}
    
    return Stream.of(
        new GradeCase(95, "A", "Excellent"),
        new GradeCase(85, "B", "Good"),
        new GradeCase(75, "C", "Average"),
        new GradeCase(50, "F", "Fail")
    ).map(tc -> dynamicTest(
        tc.desc + ": Score " + tc.score + " = " + tc.grade,
        () -> assertEquals(tc.grade, getGrade(tc.score))
    ));
}
```

### Example 3: Mathematical Operations

```java
@TestFactory
Collection<DynamicTest> testArithmetic() {
    return Arrays.asList(
        dynamicTest("2 + 3 = 5", () -> assertEquals(5, add(2, 3))),
        dynamicTest("10 - 4 = 6", () -> assertEquals(6, subtract(10, 4))),
        dynamicTest("3 × 4 = 12", () -> assertEquals(12, multiply(3, 4))),
        dynamicTest("10 ÷ 2 = 5", () -> assertEquals(5.0, divide(10, 2)))
    );
}
```

---

## 📋 @TestFactory vs @ParameterizedTest

| Aspect | @TestFactory | @ParameterizedTest |
|--------|-------------|-------------------|
| **When decided** | Runtime | Compile time |
| **Data source** | Any code/logic | Annotations only |
| **Flexibility** | Very high | Limited |
| **Complexity** | More code | Simple annotations |
| **Use when** | Data dynamic | Data fixed |

**Prefer @ParameterizedTest when possible** - idi simple. @TestFactory is for complex cases only.

---

# 2️⃣ @Tag - Test Categorization

---

## 🎯 @Tag Enti?

Tests ni **categories** lo organize cheyyi, then specific categories run cheyyi.

```java
@Test @Tag("fast") void quickTest() { }      // Quick tests
@Test @Tag("slow") void longTest() { }       // Time-consuming
@Test @Tag("integration") void dbTest() { }  // Needs database
@Test @Tag("unit") void pureTest() { }       // No dependencies
```

---

## 🤔 Enduku @Tag Use Chestam?

### Problem Without Tags

```bash
# All tests run - takes 30 minutes!
mvn test  

# But developer just wants quick feedback...
# No way to run only fast tests!
```

### Solution With Tags

```bash
# Only fast tests - 30 seconds!
mvn test -Dgroups="fast"

# Only unit tests - 2 minutes
mvn test -Dgroups="unit"

# Everything except slow - 5 minutes
mvn test -DexcludedGroups="slow"
```

---

## 📋 Common Tag Patterns

| Tag | Purpose |
|-----|---------|
| `@Tag("fast")` | Quick tests (<100ms each) |
| `@Tag("slow")` | Time-consuming (>1 second) |
| `@Tag("unit")` | No external dependencies |
| `@Tag("integration")` | Needs database/network |
| `@Tag("e2e")` | End-to-end/UI tests |
| `@Tag("smoke")` | Critical path tests |
| `@Tag("regression")` | Full regression suite |

---

## 🔧 Maven Configuration for Tags

```xml
<!-- pom.xml -->
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <configuration>
                <!-- Run only tests with these tags -->
                <groups>fast,unit</groups>
                
                <!-- Exclude tests with these tags -->
                <excludedGroups>slow</excludedGroups>
            </configuration>
        </plugin>
    </plugins>
</build>
```

---

## 🌍 Real-World CI/CD Example

```yaml
# .github/workflows/ci.yml

jobs:
  quick-check:
    # On every push - run fast tests only
    run: mvn test -Dgroups="fast,smoke"
    
  full-test:
    # On PR merge - run everything
    run: mvn test
    
  nightly:
    # Every night - run slow/integration
    run: mvn test -Dgroups="slow,integration,e2e"
```

---

# 3️⃣ @Timeout - Time Limits

---

## 🎯 @Timeout Enti?

Test ki **maximum time limit** set cheyyi. Exceed aite **FAIL**!

```java
@Test
@Timeout(5)  // 5 seconds max
void mustFinishQuickly() {
    // If takes more than 5 seconds → FAIL!
}
```

---

## 🤔 Enduku @Timeout Use Chestam?

### 1. Infinite Loop Detection

```java
@Test
@Timeout(1)  // 1 second max
void testProcessData() {
    // If there's a bug causing infinite loop,
    // test will FAIL after 1 second instead of hanging forever!
    processData();
}
```

### 2. Performance SLA

```java
@Test
@Timeout(value = 200, unit = TimeUnit.MILLISECONDS)
void testApiResponse() {
    // Our SLA says API must respond in 200ms
    apiClient.sendRequest();
}
```

### 3. CI/CD Protection

```java
// Without timeout: stuck tests block entire pipeline
// With timeout: test fails, pipeline continues
```

---

## 📝 @Timeout Syntax Options

### Basic (seconds)

```java
@Timeout(5)  // 5 seconds
void test() { }
```

### With TimeUnit

```java
@Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
void fastTest() { }

@Timeout(value = 2, unit = TimeUnit.MINUTES)
void longTest() { }
```

### On Class Level

```java
@Timeout(10)  // All tests in this class: 10 seconds max
class MyTests {
    @Test void test1() { }  // 10s max
    @Test void test2() { }  // 10s max
    
    @Test
    @Timeout(30)  // Override: 30 seconds for this one
    void longTest() { }
}
```

---

## 🔧 Programmatic Timeout Assertions

### assertTimeout - Wait then check

```java
@Test
void testWithAssertTimeout() {
    // Runs the code, WAITS for completion, then checks time
    String result = assertTimeout(
        Duration.ofMillis(100),
        () -> {
            return slowOperation();
        }
    );
    // Code ran completely, but we check if it was under 100ms
}
```

### assertTimeoutPreemptively - Kill if overtime

```java
@Test
void testWithPreemptiveTimeout() {
    // If 100ms exceeded, KILLS the execution immediately!
    String result = assertTimeoutPreemptively(
        Duration.ofMillis(100),
        () -> {
            return slowOperation();  // Gets killed if too slow!
        }
    );
}
```

### Difference Explained

| Aspect | assertTimeout | assertTimeoutPreemptively |
|--------|--------------|--------------------------|
| If overtime | Waits for completion, then fails | Kills immediately |
| Use when | You need the result | You want to limit resource usage |
| Thread | Same thread | New thread (careful with ThreadLocal!) |

---

# 4️⃣ Conditional Test Execution

---

## 🎯 Conditional Tests Enti?

**Tests ni skip/enable cheyyi based on conditions:**
- Operating System
- Java version
- Environment variables
- System properties
- Custom conditions

---

## 📋 All Conditional Annotations

### By Operating System

```java
@EnabledOnOs(OS.WINDOWS)
void windowsOnly() { }

@EnabledOnOs({OS.LINUX, OS.MAC})
void unixOnly() { }

@DisabledOnOs(OS.WINDOWS)
void notOnWindows() { }
```

### By Java Version

```java
@EnabledOnJre(JRE.JAVA_17)
void java17Only() { }

@EnabledOnJre({JRE.JAVA_17, JRE.JAVA_21})
void modernJavaOnly() { }

@DisabledOnJre(JRE.JAVA_8)
void notOnJava8() { }

@EnabledForJreRange(min = JRE.JAVA_11, max = JRE.JAVA_17)
void java11To17() { }
```

### By System Property

```java
@EnabledIfSystemProperty(named = "os.arch", matches = ".*64.*")
void only64Bit() { }

@DisabledIfSystemProperty(named = "ci", matches = "true")
void notInCI() { }
```

### By Environment Variable

```java
@EnabledIfEnvironmentVariable(named = "ENV", matches = "dev")
void devOnly() { }

@EnabledIfEnvironmentVariable(named = "CI", matches = "true")
void onlyInCI() { }
```

---

## 🏠 Real-World Use Cases

### 1. Platform-Specific Features

```java
@Test
@EnabledOnOs(OS.WINDOWS)
void testWindowsRegistry() {
    // Windows-specific code
}

@Test
@EnabledOnOs(OS.LINUX)
void testLinuxPermissions() {
    // Linux-specific code
}
```

### 2. Skip Slow Tests Locally

```java
@Test
@EnabledIfEnvironmentVariable(named = "CI", matches = "true")
void testHeavyIntegration() {
    // Only run in CI, skip locally
}
```

### 3. Feature Flags

```java
@Test
@EnabledIfSystemProperty(named = "feature.newUI", matches = "true")
void testNewUI() {
    // Only test when feature flag is on
}
```

---

## 📎 Related Files

- **All examples:** [Part9_AdvancedAnnotationsTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/service/Part9_AdvancedAnnotationsTest.java)

**Run chesi chudu:**
```powershell
.\mvnw.cmd test -Dtest=Part9_AdvancedAnnotationsTest
```

**Run only fast tests:**
```powershell
.\mvnw.cmd test -Dgroups="fast"
```
