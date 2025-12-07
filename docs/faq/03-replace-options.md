# ❓ FAQ: Replace.ANY vs Replace.NONE

> **Emi meaning? Embedded DB? Configured Datasource?**

---

## 📖 Terms First:

| Term | Meaning |
|------|---------|
| **Embedded DB** | H2, HSQLDB - in-memory, no Docker |
| **Configured Datasource** | YOUR config (Testcontainers MySQL) |

---

## 🔄 Replace Options:

| Option | What Spring Does |
|--------|------------------|
| `Replace.ANY` | **"Nee DB edhaina, H2 tho replace!"** (DEFAULT) |
| `Replace.NONE` | **"Replace cheyaku, nee config vaadu!"** |

---

## 📊 Visual:

```
Replace.ANY (DEFAULT):
Your Config: MySQL → Spring: "Nahh, H2 vaddam!"
Test → Repository → H2 (In-Memory) ← IGNORES Testcontainer!

Replace.NONE:
Your Config: MySQL → Spring: "OK, MySQL ye vaddam!"
Test → Repository → MySQL (Testcontainer) ← USES your config!
```

---

## 💻 Code:

```java
// ❌ WRONG: Uses H2, ignores Testcontainer!
@DataJpaTest
class UserRepositoryTest { }

// ✅ CORRECT: Uses YOUR MySQL Testcontainer
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class UserRepositoryTest { }
```

---

## 🎯 Memory Trick:

```
Replace.ANY  = ANY DB needhaina → H2 ki replace!
Replace.NONE = NONE replace → nee config vaadu!
```
