package com.learning.repository;

import com.learning.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserRepository - Database layer
 * 
 * [JPA CONVERSION NOTES]
 * Extends JpaRepository<User, Long> to get all standard CRUD methods for free:
 * - save(), findById(), count(), deleteById(), findAll()
 * 
 * We only declare CUSTOM query methods here.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email
     * Spring Data JPA automatically generates SQL: SELECT * FROM users WHERE email
     * = ?
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if email already exists
     * Spring Data JPA generates: SELECT COUNT(*) > 0 FROM users WHERE email = ?
     */
    boolean existsByEmail(String email);

    /**
     * Find all active users
     * Spring Data JPA generates: SELECT * FROM users WHERE active = true
     */
    List<User> findByActiveTrue();

    // NOTE:
    // save(), findById(), count(), deleteById() come from JpaRepository!
    // We don't need to define them anymore.
}
