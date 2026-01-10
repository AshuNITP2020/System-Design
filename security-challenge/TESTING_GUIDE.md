# API Testing Guide

This guide shows you how to test all the security endpoints using curl commands.

## Prerequisites

1. Start the application:
```bash
cd security-challenge
./gradlew bootRun
```

The application will start on `http://localhost:8080`

## Testing Steps

### Step 1: Register a New User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "TestPass123"
  }'
```

**Expected Response (201 Created):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "role": "USER"
  }
}
```

### Step 2: Login and Get JWT Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "TestPass123"
  }'
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "user": {
      "id": 1,
      "username": "testuser",
      "email": "test@example.com",
      "role": "USER"
    }
  }
}
```

**Save the token!** Copy the `accessToken` value for the next steps.

### Step 3: Get Current User Info (Protected Endpoint)

Replace `YOUR_TOKEN_HERE` with the token from Step 2:

```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "User information retrieved successfully",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "role": "USER",
    "enabled": true,
    "createdAt": "2024-01-01T10:00:00"
  }
}
```

### Step 4: Create a Document (Requires Authentication)

```bash
curl -X POST http://localhost:8080/api/documents \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My First Document",
    "content": "This is the content of my document"
  }'
```

**Expected Response (201 Created):**
```json
{
  "success": true,
  "message": "Document created",
  "data": {
    "id": 1,
    "title": "My First Document",
    "content": "This is the content of my document",
    "ownerUsername": "testuser",
    "visibility": "PRIVATE",
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
}
```

### Step 5: Get All My Documents

```bash
curl -X GET http://localhost:8080/api/documents \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Step 6: Get a Specific Document

```bash
curl -X GET http://localhost:8080/api/documents/1 \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Step 7: Update a Document (Only Owner)

```bash
curl -X PUT http://localhost:8080/api/documents/1 \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Updated Document Title",
    "content": "Updated content"
  }'
```

### Step 8: Delete a Document (Only Owner or Admin)

```bash
curl -X DELETE http://localhost:8080/api/documents/1 \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## Testing Authorization

### Test 1: Access Without Token (Should Fail)

```bash
curl -X GET http://localhost:8080/api/auth/me
```

**Expected Response (401 Unauthorized):**
```json
{
  "success": false,
  "message": "Authentication required. Please provide a valid JWT token.",
  "error": "UNAUTHORIZED",
  "status": 401,
  "path": "/api/auth/me"
}
```

### Test 2: Access Admin Endpoint as Regular User (Should Fail)

First, register and login as a regular user, then try:

```bash
curl -X GET http://localhost:8080/api/documents/admin/all \
  -H "Authorization: Bearer YOUR_USER_TOKEN"
```

**Expected Response (403 Forbidden):**
```json
{
  "success": false,
  "message": "Access denied. You do not have permission to access this resource.",
  "error": "FORBIDDEN",
  "status": 403,
  "path": "/api/documents/admin/all"
}
```

### Test 3: Access Admin Endpoint as Admin (Should Succeed)

1. Register an admin user (you'll need to manually set role in database or create a separate endpoint)
2. Login and get admin token
3. Access admin endpoint:

```bash
curl -X GET http://localhost:8080/api/documents/admin/all \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

## Testing Error Cases

### Invalid Login Credentials

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "WrongPassword"
  }'
```

**Expected Response (401 Unauthorized):**
```json
{
  "success": false,
  "message": "Invalid username/email or password"
}
```

### Invalid Token

```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer invalid.token.here"
```

**Expected Response (401 Unauthorized)**

### Weak Password (Registration)

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "weakuser",
    "email": "weak@example.com",
    "password": "weak"
  }'
```

**Expected Response (400 Bad Request):**
```json
{
  "success": false,
  "message": "Password must be at least 8 characters with at least 1 uppercase, 1 lowercase, and 1 digit"
}
```

## Using Environment Variables (Easier Testing)

Save your token to a variable:

```bash
# Login and save token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"TestPass123"}' \
  | jq -r '.data.accessToken')

# Use token in subsequent requests
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer $TOKEN"
```

## Using Postman

1. **Import Collection:**
   - Create a new collection called "Security Challenge API"
   - Base URL: `http://localhost:8080`

2. **Set Environment Variables:**
   - Create environment with variable `baseUrl` = `http://localhost:8080`
   - Create variable `token` (will be set after login)

3. **Requests:**
   - **Register:** POST `{{baseUrl}}/api/auth/register`
   - **Login:** POST `{{baseUrl}}/api/auth/login` → Save token to environment variable
   - **Get Me:** GET `{{baseUrl}}/api/auth/me` → Header: `Authorization: Bearer {{token}}`
   - **Create Document:** POST `{{baseUrl}}/api/documents` → Header: `Authorization: Bearer {{token}}`
   - **Get Documents:** GET `{{baseUrl}}/api/documents` → Header: `Authorization: Bearer {{token}}`

## Quick Test Script

Save this as `test-api.sh`:

```bash
#!/bin/bash

BASE_URL="http://localhost:8080"

echo "=== Testing Security Challenge API ==="
echo ""

# Register
echo "1. Registering user..."
REGISTER_RESPONSE=$(curl -s -X POST $BASE_URL/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"TestPass123"}')
echo "$REGISTER_RESPONSE" | jq '.'
echo ""

# Login
echo "2. Logging in..."
LOGIN_RESPONSE=$(curl -s -X POST $BASE_URL/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"TestPass123"}')
TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.data.accessToken')
echo "Token: $TOKEN"
echo ""

# Get current user
echo "3. Getting current user..."
curl -s -X GET $BASE_URL/api/auth/me \
  -H "Authorization: Bearer $TOKEN" | jq '.'
echo ""

# Create document
echo "4. Creating document..."
curl -s -X POST $BASE_URL/api/documents \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Document","content":"Test Content"}' | jq '.'
echo ""

# Get documents
echo "5. Getting all documents..."
curl -s -X GET $BASE_URL/api/documents \
  -H "Authorization: Bearer $TOKEN" | jq '.'
echo ""

echo "=== Testing Complete ==="
```

Make it executable and run:
```bash
chmod +x test-api.sh
./test-api.sh
```

## Troubleshooting

### Application won't start
- Check if port 8080 is already in use
- Check Java version (needs Java 17+)
- Run `./gradlew clean build` first

### 401 Unauthorized errors
- Make sure you're including the token: `Authorization: Bearer YOUR_TOKEN`
- Check if token is expired (tokens expire after 24 hours)
- Make sure there's a space after "Bearer"

### 403 Forbidden errors
- Check if user has the required role/permission
- Admin endpoints require ADMIN role
- Document operations require appropriate permissions

### Connection refused
- Make sure the application is running
- Check the port (default is 8080)
- Verify the URL is correct

