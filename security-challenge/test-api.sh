#!/bin/bash

# API Testing Script for Security Challenge
# Make sure the application is running on http://localhost:8080

BASE_URL="http://localhost:8080"
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "=========================================="
echo "  Security Challenge API Testing"
echo "=========================================="
echo ""

# Check if jq is installed
if ! command -v jq &> /dev/null; then
    echo -e "${YELLOW}Warning: jq is not installed. Install it for better JSON formatting:${NC}"
    echo "  Ubuntu/Debian: sudo apt-get install jq"
    echo "  macOS: brew install jq"
    echo ""
    USE_JQ=false
else
    USE_JQ=true
fi

# Function to print JSON (with or without jq)
print_json() {
    if [ "$USE_JQ" = true ]; then
        echo "$1" | jq '.'
    else
        echo "$1"
    fi
}

# Test 1: Register a new user
echo -e "${GREEN}Test 1: Registering new user...${NC}"
REGISTER_RESPONSE=$(curl -s -X POST $BASE_URL/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"TestPass123"}')

print_json "$REGISTER_RESPONSE"
echo ""

# Check if registration was successful
if echo "$REGISTER_RESPONSE" | grep -q '"success":true'; then
    echo -e "${GREEN}✓ Registration successful${NC}"
else
    echo -e "${RED}✗ Registration failed${NC}"
    exit 1
fi

# Test 2: Login
echo -e "${GREEN}Test 2: Logging in...${NC}"
LOGIN_RESPONSE=$(curl -s -X POST $BASE_URL/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"TestPass123"}')

print_json "$LOGIN_RESPONSE"
echo ""

# Extract token
if [ "$USE_JQ" = true ]; then
    TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.data.accessToken')
else
    TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)
fi

if [ -z "$TOKEN" ] || [ "$TOKEN" = "null" ]; then
    echo -e "${RED}✗ Login failed - could not extract token${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Login successful${NC}"
echo -e "${YELLOW}Token: ${TOKEN:0:50}...${NC}"
echo ""

# Test 3: Get current user
echo -e "${GREEN}Test 3: Getting current user info...${NC}"
ME_RESPONSE=$(curl -s -X GET $BASE_URL/api/auth/me \
  -H "Authorization: Bearer $TOKEN")

print_json "$ME_RESPONSE"
echo ""

if echo "$ME_RESPONSE" | grep -q '"success":true'; then
    echo -e "${GREEN}✓ Get current user successful${NC}"
else
    echo -e "${RED}✗ Get current user failed${NC}"
fi
echo ""

# Test 4: Create a document
echo -e "${GREEN}Test 4: Creating a document...${NC}"
CREATE_DOC_RESPONSE=$(curl -s -X POST $BASE_URL/api/documents \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"My Test Document","content":"This is test content"}')

print_json "$CREATE_DOC_RESPONSE"
echo ""

if echo "$CREATE_DOC_RESPONSE" | grep -q '"success":true'; then
    echo -e "${GREEN}✓ Document created successfully${NC}"
else
    echo -e "${RED}✗ Document creation failed${NC}"
fi
echo ""

# Test 5: Get all documents
echo -e "${GREEN}Test 5: Getting all documents...${NC}"
GET_DOCS_RESPONSE=$(curl -s -X GET $BASE_URL/api/documents \
  -H "Authorization: Bearer $TOKEN")

print_json "$GET_DOCS_RESPONSE"
echo ""

if echo "$GET_DOCS_RESPONSE" | grep -q '"success":true'; then
    echo -e "${GREEN}✓ Get documents successful${NC}"
else
    echo -e "${RED}✗ Get documents failed${NC}"
fi
echo ""

# Test 6: Access without token (should fail)
echo -e "${GREEN}Test 6: Testing unauthorized access (should fail)...${NC}"
UNAUTH_RESPONSE=$(curl -s -X GET $BASE_URL/api/auth/me)

print_json "$UNAUTH_RESPONSE"
echo ""

if echo "$UNAUTH_RESPONSE" | grep -q '"error":"UNAUTHORIZED"'; then
    echo -e "${GREEN}✓ Unauthorized access correctly rejected${NC}"
else
    echo -e "${RED}✗ Security issue: Unauthorized access was allowed!${NC}"
fi
echo ""

echo "=========================================="
echo -e "${GREEN}Testing Complete!${NC}"
echo "=========================================="
echo ""
echo "Sample users you can use:"
echo "  - john / Password123 (USER role)"
echo "  - jane / Password123 (USER role)"
echo "  - admin / AdminPass123 (ADMIN role)"
echo ""
echo "Try these commands manually:"
echo "  # Login as admin"
echo "  curl -X POST $BASE_URL/api/auth/login -H 'Content-Type: application/json' -d '{\"username\":\"admin\",\"password\":\"AdminPass123\"}'"
echo ""
echo "  # Access admin endpoint"
echo "  curl -X GET $BASE_URL/api/documents/admin/all -H 'Authorization: Bearer YOUR_ADMIN_TOKEN'"

