# Encoding/Decoding & Encryption/Decryption Guide
## Complete Beginner's Guide - From Zero to Understanding

---

## Table of Contents
1. [What is Encoding? (Simple Explanation)](#what-is-encoding-simple-explanation)
2. [What is Encryption? (Simple Explanation)](#what-is-encryption-simple-explanation)
3. [The Key Difference](#the-key-difference)
4. [Where is Encoding Used?](#where-is-encoding-used)
5. [Where is Encryption Used?](#where-is-encryption-used)
6. [How They Work Together](#how-they-work-together)
7. [Evolution: How We Got Here](#evolution-how-we-got-here)
8. [Encoding & Encryption in Spring Applications](#encoding--encryption-in-spring-applications)
9. [Common Mistakes to Avoid](#common-mistakes-to-avoid)

---

## What is Encoding? (Simple Explanation)

### Think of Encoding Like Translation

Imagine you're sending a letter, but the postal system only accepts numbers. You need to **encode** your message into numbers so it can be sent. Anyone who knows the code can **decode** it back to your original message.

**Encoding = Converting data from one format to another so it can be used somewhere else**

### Real-World Analogy
- **Problem:** You want to send a photo via email, but email only accepts text
- **Solution:** Convert the photo to text using Base64 encoding
- **Result:** Photo becomes text like "SGVsbG8gV29ybGQ=" that can be sent via email
- **Anyone can decode it** back to the photo (no secret needed)

### Key Characteristics of Encoding:
1. ✅ **Reversible** - You can always get back the original data
2. ✅ **No key needed** - Anyone can encode/decode
3. ❌ **NOT secure** - It's just format conversion, not protection
4. ✅ **Fast** - Very quick to encode/decode

### Simple Example:
```
Original Text: "Hello"
↓ Encode (convert to Base64)
Encoded Text: "SGVsbG8="
↓ Decode (convert back)
Original Text: "Hello"
```

**Notice:** No password or key is needed. Anyone can decode "SGVsbG8=" back to "Hello".

---

## What is Encryption? (Simple Explanation)

### Think of Encryption Like a Locked Safe

Imagine you have a secret message. You put it in a safe and lock it with a key. Only someone with the correct key can open the safe and read your message.

**Encryption = Converting data into a secret code that can only be read with the correct key**

### Real-World Analogy
- **Problem:** You want to send your credit card number over the internet
- **Solution:** Encrypt it with a key (like locking it in a safe)
- **Result:** Credit card becomes gibberish like "X7#kL9@mP2$vN8"
- **Only someone with the key** can decrypt it back to your credit card number

### Key Characteristics of Encryption:
1. ✅ **Reversible** - You can get back the original data
2. ✅ **Key required** - You MUST have the key to decrypt
3. ✅ **Secure** - Protects data from unauthorized access
4. ⚠️ **Slower** - Takes more time than encoding

### Simple Example:
```
Original Text: "Hello"
↓ Encrypt with Key: "mySecretKey123"
Encrypted Text: "X7#kL9@mP2$vN8&qR5"
↓ Decrypt with Key: "mySecretKey123"
Original Text: "Hello"
```

**Notice:** Without the key "mySecretKey123", you CANNOT decrypt "X7#kL9@mP2$vN8&qR5" back to "Hello".

---

## The Key Difference

### Side-by-Side Comparison

| Aspect | Encoding | Encryption |
|--------|----------|------------|
| **What it does** | Changes format | Hides content |
| **Like** | Translating to another language | Locking in a safe |
| **Key needed?** | No | Yes |
| **Secure?** | No | Yes |
| **Purpose** | Make data compatible | Protect data |
| **Example** | Photo → Text (Base64) | Credit card → Secret code |

### Visual Comparison

```
ENCODING (Like Translation):
┌─────────────┐
│ "Hello"     │  ← Original message
└─────────────┘
       │
       │ Encode (no key needed)
       ↓
┌─────────────┐
│ "SGVsbG8="  │  ← Encoded format
└─────────────┘
       │
       │ Decode (anyone can do this)
       ↓
┌─────────────┐
│ "Hello"     │  ← Back to original
└─────────────┘


ENCRYPTION (Like a Locked Safe):
┌─────────────┐
│ "Hello"     │  ← Original message
└─────────────┘
       │
       │ Encrypt + Key: "secret123"
       ↓
┌─────────────┐
│ "X7#kL9@m"  │  ← Encrypted (locked)
└─────────────┘
       │
       │ Decrypt + Key: "secret123"
       ↓
┌─────────────┐
│ "Hello"     │  ← Back to original
└─────────────┘
```

### The Most Important Point

**Encoding is NOT security!** If you encode something, anyone can decode it. It's like writing in a different alphabet - anyone who knows the alphabet can read it.

**Encryption IS security!** If you encrypt something, only people with the key can decrypt it. It's like a locked safe - you need the key to open it.

---

## Where is Encoding Used?

### 1. **Email Attachments (Base64 Encoding)**

**The Problem:**
- Email systems were designed to send text only
- Photos, PDFs, and other files are binary (not text)
- How do you send a photo via email?

**The Solution:**
- Convert the photo to text using Base64 encoding
- Send the text via email
- Receiver decodes it back to the photo

**Where you see it:**
- When you attach a file in email, it's automatically Base64 encoded
- You don't see it, but it happens behind the scenes

### 2. **Web URLs (URL Encoding)**

**The Problem:**
- URLs can only contain certain characters (letters, numbers, some symbols)
- What if you want to send "Hello World" in a URL? (space is not allowed)
- What if you want to send "user@example.com"? (@ is special in URLs)

**The Solution:**
- URL encoding converts special characters to codes
- Space becomes `%20`
- @ becomes `%40`

**Where you see it:**
```
Original: https://example.com/search?q=Hello World
Encoded:  https://example.com/search?q=Hello%20World

Original: https://example.com/user?email=user@example.com
Encoded:  https://example.com/user?email=user%40example.com
```

**In Spring:**
```java
// Spring automatically handles URL encoding
@GetMapping("/search")
public String search(@RequestParam String q) {
    // If user visits: /search?q=Hello%20World
    // Spring automatically decodes to: "Hello World"
    return "Searching for: " + q;
}
```

### 3. **JSON Web Tokens (JWT) - Header and Payload**

**The Problem:**
- JWT tokens need to be sent in HTTP headers
- Headers are text-only
- Need to convert JSON data to text format

**The Solution:**
- Encode the JSON header and payload using Base64URL encoding
- This makes it safe to send in URLs and headers

**Where you see it:**
```
JWT Token looks like:
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJuYW1lIjoiSm9obiJ9.signature

The first two parts (before the dots) are Base64 encoded JSON:
- First part: {"alg":"HS256","typ":"JWT"} (encoded)
- Second part: {"sub":"123","name":"John"} (encoded)
- Third part: Signature (encrypted)
```

**Important:** The JWT payload is ENCODED, not encrypted! Anyone can decode it and read it.

### 4. **Database Storage (UTF-8 Encoding)**

**The Problem:**
- Computers store everything as numbers (binary)
- How do you store text like "Hello" or "नमस्ते" (Hindi) or "你好" (Chinese)?

**The Solution:**
- UTF-8 encoding converts characters to numbers
- Each character gets a unique number
- Database stores the numbers

**Where you see it:**
- When you save text to a database, it's encoded as UTF-8
- When you read it back, it's decoded back to text
- You don't see this - Spring and the database handle it automatically

**In Spring:**
```java
@Entity
public class User {
    @Column(name = "name")
    private String name;  // Spring automatically handles UTF-8 encoding/decoding
    
    // When you save "Hello" or "नमस्ते", Spring encodes it
    // When you read it back, Spring decodes it
}
```

### 5. **API Responses (JSON Encoding)**

**The Problem:**
- Your Spring application has Java objects
- Client (browser, mobile app) needs JSON text
- How do you convert Java object to JSON?

**The Solution:**
- Spring automatically encodes Java objects to JSON
- This is encoding (format conversion)

**Where you see it:**
```java
@RestController
public class UserController {
    
    @GetMapping("/user")
    public User getUser() {
        User user = new User("John", "john@example.com");
        return user;  // Spring automatically encodes to JSON
    }
}

// Response (encoded as JSON):
// {
//   "name": "John",
//   "email": "john@example.com"
// }
```

---

## Where is Encryption Used?

### 1. **HTTPS (Secure Web Communication)**

**The Problem:**
- When you visit a website, data travels over the internet
- Anyone on the network can see your data (like a postcard - anyone can read it)
- How do you protect your password, credit card, personal info?

**The Solution:**
- HTTPS encrypts all data between your browser and the server
- Even if someone intercepts it, they can't read it (they need the key)

**Where you see it:**
- Look at your browser's address bar
- If you see a lock icon 🔒 and "https://" - your connection is encrypted
- If you see "http://" (no 's') - your connection is NOT encrypted (dangerous!)

**In Spring:**
```java
// Spring Boot automatically supports HTTPS
// In application.properties:
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=yourpassword
server.ssl.key-store-type=PKCS12

// Now your Spring app uses HTTPS - all data is encrypted
```

### 2. **Password Storage (Hashing - Special Type of Encryption)**

**The Problem:**
- You need to store user passwords in your database
- If database is hacked, all passwords are exposed
- How do you protect passwords?

**The Solution:**
- Hash passwords (one-way encryption - cannot be reversed)
- Store the hash, not the actual password
- When user logs in, hash their input and compare with stored hash

**Where you see it:**
```
User registers with password: "MyPassword123"
↓ Hash it (one-way)
Stored in database: "$2a$12$LQv3c1yqBWVHxkd0LHAkCO..."

User logs in with password: "MyPassword123"
↓ Hash it
Compare: Does hash match stored hash? Yes → Login successful
```

**In Spring:**
```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt is a hashing algorithm (one-way encryption)
        return new BCryptPasswordEncoder(12);
    }
}

@Service
public class UserService {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public void registerUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        // Hash the password before storing
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }
    
    public boolean loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        // Compare hashed password
        return passwordEncoder.matches(password, user.getPassword());
    }
}
```

### 3. **JWT Token Signature (HMAC Encryption)**

**The Problem:**
- JWT tokens are sent with every API request
- Anyone can create a fake token
- How do you verify the token is real and not tampered with?

**The Solution:**
- Encrypt a signature using HMAC (Hash-based Message Authentication Code)
- If token is modified, signature won't match
- Only server with the secret key can create valid signatures

**Where you see it:**
```
JWT Token has 3 parts:
1. Header (encoded): {"alg":"HS256","typ":"JWT"}
2. Payload (encoded): {"sub":"123","name":"John"}
3. Signature (encrypted): Created using secret key

If someone changes payload to {"role":"ADMIN"}, signature won't match
Server will reject the token
```

**In Spring:**
```java
@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String jwtSecret;  // Secret key for encryption
    
    public String generateToken(User user) {
        return Jwts.builder()
            .subject(user.getUsername())
            .claim("userId", user.getId())
            .claim("role", user.getRole())
            .signWith(getSigningKey())  // Encrypt signature with secret key
            .compact();
    }
    
    public boolean validateToken(String token) {
        try {
            // Verify signature using secret key
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;  // Invalid or tampered token
        }
    }
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
```

### 4. **Database Encryption (Sensitive Data at Rest)**

**The Problem:**
- You store credit card numbers, SSN, medical records in database
- If database is hacked, all sensitive data is exposed
- How do you protect data even if database is compromised?

**The Solution:**
- Encrypt sensitive fields before storing
- Decrypt when reading
- Even if database is hacked, data is still protected

**Where you see it:**
```
Credit Card: "1234-5678-9012-3456"
↓ Encrypt with key
Stored in DB: "X7#kL9@mP2$vN8&qR5tY6wE7rT8"

If database is hacked, attacker sees: "X7#kL9@mP2$vN8&qR5tY6wE7rT8"
Without the key, they can't decrypt it
```

**In Spring:**
```java
@Component
public class EncryptionService {
    
    @Value("${encryption.key}")
    private String encryptionKey;
    
    private Cipher getCipher(int mode) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(
            encryptionKey.getBytes(), "AES"
        );
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(mode, keySpec);
        return cipher;
    }
    
    public String encrypt(String data) throws Exception {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }
    
    public String decrypt(String encryptedData) throws Exception {
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
        byte[] decoded = Base64.getDecoder().decode(encryptedData);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted);
    }
}

@Entity
public class Payment {
    @Column(name = "credit_card")
    private String creditCard;  // Store encrypted value
    
    // Use encryption service before saving
}
```

### 5. **API Key Storage**

**The Problem:**
- Your Spring app uses external APIs (payment gateway, email service)
- You need to store API keys securely
- If code is leaked, API keys are exposed

**The Solution:**
- Encrypt API keys in configuration
- Decrypt when needed
- Store encryption key separately (environment variables, key management service)

**In Spring:**
```java
// application.properties (encrypted values)
payment.api.key=${ENC:encrypted_api_key_here}

// Or use environment variables
// SPRING_DATASOURCE_PASSWORD=encrypted_password

@Configuration
public class ApiConfig {
    
    @Value("${payment.api.key}")
    private String encryptedApiKey;
    
    @Autowired
    private EncryptionService encryptionService;
    
    @Bean
    public PaymentService paymentService() {
        // Decrypt API key when creating service
        String decryptedKey = encryptionService.decrypt(encryptedApiKey);
        return new PaymentService(decryptedKey);
    }
}
```

---

## How They Work Together

### Real Example: HTTPS Communication

When you use HTTPS, both encoding AND encryption happen:

```
1. Your Browser:
   Original Data: "username=john&password=secret123"
   ↓ Encrypt (for security)
   Encrypted: "X7#kL9@mP2$vN8&qR5..."
   ↓ Encode (for transmission)
   Encoded: Base64 of encrypted data
   ↓ Send over network

2. Network Transmission:
   Encoded encrypted data travels over internet
   Even if intercepted, attacker sees: encoded gibberish

3. Server Receives:
   Encoded data
   ↓ Decode
   Encrypted data
   ↓ Decrypt (with server's key)
   Original: "username=john&password=secret123"
```

### Real Example: JWT Token in Spring

JWT uses BOTH encoding and encryption:

```java
// 1. CREATE TOKEN (Encoding + Encryption)
public String generateToken(User user) {
    // Header (will be encoded)
    Map<String, Object> header = Map.of(
        "alg", "HS256",
        "typ", "JWT"
    );
    
    // Payload (will be encoded)
    Map<String, Object> claims = Map.of(
        "sub", user.getUsername(),
        "userId", user.getId(),
        "role", user.getRole()
    );
    
    // Step 1: ENCODE header and payload (Base64URL)
    String encodedHeader = base64UrlEncode(header);
    String encodedPayload = base64UrlEncode(claims);
    
    // Step 2: ENCRYPT signature (HMAC with secret key)
    String signature = hmacEncrypt(
        encodedHeader + "." + encodedPayload,
        jwtSecret
    );
    String encodedSignature = base64UrlEncode(signature);
    
    // Final token: encoded.header.encoded.payload.encrypted.signature
    return encodedHeader + "." + encodedPayload + "." + encodedSignature;
}

// 2. VERIFY TOKEN (Decoding + Decryption)
public boolean validateToken(String token) {
    String[] parts = token.split("\\.");
    
    // Step 1: DECODE header and payload
    Map<String, Object> header = base64UrlDecode(parts[0]);
    Map<String, Object> payload = base64UrlDecode(parts[1]);
    
    // Step 2: DECRYPT and verify signature
    String expectedSignature = hmacEncrypt(
        parts[0] + "." + parts[1],
        jwtSecret
    );
    
    // Compare signatures
    return expectedSignature.equals(parts[2]);
}
```

**Key Points:**
- Header and Payload are **ENCODED** (anyone can decode and read them)
- Signature is **ENCRYPTED** (only server with secret key can verify it)
- This is why you should NEVER put secrets in JWT payload!

---

## Evolution: How We Got Here

### Encoding Evolution

#### 1960s: ASCII (Character Encoding)
**Problem:** Different computers used different codes for letters
**Solution:** ASCII standard - A=65, B=66, a=97, etc.
**Impact:** Enabled computers to communicate

#### 1970s: Base64 (Binary-to-Text Encoding)
**Problem:** Email could only send text, not binary files
**Solution:** Base64 converts binary (photos, files) to text
**Impact:** Enabled email attachments

#### 1990s: UTF-8 (Unicode Encoding)
**Problem:** ASCII only supported English (128 characters)
**Solution:** UTF-8 supports all languages (millions of characters)
**Impact:** Global internet, multilingual applications

#### 1990s: URL Encoding
**Problem:** URLs can't contain spaces or special characters
**Solution:** Percent encoding (space → %20)
**Impact:** Web development, form submissions

### Encryption Evolution

#### Ancient Times: Substitution Ciphers
**Example:** Caesar Cipher - shift letters by 3
- "HELLO" → "KHOOR" (shift by 3)
**Weakness:** Easy to break

#### 1970s: DES (Data Encryption Standard)
**Problem:** Need standard way to encrypt data
**Solution:** DES with 56-bit key
**Status:** Deprecated (too weak, can be broken)

#### 1977: RSA (Public Key Encryption)
**Breakthrough:** Different keys for encryption and decryption
- Public key: Anyone can use to encrypt
- Private key: Only owner can decrypt
**Impact:** Solved key distribution problem

#### 2001: AES (Advanced Encryption Standard)
**Replaced:** DES
**Key Sizes:** 128, 192, or 256 bits
**Status:** Current standard, used everywhere
**Impact:** Secure internet, banking, e-commerce

#### 2000s: TLS/SSL (HTTPS)
**Combines:** Symmetric + Asymmetric encryption
**Impact:** Secure web communication (the lock icon 🔒 in your browser)

---

## Encoding & Encryption in Spring Applications

### 1. Password Hashing (BCrypt) - Most Common

**Where:** User registration and login

```java
// Configuration
@Configuration
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt: One-way hashing (special encryption)
        // Strength 12 = how many rounds (higher = more secure but slower)
        return new BCryptPasswordEncoder(12);
    }
}

// Service Layer
@Service
public class UserService {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserRepository userRepository;
    
    // REGISTER: Hash password before storing
    public User registerUser(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        
        // ENCODE/HASH the password (one-way encryption)
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(hashedPassword);
        
        return userRepository.save(user);
    }
    
    // LOGIN: Compare hashed passwords
    public boolean authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException());
        
        // Verify password matches stored hash
        return passwordEncoder.matches(password, user.getPassword());
    }
}
```

**What Happens:**
```
User registers: password = "MyPassword123"
↓ BCrypt hash (encryption)
Stored: "$2a$12$LQv3c1yqBWVHxkd0LHAkCO..."

User logs in: password = "MyPassword123"
↓ BCrypt hash
Compare: Does new hash match stored hash? Yes → Login
```

### 2. JWT Tokens (Encoding + Encryption)

**Where:** API authentication

```java
// Dependencies (build.gradle)
dependencies {
    implementation 'io.jsonwebtoken:jjwt-api:0.12.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.5'
}

// JWT Service
@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret}")  // Secret key from application.properties
    private String jwtSecret;
    
    @Value("${jwt.expiration}")  // e.g., 86400000 (24 hours)
    private long jwtExpiration;
    
    // GENERATE TOKEN
    public String generateToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpiration);
        
        return Jwts.builder()
            .subject(user.getUsername())  // Will be encoded in payload
            .claim("userId", user.getId())  // Will be encoded in payload
            .claim("role", user.getRole().name())  // Will be encoded in payload
            .issuedAt(now)
            .expiration(expiry)
            .signWith(getSigningKey())  // ENCRYPT signature with secret key
            .compact();  // Returns: encoded.header.encoded.payload.encrypted.signature
    }
    
    // VALIDATE TOKEN
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())  // DECRYPT and verify signature
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;  // Invalid or tampered token
        }
    }
    
    // EXTRACT USERNAME FROM TOKEN
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();  // Returns username
    }
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}

// Filter to validate JWT on every request
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        // Extract token from header: "Authorization: Bearer <token>"
        String token = getTokenFromRequest(request);
        
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // Token is valid - extract username
            String username = jwtTokenProvider.getUsernameFromToken(token);
            
            // Load user and set authentication
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // Remove "Bearer " prefix
        }
        return null;
    }
}

// Controller
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // Authenticate user
        if (userService.authenticate(request.getUsername(), request.getPassword())) {
            User user = userService.findByUsername(request.getUsername());
            
            // Generate JWT token (encoding + encryption happens here)
            String token = jwtTokenProvider.generateToken(user);
            
            return ResponseEntity.ok(new AuthResponse(token, "Bearer", 86400));
        }
        throw new BadCredentialsException("Invalid credentials");
    }
}
```

**What Happens:**
```
1. User logs in → Server generates JWT
   - Header: {"alg":"HS256","typ":"JWT"} → ENCODED
   - Payload: {"sub":"john","userId":1,"role":"USER"} → ENCODED
   - Signature: HMAC(header.payload, secret) → ENCRYPTED
   - Token: encoded.header.encoded.payload.encrypted.signature

2. Client sends token in every request:
   Header: "Authorization: Bearer <token>"

3. Server validates token:
   - DECODE header and payload
   - DECRYPT and verify signature
   - If valid, allow request
```

### 3. URL Encoding (Automatic in Spring)

**Where:** Query parameters, path variables

```java
@RestController
public class SearchController {
    
    // Spring automatically handles URL encoding/decoding
    @GetMapping("/search")
    public ResponseEntity<List<Result>> search(
            @RequestParam String q) {  // Spring decodes "Hello%20World" → "Hello World"
        
        return ResponseEntity.ok(searchService.search(q));
    }
    
    // Example: /search?q=Hello%20World
    // Spring automatically decodes %20 to space
    // q parameter = "Hello World"
}
```

### 4. JSON Encoding (Automatic in Spring)

**Where:** All REST API responses

```java
@RestController
public class UserController {
    
    @GetMapping("/user/{id}")
    public User getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        // Spring automatically ENCODES User object to JSON
        return user;
    }
    
    // Response (automatically encoded as JSON):
    // {
    //   "id": 1,
    //   "username": "john",
    //   "email": "john@example.com"
    // }
}
```

### 5. Database Field Encryption (For Sensitive Data)

**Where:** Storing credit cards, SSN, medical records

```java
// Encryption Service
@Component
public class EncryptionService {
    
    @Value("${encryption.secret-key}")
    private String secretKey;
    
    private Cipher getCipher(int mode) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(
            secretKey.getBytes(StandardCharsets.UTF_8), "AES"
        );
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(mode, keySpec);
        return cipher;
    }
    
    public String encrypt(String plainText) throws Exception {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        // ENCODE encrypted bytes to Base64 for storage
        return Base64.getEncoder().encodeToString(encrypted);
    }
    
    public String decrypt(String encryptedText) throws Exception {
        // DECODE from Base64
        byte[] decoded = Base64.getDecoder().decode(encryptedText);
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}

// Entity with encrypted field
@Entity
public class Payment {
    @Id
    private Long id;
    
    @Column(name = "credit_card")
    private String creditCard;  // Store encrypted value
    
    // Getters and setters with encryption
    public void setCreditCard(String creditCard) {
        try {
            this.creditCard = encryptionService.encrypt(creditCard);
        } catch (Exception e) {
            throw new EncryptionException("Failed to encrypt credit card", e);
        }
    }
    
    public String getCreditCard() {
        try {
            return encryptionService.decrypt(this.creditCard);
        } catch (Exception e) {
            throw new EncryptionException("Failed to decrypt credit card", e);
        }
    }
}
```

### 6. HTTPS Configuration (Encryption for All Traffic)

**Where:** Production Spring applications

```properties
# application.properties
server.port=8443
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=yourpassword
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=tomcat
```

**What Happens:**
- All HTTP traffic is encrypted
- Browser shows lock icon 🔒
- Data in transit is protected

---

## Common Mistakes to Avoid

### ❌ Mistake 1: Using Encoding for Security

```java
// ❌ BAD: Base64 is NOT encryption!
String password = "MyPassword123";
String encoded = Base64.getEncoder().encodeToString(password.getBytes());
// Anyone can decode this! Not secure!

// ✅ GOOD: Use BCrypt hashing
String hashed = passwordEncoder.encode(password);
// Cannot be reversed, secure!
```

### ❌ Mistake 2: Storing Passwords in Plain Text

```java
// ❌ BAD
user.setPassword(request.getPassword());  // Storing plain text!

// ✅ GOOD
user.setPassword(passwordEncoder.encode(request.getPassword()));
```

### ❌ Mistake 3: Putting Secrets in JWT Payload

```java
// ❌ BAD: JWT payload is only encoded, not encrypted!
Map<String, Object> claims = Map.of(
    "userId", user.getId(),
    "password", user.getPassword(),  // DON'T DO THIS!
    "creditCard", user.getCreditCard()  // DON'T DO THIS!
);

// ✅ GOOD: Only put non-sensitive data
Map<String, Object> claims = Map.of(
    "userId", user.getId(),
    "username", user.getUsername(),
    "role", user.getRole()
);
```

### ❌ Mistake 4: Using HTTP Instead of HTTPS

```java
// ❌ BAD: Data sent in plain text
@GetMapping("/api/users")
public List<User> getUsers() {
    // If using HTTP, all data is visible to attackers!
    return userService.getAllUsers();
}

// ✅ GOOD: Use HTTPS
// Configure SSL in application.properties
// All data is automatically encrypted
```

### ❌ Mistake 5: Weak Encryption Keys

```java
// ❌ BAD: Short, predictable key
String key = "secret123";  // Too short, too simple!

// ✅ GOOD: Long, random key
String key = "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0";  // 40+ characters
// Or use environment variables
@Value("${encryption.key}")
private String key;  // Load from secure config
```

---

## Summary: Quick Reference

### Encoding
- **What:** Format conversion
- **Security:** None
- **Key needed:** No
- **Spring Usage:** 
  - URL parameters (automatic)
  - JSON responses (automatic)
  - JWT header/payload (Base64URL)

### Encryption
- **What:** Data protection
- **Security:** High
- **Key needed:** Yes
- **Spring Usage:**
  - Password hashing (BCrypt)
  - JWT signature (HMAC)
  - HTTPS/TLS
  - Database field encryption

### Remember:
1. **Encoding = Translation** (anyone can decode)
2. **Encryption = Locked Safe** (needs key to decrypt)
3. **JWT uses both:** Encoding for format, Encryption for signature
4. **Always use HTTPS** in production
5. **Never put secrets in JWT payload** (it's only encoded!)
6. **Always hash passwords** (use BCrypt, not plain text)

---

**You're now ready to explain encoding and encryption to anyone!** 🎓
