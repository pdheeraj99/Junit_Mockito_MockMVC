# 6️⃣ Spy - Partial Mocking

> **Test file:** [Part5_SpyTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/mockito/Part5_SpyTest.java)

---

## 🎯 Spy vs Mock - Key Difference

| Aspect | @Mock | @Spy |
|--------|-------|------|
| **Default behavior** | Returns null/0/false | Calls REAL method |
| **Real code runs** | ❌ Never | ✅ By default |
| **Override behavior** | `when().thenReturn()` | `doReturn().when()` |
| **Use case** | Complete fake | Partial fake |

---

## 🤔 When to Use @Spy?

1. **Legacy code** - Can't mock everything
2. **Some methods are fine** - Only stub specific ones
3. **Testing real behavior** - But override one method
4. **ArrayList/HashMap** - Real collection with some stubs

---

## 📝 Basic Spy Usage

```java
@Spy
private ArrayList<String> spyList = new ArrayList<>();

@Test
void testSpy() {
    // Real method call!
    spyList.add("one");
    spyList.add("two");
    assertEquals(2, spyList.size());  // Real size!
    
    // Override specific behavior
    doReturn(100).when(spyList).size();
    assertEquals(100, spyList.size());  // Now stubbed!
}
```

---

## ⚠️ Important: Use doReturn() with Spies!

```java
// ❌ WRONG - Calls real method FIRST!
when(spy.method()).thenReturn(value);

// ✅ CORRECT - Doesn't call real method
doReturn(value).when(spy).method();
```

**Why?** `when(spy.method())` actually **executes** `spy.method()` to get reference!

---

## 📎 Related Files

- **Test examples:** [Part5_SpyTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/mockito/Part5_SpyTest.java)
