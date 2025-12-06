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
import org.mockito.stubbing.Answer;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ PART 2: STUBBING - when/thenReturn, thenThrow, thenAnswer, doReturn ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("📚 Part 2: Stubbing Methods")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Part2_StubbingTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    // ═══════════════════════════════════════════════════════════════════════════
    // thenReturn() - Simple Values
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(1)
    @DisplayName("1️⃣ thenReturn() - Return a single value")
    void testThenReturn_SingleValue() {
        System.out.println("=== thenReturn() - Single Value ===\n");

        // ARRANGE
        User fakeUser = new User(1L, "Ramesh", "ramesh@email.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(fakeUser));

        // ACT
        Optional<User> result = userService.findById(1L);

        // ASSERT
        assertTrue(result.isPresent());
        assertEquals("Ramesh", result.get().getName());

        System.out.println("Stubbed: findById(1L) → " + fakeUser);
        System.out.println("Result: " + result.get());
        System.out.println("\n✅ thenReturn() works!");
    }

    @Test
    @Order(2)
    @DisplayName("2️⃣ thenReturn() - Multiple consecutive values")
    void testThenReturn_MultipleValues() {
        System.out.println("=== thenReturn() - Multiple Values ===\n");

        // ARRANGE: Different values for consecutive calls
        when(userRepository.count())
                .thenReturn(1L, 2L, 5L, 10L);

        // ACT & ASSERT: Each call returns next value
        assertEquals(1L, userRepository.count());
        System.out.println("1st call: 1");

        assertEquals(2L, userRepository.count());
        System.out.println("2nd call: 2");

        assertEquals(5L, userRepository.count());
        System.out.println("3rd call: 5");

        assertEquals(10L, userRepository.count());
        System.out.println("4th call: 10");

        // After exhausting values, repeats last one
        assertEquals(10L, userRepository.count());
        System.out.println("5th call: 10 (repeats last)");

        System.out.println("\n✅ Consecutive returns work!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // thenThrow() - Exception Stubbing
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(3)
    @DisplayName("3️⃣ thenThrow() - Throw exception")
    void testThenThrow_Exception() {
        System.out.println("=== thenThrow() - Exceptions ===\n");

        // ARRANGE: Throw exception for specific input
        when(userRepository.findById(999L))
                .thenThrow(new RuntimeException("Database connection failed"));

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            userService.findById(999L);
        });

        assertEquals("Database connection failed", ex.getMessage());
        System.out.println("Exception thrown: " + ex.getMessage());

        System.out.println("\n✅ thenThrow() works!");
    }

    @Test
    @Order(4)
    @DisplayName("4️⃣ thenThrow() - Throw then succeed (retry scenario)")
    void testThenThrow_ThenSucceed() {
        System.out.println("=== thenThrow() then thenReturn() - Retry ===\n");

        User user = new User(1L, "RetryUser", "retry@email.com");

        // ARRANGE: First call fails, second succeeds (simulating retry)
        when(userRepository.findById(1L))
                .thenThrow(new RuntimeException("Temporary failure"))
                .thenReturn(Optional.of(user));

        // First call: throws
        System.out.println("1st call: expecting exception...");
        assertThrows(RuntimeException.class, () -> userService.findById(1L));
        System.out.println("Exception thrown!");

        // Second call (retry): succeeds
        System.out.println("2nd call (retry): expecting success...");
        Optional<User> result = userService.findById(1L);
        assertTrue(result.isPresent());
        System.out.println("Success: " + result.get());

        System.out.println("\n✅ Retry scenario simulated!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // thenAnswer() - Dynamic/Custom Logic
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(5)
    @DisplayName("5️⃣ thenAnswer() - Access arguments and return dynamically")
    void testThenAnswer_DynamicReturn() {
        System.out.println("=== thenAnswer() - Dynamic Logic ===\n");

        // ARRANGE: Save should assign ID based on current time
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userArg = invocation.getArgument(0); // Get first argument
            userArg.setId(12345L); // Simulate DB assigning ID
            return userArg;
        });

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(emailService.sendWelcomeEmail(anyString(), anyString())).thenReturn(true);

        // ACT
        User result = userService.registerUser("Dynamic", "dynamic@email.com", "pass123");

        // ASSERT
        assertEquals(12345L, result.getId());
        assertEquals("Dynamic", result.getName());

        System.out.println("Saved user got ID: " + result.getId());
        System.out.println("\n✅ thenAnswer() allows custom logic!");
    }

    @Test
    @Order(6)
    @DisplayName("6️⃣ thenAnswer() - Echo/transform input")
    void testThenAnswer_TransformInput() {
        System.out.println("=== thenAnswer() - Transform Input ===\n");

        // ARRANGE: processInput returns UPPER case
        // (Simulating a mock that transforms input)
        Answer<Optional<User>> echoAnswer = invocation -> {
            Long id = invocation.getArgument(0);
            return Optional.of(new User(id, "User" + id, "user" + id + "@email.com"));
        };

        when(userRepository.findById(anyLong())).thenAnswer(echoAnswer);

        // ACT & ASSERT
        assertEquals("User5", userService.findById(5L).get().getName());
        assertEquals("User99", userService.findById(99L).get().getName());
        assertEquals("User123", userService.findById(123L).get().getName());

        System.out.println("findById(5) → User5");
        System.out.println("findById(99) → User99");
        System.out.println("findById(123) → User123");

        System.out.println("\n✅ Dynamic response based on input!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // doReturn/doThrow/doNothing - Alternative Syntax
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(7)
    @DisplayName("7️⃣ doReturn() - Alternative syntax")
    void testDoReturn() {
        System.out.println("=== doReturn() - Alternative Syntax ===\n");

        User user = new User(1L, "DoReturn", "doreturn@email.com");

        // Alternative syntax (useful for spies and void methods)
        doReturn(Optional.of(user)).when(userRepository).findById(1L);

        // ACT
        Optional<User> result = userRepository.findById(1L);

        // ASSERT
        assertTrue(result.isPresent());
        assertEquals("DoReturn", result.get().getName());

        System.out.println("doReturn() equivalent to when().thenReturn()");
        System.out.println("\n✅ doReturn() works!");
    }

    @Test
    @Order(8)
    @DisplayName("8️⃣ doThrow() - For void methods")
    void testDoThrow_VoidMethod() {
        System.out.println("=== doThrow() for Void Methods ===\n");

        // ARRANGE: Make void method throw
        // Note: Can't use when().thenThrow() for void methods!
        doThrow(new RuntimeException("Delete failed"))
                .when(userRepository).deleteById(anyLong());

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            userRepository.deleteById(1L);
        });

        assertEquals("Delete failed", ex.getMessage());
        System.out.println("Void method threw exception: " + ex.getMessage());

        System.out.println("\n✅ doThrow() works for void methods!");
    }

    @Test
    @Order(9)
    @DisplayName("9️⃣ doNothing() - Suppress void method")
    void testDoNothing() {
        System.out.println("=== doNothing() - Suppress Void ===\n");

        // ARRANGE: Do nothing when delete is called
        doNothing().when(userRepository).deleteById(anyLong());

        // ACT: Call the method
        assertDoesNotThrow(() -> {
            userRepository.deleteById(1L);
            userRepository.deleteById(2L);
            userRepository.deleteById(3L);
        });

        System.out.println("deleteById() called 3 times - did nothing each time");

        // VERIFY it was called
        verify(userRepository, times(3)).deleteById(anyLong());

        System.out.println("\n✅ doNothing() suppressed the void method!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SUMMARY
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(10)
    @DisplayName("📋 SUMMARY: Stubbing Methods")
    void testSummary() {
        System.out.println("""

                ╔═══════════════════════════════════════════════════════════════════════╗
                ║                    STUBBING METHODS SUMMARY                           ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  when(mock.method()).thenReturn(value)                                ║
                ║     → Return specific value                                          ║
                ║     → thenReturn(v1, v2, v3) for consecutive calls                   ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  when(mock.method()).thenThrow(exception)                             ║
                ║     → Make mock throw exception                                      ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  when(mock.method()).thenAnswer(inv -> { ... })                       ║
                ║     → Custom logic with access to arguments                          ║
                ║     → inv.getArgument(0) = first argument                            ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  doReturn(value).when(mock).method()                                  ║
                ║     → Alternative syntax (for spies, void methods)                   ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  doThrow(exception).when(mock).voidMethod()                           ║
                ║     → Make void method throw exception                               ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  doNothing().when(mock).voidMethod()                                  ║
                ║     → Suppress void method (do nothing)                              ║
                ╚═══════════════════════════════════════════════════════════════════════╝
                """);
        assertTrue(true);
    }
}
