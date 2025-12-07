# 1️⃣ Lifecycle Annotations

> **Test file:** [Part1_LifecycleAndBasicAssertionsTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/service/Part1_LifecycleAndBasicAssertionsTest.java)

---

## 🎯 Lifecycle Annotations Enti?

JUnit lo **test run avvadam mundu/tarvata** emi cheyyali ani specify chese annotations ivi.

**Real-life example:**  
- Exam rasey mundu pen, paper ready chestav → `@BeforeEach`
- Exam ayyaka desk clean chestav → `@AfterEach`
- Morning school ki velley mundu breakfast → `@BeforeAll`
- Evening intiki vachi mottam cleanup → `@AfterAll`

---

## 📋 Annotations Overview

| Annotation | Eppudu Run Avtundi | Static Avasarama? | Use Case |
|------------|-------------------|-------------------|----------|
| `@BeforeAll` | **Okasari** - class lo first test ki mundu | ✅ **Yes, mandatory** | Database connection start, heavy setup |
| `@BeforeEach` | **Every test ki mundu** | ❌ No | Fresh objects create cheyyatam |
| `@Test` | This is the actual test | ❌ No | Test logic |
| `@AfterEach` | **Every test tarvata** | ❌ No | Cleanup per test |
| `@AfterAll` | **Okasari** - last test tarvata | ✅ **Yes, mandatory** | Final cleanup, close connections |

---

## 🔄 Execution Order (Ela Run Avtundi)

```
╔═══════════════════════════════════════════════════════════════╗
║                    TEST CLASS START                           ║
╠═══════════════════════════════════════════════════════════════╣
║  1. @BeforeAll (OKASARI - static method)                     ║
║     └─ Database start, config load, etc.                     ║
╠═══════════════════════════════════════════════════════════════╣
║  2. Each test ki repeat:                                      ║
║     ┌───────────────────────────────────────────────────────┐ ║
║     │ @BeforeEach → @Test → @AfterEach                      │ ║
║     │ @BeforeEach → @Test → @AfterEach                      │ ║
║     │ @BeforeEach → @Test → @AfterEach                      │ ║
║     └───────────────────────────────────────────────────────┘ ║
╠═══════════════════════════════════════════════════════════════╣
║  3. @AfterAll (OKASARI - static method)                      ║
║     └─ Database stop, final cleanup                          ║
╚═══════════════════════════════════════════════════════════════╝
```

---

## 📝 Detailed Explanations

### `@BeforeAll` - Okasari Setup

```java
@BeforeAll
static void setUpBeforeAll() {  // MUST be static!
    System.out.println("Database starting...");
    database = new Database();
    database.connect();
}
```

**Enduku use chestam?**
- Expensive operations (database connect, file load)
- Oka test class lo anni tests ki common ga kavalsindi
- Time save - every test ki repeat avvakudadu

**Important Points:**
| Point | Explanation |
|-------|-------------|
| **Static mandatory** | Class level method, instance create avvadam mundu run avtundi |
| **Okasari run** | 100 tests unna okasari ey run avtundi |
| **Errors here** | Ikkada error vaste class lo anni tests skip |

---

### `@BeforeEach` - Every Test Ki Mundu

```java
@BeforeEach
void setUp() {
    calculator = new CalculatorService();  // Fresh instance!
    testData = new ArrayList<>();
}
```

**Enduku use chestam?**
- **Test Isolation** - Oka test oka dani modify chesina, next test fresh start
- Fresh objects kavali ani guarantee
- Each test independent ga run avvali

**Real Example:**
```java
private List<String> items;

@BeforeEach
void setUp() {
    items = new ArrayList<>();  // Fresh list every test
    items.add("default");
}

@Test
void test1() {
    items.add("test1");
    assertEquals(2, items.size());  // ✅ 2 items
}

@Test
void test2() {
    items.add("test2");
    assertEquals(2, items.size());  // ✅ 2 items (fresh list!)
}
```

---

### `@AfterEach` - Every Test Tarvata Cleanup

```java
@AfterEach
void tearDown() {
    if (file != null) {
        file.delete();  // Clean up test file
    }
    calculator = null;
}
```

**Use Cases:**
- Test lo create chesina files delete
- Temporary data cleanup
- Connections close (test-specific)

---

### `@AfterAll` - Final Cleanup

```java
@AfterAll
static void tearDownAfterAll() {  // MUST be static!
    database.disconnect();
    System.out.println("All tests completed!");
}
```

**Use Cases:**
- Database connection close
- Test containers stop
- Final reports generate

---

## ⚠️ Common Mistakes

### Mistake 1: Static keyword miss avvatam

```java
// ❌ WRONG - Compile error!
@BeforeAll
void setUp() { }

// ✅ CORRECT
@BeforeAll
static void setUp() { }
```

### Mistake 2: Heavy setup @BeforeEach lo pettadam

```java
// ❌ BAD - Slow tests!
@BeforeEach
void setUp() {
    database.connect();  // 5 sec every test!
}

// ✅ GOOD - Fast tests
@BeforeAll
static void setUpOnce() {
    database.connect();  // 5 sec okasari
}
```

---

## 🔗 Exception: `@TestInstance(PER_CLASS)`

**Default ga:** JUnit each test ki new instance create chestundi, ade `PER_METHOD`.

**PER_CLASS use cheste:** Same instance for all tests → `@BeforeAll` non-static avvachu!

```java
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MyTest {
    
    @BeforeAll
    void setUp() {  // NOT static! PER_CLASS allows this
        // setup code
    }
}
```

**Eppudu use chestam?**
- State share cheyyali tests madhya
- Non-static @BeforeAll kavali

---

## 📎 Related Test File

**Chudataniki vellu:** [Part1_LifecycleAndBasicAssertionsTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/service/Part1_LifecycleAndBasicAssertionsTest.java)

Console output lo lifecycle order clearly kanipistundi! 🎯
