package com.learning.repository;

import com.learning.integration.AbstractContainerBaseTest;
import com.learning.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ REPOSITORY SLICE TEST (@DataJpaTest) ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 * 
 * Concepts:
 * 1. @DataJpaTest: Loads ONLY Repository layer (No Controller, No Service).
 * - Very fast compared to @SpringBootTest.
 * - Transactional by default (Rolls back after each test).
 * 
 * 2. @AutoConfigureTestDatabase(replace = NONE):
 * - Normally @DataJpaTest replaces DB with H2.
 * - We want to use our Real MySQL Container, so we disable replacement.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest extends AbstractContainerBaseTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should find user by active status (Derived Query)")
    void shouldFindActiveUsers() {
        // Given
        User activeUser = new User("Active", "active@test.com", "password123");
        User inactiveUser = new User("Inactive", "inactive@test.com", "password123");
        inactiveUser.setActive(false);
        inactiveUser.setCreatedAt(LocalDateTime.now()); // Manual set as @PrePersist might rely on full context? No, JPA
                                                        // handles it.

        userRepository.save(activeUser);
        userRepository.save(inactiveUser);

        // When
        var activeUsers = userRepository.findByActiveTrue();

        // Then
        assertThat(activeUsers).hasSize(1);
        assertThat(activeUsers.get(0).getEmail()).isEqualTo("active@test.com");
    }

    @Test
    @DisplayName("Should check if email exists")
    void shouldCheckEmailExists() {
        // Given
        User user = new User("Exist", "exist@test.com", "password123");
        userRepository.save(user);

        // Then
        assertThat(userRepository.existsByEmail("exist@test.com")).isTrue();
        assertThat(userRepository.existsByEmail("other@test.com")).isFalse();
    }
}
