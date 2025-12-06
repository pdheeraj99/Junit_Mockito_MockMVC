package com.learning.service;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ PART 8: @RepeatedTest - Run Same Test Multiple Times ║
 * ║ Useful for: random data testing, flaky test detection, performance testing
 * ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 * 
 * WHY @RepeatedTest?
 * ──────────────────
 * - Test behavior consistency across multiple runs
 * - Detect flaky tests (tests that sometimes fail)
 * - Performance/stress testing
 * - Testing with random data
 * 
 * SYNTAX:
 * ───────
 * @RepeatedTest(5) → Run 5 times
 * 
 * @RepeatedTest(value = 5, name = "...") → Custom display name
 * 
 *                     DISPLAY NAME PLACEHOLDERS:
 *                     ──────────────────────────
 *                     {displayName} → Test method display name
 *                     {currentRepetition}→ Current repetition number (1, 2,
 *                     3...)
 *                     {totalRepetitions} → Total number of repetitions
 */
@DisplayName("📚 Part 8: @RepeatedTest - Multiple Executions")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Part8_RepeatedTestsTest {

    private CalculatorService calculator;

    @BeforeEach
    void setUp() {
        calculator = new CalculatorService();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Basic @RepeatedTest
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Basic @RepeatedTest - runs 5 times
     */
    @Order(1)
    @RepeatedTest(5)
    @DisplayName("1️⃣ Basic: Run addition test 5 times")
    void testRepeatedBasic() {
        assertEquals(5, calculator.add(2, 3));
        System.out.println("  ✓ Addition test passed");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // @RepeatedTest with Custom Name
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * @RepeatedTest with custom display name using placeholders
     */
    @Order(2)
    @RepeatedTest(value = 3, name = "Repetition {currentRepetition} of {totalRepetitions}")
    @DisplayName("2️⃣ Custom name: Multiplication test")
    void testRepeatedWithCustomName() {
        assertEquals(20, calculator.multiply(4, 5));
        System.out.println("  ✓ 4 * 5 = 20");
    }

    /**
     * Another custom name format
     */
    @Order(3)
    @RepeatedTest(value = 3, name = "{displayName} → Run #{currentRepetition}")
    @DisplayName("3️⃣ isPositive check")
    void testRepeatedWithDisplayName() {
        assertTrue(calculator.isPositive(100));
        System.out.println("  ✓ 100 is positive");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // @RepeatedTest with RepetitionInfo
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * RepetitionInfo - Access repetition details in your test
     * Use this to vary behavior based on which repetition you're on
     */
    @Order(4)
    @RepeatedTest(value = 5, name = "Testing number {currentRepetition}")
    @DisplayName("4️⃣ Using RepetitionInfo to access repetition details")
    void testWithRepetitionInfo(RepetitionInfo repetitionInfo) {
        int currentRep = repetitionInfo.getCurrentRepetition();
        int totalReps = repetitionInfo.getTotalRepetitions();

        // Use current repetition as test data
        int testNumber = currentRep * 10; // 10, 20, 30, 40, 50

        assertTrue(calculator.isPositive(testNumber),
                "Repetition " + currentRep + ": " + testNumber + " should be positive");

        System.out.println("  ✓ Rep " + currentRep + "/" + totalReps +
                ": Testing number " + testNumber);
    }

    /**
     * RepetitionInfo for different test scenarios per repetition
     */
    @Order(5)
    @RepeatedTest(value = 4, name = "Scenario {currentRepetition}")
    @DisplayName("5️⃣ Different scenarios per repetition")
    void testDifferentScenariosPerRepetition(RepetitionInfo info) {
        int rep = info.getCurrentRepetition();

        switch (rep) {
            case 1 -> {
                assertEquals(3, calculator.add(1, 2));
                System.out.println("  ✓ Scenario 1: Basic addition (1+2=3)");
            }
            case 2 -> {
                assertEquals(0, calculator.add(-5, 5));
                System.out.println("  ✓ Scenario 2: Add to zero (-5+5=0)");
            }
            case 3 -> {
                assertEquals(-10, calculator.add(-3, -7));
                System.out.println("  ✓ Scenario 3: Negative addition (-3+-7=-10)");
            }
            case 4 -> {
                assertEquals(1000000, calculator.add(500000, 500000));
                System.out.println("  ✓ Scenario 4: Large numbers");
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // @RepeatedTest for Consistency Testing
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Testing consistency - same input should always give same output
     */
    @Order(6)
    @RepeatedTest(value = 10, name = "Consistency check #{currentRepetition}")
    @DisplayName("6️⃣ Consistency: factorial(5) should ALWAYS be 120")
    void testFactorialConsistency() {
        // Run 10 times to ensure consistent behavior
        assertEquals(120, calculator.factorial(5));
    }

    /**
     * Testing with random data - verify constraints hold
     */
    @Order(7)
    @RepeatedTest(value = 10, name = "Random test #{currentRepetition}")
    @DisplayName("7️⃣ Random data: isEven should work for random even numbers")
    void testRandomEvenNumbers(RepetitionInfo info) {
        // Generate random even number between 0 and 1000
        int randomEven = (int) (Math.random() * 500) * 2; // Always even

        assertTrue(calculator.isEven(randomEven),
                randomEven + " should be even");

        System.out.println("  ✓ Rep " + info.getCurrentRepetition() +
                ": " + randomEven + " is even");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SUMMARY
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(8)
    @DisplayName("📋 SUMMARY: @RepeatedTest Guide")
    void testRepeatedSummary() {
        System.out.println("""

                ╔═══════════════════════════════════════════════════════════════════════╗
                ║                    @RepeatedTest GUIDE                                ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  SYNTAX:                                                              ║
                ║  @RepeatedTest(5)                        → Run 5 times               ║
                ║  @RepeatedTest(value = 5, name = "...")  → Custom display name       ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  PLACEHOLDERS for name:                                               ║
                ║  {displayName}       → @DisplayName value                            ║
                ║  {currentRepetition} → 1, 2, 3...                                    ║
                ║  {totalRepetitions}  → Total count                                   ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  RepetitionInfo parameter:                                            ║
                ║  • getCurrentRepetition() → Current run number                       ║
                ║  • getTotalRepetitions()  → Total runs                               ║
                ║  • Use to vary test data per repetition                              ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  USE CASES:                                                           ║
                ║  • Flaky test detection (run many times, should always pass)         ║
                ║  • Random data testing (different data each run)                     ║
                ║  • Consistency verification                                           ║
                ║  • Simple stress/performance testing                                 ║
                ╚═══════════════════════════════════════════════════════════════════════╝
                """);
        assertTrue(true);
    }
}
