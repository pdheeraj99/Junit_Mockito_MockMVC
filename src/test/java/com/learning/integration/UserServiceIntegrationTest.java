package com.learning.integration;

import com.learning.external.EmailService;
import com.learning.model.User;
import com.learning.repository.UserRepository;
import com.learning.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ SERVICE LAYER INTEGRATION TEST ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 * 
 * Concept:
 * - We want to test COMPLEX BUSINESS LOGIC with a REAL DATABASE.
 * - But we don't care about HTTP/JSON/Controllers.
 * 
 * Setup:
 * - @SpringBootTest: Loads the context (Service + Repo).
 * - AbstractIntegrationTest: Starts the MySQL Container.
 */
@DisplayName("⚙️ Service Layer Integration (No Controller)")
class UserServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private EmailService emailService; // We still mock external systems (Email/Kafka)

    @Test
    @DisplayName("Should register user and save to Real DB")
    void shouldRegisterUser() {
        // Given
        String name = "Service User";
        String email = "service@test.com";
        String password = "password123";

        // When (Call Method directly, NO MockMvc)
        User result = userService.registerUser(name, email, password);

        // Then
        assertNotNull(result.getId());
        assertEquals(name, result.getName());

        // Verify in DB directly
        User savedInDb = userRepository.findById(result.getId()).orElseThrow();
        assertEquals("service@test.com", savedInDb.getEmail());

        System.out.println("✅ Verified: Service Layer wrote to Real MySQL!");
    }

    @Test
    @DisplayName("Should throw exception for duplicate email (Business Logic)")
    void shouldThrowForDuplicate() {
        // Given
        User existing = new User("Old", "dup@test.com", "password123");
        userRepository.save(existing);

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            userService.registerUser("New", "dup@test.com", "password456");
        });

        System.out.println("✅ Verified: Service Layer correctly blocked duplicate!");
    }
}
