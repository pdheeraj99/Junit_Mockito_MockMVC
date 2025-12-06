package com.learning.service;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ PART 1: LIFECYCLE ANNOTATIONS & BASIC ASSERTIONS ║
 * ║ This is your STARTER TEST CLASS - Run this first! ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 * 
 * LIFECYCLE ANNOTATIONS ORDER:
 * 1. @BeforeAll → Runs ONCE before ALL tests (must be static)
 * 2. @BeforeEach → Runs BEFORE EACH test method
 * 3. @Test → The actual test method
 * 4. @AfterEach → Runs AFTER EACH test method
 * 5. @AfterAll → Runs ONCE after ALL tests (must be static)
 * 
 * RUN THIS TEST TO SEE THE ORDER IN CONSOLE!
 */
@DisplayName("📚 Part 1: Lifecycle & Basic Assertions")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Part1_LifecycleAndBasicAssertionsTest {

    private CalculatorService calculator;
    private static int testCounter = 0;

    // ═══════════════════════════════════════════════════════════════════════════
    // LIFECYCLE ANNOTATIONS
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * @BeforeAll - Runs ONCE before any test in this class
     *            ✅ Must be STATIC
     *            ✅ Use for: expensive setup like database connections, file loading
     */
    @BeforeAll
    static void setUpBeforeAll() {
        System.out.println("\n╔═══════════════════════════════════════════════════════╗");
        System.out.println("║ 🚀 @BeforeAll: Starting test class execution...       ║");
        System.out.println("║    This runs ONCE before ALL tests!                   ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝\n");
    }

    /**
     * @BeforeEach - Runs before EACH test method
     *             ✅ Use for: creating fresh objects, resetting state
     */
    @BeforeEach
    void setUp() {
        testCounter++;
        System.out.println("  ▶ @BeforeEach: Setting up for test #" + testCounter);
        calculator = new CalculatorService(); // Fresh instance for each test
    }

    /**
     * @AfterEach - Runs after EACH test method
     *            ✅ Use for: cleanup, closing resources
     */
    @AfterEach
    void tearDown() {
        System.out.println("  ◀ @AfterEach: Cleaning up after test #" + testCounter + "\n");
        calculator = null;
    }

    /**
     * @AfterAll - Runs ONCE after all tests complete
     *           ✅ Must be STATIC
     *           ✅ Use for: final cleanup, reporting
     */
    @AfterAll
    static void tearDownAfterAll() {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║ 🏁 @AfterAll: All tests completed!                    ║");
        System.out.println("║    Total tests run: " + testCounter + "                              ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝\n");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // BASIC ASSERTIONS
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * assertEquals - Checks if two values are EQUAL
     */
    @Test
    @Order(1)
    @DisplayName("1️⃣ assertEquals: Testing addition")
    void testAssertEquals_Addition() {
        System.out.println("    📋 Running assertEquals test...");

        // Basic assertEquals
        assertEquals(5, calculator.add(2, 3));
        assertEquals(0, calculator.add(-5, 5));
        assertEquals(-10, calculator.add(-5, -5));

        // assertEquals with custom message (shown if test fails)
        assertEquals(100, calculator.add(50, 50), "50 + 50 should equal 100");

        System.out.println("    ✅ All assertEquals passed!");
    }

    /**
     * assertNotEquals - Checks if two values are NOT EQUAL
     */
    @Test
    @Order(2)
    @DisplayName("2️⃣ assertNotEquals: Testing subtraction")
    void testAssertNotEquals_Subtraction() {
        System.out.println("    📋 Running assertNotEquals test...");

        assertNotEquals(0, calculator.subtract(10, 5));
        assertNotEquals(10, calculator.subtract(10, 5));

        // With custom message
        assertNotEquals(100, calculator.subtract(50, 50),
                "50 - 50 should NOT equal 100");

        System.out.println("    ✅ All assertNotEquals passed!");
    }

    /**
     * assertTrue - Checks if condition is TRUE
     */
    @Test
    @Order(3)
    @DisplayName("3️⃣ assertTrue: Testing positive numbers")
    void testAssertTrue_IsPositive() {
        System.out.println("    📋 Running assertTrue test...");

        assertTrue(calculator.isPositive(1));
        assertTrue(calculator.isPositive(100));
        assertTrue(calculator.isPositive(999999));

        // With custom message
        assertTrue(calculator.isPositive(42),
                "42 should be a positive number");

        System.out.println("    ✅ All assertTrue passed!");
    }

    /**
     * assertFalse - Checks if condition is FALSE
     */
    @Test
    @Order(4)
    @DisplayName("4️⃣ assertFalse: Testing non-positive numbers")
    void testAssertFalse_IsNotPositive() {
        System.out.println("    📋 Running assertFalse test...");

        assertFalse(calculator.isPositive(0));
        assertFalse(calculator.isPositive(-1));
        assertFalse(calculator.isPositive(-100));

        // With custom message
        assertFalse(calculator.isPositive(-42),
                "-42 should NOT be positive");

        System.out.println("    ✅ All assertFalse passed!");
    }

    /**
     * assertNull - Checks if value is NULL
     */
    @Test
    @Order(5)
    @DisplayName("5️⃣ assertNull: Testing null responses")
    void testAssertNull() {
        System.out.println("    📋 Running assertNull test...");

        assertNull(calculator.processInput(null));
        assertNull(calculator.processInput(""));

        // With custom message
        assertNull(calculator.processInput(null),
                "Processing null should return null");

        System.out.println("    ✅ All assertNull passed!");
    }

    /**
     * assertNotNull - Checks if value is NOT NULL
     */
    @Test
    @Order(6)
    @DisplayName("6️⃣ assertNotNull: Testing non-null responses")
    void testAssertNotNull() {
        System.out.println("    📋 Running assertNotNull test...");

        assertNotNull(calculator.processInput("hello"));
        assertNotNull(calculator.processInput("test"));

        // With custom message
        assertNotNull(calculator.processInput("world"),
                "Processing 'world' should NOT return null");

        System.out.println("    ✅ All assertNotNull passed!");
    }

    /**
     * assertArrayEquals - Checks if two arrays are EQUAL
     */
    @Test
    @Order(7)
    @DisplayName("7️⃣ assertArrayEquals: Testing array results")
    void testAssertArrayEquals() {
        System.out.println("    📋 Running assertArrayEquals test...");

        int[] expected = { 1, 4, 9, 16 };
        int[] actual = calculator.getSquares(1, 2, 3, 4);

        assertArrayEquals(expected, actual);

        // Another example
        assertArrayEquals(new int[] { 25, 36, 49 },
                calculator.getSquares(5, 6, 7),
                "Squares of 5, 6, 7 should be 25, 36, 49");

        System.out.println("    ✅ All assertArrayEquals passed!");
    }

    /**
     * assertIterableEquals - Checks if two Iterables are EQUAL
     */
    @Test
    @Order(8)
    @DisplayName("8️⃣ assertIterableEquals: Testing list results")
    void testAssertIterableEquals() {
        System.out.println("    📋 Running assertIterableEquals test...");

        var expected = java.util.List.of(5, 10, 15, 20);
        var actual = calculator.getMultiples(5, 4);

        assertIterableEquals(expected, actual);

        // Another example: multiples of 3
        assertIterableEquals(
                java.util.List.of(3, 6, 9),
                calculator.getMultiples(3, 3),
                "First 3 multiples of 3 should be [3, 6, 9]");

        System.out.println("    ✅ All assertIterableEquals passed!");
    }
}
