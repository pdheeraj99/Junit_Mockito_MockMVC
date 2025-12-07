# Performance Testing 101: JMeter & BlazeMeter 🚀

**Why do we need this?**
Unit tests verify *correctness* (2 + 2 = 4).
Performance tests verify *stability* (Can 10,000 users do 2 + 2 = 4 at the same time?).

---

## 1. Apache JMeter (The Engine) 🚂
**Apache JMeter** is a free, open-source tool used to simulate heavy loads on servers. It acts like a "Virtual User Generator".

### Core Concepts:
1.  **Thread Group**: Represents "Users".
    *   *Number of Threads*: 100 (means 100 users).
    *   *Ramp-up Period*: 10 (start all 100 users in 10 seconds).
    *   *Loop Count*: 5 (each user repeats the action 5 times).
2.  **Sampler (HTTP Request)**: The actual API call.
    *   `POST /api/users`
    *   `GET /api/users/1`
3.  **Listeners**: Reports/Graphs (View Results Tree, Summary Report).
4.  **Assertions**: Checks if response is 200 OK (like JUnit assertions).

### How to use locally:
1.  **Download**: [jmeter.apache.org](https://jmeter.apache.org/download_jmeter.cgi)
2.  **Run**: `bin/jmeter.bat`
3.  **Plan**:
    *   Add Thread Group.
    *   Add HTTP Request (`localhost`, port `8080`, method `POST`).
    *   Add Listener (View Results Table).
    *   Click Green Play Button ▶.

---

## 2. BlazeMeter (The Platform) ☁️
JMeter is great locally, but your laptop cannot simulate 100,000 users (CPU/Network limit).
**BlazeMeter** is a Cloud Platform (SaaS) that runs JMeter scripts.

| Feature | Apache JMeter | BlazeMeter |
|---------|---------------|------------|
| **Execution** | Runs on your Laptop | Runs on Cloud (AWS/Azure) |
| **Scale** | ~1,000 Users Max | 1,000,000+ Users |
| **Reports** | Basic Tables/Graphs | Beautiful Dashboards, Trends |
| **Cost** | Free (Open Source) | Paid (Enterprise) |

### Enterprise Workflow:
1.  **Developer** creates a test script (`.jmx` file) on their laptop using **JMeter GUI**.
2.  **Developer** validates it works with 1-10 users.
3.  **Upload** the `.jmx` file to **BlazeMeter**.
4.  **BlazeMeter** spins up 50 servers and hammers your application with 50,000 users.

---

## 3. How to Test Our App (Junit_Mockito_MockMVC)

### Scenario: "User Registration Storm"
We want to see if our `OrderService` can handle 100 orders per second.

**JMeter Configuration:**
1.  **Thread Group**: 100 Users.
2.  **HTTP Request**: `POST http://localhost:8080/api/users`
    *   Body: `{"name": "User${__Random(1,1000)}", "email": "test${__Random(1,1000)}@test.com", "password": "pass123"}`
    *   *(Note: uses JMeter functions to generate random data to avoid "Email Unique" errors)*.
3.  **Run**: Watch if our Database connection pool (`HikariCP`) gets exhausted or if API slows down.

### What to watch for?
*   **Throughput**: Requests per second (Higher is better).
*   **Latency**: Response time (Lower is better, e.g., < 200ms).
*   **Error %**: Should be 0%. If validation fails (400) or DB crashes (500), this goes up.

---

## 4. Key Metrics (The "Scorecard") 📊
*   **Throughput**: **"Speed"**. (Higher is Better)
    *   *Definition*: Number of requests handled per second.
    *   *Analogy*: Oka Petrol Bunk lo **1 Minute** lo **14 Cars** fill chesthe, Bunk Capacity = **14 Cars/min**.
    *   *Our Result*: **14.56 req/sec** -> Ante mana Server second ki approx 14 mandi users ni handle chesindi locally using Docker.
*   **Latency**: **"Delay"**. (Lower is Better)
    *   *Definition*: Time taken for 1 request to complete.

---

## Summary
*   **JMeter**: Tool to *create* and *debug* scripts.
*   **BlazeMeter**: Platform to *run* those scripts at *scale*.
*   **Your Job**: As a dev, you mostly use JMeter GUI to build the `.jmx` file. The QA/DevOps team usually runs the big load in BlazeMeter.
