package com.challenge.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ⚠️ SECURITY CHALLENGE: Implement authentication endpoints!
 * 
 * YOUR TASKS:
 * 1. Implement user registration (POST /auth/register)
 * 2. Implement user login (POST /auth/login)
 * 3. Return JWT token on successful login
 * 4. Optionally: Implement refresh token mechanism
 * 
 * REQUIREMENTS FOR REGISTRATION:
 * - Validate username (3-50 chars, unique)
 * - Validate email (valid format, unique)
 * - Validate password (min 8 chars, at least 1 uppercase, 1 lowercase, 1 digit)
 * - Hash password before storing (use BCrypt)
 * - Assign default role (USER)
 * 
 * REQUIREMENTS FOR LOGIN:
 * - Accept username/email and password
 * - Verify credentials
 * - Generate and return JWT token
 * - Token should contain: userId, username, role, expiration
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * Register a new user
     * TODO: Implement registration logic
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> request) {
        
        // TODO: Implement the following:
        // 1. Extract username, email, password from request
        // 2. Validate all fields
        // 3. Check if username/email already exists
        // 4. Hash the password using BCryptPasswordEncoder
        // 5. Create and save new User with role USER
        // 6. Return success response
        
        return ResponseEntity.ok(Map.of(
            "success", false,
            "message", "Registration not implemented yet - YOUR TASK!"
        ));
    }

    /**
     * Login user and return JWT token
     * TODO: Implement login logic
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        
        // TODO: Implement the following:
        // 1. Extract username and password from request
        // 2. Find user by username
        // 3. Verify password using BCryptPasswordEncoder.matches()
        // 4. Generate JWT token containing user info
        // 5. Return token in response
        
        return ResponseEntity.ok(Map.of(
            "success", false,
            "message", "Login not implemented yet - YOUR TASK!"
        ));
    }

    /**
     * Get current user info (requires authentication)
     * TODO: Add security and return current user's info
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        
        // TODO: Get authenticated user from SecurityContext
        // Return user info (without password!)
        
        return ResponseEntity.ok(Map.of(
            "success", false,
            "message", "Get current user not implemented yet - YOUR TASK!"
        ));
    }
}

