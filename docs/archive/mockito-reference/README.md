# Mockito Complete Reference Guide 📚

> **Mawa, ee guide lo Mockito anni concepts detailed ga explain chesanu.**  
> **Real-world scenarios focus tho!**

---

## 📖 Documentation Index

| Part | Topic | Reference File |
|------|-------|----------------|
| 1 | Why Mocking? | [01-why-mocking.md](./01-why-mocking.md) |
| 2 | @Mock & @InjectMocks | [02-mock-injectmocks.md](./02-mock-injectmocks.md) |
| 3 | Stubbing (when/thenReturn) | [03-stubbing.md](./03-stubbing.md) |
| 4 | Verification | [04-verification.md](./04-verification.md) |
| 5 | Argument Matchers | [05-argument-matchers.md](./05-argument-matchers.md) |
| 6 | Spy vs Mock | [06-spy.md](./06-spy.md) |
| 7 | ArgumentCaptor | [07-argument-captor.md](./07-argument-captor.md) |
| 8 | BDD Style | [08-bdd-style.md](./08-bdd-style.md) |

---

## 🚀 Quick Start Commands

```powershell
# Run ALL Mockito tests
.\mvnw.cmd test -Dtest="*Mockito*"

# Run specific part
.\mvnw.cmd test -Dtest=Part1_MockBasicsTest
```

---

## 📁 Project Structure

```
src/main/java/com/learning/
├── model/
│   ├── User.java
│   ├── Order.java
│   └── OrderItem.java
├── repository/
│   ├── UserRepository.java
│   └── OrderRepository.java
├── external/
│   ├── EmailService.java
│   └── PaymentGateway.java
└── service/
    ├── UserService.java      ← We TEST this!
    └── OrderService.java     ← We TEST this!

src/test/java/com/learning/mockito/
├── Part1_MockBasicsTest.java
├── Part2_StubbingTest.java
├── Part3_VerificationTest.java
└── ... more test files
```

---

## 🎯 Key Concept

```
╔════════════════════════════════════════════════════════════════╗
║  UserService (We test this!)                                   ║
║  ├── depends on → UserRepository (MOCK this!)                 ║
║  └── depends on → EmailService (MOCK this!)                   ║
╚════════════════════════════════════════════════════════════════╝

We test SERVICE logic, not database/email logic!
```
