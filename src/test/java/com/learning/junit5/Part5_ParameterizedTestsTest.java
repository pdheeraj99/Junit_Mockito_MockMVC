package com.learning.junit5;

import com.learning.service.CalculatorService;

import com.learning.model.DayOfWeek;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ PART 5: PARAMETERIZED TESTS ║
 * ║ Same test logic, different input values - DRY principle! ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 * 
 * WHY PARAMETERIZED TESTS?
 * ─────────────────────────
 * Instead of writing:
 * testIsPositive_1() { assertTrue(isPositive(1)); }
 * testIsPositive_5() { assertTrue(isPositive(5)); }
 * testIsPositive_10() { assertTrue(isPositive(10)); }
 * 
 * Write ONCE, run with MULTIPLE values:
 * 
 * @ParameterizedTest
 * @ValueSource(ints = {1, 5, 10})
 *                   void testIsPositive(int num) { assertTrue(isPositive(num));
 *                   }
 * 
 *                   ANNOTATIONS COVERED:
 *                   ┌─────────────────────┬────────────────────────────────────────────────┐
 *                   │ @ValueSource │ Simple values: ints, strings, doubles,
 *                   etc. │
 *                   │ @EnumSource │ All or specific enum values │
 *                   │ @CsvSource │ Comma-separated values (inline) │
 *                   │ @CsvFileSource │ CSV values from external file │
 *                   │ @MethodSource │ Values from a static method │
 *                   │ @ArgumentsSource │ Custom ArgumentsProvider class │
 *                   │ @NullSource │ Provides null │
 *                   │ @EmptySource │ Provides empty string/collection │
 *                   │ @NullAndEmptySource │ Both null and empty │
 *                   └─────────────────────┴────────────────────────────────────────────────┘
 */
@DisplayName("📚 Part 5: Parameterized Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Part5_ParameterizedTestsTest {

    private CalculatorService calculator;

    @BeforeEach
    void setUp() {
        calculator = new CalculatorService();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // @ValueSource - Simple Values
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * @ValueSource with ints
     *              Tests multiple int values with same logic
     */
    @Order(1)
    @ParameterizedTest(name = "#{index} - isPositive({0}) should be true")
    @ValueSource(ints = { 1, 5, 10, 100, 999 })
    @DisplayName("1️⃣ @ValueSource - ints: Test positive numbers")
    void testValueSource_Ints_Positive(int number) {
        assertTrue(calculator.isPositive(number),
                () -> number + " should be positive");
        System.out.println("  ✓ " + number + " is positive");
    }

    /**
     * @ValueSource with strings
     */
    @Order(2)
    @ParameterizedTest(name = "processInput(\"{0}\") should not be null")
    @ValueSource(strings = { "hello", "world", "JUnit5", "testing" })
    @DisplayName("2️⃣ @ValueSource - strings: Test non-null processing")
    void testValueSource_Strings(String input) {
        assertNotNull(calculator.processInput(input));
        assertEquals(input.toUpperCase(), calculator.processInput(input));
        System.out.println("  ✓ \"" + input + "\" → \"" + calculator.processInput(input) + "\"");
    }

    /**
     * @ValueSource with doubles
     */
    @Order(3)
    @ParameterizedTest(name = "factorial of valid small number")
    @ValueSource(doubles = { 0.0, 1.0, 2.0, 3.0, 4.0, 5.0 })
    @DisplayName("3️⃣ @ValueSource - doubles: Test factorial")
    void testValueSource_Doubles(double number) {
        int n = (int) number;
        assertDoesNotThrow(() -> calculator.factorial(n));
        System.out.println("  ✓ factorial(" + n + ") = " + calculator.factorial(n));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // @NullSource, @EmptySource, @NullAndEmptySource
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * @NullSource - Provides null as test input
     */
    @Order(4)
    @ParameterizedTest
    @NullSource
    @DisplayName("4️⃣ @NullSource: Test null input")
    void testNullSource(String input) {
        assertNull(input);
        assertNull(calculator.processInput(input));
        System.out.println("  ✓ null input returns null");
    }

    /**
     * @EmptySource - Provides empty string as test input
     */
    @Order(5)
    @ParameterizedTest
    @EmptySource
    @DisplayName("5️⃣ @EmptySource: Test empty string input")
    void testEmptySource(String input) {
        assertEquals("", input);
        assertNull(calculator.processInput(input)); // Empty string returns null
        System.out.println("  ✓ empty string input returns null");
    }

    /**
     * @NullAndEmptySource - Combines @NullSource and @EmptySource
     *                     Very useful for testing edge cases!
     */
    @Order(6)
    @ParameterizedTest(name = "processInput({0}) should return null")
    @NullAndEmptySource
    @DisplayName("6️⃣ @NullAndEmptySource: Test null AND empty")
    void testNullAndEmptySource(String input) {
        assertNull(calculator.processInput(input));
        System.out.println("  ✓ Input '" + input + "' returns null");
    }

    /**
     * Combine @NullAndEmptySource with @ValueSource!
     */
    @Order(7)
    @ParameterizedTest(name = "Input: \"{0}\"")
    @NullAndEmptySource
    @ValueSource(strings = { "  ", "\t", "\n" }) // Blank strings (whitespace only)
    @DisplayName("7️⃣ Combined: @NullAndEmptySource + @ValueSource")
    void testCombinedSources(String input) {
        // null, empty, and whitespace-only strings
        String result = calculator.processInput(input);
        // Our method returns null for null/empty, but whitespace becomes uppercase
        // whitespace
        if (input == null || input.isEmpty()) {
            assertNull(result);
        } else {
            assertNotNull(result);
        }
        System.out.println("  ✓ Tested input: "
                + (input == null ? "null" : "\"" + input.replace("\t", "\\t").replace("\n", "\\n") + "\""));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // @EnumSource - Enum Values
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * @EnumSource - Tests ALL enum values
     */
    @Order(8)
    @ParameterizedTest(name = "{0} is a valid day")
    @EnumSource(DayOfWeek.class)
    @DisplayName("8️⃣ @EnumSource: Test ALL enum values")
    void testEnumSource_AllValues(DayOfWeek day) {
        assertNotNull(day);
        System.out.println("  ✓ " + day + " (weekend: " + day.isWeekend() + ")");
    }

    /**
     * @EnumSource with names - Test ONLY specific enum values
     */
    @Order(9)
    @ParameterizedTest(name = "{0} should be a weekend")
    @EnumSource(value = DayOfWeek.class, names = { "SATURDAY", "SUNDAY" })
    @DisplayName("9️⃣ @EnumSource (names): Test ONLY weekends")
    void testEnumSource_OnlyWeekends(DayOfWeek day) {
        assertTrue(day.isWeekend());
        System.out.println("  ✓ " + day + " is a weekend!");
    }

    /**
     * @EnumSource with mode EXCLUDE - Test all EXCEPT specific values
     */
    @Order(10)
    @ParameterizedTest(name = "{0} should be a weekday")
    @EnumSource(value = DayOfWeek.class, mode = EnumSource.Mode.EXCLUDE, names = { "SATURDAY", "SUNDAY" })
    @DisplayName("🔟 @EnumSource (EXCLUDE): Test ONLY weekdays")
    void testEnumSource_ExcludeWeekends(DayOfWeek day) {
        assertFalse(day.isWeekend());
        assertTrue(day.isWeekday());
        System.out.println("  ✓ " + day + " is a weekday!");
    }

    /**
     * @EnumSource with mode MATCH_ALL - Regex pattern matching
     */
    @Order(11)
    @ParameterizedTest(name = "{0} starts with 'S'")
    @EnumSource(value = DayOfWeek.class, mode = EnumSource.Mode.MATCH_ALL, names = "^S.*")
    @DisplayName("1️⃣1️⃣ @EnumSource (MATCH_ALL regex): Days starting with 'S'")
    void testEnumSource_Regex(DayOfWeek day) {
        assertTrue(day.name().startsWith("S"));
        System.out.println("  ✓ " + day + " starts with 'S'");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // @CsvSource - Comma-Separated Values (Inline)
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * @CsvSource - Multiple parameters per test
     *            Format: "param1, param2, param3, ..."
     */
    @Order(12)
    @ParameterizedTest(name = "{0} + {1} = {2}")
    @CsvSource({
            "1, 2, 3",
            "5, 5, 10",
            "10, -5, 5",
            "-3, -7, -10",
            "0, 0, 0"
    })
    @DisplayName("1️⃣2️⃣ @CsvSource: Test addition with expected results")
    void testCsvSource_Addition(int a, int b, int expected) {
        assertEquals(expected, calculator.add(a, b));
        System.out.println("  ✓ " + a + " + " + b + " = " + expected);
    }

    /**
     * @CsvSource with strings
     */
    @Order(13)
    @ParameterizedTest(name = "Score {0} = Grade {1}")
    @CsvSource({
            "95, A",
            "85, B",
            "75, C",
            "65, D",
            "55, F",
            "0, F",
            "100, A"
    })
    @DisplayName("1️⃣3️⃣ @CsvSource: Test grade calculation")
    void testCsvSource_Grades(int score, String expectedGrade) {
        assertEquals(expectedGrade, calculator.getGrade(score));
        System.out.println("  ✓ Score " + score + " → Grade " + expectedGrade);
    }

    /**
     * @CsvSource with custom delimiter
     */
    @Order(14)
    @ParameterizedTest(name = "{0} * {1} = {2}")
    @CsvSource(value = {
            "2 | 3 | 6",
            "5 | 5 | 25",
            "10 | 0 | 0",
            "-2 | 3 | -6"
    }, delimiter = '|')
    @DisplayName("1️⃣4️⃣ @CsvSource with custom delimiter (|)")
    void testCsvSource_CustomDelimiter(int a, int b, int expected) {
        assertEquals(expected, calculator.multiply(a, b));
        System.out.println("  ✓ " + a + " * " + b + " = " + expected);
    }

    /**
     * @CsvSource with nullValues and emptyValue
     */
    @Order(15)
    @ParameterizedTest(name = "processInput(\"{0}\") = {1}")
    @CsvSource(value = {
            "hello, HELLO",
            "NULL, null", // NULL string becomes null
            "'', null" // Empty string becomes null
    }, nullValues = "NULL")
    @DisplayName("1️⃣5️⃣ @CsvSource with NULL handling")
    void testCsvSource_NullHandling(String input, String expected) {
        String result = calculator.processInput(input);
        if ("null".equals(expected)) {
            assertNull(result);
        } else {
            assertEquals(expected, result);
        }
        System.out.println("  ✓ \"" + input + "\" → " + (result == null ? "null" : "\"" + result + "\""));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // @MethodSource - Arguments from Method
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * @MethodSource - Simple: Method returns Stream<Integer>
     */
    @Order(16)
    @ParameterizedTest(name = "{0} is even")
    @MethodSource("provideEvenNumbers")
    @DisplayName("1️⃣6️⃣ @MethodSource: Simple - Stream of even numbers")
    void testMethodSource_Simple(int number) {
        assertTrue(calculator.isEven(number));
        System.out.println("  ✓ " + number + " is even");
    }

    // Method that provides test data - MUST be static!
    static Stream<Integer> provideEvenNumbers() {
        return Stream.of(2, 4, 6, 8, 10, 100, 1000);
    }

    /**
     * @MethodSource - With Arguments (multiple parameters)
     */
    @Order(17)
    @ParameterizedTest(name = "sumOfDigits({0}) = {1}")
    @MethodSource("provideSumOfDigitsTestData")
    @DisplayName("1️⃣7️⃣ @MethodSource: Multiple params - Sum of digits")
    void testMethodSource_MultipleParams(int number, int expectedSum) {
        assertEquals(expectedSum, calculator.sumOfDigits(number));
        System.out.println("  ✓ sumOfDigits(" + number + ") = " + expectedSum);
    }

    static Stream<Arguments> provideSumOfDigitsTestData() {
        return Stream.of(
                Arguments.of(123, 6), // 1+2+3 = 6
                Arguments.of(999, 27), // 9+9+9 = 27
                Arguments.of(100, 1), // 1+0+0 = 1
                Arguments.of(5, 5), // Just 5
                Arguments.of(0, 0) // Zero
        );
    }

    /**
     * @MethodSource - Complex objects as arguments
     */
    @Order(18)
    @ParameterizedTest(name = "Prime check: {0}")
    @MethodSource("providePrimeTestData")
    @DisplayName("1️⃣8️⃣ @MethodSource: Complex - Prime number testing")
    void testMethodSource_Complex(int number, boolean expectedPrime, String description) {
        assertEquals(expectedPrime, calculator.isPrime(number), description);
        System.out.println("  ✓ " + number + ": " + description);
    }

    static Stream<Arguments> providePrimeTestData() {
        return Stream.of(
                Arguments.of(2, true, "2 is the smallest prime"),
                Arguments.of(3, true, "3 is prime"),
                Arguments.of(4, false, "4 is NOT prime (2*2)"),
                Arguments.of(17, true, "17 is prime"),
                Arguments.of(1, false, "1 is NOT prime"),
                Arguments.of(0, false, "0 is NOT prime"),
                Arguments.of(-5, false, "Negative numbers are NOT prime"));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // @ArgumentsSource - Custom Provider Class
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * @ArgumentsSource - Custom ArgumentsProvider for reusable test data
     *                  More flexible than @MethodSource - can be reused across test
     *                  classes!
     */
    @Order(19)
    @ParameterizedTest(name = "Factorial of {0} = {1}")
    @ArgumentsSource(FactorialArgumentsProvider.class)
    @DisplayName("1️⃣9️⃣ @ArgumentsSource: Custom provider class")
    void testArgumentsSource(int n, int expectedFactorial) {
        assertEquals(expectedFactorial, calculator.factorial(n));
        System.out.println("  ✓ factorial(" + n + ") = " + expectedFactorial);
    }

    // Custom ArgumentsProvider - can be in separate file for reuse!
    static class FactorialArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(0, 1), // 0! = 1
                    Arguments.of(1, 1), // 1! = 1
                    Arguments.of(2, 2), // 2! = 2
                    Arguments.of(3, 6), // 3! = 6
                    Arguments.of(4, 24), // 4! = 24
                    Arguments.of(5, 120) // 5! = 120
            );
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SUMMARY
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(20)
    @DisplayName("📋 SUMMARY: Parameterized Test Sources")
    void testSummary() {
        System.out.println("""

                ╔═══════════════════════════════════════════════════════════════════════╗
                ║                PARAMETERIZED TEST SOURCES GUIDE                       ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  @ValueSource     │ Simple: ints, strings, doubles, longs, etc.      ║
                ║                   │ Use for: single parameter tests                   ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  @NullSource      │ Provides: null                                    ║
                ║  @EmptySource     │ Provides: "" (empty string) or empty collection   ║
                ║  @NullAndEmptySource│ Combines both - great for edge case testing!   ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  @EnumSource      │ All enum values or filtered by names/regex        ║
                ║                   │ modes: INCLUDE, EXCLUDE, MATCH_ALL, MATCH_ANY     ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  @CsvSource       │ Inline CSV: "param1, param2, expected"            ║
                ║                   │ Great for: input-output testing                   ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  @CsvFileSource   │ CSV from file (we'll cover in next example)       ║
                ║                   │ Great for: large test data sets                   ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  @MethodSource    │ Static method returning Stream<Arguments>         ║
                ║                   │ Great for: complex/computed test data             ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  @ArgumentsSource │ Custom ArgumentsProvider class                    ║
                ║                   │ Great for: reusable test data across classes      ║
                ╚═══════════════════════════════════════════════════════════════════════╝
                """);
        assertTrue(true);
    }
}
