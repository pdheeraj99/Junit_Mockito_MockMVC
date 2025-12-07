# ❓ FAQ: Web Environment - When Needed?

> **Controller tests ki web environment kavala, Service tests ki vadda?**

---

## ✅ Answer: Correct!

### Controller Test - WEB ENVIRONMENT Needed:

```java
@SpringBootTest  // Default: webEnvironment = MOCK
@AutoConfigureMockMvc
class UserControllerIntegrationTest {
    @Autowired MockMvc mockMvc;  // HTTP simulation
}
```

```
MockMvc → HTTP Request → Controller → Service → DB
         ↑
   Web environment needed for this!
```

### Service Test - NO WEB ENVIRONMENT:

```java
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class UserServiceIntegrationTest {
    @Autowired UserService userService;  // Direct method call
}
```

```
Test → userService.register() → Repository → DB
       ↑
  Direct call, no HTTP!
```

---

## 🎯 Rule:

| Test Type | Web Environment |
|-----------|-----------------|
| Controller (HTTP) | ✅ YES (MOCK or RANDOM_PORT) |
| Service (Direct call) | ❌ NO (NONE) |
| Repository | ❌ NO |
