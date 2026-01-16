🚀 Top Spring Boot Interview Questions (Most Asked)

If you’re preparing for Spring Boot interviews or revising core concepts, save this post 🔖

🔹 Core Spring Boot

1️⃣ What is Spring Boot and how is it different from Spring Framework?
2️⃣ What are the main features of Spring Boot?
3️⃣ What is auto-configuration in Spring Boot?
4️⃣ What is a starter dependency? Name some common starters.
5️⃣ What is the role of @SpringBootApplication?

🔹 Configuration & Annotations

6️⃣ Difference between @Component, @Service, and @Repository
7️⃣ What is @ConfigurationProperties vs @Value?
8️⃣ How does application.properties differ from application.yml?
9️⃣ What is profile-based configuration?
🔟 What is Spring Boot Actuator?

🔹 REST & Web

1️⃣1️⃣ Difference between @Controller and @RestController
1️⃣2️⃣ What is @RequestMapping vs @GetMapping?
1️⃣3️⃣ How do you handle exception handling globally?
1️⃣4️⃣ What is ResponseEntity and why is it used?
1️⃣5️⃣ How does Spring Boot handle JSON serialization/deserialization?

🔹 Data & JPA

1️⃣6️⃣ Difference between JpaRepository and CrudRepository
1️⃣7️⃣ What is Hibernate? How does it work with Spring Boot?
1️⃣8️⃣ What is Lazy vs Eager loading?
1️⃣9️⃣ What is N+1 query problem?
2️⃣0️⃣ How do you enable transaction management?

🔹 Microservices & Production

2️⃣1️⃣ How do you externalize configuration in Spring Boot?
2️⃣2️⃣ What is Spring Boot DevTools?
2️⃣3️⃣ How do you secure a Spring Boot application?
2️⃣4️⃣ What is caching and how is it implemented?
2️⃣5️⃣ How do you debug performance issues in production?


# 🚀 Complete Spring Boot Interview Questions & Answers

> **Comprehensive guide covering 50 most asked Spring Boot interview questions**
> 
> This document covers everything from basic concepts to advanced topics, perfect for interview preparation.

---

## 📋 Table of Contents

- [Core Spring Boot (1-5)](#core-spring-boot-1-5)
- [Configuration & Annotations (6-10)](#configuration--annotations-6-10)
- [REST & Web (11-15)](#rest--web-11-15)
- [Data & JPA (16-20)](#data--jpa-16-20)
- [Microservices & Production (21-25)](#microservices--production-21-25)
- [Core Spring Framework (26-31)](#core-spring-framework-26-31)
- [AOP (32-34)](#aop-32-34)
- [Testing (35-36)](#testing-35-36)
- [Advanced Spring Boot (37-40)](#advanced-spring-boot-37-40)
- [Transaction Management (41-43)](#transaction-management-41-43)
- [Spring Security (44-46)](#spring-security-44-46)
- [Spring MVC Internals (47-48)](#spring-mvc-internals-47-48)
- [Error Handling (49)](#error-handling-49)
- [Performance & Optimization (50)](#performance--optimization-50)

---

## 🔹 Core Spring Boot (1-5)

### 1️⃣ What is Spring Boot and how is it different from Spring Framework?

**Spring Boot** is an opinionated framework built on top of Spring Framework that simplifies development by providing:
- **Convention over configuration** - Sensible defaults
- **Auto-configuration** - Automatic setup based on classpath
- **Embedded servers** - No external server needed
- **Production-ready features** - Actuator, metrics, etc.

**Key Differences:**

| Aspect | Spring Framework | Spring Boot |
|--------|------------------|-------------|
| **Configuration** | Manual (XML/Java Config, 100+ lines) | Auto-configured (minimal config) |
| **Server** | External Tomcat (manual setup) | Embedded Tomcat (included) |
| **Dependencies** | Manual version management | Starters (pre-bundled, versioned) |
| **Deployment** | WAR → external server | JAR (standalone, `java -jar`) |
| **Setup Time** | Hours/days | Minutes |

**Example:**
```java
// Spring Framework: 50+ lines of XML/Java config
// Spring Boot: Just this:
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
```

---

### 2️⃣ What are the main features of Spring Boot?

1. **Auto-Configuration**
   - Automatically configures beans based on classpath dependencies
   - Example: `spring-boot-starter-web` → auto-configures DispatcherServlet, Tomcat, Jackson

2. **Starter Dependencies**
   - Pre-bundled dependency sets (e.g., `spring-boot-starter-web`, `spring-boot-starter-data-jpa`)

3. **Embedded Server**
   - Tomcat/Jetty/Undertow included; run as standalone JAR

4. **Production-Ready Features**
   - Actuator (health, metrics, monitoring)
   - Externalized configuration
   - Logging defaults

5. **No Code Generation**
   - No XML configuration required
   - Pure Java-based configuration

6. **DevTools**
   - Auto-restart on code changes
   - LiveReload support

---

### 3️⃣ What is auto-configuration in Spring Boot?

Auto-configuration automatically configures beans based on:
- Dependencies on the classpath
- Properties in `application.properties`
- Existing bean definitions you provide

**How it works:**
1. Spring Boot scans for `@EnableAutoConfiguration` (included in `@SpringBootApplication`)
2. Reads `META-INF/spring.factories` from starter JARs
3. Checks conditions (e.g., `@ConditionalOnClass`, `@ConditionalOnMissingBean`)
4. Creates beans if conditions are met

**Example:**
```java
// If H2 is on classpath → Auto-configure H2 DataSource
// If JPA is on classpath → Auto-configure EntityManagerFactory
// If spring-boot-starter-web → Auto-configure DispatcherServlet
```

**To see what's auto-configured:**
```properties
# application.properties
debug=true
```
This logs positive/negative auto-configuration matches.

**Customization:**
- Override with your own `@Bean` definitions
- Use `application.properties` to customize
- Exclude: `@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})`

---

### 4️⃣ What is a starter dependency? Name some common starters.

A **starter dependency** is a pre-bundled set of dependencies with compatible versions.

**Benefits:**
- No manual version management
- Tested compatibility
- One dependency brings in multiple related libraries

**Common Starters:**

1. **`spring-boot-starter-web`**
   - Spring MVC, Tomcat, Jackson, Validation
   - For REST APIs and web applications

2. **`spring-boot-starter-data-jpa`**
   - JPA, Hibernate, Transaction Management
   - For database operations

3. **`spring-boot-starter-data-jdbc`**
   - Spring JDBC (without JPA/Hibernate)

4. **`spring-boot-starter-security`**
   - Spring Security

5. **`spring-boot-starter-test`**
   - JUnit, Mockito, AssertJ, Spring Test

6. **`spring-boot-starter-validation`**
   - Bean Validation (Hibernate Validator)

7. **`spring-boot-starter-actuator`**
   - Production monitoring and management endpoints

8. **`spring-boot-starter-cache`**
   - Spring Cache abstraction

9. **`spring-boot-starter-mail`**
   - JavaMail for email

10. **`spring-boot-starter-thymeleaf`**
    - Thymeleaf templating engine

**Example:**
```gradle
implementation 'org.springframework.boot:spring-boot-starter-web'
// This one line includes: Spring MVC, Tomcat, Jackson, Validation, etc.
```

---

### 5️⃣ What is the role of @SpringBootApplication?

`@SpringBootApplication` is a meta-annotation that combines three annotations:

```java
@SpringBootApplication
// Is equivalent to:
@Configuration
@EnableAutoConfiguration
@ComponentScan
```

**Breakdown:**

1. **`@Configuration`**
   - Marks the class as a source of bean definitions
   - Replaces XML configuration files
   - Allows `@Bean` methods

2. **`@EnableAutoConfiguration`**
   - Enables auto-configuration
   - Configures beans based on classpath dependencies
   - Core of Spring Boot's "magic"

3. **`@ComponentScan`**
   - Scans for `@Component`, `@Service`, `@Repository`, `@Controller`
   - Scans from the package of the annotated class and sub-packages
   - Replaces XML's `<context:component-scan>`

**Usage:**
```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

**What happens:**
1. Creates `ApplicationContext`
2. Auto-configures based on classpath
3. Scans for components
4. Wires dependencies
5. Starts embedded server
6. Application is ready!

**Customization:**
```java
@SpringBootApplication(
    scanBasePackages = "com.example",  // Custom scan path
    exclude = {DataSourceAutoConfiguration.class}  // Exclude auto-config
)
```

---

## 🔹 Configuration & Annotations (6-10)

### 6️⃣ Difference between @Component, @Service, and @Repository

All three are stereotype annotations that mark classes as Spring beans. Functionally, they are **equivalent** - the differences are **semantic** and for tooling.

**Technical Equivalence:**
All three are meta-annotated with `@Component`:

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component  // ← All extend @Component!
public @interface Service { }

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component  // ← All extend @Component!
public @interface Repository { }
```

**Differences:**

| Annotation | Purpose | Special Behavior | When to Use |
|------------|---------|------------------|-------------|
| **`@Component`** | Generic Spring bean | None | Utility classes, helpers, generic components |
| **`@Service`** | Business logic layer | None (semantic only) | Service classes containing business logic |
| **`@Repository`** | Data access layer | Exception translation (DB exceptions → Spring DataAccessException) | DAO/Repository classes that access databases |

**Example:**
```java
// Generic component
@Component
class GenericBean {
    // Utility or helper class
}

// Business logic layer
@Service
@Transactional
public class BookService {
    // Contains business logic
    // Orchestrates operations
}

// Data access layer
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // Database operations
    // Exceptions automatically translated
}
```

**Why use different annotations?**
1. **Code clarity** - Indicates the layer/role
2. **Tooling** - IDEs and tools can categorize
3. **AOP** - Easier to target specific layers
4. **`@Repository`** adds exception translation for data access

**Note:** You can use `@Component` everywhere, but using `@Service` and `@Repository` improves readability and maintainability.

---

### 7️⃣ What is @ConfigurationProperties vs @Value?

Both inject values from `application.properties`, but they serve different use cases.

**`@Value` - Single Property Injection:**

```java
@Component
public class AppConfig {
    
    @Value("${server.port}")
    private int serverPort;
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration:86400000}")  // Default value
    private long jwtExpiration;
    
    @Value("${app.name:MyApp}")  // Default if not found
    private String appName;
}
```

**Characteristics:**
- Simple, direct injection
- Good for single properties
- Supports SpEL: `@Value("#{systemProperties['user.name']}")`
- Supports defaults: `@Value("${prop:defaultValue}")`

**Limitations:**
- Verbose for many properties
- No type safety or validation
- No IDE autocomplete for property names

**`@ConfigurationProperties` - Type-Safe Configuration Binding:**

```java
@ConfigurationProperties(prefix = "app")
@Component  // or @Configuration
public class AppProperties {
    
    private String name;
    private String version;
    private Database database;
    private Security security;
    
    // Getters and setters required
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    // Nested properties
    public static class Database {
        private String url;
        private String username;
        private String password;
        
        // getters/setters
    }
    
    public static class Security {
        private String secret;
        private long expiration;
        
        // getters/setters
    }
}
```

**application.properties:**
```properties
app.name=MyApplication
app.version=1.0.0
app.database.url=jdbc:h2:mem:testdb
app.database.username=sa
app.database.password=
app.security.secret=mySecretKey
app.security.expiration=86400000
```

**Characteristics:**
- Type-safe binding
- Groups related properties
- Supports nested properties
- Validation with `@Validated` and `@NotNull`, `@Min`, etc.
- IDE autocomplete support (with annotation processor)

**Comparison:**

| Feature | `@Value` | `@ConfigurationProperties` |
|---------|----------|----------------------------|
| **Use Case** | Single properties | Multiple related properties |
| **Type Safety** | Manual casting | Automatic |
| **Nested Properties** | Not supported | Supported |
| **Validation** | Manual | Built-in with `@Validated` |
| **IDE Support** | Limited | Full autocomplete |
| **Reload** | No | Yes (with `@RefreshScope`) |
| **SpEL Support** | Yes | No |

**When to use which?**

**Use `@Value`:**
- Single, unrelated properties
- Need SpEL expressions
- Simple configuration

**Use `@ConfigurationProperties`:**
- Multiple related properties
- Need type safety
- Want validation
- Building configuration classes

---

### 8️⃣ How does application.properties differ from application.yml?

Both configure Spring Boot applications. Differences:

**Format:**

**application.properties (Key-Value):**
```properties
server.port=8080
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

**application.yml (YAML - Hierarchical):**
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

**Comparison:**

| Aspect | application.properties | application.yml |
|--------|------------------------|----------------|
| **Syntax** | Key-value pairs | YAML (hierarchical) |
| **Readability** | Good for simple config | Better for nested config |
| **Hierarchy** | Uses dots (`.`) | Uses indentation |
| **Lists/Arrays** | Comma-separated | Native YAML arrays |
| **Comments** | `#` | `#` |
| **File Size** | More verbose for nested | More concise |
| **Error-Prone** | Less (simple syntax) | More (indentation matters) |
| **IDE Support** | Good | Excellent (syntax highlighting) |

**Examples:**

**Lists/Arrays:**
```properties
# application.properties
app.servers=server1,server2,server3
app.ports=8080,8081,8082
```

```yaml
# application.yml
app:
  servers:
    - server1
    - server2
    - server3
  ports:
    - 8080
    - 8081
    - 8082
```

**Priority:**
If both files exist, `application.properties` takes precedence over `application.yml`.

**When to use which?**

**Use `.properties`:**
- Simple configuration
- Prefer explicit syntax
- Team familiar with properties format

**Use `.yml`:**
- Complex, nested configuration
- Want cleaner, more readable config
- Working with microservices (common in cloud config)

---

### 9️⃣ What is profile-based configuration?

Profiles allow you to use different configurations for different environments (dev, test, prod) without changing code.

**Problem it solves:**
Different environments need different settings:
- Dev: H2 in-memory DB, debug logging, H2 console enabled
- Test: H2 in-memory DB, minimal logging
- Prod: PostgreSQL, production logging, security enabled

**How it works:**

**1. Create profile-specific files:**
```
src/main/resources/
  ├── application.properties          (default/base config)
  ├── application-dev.properties       (dev profile)
  ├── application-test.properties      (test profile)
  └── application-prod.properties     (prod profile)
```

**2. Define properties per profile:**

**application.properties (base):**
```properties
# Common configuration
app.name=MyApplication
app.version=1.0.0
```

**application-dev.properties:**
```properties
# Development profile
spring.datasource.url=jdbc:h2:mem:devdb
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.h2.console.enabled=true
logging.level.root=DEBUG
```

**application-prod.properties:**
```properties
# Production profile
spring.datasource.url=jdbc:postgresql://prod-db:5432/mydb
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.h2.console.enabled=false
logging.level.root=INFO
```

**3. Activate profile:**

**Option A: application.properties**
```properties
spring.profiles.active=dev
```

**Option B: Environment variable**
```bash
export SPRING_PROFILES_ACTIVE=prod
```

**Option C: Command line**
```bash
java -jar app.jar --spring.profiles.active=prod
```

**Option D: Programmatically**
```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.setAdditionalProfiles("prod");
        app.run(args);
    }
}
```

**Multiple profiles:**
```properties
spring.profiles.active=dev,debug,feature-x
```

**Profile-specific beans:**
```java
@Configuration
@Profile("dev")
public class DevConfig {
    @Bean
    public DataSource devDataSource() {
        // Dev-specific DataSource
        return new H2DataSource();
    }
}

@Configuration
@Profile("prod")
public class ProdConfig {
    @Bean
    public DataSource prodDataSource() {
        // Prod-specific DataSource
        return new PostgreSQLDataSource();
    }
}
```

**Benefits:**
- Environment-specific configuration
- No code changes between environments
- Easy to test different configurations
- Security: sensitive data in profile-specific files

---

### 🔟 What is Spring Boot Actuator?

Spring Boot Actuator provides production-ready monitoring and management endpoints for your application.

**What it provides:**
1. Health checks
2. Metrics (memory, CPU, custom metrics)
3. Application info
4. Environment details
5. Logging management
6. Thread dumps
7. HTTP traces

**Setup:**

**1. Add dependency:**
```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
}
```

**2. Configure endpoints (application.properties):**
```properties
# Expose all endpoints
management.endpoints.web.exposure.include=*

# Or specific endpoints
management.endpoints.web.exposure.include=health,info,metrics

# Change base path (default is /actuator)
management.endpoints.web.base-path=/monitor

# Enable specific endpoints
management.endpoint.health.enabled=true
management.endpoint.info.enabled=true
management.endpoint.metrics.enabled=true
```

**Common Endpoints:**

| Endpoint | URL | Description |
|----------|-----|-------------|
| **Health** | `/actuator/health` | Application health status |
| **Info** | `/actuator/info` | Application information |
| **Metrics** | `/actuator/metrics` | Application metrics |
| **Env** | `/actuator/env` | Environment variables |
| **Beans** | `/actuator/beans` | All Spring beans |
| **Mappings** | `/actuator/mappings` | All HTTP mappings |
| **Loggers** | `/actuator/loggers` | Logging configuration |

**Examples:**

**1. Health Check:**
```bash
GET /actuator/health
```
Response:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "H2",
        "validationQuery": "isValid()"
      }
    }
  }
}
```

**2. Metrics:**
```bash
GET /actuator/metrics
GET /actuator/metrics/jvm.memory.used
GET /actuator/metrics/http.server.requests
```

**3. Custom Health Indicator:**
```java
@Component
public class CustomHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        // Check your custom condition
        if (isServiceHealthy()) {
            return Health.up()
                .withDetail("service", "Available")
                .build();
        }
        return Health.down()
            .withDetail("service", "Unavailable")
            .build();
    }
}
```

**4. Custom Info:**
```properties
# application.properties
info.app.name=My Application
info.app.version=1.0.0
info.app.description=Spring Boot Learning App
```

**Security Considerations:**

**For Production:**
```properties
# Expose only necessary endpoints
management.endpoints.web.exposure.include=health,info

# Secure sensitive endpoints
management.endpoint.env.enabled=false
management.endpoint.beans.enabled=false

# Add security (requires Spring Security)
management.endpoints.web.exposure.exclude=*
management.endpoint.health.show-details=when-authorized
```

**Benefits:**
- Production monitoring
- Health checks for load balancers
- Metrics for monitoring tools (Prometheus, Grafana)
- Debugging in production
- Operational insights

---

## 🔹 REST & Web (11-15)

### 1️⃣1️⃣ Difference between @Controller and @RestController

Both handle HTTP requests, but they differ in what they return.

**@Controller (Traditional MVC):**
Used for server-side rendering (HTML pages, JSP, Thymeleaf).

```java
@Controller
public class BookController {
    
    @GetMapping("/books")
    public String showBooks(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);  // Add data to model
        return "books";  // ← Returns VIEW NAME (not data!)
    }                    // ViewResolver finds books.jsp
}
```

**Characteristics:**
- Returns view name (String) → ViewResolver renders HTML
- Used with JSP, Thymeleaf, FreeMarker
- Requires `@ResponseBody` on methods to return JSON
- Flow: Request → Controller → Model → View → HTML Response

**@RestController (REST API):**
Used for REST APIs that return JSON/XML.

```java
@RestController  // = @Controller + @ResponseBody
@RequestMapping("/api/books")
public class BookController {
    
    @GetMapping
    public List<BookDTO> getAllBooks() {
        return bookService.getAllBooks();  // ← Returns DATA directly
    }                                       // Jackson converts to JSON
}
```

**Characteristics:**
- Returns data directly (objects) → Jackson converts to JSON
- All methods automatically have `@ResponseBody`
- No view rendering
- Flow: Request → Controller → Model → JSON Response

**Technical Difference:**
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
@ResponseBody  // ← This is the key difference!
public @interface RestController {
}
```

**`@RestController` = `@Controller` + `@ResponseBody`**

**When to use which?**

| Use Case | Annotation |
|----------|-----------|
| REST API returning JSON/XML | `@RestController` |
| Server-side HTML pages | `@Controller` |
| Mixed (some HTML, some JSON) | `@Controller` + `@ResponseBody` on JSON methods |

---

### 1️⃣2️⃣ What is @RequestMapping vs @GetMapping?

Both map HTTP requests to controller methods, but `@GetMapping` is a shortcut for `@RequestMapping` with `method = RequestMethod.GET`.

**@RequestMapping (Generic):**
Can specify any HTTP method and additional attributes.

```java
@RestController
@RequestMapping("/api/books")  // Base path for all methods
public class BookController {
    
    // Method 1: Specify method explicitly
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public BookDTO getBook(@PathVariable Long id) {
        return bookService.getBook(id);
    }
    
    // Method 2: Multiple methods
    @RequestMapping(value = "/{id}", method = {RequestMethod.GET, RequestMethod.POST})
    public BookDTO handleBook(@PathVariable Long id) {
        // Handles both GET and POST
    }
    
    // Method 3: Multiple paths
    @RequestMapping(value = {"/book", "/item"}, method = RequestMethod.GET)
    public BookDTO getBook() {
        // Handles both /book and /item
    }
    
    // Method 4: With headers
    @RequestMapping(value = "/data", 
                   method = RequestMethod.GET,
                   headers = "X-API-Version=1")
    public BookDTO getBook() {
        // Only matches if header is present
    }
}
```

**@GetMapping (Shortcut):**
Shorthand for `@RequestMapping(method = RequestMethod.GET)`.

```java
@RestController
@RequestMapping("/api/books")
public class BookController {
    
    // These are equivalent:
    
    // Using @RequestMapping
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public BookDTO getBook(@PathVariable Long id) { }
    
    // Using @GetMapping (shorter!)
    @GetMapping("/{id}")
    public BookDTO getBook(@PathVariable Long id) { }
}
```

**HTTP Method Shortcuts:**
Spring provides shortcuts for common HTTP methods:

```java
@GetMapping("/books")        // GET
@PostMapping("/books")       // POST
@PutMapping("/books/{id}")  // PUT
@DeleteMapping("/books/{id}") // DELETE
@PatchMapping("/books/{id}")  // PATCH
```

All are equivalent to:
```java
@RequestMapping(value = "/books", method = RequestMethod.GET)
@RequestMapping(value = "/books", method = RequestMethod.POST)
// etc.
```

**Comparison:**

| Feature | `@RequestMapping` | `@GetMapping` |
|---------|-------------------|---------------|
| **HTTP Method** | Must specify `method = ...` | Automatically GET |
| **Readability** | More verbose | Cleaner, more readable |
| **Flexibility** | Can handle multiple methods | Single method only |
| **Common Use** | When you need flexibility | Most common for REST APIs |

**Best Practices:**

**Use `@GetMapping`, `@PostMapping`, etc. when:**
- You know the HTTP method
- You want cleaner, more readable code
- Following REST conventions

**Use `@RequestMapping` when:**
- You need to handle multiple HTTP methods
- You need advanced features (headers, params, consumes, produces)
- You're defining a base path at class level

---

### 1️⃣3️⃣ How do you handle exception handling globally?

Use `@RestControllerAdvice` (or `@ControllerAdvice`) with `@ExceptionHandler` to handle exceptions across all controllers.

**Approach 1: Simple @ExceptionHandler**

```java
@RestControllerAdvice  // = @ControllerAdvice + @ResponseBody
public class GlobalExceptionHandler {
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(
            ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(Exception.class)  // Catch-all
    public ResponseEntity<ApiResponse<Void>> handleAllExceptions(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred"));
    }
}
```

**Approach 2: Extend ResponseEntityExceptionHandler**

Override Spring's default exception handlers for more control.

```java
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    
    // Override Spring's validation error handler
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        
        // Extract validation errors
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
    
    // Handle custom exceptions
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }
}
```

**Complete Example:**
```java
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    // Handle validation errors (@Valid failures)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        
        List<String> errors = ex.getBindingResult().getAllErrors()
                .stream()
                .map(error -> {
                    if (error instanceof FieldError) {
                        FieldError fieldError = (FieldError) error;
                        return fieldError.getField() + " : " + fieldError.getDefaultMessage();
                    }
                    return error.getObjectName() + " : " + error.getDefaultMessage();
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", errors));
    }
    
    // Handle business logic exceptions
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        log.error("IllegalArgumentException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }
    
    // Handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAllExceptions(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred"));
    }
}
```

**@ControllerAdvice vs @RestControllerAdvice:**

| Annotation | Use Case | Return Type |
|------------|----------|-------------|
| `@ControllerAdvice` | Traditional MVC | Can return views or JSON |
| `@RestControllerAdvice` | REST APIs | Always returns JSON |

**`@RestControllerAdvice` = `@ControllerAdvice` + `@ResponseBody`**

**Benefits:**
1. Centralized exception handling
2. Consistent error responses
3. Cleaner controllers (no try-catch)
4. Easy to add logging, metrics, etc.

**Exception Priority:**
Spring matches the most specific exception handler first:

```java
@ExceptionHandler(IllegalArgumentException.class)  // More specific
@ExceptionHandler(RuntimeException.class)         // Less specific
@ExceptionHandler(Exception.class)                // Catch-all
```

---

### 1️⃣4️⃣ What is ResponseEntity and why is it used?

`ResponseEntity` wraps the HTTP response, including status code, headers, and body. It provides full control over the response.

**Why use ResponseEntity?**

**Without ResponseEntity:**
```java
@GetMapping("/{id}")
public BookDTO getBook(@PathVariable Long id) {
    return bookService.getBook(id);  // Always returns 200 OK
}
```

**Problems:**
- Can't set custom HTTP status codes
- Can't add custom headers
- Can't return 404 when book not found

**With ResponseEntity:**
```java
@GetMapping("/{id}")
public ResponseEntity<BookDTO> getBook(@PathVariable Long id) {
    return bookService.getBook(id)
            .map(book -> ResponseEntity.ok(book))  // 200 OK
            .orElse(ResponseEntity.notFound().build());  // 404 Not Found
}
```

**Common Use Cases:**

**1. Custom HTTP Status Codes:**
```java
@PostMapping
public ResponseEntity<BookDTO> createBook(@RequestBody BookDTO book) {
    BookDTO created = bookService.createBook(book);
    return ResponseEntity.status(HttpStatus.CREATED)  // 201 Created
            .body(created);
}
```

**2. Custom Headers:**
```java
@GetMapping("/{id}")
public ResponseEntity<BookDTO> getBook(@PathVariable Long id) {
    BookDTO book = bookService.getBook(id);
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Custom-Header", "value");
    return ResponseEntity.ok()
            .headers(headers)
            .body(book);
}
```

**3. Conditional Responses:**
```java
@GetMapping("/{id}")
public ResponseEntity<BookDTO> getBook(@PathVariable Long id) {
    return bookService.getBook(id)
            .map(book -> ResponseEntity.ok(book))  // Found → 200
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)  // Not found → 404
                    .body(null));
}
```

**4. No Content Responses:**
```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
    bookService.deleteBook(id);
    return ResponseEntity.noContent().build();  // 204 No Content
}
```

**Common HTTP Status Codes:**
```java
ResponseEntity.ok()                    // 200 OK
ResponseEntity.created(uri)            // 201 Created
ResponseEntity.accepted()              // 202 Accepted
ResponseEntity.noContent()             // 204 No Content
ResponseEntity.badRequest()            // 400 Bad Request
ResponseEntity.unauthorized()         // 401 Unauthorized
ResponseEntity.forbidden()            // 403 Forbidden
ResponseEntity.notFound()             // 404 Not Found
ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)  // 500
```

**When to use ResponseEntity?**

**Use ResponseEntity when:**
- You need custom HTTP status codes
- You need to add headers
- You need conditional responses (404, 409, etc.)
- Building REST APIs with proper status codes

**Don't need ResponseEntity when:**
- Simple endpoints that always return 200 OK
- Internal APIs where status codes don't matter

---

### 1️⃣5️⃣ How does Spring Boot handle JSON serialization/deserialization?

Spring Boot uses **Jackson** (via `spring-boot-starter-web`) to automatically convert between Java objects and JSON.

**Auto-Configuration:**
When you add `spring-boot-starter-web`, Spring Boot auto-configures:
- Jackson `ObjectMapper` bean
- `MappingJackson2HttpMessageConverter` for JSON
- Automatic JSON conversion for `@RequestBody` and `@ResponseBody`

**How it works:**

**1. Request (JSON → Java Object):**
```java
@PostMapping
public ResponseEntity<BookDTO> createBook(@RequestBody BookDTO bookDTO) {
    // Spring automatically:
    // 1. Reads JSON from request body
    // 2. Uses Jackson to deserialize JSON → BookDTO
    // 3. Injects BookDTO into method parameter
    return ResponseEntity.ok(bookService.createBook(bookDTO));
}
```

**Request:**
```json
POST /api/books
Content-Type: application/json

{
  "title": "Clean Code",
  "author": "Robert Martin",
  "price": 45.99
}
```

**Spring Boot automatically:**
- Detects `@RequestBody`
- Reads JSON from request
- Uses Jackson to convert JSON → `BookDTO` object
- Injects into method parameter

**2. Response (Java Object → JSON):**
```java
@GetMapping("/{id}")
public BookDTO getBook(@PathVariable Long id) {
    // Spring automatically:
    // 1. Takes BookDTO return value
    // 2. Uses Jackson to serialize BookDTO → JSON
    // 3. Sets Content-Type: application/json
    // 4. Returns JSON in response body
    return bookService.getBook(id);
}
```

**Response:**
```json
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 1,
  "title": "Clean Code",
  "author": "Robert Martin",
  "price": 45.99
}
```

**Jackson Annotations:**

**1. @JsonIgnore - Exclude field:**
```java
public class BookDTO {
    private Long id;
    private String title;
    
    @JsonIgnore  // Won't appear in JSON
    private String internalSecret;
}
```

**2. @JsonProperty - Rename field:**
```java
public class BookDTO {
    @JsonProperty("book_id")  // JSON: "book_id" instead of "id"
    private Long id;
}
```

**3. @JsonFormat - Date formatting:**
```java
public class BookDTO {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishDate;
}
```

**4. @JsonInclude - Exclude nulls:**
```java
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookDTO {
    // null fields won't appear in JSON
}
```

**Custom ObjectMapper Configuration:**

**Option 1: application.properties:**
```properties
# Jackson configuration
spring.jackson.serialization.write-dates-as-timestamps=false
spring.jackson.default-property-inclusion=non_null
spring.jackson.serialization.indent-output=true
```

**Option 2: Java Configuration:**
```java
@Configuration
public class JacksonConfig {
    
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
```

**What happens behind the scenes?**
1. Request arrives with JSON body
2. `DispatcherServlet` receives request
3. `HandlerMapping` finds controller method
4. `HttpMessageConverter` (Jackson) deserializes JSON → Java object
5. Method executes with Java object
6. Method returns Java object
7. `HttpMessageConverter` (Jackson) serializes Java object → JSON
8. Response sent with JSON body

**Benefits:**
- No manual JSON parsing
- Type-safe conversion
- Automatic error handling
- Configurable via properties
- Works with nested objects, collections, dates, etc.

**Summary:**
Spring Boot handles JSON automatically via:
- Jackson auto-configuration
- `@RequestBody` for deserialization (JSON → Java)
- `@ResponseBody` / `@RestController` for serialization (Java → JSON)
- No manual JSON code needed

---

## 🔹 Data & JPA (16-20)

### 1️⃣6️⃣ Difference between JpaRepository and CrudRepository

Both are Spring Data JPA repository interfaces. `JpaRepository` extends `CrudRepository` and adds JPA-specific features.

**Repository Hierarchy:**
```
Repository<T, ID> (Marker interface)
        │
        ▼
CrudRepository<T, ID>
        │
        ▼
PagingAndSortingRepository<T, ID>
        │
        ▼
JpaRepository<T, ID>  ← Most commonly used!
```

**CrudRepository:**
Provides basic CRUD operations:

```java
public interface CrudRepository<T, ID> extends Repository<T, ID> {
    <S extends T> S save(S entity);
    <S extends T> Iterable<S> saveAll(Iterable<S> entities);
    Optional<T> findById(ID id);
    boolean existsById(ID id);
    Iterable<T> findAll();
    Iterable<T> findAllById(Iterable<ID> ids);
    long count();
    void deleteById(ID id);
    void delete(T entity);
    void deleteAll();
}
```

**JpaRepository:**
Extends `PagingAndSortingRepository` (which extends `CrudRepository`) and adds JPA-specific methods:

```java
public interface JpaRepository<T, ID> 
    extends PagingAndSortingRepository<T, ID>, QueryByExampleExecutor<T> {
    
    // All methods from CrudRepository +
    
    // JPA-specific methods
    List<T> findAll();
    List<T> findAll(Sort sort);
    Page<T> findAll(Pageable pageable);
    <S extends T> List<S> saveAll(Iterable<S> entities);
    void flush();
    <S extends T> S saveAndFlush(S entity);
    void deleteInBatch(Iterable<T> entities);
    void deleteAllInBatch();
    T getOne(ID id);  // Returns proxy (lazy)
    T getById(ID id); // Returns proxy (lazy)
    <S extends T> List<S> findAll(Example<S> example);
}
```

**Differences:**

| Feature | CrudRepository | JpaRepository |
|---------|----------------|---------------|
| **Basic CRUD** | ✅ Yes | ✅ Yes (inherited) |
| **Pagination** | ❌ No | ✅ Yes |
| **Sorting** | ❌ No | ✅ Yes |
| **Batch Operations** | ❌ No | ✅ Yes (deleteInBatch, deleteAllInBatch) |
| **Flush Operations** | ❌ No | ✅ Yes (flush, saveAndFlush) |
| **Return Types** | `Iterable<T>` | `List<T>` (more convenient) |
| **getOne/getById** | ❌ No | ✅ Yes (returns proxy) |
| **Query by Example** | ❌ No | ✅ Yes |

**When to use which?**

**Use `CrudRepository` when:**
- You only need basic CRUD
- You want a minimal interface
- You're not using JPA-specific features

**Use `JpaRepository` when:**
- You need pagination/sorting
- You want batch operations
- You prefer `List<T>` over `Iterable<T>`
- You're building a JPA application

**Most common choice:** `JpaRepository` (most Spring Boot apps use it)

---

### 1️⃣7️⃣ What is Hibernate? How does it work with Spring Boot?

**Hibernate** is an ORM (Object-Relational Mapping) framework and a JPA implementation.

**What is Hibernate?**
- **ORM:** Maps Java objects to database tables automatically
- **JPA Implementation:** Implements the JPA specification
- **Reduces Boilerplate:** No manual SQL/ResultSet mapping

**Hibernate vs JPA:**
```
JPA (Specification/Interface)
        │
        ├─── Hibernate (Most Popular Implementation)
        ├─── EclipseLink (Reference Implementation)
        └─── OpenJPA (Apache)
```

- **JPA:** What to do (specification)
- **Hibernate:** How to do it (implementation)

**How it works with Spring Boot:**

**1. Auto-Configuration:**
When you add `spring-boot-starter-data-jpa`, Spring Boot:
- Detects Hibernate on classpath
- Auto-configures `EntityManagerFactory`
- Auto-configures `DataSource`
- Auto-configures `TransactionManager`
- Sets up Hibernate properties

**2. Configuration (application.properties):**
```properties
# Database connection
spring.datasource.url=jdbc:h2:mem:bookdb
spring.datasource.driver-class-name=org.h2.Driver

# Hibernate settings
spring.jpa.hibernate.ddl-auto=create-drop  # Auto-create tables
spring.jpa.show-sql=true                  # Show SQL queries
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

**3. Entity Mapping:**
```java
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    // Hibernate automatically:
    // - Creates table from entity
    // - Maps fields to columns
    // - Handles relationships
}
```

**4. Repository Layer:**
```java
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // Spring Data JPA uses Hibernate under the hood
    // Hibernate executes the queries
}
```

**What Hibernate does automatically:**
1. Table creation: `@Entity` → CREATE TABLE
2. Object mapping: Java object ↔ Database row
3. Relationship mapping: `@OneToMany`, `@ManyToOne`, etc.
4. Query generation: JPQL → SQL
5. Lazy loading: Loads related entities on demand
6. Caching: First-level and second-level cache
7. Transaction management: Works with `@Transactional`

**Example Flow:**
```java
// 1. Entity
@Entity
public class Book { ... }

// 2. Repository
public interface BookRepository extends JpaRepository<Book, Long> { }

// 3. Service
@Service
@Transactional
public class BookService {
    @Autowired
    private BookRepository repository;
    
    public Book save(Book book) {
        // Hibernate automatically:
        // - Opens transaction
        // - Converts Book object to SQL INSERT
        // - Executes query
        // - Returns saved entity with ID
        return repository.save(book);
    }
}
```

**Benefits:**
- No manual SQL
- Automatic schema management
- Relationship handling
- Performance optimizations (caching, lazy loading)
- Database-agnostic (works with MySQL, PostgreSQL, H2, etc.)

---

### 1️⃣8️⃣ What is Lazy vs Eager loading?

These are fetch strategies that control when related entities are loaded.

**Eager Loading (FetchType.EAGER):**
Loads related entities immediately with the parent entity.

```java
@Entity
public class Department {
    @OneToMany(fetch = FetchType.EAGER)  // ← EAGER
    private List<Employee> employees;
}
```

**Behavior:**
```java
Department dept = departmentRepository.findById(1L);
// SQL executed:
// 1. SELECT * FROM departments WHERE id = 1
// 2. SELECT * FROM employees WHERE dept_id = 1  ← Loaded immediately!
```

**Characteristics:**
- ✅ Data available immediately
- ✅ No `LazyInitializationException`
- ❌ Can load unnecessary data
- ❌ Can cause performance issues (N+1 problem)
- ❌ More memory usage

**Lazy Loading (FetchType.LAZY):**
Loads related entities only when accessed.

```java
@Entity
public class Department {
    @OneToMany(fetch = FetchType.LAZY)  // ← LAZY (default for @OneToMany)
    private List<Employee> employees;
}
```

**Behavior:**
```java
Department dept = departmentRepository.findById(1L);
// SQL executed:
// 1. SELECT * FROM departments WHERE id = 1
// employees NOT loaded yet!

dept.getEmployees().size();  // ← Accessing employees
// SQL executed NOW:
// 2. SELECT * FROM employees WHERE dept_id = 1
```

**Characteristics:**
- ✅ Better performance (load only what you need)
- ✅ Less memory usage
- ❌ Can cause `LazyInitializationException` if accessed outside transaction
- ❌ Multiple queries if not handled properly

**Default Fetch Types:**

| Relationship | Default Fetch Type |
|--------------|-------------------|
| `@OneToOne` | `EAGER` |
| `@ManyToOne` | `EAGER` |
| `@OneToMany` | `LAZY` |
| `@ManyToMany` | `LAZY` |

**LazyInitializationException:**
Occurs when accessing a lazy-loaded collection outside an active transaction:

```java
// ❌ WRONG - Outside transaction
public Department getDepartment(Long id) {
    Department dept = departmentRepository.findById(id).orElseThrow();
    return dept;  // Transaction closed
}

// Later...
dept.getEmployees().size();  // ❌ LazyInitializationException!
```

**Solution:**
```java
// ✅ CORRECT - Inside transaction
@Transactional(readOnly = true)
public Department getDepartment(Long id) {
    Department dept = departmentRepository.findById(id