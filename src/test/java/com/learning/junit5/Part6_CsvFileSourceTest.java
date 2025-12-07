package com.learning.junit5;

import com.learning.service.CalculatorService;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ PART 6: @CsvFileSource - External CSV Files ║
 * ║ Great for large datasets that you don't want in your test code! ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 * 
 * WHY @CsvFileSource?
 * ───────────────────
 * - Keep test data separate from test logic
 * - Easy to add more test cases without changing code
 * - Non-developers can add test data to CSV files
 * - Reuse same CSV across multiple tests
 * 
 * CSV FILE PATH:
 * ──────────────
 * Files should be in: src/test/resources/
 * Reference with: resources = "/test-data/grades.csv"
 */
@DisplayName("📚 Part 6: @CsvFileSource - External File Testing")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Part6_CsvFileSourceTest {

    private CalculatorService calculator;

    @BeforeEach
    void setUp() {
        calculator = new CalculatorService();
    }

    /**
     * @CsvFileSource - Read test data from external CSV file
     * 
     *                CSV file format (grades.csv):
     *                score,expectedGrade,description
     *                95,A,Excellent score
     *                85,B,Good B grade
     *                ...
     */
    @Order(1)
    @ParameterizedTest(name = "Score {0} → Grade {1} ({2})")
    @CsvFileSource(resources = "/test-data/grades.csv", numLinesToSkip = 1) // Skip header row
    @DisplayName("1️⃣ @CsvFileSource: Grade testing from CSV file")
    void testCsvFileSource_Grades(int score, String expectedGrade, String description) {
        String actualGrade = calculator.getGrade(score);
        assertEquals(expectedGrade, actualGrade, description);
        System.out.println("  ✓ Score " + score + " → " + actualGrade + " (" + description + ")");
    }

    /**
     * @CsvFileSource options explained:
     * 
     *                resources = "/path/to/file.csv" → Path relative to
     *                src/test/resources
     *                numLinesToSkip = 1 → Skip header row(s)
     *                delimiter = ',' → Column separator (default: comma)
     *                lineSeparator = "\n" → Line separator
     *                encoding = "UTF-8" → File encoding
     *                nullValues = {"NULL", "N/A"} → Strings treated as null
     *                emptyValue = "" → Value for empty columns
     */
    @Test
    @Order(2)
    @DisplayName("📋 @CsvFileSource Options Summary")
    void testCsvFileSourceOptions() {
        System.out.println("""

                ╔═══════════════════════════════════════════════════════════════════════╗
                ║                @CsvFileSource OPTIONS                                 ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  resources       │ Path to CSV (relative to src/test/resources)      ║
                ║  numLinesToSkip  │ Skip header rows (usually = 1)                    ║
                ║  delimiter       │ Column separator (default: comma)                  ║
                ║  lineSeparator   │ Line ending character                             ║
                ║  encoding        │ File encoding (default: UTF-8)                    ║
                ║  nullValues      │ Strings to treat as null                          ║
                ║  emptyValue      │ Value for empty columns                           ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  WHEN TO USE @CsvFileSource vs @CsvSource:                           ║
                ║  ────────────────────────────────────────────────────────────────────║
                ║  @CsvSource      │ Small datasets (< 20 cases), inline is OK         ║
                ║  @CsvFileSource  │ Large datasets, shared across tests, non-devs     ║
                ║                  │ can edit CSV without touching code                ║
                ╚═══════════════════════════════════════════════════════════════════════╝
                """);
        assertTrue(true);
    }
}
