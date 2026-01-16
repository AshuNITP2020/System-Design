package com.learning.java_interview;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ===================================================================
 * JUNIT WITH MOCKITO - ADVANCED TESTING EXAMPLES
 * ===================================================================
 * 
 * Mockito is a mocking framework used with JUnit to create test doubles.
 * It allows you to:
 * - Create mock objects
 * - Stub method calls
 * - Verify interactions
 * - Capture arguments
 * 
 * Spring Boot Test includes Mockito by default.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JUnit with Mockito Examples")
class JUnitMockingExamplesTest {

    // ===================================================================
    // BASIC MOCKING
    // ===================================================================

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        // With @ExtendWith(MockitoExtension.class), mocks are automatically initialized
        userService = new UserService(userRepository, emailService);
    }

    @Test
    @DisplayName("Basic Mock - Stub Method Return Value")
    void testBasicMock() {
        // Arrange - Stub the mock behavior
        User mockUser = new User(1L, "John Doe", "john@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // Act
        Optional<User> result = userService.findUserById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
        assertEquals("john@example.com", result.get().getEmail());
    }

    @Test
    @DisplayName("Mock - Return Empty Optional")
    void testMockReturnEmpty() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findUserById(999L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Mock - Throw Exception")
    void testMockThrowException() {
        // Arrange
        when(userRepository.findById(anyLong()))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.findUserById(1L);
        });
    }

    // ===================================================================
    // VERIFICATION
    // ===================================================================

    @Test
    @DisplayName("Verify Method Calls")
    void testVerifyMethodCalls() {
        // Arrange
        User user = new User(1L, "Jane Doe", "jane@example.com");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.createUser("Jane Doe", "jane@example.com");

        // Assert - Verify interactions
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendWelcomeEmail(anyString());
        verify(userRepository, never()).delete(anyLong());
    }

    @Test
    @DisplayName("Verify Method Call with Specific Arguments")
    void testVerifyWithArguments() {
        // Arrange
        User user = new User(1L, "Test User", "test@example.com");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.createUser("Test User", "test@example.com");

        // Assert - Verify with argument matchers
        verify(userRepository).save(argThat(u -> 
            u.getName().equals("Test User") && 
            u.getEmail().equals("test@example.com")
        ));
    }

    @Test
    @DisplayName("Verify No Interactions")
    void testVerifyNoInteractions() {
        // Act
        userService.findUserById(1L);

        // Assert
        verifyNoInteractions(emailService);
    }

    // ===================================================================
    // ARGUMENT MATCHERS
    // ===================================================================

    @Test
    @DisplayName("Argument Matchers - any(), anyString(), etc.")
    void testArgumentMatchers() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(new User(1L, "Test", "test@test.com"));

        // Act
        userService.findUserByEmail("any@email.com");
        userService.createUser("Any Name", "any@email.com");

        // Assert
        verify(userRepository).findByEmail(anyString());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Argument Matchers - Custom Matcher")
    void testCustomArgumentMatcher() {
        // Arrange
        when(userRepository.save(argThat(user -> 
            user.getName().length() > 5
        ))).thenReturn(new User(1L, "Long Name", "test@test.com"));

        // Act
        User result = userService.createUser("Long Name User", "test@test.com");

        // Assert
        assertNotNull(result);
        verify(userRepository).save(argThat(user -> user.getName().length() > 5));
    }

    // ===================================================================
    // STUBBING WITH DIFFERENT CALLS
    // ===================================================================

    @Test
    @DisplayName("Stub - Multiple Return Values")
    void testMultipleReturnValues() {
        // Arrange - Return different values on consecutive calls
        when(userRepository.count())
            .thenReturn(0L)
            .thenReturn(1L)
            .thenReturn(2L);

        // Act & Assert
        assertEquals(0L, userRepository.count());
        assertEquals(1L, userRepository.count());
        assertEquals(2L, userRepository.count());
    }

    @Test
    @DisplayName("Stub - Call Real Method")
    void testCallRealMethod() {
        // Arrange
        UserRepository realRepository = new InMemoryUserRepository();
        UserService realService = new UserService(realRepository, emailService);

        // Create a spy (partial mock)
        UserService spyService = spy(realService);
        doReturn(Optional.empty()).when(spyService).findUserById(999L);

        // Act
        Optional<User> result = spyService.findUserById(999L);

        // Assert
        assertFalse(result.isPresent());
    }

    // ===================================================================
    // ARGUMENT CAPTURE
    // ===================================================================

    @Test
    @DisplayName("Capture Arguments")
    void testCaptureArguments() {
        // Arrange
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(any(User.class)))
            .thenReturn(new User(1L, "Captured", "captured@test.com"));

        // Act
        userService.createUser("John Doe", "john@example.com");

        // Assert - Capture the argument passed to save()
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        
        assertEquals("John Doe", capturedUser.getName());
        assertEquals("john@example.com", capturedUser.getEmail());
    }

    @Test
    @DisplayName("Capture Multiple Arguments")
    void testCaptureMultipleArguments() {
        // Arrange
        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        when(userRepository.save(any(User.class))).thenReturn(new User(1L, "Test", "test@test.com"));

        // Act
        userService.createUser("User 1", "user1@test.com");
        userService.createUser("User 2", "user2@test.com");

        // Assert - Capture all calls
        verify(emailService, times(2)).sendWelcomeEmail(emailCaptor.capture());
        List<String> capturedEmails = emailCaptor.getAllValues();
        
        assertEquals(2, capturedEmails.size());
        assertTrue(capturedEmails.contains("user1@test.com"));
        assertTrue(capturedEmails.contains("user2@test.com"));
    }

    // ===================================================================
    // SPY (Partial Mock)
    // ===================================================================

    @Test
    @DisplayName("Spy - Partial Mock")
    void testSpy() {
        // Arrange - Create a spy of a real object
        List<String> list = new java.util.ArrayList<>();
        List<String> spyList = spy(list);

        // Act - Real method is called
        spyList.add("one");
        spyList.add("two");

        // Stub a method
        when(spyList.size()).thenReturn(100);

        // Assert
        assertEquals(100, spyList.size()); // Stubbed
        assertTrue(spyList.contains("one")); // Real method
        assertTrue(spyList.contains("two")); // Real method
    }

    // ===================================================================
    // RESET MOCK
    // ===================================================================

    @Test
    @DisplayName("Reset Mock")
    void testResetMock() {
        // Arrange
        when(userRepository.count()).thenReturn(10L);
        assertEquals(10L, userRepository.count());

        // Reset the mock
        reset(userRepository);

        // After reset, returns default value (0 for Long)
        assertEquals(0L, userRepository.count());
    }

    // ===================================================================
    // ANSWER INTERFACE (Custom Behavior)
    // ===================================================================

    @Test
    @DisplayName("Custom Answer")
    void testCustomAnswer() {
        // Arrange - Custom behavior using Answer
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(999L); // Set ID before returning
            return user;
        });

        // Act
        User result = userService.createUser("Test User", "test@test.com");

        // Assert
        assertNotNull(result);
        assertEquals(999L, result.getId());
    }

    // ===================================================================
    // IN-ORDER VERIFICATION
    // ===================================================================

    @Test
    @DisplayName("Verify In Order")
    void testVerifyInOrder() {
        // Arrange
        InOrder inOrder = inOrder(userRepository, emailService);
        when(userRepository.save(any(User.class)))
            .thenReturn(new User(1L, "Test", "test@test.com"));

        // Act
        userService.createUser("Test User", "test@test.com");

        // Assert - Verify order of method calls
        inOrder.verify(userRepository).save(any(User.class));
        inOrder.verify(emailService).sendWelcomeEmail(anyString());
    }

    // ===================================================================
    // HELPER CLASSES
    // ===================================================================

    // Simple User entity
    static class User {
        private Long id;
        private String name;
        private String email;

        public User(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    // Repository interface
    interface UserRepository {
        Optional<User> findById(Long id);
        Optional<User> findByEmail(String email);
        User save(User user);
        void delete(Long id);
        long count();
    }

    // Service class
    static class UserService {
        private final UserRepository userRepository;
        private final EmailService emailService;

        public UserService(UserRepository userRepository, EmailService emailService) {
            this.userRepository = userRepository;
            this.emailService = emailService;
        }

        public Optional<User> findUserById(Long id) {
            return userRepository.findById(id);
        }

        public Optional<User> findUserByEmail(String email) {
            return userRepository.findByEmail(email);
        }

        public User createUser(String name, String email) {
            User user = new User(null, name, email);
            User savedUser = userRepository.save(user);
            emailService.sendWelcomeEmail(savedUser.getEmail());
            return savedUser;
        }
    }

    // Email service interface
    interface EmailService {
        void sendWelcomeEmail(String email);
    }

    // In-memory implementation for testing
    static class InMemoryUserRepository implements UserRepository {
        private final java.util.Map<Long, User> users = new java.util.HashMap<>();
        private long nextId = 1;

        @Override
        public Optional<User> findById(Long id) {
            return Optional.ofNullable(users.get(id));
        }

        @Override
        public Optional<User> findByEmail(String email) {
            return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
        }

        @Override
        public User save(User user) {
            if (user.getId() == null) {
                user.setId(nextId++);
            }
            users.put(user.getId(), user);
            return user;
        }

        @Override
        public void delete(Long id) {
            users.remove(id);
        }

        @Override
        public long count() {
            return users.size();
        }
    }
}

