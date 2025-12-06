package com.learning.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.model.User;
import com.learning.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ WEB LAYER SLICE TEST (@WebMvcTest) ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 * 
 * Concepts:
 * 1. @WebMvcTest(UserController.class):
 * - Loads ONLY the Web Layer (Controller, Filters, Advice).
 * - Does NOT load Service, Repository, or Database.
 * - Super fast.
 * 
 * 2. @MockBean:
 * - Since Service layer is not loaded, we MUST mock it.
 * - Spring replaces the real bean with a Mockito mock in the context.
 */
@WebMvcTest(UserController.class)
class UserControllerWebLayerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService; // Mocked Service

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should return 201 Created when Service saves user")
    void shouldCreateUser() throws Exception {
        // Given
        UserController.RegistrationRequest request = new UserController.RegistrationRequest("Web User", "web@test.com",
                "pass123");
        User savedUser = new User("Web User", "web@test.com", "pass123");
        savedUser.setId(1L);

        // MOCK BEHAVIOR
        given(userService.registerUser(any(), any(), any())).willReturn(savedUser);

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Web User"));
    }
}
