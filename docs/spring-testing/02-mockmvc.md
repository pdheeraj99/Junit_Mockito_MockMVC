# 🌐 MockMvc Guide

> **Mawa, HTTP requests test cheyyali without starting server? MockMvc use chey!**

---

## 🤔 What is MockMvc?

```
Real Server             vs        MockMvc
━━━━━━━━━━━━━                     ━━━━━━━━━
HTTP → Tomcat →         vs        Direct →
Controller              vs        Controller

Slow (network)          vs        Fast (no network)
Real HTTP               vs        Simulated HTTP
```

**MockMvc = Simulate HTTP requests without starting a real server!**

---

## 💻 Basic Setup

```java
@SpringBootTest
@AutoConfigureMockMvc
class MyControllerTest {
    
    @Autowired
    MockMvc mockMvc;
    
    @Test
    void testGet() throws Exception {
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk());
    }
}
```

---

## 📋 Request Methods

```java
// GET
mockMvc.perform(get("/api/users"))

// GET with path variable
mockMvc.perform(get("/api/users/{id}", 1L))

// GET with query params
mockMvc.perform(get("/api/users")
    .param("page", "0")
    .param("size", "10"))

// POST with JSON
mockMvc.perform(post("/api/users")
    .contentType(MediaType.APPLICATION_JSON)
    .content("""
        {"name": "John", "email": "john@test.com"}
        """))

// PUT
mockMvc.perform(put("/api/users/{id}", 1L)
    .contentType(MediaType.APPLICATION_JSON)
    .content(jsonContent))

// DELETE
mockMvc.perform(delete("/api/users/{id}", 1L))

// PATCH
mockMvc.perform(patch("/api/users/{id}", 1L)
    .contentType(MediaType.APPLICATION_JSON)
    .content(patchContent))
```

---

## ✅ Response Assertions

### Status Assertions:

```java
.andExpect(status().isOk())            // 200
.andExpect(status().isCreated())       // 201
.andExpect(status().isNoContent())     // 204
.andExpect(status().isBadRequest())    // 400
.andExpect(status().isUnauthorized())  // 401
.andExpect(status().isForbidden())     // 403
.andExpect(status().isNotFound())      // 404
.andExpect(status().isConflict())      // 409
.andExpect(status().is5xxServerError()) // 5xx
```

### JSON Assertions:

```java
// Field value
.andExpect(jsonPath("$.name").value("John"))
.andExpect(jsonPath("$.email").value("john@test.com"))

// Field exists
.andExpect(jsonPath("$.id").exists())
.andExpect(jsonPath("$.deletedAt").doesNotExist())

// Array size
.andExpect(jsonPath("$.items").isArray())
.andExpect(jsonPath("$.items.length()").value(3))

// Nested field
.andExpect(jsonPath("$.address.city").value("NYC"))

// Array element
.andExpect(jsonPath("$.items[0].name").value("Item1"))
```

### Content Assertions:

```java
// Content type
.andExpect(content().contentType(MediaType.APPLICATION_JSON))

// Exact content
.andExpect(content().string("Hello World"))

// Contains
.andExpect(content().string(containsString("success")))
```

---

## 💻 Complete Examples

### GET Example:

```java
@Test
void shouldGetUser() throws Exception {
    mockMvc.perform(get("/api/users/{id}", 1L)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("John"))
        .andExpect(jsonPath("$.email").value("john@test.com"));
}
```

### POST Example:

```java
@Test
void shouldCreateUser() throws Exception {
    String requestJson = """
        {
            "name": "John",
            "email": "john@test.com",
            "password": "secret123"
        }
        """;
    
    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value("John"));
}
```

### Validation Error Example:

```java
@Test
void shouldReturn400ForInvalidInput() throws Exception {
    String invalidJson = """
        {
            "name": "",
            "email": "invalid",
            "password": "123"
        }
        """;
    
    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.name").value("Name is required"))
        .andExpect(jsonPath("$.email").value("Invalid email format"));
}
```

---

## 🎯 With ObjectMapper

```java
@Autowired
ObjectMapper objectMapper;

@Test
void createWithObjectMapper() throws Exception {
    UserRequest request = new UserRequest("John", "john@test.com", "pass123");
    
    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());
}
```

---

## 📊 Get Response Content

```java
@Test
void getResponseContent() throws Exception {
    MvcResult result = mockMvc.perform(get("/api/users/1"))
        .andExpect(status().isOk())
        .andReturn();
    
    String json = result.getResponse().getContentAsString();
    User user = objectMapper.readValue(json, User.class);
    
    assertEquals("John", user.getName());
}
```

---

## 🏢 With Authentication

```java
@Test
void withAuth() throws Exception {
    mockMvc.perform(get("/api/admin/users")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk());
}

// Or with Spring Security Test
@Test
@WithMockUser(roles = "ADMIN")
void withMockUser() throws Exception {
    mockMvc.perform(get("/api/admin/users"))
        .andExpect(status().isOk());
}
```

---

## 😂 Memory Trick

```
mockMvc.perform() = "REQUEST bhejo!"
.andExpect() = "EXPECT ye result!"
.andReturn() = "Response RETURN karo!"

perform(get/post/put/delete) → status().isXxx() → jsonPath("$.field")
```

---

## 🔗 Related Topics

- [Annotations](./01-annotations.md) - @SpringBootTest, @WebMvcTest
- [Slice Tests](./03-slice-tests.md) - Web layer testing
- [Your Code](file:///d:/Antigravity_Projects/Junit_Mockito_MockMVC/src/test/java/com/learning/integration/UserControllerIntegrationTest.java)
