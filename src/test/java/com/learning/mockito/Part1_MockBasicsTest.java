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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ PART 1: MOCKITO BASICS - @Mock, @InjectMocks, when/thenReturn ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 * 
 * This test class demonstrates:
 * 1. Setting up mocks with @Mock
 * 2. Injecting mocks with @InjectMocks
 * 3. Basic stubbing with when().thenReturn()
 * 4. Basic verification with verify()
 */
@ExtendWith(MockitoExtension.class) // Required! Enables Mockito annotations
@DisplayName("📚 Part 1: Mockito Basics")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Part1_MockBasicsTest {

    // ═══════════════════════════════════════════════════════════════════════════
    // MOCK SETUP
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * @Mock - Creates a FAKE UserRepository
     *       - No real database connection
     *       - Returns null/empty by default
     *       - We control what it returns using when().thenReturn()
     */
    @Mock
    private UserRepository userRepository;

    /**
     * @Mock - Creates a FAKE EmailService
     *       - No real emails sent
     *       - We just verify it was called correctly
     */
    @Mock
    private EmailService emailService;

    /**
     * @InjectMocks - Creates REAL UserService
     *              - But with MOCK dependencies injected
     *              - This is the class we're actually TESTING
     */
    @InjectMocks
    private UserService userService;

    // ═══════════════════════════════════════════════════════════════════════════
    // TEST 1: Default Mock Behavior
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(1)
    @DisplayName("1️⃣ Default mock behavior - returns null/empty/false")
    void testDefaultMockBehavior() {
        System.out.println("=== DEFAULT MOCK BEHAVIOR ===\n");

        // Without stubbing, mocks return default values:

        // Objects return null
        Optional<User> userById = userRepository.findById(1L);
        System.out.println("findById(1L) → " + userById);
        assertEquals(Optional.empty(), userById); // Empty optional

        // Booleans return false
        boolean exists = userRepository.existsByEmail("any@email.com");
        System.out.println("existsByEmail(any) → " + exists);
        assertFalse(exists);

        // Numbers return 0
        long count = userRepository.count();
        System.out.println("count() → " + count);
        assertEquals(0, count);

        // Collections return empty
        List<User> allUsers = userRepository.findAll();
        System.out.println("findAll() → " + allUsers);
        assertTrue(allUsers.isEmpty());

        System.out.println("\n✅ Mocks return safe defaults without stubbing!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TEST 2: Basic Stubbing with when().thenReturn()
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(2)
    @DisplayName("2️⃣ when().thenReturn() - Tell mock what to return")
    void testBasicStubbing() {
        System.out.println("=== BASIC STUBBING ===\n");

        // ARRANGE: Create fake data
        User fakeUser = new User(1L, "Ramesh", "ramesh@example.com");

        // ARRANGE: Tell mock what to return
        when(userRepository.findById(1L)).thenReturn(Optional.of(fakeUser));

        // ACT: Call the mock
        Optional<User> result = userRepository.findById(1L);

        // ASSERT: Got what we stubbed!
        assertTrue(result.isPresent());
        assertEquals("Ramesh", result.get().getName());
        assertEquals("ramesh@example.com", result.get().getEmail());

        System.out.println("Stubbed: when findById(1L) → return " + fakeUser);
        System.out.println("Result: " + result.get());
        System.out.println("\n✅ Mock returned exactly what we told it to!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TEST 3: Testing Service with Mocked Dependencies
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(3)
    @DisplayName("3️⃣ Testing UserService.registerUser() - Happy Path")
    void testRegisterUser_Success() {
        System.out.println("=== TESTING SERVICE LOGIC ===\n");

        // ARRANGE: Setup mock responses
        String testEmail = "newuser@example.com";

        // 1. existsByEmail should return false (email not taken)
        when(userRepository.existsByEmail(testEmail)).thenReturn(false);

        // 2. save should return user with ID assigned
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(100L); // Simulate DB generating ID
            return user;
        });

        // 3. emailService - don't need to stub (void method), just verify later
        when(emailService.sendWelcomeEmail(anyString(), anyString())).thenReturn(true);

        System.out.println("Mocks configured:");
        System.out.println("  - existsByEmail('" + testEmail + "') → false");
        System.out.println("  - save(any) → assigns ID 100");
        System.out.println("  - sendWelcomeEmail → true\n");

        // ACT: Call the REAL service method (with mock dependencies)
        User registeredUser = userService.registerUser("New User", testEmail, "password123");

        // ASSERT: Verify service logic worked correctly
        assertNotNull(registeredUser);
        assertEquals(100L, registeredUser.getId());
        assertEquals("New User", registeredUser.getName());
        assertEquals(testEmail, registeredUser.getEmail());
        assertTrue(registeredUser.isActive());

        System.out.println("Result: " + registeredUser);
        System.out.println("\n✅ Service logic tested WITHOUT real database/email!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TEST 4: Basic Verification with verify()
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(4)
    @DisplayName("4️⃣ verify() - Check that mock methods were called")
    void testVerifyMethodCalls() {
        System.out.println("=== VERIFICATION ===\n");

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

        // VERIFY: Check that specific methods were called
        verify(userRepository).existsByEmail("test@email.com");
        System.out.println("✓ Verified: existsByEmail('test@email.com') was called");

        verify(userRepository).save(any(User.class));
        System.out.println("✓ Verified: save() was called");

        verify(emailService).sendWelcomeEmail("test@email.com", "Test");
        System.out.println("✓ Verified: sendWelcomeEmail('test@email.com', 'Test') was called");

        // VERIFY: Check that method was NOT called
        verify(userRepository, never()).deleteById(anyLong());
        System.out.println("✓ Verified: deleteById() was NEVER called");

        System.out.println("\n✅ All expected interactions verified!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TEST 5: Testing Exception Scenarios
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(5)
    @DisplayName("5️⃣ Testing when email already exists - should throw exception")
    void testRegisterUser_EmailExists_ThrowsException() {
        System.out.println("=== EXCEPTION SCENARIO ===\n");

        // ARRANGE: Email already exists!
        when(userRepository.existsByEmail("existing@email.com")).thenReturn(true);

        System.out.println("Stubbed: existsByEmail('existing@email.com') → TRUE\n");

        // ACT & ASSERT: Should throw exception
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> userService.registerUser("Test", "existing@email.com", "pass123"));

        // VERIFY exception message
        assertTrue(exception.getMessage().contains("already registered"));
        System.out.println("Exception thrown: " + exception.getMessage());

        // VERIFY: save was NOT called (registration stopped early)
        verify(userRepository, never()).save(any(User.class));
        System.out.println("✓ Verified: save() was NOT called (stopped before save)");

        // VERIFY: email was NOT sent
        verify(emailService, never()).sendWelcomeEmail(anyString(), anyString());
        System.out.println("✓ Verified: sendWelcomeEmail() was NOT called");

        System.out.println("\n✅ Exception handling tested correctly!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TEST 6: Testing with Multiple Return Values
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(6)
    @DisplayName("6️⃣ Stubbing with lists - findAll() multiple users")
    void testFindAllActiveUsers() {
        System.out.println("=== STUBBING LISTS ===\n");

        // ARRANGE: Create test data
        List<User> fakeUsers = Arrays.asList(
                new User(1L, "User1", "user1@email.com"),
                new User(2L, "User2", "user2@email.com"),
                new User(3L, "User3", "user3@email.com"));

        // Stub the mock
        when(userRepository.findByActiveTrue()).thenReturn(fakeUsers);

        // ACT
        List<User> activeUsers = userService.getActiveUsers();

        // ASSERT
        assertEquals(3, activeUsers.size());
        assertEquals("User1", activeUsers.get(0).getName());
        assertEquals("User2", activeUsers.get(1).getName());
        assertEquals("User3", activeUsers.get(2).getName());

        System.out.println("Returned users:");
        activeUsers.forEach(u -> System.out.println("  - " + u));

        System.out.println("\n✅ List stubbing works!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SUMMARY
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(7)
    @DisplayName("📋 SUMMARY: Mockito Basics")
    void testSummary() {
        System.out.println("""

                ╔═══════════════════════════════════════════════════════════════════════╗
                ║                    MOCKITO BASICS SUMMARY                             ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  @ExtendWith(MockitoExtension.class)                                  ║
                ║     → Required on test class to enable Mockito                       ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  @Mock                                                                ║
                ║     → Creates fake object (dependency)                               ║
                ║     → Returns null/0/false/empty by default                          ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  @InjectMocks                                                         ║
                ║     → Creates real object (class under test)                         ║
                ║     → Automatically injects @Mock fields                             ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  when(mock.method()).thenReturn(value)                                ║
                ║     → Tell mock what to return                                       ║
                ║     → Called "stubbing"                                               ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  verify(mock).method()                                                ║
                ║     → Check that method was called                                   ║
                ║     → verify(mock, never()) = was NOT called                         ║
                ╚═══════════════════════════════════════════════════════════════════════╝
                """);
        assertTrue(true);
    }
}
