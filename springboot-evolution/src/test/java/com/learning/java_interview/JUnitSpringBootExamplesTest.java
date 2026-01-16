package com.learning.java_interview;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ===================================================================
 * JUNIT WITH SPRING BOOT - INTEGRATION TESTING EXAMPLES
 * ===================================================================
 * 
 * Spring Boot provides several testing annotations and utilities:
 * 
 * @SpringBootTest - Full application context test
 * @WebMvcTest - Web layer test (controllers only)
 * @DataJpaTest - JPA repository test
 * @MockBean - Mock Spring bean
 * @Autowired - Inject Spring beans
 * 
 * Note: These are examples. Actual implementation depends on your Spring Boot app.
 */
@DisplayName("JUnit with Spring Boot Examples")
class JUnitSpringBootExamplesTest {

    // ===================================================================
    // SLICE TESTING - @WebMvcTest (Controller Layer Only)
    // ===================================================================

    /**
     * @WebMvcTest - Tests only the web layer (controllers)
     * - Loads only web-related beans
     * - Auto-configures MockMvc
     * - Faster than full @SpringBootTest
     */
    @WebMvcTest
    @DisplayName("Web MVC Test Example")
    static class WebMvcTestExample {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private UserService userService;

        @Test
        @DisplayName("Test GET endpoint")
        void testGetEndpoint() throws Exception {
            // This is a template - adjust based on your actual controller
            mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("Test POST endpoint")
        void testPostEndpoint() throws Exception {
            // Template for POST request
            mockMvc.perform(post("/api/users")
                    .contentType("application/json")
                    .content("{\"name\":\"John\",\"email\":\"john@test.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John"));
        }
    }

    // ===================================================================
    // SLICE TESTING - @DataJpaTest (Repository Layer Only)
    // ===================================================================

    /**
     * @DataJpaTest - Tests only the data layer
     * - Uses in-memory database (H2 by default)
     * - Auto-configures JPA repositories
     * - Faster than full @SpringBootTest
     */
    @DataJpaTest
    @ActiveProfiles("test")
    @DisplayName("Data JPA Test Example")
    static class DataJpaTestExample {

        // @Autowired
        // private UserRepository userRepository;

        @Test
        @DisplayName("Test repository save")
        void testRepositorySave() {
            // Example test
            // User user = new User("Test User", "test@test.com");
            // User saved = userRepository.save(user);
            // assertNotNull(saved.getId());
        }

        @Test
        @DisplayName("Test repository find")
        void testRepositoryFind() {
            // Example test
            // Optional<User> user = userRepository.findById(1L);
            // assertTrue(user.isPresent());
        }
    }

    // ===================================================================
    // FULL INTEGRATION TEST - @SpringBootTest
    // ===================================================================

    /**
     * @SpringBootTest - Full application context test
     * - Loads complete Spring application
     * - Can use TestRestTemplate for HTTP calls
     * - Slower but more comprehensive
     */
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    @DisplayName("Full Integration Test Example")
    static class FullIntegrationTestExample {

        @LocalServerPort
        private int port;

        @Autowired
        private TestRestTemplate restTemplate;

        @Test
        @DisplayName("Test full application context")
        void testFullContext() {
            // Test that application context loads successfully
            assertNotNull(restTemplate);
            assertTrue(port > 0);
        }

        @Test
        @DisplayName("Test HTTP endpoint with TestRestTemplate")
        void testHttpEndpoint() {
            // Example HTTP test
            String url = "http://localhost:" + port + "/api/health";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }
    }

    // ===================================================================
    // MOCKING SPRING BEANS
    // ===================================================================

    @SpringBootTest
    @DisplayName("Mock Spring Bean Example")
    static class MockSpringBeanExample {

        @MockBean
        private EmailService emailService;

        @Autowired
        private UserService userService;

        @Test
        @DisplayName("Test with mocked Spring bean")
        void testWithMockedBean() {
            // The emailService is mocked, so no actual email is sent
            // Example usage (uncomment when you have actual implementation):
            // User user = userService.createUser("Test", "test@test.com");
            // verify(emailService).sendWelcomeEmail(anyString());
            assertNotNull(userService); // Suppress unused warning
        }
    }

    // ===================================================================
    // TEST PROFILES
    // ===================================================================

    @SpringBootTest
    @ActiveProfiles("test")
    @DisplayName("Test with Profile")
    static class TestWithProfile {
        
        @Test
        @DisplayName("Test runs with test profile")
        void testWithTestProfile() {
            // Application runs with application-test.properties
            assertTrue(true);
        }
    }

    // ===================================================================
    // HELPER INTERFACES (Placeholders)
    // ===================================================================

    interface UserService {
        User createUser(String name, String email);
    }

    interface EmailService {
        void sendWelcomeEmail(String email);
    }

    static class User {
        private Long id;
        private String name;
        private String email;

        public User(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
    }
}

