package com.learning.mockito;

import com.learning.external.EmailService;
import com.learning.model.User;
import com.learning.repository.UserRepository;
import com.learning.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ PART 6: ARGUMENT CAPTOR - Capturing and Inspecting Arguments ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("📚 Part 6: ArgumentCaptor")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Part6_ArgumentCaptorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    // ArgumentCaptor using @Captor annotation
    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    // ═══════════════════════════════════════════════════════════════════════════
    // Basic Capture
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(1)
    @DisplayName("1️⃣ Basic ArgumentCaptor - Capture and inspect")
    void testBasicCapture() {
        System.out.println("=== Basic ArgumentCaptor ===\n");

        // ARRANGE
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(emailService.sendWelcomeEmail(anyString(), anyString())).thenReturn(true);

        // ACT
        userService.registerUser("Ramesh Kumar", "ramesh@example.com", "secret123");

        // CAPTURE the User that was saved
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        // INSPECT the captured object
        System.out.println("Captured User:");
        System.out.println("  Name: " + capturedUser.getName());
        System.out.println("  Email: " + capturedUser.getEmail());
        System.out.println("  Password: " + capturedUser.getPassword());
        System.out.println("  Active: " + capturedUser.isActive());

        // ASSERT on captured values
        assertEquals("Ramesh Kumar", capturedUser.getName());
        assertEquals("ramesh@example.com", capturedUser.getEmail());
        assertEquals("secret123", capturedUser.getPassword());
        assertTrue(capturedUser.isActive());

        System.out.println("\n✅ Captured and inspected saved User!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Capture Multiple Arguments
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(2)
    @DisplayName("2️⃣ Capture multiple arguments from same call")
    void testCaptureMultipleArgs() {
        System.out.println("=== Capture Multiple Arguments ===\n");

        // ARRANGE
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(emailService.sendWelcomeEmail(anyString(), anyString())).thenReturn(true);

        // ACT
        userService.registerUser("Test User", "test@example.com", "pass123");

        // CAPTURE both email and name arguments
        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);

        verify(emailService).sendWelcomeEmail(emailCaptor.capture(), nameCaptor.capture());

        System.out.println("sendWelcomeEmail was called with:");
        System.out.println("  Email: " + emailCaptor.getValue());
        System.out.println("  Name: " + nameCaptor.getValue());

        assertEquals("test@example.com", emailCaptor.getValue());
        assertEquals("Test User", nameCaptor.getValue());

        System.out.println("\n✅ Captured both arguments!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Capture from Multiple Calls (getAllValues)
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(3)
    @DisplayName("3️⃣ Capture from multiple calls - getAllValues()")
    void testCaptureMultipleCalls() {
        System.out.println("=== Capture from Multiple Calls ===\n");

        // ARRANGE
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(System.nanoTime());
            return u;
        });
        when(emailService.sendWelcomeEmail(anyString(), anyString())).thenReturn(true);

        // ACT - Register 3 users
        userService.registerUser("User1", "user1@example.com", "password1");
        userService.registerUser("User2", "user2@example.com", "password2");
        userService.registerUser("User3", "user3@example.com", "password3");

        // CAPTURE all saves
        verify(userRepository, times(3)).save(userCaptor.capture());

        // GET ALL captured values
        List<User> allCapturedUsers = userCaptor.getAllValues();

        System.out.println("Captured " + allCapturedUsers.size() + " users:");
        for (int i = 0; i < allCapturedUsers.size(); i++) {
            System.out.println("  User " + (i + 1) + ": " + allCapturedUsers.get(i).getName() +
                    " (" + allCapturedUsers.get(i).getEmail() + ")");
        }

        // ASSERT
        assertEquals(3, allCapturedUsers.size());
        assertEquals("User1", allCapturedUsers.get(0).getName());
        assertEquals("User2", allCapturedUsers.get(1).getName());
        assertEquals("User3", allCapturedUsers.get(2).getName());

        System.out.println("\n✅ Captured all 3 users from multiple calls!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Use Case: Verify Dynamic Values
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(4)
    @DisplayName("4️⃣ Use case: Verify dynamically generated values")
    void testVerifyDynamicValues() {
        System.out.println("=== Verify Dynamic/Computed Values ===\n");

        // ARRANGE
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(emailService.sendWelcomeEmail(anyString(), anyString())).thenReturn(true);

        // ACT
        userService.registerUser("Test", "test@example.com", "password123");

        // CAPTURE
        verify(userRepository).save(userCaptor.capture());
        User captured = userCaptor.getValue();

        // VERIFY properties that are set by the service
        assertNotNull(captured.getCreatedAt(), "CreatedAt should be set by service");
        assertTrue(captured.isActive(), "New user should be active");
        assertNotNull(captured.getPassword(), "Password should be stored");

        System.out.println("Verified dynamic values:");
        System.out.println("  CreatedAt: " + captured.getCreatedAt());
        System.out.println("  IsActive: " + captured.isActive());

        System.out.println("\n✅ Dynamic values verified via captor!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Inline Captor (Without @Captor)
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(5)
    @DisplayName("5️⃣ Inline captor creation (without @Captor)")
    void testInlineCaptor() {
        System.out.println("=== Inline Captor ===\n");

        // ARRANGE
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(emailService.sendWelcomeEmail(anyString(), anyString())).thenReturn(true);

        // ACT
        userService.registerUser("Inline Test", "inline@email.com", "password123");

        // INLINE captor creation
        ArgumentCaptor<User> inlineCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(inlineCaptor.capture());

        assertEquals("Inline Test", inlineCaptor.getValue().getName());
        System.out.println("Captured (inline): " + inlineCaptor.getValue().getName());

        System.out.println("\n✅ Inline captor works too!");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SUMMARY
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @Order(6)
    @DisplayName("📋 SUMMARY: ArgumentCaptor")
    void testSummary() {
        System.out.println("""

                ╔═══════════════════════════════════════════════════════════════════════╗
                ║                    ARGUMENT CAPTOR SUMMARY                            ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  @Captor                                                              ║
                ║  ArgumentCaptor<Type> captor;                                         ║
                ║     → Declare captor as field                                        ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  ArgumentCaptor.forClass(Type.class)                                  ║
                ║     → Create inline captor                                           ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  verify(mock).method(captor.capture())                                ║
                ║     → Capture the argument                                           ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  captor.getValue()                                                    ║
                ║     → Get single (or last) captured value                            ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  captor.getAllValues()                                                ║
                ║     → Get all captured values (for multiple calls)                   ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  Use when:                                                            ║
                ║     → Need to inspect object properties                              ║
                ║     → Verifying computed/dynamic values                              ║
                ║     → Capturing from multiple calls                                  ║
                ║     → More detailed assertions than argThat()                        ║
                ╚═══════════════════════════════════════════════════════════════════════╝
                """);
        assertTrue(true);
    }
}
