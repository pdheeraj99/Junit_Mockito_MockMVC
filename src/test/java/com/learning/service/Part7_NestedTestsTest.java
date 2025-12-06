package com.learning.service;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ PART 7: @Nested Tests - Hierarchical Test Organization ║
 * ║ Group related tests together for better organization and readability! ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 * 
 * WHY @Nested?
 * ────────────
 * - Organize tests by behavior/scenario
 * - Share setup within groups
 * - Better test report readability
 * - BDD-style test organization (Given-When-Then)
 * 
 * STRUCTURE:
 * ──────────
 * OuterClass
 * └── @Nested InnerClass1 (group of related tests)
 * ├── setUp for this group
 * ├── test1
 * └── test2
 * └── @Nested InnerClass2 (another group)
 * ├── setUp for this group
 * └── @Nested DeeperNested (can nest further!)
 */
@DisplayName("📚 Part 7: @Nested Tests - Hierarchical Organization")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Part7_NestedTestsTest {

    private CalculatorService calculator;

    @BeforeEach
    void setUp() {
        System.out.println("🔷 OUTER BeforeEach: Creating calculator");
        calculator = new CalculatorService();
    }

    @AfterEach
    void tearDown() {
        System.out.println("🔷 OUTER AfterEach: Cleanup");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // NESTED: Addition Tests
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("➕ When testing Addition")
    class AdditionTests {

        @BeforeEach
        void setUpAddition() {
            System.out.println("  🔹 NESTED (Addition) BeforeEach");
        }

        @Test
        @DisplayName("Two positive numbers")
        void testAddPositiveNumbers() {
            assertEquals(5, calculator.add(2, 3));
            System.out.println("    ✓ 2 + 3 = 5");
        }

        @Test
        @DisplayName("Positive and negative number")
        void testAddMixedNumbers() {
            assertEquals(2, calculator.add(5, -3));
            System.out.println("    ✓ 5 + (-3) = 2");
        }

        @Test
        @DisplayName("Two negative numbers")
        void testAddNegativeNumbers() {
            assertEquals(-8, calculator.add(-3, -5));
            System.out.println("    ✓ (-3) + (-5) = -8");
        }

        @Test
        @DisplayName("Adding zero")
        void testAddZero() {
            assertEquals(5, calculator.add(5, 0));
            assertEquals(5, calculator.add(0, 5));
            System.out.println("    ✓ Adding zero doesn't change value");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // NESTED: Division Tests (with edge cases)
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("➗ When testing Division")
    class DivisionTests {

        @BeforeEach
        void setUpDivision() {
            System.out.println("  🔹 NESTED (Division) BeforeEach");
        }

        @Nested
        @DisplayName("✅ With valid inputs")
        class ValidDivision {

            @Test
            @DisplayName("Divide two positive numbers")
            void testDividePositive() {
                assertEquals(5.0, calculator.divide(10, 2));
                System.out.println("      ✓ 10 / 2 = 5.0");
            }

            @Test
            @DisplayName("Divide with decimal result")
            void testDivideDecimal() {
                assertEquals(2.5, calculator.divide(5, 2));
                System.out.println("      ✓ 5 / 2 = 2.5");
            }

            @Test
            @DisplayName("Divide zero by non-zero")
            void testDivideZero() {
                assertEquals(0.0, calculator.divide(0, 5));
                System.out.println("      ✓ 0 / 5 = 0.0");
            }
        }

        @Nested
        @DisplayName("❌ With invalid inputs (exceptions)")
        class InvalidDivision {

            @Test
            @DisplayName("Divide by zero throws ArithmeticException")
            void testDivideByZero() {
                ArithmeticException exception = assertThrows(
                        ArithmeticException.class,
                        () -> calculator.divide(10, 0));
                assertEquals("Cannot divide by zero!", exception.getMessage());
                System.out.println("      ✓ Division by zero throws exception");
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // NESTED: Factorial Tests (BDD Style)
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("🔢 Factorial - BDD Style")
    class FactorialBDDTests {

        @Nested
        @DisplayName("Given valid input")
        class GivenValidInput {

            @Nested
            @DisplayName("When input is zero")
            class WhenInputIsZero {

                @Test
                @DisplayName("Then return 1 (0! = 1)")
                void thenReturnOne() {
                    assertEquals(1, calculator.factorial(0));
                    System.out.println("        ✓ 0! = 1");
                }
            }

            @Nested
            @DisplayName("When input is positive")
            class WhenInputIsPositive {

                @Test
                @DisplayName("Then return correct factorial")
                void thenReturnFactorial() {
                    assertEquals(1, calculator.factorial(1));
                    assertEquals(2, calculator.factorial(2));
                    assertEquals(6, calculator.factorial(3));
                    assertEquals(24, calculator.factorial(4));
                    assertEquals(120, calculator.factorial(5));
                    System.out.println("        ✓ Factorials: 1!, 2!, 3!, 4!, 5! verified");
                }
            }
        }

        @Nested
        @DisplayName("Given invalid input")
        class GivenInvalidInput {

            @Nested
            @DisplayName("When input is negative")
            class WhenInputIsNegative {

                @Test
                @DisplayName("Then throw IllegalArgumentException")
                void thenThrowException() {
                    assertThrows(IllegalArgumentException.class,
                            () -> calculator.factorial(-1));
                    assertThrows(IllegalArgumentException.class,
                            () -> calculator.factorial(-100));
                    System.out.println("        ✓ Negative inputs throw IllegalArgumentException");
                }
            }

            @Nested
            @DisplayName("When input is too large (> 20)")
            class WhenInputIsTooLarge {

                @Test
                @DisplayName("Then throw ArithmeticException (overflow)")
                void thenThrowOverflowException() {
                    assertThrows(ArithmeticException.class,
                            () -> calculator.factorial(21));
                    assertThrows(ArithmeticException.class,
                            () -> calculator.factorial(100));
                    System.out.println("        ✓ Large inputs throw ArithmeticException");
                }
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SUMMARY TEST
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("📋 SUMMARY: @Nested Test Benefits")
    void testNestedSummary() {
        System.out.println("""

                ╔═══════════════════════════════════════════════════════════════════════╗
                ║                    @Nested TESTS GUIDE                                ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  PURPOSE:                                                             ║
                ║  • Group related tests together                                       ║
                ║  • Share @BeforeEach within groups                                    ║
                ║  • Hierarchical organization (can nest multiple levels)              ║
                ║  • BDD-style: Given → When → Then                                    ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  LIFECYCLE ORDER:                                                     ║
                ║  1. Outer @BeforeAll (if static)                                     ║
                ║  2. Outer @BeforeEach                                                ║
                ║  3. Nested @BeforeEach                                               ║
                ║  4. Test method                                                       ║
                ║  5. Nested @AfterEach                                                ║
                ║  6. Outer @AfterEach                                                 ║
                ║  7. Outer @AfterAll (if static)                                      ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  RULES:                                                               ║
                ║  • @Nested classes must be NON-STATIC inner classes                  ║
                ║  • @BeforeAll/@AfterAll in nested need @TestInstance(PER_CLASS)      ║
                ║  • Each level inherits parent's @BeforeEach                          ║
                ╚═══════════════════════════════════════════════════════════════════════╝
                """);
        assertTrue(true);
    }
}
