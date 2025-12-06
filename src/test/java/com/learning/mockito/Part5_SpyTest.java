package com.learning.mockito;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ PART 5: SPY - Partial Mocking (@Spy vs @Mock) ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("📚 Part 5: Spy - Partial Mocking")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Part5_SpyTest {

    @Spy
    private ArrayList<String> spyList = new ArrayList<>();

    // ═══════════════════════════════════════════════════════════════════════════
    // Spy: Real Methods Called by Default
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(1)
    @DisplayName("1️⃣ Spy calls REAL methods by default")
    void testSpyCallsRealMethods() {
        System.out.println("=== Spy Calls Real Methods ===\n");

        // REAL add() method is called!
        spyList.add("one");
        spyList.add("two");
        spyList.add("three");

        // REAL size() returns actual count
        assertEquals(3, spyList.size());
        System.out.println("Added 3 items, size() = " + spyList.size());

        // REAL get() returns actual element
        assertEquals("two", spyList.get(1));
        System.out.println("get(1) = " + spyList.get(1));

        System.out.println("\n✅ Spy uses REAL implementation by default!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Override Specific Methods
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(2)
    @DisplayName("2️⃣ Override specific method with doReturn()")
    void testSpyOverrideMethod() {
        System.out.println("=== Override Specific Method ===\n");

        // Add real items
        spyList.add("one");
        spyList.add("two");
        System.out.println("Added 2 items, real size = " + spyList.size());

        // Override size() - but keep add() real!
        doReturn(100).when(spyList).size();

        // Now size() returns stubbed value
        assertEquals(100, spyList.size());
        System.out.println("After stub, size() = " + spyList.size());

        // But items are still there!
        assertEquals("one", spyList.get(0));
        System.out.println("get(0) still works = " + spyList.get(0));

        System.out.println("\n✅ Only size() is stubbed, rest is real!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ⚠️ doReturn vs when for Spies
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(3)
    @DisplayName("3️⃣ ⚠️ Why use doReturn() instead of when() for spies?")
    void testDoReturnVsWhenForSpy() {
        System.out.println("=== doReturn() vs when() for Spies ===\n");

        System.out.println("""
                    ⚠️ IMPORTANT for Spies:

                    when(spy.method()).thenReturn(value)
                    ↳ This FIRST CALLS spy.method()!
                    ↳ If method has side effects, they happen!

                    doReturn(value).when(spy).method()
                    ↳ Does NOT call the real method
                    ↳ Safer for spies!
                """);

        // Safe stubbing for spy
        doReturn("mocked").when(spyList).get(999);

        // Now get(999) is stubbed, won't throw IndexOutOfBoundsException!
        assertEquals("mocked", spyList.get(999));
        System.out.println("get(999) = 'mocked' (stubbed, no exception!)");

        System.out.println("\n✅ Always use doReturn/doThrow with spies!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Mock vs Spy Comparison
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(4)
    @DisplayName("4️⃣ Mock vs Spy: Side-by-side comparison")
    void testMockVsSpyComparison() {
        System.out.println("=== Mock vs Spy Comparison ===\n");

        // Create both
        List<String> mockedList = mock(ArrayList.class);
        List<String> spiedList = spy(new ArrayList<>());

        System.out.println("Calling add() on both:\n");

        // Mock: add() does nothing! Returns default (false)
        boolean mockResult = mockedList.add("test");
        System.out.println("Mock: add('test') returned " + mockResult);
        System.out.println("Mock: size() = " + mockedList.size());

        // Spy: add() actually adds!
        boolean spyResult = spiedList.add("test");
        System.out.println("\nSpy: add('test') returned " + spyResult);
        System.out.println("Spy: size() = " + spiedList.size());

        assertEquals(0, mockedList.size()); // Mock: nothing added
        assertEquals(1, spiedList.size()); // Spy: actually added!

        System.out.println("\n✅ Mock returns defaults, Spy uses real code!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Spy Use Case: Verify Real + Stub Behavior
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(5)
    @DisplayName("5️⃣ Use case: Track real calls + stub specific methods")
    void testSpyUseCase() {
        System.out.println("=== Spy Use Case ===\n");

        // Use real add()
        spyList.add("item1");
        spyList.add("item2");

        // Verify real methods were called
        verify(spyList, times(2)).add(anyString());
        System.out.println("✓ Verified: add() was called twice");

        // Stub specific method for testing
        doReturn(false).when(spyList).isEmpty();

        // isEmpty() now always returns false (even if list is cleared)
        spyList.clear();
        assertFalse(spyList.isEmpty()); // Stubbed!
        System.out.println("✓ isEmpty() stubbed to return false even after clear()");

        System.out.println("\n✅ Spy allows mix of real and stubbed behavior!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SUMMARY
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(6)
    @DisplayName("📋 SUMMARY: Spy vs Mock")
    void testSummary() {
        System.out.println("""

                ╔═══════════════════════════════════════════════════════════════════════╗
                ║                    SPY VS MOCK SUMMARY                                ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  @Mock                                                                ║
                ║     → Complete fake object                                           ║
                ║     → Returns null/0/false by default                                ║
                ║     → when().thenReturn() works fine                                 ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  @Spy                                                                 ║
                ║     → Real object with selective stubbing                            ║
                ║     → Calls REAL methods by default                                  ║
                ║     → Use doReturn().when() to avoid calling real method!            ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  When to use @Spy:                                                    ║
                ║     → Testing legacy code (can't mock everything)                    ║
                ║     → Most methods should be real, stub few                          ║
                ║     → Need to verify calls on real object                            ║
                ║     → Working with collections (ArrayList, HashMap)                  ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  ⚠️ Always use doReturn/doThrow with spies!                          ║
                ╚═══════════════════════════════════════════════════════════════════════╝
                """);
        assertTrue(true);
    }
}
