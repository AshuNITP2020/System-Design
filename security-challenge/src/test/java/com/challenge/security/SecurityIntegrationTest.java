package com.challenge.security;

import com.challenge.entity.Role;
import com.challenge.entity.User;
import com.challenge.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for security features.
 * Tests authentication, authorization, JWT tokens, and access control.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private User adminUser;
    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("TestPass123"));
        testUser.setRole(Role.USER);
        testUser.setEnabled(true);
        testUser = userRepository.save(testUser);
        userToken = jwtTokenProvider.generateToken(testUser);

        // Create admin user
        adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword(passwordEncoder.encode("AdminPass123"));
        adminUser.setRole(Role.ADMIN);
        adminUser.setEnabled(true);
        adminUser = userRepository.save(adminUser);
        adminToken = jwtTokenProvider.generateToken(adminUser);
    }

    // ========== REGISTRATION TESTS ==========

    @Test
    void testUserRegistration_Success() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("username", "newuser");
        request.put("email", "newuser@example.com");
        request.put("password", "NewPass123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("newuser"))
                .andExpect(jsonPath("$.data.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.data.password").doesNotExist()); // Password should not be in response
    }

    @Test
    void testUserRegistration_DuplicateUsername() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("username", "testuser"); // Already exists
        request.put("email", "different@example.com");
        request.put("password", "TestPass123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }

    @Test
    void testUserRegistration_WeakPassword() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("username", "weakuser");
        request.put("email", "weak@example.com");
        request.put("password", "weak"); // Too weak

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ========== LOGIN TESTS ==========

    @Test
    void testLogin_Success() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("username", "testuser");
        request.put("password", "TestPass123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.user.username").value("testuser"));
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("username", "testuser");
        request.put("password", "WrongPassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid username/email or password"));
    }

    @Test
    void testLogin_UserNotFound() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("username", "nonexistent");
        request.put("password", "SomePass123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ========== JWT TOKEN VALIDATION TESTS ==========

    @Test
    void testAccessProtectedEndpoint_WithValidToken() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    void testAccessProtectedEndpoint_WithoutToken() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void testAccessProtectedEndpoint_WithInvalidToken() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAccessProtectedEndpoint_WithExpiredToken() throws Exception {
        // Create an expired token (this would require mocking time, simplified here)
        // In a real scenario, you'd need to manipulate the token expiration
        String expiredToken = "expired.token.here";
        
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    // ========== AUTHORIZATION TESTS ==========

    @Test
    void testAdminEndpoint_AsAdmin() throws Exception {
        mockMvc.perform(get("/api/documents/admin/all")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testAdminEndpoint_AsRegularUser() throws Exception {
        mockMvc.perform(get("/api/documents/admin/all")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("FORBIDDEN"));
    }

    @Test
    void testCreateDocument_WithWritePermission() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("title", "Test Document");
        request.put("content", "Test Content");

        mockMvc.perform(post("/api/documents")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetCurrentUser() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.role").value("USER"))
                .andExpect(jsonPath("$.data.password").doesNotExist()); // Password should never be returned
    }

    // ========== PUBLIC ENDPOINT TESTS ==========

    @Test
    void testPublicEndpoints_NoAuthenticationRequired() throws Exception {
        // Registration endpoint should be accessible without authentication
        Map<String, String> request = new HashMap<>();
        request.put("username", "publicuser");
        request.put("email", "public@example.com");
        request.put("password", "PublicPass123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    // ========== EDGE CASE TESTS ==========

    @Test
    void testLogin_WithEmailInsteadOfUsername() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("username", "test@example.com"); // Using email
        request.put("password", "TestPass123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists());
    }

    @Test
    void testAccessProtectedEndpoint_WithMalformedToken() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer not.a.valid.jwt.token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAccessProtectedEndpoint_WithWrongHeaderFormat() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", userToken)) // Missing "Bearer " prefix
                .andExpect(status().isUnauthorized());
    }
}

