# MockMVC Testing Guide

## 1. What is MockMVC?
**MockMVC** is the standard way to test Spring MVC Controllers.
- It sends **fake HTTP requests** to the `DispatcherServlet`.
- It validates the **Response** (Status, JSON, Headers).
- It is **faster** than a full real server (e.g., Selenium) because it doesn't actually open a network port.

## 2. Key Annotations

### `@AutoConfigureMockMvc`
- Used with `@SpringBootTest`.
- Automatically creates and configures the `MockMvc` bean.
- Allows you to `@Autowired MockMvc mockMvc` in your test.

### `@WebMvcTest` (vs `@SpringBootTest`)
- **`@SpringBootTest`**: Loads entire context (DB, Service, Controller). Used for Integration Tests.
- **`@WebMvcTest(Controller.class)`**: Loads ONLY the web layer. Mocks the Service layer. Used for Unit Tests of Controllers.

## 3. How to write a MockMVC Test

```java
@Autowired
private MockMvc mockMvc;

@Test
void testApi() throws Exception {
    mockMvc.perform(post("/api/users")           // 1. Build Request
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Mawa\"}"))
            
            .andExpect(status().isCreated())     // 2. Verify Status
            .andExpect(jsonPath("$.name").value("Mawa")); // 3. Verify JSON
}
```

## 4. `jsonPath`
- A query language for JSON.
- `$` = Root object
- `$.name` = field 'name'
- `$.items[0].id` = ID of first item in list

## 5. Enterprise Best Practice
- Use **`ObjectMapper`** to convert Java Objects to JSON strings (don't write manual JSON strings).
- Verify **Input Validation** (400 Bad Request) scenarios.
