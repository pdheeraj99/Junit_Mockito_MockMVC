# JUnit 5 Complete Reference Guide 📚

> **Mawa, ee guide lo JUnit 5 anni concepts detailed ga explain chesanu.**  
> **First ee docs chadivi, tarvata test files lo chudataniki vellu!**

---

## 📖 Documentation Index

| Part | Topic | Reference File |
|------|-------|----------------|
| 1 | Lifecycle Annotations | [01-lifecycle-annotations.md](./01-lifecycle-annotations.md) |
| 2 | Assertions - Basic | [02-assertions-basic.md](./02-assertions-basic.md) |
| 3 | Assertions - Primitives vs Objects | [03-assertions-primitives-vs-objects.md](./03-assertions-primitives-vs-objects.md) |
| 4 | Exception Testing | [04-exception-testing.md](./04-exception-testing.md) |
| 5 | Display Name & Disabled | [05-displayname-disabled.md](./05-displayname-disabled.md) |
| 6 | Parameterized Tests | [06-parameterized-tests.md](./06-parameterized-tests.md) |
| 7 | Nested & Repeated Tests | [07-nested-repeated-tests.md](./07-nested-repeated-tests.md) |
| 8 | Advanced Annotations | [08-advanced-annotations.md](./08-advanced-annotations.md) |
| 9 | Assumptions & assertAll | [09-assumptions-assertall.md](./09-assumptions-assertall.md) |

---

## 🚀 Quick Start Commands

```powershell
# Run ALL tests
.\mvnw.cmd test

# Run specific part
.\mvnw.cmd test -Dtest=Part1_LifecycleAndBasicAssertionsTest

# Run by tag
.\mvnw.cmd test -Dgroups="fast"
```

---

## 📁 Project Structure

```
src/test/java/com/learning/service/
├── Part1_LifecycleAndBasicAssertionsTest.java
├── Part2_ExceptionTestingTest.java
├── Part3_DisplayNameAndDisabledTest.java
├── Part4_PrimitivesVsObjectsAssertionsTest.java
├── Part5_ParameterizedTestsTest.java
├── Part6_CsvFileSourceTest.java
├── Part7_NestedTestsTest.java
├── Part8_RepeatedTestsTest.java
├── Part9_AdvancedAnnotationsTest.java
└── Part10_RemainingConceptsTest.java

docs/junit5-reference/
├── README.md (this file)
├── 01-lifecycle-annotations.md
├── 02-assertions-basic.md
├── ... (all reference files)
```
