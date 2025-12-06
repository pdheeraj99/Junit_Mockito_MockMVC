package com.learning.mockito;

import com.learning.external.EmailService;
import com.learning.model.User;
import com.learning.repository.UserRepository;
import com.learning.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*; // BDD imports!

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ PART 7: BDD STYLE - given/when/then with BDDMockito ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("📚 Part 7: BDD Style")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Part7_BDDStyleTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    // ═══════════════════════════════════════════════════════════════════════════
    // BDD Syntax Comparison
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(1)
    @DisplayName("1️⃣ Standard vs BDD syntax comparison")
    void testBddVsStandardSyntax() {
        System.out.println("=== Standard vs BDD Syntax ===\n");

        System.out.println("""
                    Standard Mockito          →  BDD Mockito
                    ─────────────────────────────────────────────────
                    when().thenReturn()       →  given().willReturn()
                    when().thenThrow()        →  given().willThrow()
                    verify(mock).method()     →  then(mock).should().method()
                    verify(mock, never())     →  then(mock).should(never())
                    verify(mock, times(n))    →  then(mock).should(times(n))
                """);

        assertTrue(true);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Complete BDD Test
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(2)
    @DisplayName("2️⃣ Complete BDD test: Register user happy path")
    void should_register_user_and_send_welcome_email() {
        System.out.println("=== Complete BDD Test ===\n");

        // ═══════════════════════════════════════════════
        // GIVEN - Setup preconditions
        // ═══════════════════════════════════════════════
        System.out.println("GIVEN:");
        System.out.println("  - Email does not exist in database");
        System.out.println("  - Save operation will succeed");
        System.out.println("  - Email service is available");

        given(userRepository.existsByEmail("newuser@email.com"))
                .willReturn(false);

        given(userRepository.save(any(User.class)))
                .willAnswer(invocation -> {
                    User u = invocation.getArgument(0);
                    u.setId(100L);
                    return u;
                });

        given(emailService.sendWelcomeEmail(anyString(), anyString()))
                .willReturn(true);

        // ═══════════════════════════════════════════════
        // WHEN - Perform action
        // ═══════════════════════════════════════════════
        System.out.println("\nWHEN:");
        System.out.println("  - User registers with valid details");

        User registeredUser = userService.registerUser(
                "New User",
                "newuser@email.com",
                "securePass123");

        // ═══════════════════════════════════════════════
        // THEN - Verify expectations
        // ═══════════════════════════════════════════════
        System.out.println("\nTHEN:");

        // Verify user was saved
        then(userRepository).should().save(any(User.class));
        System.out.println("  ✓ User was saved to database");

        // Verify welcome email was sent
        then(emailService).should().sendWelcomeEmail("newuser@email.com", "New User");
        System.out.println("  ✓ Welcome email was sent");

        // Verify user object
        assertNotNull(registeredUser);
        assertEquals(100L, registeredUser.getId());
        assertEquals("New User", registeredUser.getName());
        System.out.println("  ✓ Returned user has correct data");

        System.out.println("\n✅ BDD test passed!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // BDD with Exception
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(3)
    @DisplayName("3️⃣ BDD: Duplicate email should throw exception")
    void should_throw_exception_when_email_already_exists() {
        System.out.println("=== BDD Exception Test ===\n");

        // GIVEN
        System.out.println("GIVEN: Email already exists in database");
        given(userRepository.existsByEmail("existing@email.com"))
                .willReturn(true);

        // WHEN
        System.out.println("WHEN: User tries to register with existing email");

        // THEN
        System.out.println("THEN: Should throw IllegalStateException");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> userService.registerUser("Test", "existing@email.com", "password123"));

        assertTrue(exception.getMessage().contains("already registered"));
        System.out.println("  ✓ Exception thrown: " + exception.getMessage());

        // THEN: Save should never be called
        then(userRepository).should(never()).save(any(User.class));
        System.out.println("  ✓ save() was never called");

        // THEN: Email should never be sent
        then(emailService).shouldHaveNoInteractions();
        System.out.println("  ✓ emailService had no interactions");

        System.out.println("\n✅ Exception handling verified!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // BDD Verification Modes
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(5)
    @DisplayName("5️⃣ BDD verification modes: times, never, atLeast")
    void should_demonstrate_verification_modes() {
        System.out.println("=== BDD Verification Modes ===\n");

        // GIVEN
        given(userRepository.findById(anyLong())).willReturn(
                Optional.of(new User(1L, "Test", "test@email.com")));

        // WHEN - Call multiple times
        userService.findById(1L);
        userService.findById(2L);
        userService.findById(3L);

        // THEN - Verify with modes
        then(userRepository).should(times(3)).findById(anyLong());
        System.out.println("✓ should(times(3)) - called exactly 3 times");

        then(userRepository).should(atLeast(2)).findById(anyLong());
        System.out.println("✓ should(atLeast(2)) - called at least 2 times");

        then(userRepository).should(never()).deleteById(anyLong());
        System.out.println("✓ should(never()) - deleteById was never called");

        System.out.println("\n✅ All BDD verification modes work!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SUMMARY
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(6)
    @DisplayName("📋 SUMMARY: BDD Style")
    void testSummary() {
        System.out.println("""

                ╔═══════════════════════════════════════════════════════════════════════╗
                ║                    BDD STYLE SUMMARY                                  ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  import static org.mockito.BDDMockito.*;                              ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  GIVEN (Setup):                                                       ║
                ║     given(mock.method()).willReturn(value)                           ║
                ║     given(mock.method()).willThrow(exception)                        ║
                ║     given(mock.method()).willAnswer(invocation -> ...)               ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  WHEN (Action):                                                       ║
                ║     result = service.methodUnderTest(args);                          ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  THEN (Verify):                                                       ║
                ║     then(mock).should().method(args)                                 ║
                ║     then(mock).should(times(n)).method()                             ║
                ║     then(mock).should(never()).method()                              ║
                ║     then(mock).shouldHaveNoInteractions()                            ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  Benefits:                                                            ║
                ║     → More readable test structure                                   ║
                ║     → Matches BDD naming conventions                                 ║
                ║     → Same functionality as standard Mockito                         ║
                ╚═══════════════════════════════════════════════════════════════════════╝
                """);
        assertTrue(true);
    }
}
