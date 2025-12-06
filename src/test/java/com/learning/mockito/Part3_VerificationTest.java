package com.learning.mockito;

import com.learning.external.EmailService;
import com.learning.model.User;
import com.learning.repository.UserRepository;
import com.learning.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ PART 3: VERIFICATION - verify(), times(), never(), inOrder() ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("📚 Part 3: Verification")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Part3_VerificationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    // ═══════════════════════════════════════════════════════════════════════════
    // Basic Verification
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(1)
    @DisplayName("1️⃣ Basic verify() - Check method was called")
    void testBasicVerify() {
        System.out.println("=== Basic verify() ===\n");

        // ARRANGE
        when(userRepository.existsByEmail("test@email.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(emailService.sendWelcomeEmail(anyString(), anyString())).thenReturn(true);

        // ACT
        userService.registerUser("Test", "test@email.com", "pass123");

        // VERIFY - Check that methods were called
        verify(userRepository).existsByEmail("test@email.com");
        System.out.println("✓ existsByEmail('test@email.com') was called");

        verify(userRepository).save(any(User.class));
        System.out.println("✓ save(User) was called");

        verify(emailService).sendWelcomeEmail("test@email.com", "Test");
        System.out.println("✓ sendWelcomeEmail was called with correct args");

        System.out.println("\n✅ All expected interactions verified!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // times() - Exact Count
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(2)
    @DisplayName("2️⃣ verify(times(n)) - Exact call count")
    void testVerifyTimes() {
        System.out.println("=== verify(times(n)) ===\n");

        // ARRANGE
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User(1L, "Test", "test@email.com")));

        // ACT - Call multiple times
        userService.findById(1L);
        userService.findById(2L);
        userService.findById(3L);

        // VERIFY
        verify(userRepository, times(3)).findById(anyLong());
        System.out.println("✓ findById() called exactly 3 times");

        verify(userRepository, times(1)).findById(1L);
        System.out.println("✓ findById(1L) called exactly 1 time");

        verify(userRepository, times(1)).findById(2L);
        System.out.println("✓ findById(2L) called exactly 1 time");

        System.out.println("\n✅ Exact counts verified!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // never() - Not Called
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(3)
    @DisplayName("3️⃣ verify(never()) - Method was NOT called")
    void testVerifyNever() {
        System.out.println("=== verify(never()) ===\n");

        // ARRANGE - Email already exists
        when(userRepository.existsByEmail("existing@email.com")).thenReturn(true);

        // ACT - Try to register (will fail)
        assertThrows(IllegalStateException.class, () -> {
            userService.registerUser("Test", "existing@email.com", "pass123");
        });

        // VERIFY - save() and sendEmail() should NEVER be called
        verify(userRepository, never()).save(any(User.class));
        System.out.println("✓ save() was NEVER called (registration stopped early)");

        verify(emailService, never()).sendWelcomeEmail(anyString(), anyString());
        System.out.println("✓ sendWelcomeEmail() was NEVER called");

        System.out.println("\n✅ Confirmed methods were NOT called!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // atLeast(), atMost(), atLeastOnce()
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(4)
    @DisplayName("4️⃣ atLeast(), atMost() - Range verification")
    void testVerifyAtLeastAtMost() {
        System.out.println("=== atLeast() / atMost() ===\n");

        // ARRANGE
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User(1L, "Test", "test@email.com")));

        // ACT - Call 5 times
        for (int i = 0; i < 5; i++) {
            userService.findById(1L);
        }

        // VERIFY
        verify(userRepository, atLeast(3)).findById(anyLong());
        System.out.println("✓ Called at least 3 times (actually 5)");

        verify(userRepository, atMost(10)).findById(anyLong());
        System.out.println("✓ Called at most 10 times (actually 5)");

        verify(userRepository, atLeastOnce()).findById(anyLong());
        System.out.println("✓ Called at least once");

        System.out.println("\n✅ Range verifications passed!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // InOrder - Verify Call Sequence
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(5)
    @DisplayName("5️⃣ inOrder() - Verify call sequence")
    void testVerifyInOrder() {
        System.out.println("=== inOrder() - Call Sequence ===\n");

        // ARRANGE
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(emailService.sendWelcomeEmail(anyString(), anyString())).thenReturn(true);

        // ACT
        userService.registerUser("Test", "test@email.com", "pass123");

        // VERIFY IN ORDER
        InOrder inOrder = inOrder(userRepository, emailService);

        System.out.println("Expected order:");
        System.out.println("  1. existsByEmail()");
        System.out.println("  2. save()");
        System.out.println("  3. sendWelcomeEmail()");

        inOrder.verify(userRepository).existsByEmail("test@email.com");
        inOrder.verify(userRepository).save(any(User.class));
        inOrder.verify(emailService).sendWelcomeEmail(anyString(), anyString());

        System.out.println("\n✅ Order verified correctly!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // verifyNoInteractions / verifyNoMoreInteractions
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(6)
    @DisplayName("6️⃣ verifyNoInteractions() - Mock was never used")
    void testVerifyNoInteractions() {
        System.out.println("=== verifyNoInteractions() ===\n");

        // ARRANGE - Email exists
        when(userRepository.existsByEmail("existing@email.com")).thenReturn(true);

        // ACT - Registration fails early
        assertThrows(IllegalStateException.class, () -> {
            userService.registerUser("Test", "existing@email.com", "pass123");
        });

        // EmailService should have NO interactions at all
        verifyNoInteractions(emailService);
        System.out.println("✓ emailService had ZERO interactions!");

        System.out.println("\n✅ Confirmed mock was completely untouched!");
    }

    @Test
    @Order(7)
    @DisplayName("7️⃣ verifyNoMoreInteractions() - Only expected calls, nothing extra")
    void testVerifyNoMoreInteractions() {
        System.out.println("=== verifyNoMoreInteractions() ===\n");

        // ARRANGE
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "Test", "test@email.com")));

        // ACT
        userService.findById(1L);

        // VERIFY specific call
        verify(userRepository).findById(1L);
        System.out.println("✓ findById(1L) was called");

        // VERIFY no other methods were called
        verifyNoMoreInteractions(userRepository);
        System.out.println("✓ No OTHER methods were called on userRepository");

        System.out.println("\n✅ Only the expected interaction occurred!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Verification with Argument Matchers
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(8)
    @DisplayName("8️⃣ Verification with argument matchers")
    void testVerifyWithMatchers() {
        System.out.println("=== Verify with Matchers ===\n");

        // ARRANGE
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(emailService.sendWelcomeEmail(anyString(), anyString())).thenReturn(true);

        // ACT
        userService.registerUser("Ramesh Kumar", "ramesh@example.com", "pass123");

        // VERIFY with matchers
        verify(userRepository).save(argThat(user -> user.getName().equals("Ramesh Kumar") &&
                user.getEmail().equals("ramesh@example.com")));
        System.out.println("✓ save() called with user having correct name and email");

        verify(emailService).sendWelcomeEmail(
                contains("@example.com"), // Email contains domain
                startsWith("Ramesh") // Name starts with Ramesh
        );
        System.out.println("✓ sendWelcomeEmail() verified with partial matchers");

        System.out.println("\n✅ Argument matching in verification works!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SUMMARY
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(9)
    @DisplayName("📋 SUMMARY: Verification Methods")
    void testSummary() {
        System.out.println("""

                ╔═══════════════════════════════════════════════════════════════════════╗
                ║                    VERIFICATION SUMMARY                               ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  verify(mock).method()                                                ║
                ║     → Was method called? (exactly once)                              ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  verify(mock, times(n)).method()                                      ║
                ║     → Called exactly n times                                         ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  verify(mock, never()).method()                                       ║
                ║     → NEVER called                                                   ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  verify(mock, atLeast(n)).method()                                    ║
                ║     → Called n or more times                                         ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  verify(mock, atMost(n)).method()                                     ║
                ║     → Called at most n times                                         ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  InOrder inOrder = inOrder(mock1, mock2);                             ║
                ║  inOrder.verify(mock1).method();                                      ║
                ║  inOrder.verify(mock2).method();                                      ║
                ║     → Verify order of calls across mocks                             ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  verifyNoInteractions(mock)                                           ║
                ║     → Mock was never touched                                         ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  verifyNoMoreInteractions(mock)                                       ║
                ║     → After verify(), no other calls happened                        ║
                ╚═══════════════════════════════════════════════════════════════════════╝
                """);
        assertTrue(true);
    }
}
