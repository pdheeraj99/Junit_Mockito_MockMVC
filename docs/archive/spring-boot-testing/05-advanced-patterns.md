# Advanced Spring Boot Testing Patterns 🚀

## 1. Validation Testing (400 Bad Request)
Enterprise APIs must handle invalid input gracefully. We use Jakarta Validation (`@Valid`, `@NotBlank`) and verify it with `.andExpect(status().isBadRequest())`.

### The Global Exception Handler
Don't let Tomcat return HTML error pages. Use `@RestControllerAdvice` to enforce JSON errors.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        // Convert strict errors to simple Map
        return ResponseEntity.badRequest().body(errors);
    }
}
```

### Testing It
```java
@Test
void shouldRejectEmptyName() throws Exception {
    UserRequest badRequest = new UserRequest("", "valid@email.com"); // Invalid

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(badRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.name").value("Name is required")); // specific check
}
```

---

## 2. Testing Custom Exceptions
If your service throws a business exception (e.g., `UserNotFoundException`), ensure your Controller handles it.

```java
// Mock the service to throw exception
given(userService.findById(99L)).willThrow(new UserNotFoundException(99L));

// Expect appropriate status mapping
mockMvc.perform(get("/api/users/99"))
       .andExpect(status().isNotFound());
```

---

## 3. Dealing with Security (Concept)
In enterprise apps with Spring Security:
- `@WithMockUser(username="admin")`: Simulates a logged-in user.
- `@WebMvcTest` + `@Import(SecurityConfig.class)`: To test auth rules.
- **CSRF**: Disable in tests or provide `csrf()` token.

---

## Checklist for 99% Coverage
1.  **Happy Path**: 200 OK / 201 Created.
2.  **Validation**: 400 Bad Request (all fields).
3.  **Business Errors**: 404 Not Found, 409 Conflict.
4.  **Serialization**: Ensure JSON dates match expected format.
5.  **Database**: Verify custom queries actually work (`@DataJpaTest`).
