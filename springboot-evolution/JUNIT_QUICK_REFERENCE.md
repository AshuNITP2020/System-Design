# JUnit 5 Quick Reference Guide

## Overview
This guide provides comprehensive JUnit 5 examples with different testing variations.

## Files Created

1. **JUnitGuide.java** - Comprehensive documentation of JUnit concepts
2. **JUnitExamplesTest.java** - Practical examples of JUnit 5 features
3. **JUnitMockingExamplesTest.java** - Mockito integration examples
4. **JUnitSpringBootExamplesTest.java** - Spring Boot testing examples

## Running Tests

### Run All Tests
```bash
./gradlew test
```

### Run Specific Test Class
```bash
./gradlew test --tests JUnitExamplesTest
```

### Run Tests with Specific Tag
```bash
./gradlew test --tests "*" -PincludeTags=fast
```

### Run Tests in IDE
- Right-click on test class → Run
- Or use keyboard shortcut (Ctrl+Shift+F10 / Cmd+Shift+R)

## Key JUnit 5 Features Demonstrated

### 1. Basic Annotations
- `@Test` - Mark test methods
- `@BeforeEach` / `@AfterEach` - Setup/teardown
- `@BeforeAll` / `@AfterAll` - Class-level setup/teardown
- `@DisplayName` - Custom test names
- `@Disabled` - Skip tests

### 2. Assertions
- `assertEquals()` - Check equality
- `assertTrue()` / `assertFalse()` - Boolean checks
- `assertNull()` / `assertNotNull()` - Null checks
- `assertThrows()` - Exception testing
- `assertAll()` - Grouped assertions

### 3. Advanced Features
- `@ParameterizedTest` - Test with multiple inputs
- `@RepeatedTest` - Repeat tests
- `@Timeout` - Time-based testing
- `@Nested` - Group related tests
- `@Tag` - Categorize tests
- Dynamic Tests - Generate tests at runtime

### 4. Conditional Execution
- `@EnabledOnOs` - OS-specific tests
- `@DisabledOnJre` - JRE-specific tests
- `assumeTrue()` - Conditional skip

### 5. Mockito Integration
- `@Mock` - Create mocks
- `@InjectMocks` - Inject mocks
- `when().thenReturn()` - Stub behavior
- `verify()` - Verify interactions
- `ArgumentCaptor` - Capture arguments
- `@Spy` - Partial mocks

### 6. Spring Boot Testing
- `@SpringBootTest` - Full integration test
- `@WebMvcTest` - Controller testing
- `@DataJpaTest` - Repository testing
- `@MockBean` - Mock Spring beans
- `MockMvc` - Web layer testing

## Test Patterns

### AAA Pattern (Arrange-Act-Assert)
```java
@Test
void testExample() {
    // Arrange
    Calculator calc = new Calculator();
    
    // Act
    int result = calc.add(2, 3);
    
    // Assert
    assertEquals(5, result);
}
```

### Given-When-Then Pattern
```java
@Test
void testExample() {
    // Given
    User user = new User("John");
    
    // When
    user.setName("Jane");
    
    // Then
    assertEquals("Jane", user.getName());
}
```

## Best Practices

1. **Test Naming**: Use descriptive names (`testAddTwoPositiveNumbers`)
2. **One Assertion**: Test one thing per test method
3. **Independence**: Tests should not depend on each other
4. **Fast Tests**: Keep tests fast (avoid I/O, network)
5. **Mock External Dependencies**: Use mocks for databases, APIs, etc.
6. **Test Edge Cases**: Test boundaries, null values, empty collections
7. **Use Meaningful Messages**: Provide clear assertion messages
8. **Keep Tests Simple**: Easy to read and understand

## Common Test Scenarios

### Testing Exceptions
```java
@Test
void testException() {
    assertThrows(IllegalArgumentException.class, () -> {
        calculator.divide(10, 0);
    });
}
```

### Parameterized Testing
```java
@ParameterizedTest
@ValueSource(ints = {2, 4, 6, 8})
void testEvenNumbers(int number) {
    assertTrue(calculator.isEven(number));
}
```

### Mocking
```java
@Mock
private UserRepository repository;

@Test
void testWithMock() {
    when(repository.findById(1L)).thenReturn(Optional.of(user));
    verify(repository).findById(1L);
}
```

## Test Organization

```
src/test/java/
  ├── com/learning/
  │   ├── unit/          # Unit tests
  │   ├── integration/   # Integration tests
  │   └── e2e/           # End-to-end tests
```

## Tags for Test Filtering

```java
@Test
@Tag("fast")
void fastTest() { }

@Test
@Tag("slow")
void slowTest() { }

@Test
@Tag("integration")
void integrationTest() { }
```

Run: `./gradlew test -PincludeTags=fast`

## Resources

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)

## Example Test Execution

```bash
# Run all tests
./gradlew test

# Run with verbose output
./gradlew test --info

# Run specific test class
./gradlew test --tests JUnitExamplesTest

# Run tests matching pattern
./gradlew test --tests "*Mocking*"

# Generate test report
./gradlew test
# Report location: build/reports/tests/test/index.html
```

