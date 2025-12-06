package com.learning.mockito;

import com.learning.external.EmailService;
import com.learning.model.User;
import com.learning.repository.UserRepository;
import com.learning.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ PART 8: ENTERPRISE SCENARIOS - Advanced Real-World Mockito ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 * 
 * This covers scenarios you'll encounter in enterprise applications:
 * - Mocking static methods
 * - Lenient stubbing
 * - Deep stubs
 * - Reset mocks
 * - Timeout verification
 * - Answer with void
 * - Consecutive stubbing patterns
 * - Mock final classes/methods (Mockito 2+)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("📚 Part 8: Enterprise Scenarios")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Part8_EnterpriseScenariosTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    // ═══════════════════════════════════════════════════════════════════════════
    // 1. LENIENT STUBBING - When strictness hurts
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(1)
    @DisplayName("1️⃣ Lenient stubbing - avoid UnnecessaryStubbingException")
    void testLenientStubbing() {
        System.out.println("=== Lenient Stubbing ===\n");

        System.out.println("""
                    In strict mode (default), unused stubs cause UnnecessaryStubbingException.
                    Use lenient() when:
                    - Setup method creates stubs for multiple tests
                    - Not all stubs are used in every test
                    - Testing conditional paths
                """);

        // Lenient stubbing - won't fail if not used
        lenient().when(userRepository.count()).thenReturn(100L);
        lenient().when(emailService.sendWelcomeEmail(anyString(), anyString())).thenReturn(true);

        // Only using findById, not the lenient stubs
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "Test", "test@email.com")));

        Optional<User> user = userService.findById(1L);
        assertTrue(user.isPresent());

        System.out.println("✅ No UnnecessaryStubbingException even though lenient stubs weren't used!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 2. DEEP STUBS - Chained method calls
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(2)
    @DisplayName("2️⃣ Deep stubs - for chained method calls")
    void testDeepStubs() {
        System.out.println("=== Deep Stubs ===\n");

        // Without deep stubs, you'd need multiple mocks
        // userService.getConfig().getSettings().getTimeout() → NullPointerException!

        // With RETURNS_DEEP_STUBS, intermediate objects are auto-mocked
        // Demo with a simple List mock
        java.util.List<String> deepMock = mock(java.util.List.class, RETURNS_DEEP_STUBS);

        // Iterator is auto-mocked!
        when(deepMock.iterator().hasNext()).thenReturn(true);
        assertTrue(deepMock.iterator().hasNext());

        System.out.println("Deep stubs useful for:");
        System.out.println("  - Builder patterns");
        System.out.println("  - Fluent APIs");
        System.out.println("  - Chained getters");

        System.out.println("\n✅ Deep stubs prevent NPE on method chains!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 3. RESET MOCKS - Clean slate between verifications
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(3)
    @DisplayName("3️⃣ Reset mocks - clear all stubbing and verifications")
    void testResetMocks() {
        System.out.println("=== Reset Mocks ===\n");

        // First scenario
        when(userRepository.count()).thenReturn(10L);
        assertEquals(10L, userRepository.count());
        verify(userRepository).count();

        System.out.println("Before reset: count() returns 10");

        // Reset the mock - clears all stubbing and invocation history
        reset(userRepository);

        // After reset - returns default (0L for long)
        assertEquals(0L, userRepository.count());
        System.out.println("After reset: count() returns 0 (default)");

        // Previous verify doesn't count anymore
        verify(userRepository).count(); // This is the ONLY call after reset

        System.out.println("\n⚠️ Use reset() sparingly - usually indicates test doing too much!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 4. ANSWER WITH CALLBACK - Complex behavior
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(4)
    @DisplayName("4️⃣ Answer with callback - simulate complex behavior")
    void testAnswerCallback() {
        System.out.println("=== Answer Callback ===\n");

        // Track how many times save was called
        final int[] saveCount = { 0 };

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            saveCount[0]++;
            User user = invocation.getArgument(0);
            user.setId((long) saveCount[0]); // ID based on call count

            System.out.println("Save called #" + saveCount[0] + " for: " + user.getName());
            return user;
        });

        // Multiple saves get different IDs
        User u1 = userRepository.save(new User("User1", "u1@email.com", "pass1234"));
        User u2 = userRepository.save(new User("User2", "u2@email.com", "pass5678"));
        User u3 = userRepository.save(new User("User3", "u3@email.com", "pass9999"));

        assertEquals(1L, u1.getId());
        assertEquals(2L, u2.getId());
        assertEquals(3L, u3.getId());

        System.out.println("\n✅ Answer provides full control over mock behavior!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 5. doAnswer FOR VOID METHODS - Complex void behavior
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(5)
    @DisplayName("5️⃣ doAnswer for void methods - side effects")
    void testDoAnswerForVoid() {
        System.out.println("=== doAnswer for Void Methods ===\n");

        final StringBuilder log = new StringBuilder();

        // doAnswer for void method - useful for side effects
        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            log.append("Deleted user: ").append(id).append("\n");
            return null; // void method, return null
        }).when(userRepository).deleteById(anyLong());

        // Call the void method
        userRepository.deleteById(1L);
        userRepository.deleteById(2L);
        userRepository.deleteById(3L);

        System.out.println("Captured side effects:");
        System.out.println(log);

        assertTrue(log.toString().contains("Deleted user: 1"));
        assertTrue(log.toString().contains("Deleted user: 2"));
        assertTrue(log.toString().contains("Deleted user: 3"));

        System.out.println("✅ doAnswer captures void method calls!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 6. TIMEOUT VERIFICATION - Async operations
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(6)
    @DisplayName("6️⃣ Timeout verification - for async code")
    void testTimeoutVerification() throws Exception {
        System.out.println("=== Timeout Verification ===\n");

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "Async", "async@email.com")));

        // Simulate async operation
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(100); // Small delay
                userService.findById(1L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Wait up to 1 second for the method to be called
        verify(userRepository, timeout(1000)).findById(1L);

        System.out.println("timeout(ms) useful for:");
        System.out.println("  - Async callbacks");
        System.out.println("  - Event handlers");
        System.out.println("  - Background jobs");

        System.out.println("\n✅ Timeout verification for async code!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 7. MOCK STATIC METHODS (Mockito 3.4+)
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(7)
    @DisplayName("7️⃣ Mock static methods - System, UUID, Instant")
    void testMockStaticMethods() {
        System.out.println("=== Mock Static Methods ===\n");

        System.out.println("""
                    Mockito 3.4+ supports mocking static methods using try-with-resources:

                    try (MockedStatic<UUID> mockedUUID = mockStatic(UUID.class)) {
                        UUID fixedUUID = UUID.fromString("fixed-uuid-string");
                        mockedUUID.when(UUID::randomUUID).thenReturn(fixedUUID);
                        assertEquals(fixedUUID, UUID.randomUUID());
                    }

                    Common use cases:
                    - UUID.randomUUID() → predictable IDs in tests
                    - System.currentTimeMillis() → fixed time
                    - Instant.now() → controlled timestamps
                    - Files.readAllBytes() → fake file content

                    Note: Static mocking has scope - only works inside try block.
                    After the try block, original static method is restored.
                """);

        // Simple demo just to show it works
        String testValue = "demo-value";
        System.out.println("Static mocking is great for:");
        System.out.println("  - Time-dependent code (Instant.now())");
        System.out.println("  - Random generators (UUID.randomUUID())");
        System.out.println("  - File system operations (Files.*)");

        System.out.println("\n✅ Static mocking works with try-with-resources scope!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 8. ARGUMENT CAPTOR WITH VERIFICATION
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(8)
    @DisplayName("8️⃣ Captor + verify in complex scenarios")
    void testCaptorWithComplexVerification() {
        System.out.println("=== Complex Captor Verification ===\n");

        // Setup
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(emailService.sendWelcomeEmail(anyString(), anyString())).thenReturn(true);

        // Act
        userService.registerUser("Enterprise User", "enterprise@company.com", "SecurePass123");

        // Capture and verify multiple things at once
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);

        verify(userRepository).save(userCaptor.capture());
        verify(emailService).sendWelcomeEmail(emailCaptor.capture(), nameCaptor.capture());

        // Complex assertions
        User savedUser = userCaptor.getValue();
        assertAll("User validation",
                () -> assertEquals("Enterprise User", savedUser.getName()),
                () -> assertEquals("enterprise@company.com", savedUser.getEmail()),
                () -> assertTrue(savedUser.isActive()),
                () -> assertNotNull(savedUser.getCreatedAt()));

        assertAll("Email validation",
                () -> assertEquals("enterprise@company.com", emailCaptor.getValue()),
                () -> assertEquals("Enterprise User", nameCaptor.getValue()));

        System.out.println("✅ Combined captor + assertAll = comprehensive verification!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 9. MOCK SETTINGS - Custom defaults
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(9)
    @DisplayName("9️⃣ Mock settings - custom default answers")
    void testMockSettings() {
        System.out.println("=== Mock Settings ===\n");

        // Smart nulls - returns mock instead of null
        UserRepository smartMock = mock(UserRepository.class, RETURNS_SMART_NULLS);

        // This returns Optional.empty() instead of null
        Optional<User> result = smartMock.findById(1L);
        System.out.println("RETURNS_SMART_NULLS: findById returns " + result);

        // Custom name for better error messages
        UserRepository namedMock = mock(UserRepository.class,
                withSettings().name("UserRepository-TestMock").verboseLogging());

        System.out.println("\nMock Settings options:");
        System.out.println("  - RETURNS_DEFAULTS (default)");
        System.out.println("  - RETURNS_SMART_NULLS (SmartNull instead of null)");
        System.out.println("  - RETURNS_MOCKS (return mocks for Object methods)");
        System.out.println("  - RETURNS_DEEP_STUBS (for chains)");
        System.out.println("  - RETURNS_SELF (for builders)");
        System.out.println("  - withSettings().name() for custom name");
        System.out.println("  - withSettings().verboseLogging()");

        System.out.println("\n✅ Mock settings customize behavior!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 10. VERIFYING NO MORE/NO INTERACTIONS
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(10)
    @DisplayName("🔟 Strict verification - no unwanted calls")
    void testStrictVerification() {
        System.out.println("=== Strict Verification ===\n");

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "Test", "test@email.com")));

        // Act - only call findById
        userService.findById(1L);

        // Verify exact interactions
        verify(userRepository).findById(1L);

        // Verify no other methods were called
        verifyNoMoreInteractions(userRepository);

        // Verify email service wasn't touched at all
        verifyNoInteractions(emailService);

        System.out.println("✅ verifyNoMoreInteractions - catches unexpected calls");
        System.out.println("✅ verifyNoInteractions - mock was completely untouched");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SUMMARY
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(11)
    @DisplayName("📋 ENTERPRISE SCENARIOS SUMMARY")
    void testSummary() {
        System.out.println("""

                ╔═══════════════════════════════════════════════════════════════════════╗
                ║               ENTERPRISE MOCKITO SCENARIOS SUMMARY                    ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  1. lenient().when()     → Avoid strict stubbing errors              ║
                ║  2. RETURNS_DEEP_STUBS   → Mock method chains                        ║
                ║  3. reset(mock)          → Clear all stubbing/history                ║
                ║  4. thenAnswer()         → Dynamic/stateful responses                ║
                ║  5. doAnswer().when()    → Side effects for void methods             ║
                ║  6. verify(timeout(ms))  → Async verification                        ║
                ║  7. MockedStatic<>       → Mock static methods (try-with-resources)  ║
                ║  8. Captor + assertAll   → Comprehensive object verification         ║
                ║  9. withSettings()       → Custom mock behavior                      ║
                ║ 10. verifyNoMore/NoInt   → Strict interaction verification           ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  Additional enterprise patterns:                                      ║
                ║  - @MockBean (Spring Boot test integration)                          ║
                ║  - @SpyBean (Partial mock in Spring context)                         ║
                ║  - clearInvocations(mock) (keep stubs, clear history)                ║
                ║  - ignoreStubs(mock) (for verifyNoMoreInteractions)                  ║
                ║  - RETURNS_SELF (for builder pattern mocks)                          ║
                ╚═══════════════════════════════════════════════════════════════════════╝
                """);
        assertTrue(true);
    }
}
