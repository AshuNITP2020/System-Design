┌─────────────────────────────────────────────────────────────┐
│  LOGIN (Token Created Once)                                 │
└─────────────────────────────────────────────────────────────┘
POST /api/auth/login
Body: {username: "john", password: "pass123"}
↓
Server creates token with payload:
{
  "userId": 1,
  "role": "USER",
  "sub": "john",
  "iat": 1638567200,
  "exp": 1638653600
}
↓
Signature calculated: HMAC(payload, secret) = "ABC123..."
↓
Token returned: "eyJhbG...ABC123..."

┌─────────────────────────────────────────────────────────────┐
│  REQUEST 1: GET /api/profile                                │
└─────────────────────────────────────────────────────────────┘
Authorization: Bearer eyJhbG...ABC123...
Body: (empty)
↓
Server validates: HMAC(payload, secret) = "ABC123..." ✅
Same token, same signature!

┌─────────────────────────────────────────────────────────────┐
│  REQUEST 2: POST /api/documents                            │
└─────────────────────────────────────────────────────────────┘
Authorization: Bearer eyJhbG...ABC123...
Body: {"title": "Doc", "content": "..."}  ← Different body!
↓
Server validates: HMAC(payload, secret) = "ABC123..." ✅
Same token, same signature!
(HTTP body doesn't affect JWT validation)

┌─────────────────────────────────────────────────────────────┐
│  REQUEST 3: DELETE /api/documents/123                       │
└─────────────────────────────────────────────────────────────┘
Authorization: Bearer eyJhbG...ABC123...
Body: (empty)
↓
Server validates: HMAC(payload, secret) = "ABC123..." ✅
Same token, same signature!