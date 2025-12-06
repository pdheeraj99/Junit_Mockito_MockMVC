package com.learning.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.controller.UserController;
import com.learning.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ ADVANCED WEB TEST (Validation & Error Handling) ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 * 
 * Focus:
 * 1. Testing Validation ("@NotBlank" failure -> 400 Bad Request)
 * 2. Testing Exception Handling (GlobalExceptionHandler)
 * 3. Verifying precise JSON Error messages
 */
@WebMvcTest(UserController.class)
class UserControllerAdvancedTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService; // Mocked

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should return 400 Bad Request when Name is Empty")
    void shouldReturn400WhenNameIsEmpty() throws Exception {
        // Given: Invalid Request (Empty Name)
        UserController.RegistrationRequest request = new UserController.RegistrationRequest("", "valid@test.com",
                "pass123");

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.name").value("Name is required")); // Check specific error message
    }

    @Test
    @DisplayName("Should return 400 Bad Request when Email is Invalid")
    void shouldReturn400WhenEmailIsInvalid() throws Exception {
        // Given: Invalid Request (Bad Email)
        UserController.RegistrationRequest request = new UserController.RegistrationRequest("Valid Name",
                "not-an-email", "pass123");

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Invalid email format"));
    }
}
