package com.learning.service;

import com.learning.external.EmailService;
import com.learning.model.User;
import com.learning.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

/**
 * UserService - Business logic layer
 * 
 * This is what we TEST with Mockito!
 * - It DEPENDS on UserRepository (database)
 * - It DEPENDS on EmailService (external)
 * 
 * In tests:
 * - We MOCK the dependencies
 * - We test the SERVICE logic in isolation
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    // Constructor injection - Mockito will inject mocks here!
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    /**
     * Register a new user
     * 
     * Business logic:
     * 1. Validate input
     * 2. Check if email exists
     * 3. Save user
     * 4. Send welcome email
     */
    public User registerUser(String name, String email, String password) {
        // Validation
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        // Check duplicate email
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("Email already registered: " + email);
        }

        // Create and save user
        User user = new User(name, email, password);
        User savedUser = userRepository.save(user);

        // Send welcome email (fire and forget - don't fail registration if email fails)
        try {
            emailService.sendWelcomeEmail(email, name);
        } catch (Exception e) {
            // Log error but don't fail registration
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }

        return savedUser;
    }

    /**
     * Find user by ID
     */
    public Optional<User> findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        return userRepository.findById(id);
    }

    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        return userRepository.findByEmail(email);
    }

    /**
     * Get all active users
     */
    public List<User> getActiveUsers() {
        return userRepository.findByActiveTrue();
    }

    /**
     * Deactivate a user
     */
    public User deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    /**
     * Update user profile
     */
    public User updateProfile(Long userId, String newName, String newEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // If email is changing, check for duplicates
        if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new IllegalStateException("Email already in use: " + newEmail);
        }

        user.setName(newName);
        user.setEmail(newEmail);
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    /**
     * Request password reset
     */
    public boolean requestPasswordReset(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            // Don't reveal if email exists or not (security)
            return true;
        }

        User user = userOpt.get();
        String resetToken = generateResetToken();

        return emailService.sendPasswordResetEmail(email, resetToken);
    }

    private String generateResetToken() {
        // Simple token generation - in real app use secure random
        return "RESET-" + System.currentTimeMillis();
    }

    /**
     * Get total user count
     */
    public long getUserCount() {
        return userRepository.count();
    }
}
