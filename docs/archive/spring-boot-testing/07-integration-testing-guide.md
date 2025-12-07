# Spring Boot Integration Testing - The Enterprise Guide (Tenglish Version)

Mawa, Enterprise lo **Integration Testing** ante "Does everything work together?" ani check cheyadam.

Unit Tests lo manam anni mock chestam (fake dependencies). Kani Integration Tests lo manam **REAL Components** ni test chestam.

## 1. Unit vs Integration Testing

| Feature | Unit Testing | Integration Testing |
| :--- | :--- | :--- |
| **Focus** | Single Class / Method | Multiple Layers (Controller + Service + DB) |
| **Speed** | Super Fast (Milliseconds) | Slower (Seconds - needs DB startup) |
| **Database** | Mocked (No real DB) | Real DB (Testcontainers) or InMemory (H2) |
| **Confidence** | Logic is correct | **System actually works** |

> **Enterprise Rule:** Unit Tests logic verify chestayi, Integration Tests wiring verify chestayi. Rendu kavali!

---

## 2. Why Testcontainers? (The Modern Standard)

Chala tutorials lo `H2 Database` (In-Memory) vadataru. Adi fast ga untundi, kani **Dangerous**.

*   **Problem:** Production lo MySQL/Postgres untundi, Tests lo H2 untundi. H2 syntax different ga undochu.
*   **Solution (Testcontainers):** Manam Docker use chesi **REAL MySQL** ni test run ayye appudu spin up chestam.
*   **Result:** Test pass ayithe, Production lo kuda 99% work avtundi.

---

## 3. Types of Integration Tests (Real World)

Mawa, nee doubt correct. Integration Testing ante only Controller test matrame kadu. Manam **SLICES** test cheyyachu or **FULL STACK** test cheyyachu.

### A. Full Stack Integration Test (Current Approach)
**Scope:** Controller -> Service -> Repository -> Database
**When to use:** Customer flow verify cheyyadaniki (End-to-End).
*   **Example:** Create User -> Save to DB.
*   **Annotation:** `@SpringBootTest`, `@AutoConfigureMockMvc`

### B. Service Layer Integration Test
**Scope:** Service -> Repository -> Database (NO Controller, NO HTTP)
**When to use:** Complex Business Logic ni test cheyyadaniki.
*   Example: Discounts calculate cheyyadam, Payments process cheyyadam.
*   HTTP calls avoid chesi direct Service methods ni call chestam.

### C. Repository Slice Test
**Scope:** Repository -> Database (NO Controller, NO Service)
**When to use:** Complex SQL queries ni test cheyyadaniki.
*   **Annotation:** `@DataJpaTest`
*   Spring Boot only Repository layer ni load chestundi (Fast ga untundi).

---

## 4. The Flow of an Integration Test

Mana `UserControllerIntegrationTest` lo jarigedi idhe:

### Step 1: Start the Environment (`@SpringBootTest`)
Spring Boot application mottham start avtundi.
*   Tomcat start avvadu (Mock Environment main thread lo untundi).
*   Beans anni create avthayi (`UserController`, `UserService`, `UserRepository`).
*   **Docker Container Starts:** `AbstractIntegrationTest` valla oka temporary MySQL start avtundi.

### Step 2: Make the Request (`MockMvc`)
Manam Postman lo request pampinchatlu, code dwara request pampistam.
```java
mockMvc.perform(post("/api/users")...)
```

### Step 3: Hits the Real Controller
Request `UserController` ki velthundi.
*   Controller `UserService` ni call chestundi.
*   Service validataions chestundi.
*   Service `UserRepository` ni call chestundi.

### Step 4: Hits the Real Database
Hibernate SQL generate chesi **Docker MySQL** lo data save chestundi.

### Step 5: Verify (`Assertions`)
Manam rendu rakalu ga verify chestam:
1.  **Response:** HTTP 201 vachinda? JSON correct ga unda?
2.  **Database:** `userRepository.findById()` use chesi DB lo nijam ga save ayinda ani check chestam.
