package com.learning.controller;

import com.learning.model.User;
import com.learning.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UserController - REST API Layer
 * 
 * We will test this with MOCKMVC!
 * - @Valid: Triggers Jakarta Validation on the DTO
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // POST /api/users
    @PostMapping
    public ResponseEntity<User> registerUser(@Valid @RequestBody RegistrationRequest request) { // Added @Valid
        User user = userService.registerUser(request.name(), request.email(), request.password());
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    // GET /api/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/users/active
    @GetMapping("/active")
    public List<User> getActiveUsers() {
        return userService.getActiveUsers();
    }

    // Simple DTO for registration with VALIDATION
    public record RegistrationRequest(
            @NotBlank(message = "Name is required") String name,

            @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,

            @Size(min = 6, message = "Password must be at least 6 characters") String password) {
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleDuplicateEmail(IllegalStateException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }
}
