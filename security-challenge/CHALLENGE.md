# 🔐 Security Implementation Challenge

## Overview
You have a **Document Management API** that currently has **ZERO security**. Your mission is to implement proper authentication and authorization from scratch.

---

## 📁 Project Structure

```
security-challenge/
├── pom.xml
├── src/main/java/com/challenge/
│   ├── SecurityChallengeApplication.java
│   ├── controller/
│   │   ├── AuthController.java          ← Implement login/register
│   │   └── DocumentController.java      ← Add security annotations
│   ├── dto/
│   │   ├── DocumentRequest.java
│   │   └── DocumentResponse.java
│   ├── entity/
│   │   ├── Document.java
│   │   └── User.java                    ← Add Role enum
│   ├── repository/
│   │   ├── DocumentRepository.java
│   │   └── UserRepository.java
│   └── service/
│       └── DocumentService.java         ← Add authorization checks
└── src/main/resources/
    └── application.properties
```

---

## 🎯 Your Tasks

### Task 1: Create Role Enum
Create `src/main/java/com/challenge/entity/Role.java`:
```java
public enum Role {
    USER,
    MODERATOR,
    ADMIN
}
```

Add permissions to each role:
- **USER**: `document:read`
- **MODERATOR**: `document:read`, `document:write`
- **ADMIN**: All permissions including `document:delete`, `user:read`, `user:write`

---

### Task 2: Update User Entity
Modify `User.java` to:
1. Add `Role` field with `@Enumerated(EnumType.STRING)`
2. Implement `UserDetails` interface from Spring Security
3. Implement `getAuthorities()` to return role + permissions

---

### Task 3: Create Security Configuration
Create `src/main/java/com/challenge/config/SecurityConfig.java`:
- Configure `SecurityFilterChain`
- Allow `/api/auth/**` endpoints without authentication
- Require authentication for all other endpoints
- Add JWT filter (Task 5)
- Configure CORS and CSRF as needed

---

### Task 4: Implement Password Encoding
- Use `BCryptPasswordEncoder` for hashing passwords
- Never store plain-text passwords

---

### Task 5: Implement JWT Authentication
Create these classes:

**JwtTokenProvider.java** (in `security/` package):
- Generate JWT token with claims: userId, username, role
- Validate JWT token
- Extract user information from token
- Set expiration time (e.g., 24 hours)

**JwtAuthenticationFilter.java**:
- Extend `OncePerRequestFilter`
- Extract JWT from `Authorization: Bearer <token>` header
- Validate token and set authentication in SecurityContext

---

### Task 6: Implement AuthController
Complete the authentication endpoints:

**POST /api/auth/register**
```json
Request:
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePass123"
}

Response (201):
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com"
  }
}
```

**POST /api/auth/login**
```json
Request:
{
  "username": "john_doe",
  "password": "SecurePass123"
}

Response (200):
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "tokenType": "Bearer",
    "expiresIn": 86400
  }
}
```

---

### Task 7: Secure DocumentController
Add proper security annotations:

| Endpoint | Security Requirement |
|----------|---------------------|
| `POST /documents` | Authenticated + `document:write` permission |
| `GET /documents` | Authenticated |
| `GET /documents/{id}` | Authenticated + access check |
| `PUT /documents/{id}` | Authenticated + owner only |
| `DELETE /documents/{id}` | Authenticated + owner OR admin |
| `GET /documents/admin/all` | `ADMIN` role only |

Use `@AuthenticationPrincipal User user` to get current user.

---

### Task 8: Implement Authorization Logic
In `DocumentService.java`, add proper checks:

```java
// Example for getDocumentById:
public DocumentResponse getDocumentById(Long documentId, User requestingUser) {
    Document document = findById(documentId);
    
    // Allow if:
    // 1. Document is PUBLIC
    // 2. User is the owner
    // 3. User has ADMIN role
    
    if (!canAccess(document, requestingUser)) {
        throw new AccessDeniedException("You don't have access to this document");
    }
    
    return mapToResponse(document);
}
```

---

## ✅ Acceptance Criteria

### Security Tests to Pass:

| Test | Expected Result |
|------|-----------------|
| Unauthenticated user calls `/api/documents` | 401 Unauthorized |
| User registers with weak password | 400 Bad Request |
| User logs in with wrong password | 401 Unauthorized |
| User tries to update another user's document | 403 Forbidden |
| User tries to delete another user's document | 403 Forbidden |
| Regular user calls `/api/documents/admin/all` | 403 Forbidden |
| Admin calls `/api/documents/admin/all` | 200 OK |
| User views their own private document | 200 OK |
| User views another's private document | 403 Forbidden |
| User views public document | 200 OK |

---

## 💡 Hints

### Useful Annotations:
```java
@PreAuthorize("hasRole('ADMIN')")
@PreAuthorize("hasAuthority('document:write')")
@PreAuthorize("hasRole('ADMIN') or @documentService.isOwner(#id, principal)")
```

### Get Current User:
```java
@GetMapping
public ResponseEntity<?> getDocuments(@AuthenticationPrincipal User user) {
    // user is the currently authenticated user
}
```

### Throwing Access Denied:
```java
import org.springframework.security.access.AccessDeniedException;

if (!isOwner) {
    throw new AccessDeniedException("You don't have permission");
}
```

---

## 🚀 Getting Started

1. Uncomment Spring Security dependency in `build.gradle`
2. Uncomment JWT dependencies in `build.gradle`
3. Start implementing from Task 1

Run the application:
```bash
cd security-challenge
./gradlew bootRun
```

Or from the parent directory:
```bash
cd security-challenge && ./gradlew bootRun
```

Test with curl or Postman!

---

## 📚 Resources

- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/)
- [JWT.io](https://jwt.io/) - Decode and debug JWT tokens
- [BCrypt Password Encoder](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/crypto/bcrypt/BCryptPasswordEncoder.html)

---

**Good luck! 🍀**

