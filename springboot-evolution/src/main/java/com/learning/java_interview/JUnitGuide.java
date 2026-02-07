package com.learning.java_interview;

/**
 * ===================================================================
 * JUNIT COMPREHENSIVE GUIDE
 * ===================================================================
 * 
 * JUnit is the most popular unit testing framework for Java.
 * JUnit 5 (Jupiter) is the current version, replacing JUnit 4.
 * 
 * ===================================================================
 * CORE CONCEPTS
 * ===================================================================
 * 
 * 1. TEST METHOD: A method annotated with @Test that contains test logic
 * 2. ASSERTION: Statements that verify expected behavior
 * 3. TEST FIXTURE: Setup and teardown methods (@BeforeEach, @AfterEach)
 * 4. TEST SUITE: Group of tests run together
 * 5. TEST RUNNER: Executes tests and reports results
 * 
 * ===================================================================
 * JUNIT 5 ANNOTATIONS
 * ===================================================================
 * 
 * @Test - Marks a method as a test method
 * @BeforeEach - Executes before each test method
 * @AfterEach - Executes after each test method
 * @BeforeAll - Executes once before all tests (static method)
 * @AfterAll - Executes once after all tests (static method)
 * @DisplayName - Custom display name for test
 * @Disabled - Skips a test
 * @RepeatedTest - Repeats a test N times
 * @ParameterizedTest - Runs test with different parameters
 * @Tag - Groups tests for filtering
 * @Nested - Groups related tests together
 * @Timeout - Fails if test exceeds time limit
 * 
 * ===================================================================
 * ASSERTIONS
 * ===================================================================
 * 
 * assertEquals(expected, actual) - Checks equality
 * assertNotEquals(expected, actual) - Checks inequality
 * assertTrue(condition) - Checks true condition
 * assertFalse(condition) - Checks false condition
 * assertNull(object) - Checks null
 * assertNotNull(object) - Checks not null
 * assertSame(expected, actual) - Checks same reference
 * assertNotSame(expected, actual) - Checks different reference
 * assertArrayEquals(expected, actual) - Checks array equality
 * assertThrows(Exception.class, () -> {}) - Checks exception thrown
 * assertDoesNotThrow(() -> {}) - Checks no exception
 * assertAll() - Groups multiple assertions (all run even if one fails)
 * assertIterableEquals() - Checks iterable equality
 * 
 * ===================================================================
 * ASSUMPTIONS
 * ===================================================================
 * 
 * assumeTrue(condition) - Skip test if condition is false
 * assumeFalse(condition) - Skip test if condition is true
 * assumingThat(condition, () -> {}) - Conditional execution
 * 
 * ===================================================================
 * TESTING PATTERNS
 * ===================================================================
 * 
 * 1. AAA Pattern (Arrange-Act-Assert)
 *    - Arrange: Set up test data
 *    - Act: Execute the method under test
 *    - Assert: Verify the results
 * 
 * 2. Given-When-Then Pattern
 *    - Given: Initial state
 *    - When: Action performed
 *    - Then: Expected outcome
 * 
 * 3. Test Doubles (Mocks, Stubs, Spies)
 *    - Mock: Fake object with predefined behavior
 *    - Stub: Provides canned responses
 *    - Spy: Partial mock, wraps real object
 * 
 * ===================================================================
 * BEST PRACTICES
 * ===================================================================
 * 
 * 1. Test method names should be descriptive
 * 2. One assertion per test (when possible)
 * 3. Test one thing at a time
 * 4. Use @BeforeEach for common setup
 * 5. Keep tests independent (no order dependency)
 * 6. Use meaningful assertion messages
 * 7. Test both positive and negative cases
 * 8. Test edge cases and boundary conditions
 * 9. Keep tests fast (avoid I/O, network calls)
 * 10. Use mocking for external dependencies
 * 
 * ===================================================================
 * COMMON TESTING SCENARIOS
 * ===================================================================
 * 
 * 1. Unit Tests - Test individual methods/classes
 * 2. Integration Tests - Test component interactions
 * 3. Parameterized Tests - Test with multiple inputs
 * 4. Exception Tests - Verify exception handling
 * 5. Performance Tests - Verify execution time
 * 6. Conditional Tests - Skip based on conditions
 * 
 * ===================================================================
 */

public class JUnitGuide {
    // This is a documentation/guide class
    // See JUnitExamplesTest.java for practical examples
}

