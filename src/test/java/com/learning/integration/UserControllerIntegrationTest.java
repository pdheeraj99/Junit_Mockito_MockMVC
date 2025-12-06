package com.learning.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.controller.UserController;
import com.learning.model.User;
import com.learning.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ REAL WORLD INTEGRATION TEST (Full Stack) ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 * 
 * Flow:
 * Test -> MockMVC (fake HTTP) -> UserController -> UserService ->
 * UserRepository -> MySQL Container
 * 
 * Annotations:
 * 
 * @AutoConfigureMockMvc: Configures MockMvc instance to hit Controllers
 *                        Extends AbstractIntegrationTest: Starts MySQL
 *                        Container!
 */
@AutoConfigureMockMvc
@DisplayName("🚀 End-to-End Integration Test (MockMVC + MySQL)")
class UserControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper; // For JSON conversion

    @org.springframework.boot.test.mock.mockito.MockBean
    private com.learning.external.EmailService emailService;

    @Test
    @DisplayName("Should create user via API and save to Real MySQL")
    void shouldCreateUserEndToEnd() throws Exception {
        // 1. Prepare JSON Request
        UserController.RegistrationRequest request = new UserController.RegistrationRequest("Integration User",
                "integration@test.com", "secret123");

        String jsonRequest = objectMapper.writeValueAsString(request);

        // 2. Perform POST request
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Integration User")))
                .andExpect(jsonPath("$.email", is("integration@test.com")));

        // 3. VERIFY DATA IN REAL DB
        // This proves the Controller -> Service -> Repository -> DB chain works!
        User savedUser = userRepository.findByEmail("integration@test.com").orElseThrow();
        assertEquals("Integration User", savedUser.getName());
        assertEquals("secret123", savedUser.getPassword());

        System.out.println("✅ Verified: Data persisted in MySQL Container!");
    }

    @Test
    @DisplayName("Should retrieve existing user via API")
    void shouldRetrieveUser() throws Exception {
        // 1. Manually save data to DB
        User user = new User("Getter User", "get@test.com", "password123");
        User saved = userRepository.save(user);

        // 2. Call GET API
        mockMvc.perform(get("/api/users/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Getter User")));

        System.out.println("✅ Verified: API retrieved data from MySQL Container!");
    }

    /**
     * SCENARIO 3: Error Handling (Validation)
     * 
     * What are we testing?
     * - Attempting to send "Bad Data" (Empty name, Invalid email)
     * - Expecting: HTTP 400 Bad Request
     * - Expecting: Spring Boot Validation to kick in
     */
    @Test
    @DisplayName("Should return 400 Bad Request when validation fails")
    void shouldReturn400_WhenValidationFails() throws Exception {
        // 1. Prepare BAD Request (Empty Name, Invalid Email, Short Password)
        UserController.RegistrationRequest badRequest = new UserController.RegistrationRequest(
                "", // Empty Name (Invalid)
                "not-an-email", // Invalid Email
                "123" // Short Password
        );

        String jsonRequest = objectMapper.writeValueAsString(badRequest);

        // 2. Perform POST and Verify Error
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest()) // Verify HTTP 400
                .andExpect(jsonPath("$.name", is("Name is required"))) // Verify Message
                .andExpect(jsonPath("$.email", is("Invalid email format")))
                .andExpect(jsonPath("$.password", is("Password must be at least 6 characters")));

        System.out.println("✅ Verified: Validations are working correctly!");
    }

    /**
     * SCENARIO 4: Business Logic Error (Duplicate Email)
     * 
     * What are we testing?
     * - Creating a user that ALREADY exists.
     * - We expect our Service to throw 'IllegalStateException'.
     * - We expect our Controller to catch it and return HTTP 409 (Conflict).
     */
    @Test
    @DisplayName("Should return 409 Conflict when email already exists")
    void shouldReturn409_WhenEmailExists() throws Exception {
        // 1. PRE-CONDITION: Save a user first
        User existingUser = new User("Existing User", "duplicate@test.com", "pass123");
        userRepository.save(existingUser);

        // 2. Prepare Request with SAME email
        UserController.RegistrationRequest duplicateRequest = new UserController.RegistrationRequest(
                "New Name",
                "duplicate@test.com", // DUPLICATE!
                "newpass123");

        String jsonRequest = objectMapper.writeValueAsString(duplicateRequest);

        // 3. Perform POST and Expect 409
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isConflict()) // Expect HTTP 409
                .andExpect(jsonPath("$", is("Email already registered: duplicate@test.com")));

        System.out.println("✅ Verified: Duplicate email handling works!");
    }
}
