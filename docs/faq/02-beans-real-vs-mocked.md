# ❓ FAQ: @SpringBootTest lo Beans - Real or Mocked?

> **Beans real aa? Fake aa?**

---

## ✅ Answer: REAL BEANS!

```java
@SpringBootTest
class MyTest {
    @Autowired UserService userService;     // ✅ REAL
    @Autowired UserRepository userRepository; // ✅ REAL
    @Autowired UserController userController; // ✅ REAL
}
```

**@SpringBootTest = Production laga REAL beans!**

---

## 🔴 @MockBean - Specific Bean ni Mock Cheyyali Ante:

```java
@SpringBootTest
class MyTest {
    @Autowired UserService userService;   // ✅ REAL
    @MockBean EmailService emailService;  // 🔴 FAKE!
}
```

---

## 📊 Visual:

```
WITHOUT @MockBean:
┌───────────────────────────────────────┐
│ Spring Context                        │
│   UserController  → REAL              │
│   UserService     → REAL              │
│   EmailService    → REAL (sends email!)│
└───────────────────────────────────────┘

WITH @MockBean EmailService:
┌───────────────────────────────────────┐
│ Spring Context                        │
│   UserController  → REAL              │
│   UserService     → REAL              │
│   EmailService    → MOCK (no email!)  │
└───────────────────────────────────────┘
```

---

## 🎯 Rule:

| Annotation | What You Get |
|------------|--------------|
| `@SpringBootTest` alone | ALL REAL beans |
| `@SpringBootTest` + `@MockBean X` | All real EXCEPT X |
