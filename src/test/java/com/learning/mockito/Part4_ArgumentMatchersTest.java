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
import static org.mockito.Mockito.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ PART 4: ARGUMENT MATCHERS - any(), eq(), argThat(), contains() ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("📚 Part 4: Argument Matchers")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Part4_ArgumentMatchersTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    // ═══════════════════════════════════════════════════════════════════════════
    // Basic Type Matchers
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(1)
    @DisplayName("1️⃣ any() - Match any object")
    void testAnyMatcher() {
        System.out.println("=== any() Matcher ===\n");

        // any(Class) - matches any object of type
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(999L);
            return u;
        });

        // Will match for ANY User object
        User user1 = new User("User1", "u1@email.com", "pass");
        User user2 = new User("User2", "u2@email.com", "pass");

        userRepository.save(user1);
        userRepository.save(user2);

        assertEquals(999L, user1.getId());
        assertEquals(999L, user2.getId());

        System.out.println("✓ any(User.class) matched both saves");
        System.out.println("\n✅ any() works!");
    }

    @Test
    @Order(2)
    @DisplayName("2️⃣ anyLong(), anyString(), anyBoolean() - Primitive matchers")
    void testPrimitiveMatchers() {
        System.out.println("=== Primitive Matchers ===\n");

        User defaultUser = new User(1L, "Default", "default@email.com");

        // anyLong() - match any Long value
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(defaultUser));

        // All these match!
        assertTrue(userService.findById(1L).isPresent());
        assertTrue(userService.findById(100L).isPresent());
        assertTrue(userService.findById(999999L).isPresent());

        System.out.println("✓ anyLong() matched 1, 100, 999999");

        // anyString()
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertTrue(userRepository.existsByEmail("any@email.com"));
        assertTrue(userRepository.existsByEmail("different@email.com"));

        System.out.println("✓ anyString() matched multiple emails");

        System.out.println("\n✅ Primitive matchers work!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // eq() - Exact Match
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(3)
    @DisplayName("3️⃣ eq() - Exact value match (required when mixing)")
    void testEqMatcher() {
        System.out.println("=== eq() Matcher ===\n");

        // When you use ANY matcher, ALL args must be matchers!
        // eq() wraps literal values

        when(emailService.sendWelcomeEmail(eq("specific@email.com"), anyString()))
                .thenReturn(true);

        // Only matches if first arg is exactly "specific@email.com"
        assertTrue(emailService.sendWelcomeEmail("specific@email.com", "Any Name"));

        // Different email - won't match this stub!
        when(emailService.sendWelcomeEmail(eq("other@email.com"), anyString()))
                .thenReturn(false);

        assertFalse(emailService.sendWelcomeEmail("other@email.com", "Any Name"));

        System.out.println("✓ eq('specific@email.com') only matches exact value");
        System.out.println("✓ Different stubs for different exact values");

        System.out.println("\n✅ eq() works!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // String Matchers
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(4)
    @DisplayName("4️⃣ contains(), startsWith(), endsWith() - String matchers")
    void testStringMatchers() {
        System.out.println("=== String Matchers ===\n");

        // Setup
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(emailService.sendWelcomeEmail(anyString(), anyString())).thenReturn(true);

        // ACT
        userService.registerUser("Test Kumar", "test@example.com", "pass123");

        // VERIFY with string matchers
        verify(userRepository).existsByEmail(contains("@"));
        System.out.println("✓ contains('@') matched");

        verify(emailService).sendWelcomeEmail(endsWith(".com"), anyString());
        System.out.println("✓ endsWith('.com') matched");

        verify(emailService).sendWelcomeEmail(anyString(), startsWith("Test"));
        System.out.println("✓ startsWith('Test') matched");

        System.out.println("\n✅ String matchers work!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // argThat() - Custom Condition
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(5)
    @DisplayName("5️⃣ argThat() - Custom matching logic")
    void testArgThatMatcher() {
        System.out.println("=== argThat() - Custom Matcher ===\n");

        // Setup
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(emailService.sendWelcomeEmail(anyString(), anyString())).thenReturn(true);

        // ACT
        userService.registerUser("Ramesh Kumar", "ramesh@gmail.com", "secure123");

        // VERIFY with custom condition
        verify(userRepository).save(argThat(user -> user.getName().equals("Ramesh Kumar") &&
                user.getEmail().contains("@gmail")));
        System.out.println("✓ argThat() verified name AND email domain");

        verify(emailService).sendWelcomeEmail(
                argThat(email -> email.length() > 5 && email.contains("@")),
                argThat(name -> name.split(" ").length >= 2) // Has first and last name
        );
        System.out.println("✓ argThat() with complex conditions");

        System.out.println("\n✅ argThat() allows any custom logic!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // IMPORTANT: All or None Rule
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(6)
    @DisplayName("6️⃣ ⚠️ ALL OR NONE RULE - Must use matchers consistently")
    void testAllOrNoneRule() {
        System.out.println("=== ALL OR NONE RULE ===\n");

        System.out.println("""
                    ⚠️ IMPORTANT RULE:
                    If you use ANY matcher, ALL arguments must be matchers!

                    ❌ WRONG:
                    when(service.method(anyString(), "literal"))
                        → Compile error or runtime error!

                    ✅ CORRECT:
                    when(service.method(anyString(), eq("literal")))
                        → Use eq() to wrap literal values!
                """);

        // Correct usage
        when(emailService.sendWelcomeEmail(anyString(), eq("Ramesh"))).thenReturn(true);
        when(emailService.sendWelcomeEmail(eq("ramesh@email.com"), anyString())).thenReturn(false);

        assertTrue(emailService.sendWelcomeEmail("any@email.com", "Ramesh"));
        assertFalse(emailService.sendWelcomeEmail("ramesh@email.com", "Any Name"));

        System.out.println("✅ Rule followed correctly!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Null Handling
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(7)
    @DisplayName("7️⃣ isNull(), notNull(), nullable() - Null handling")
    void testNullMatchers() {
        System.out.println("=== Null Matchers ===\n");

        // notNull() - matches non-null only
        when(userRepository.save(notNull())).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        User user = new User("Test", "test@email.com", "pass");
        User saved = userRepository.save(user);
        assertEquals(1L, saved.getId());
        System.out.println("✓ notNull() matched non-null user");

        // nullable() - matches type OR null
        when(userRepository.findByEmail(nullable(String.class)))
                .thenReturn(Optional.empty());

        assertTrue(userRepository.findByEmail("any@email.com").isEmpty());
        assertTrue(userRepository.findByEmail(null).isEmpty());
        System.out.println("✓ nullable() matched both string and null");

        System.out.println("\n✅ Null handling works!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SUMMARY
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(8)
    @DisplayName("📋 SUMMARY: Argument Matchers")
    void testSummary() {
        System.out.println("""

                ╔═══════════════════════════════════════════════════════════════════════╗
                ║                    ARGUMENT MATCHERS SUMMARY                          ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  any(Class.class)    → Match any object of type                      ║
                ║  anyLong(), anyInt() → Match any primitive                           ║
                ║  anyString()         → Match any string                              ║
                ║  anyList(), anyMap() → Match any collection                          ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  eq(value)           → Exact value match (use when mixing)           ║
                ║  same(object)        → Same reference                                ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  contains("text")    → String contains                               ║
                ║  startsWith("pre")   → String starts with                            ║
                ║  endsWith("suf")     → String ends with                              ║
                ║  matches("regex")    → Regex match                                   ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  argThat(predicate)  → Custom condition (lambda)                     ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  isNull()            → Null only                                     ║
                ║  notNull()           → Non-null only                                 ║
                ║  nullable(Class)     → Type or null                                  ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  ⚠️ RULE: If you use ANY matcher, ALL args must be matchers!        ║
                ║     Use eq() to wrap literal values: eq("literal")                   ║
                ╚═══════════════════════════════════════════════════════════════════════╝
                """);
        assertTrue(true);
    }
}
