package com.learning.mockito;

import com.learning.external.EmailService;
import com.learning.model.User;
import com.learning.repository.UserRepository;
import com.learning.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ PART 9: REAL-WORLD USER SERVICE TEST ║
 * ║ Complete test class demonstrating all Mockito features in one cohesive
 * file║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("🏢 Real-World UserService Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Part9_RealWorldUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    // ═══════════════════════════════════════════════════════════════════════════
    // HAPPY PATH TESTS
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("✅ Happy Path - Registration")
    class RegistrationHappyPath {

        @Test
        @DisplayName("Should register new user successfully")
        void shouldRegisterNewUser() {
            // GIVEN
            given(userRepository.existsByEmail("new@email.com")).willReturn(false);
            given(userRepository.save(any(User.class))).willAnswer(inv -> {
                User u = inv.getArgument(0);
                u.setId(1L);
                return u;
            });
            given(emailService.sendWelcomeEmail(anyString(), anyString())).willReturn(true);

            // WHEN
            User result = userService.registerUser("New User", "new@email.com", "password123");

            // THEN
            assertAll("Registration result",
                    () -> assertNotNull(result),
                    () -> assertEquals(1L, result.getId()),
                    () -> assertEquals("New User", result.getName()),
                    () -> assertTrue(result.isActive()));

            then(userRepository).should().save(userCaptor.capture());
            then(emailService).should().sendWelcomeEmail("new@email.com", "New User");
        }

        @Test
        @DisplayName("Should set creation timestamp on registration")
        void shouldSetCreationTimestamp() {
            // GIVEN
            given(userRepository.existsByEmail(anyString())).willReturn(false);
            given(userRepository.save(any(User.class))).willAnswer(inv -> inv.getArgument(0));
            given(emailService.sendWelcomeEmail(anyString(), anyString())).willReturn(true);

            // WHEN
            userService.registerUser("Test", "test@email.com", "password123");

            // THEN - Verify dynamic timestamp was set
            verify(userRepository).save(userCaptor.capture());
            User captured = userCaptor.getValue();

            assertNotNull(captured.getCreatedAt());
            assertTrue(captured.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ERROR SCENARIOS
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("❌ Error Scenarios")
    class ErrorScenarios {

        @Test
        @DisplayName("Should throw when email already exists")
        void shouldThrowOnDuplicateEmail() {
            // GIVEN
            given(userRepository.existsByEmail("existing@email.com")).willReturn(true);

            // WHEN/THEN
            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> userService.registerUser("Test", "existing@email.com", "password123"));

            assertTrue(exception.getMessage().contains("already registered"));
            then(userRepository).should(never()).save(any());
            then(emailService).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Should throw on invalid name")
        void shouldThrowOnEmptyName() {
            assertThrows(IllegalArgumentException.class,
                    () -> userService.registerUser("", "test@email.com", "password123"));

            assertThrows(IllegalArgumentException.class,
                    () -> userService.registerUser(null, "test@email.com", "password123"));
        }

        @Test
        @DisplayName("Should throw on invalid email")
        void shouldThrowOnInvalidEmail() {
            assertThrows(IllegalArgumentException.class,
                    () -> userService.registerUser("Test", "invalid-email", "password123"));

            assertThrows(IllegalArgumentException.class,
                    () -> userService.registerUser("Test", "", "password123"));
        }

        @Test
        @DisplayName("Should throw on short password")
        void shouldThrowOnShortPassword() {
            assertThrows(IllegalArgumentException.class,
                    () -> userService.registerUser("Test", "test@email.com", "123"));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // FIND BY ID TESTS
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("🔍 Find User")
    class FindUserTests {

        @Test
        @DisplayName("Should find existing user")
        void shouldFindExistingUser() {
            // GIVEN
            User existingUser = new User(1L, "Existing", "existing@email.com");
            given(userRepository.findById(1L)).willReturn(Optional.of(existingUser));

            // WHEN
            Optional<User> result = userService.findById(1L);

            // THEN
            assertTrue(result.isPresent());
            assertEquals("Existing", result.get().getName());
        }

        @Test
        @DisplayName("Should return empty for non-existent user")
        void shouldReturnEmptyForNonExistent() {
            // GIVEN
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // WHEN
            Optional<User> result = userService.findById(999L);

            // THEN
            assertTrue(result.isEmpty());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // DEACTIVATION TESTS
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("🚫 Deactivate User")
    class DeactivateUserTests {

        @Test
        @DisplayName("Should deactivate existing user")
        void shouldDeactivateUser() {
            // GIVEN
            User activeUser = new User(1L, "Active", "active@email.com");
            activeUser.setActive(true);
            given(userRepository.findById(1L)).willReturn(Optional.of(activeUser));
            given(userRepository.save(any(User.class))).willAnswer(inv -> inv.getArgument(0));

            // WHEN
            User result = userService.deactivateUser(1L);

            // THEN
            assertNotNull(result);
            verify(userRepository).save(userCaptor.capture());
            assertFalse(userCaptor.getValue().isActive());
        }

        @Test
        @DisplayName("Should throw for non-existent user")
        void shouldThrowForNonExistentUser() {
            // GIVEN
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // WHEN/THEN
            assertThrows(IllegalArgumentException.class, () -> userService.deactivateUser(999L));
            verify(userRepository, never()).save(any());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PROFILE UPDATE TESTS
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("✏️ Update Profile")
    class UpdateProfileTests {

        @Test
        @DisplayName("Should update user profile")
        void shouldUpdateProfile() {
            // GIVEN
            User existingUser = new User(1L, "Old Name", "old@email.com");
            given(userRepository.findById(1L)).willReturn(Optional.of(existingUser));
            given(userRepository.existsByEmail("new@email.com")).willReturn(false);
            given(userRepository.save(any(User.class))).willAnswer(inv -> inv.getArgument(0));

            // WHEN
            User result = userService.updateProfile(1L, "New Name", "new@email.com");

            // THEN
            assertNotNull(result);

            verify(userRepository).save(userCaptor.capture());
            User captured = userCaptor.getValue();

            assertEquals("New Name", captured.getName());
            assertEquals("new@email.com", captured.getEmail());
            assertNotNull(captured.getUpdatedAt());
        }

        @Test
        @DisplayName("Should reject duplicate email on update")
        void shouldRejectDuplicateEmailOnUpdate() {
            // GIVEN
            User existingUser = new User(1L, "Test", "test@email.com");
            given(userRepository.findById(1L)).willReturn(Optional.of(existingUser));
            given(userRepository.existsByEmail("taken@email.com")).willReturn(true);

            // WHEN/THEN
            assertThrows(IllegalStateException.class,
                    () -> userService.updateProfile(1L, "Test", "taken@email.com"));

            verify(userRepository, never()).save(any());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // VERIFICATION ORDER TESTS
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Should execute operations in correct order")
    void shouldExecuteInOrder() {
        // GIVEN
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(userRepository.save(any(User.class))).willAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        given(emailService.sendWelcomeEmail(anyString(), anyString())).willReturn(true);

        // WHEN
        userService.registerUser("Test", "test@email.com", "password123");

        // THEN - Verify order
        InOrder inOrder = inOrder(userRepository, emailService);
        inOrder.verify(userRepository).existsByEmail("test@email.com");
        inOrder.verify(userRepository).save(any(User.class));
        inOrder.verify(emailService).sendWelcomeEmail(anyString(), anyString());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // GET ACTIVE USERS
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Should get active users")
    void shouldGetActiveUsers() {
        // GIVEN
        User u1 = new User(1L, "User1", "u1@email.com");
        User u2 = new User(2L, "User2", "u2@email.com");
        User u3 = new User(3L, "User3", "u3@email.com");

        given(userRepository.findByActiveTrue()).willReturn(Arrays.asList(u1, u2, u3));

        // WHEN
        List<User> result = userService.getActiveUsers();

        // THEN
        assertEquals(3, result.size());
        verify(userRepository).findByActiveTrue();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // EMAIL FAILURE HANDLING
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Should register even if email fails (graceful degradation)")
    void shouldRegisterEvenIfEmailFails() {
        // GIVEN
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(userRepository.save(any(User.class))).willAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        // Email fails!
        given(emailService.sendWelcomeEmail(anyString(), anyString())).willReturn(false);

        // WHEN - Registration should still succeed
        User result = userService.registerUser("Test", "test@email.com", "password123");

        // THEN
        assertNotNull(result);
        assertEquals(1L, result.getId());

        // Email was attempted but failed
        verify(emailService).sendWelcomeEmail(anyString(), anyString());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PASSWORD RESET TESTS
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("🔑 Password Reset")
    class PasswordResetTests {

        @Test
        @DisplayName("Should send password reset email")
        void shouldSendPasswordResetEmail() {
            // GIVEN
            User user = new User(1L, "Test", "test@email.com");
            given(userRepository.findByEmail("test@email.com")).willReturn(Optional.of(user));
            given(emailService.sendPasswordResetEmail(anyString(), anyString())).willReturn(true);

            // WHEN
            boolean result = userService.requestPasswordReset("test@email.com");

            // THEN
            assertTrue(result);
            verify(emailService).sendPasswordResetEmail(eq("test@email.com"),
                    argThat(token -> token.startsWith("RESET-")));
        }

        @Test
        @DisplayName("Should return true for non-existent email (security)")
        void shouldReturnTrueForNonExistentEmail() {
            // GIVEN
            given(userRepository.findByEmail("unknown@email.com")).willReturn(Optional.empty());

            // WHEN
            boolean result = userService.requestPasswordReset("unknown@email.com");

            // THEN - Returns true (don't reveal if email exists)
            assertTrue(result);
            verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // USER COUNT
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Should return user count")
    void shouldReturnUserCount() {
        // GIVEN
        given(userRepository.count()).willReturn(42L);

        // WHEN
        long count = userService.getUserCount();

        // THEN
        assertEquals(42L, count);
    }
}
