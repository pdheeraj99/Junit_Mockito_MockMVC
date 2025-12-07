package com.learning.junit5;

import com.learning.service.CalculatorService;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ PART 2: EXCEPTION TESTING ║
 * ║ Learn how to test that your code throws the RIGHT exceptions! ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 * 
 * KEY METHODS:
 * - assertThrows → Checks if an exception of given TYPE or its SUBCLASS is
 * thrown
 * - assertThrowsExactly → Checks if EXACTLY the specified exception type is
 * thrown
 * - assertDoesNotThrow → Checks that NO exception is thrown
 */
@DisplayName("📚 Part 2: Exception Testing")
class Part2_ExceptionTestingTest {

    private CalculatorService calculator;

    @BeforeEach
    void setUp() {
        calculator = new CalculatorService();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // assertThrows
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * assertThrows - Basic usage
     * Tests that an exception is thrown when dividing by zero
     */
    @Test
    @DisplayName("1️⃣ assertThrows: Division by zero throws ArithmeticException")
    void testAssertThrows_DivisionByZero() {
        // SYNTAX: assertThrows(ExpectedException.class, () -> codeToTest)

        assertThrows(ArithmeticException.class, () -> {
            calculator.divide(10, 0);
        });

        System.out.println("✅ ArithmeticException was thrown as expected!");
    }

    /**
     * assertThrows with message verification
     * You can capture the exception and verify its message
     */
    @Test
    @DisplayName("2️⃣ assertThrows: Verify exception message")
    void testAssertThrows_VerifyMessage() {
        // Capture the thrown exception
        ArithmeticException exception = assertThrows(
                ArithmeticException.class,
                () -> calculator.divide(5, 0));

        // Now verify the exception message
        assertEquals("Cannot divide by zero!", exception.getMessage());

        System.out.println("✅ Exception message verified: " + exception.getMessage());
    }

    /**
     * assertThrows - Testing IllegalArgumentException
     */
    @Test
    @DisplayName("3️⃣ assertThrows: Negative factorial throws IllegalArgumentException")
    void testAssertThrows_NegativeFactorial() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.factorial(-5));

        assertTrue(exception.getMessage().contains("non-negative"));

        System.out.println("✅ IllegalArgumentException thrown for negative factorial");
    }

    /**
     * assertThrows - Testing with multiple invalid inputs
     */
    @Test
    @DisplayName("4️⃣ assertThrows: Invalid score throws IllegalArgumentException")
    void testAssertThrows_InvalidScore() {
        // Score below 0
        assertThrows(IllegalArgumentException.class, () -> calculator.getGrade(-1));

        // Score above 100
        assertThrows(IllegalArgumentException.class, () -> calculator.getGrade(101));

        // Score WAY out of range
        assertThrows(IllegalArgumentException.class, () -> calculator.getGrade(500));

        System.out.println("✅ All invalid scores correctly threw exceptions!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // assertThrowsExactly
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * assertThrowsExactly - Matches EXACT exception type (no subclasses)
     * 
     * DIFFERENCE FROM assertThrows:
     * - assertThrows: passes if thrown exception IS-A expected type (subclass OK)
     * - assertThrowsExactly: passes ONLY if exact same class
     */
    @Test
    @DisplayName("5️⃣ assertThrowsExactly: Exact exception type matching")
    void testAssertThrowsExactly() {
        // This will pass - ArithmeticException is EXACTLY what's thrown
        assertThrowsExactly(ArithmeticException.class, () -> {
            calculator.divide(10, 0);
        });

        System.out.println("✅ assertThrowsExactly passed for ArithmeticException!");
    }

    /**
     * Demonstrating the difference between assertThrows and assertThrowsExactly
     */
    @Test
    @DisplayName("6️⃣ assertThrows vs assertThrowsExactly: Understanding the difference")
    void testDifferenceBetweenThrowsMethods() {
        // ArithmeticException extends RuntimeException
        // So assertThrows(RuntimeException.class) will PASS
        // But assertThrowsExactly(RuntimeException.class) will FAIL

        // This PASSES - ArithmeticException IS-A RuntimeException
        assertThrows(RuntimeException.class, () -> calculator.divide(10, 0));
        System.out.println("  ✓ assertThrows(RuntimeException.class) PASSED");

        // This PASSES - exact match
        assertThrowsExactly(ArithmeticException.class, () -> calculator.divide(10, 0));
        System.out.println("  ✓ assertThrowsExactly(ArithmeticException.class) PASSED");

        // NOTE: assertThrowsExactly(RuntimeException.class, () -> calculator.divide(10,
        // 0))
        // would FAIL because ArithmeticException is thrown, not RuntimeException
        // exactly!

        System.out.println("\n💡 TIP: Use assertThrowsExactly when you need strict type checking!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // assertDoesNotThrow
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * assertDoesNotThrow - Verify that code runs without throwing any exception
     */
    @Test
    @DisplayName("7️⃣ assertDoesNotThrow: Valid operations don't throw")
    void testAssertDoesNotThrow() {
        // Valid division should not throw
        assertDoesNotThrow(() -> calculator.divide(10, 2));

        // Valid factorial should not throw
        assertDoesNotThrow(() -> calculator.factorial(5));

        // Valid grade should not throw
        assertDoesNotThrow(() -> calculator.getGrade(85));

        System.out.println("✅ All valid operations completed without exceptions!");
    }

    /**
     * assertDoesNotThrow - With return value
     * You can capture the result of the operation too!
     */
    @Test
    @DisplayName("8️⃣ assertDoesNotThrow: Capture return value")
    void testAssertDoesNotThrow_WithReturnValue() {
        // Execute and capture result
        double result = assertDoesNotThrow(() -> calculator.divide(10, 2));

        assertEquals(5.0, result);

        System.out.println("✅ Operation completed without exception, result: " + result);
    }
}
