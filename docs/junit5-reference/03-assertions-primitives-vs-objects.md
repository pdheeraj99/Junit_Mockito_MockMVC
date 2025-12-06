# 3️⃣ Assertions - Primitives vs Objects (MOST IMPORTANT! 🔥)

> **Test file:** [Part4_PrimitivesVsObjectsAssertionsTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/service/Part4_PrimitivesVsObjectsAssertionsTest.java)

---

## 🎯 Enduku Important?

Mawa, ee concept Java testing lo **most common mistake** avoid cheyyataniki kavali!

**Problem:**
```java
int[] arr1 = {1, 2, 3};
int[] arr2 = {1, 2, 3};

assertEquals(arr1, arr2);  // ❌ FAIL! Same content ayina fail!
```

**Enduku fail?** - Detailed ga chuddham!

---

## 📊 Master Reference Table

| Type | `assertEquals` | `assertArrayEquals` | `assertIterableEquals` | `assertSame` |
|------|---------------|--------------------|-----------------------|--------------|
| **Primitives** (`int`, `double`) | ✅ Value compare | ❌ N/A | ❌ N/A | ✅ (same as equals) |
| **Wrapper** (`Integer`, `String`) | ✅ Uses `.equals()` | ❌ N/A | ❌ N/A | ⚠️ Reference compare |
| **Arrays** (`int[]`, `String[]`) | ❌ **FAIL!** Reference | ✅ **USE THIS** | ❌ N/A | ❌ Reference only |
| **Collections** (`List`, `Set`) | ✅ Uses `.equals()` | ❌ N/A | ✅ Element-by-element | ❌ Reference only |
| **Custom Objects** | ⚠️ Needs `.equals()` | ❌ N/A | ❌ N/A | ❌ Reference only |

---

## 1️⃣ Primitives (`int`, `double`, `boolean`, etc.)

**Simple - Anni work chestyi!**

```java
int a = 5;
int b = 5;

assertEquals(a, b);     // ✅ PASS - value comparison
assertSame(a, b);       // ✅ PASS - (primitives ki same behavior)
assertTrue(a == b);     // ✅ PASS
```

**Important Notes:**
- Primitives are **values**, not objects
- Memory reference concept applicable kaadu
- Direct value comparison always works

---

## 2️⃣ Wrapper Objects (`Integer`, `String`, `Double`)

### String - Works because `.equals()` exists

```java
String s1 = new String("hello");
String s2 = new String("hello");

assertEquals(s1, s2);   // ✅ PASS - String.equals() compares content
assertSame(s1, s2);     // ❌ FAIL! - Different objects in memory
```

### Integer - Caching Behavior (Tricky!)

```java
// Within cache range (-128 to 127)
Integer i1 = 100;
Integer i2 = 100;
assertSame(i1, i2);     // ✅ PASS - Same cached object!

// Outside cache range
Integer i3 = 1000;
Integer i4 = 1000;
assertSame(i3, i4);     // ❌ FAIL - Different objects!
assertEquals(i3, i4);   // ✅ PASS - Same value
```

---

## 3️⃣ Arrays (`int[]`, `String[]`, `Object[]`) ⚠️ DANGER ZONE!

### ❌ Common Mistake

```java
int[] arr1 = {1, 2, 3};
int[] arr2 = {1, 2, 3};

// WRONG! This FAILS even though content is same!
assertEquals(arr1, arr2);  // ❌ FAIL!
```

### Enduku Fail Avtundi?

**Reason:** Arrays `.equals()` method override cheyaledu!

```java
// Array internally does this:
public boolean equals(Object obj) {
    return (this == obj);  // Reference comparison only!
}
```

| Array | Location in Memory | Content |
|-------|-------------------|---------|
| `arr1` | `0x1234` | `[1, 2, 3]` |
| `arr2` | `0x5678` | `[1, 2, 3]` |

**Memory locations different, so `arr1.equals(arr2)` = `false`!**

### ✅ Correct Way

```java
int[] arr1 = {1, 2, 3};
int[] arr2 = {1, 2, 3};

assertArrayEquals(arr1, arr2);  // ✅ PASS - Content comparison!
```

### All Array Types

```java
// Primitive arrays
int[] intArr1 = {1, 2};
int[] intArr2 = {1, 2};
assertArrayEquals(intArr1, intArr2);  // ✅

double[] dblArr1 = {1.1, 2.2};
double[] dblArr2 = {1.1, 2.2};
assertArrayEquals(dblArr1, dblArr2);  // ✅

// Object arrays - uses .equals() on each element
String[] strArr1 = {"a", "b"};
String[] strArr2 = {"a", "b"};
assertArrayEquals(strArr1, strArr2);  // ✅
```

---

## 4️⃣ Collections (`List`, `Set`, `Map`)

### Good News: `assertEquals` Works!

```java
List<Integer> list1 = Arrays.asList(1, 2, 3);
List<Integer> list2 = Arrays.asList(1, 2, 3);

assertEquals(list1, list2);        // ✅ PASS!
assertIterableEquals(list1, list2); // ✅ Also works
```

### Enduku Work Chestundi?

**Reason:** `List`, `Set`, `Map` interfaces properly `.equals()` override chesayi!

```java
// AbstractList.equals() does this:
public boolean equals(Object o) {
    // Compare element by element
    Iterator<E> e1 = this.iterator();
    Iterator<?> e2 = ((List<?>) o).iterator();
    while (e1.hasNext() && e2.hasNext()) {
        if (!Objects.equals(e1.next(), e2.next()))
            return false;
    }
    return true;
}
```

### `assertEquals` vs `assertIterableEquals`

| Assertion | Behavior |
|-----------|----------|
| `assertEquals` | Uses `List.equals()` |
| `assertIterableEquals` | Element-by-element, better error messages |

---

## 5️⃣ Custom Objects ⚠️ MUST OVERRIDE equals()!

### Without `.equals()` Override

```java
class Person {
    String name;
    Person(String name) { this.name = name; }
    // NO .equals() override!
}

Person p1 = new Person("Ramesh");
Person p2 = new Person("Ramesh");

assertEquals(p1, p2);  // ❌ FAIL! Uses Object.equals() = reference check
```

### With `.equals()` Override

```java
class Person {
    String name;
    
    Person(String name) { this.name = name; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(name, person.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

Person p1 = new Person("Ramesh");
Person p2 = new Person("Ramesh");

assertEquals(p1, p2);  // ✅ PASS now!
```

---

## 6️⃣ `assertSame` vs `assertEquals`

### Visual Difference

```
Memory:
┌─────────┐     ┌─────────┐
│ obj1    │     │ obj2    │
│ @0x1234 │     │ @0x5678 │
│ "hello" │     │ "hello" │
└─────────┘     └─────────┘

assertEquals(obj1, obj2) → Checks content → "hello" == "hello" → ✅
assertSame(obj1, obj2)   → Checks reference → 0x1234 == 0x5678 → ❌
```

### Code Example

```java
String s1 = new String("hello");
String s2 = new String("hello");
String s3 = s1;  // Same reference!

// assertEquals - Content
assertEquals(s1, s2);  // ✅ PASS
assertEquals(s1, s3);  // ✅ PASS

// assertSame - Reference
assertSame(s1, s2);    // ❌ FAIL - different objects
assertSame(s1, s3);    // ✅ PASS - same object!
```

### Eppudu `assertSame` Use Chestam?

1. **Singleton Pattern Testing**
```java
Singleton s1 = Singleton.getInstance();
Singleton s2 = Singleton.getInstance();
assertSame(s1, s2);  // Must be same instance!
```

2. **Cached Objects**
```java
List<String> empty1 = Collections.emptyList();
List<String> empty2 = Collections.emptyList();
assertSame(empty1, empty2);  // Same cached object!
```

---

## 📋 Quick Decision Chart

```
╔═══════════════════════════════════════════════════════════════════════╗
║                   WHICH ASSERTION TO USE?                             ║
╠═══════════════════════════════════════════════════════════════════════╣
║  Testing primitives (int, double)?                                    ║
║    └─→ assertEquals() ✅                                              ║
║                                                                       ║
║  Testing arrays (int[], String[])?                                    ║
║    └─→ assertArrayEquals() ✅                                         ║
║    └─→ assertEquals() ❌ DON'T USE!                                   ║
║                                                                       ║
║  Testing Lists/Collections?                                           ║
║    └─→ assertEquals() ✅ (works!)                                     ║
║    └─→ assertIterableEquals() ✅ (also works)                         ║
║                                                                       ║
║  Testing custom objects?                                              ║
║    └─→ First check: does class have .equals()?                       ║
║        └─→ Yes: assertEquals() ✅                                     ║
║        └─→ No: assertEquals() ❌ WILL FAIL!                           ║
║                                                                       ║
║  Testing if SAME object (singleton, cache)?                           ║
║    └─→ assertSame() ✅                                                ║
╚═══════════════════════════════════════════════════════════════════════╝
```

---

## 📎 Related Files

- **Test examples:** [Part4_PrimitivesVsObjectsAssertionsTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/service/Part4_PrimitivesVsObjectsAssertionsTest.java)
- **Run to see all examples:**
  ```powershell
  .\mvnw.cmd test -Dtest=Part4_PrimitivesVsObjectsAssertionsTest
  ```
