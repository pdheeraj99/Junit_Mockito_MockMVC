package com.learning.service;

import com.learning.external.EmailService;
import com.learning.model.User;
import com.learning.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ SERVICE LAYER UNIT TEST (MOCKITO) ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 * 
 * Target: UserService
 * Type: UNIT TEST (Slice)
 * Tool: JUnit 5 + Mockito
 * 
 * WHAT WE TEST:
 * - Pure business logic in UserService
 * - Exception handling
 * - Interaction with dependencies (Repository/EmailService)
 * 
 * WHAT WE MOCK:
 * - UserRepository (Database)
 * - EmailService (External System)
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("Registration Scenarios")
    class RegistrationTests {

        @Test
        @DisplayName("✅ Should register user successfully")
        void shouldRegisterUserSuccess() {
            // Given
            String name = "Test User";
            String email = "test@example.com";
            String password = "password123";

            // Mock: Email does not exist
            given(userRepository.existsByEmail(email)).willReturn(false);

            // Mock: Saving user returns the user with ID
            given(userRepository.save(any(User.class))).willAnswer(invocation -> {
                User u = invocation.getArgument(0);
                u.setId(1L); // Simulate DB ID generation
                u.setCreatedAt(LocalDateTime.now());
                return u;
            });

            // When
            User result = userService.registerUser(name, email, password);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getEmail()).isEqualTo(email);

            // Verify interactions
            verify(userRepository).save(any(User.class));
            verify(emailService).sendWelcomeEmail(email, name);
        }

        @Test
        @DisplayName("❌ Should NOT register if email exists")
        void shouldFailIfEmailExists() {
            // Given
            String email = "existing@example.com";
            given(userRepository.existsByEmail(email)).willReturn(true);

            // When & Then
            assertThatThrownBy(() -> userService.registerUser("User", email, "pass123"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Email already registered");

            // Verify NO save occurred
            verify(userRepository, never()).save(any());
            verify(emailService, never()).sendWelcomeEmail(anyString(), anyString());
        }

        @Test
        @DisplayName("❌ Should fail on invalid input")
        void shouldFailValidation() {
            assertThatThrownBy(() -> userService.registerUser(null, "valid@email.com", "pass"))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() -> userService.registerUser("Valid", "invalid-email", "pass"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Retrieval Scenarios")
    class RetrievalTests {

        @Test
        @DisplayName("✅ Should find existing user by ID")
        void shouldFindById() {
            // Given
            Long id = 1L;
            User mockUser = new User("User", "email@test.com", "pass");
            given(userRepository.findById(id)).willReturn(Optional.of(mockUser));

            // When
            Optional<User> result = userService.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo("email@test.com");
        }

        @Test
        @DisplayName("❌ Should return empty for non-existing ID")
        void shouldReturnEmptyForMissingId() {
            // Given
            given(userRepository.findById(99L)).willReturn(Optional.empty());

            // When
            Optional<User> result = userService.findById(99L);

            // Then
            assertThat(result).isEmpty();
        }
    }
}
