package com.learning.junit5;

import com.learning.service.CalculatorService;

import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ PRIMITIVES vs OBJECTS - ASSERTION DIFFERENCES ║
 * ║ Mawa, ee class lo clear ga chupisthunna edi ekkada use cheyyali! ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 * 
 * SUMMARY TABLE:
 * ┌─────────────────────┬────────────┬─────────────┬──────────────────────────────┐
 * │ Assertion │ Primitives │ Objects │ Notes │
 * ├─────────────────────┼────────────┼─────────────┼──────────────────────────────┤
 * │ assertEquals │ ✅ VALUE │ ✅ .equals()│ Objects need equals() override│
 * │ assertSame │ ✅ (same) │ ⚠️ REFERENCE│ Checks memory location │
 * │ assertArrayEquals │ ✅ VALUE │ ✅ .equals()│ USE THIS for arrays! │
 * │ assertIterableEquals│ ❌ N/A │ ✅ .equals()│ Only for List, Set, etc. │
 * │ assertNotEquals │ ✅ VALUE │ ✅ .equals()│ Opposite of assertEquals │
 * │ assertNotSame │ ✅ │ ✅ REFERENCE│ Opposite of assertSame │
 * └─────────────────────┴────────────┴─────────────┴──────────────────────────────┘
 */
@DisplayName("🔥 Primitives vs Objects - Assertion Differences")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Part4_PrimitivesVsObjectsAssertionsTest {

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 1: assertEquals BEHAVIOR
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(1)
    @DisplayName("1️⃣ assertEquals - Primitives use == (value comparison)")
    void testAssertEquals_Primitives() {
        // ALL primitives work with assertEquals - compares VALUES

        int a = 10, b = 10;
        assertEquals(a, b); // ✅ PASS - same value

        double d1 = 3.14, d2 = 3.14;
        assertEquals(d1, d2); // ✅ PASS

        char c1 = 'A', c2 = 'A';
        assertEquals(c1, c2); // ✅ PASS

        boolean bool1 = true, bool2 = true;
        assertEquals(bool1, bool2); // ✅ PASS

        System.out.println("✅ All primitive assertEquals passed!");
    }

    @Test
    @Order(2)
    @DisplayName("2️⃣ assertEquals - Objects use .equals() method")
    void testAssertEquals_Objects() {
        // Objects use .equals() method internally

        // String has .equals() overridden - compares content
        String s1 = new String("hello");
        String s2 = new String("hello");
        assertEquals(s1, s2); // ✅ PASS - .equals() compares content

        // Integer has .equals() overridden
        Integer i1 = Integer.valueOf(100);
        Integer i2 = Integer.valueOf(100);
        assertEquals(i1, i2); // ✅ PASS

        // List has .equals() overridden
        List<String> list1 = Arrays.asList("a", "b");
        List<String> list2 = Arrays.asList("a", "b");
        assertEquals(list1, list2); // ✅ PASS

        System.out.println("✅ Objects with .equals() override work correctly!");
    }

    @Test
    @Order(3)
    @DisplayName("3️⃣ assertEquals - Custom object WITHOUT equals() FAILS!")
    void testAssertEquals_CustomObjectWithoutEquals() {
        // This is a common mistake!

        class Person {
            @SuppressWarnings("unused")
            String name;

            Person(String name) {
                this.name = name;
            }
            // NO .equals() override - uses Object.equals() which is ==
        }

        Person p1 = new Person("Ramesh");
        Person p2 = new Person("Ramesh");

        // This FAILS because default equals() uses == (reference comparison)
        assertNotEquals(p1, p2); // ✅ They are NOT equal (different objects)

        // Same object reference - this passes
        Person p3 = p1;
        assertEquals(p1, p3); // ✅ PASS - same reference

        System.out.println("⚠️ Custom objects without equals() use reference comparison!");
    }

    @Test
    @Order(4)
    @DisplayName("4️⃣ assertEquals - Custom object WITH equals() WORKS!")
    void testAssertEquals_CustomObjectWithEquals() {
        class Person {
            String name;

            Person(String name) {
                this.name = name;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o)
                    return true;
                if (o == null || getClass() != o.getClass())
                    return false;
                Person person = (Person) o;
                return Objects.equals(name, person.name);
            }
        }

        Person p1 = new Person("Ramesh");
        Person p2 = new Person("Ramesh");

        assertEquals(p1, p2); // ✅ PASS now - equals() is overridden!

        System.out.println("✅ Custom objects WITH equals() work properly!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 2: ARRAYS - THE TRICKY PART!
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(5)
    @DisplayName("5️⃣ ❌ assertEquals on Arrays - FAILS even with same content!")
    void testAssertEquals_ArraysFails() {
        // THIS IS THE COMMON MISTAKE YOU ASKED ABOUT!

        int[] arr1 = { 1, 2, 3 };
        int[] arr2 = { 1, 2, 3 };

        // Arrays DON'T override equals() - they use Object.equals() which is ==
        // So assertEquals checks REFERENCE, not content!

        assertNotEquals(arr1, arr2); // ✅ They are different objects!

        // Even for object arrays:
        String[] strArr1 = { "a", "b", "c" };
        String[] strArr2 = { "a", "b", "c" };
        assertNotEquals(strArr1, strArr2); // ✅ Different references!

        System.out.println("⚠️ assertEquals on arrays compares REFERENCE, not content!");
        System.out.println("   Use assertArrayEquals() instead!");
    }

    @Test
    @Order(6)
    @DisplayName("6️⃣ ✅ assertArrayEquals - USE THIS for Arrays!")
    void testAssertArrayEquals_Works() {
        // THIS IS THE CORRECT WAY TO COMPARE ARRAYS!

        // Primitive arrays
        int[] arr1 = { 1, 2, 3 };
        int[] arr2 = { 1, 2, 3 };
        assertArrayEquals(arr1, arr2); // ✅ PASS - compares content!

        double[] dArr1 = { 1.1, 2.2, 3.3 };
        double[] dArr2 = { 1.1, 2.2, 3.3 };
        assertArrayEquals(dArr1, dArr2); // ✅ PASS

        // Object arrays - uses .equals() on each element
        String[] strArr1 = { "hello", "world" };
        String[] strArr2 = { "hello", "world" };
        assertArrayEquals(strArr1, strArr2); // ✅ PASS

        System.out.println("✅ assertArrayEquals compares CONTENT, not reference!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 3: assertSame vs assertEquals
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(7)
    @DisplayName("7️⃣ assertSame - Checks SAME object reference (==)")
    void testAssertSame_Reference() {
        // assertSame uses == for BOTH primitives and objects

        // For primitives - same as assertEquals
        int a = 5, b = 5;
        assertSame(a, b); // ✅ Works (but assertEquals is preferred)

        // For objects - checks if SAME object in memory
        String s1 = "hello"; // String pool
        String s2 = "hello"; // Same string pool reference
        assertSame(s1, s2); // ✅ PASS - same string pool reference

        // NEW objects - different memory locations!
        String s3 = new String("hello");
        String s4 = new String("hello");
        assertNotSame(s3, s4); // ✅ They are NOT the same object
        assertEquals(s3, s4); // ✅ But they have same content

        System.out.println("✅ assertSame checks memory reference, not content!");
    }

    @Test
    @Order(8)
    @DisplayName("8️⃣ When to use assertSame? Cached/Singleton objects!")
    void testAssertSame_UseCaseCachedObjects() {
        // assertSame is useful for checking SAME object reference

        // Example 1: Collections.emptyList() returns SAME singleton instance
        List<String> empty1 = Collections.emptyList();
        List<String> empty2 = Collections.emptyList();
        assertSame(empty1, empty2); // ✅ PASS - same cached object

        // Example 2: Integer caching (-128 to 127)
        Integer cached1 = Integer.valueOf(100); // Cached range
        Integer cached2 = Integer.valueOf(100);
        assertSame(cached1, cached2); // ✅ PASS - same cached Integer

        // But outside cache range - different objects!
        Integer big1 = Integer.valueOf(1000); // Outside cache
        Integer big2 = Integer.valueOf(1000);
        assertNotSame(big1, big2); // ✅ Different objects
        assertEquals(big1, big2); // ✅ But same value

        // Example 3: new ArrayList - different objects
        List<String> newList1 = new ArrayList<>();
        List<String> newList2 = new ArrayList<>();
        assertNotSame(newList1, newList2); // ✅ Different objects
        assertEquals(newList1, newList2); // ✅ But same content (both empty)

        System.out.println("✅ assertSame checks if SAME object in memory!");
        System.out.println("   Useful for: Singletons, cached objects, factory methods");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 4: Collections - List, Set, etc.
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(9)
    @DisplayName("9️⃣ assertEquals on Lists - Works! (List.equals() is overridden)")
    void testAssertEquals_Lists() {
        // Lists have proper .equals() implementation!

        List<Integer> list1 = Arrays.asList(1, 2, 3);
        List<Integer> list2 = Arrays.asList(1, 2, 3);

        assertEquals(list1, list2); // ✅ PASS - List.equals() compares elements

        List<String> strList1 = List.of("a", "b", "c");
        List<String> strList2 = List.of("a", "b", "c");
        assertEquals(strList1, strList2); // ✅ PASS

        System.out.println("✅ Lists work with assertEquals (unlike arrays)!");
    }

    @Test
    @Order(10)
    @DisplayName("🔟 assertIterableEquals - More strict comparison for Iterables")
    void testAssertIterableEquals() {
        // assertIterableEquals is stricter - checks element by element

        List<Integer> list1 = Arrays.asList(1, 2, 3);
        List<Integer> list2 = Arrays.asList(1, 2, 3);

        assertIterableEquals(list1, list2); // ✅ PASS

        // Even Set works (order doesn't matter for Set.equals, but does for Iterable)
        Set<String> set1 = new LinkedHashSet<>(Arrays.asList("a", "b"));
        Set<String> set2 = new LinkedHashSet<>(Arrays.asList("a", "b"));
        assertIterableEquals(set1, set2); // ✅ PASS

        System.out.println("✅ assertIterableEquals for any Iterable!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 5: SUMMARY - Quick Reference
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(11)
    @DisplayName("📋 SUMMARY: What to use when?")
    void testSummary() {
        System.out.println("""

                ╔═══════════════════════════════════════════════════════════════════════╗
                ║                    ASSERTION USAGE GUIDE                              ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  🔢 PRIMITIVES (int, double, char, boolean...)                       ║
                ║     → assertEquals()      ✅ Always use this                         ║
                ║     → assertSame()        ✅ Works, but not preferred                ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  📦 OBJECTS (String, Integer, custom classes...)                     ║
                ║     → assertEquals()      ✅ Uses .equals() - check if equals exists!║
                ║     → assertSame()        ✅ For reference check (singleton testing) ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  📚 ARRAYS (int[], String[], Object[]...)                            ║
                ║     → assertArrayEquals() ✅ USE THIS - compares content             ║
                ║     → assertEquals()      ❌ DON'T USE - compares reference only!    ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  📋 COLLECTIONS (List, Set, Map...)                                  ║
                ║     → assertEquals()        ✅ Works (they override .equals())       ║
                ║     → assertIterableEquals()✅ For element-by-element comparison     ║
                ╚═══════════════════════════════════════════════════════════════════════╝
                """);

        assertTrue(true); // Just to make the test pass
    }
}
