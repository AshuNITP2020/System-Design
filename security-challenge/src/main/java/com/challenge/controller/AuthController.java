package com.challenge.controller;

import com.challenge.entity.Role;
import com.challenge.entity.User;
import com.challenge.repository.UserRepository;
import com.challenge.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    // Password validation pattern: min 8 chars, at least 1 uppercase, 1 lowercase, 1 digit
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$"
    );

    /**
     * Register a new user
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 1. Extract username, email, password from request
            String username = request.get("username");
            String email = request.get("email");
            String password = request.get("password");
            
            // 2. Validate all fields
            if (username == null || username.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Username is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (email == null || email.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Email is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (password == null || password.isEmpty()) {
                response.put("success", false);
                response.put("message", "Password is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Validate username (3-50 chars)
            username = username.trim();
            if (username.length() < 3 || username.length() > 50) {
                response.put("success", false);
                response.put("message", "Username must be between 3 and 50 characters");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Validate email format
            email = email.trim().toLowerCase();
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                response.put("success", false);
                response.put("message", "Invalid email format");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Validate password strength
            if (!PASSWORD_PATTERN.matcher(password).matches()) {
                response.put("success", false);
                response.put("message", "Password must be at least 8 characters with at least 1 uppercase, 1 lowercase, and 1 digit");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 3. Check if username/email already exists
            if (userRepository.existsByUsername(username)) {
                response.put("success", false);
                response.put("message", "Username already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            
            if (userRepository.existsByEmail(email)) {
                response.put("success", false);
                response.put("message", "Email already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            
            // 4. Hash the password using BCryptPasswordEncoder
            String hashedPassword = passwordEncoder.encode(password);
            
            // 5. Create and save new User with role USER
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(hashedPassword);
            user.setRole(Role.USER);
            user.setEnabled(true);
            
            User savedUser = userRepository.save(user);
            
            // 6. Return success response (without password!)
            response.put("success", true);
            response.put("message", "User registered successfully");
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", savedUser.getId());
            userData.put("username", savedUser.getUsername());
            userData.put("email", savedUser.getEmail());
            userData.put("role", savedUser.getRole().name());
            response.put("data", userData);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Login user and return JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 1. Extract username/email and password from request
            String usernameOrEmail = request.get("username");
            String password = request.get("password");
            
            // Validate input
            if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Username or email is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (password == null || password.isEmpty()) {
                response.put("success", false);
                response.put("message", "Password is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            usernameOrEmail = usernameOrEmail.trim();
            
            // 2. Find user by username or email
            User user = null;
            
            // Try to find by username first
            user = userRepository.findByUsername(usernameOrEmail).orElse(null);
            
            // If not found by username, try email
            if (user == null) {
                user = userRepository.findByEmail(usernameOrEmail.toLowerCase()).orElse(null);
            }
            
            // If user not found, return error
            if (user == null) {
                response.put("success", false);
                response.put("message", "Invalid username/email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // 3. Verify password using BCryptPasswordEncoder.matches()
            if (!passwordEncoder.matches(password, user.getPassword())) {
                response.put("success", false);
                response.put("message", "Invalid username/email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // 4. Generate JWT token containing user info
            String token = jwtTokenProvider.generateToken(user);
            
            // 5. Return token in response
            response.put("success", true);
            response.put("message", "Login successful");
            Map<String, Object> data = new HashMap<>();
            data.put("accessToken", token);
            data.put("tokenType", "Bearer");
            data.put("expiresIn", 86400); // 24 hours in seconds
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            userData.put("role", user.getRole().name());
            data.put("user", userData);
            response.put("data", data);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get current user info (requires authentication)
     * Returns the authenticated user's information
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @AuthenticationPrincipal User currentUser) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User information retrieved successfully");
        
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", currentUser.getId());
        userData.put("username", currentUser.getUsername());
        userData.put("email", currentUser.getEmail());
        userData.put("role", currentUser.getRole().name());
        userData.put("enabled", currentUser.isEnabled());
        userData.put("createdAt", currentUser.getCreatedAt());
        
        response.put("data", userData);
        return ResponseEntity.ok(response);
    }
}

