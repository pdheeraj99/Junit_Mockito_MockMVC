package com.learning.junit5;

import com.learning.service.CalculatorService;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ PART 10: REMAINING CONCEPTS - assertAll, Assumptions, TestInstance, etc. ║
 * ║ Mawa, idi last part - complete 99% coverage istundi! ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 */
@DisplayName("📚 Part 10: Remaining JUnit 5 Concepts")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Share instance across tests!
class Part10_RemainingConceptsTest {

    private CalculatorService calculator;
    private int testCount = 0; // This WORKS because PER_CLASS!

    // ═══════════════════════════════════════════════════════════════════════════
    // @TestInstance - Control Instance Lifecycle
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * @TestInstance(Lifecycle.PER_CLASS) - Same instance for ALL tests
     * 
     * DEFAULT: PER_METHOD - New instance for each test (isolation)
     * PER_CLASS - Same instance (can share state, @BeforeAll can be non-static!)
     */
    @BeforeAll
    void setUpOnce() { // NOT STATIC! Because PER_CLASS
        calculator = new CalculatorService();
        System.out.println("🔷 @BeforeAll (non-static!) - PER_CLASS mode");
    }

    @BeforeEach
    void countTest() {
        testCount++; // Works because same instance!
    }

    @Test
    @Order(1)
    @DisplayName("1️⃣ @TestInstance: State preserved across tests")
    void testTestInstance_StatePersists() {
        System.out.println("  Test count: " + testCount);
        assertTrue(testCount >= 1);
        System.out.println("  ✓ @TestInstance(PER_CLASS) preserves state!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // assertAll - Grouped Assertions (IMPORTANT!)
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * assertAll - Run ALL assertions even if some fail!
     * 
     * Without assertAll: First failure stops execution
     * With assertAll: ALL failures are reported together
     * 
     * SUPER USEFUL for testing multiple properties of an object!
     */
    @Test
    @Order(2)
    @DisplayName("2️⃣ assertAll: Grouped assertions - see ALL failures at once")
    void testAssertAll_GroupedAssertions() {
        int a = 10, b = 5;

        // All these assertions run even if one fails!
        assertAll("Calculator operations",
                () -> assertEquals(15, calculator.add(a, b), "Addition"),
                () -> assertEquals(5, calculator.subtract(a, b), "Subtraction"),
                () -> assertEquals(50, calculator.multiply(a, b), "Multiplication"),
                () -> assertEquals(2.0, calculator.divide(a, b), "Division"));

        System.out.println("  ✓ All 4 operations passed in one assertAll!");
    }

    /**
     * assertAll - Nested groups
     */
    @Test
    @Order(3)
    @DisplayName("3️⃣ assertAll: Nested groups with headers")
    void testAssertAll_NestedGroups() {
        assertAll("Complete validation",
                () -> assertAll("Positive numbers",
                        () -> assertTrue(calculator.isPositive(1)),
                        () -> assertTrue(calculator.isPositive(100))),
                () -> assertAll("Even numbers",
                        () -> assertTrue(calculator.isEven(2)),
                        () -> assertTrue(calculator.isEven(100))),
                () -> assertAll("Prime numbers",
                        () -> assertTrue(calculator.isPrime(2)),
                        () -> assertTrue(calculator.isPrime(17))));

        System.out.println("  ✓ Nested assertAll groups passed!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Assumptions - assumeTrue, assumeFalse, assumingThat
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * assumeTrue - SKIP test if condition is false (not FAIL!)
     * 
     * DIFFERENCE from assertTrue:
     * - assertTrue: FAILS the test if false
     * - assumeTrue: SKIPS the test if false (no failure!)
     * 
     * Use for: environment-specific tests, CI vs local
     */
    @Test
    @Order(4)
    @DisplayName("4️⃣ assumeTrue: Skip if condition is false")
    void testAssumeTrue() {
        // This test SKIPS on non-Windows, doesn't FAIL
        assumeTrue(System.getProperty("os.name").contains("Windows"),
                "Skipping: requires Windows");

        // Only runs if assumption passes
        assertEquals(5, calculator.add(2, 3));
        System.out.println("  ✓ Running on Windows - test executed!");
    }

    /**
     * assumeFalse - SKIP test if condition is TRUE
     */
    @Test
    @Order(5)
    @DisplayName("5️⃣ assumeFalse: Skip if condition is true")
    void testAssumeFalse() {
        // Skip in CI environment
        assumeFalse("true".equals(System.getenv("CI")),
                "Skipping in CI environment");

        assertEquals(120, calculator.factorial(5));
        System.out.println("  ✓ Not in CI - test executed!");
    }

    /**
     * assumingThat - Execute part of test conditionally
     * 
     * Rest of test always runs, only the lambda is conditional
     */
    @Test
    @Order(6)
    @DisplayName("6️⃣ assumingThat: Conditional execution within test")
    void testAssumingThat() {
        boolean isWindows = System.getProperty("os.name").contains("Windows");

        // This part only runs on Windows
        assumingThat(isWindows, () -> {
            System.out.println("  → Windows-specific assertion running...");
            assertTrue(calculator.isPositive(100));
        });

        // This ALWAYS runs, regardless of assumption
        assertEquals(10, calculator.add(5, 5));
        System.out.println("  ✓ Common assertion passed (always runs)!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // fail() - Explicit Test Failure
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * fail() - Explicitly fail a test
     * 
     * Use for:
     * - Marking tests as TODO
     * - Forcing failure in conditional blocks
     * - Asserting unreachable code shouldn't be reached
     */
    @Test
    @Order(7)
    @DisplayName("7️⃣ fail(): Use in unreachable code blocks")
    void testFail_UnreachableCode() {
        try {
            calculator.divide(10, 0);
            fail("Expected ArithmeticException was not thrown!");
        } catch (ArithmeticException e) {
            // Expected!
            System.out.println("  ✓ Exception caught as expected");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // assertLinesMatch - Compare List of Strings with Patterns
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * assertLinesMatch - Compare list of strings with regex patterns
     * 
     * Supports:
     * - Exact match
     * - Regex patterns
     * - Fast-forward markers (>> skip >>)
     */
    @Test
    @Order(8)
    @DisplayName("8️⃣ assertLinesMatch: List of strings comparison")
    void testAssertLinesMatch() {
        var expected = java.util.List.of(
                "Hello",
                "\\d+", // Regex: any digits
                "World");

        var actual = java.util.List.of(
                "Hello",
                "12345", // Matches \d+
                "World");

        assertLinesMatch(expected, actual);
        System.out.println("  ✓ assertLinesMatch with regex patterns!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // @DisplayNameGeneration - Auto-generate display names
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("9️⃣ @DisplayNameGeneration examples")
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class DisplayNameGenerationTest {

        // Method name underscores become spaces!
        @Test
        void add_two_positive_numbers_returns_correct_sum() {
            assertEquals(5, calculator.add(2, 3));
            System.out.println("  ✓ Display name auto-generated from method name!");
        }

        @Test
        void multiply_by_zero_returns_zero() {
            assertEquals(0, calculator.multiply(100, 0));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // @ExtendWith - Custom Extensions (Hooks into JUnit lifecycle)
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * @ExtendWith - Register custom JUnit extensions
     * 
     *             Extensions can:
     *             - Add lifecycle callbacks
     *             - Provide parameter resolution
     *             - Handle exceptions
     *             - Modify test execution
     * 
     *             Common uses:
     *             - @ExtendWith(SpringExtension.class) → Spring testing
     *             - @ExtendWith(MockitoExtension.class) → Mockito
     */
    @Test
    @Order(10)
    @DisplayName("🔟 @ExtendWith: Understanding extensions")
    void testExtendWithExplanation() {
        System.out.println("""
                    @ExtendWith registers JUnit 5 extensions:

                    Example extensions:
                    • SpringExtension.class → Spring Boot testing
                    • MockitoExtension.class → Mockito mocking
                    • TimingExtension.class → Custom timing

                    In Spring Boot tests:
                    @SpringBootTest → already includes SpringExtension!
                """);
        assertTrue(true);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // FINAL SUMMARY
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(11)
    @DisplayName("📋 FINAL SUMMARY: 99% JUnit 5 Coverage!")
    void testFinalSummary() {
        System.out.println("""

                ╔══════════════════════════════════════════════════════════════════════════╗
                ║              🎯 JUNIT 5 - 99% COMPLETE COVERAGE! 🎯                     ║
                ╠══════════════════════════════════════════════════════════════════════════╣
                ║  ✅ LIFECYCLE: @BeforeAll, @AfterAll, @BeforeEach, @AfterEach           ║
                ║  ✅ ASSERTIONS: assertEquals, assertTrue, assertNull, assertThrows...   ║
                ║  ✅ ARRAYS: assertArrayEquals (not assertEquals!)                        ║
                ║  ✅ COLLECTIONS: assertIterableEquals, assertEquals (works!)             ║
                ║  ✅ EXCEPTIONS: assertThrows, assertThrowsExactly, assertDoesNotThrow   ║
                ║  ✅ PARAMETERIZED: @ValueSource, @CsvSource, @MethodSource, etc.        ║
                ║  ✅ ORGANIZATION: @Nested, @DisplayName, @Tag                           ║
                ║  ✅ REPEATED: @RepeatedTest, RepetitionInfo                             ║
                ║  ✅ DYNAMIC: @TestFactory, DynamicTest                                   ║
                ║  ✅ TIMEOUTS: @Timeout, assertTimeout, assertTimeoutPreemptively        ║
                ║  ✅ CONDITIONAL: @EnabledOnOs, @EnabledOnJre, @EnabledIf...             ║
                ║  ✅ GROUPED: assertAll (run ALL assertions!)                            ║
                ║  ✅ ASSUMPTIONS: assumeTrue, assumeFalse, assumingThat                  ║
                ║  ✅ INSTANCE: @TestInstance(PER_CLASS/PER_METHOD)                       ║
                ║  ✅ EXTENSIONS: @ExtendWith (SpringExtension, MockitoExtension)         ║
                ╠══════════════════════════════════════════════════════════════════════════╣
                ║  🚀 NEXT: Mockito (Mocking) + MockMVC (Spring Web Testing)              ║
                ╚══════════════════════════════════════════════════════════════════════════╝
                """);
        assertTrue(true);
    }
}
