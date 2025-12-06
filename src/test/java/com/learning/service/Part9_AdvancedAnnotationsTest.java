package com.learning.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ PART 9: ADVANCED ANNOTATIONS ║
 * ║ @TestFactory, @Tag, @Timeout, @TestInstance, Conditional Tests ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 * 
 * COVERED ANNOTATIONS:
 * ┌─────────────────────┬────────────────────────────────────────────────┐
 * │ @TestFactory │ Dynamic test generation at runtime │
 * │ @Tag │ Categorize tests for filtered execution │
 * │ @Timeout │ Fail test if takes too long │
 * │ @TestInstance │ Control test instance lifecycle │
 * │ @EnabledIf/OnOs/etc │ Conditional test execution │
 * └─────────────────────┴────────────────────────────────────────────────┘
 */
@DisplayName("📚 Part 9: Advanced Annotations")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Part9_AdvancedAnnotationsTest {

    private CalculatorService calculator;

    @BeforeEach
    void setUp() {
        calculator = new CalculatorService();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // @TestFactory - Dynamic Tests
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * @TestFactory - Generate tests DYNAMICALLY at runtime
     * 
     *              Unlike @ParameterizedTest which is declarative,
     * @TestFactory lets you create tests with full control in code!
     * 
     *              Must return: Stream, Collection, Iterable, or Iterator of
     *              DynamicTest
     */
    @Order(1)
    @TestFactory
    @DisplayName("1️⃣ @TestFactory: Dynamic addition tests")
    Stream<DynamicTest> testFactory_DynamicAdditionTests() {
        return Stream.of(
                new int[] { 1, 2, 3 },
                new int[] { 5, 5, 10 },
                new int[] { -3, 3, 0 },
                new int[] { 100, 200, 300 }).map(
                        data -> dynamicTest(
                                data[0] + " + " + data[1] + " = " + data[2],
                                () -> assertEquals(data[2], calculator.add(data[0], data[1]))));
    }

    /**
     * @TestFactory returning Collection
     */
    @Order(2)
    @TestFactory
    @DisplayName("2️⃣ @TestFactory: Dynamic prime tests (Collection)")
    Collection<DynamicTest> testFactory_PrimeTests() {
        return Arrays.asList(
                dynamicTest("2 is prime", () -> assertTrue(calculator.isPrime(2))),
                dynamicTest("3 is prime", () -> assertTrue(calculator.isPrime(3))),
                dynamicTest("4 is NOT prime", () -> assertFalse(calculator.isPrime(4))),
                dynamicTest("17 is prime", () -> assertTrue(calculator.isPrime(17))),
                dynamicTest("1 is NOT prime", () -> assertFalse(calculator.isPrime(1))));
    }

    /**
     * @TestFactory - More complex: test generation from data source
     */
    @Order(3)
    @TestFactory
    @DisplayName("3️⃣ @TestFactory: Grade tests from list")
    Stream<DynamicTest> testFactory_GradeTests() {
        record GradeTestCase(int score, String grade, String description) {
        }

        return Stream.of(
                new GradeTestCase(95, "A", "Excellent"),
                new GradeTestCase(85, "B", "Good"),
                new GradeTestCase(75, "C", "Average"),
                new GradeTestCase(65, "D", "Below average"),
                new GradeTestCase(50, "F", "Fail")).map(
                        tc -> dynamicTest(
                                "Score " + tc.score + " → " + tc.grade + " (" + tc.description + ")",
                                () -> {
                                    assertEquals(tc.grade, calculator.getGrade(tc.score));
                                    System.out.println("  ✓ " + tc.description + ": " + tc.score + " → " + tc.grade);
                                }));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // @Tag - Test Categorization
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * @Tag - Categorize tests for filtered execution
     * 
     *      Run only specific tags with Maven:
     *      mvn test -Dgroups="fast" → Run only @Tag("fast") tests
     *      mvn test -Dgroups="slow,integration" → Run slow OR integration
     *      mvn test -DexcludedGroups="slow" → Exclude slow tests
     */
    @Test
    @Order(4)
    @Tag("fast")
    @Tag("unit")
    @DisplayName("4️⃣ @Tag(\"fast\", \"unit\"): Quick addition test")
    void testTagFastUnit() {
        assertEquals(5, calculator.add(2, 3));
        System.out.println("✓ Fast unit test - addition");
    }

    @Test
    @Order(5)
    @Tag("slow")
    @DisplayName("5️⃣ @Tag(\"slow\"): Simulated slow test")
    void testTagSlow() throws InterruptedException {
        Thread.sleep(100); // Simulate slow operation
        assertEquals(120, calculator.factorial(5));
        System.out.println("✓ Slow test - factorial (took 100ms)");
    }

    @Test
    @Order(6)
    @Tag("integration")
    @DisplayName("6️⃣ @Tag(\"integration\"): Integration-style test")
    void testTagIntegration() {
        // Simulating integration test - tests multiple methods together
        int a = 10, b = 5;
        int sum = calculator.add(a, b);
        int diff = calculator.subtract(a, b);
        int product = calculator.multiply(a, b);

        assertEquals(15, sum);
        assertEquals(5, diff);
        assertEquals(50, product);
        System.out.println("✓ Integration test - multiple operations");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // @Timeout - Time Limit for Tests
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * @Timeout - Fail test if it exceeds time limit
     * 
     *          Great for:
     *          - Performance testing
     *          - Detecting infinite loops
     *          - Ensuring SLA compliance
     */
    @Test
    @Order(7)
    @Timeout(1) // 1 second max
    @DisplayName("7️⃣ @Timeout(1): Test must complete in 1 second")
    void testTimeout_Pass() {
        // This should complete well within 1 second
        for (int i = 0; i < 100; i++) {
            calculator.add(i, i);
        }
        System.out.println("✓ Completed 100 additions within 1 second");
    }

    /**
     * Timeout with TimeUnit
     */
    @Test
    @Order(8)
    @Timeout(value = 500, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
    @DisplayName("8️⃣ @Timeout(500ms): Quick operation test")
    void testTimeout_Milliseconds() {
        assertEquals(6, calculator.multiply(2, 3));
        System.out.println("✓ Completed within 500ms");
    }

    /**
     * assertTimeout - Programmatic timeout assertion
     */
    @Test
    @Order(9)
    @DisplayName("9️⃣ assertTimeout(): Inline timeout check")
    void testAssertTimeout() {
        // Test that operation completes within duration
        int result = assertTimeout(
                Duration.ofMillis(100),
                () -> {
                    return calculator.factorial(10);
                });
        assertEquals(3628800, result);
        System.out.println("✓ factorial(10) completed within 100ms");
    }

    /**
     * assertTimeoutPreemptively - Kills test if timeout exceeded
     * 
     * DIFFERENCE from assertTimeout:
     * - assertTimeout: waits for completion, then checks time
     * - assertTimeoutPreemptively: KILLS execution if time exceeded
     */
    @Test
    @Order(10)
    @DisplayName("🔟 assertTimeoutPreemptively(): Preemptive timeout")
    void testAssertTimeoutPreemptively() {
        String result = assertTimeoutPreemptively(
                Duration.ofMillis(100),
                () -> {
                    return calculator.processInput("hello");
                });
        assertEquals("HELLO", result);
        System.out.println("✓ processInput completed (would be killed if timeout)");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Conditional Test Execution
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(11)
    @EnabledOnOs(OS.WINDOWS)
    @DisplayName("1️⃣1️⃣ @EnabledOnOs(WINDOWS): Windows only")
    void testOnlyOnWindows() {
        assertTrue(System.getProperty("os.name").contains("Windows"));
        System.out.println("✓ Running on Windows");
    }

    @Test
    @Order(12)
    @DisabledOnOs(OS.MAC)
    @DisplayName("1️⃣2️⃣ @DisabledOnOs(MAC): Disabled on Mac")
    void testNotOnMac() {
        assertFalse(System.getProperty("os.name").contains("Mac"));
        System.out.println("✓ NOT running on Mac");
    }

    @Test
    @Order(13)
    @EnabledOnJre({ JRE.JAVA_17, JRE.JAVA_21 })
    @DisplayName("1️⃣3️⃣ @EnabledOnJre: Java 17 or 21 only")
    void testOnJava17Or21() {
        String version = System.getProperty("java.version");
        assertTrue(version.startsWith("17") || version.startsWith("21"));
        System.out.println("✓ Running on Java: " + version);
    }

    @Test
    @Order(14)
    @EnabledIfSystemProperty(named = "os.arch", matches = ".*64.*")
    @DisplayName("1️⃣4️⃣ @EnabledIfSystemProperty: 64-bit only")
    void testOnly64Bit() {
        assertTrue(System.getProperty("os.arch").contains("64"));
        System.out.println("✓ Running on 64-bit architecture");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SUMMARY
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(15)
    @DisplayName("📋 SUMMARY: Advanced Annotations")
    void testAdvancedSummary() {
        System.out.println("""

                ╔═══════════════════════════════════════════════════════════════════════╗
                ║                  ADVANCED ANNOTATIONS GUIDE                           ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  @TestFactory                                                         ║
                ║  • Generate tests dynamically at runtime                              ║
                ║  • Return Stream/Collection/Iterable of DynamicTest                  ║
                ║  • Use dynamicTest("name", () -> assertions)                         ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  @Tag("name")                                                         ║
                ║  • Categorize tests: "fast", "slow", "integration", etc.             ║
                ║  • Run specific: mvn test -Dgroups="fast"                            ║
                ║  • Exclude: mvn test -DexcludedGroups="slow"                         ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  @Timeout(value, unit)                                                ║
                ║  • Fail test if exceeds time limit                                   ║
                ║  • assertTimeout() - waits then checks                               ║
                ║  • assertTimeoutPreemptively() - kills on timeout                    ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  Conditional Execution:                                               ║
                ║  • @EnabledOnOs / @DisabledOnOs                                      ║
                ║  • @EnabledOnJre / @DisabledOnJre                                    ║
                ║  • @EnabledIfSystemProperty / @DisabledIfSystemProperty              ║
                ║  • @EnabledIfEnvironmentVariable                                     ║
                ║  • @EnabledIf("customConditionMethod")                               ║
                ╚═══════════════════════════════════════════════════════════════════════╝
                """);
        assertTrue(true);
    }
}
