# 5️⃣ Argument Matchers - Flexible Matching

> **Test file:** [Part4_ArgumentMatchersTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/mockito/Part4_ArgumentMatchersTest.java)

---

## 🎯 Argument Matchers Enti?

**Exact value instead of, flexible matching!**

```java
// Without matchers - only matches EXACTLY 1L
when(repo.findById(1L)).thenReturn(user);

// With matchers - matches ANY Long value
when(repo.findById(anyLong())).thenReturn(user);
```

---

## 📋 Common Argument Matchers

### Type Matchers

| Matcher | Matches |
|---------|---------|
| `any()` | Any object (including null) |
| `any(Class.class)` | Any object of type |
| `anyInt()`, `anyLong()`, `anyDouble()` | Any primitive |
| `anyString()` | Any String |
| `anyList()`, `anySet()`, `anyMap()` | Any collection |
| `anyBoolean()` | Any boolean |

### Null Handling

| Matcher | Matches |
|---------|---------|
| `isNull()` | Null only |
| `notNull()` | Non-null only |
| `nullable(Class.class)` | Null or type |

### Value Matchers

| Matcher | Purpose |
|---------|---------|
| `eq(value)` | Exact value match |
| `same(object)` | Same reference |
| `refEq(object)` | Reflection equals |

### String Matchers

| Matcher | Purpose |
|---------|---------|
| `contains("text")` | Contains substring |
| `startsWith("prefix")` | Starts with |
| `endsWith("suffix")` | Ends with |
| `matches("regex")` | Matches regex |

### Custom Matchers

| Matcher | Purpose |
|---------|---------|
| `argThat(predicate)` | Custom lambda condition |

---

## ⚠️ Important Rule: ALL or NONE!

```java
// ❌ WRONG - mixing matcher and literal
when(service.method(anyString(), "literal")).thenReturn(value);

// ✅ CORRECT - all matchers
when(service.method(anyString(), eq("literal"))).thenReturn(value);
```

**If you use ANY matcher, ALL arguments must be matchers!**

---

## 📋 Quick Examples

```java
// Any type
when(repo.save(any(User.class))).thenReturn(user);

// Any string
when(email.send(anyString(), anyString())).thenReturn(true);

// Contains
verify(email).send(contains("@email.com"), anyString());

// Custom condition
verify(repo).save(argThat(user -> user.getAge() > 18));
```

---

## 📎 Related Files

- **Test examples:** [Part4_ArgumentMatchersTest.java](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/mockito/Part4_ArgumentMatchersTest.java)
