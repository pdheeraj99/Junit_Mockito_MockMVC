package com.learning.junit5;

import com.learning.service.CalculatorService;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ PART 3: DISPLAY NAME, DISABLED TESTS, AND TEST ORDERING ║
 * ║ Learn how to organize and control your tests! ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 * 
 * KEY ANNOTATIONS:
 * - @DisplayName → Custom human-readable test names (supports emojis!)
 * - @Disabled → Skip/disable a test temporarily
 * - @TestMethodOrder → Control the order tests run
 * - @Order → Specify order for each test
 */
@DisplayName("📚 Part 3: Display Names & Test Organization")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Enable ordering
class Part3_DisplayNameAndDisabledTest {

    private CalculatorService calculator;

    @BeforeEach
    void setUp() {
        calculator = new CalculatorService();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // @DisplayName Examples
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(1)
    @DisplayName("🧮 Addition: 2 + 3 should equal 5")
    void testAddition() {
        assertEquals(5, calculator.add(2, 3));
        System.out.println("✅ Test with emoji DisplayName passed!");
    }

    @Test
    @Order(2)
    @DisplayName("When multiplying two positive numbers, result should be positive")
    void testMultiplication() {
        assertTrue(calculator.multiply(2, 3) > 0);
        System.out.println("✅ Test with descriptive DisplayName passed!");
    }

    @Test
    @Order(3)
    @DisplayName("➖ Subtraction Test: Testing negative results")
    void testSubtraction() {
        assertEquals(-5, calculator.subtract(5, 10));
        System.out.println("✅ Subtraction test passed!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // @Disabled Examples
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * @Disabled - This test will be SKIPPED when running tests
     *           ✅ Use when: Test is broken, feature not implemented, or temporarily
     *           excluded
     */
    @Test
    @Order(4)
    @Disabled("Feature not yet implemented - TODO in Sprint 5")
    @DisplayName("🚧 Future feature test (DISABLED)")
    void testFutureFeature() {
        // This won't run!
        fail("This test should not run because it's disabled");
    }

    @Test
    @Order(5)
    @Disabled("Known bug #1234 - waiting for fix")
    @DisplayName("🐛 Test with known bug (DISABLED)")
    void testWithKnownBug() {
        // Disabled due to bug
        fail("This test should not run");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Conditional Test Execution
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(6)
    @EnabledOnOs(OS.WINDOWS)
    @DisplayName("🪟 Windows-only test")
    void testWindowsOnly() {
        System.out.println("✅ This test runs only on Windows!");
        assertTrue(true);
    }

    @Test
    @Order(7)
    @EnabledOnOs(OS.LINUX)
    @DisplayName("🐧 Linux-only test")
    void testLinuxOnly() {
        System.out.println("✅ This test runs only on Linux!");
        assertTrue(true);
    }

    @Test
    @Order(8)
    @EnabledOnOs(OS.MAC)
    @DisplayName("🍎 Mac-only test")
    void testMacOnly() {
        System.out.println("✅ This test runs only on Mac!");
        assertTrue(true);
    }

    @Test
    @Order(9)
    @EnabledOnJre(JRE.JAVA_17)
    @DisplayName("☕ Java 17 specific test")
    void testJava17Only() {
        System.out.println("✅ This test runs only on Java 17!");
        assertTrue(true);
    }

    @Test
    @Order(10)
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    @DisplayName("🔄 CI environment only test")
    void testCIEnvironmentOnly() {
        System.out.println("This test runs only in CI environment");
        assertTrue(true);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // @TestMethodOrder Demonstration
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * The @Order annotations on tests above demonstrate ordering!
     * Tests run in Order(1), Order(2), Order(3)... sequence
     * 
     * Other MethodOrderer options:
     * - MethodOrderer.MethodName.class → Alphabetical by method name
     * - MethodOrderer.DisplayName.class → Alphabetical by display name
     * - MethodOrderer.Random.class → Random order (good for finding test
     * dependencies!)
     */
    @Test
    @Order(100) // This runs LAST because it has highest order number
    @DisplayName("🏁 Final test (Order 100 - runs last)")
    void testRunsLast() {
        System.out.println("✅ This test runs last due to @Order(100)!");
        assertTrue(true);
    }
}
