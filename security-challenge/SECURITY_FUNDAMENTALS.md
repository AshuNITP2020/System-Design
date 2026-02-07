# 🔐 Web Application & API Security - From Basics to Advanced

## Part 1: The Absolute Basics

---

### Chapter 1: Why Security Matters

#### The Real-World Analogy

Think of your web application as a **bank building**:

```
┌─────────────────────────────────────────────────────────────────┐
│                        🏦 YOUR BANK (Web App)                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   🚪 Front Door ──────────► Who can enter? (Authentication)     │
│                                                                  │
│   🔑 Vault Access ────────► Who can access what? (Authorization)│
│                                                                  │
│   📹 Security Cameras ────► Who did what? (Auditing/Logging)    │
│                                                                  │
│   🔒 Safe Deposit Boxes ──► How is data protected? (Encryption) │
│                                                                  │
│   👮 Security Guards ─────► Who monitors threats? (Monitoring)  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

#### What Happens Without Security?

Without proper security, anyone can:
- **Read** sensitive data (customer info, financial records)
- **Modify** data (change account balances, delete records)
- **Impersonate** other users (pretend to be admin)
- **Disrupt** services (crash the application)

---

### Chapter 2: The CIA Triad - Foundation of Security

Every security decision you make should protect one or more of these:

```
                    CONFIDENTIALITY
                          🔒
                         /   \
                        /     \
                       /       \
                      /    📊   \
                     /   DATA    \
                    /             \
                   /               \
                  🛡️───────────────🔄
            INTEGRITY          AVAILABILITY
```

#### 1. Confidentiality 🔒
> "Only authorized people can SEE the data"

**Examples:**
- Your password is hidden (even from admins)
- Your bank balance is only visible to you
- Medical records are only accessible to your doctor

**Threats:**
- Data breaches
- Eavesdropping
- Unauthorized access

**Protections:**
- Encryption
- Access controls
- Authentication

---

#### 2. Integrity 🛡️
> "Data hasn't been MODIFIED by unauthorized people"

**Examples:**
- Your bank balance can't be changed by hackers
- Your medical records can't be altered
- Messages aren't modified in transit

**Threats:**
- Data tampering
- Man-in-the-middle attacks
- SQL injection

**Protections:**
- Digital signatures
- Checksums/hashes
- Input validation

---

#### 3. Availability 🔄
> "Data and systems are ACCESSIBLE when needed"

**Examples:**
- The banking website works during business hours
- Your email is always accessible
- Emergency services are never down

**Threats:**
- DDoS attacks
- Server crashes
- Ransomware

**Protections:**
- Load balancing
- Redundancy
- Backups

---

### Chapter 3: Authentication vs Authorization

This is the **most fundamental concept** to understand:

```
┌─────────────────────────────────────────────────────────────────┐
│                                                                  │
│  AUTHENTICATION                    AUTHORIZATION                 │
│  (AuthN)                           (AuthZ)                       │
│                                                                  │
│  ┌─────────────┐                   ┌─────────────┐              │
│  │   WHO       │                   │   WHAT      │              │
│  │   are you?  │                   │   can you   │              │
│  │             │                   │   do?       │              │
│  └─────────────┘                   └─────────────┘              │
│                                                                  │
│  "Prove your identity"             "Check your permissions"      │
│                                                                  │
│  Examples:                         Examples:                     │
│  • Username + Password             • Can view documents?         │
│  • Fingerprint                     • Can delete users?           │
│  • Face ID                         • Can access admin panel?     │
│  • Smart card                      • Can modify settings?        │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

#### Real-World Analogy: Airport Security

```
Step 1: AUTHENTICATION (Ticket Counter)
┌──────────────────────────────────────────┐
│ 🛂 "Show me your ID"                     │
│                                          │
│ Passport + Ticket = Identity Verified    │
│                                          │
│ Now I know WHO you are                   │
└──────────────────────────────────────────┘
              │
              ▼
Step 2: AUTHORIZATION (Boarding Gate)
┌──────────────────────────────────────────┐
│ ✈️ "Let me check your boarding pass"     │
│                                          │
│ Economy ticket → Seat 32A (not Business) │
│                                          │
│ Now I know WHAT you can access           │
└──────────────────────────────────────────┘
```

#### In Web Applications

```
Request: GET /api/admin/users

Step 1: Authentication
├── Is there a valid token/session? 
├── If NO  → 401 Unauthorized ("Who are you?")
└── If YES → Continue to Authorization

Step 2: Authorization
├── Does this user have ADMIN role?
├── If NO  → 403 Forbidden ("You can't do this")
└── If YES → Return the data ✅
```

**Key Difference:**
- **401 Unauthorized** = "I don't know who you are" (Authentication failed)
- **403 Forbidden** = "I know who you are, but you can't do this" (Authorization failed)

---

### Chapter 4: Types of Credentials

#### 4.1 Something You KNOW (Knowledge Factor)

```
┌─────────────────────────────────────┐
│ 🧠 KNOWLEDGE-BASED                  │
├─────────────────────────────────────┤
│ • Passwords                         │
│ • PINs                              │
│ • Security questions                │
│ • Passphrases                       │
├─────────────────────────────────────┤
│ Pros: Easy to implement             │
│ Cons: Can be guessed, stolen, phished│
└─────────────────────────────────────┘
```

#### 4.2 Something You HAVE (Possession Factor)

```
┌─────────────────────────────────────┐
│ 📱 POSSESSION-BASED                 │
├─────────────────────────────────────┤
│ • Phone (SMS codes)                 │
│ • Hardware tokens (YubiKey)         │
│ • Smart cards                       │
│ • Authenticator apps                │
├─────────────────────────────────────┤
│ Pros: Hard to steal remotely        │
│ Cons: Can be lost, expensive        │
└─────────────────────────────────────┘
```

#### 4.3 Something You ARE (Inherence Factor)

```
┌─────────────────────────────────────┐
│ 👆 BIOMETRIC-BASED                  │
├─────────────────────────────────────┤
│ • Fingerprint                       │
│ • Face recognition                  │
│ • Voice recognition                 │
│ • Retina scan                       │
├─────────────────────────────────────┤
│ Pros: Can't be forgotten            │
│ Cons: Can't be changed if compromised│
└─────────────────────────────────────┘
```

#### Multi-Factor Authentication (MFA)

Combines 2 or more factors:

```
┌─────────────────────────────────────────────────────────┐
│                 TWO-FACTOR AUTHENTICATION               │
├─────────────────────────────────────────────────────────┤
│                                                         │
│   Password (Something you KNOW)                         │
│        +                                                │
│   SMS Code (Something you HAVE)                         │
│        =                                                │
│   🔐 Much stronger security!                            │
│                                                         │
│   Even if password is stolen, attacker needs phone     │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

---

### Chapter 5: Session vs Token Authentication

These are the two main approaches to "remembering" that a user is authenticated:

#### 5.1 Session-Based Authentication (Traditional)

```
                     SESSION-BASED FLOW
                     
┌────────┐                              ┌────────┐
│ Client │                              │ Server │
│(Browser)│                              │        │
└───┬────┘                              └───┬────┘
    │                                       │
    │  1. POST /login                       │
    │     {username, password}              │
    │ ─────────────────────────────────────►│
    │                                       │
    │                           2. Validate credentials
    │                                       │
    │                           3. Create SESSION
    │                              Store in server memory:
    │                              sessions["abc123"] = {
    │                                userId: 1,
    │                                role: "USER",
    │                                expiry: "..."
    │                              }
    │                                       │
    │  4. Response + Cookie                 │
    │     Set-Cookie: SESSIONID=abc123      │
    │◄───────────────────────────────────── │
    │                                       │
    │  5. GET /api/profile                  │
    │     Cookie: SESSIONID=abc123          │
    │ ─────────────────────────────────────►│
    │                           6. Look up session["abc123"]
    │                              Found! User is authenticated
    │                                       │
    │  7. Response: {name: "John", ...}     │
    │◄───────────────────────────────────── │
```

**How it works:**
1. User logs in with credentials
2. Server creates a **session** (stored on server)
3. Server sends back a **session ID** (small identifier)
4. Browser automatically sends session ID with every request (in cookies)
5. Server looks up session ID to find user info

**Pros:**
- Simple to implement
- Server has full control (can invalidate anytime)
- Session data stored securely on server

**Cons:**
- Server must store all sessions (memory usage)
- Hard to scale (need shared session storage)
- Requires sticky sessions or session replication

---

#### 5.2 Token-Based Authentication (Modern)

```
                     TOKEN-BASED FLOW (JWT)
                     
┌────────┐                              ┌────────┐
│ Client │                              │ Server │
│(Browser)│                              │        │
└───┬────┘                              └───┬────┘
    │                                       │
    │  1. POST /login                       │
    │     {username, password}              │
    │ ─────────────────────────────────────►│
    │                                       │
    │                           2. Validate credentials
    │                                       │
    │                           3. Generate JWT TOKEN
    │                              (Contains user info)
    │                              {userId, role, expiry}
    │                              + Digital Signature
    │                                       │
    │  4. Response: {token: "eyJhbG..."}    │
    │◄───────────────────────────────────── │
    │                                       │
    │  5. Store token (localStorage)        │
    │                                       │
    │  6. GET /api/profile                  │
    │     Authorization: Bearer eyJhbG...   │
    │ ─────────────────────────────────────►│
    │                           7. Verify token signature
    │                              Extract user info from token
    │                              (NO database lookup needed!)
    │                                       │
    │  8. Response: {name: "John", ...}     │
    │◄───────────────────────────────────── │
```

**How it works:**
1. User logs in with credentials
2. Server creates a **token** containing user info (self-contained)
3. Token is **signed** so it can't be tampered with
4. Client stores token and sends it with each request
5. Server **verifies signature** and extracts user info from token itself

**Pros:**
- **Stateless** - server doesn't store anything
- **Scalable** - any server can verify the token
- Works great for APIs and microservices

**Cons:**
- Can't easily invalidate tokens (until they expire)
- Token size is larger than session ID
- Must handle token refresh

---

#### When to Use Which?

```
┌─────────────────────────────────────────────────────────────────┐
│                  WHICH APPROACH TO USE?                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  SESSION-BASED is better for:                                   │
│  ┌───────────────────────────────────┐                          │
│  │ • Traditional web apps            │                          │
│  │ • Apps with server-rendered pages │                          │
│  │ • When you need instant logout    │                          │
│  │ • Simple, monolithic applications │                          │
│  └───────────────────────────────────┘                          │
│                                                                  │
│  TOKEN-BASED is better for:                                     │
│  ┌───────────────────────────────────┐                          │
│  │ • REST APIs                       │                          │
│  │ • Single Page Applications (SPA)  │                          │
│  │ • Mobile applications             │                          │
│  │ • Microservices architecture      │                          │
│  │ • Cross-domain authentication     │                          │
│  └───────────────────────────────────┘                          │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## Part 2: Understanding Threats (OWASP Top 10)

---

### Chapter 6: What is OWASP?

**OWASP** = Open Web Application Security Project

A non-profit organization that produces:
- Security guidelines
- Tools
- The famous **"Top 10"** list of web application security risks

The Top 10 is updated every few years based on real-world data about vulnerabilities.

---

### Chapter 7: The OWASP Top 10 Explained

#### A01: Broken Access Control 🚨

> "Users can do things they shouldn't be allowed to do"

**The Problem:**
```
User A logs in and sees their profile at:
  /api/users/100/profile

What if they change the URL to:
  /api/users/101/profile  ← Another user's profile!
  
If the server doesn't check ownership, User A sees User B's data!
```

**Real-World Impact:**
- View other users' private data
- Modify other users' records
- Access admin functionality

**Types of Broken Access Control:**

```
1. VERTICAL Privilege Escalation
   ┌─────────────────────────────────────┐
   │ Regular User ──► Admin Functions    │
   │                                     │
   │ Example: User accesses /admin/users │
   └─────────────────────────────────────┘

2. HORIZONTAL Privilege Escalation
   ┌─────────────────────────────────────┐
   │ User A ──► User B's Data            │
   │                                     │
   │ Example: User 1 views User 2's docs │
   └─────────────────────────────────────┘

3. INSECURE Direct Object Reference (IDOR)
   ┌─────────────────────────────────────┐
   │ Manipulating IDs in URLs/params     │
   │                                     │
   │ /order?id=1001 → /order?id=1002     │
   └─────────────────────────────────────┘
```

---

#### A02: Cryptographic Failures 🔐

> "Sensitive data is not properly protected"

**The Problem:**
```
Scenario 1: Passwords stored in plain text
┌──────────────────────────────────────┐
│ Database Table: users                │
├────────────┬─────────────────────────┤
│ username   │ password                │
├────────────┼─────────────────────────┤
│ john       │ MyPassword123  ← BAD!   │
│ jane       │ SecretPass456  ← BAD!   │
└────────────┴─────────────────────────┘

If database is breached, ALL passwords exposed!

Scenario 2: Passwords properly hashed
┌──────────────────────────────────────┐
│ username   │ password_hash           │
├────────────┼─────────────────────────┤
│ john       │ $2a$12$LQv3c1yq...      │
│ jane       │ $2a$12$N9qo8uLO...      │
└────────────┴─────────────────────────┘

Even if breached, passwords are protected!
```

**What Should Be Encrypted:**
- Passwords (use hashing: bcrypt, argon2)
- Credit card numbers
- Personal identifiable information (PII)
- Medical records
- Data in transit (HTTPS)

---

#### A03: Injection 💉

> "Untrusted data is sent to an interpreter as part of a command"

**SQL Injection - The Classic Attack:**

```
Normal Login:
┌────────────────────────────────────────────────────┐
│ Username: john                                     │
│ Password: secret123                                │
│                                                    │
│ Query: SELECT * FROM users                         │
│        WHERE username='john' AND password='secret' │
│                                                    │
│ Result: Returns john's record (if password correct)│
└────────────────────────────────────────────────────┘

SQL Injection Attack:
┌────────────────────────────────────────────────────┐
│ Username: ' OR '1'='1                              │
│ Password: ' OR '1'='1                              │
│                                                    │
│ Query: SELECT * FROM users                         │
│        WHERE username='' OR '1'='1'                │
│        AND password='' OR '1'='1'                  │
│                                                    │
│ Result: Returns ALL users! '1'='1' is always true! │
└────────────────────────────────────────────────────┘
```

**Other Types of Injection:**
- **Command Injection**: Injecting OS commands
- **LDAP Injection**: Attacking directory services
- **NoSQL Injection**: Attacking MongoDB, etc.
- **XPath Injection**: Attacking XML queries

---

#### A04: Insecure Design 🏗️

> "Security wasn't considered during design phase"

**The Problem:**
Security is an afterthought, not built into the design.

```
Bad Design Example: Password Reset
┌──────────────────────────────────────────────────────┐
│ 1. User clicks "Forgot Password"                     │
│ 2. System asks security question: "Pet's name?"      │
│ 3. Attacker checks Facebook → Finds pet photos       │
│ 4. Attacker answers: "Fluffy"                        │
│ 5. Attacker gains access!                            │
│                                                      │
│ Problem: Security questions are easily researched    │
└──────────────────────────────────────────────────────┘

Good Design Example:
┌──────────────────────────────────────────────────────┐
│ 1. User clicks "Forgot Password"                     │
│ 2. System sends one-time link to registered email    │
│ 3. Link expires in 15 minutes                        │
│ 4. Link can only be used once                        │
│ 5. Much harder to exploit!                           │
└──────────────────────────────────────────────────────┘
```

---

#### A05: Security Misconfiguration ⚙️

> "Default settings, unnecessary features, or improper configurations"

**Common Misconfigurations:**

```
1. Default Credentials
┌────────────────────────────────┐
│ Admin Panel Login              │
│ Username: admin                │
│ Password: admin                │
│ ← Still using defaults!        │
└────────────────────────────────┘

2. Verbose Error Messages
┌────────────────────────────────┐
│ Error: SQLException            │
│ Query: SELECT * FROM users     │
│ WHERE id = 'abc'               │
│ Stack trace: ...               │
│ ← Reveals database structure!  │
└────────────────────────────────┘

3. Debug Mode in Production
┌────────────────────────────────┐
│ DEBUG=true                     │
│ Shows: Source code paths       │
│ Shows: Environment variables   │
│ Shows: Database connections    │
│ ← Gold mine for attackers!     │
└────────────────────────────────┘

4. Unnecessary Services Running
┌────────────────────────────────┐
│ FTP Server: Running (unused)   │
│ SSH: Open to all IPs           │
│ Database: Publicly accessible  │
│ ← Each is an attack vector!    │
└────────────────────────────────┘
```

---

#### A06: Vulnerable Components 📦

> "Using libraries with known vulnerabilities"

**The Problem:**
```
Your app uses:
├── Spring Boot 2.5.0 ✅ (current)
├── Log4j 2.14.1 ← VULNERABLE! (Log4Shell)
├── Jackson 2.9.8 ← Has known CVEs
└── Old jQuery 1.x ← XSS vulnerabilities

You're only as secure as your weakest dependency!
```

**Real Example - Log4Shell (2021):**
- Affected millions of applications
- Allowed remote code execution
- One vulnerable library = complete system compromise

---

#### A07: Authentication Failures 🔑

> "Weak authentication mechanisms"

**Common Authentication Failures:**

```
1. Weak Password Policy
┌────────────────────────────────┐
│ Password: 123456    ← Accepted!│
│ Password: password  ← Accepted!│
│ Password: qwerty    ← Accepted!│
└────────────────────────────────┘

2. No Brute Force Protection
┌────────────────────────────────┐
│ Login attempt 1: Failed        │
│ Login attempt 2: Failed        │
│ ...                            │
│ Login attempt 10000: Success!  │
│ ← No lockout or rate limiting! │
└────────────────────────────────┘

3. Session Fixation
┌────────────────────────────────┐
│ Attacker sets victim's session │
│ ID before login. After login,  │
│ attacker knows the session ID! │
└────────────────────────────────┘

4. Insecure Session Storage
┌────────────────────────────────┐
│ Token stored in URL parameters │
│ Session ID in logs             │
│ Cookies without HttpOnly flag  │
└────────────────────────────────┘
```

---

#### A08: Software Integrity Failures 🔄

> "Code and infrastructure updates without integrity verification"

**The Problem:**
```
Scenario: Auto-update mechanism
┌─────────────────────────────────────────────────────┐
│ 1. App checks for updates from update.example.com  │
│ 2. Attacker compromises update server              │
│ 3. Attacker uploads malicious update               │
│ 4. App downloads and installs malware!             │
│                                                    │
│ Solution: Sign updates and verify signatures       │
└─────────────────────────────────────────────────────┘
```

---

#### A09: Logging & Monitoring Failures 📊

> "Attacks go undetected due to insufficient logging"

**The Problem:**
```
Without logging:
┌─────────────────────────────────────────────────────┐
│ Day 1: Attacker probes system                       │
│ Day 2: Attacker finds vulnerability                 │
│ Day 3: Attacker exploits vulnerability              │
│ Day 30: You notice something's wrong                │
│ Day 31: You have no logs to investigate!            │
└─────────────────────────────────────────────────────┘

With proper logging:
┌─────────────────────────────────────────────────────┐
│ Alert: 50 failed login attempts from IP 1.2.3.4    │
│ Alert: Unusual access pattern detected             │
│ Alert: Admin API accessed from unknown location    │
│                                                    │
│ → Immediate investigation and response!            │
└─────────────────────────────────────────────────────┘
```

**What to Log:**
- Authentication events (success/failure)
- Authorization failures
- Input validation failures
- Server errors
- Changes to sensitive data

---

#### A10: Server-Side Request Forgery (SSRF) 🌐

> "Server is tricked into making requests to unintended locations"

**The Problem:**
```
Normal feature: Fetch URL preview
┌─────────────────────────────────────────────────────┐
│ User submits: https://example.com/article          │
│ Server fetches URL and shows preview               │
│ ✅ Intended behavior                                │
└─────────────────────────────────────────────────────┘

SSRF Attack:
┌─────────────────────────────────────────────────────┐
│ User submits: http://localhost:8080/admin          │
│ Server fetches internal admin panel!               │
│ ❌ Attacker accesses internal systems!              │
│                                                    │
│ Or even worse:                                     │
│ User submits: http://169.254.169.254/metadata      │
│ Server fetches cloud metadata (AWS credentials!)   │
└─────────────────────────────────────────────────────┘
```

---

## Part 3: HTTP Security Basics

---

### Chapter 8: Understanding HTTP Headers for Security

#### Common Security Headers

```
┌─────────────────────────────────────────────────────────────────┐
│                    SECURITY HEADERS                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│ 1. Content-Security-Policy (CSP)                                │
│    └─ Prevents XSS by controlling which scripts can run         │
│    └─ Example: Content-Security-Policy: default-src 'self'      │
│                                                                  │
│ 2. X-Frame-Options                                              │
│    └─ Prevents clickjacking by blocking framing                 │
│    └─ Example: X-Frame-Options: DENY                            │
│                                                                  │
│ 3. X-Content-Type-Options                                       │
│    └─ Prevents MIME type sniffing                               │
│    └─ Example: X-Content-Type-Options: nosniff                  │
│                                                                  │
│ 4. Strict-Transport-Security (HSTS)                             │
│    └─ Forces HTTPS connections                                  │
│    └─ Example: Strict-Transport-Security: max-age=31536000      │
│                                                                  │
│ 5. X-XSS-Protection                                             │
│    └─ Enables browser's XSS filter                              │
│    └─ Example: X-XSS-Protection: 1; mode=block                  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

### Chapter 9: CORS (Cross-Origin Resource Sharing)

#### The Same-Origin Policy

Browsers enforce a security rule: scripts can only access resources from the **same origin**.

```
Origin = Protocol + Domain + Port

https://example.com:443/page
  │         │         │
  └── Protocol (https)
            └── Domain (example.com)
                      └── Port (443)
```

**Same Origin Examples:**
```
✅ https://example.com/page1  →  https://example.com/page2   (Same)
❌ https://example.com       →  http://example.com          (Different protocol)
❌ https://example.com       →  https://api.example.com     (Different subdomain)
❌ https://example.com       →  https://example.com:8080    (Different port)
```

#### Why CORS Exists

```
Without CORS (Same-Origin Policy):
┌──────────────────────────────────────────────────────────────┐
│ You're on: https://bank.com                                  │
│ Logged in with session cookie                                │
│                                                              │
│ You visit: https://evil.com                                  │
│ Evil.com tries: fetch('https://bank.com/transfer?to=hacker') │
│                                                              │
│ Browser BLOCKS this request!                                 │
│ Your money is safe.                                          │
└──────────────────────────────────────────────────────────────┘

With CORS (Controlled Access):
┌──────────────────────────────────────────────────────────────┐
│ Your frontend: https://myapp.com                             │
│ Your API: https://api.myapp.com                              │
│                                                              │
│ API responds with:                                           │
│ Access-Control-Allow-Origin: https://myapp.com               │
│                                                              │
│ Browser ALLOWS the request because API explicitly permits it │
└──────────────────────────────────────────────────────────────┘
```

#### CORS Flow

```
       SIMPLE REQUEST                      PREFLIGHT REQUEST
       (GET, simple POST)                  (PUT, DELETE, custom headers)

┌────────┐         ┌────────┐       ┌────────┐         ┌────────┐
│ Browser│         │ Server │       │ Browser│         │ Server │
└───┬────┘         └───┬────┘       └───┬────┘         └───┬────┘
    │                  │                │                  │
    │  GET /api/data   │                │ OPTIONS /api/data│ (Preflight)
    │ ────────────────►│                │ ────────────────►│
    │                  │                │                  │
    │  Response +      │                │  CORS headers    │
    │  CORS headers    │                │ ◄────────────────│
    │ ◄────────────────│                │                  │
    │                  │                │  Actual Request  │
                                        │  PUT /api/data   │
                                        │ ────────────────►│
                                        │                  │
                                        │  Response        │
                                        │ ◄────────────────│
```

---

### Chapter 10: CSRF (Cross-Site Request Forgery)

#### How CSRF Works

```
Step 1: You log into your bank
┌─────────────────────────────────────────┐
│ bank.com sets cookie: sessionId=xyz     │
│ You're now authenticated                │
└─────────────────────────────────────────┘

Step 2: You visit evil.com (in another tab)
┌─────────────────────────────────────────┐
│ Evil.com contains hidden form:          │
│                                         │
│ <form action="bank.com/transfer">       │
│   <input name="to" value="hacker">      │
│   <input name="amount" value="10000">   │
│ </form>                                 │
│ <script>form.submit()</script>          │
└─────────────────────────────────────────┘

Step 3: Browser sends request
┌─────────────────────────────────────────┐
│ POST bank.com/transfer                  │
│ Cookie: sessionId=xyz  ← Auto-attached! │
│ Body: to=hacker&amount=10000            │
│                                         │
│ Bank sees valid session → Transfers $$$ │
└─────────────────────────────────────────┘
```

#### CSRF Protection: Tokens

```
With CSRF Token:
┌─────────────────────────────────────────────────────────────────┐
│ 1. Server generates random token per session                    │
│ 2. Token is included in forms as hidden field                   │
│ 3. Server validates token on POST requests                      │
│                                                                 │
│ <form action="/transfer">                                       │
│   <input type="hidden" name="_csrf" value="random-token-xyz">   │
│   ...                                                           │
│ </form>                                                         │
│                                                                 │
│ Evil.com can't know this token, so attack fails!                │
└─────────────────────────────────────────────────────────────────┘
```

#### CSRF and APIs

```
For token-based APIs (JWT):
┌─────────────────────────────────────────────────────────────────┐
│ CSRF is NOT a concern because:                                  │
│                                                                 │
│ • Tokens are sent in Authorization header                       │
│ • Headers are NOT automatically attached by browser             │
│ • Evil.com can't add Authorization header to cross-origin req   │
│                                                                 │
│ This is why APIs often disable CSRF protection                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## Part 4: Password Security

---

### Chapter 11: Why Plain Text Passwords Are Dangerous

```
Scenario: Database Breach
┌─────────────────────────────────────────────────────────────────┐
│                                                                  │
│ Plain Text Storage:                                             │
│ ┌──────────┬─────────────────┐                                  │
│ │ username │ password        │                                  │
│ ├──────────┼─────────────────┤                                  │
│ │ john     │ MySecret123     │ ← Immediately compromised!       │
│ │ jane     │ Password456     │                                  │
│ └──────────┴─────────────────┘                                  │
│                                                                  │
│ Hashed Storage:                                                 │
│ ┌──────────┬─────────────────────────────────────┐              │
│ │ username │ password_hash                        │              │
│ ├──────────┼─────────────────────────────────────┤              │
│ │ john     │ $2a$12$LQv3c1yqBWVHxkd0LHAkCO...   │ ← Protected! │
│ │ jane     │ $2a$12$N9qo8uLOickgx2ZMRZoMye...   │              │
│ └──────────┴─────────────────────────────────────┘              │
│                                                                  │
│ Even if attacker gets hashes, they can't reverse them!          │
└─────────────────────────────────────────────────────────────────┘
```

---

### Chapter 12: Hashing vs Encryption

```
┌─────────────────────────────────────────────────────────────────┐
│                   HASHING vs ENCRYPTION                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  HASHING (One-way)                                              │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │  "password123" ──► Hash Function ──► "a1b2c3d4e5..."    │    │
│  │                                                          │    │
│  │  ❌ Cannot reverse: "a1b2c3d4e5..." ──► ?               │    │
│  │  ✅ Same input always gives same output                  │    │
│  │  ✅ Different inputs give different outputs              │    │
│  │                                                          │    │
│  │  Use for: Passwords, data integrity                      │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                  │
│  ENCRYPTION (Two-way)                                           │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │  "Hello" + Key ──► Encrypt ──► "X7#kL9@m"               │    │
│  │                                                          │    │
│  │  ✅ Can reverse: "X7#kL9@m" + Key ──► "Hello"           │    │
│  │                                                          │    │
│  │  Use for: Data that needs to be read later              │    │
│  │  (Credit cards, personal info, messages)                 │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

### Chapter 13: Password Hashing Algorithms

```
Evolution of Password Hashing:

❌ MD5 (Don't use)
   └─ Fast = Bad for passwords
   └─ Rainbow tables exist
   └─ Collisions found

❌ SHA-1 (Don't use)
   └─ Fast = Bad for passwords
   └─ Cryptographically broken

⚠️ SHA-256 (Not ideal for passwords)
   └─ Still too fast
   └─ Needs additional salting

✅ bcrypt (Recommended)
   └─ Slow by design (configurable work factor)
   └─ Built-in salt
   └─ Widely supported

✅ Argon2 (Best, newer)
   └─ Memory-hard (resists GPU attacks)
   └─ Configurable time, memory, parallelism
   └─ Winner of Password Hashing Competition

✅ scrypt (Good alternative)
   └─ Memory-hard
   └─ Good for high-security applications
```

---

### Chapter 14: Salting

```
Without Salt:
┌─────────────────────────────────────────────────────────────────┐
│ hash("password123") = "ef92b778baf..."                          │
│ hash("password123") = "ef92b778baf..."  ← Same hash!            │
│                                                                  │
│ Problem: Attackers can use precomputed tables (rainbow tables)  │
│ to look up common passwords instantly!                          │
└─────────────────────────────────────────────────────────────────┘

With Salt:
┌─────────────────────────────────────────────────────────────────┐
│ salt1 = "x7k9m2"  (random, unique per user)                     │
│ salt2 = "p3n8q1"  (random, unique per user)                     │
│                                                                  │
│ hash("password123" + salt1) = "a1b2c3d4..."                     │
│ hash("password123" + salt2) = "z9y8x7w6..."  ← Different!       │
│                                                                  │
│ Even same password has different hashes!                        │
│ Rainbow tables become useless.                                  │
└─────────────────────────────────────────────────────────────────┘

BCrypt includes salt automatically:
┌─────────────────────────────────────────────────────────────────┐
│ $2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4bNxUpNvfSvuDHfi  │
│  │   │  └─────────────────────┘└──────────────────────────────┘ │
│  │   │          Salt                    Hash                    │
│  │   └── Cost factor (12)                                       │
│  └── Algorithm (bcrypt)                                         │
└─────────────────────────────────────────────────────────────────┘
```

---

## Part 5: JWT Deep Dive

---

### Chapter 15: JWT Structure

```
JWT = Header.Payload.Signature

eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4iLCJpYXQiOjE1MTYyMzkwMjJ9.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
└─────────────── Header ───────────────┘└────────────────── Payload ──────────────────┘└──────────────── Signature ────────────────┘
```

#### Header (Metadata)
```json
{
  "alg": "HS256",    // Algorithm: HMAC-SHA256
  "typ": "JWT"       // Type: JWT
}
// Base64URL encoded → eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
```

#### Payload (Claims)
```json
{
  "sub": "1234567890",     // Subject (user ID)
  "name": "John Doe",      // Custom claim
  "role": "ADMIN",         // Custom claim
  "iat": 1516239022,       // Issued At (timestamp)
  "exp": 1516325422        // Expiration (timestamp)
}
// Base64URL encoded → eyJzdWIiOiIxMjM0NTY3ODkwIi...
```

#### Signature (Verification)
```
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret_key
)
// Result → SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

---

### Chapter 16: Why JWTs Are Secure

```
┌─────────────────────────────────────────────────────────────────┐
│                    JWT SECURITY                                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│ Q: Can attackers read the payload?                              │
│ A: YES! JWT is NOT encrypted by default.                        │
│    The payload is just Base64 encoded (easily decoded).         │
│    ⚠️ Don't put secrets in the payload!                         │
│                                                                  │
│ Q: Can attackers modify the payload?                            │
│ A: NO! Any change invalidates the signature.                    │
│                                                                  │
│    Original: {"role": "USER"}                                   │
│    Tampered: {"role": "ADMIN"}                                  │
│                                                                  │
│    Server recalculates signature:                               │
│    Expected signature ≠ Token signature                         │
│    → Token rejected!                                            │
│                                                                  │
│ Q: Can attackers create fake tokens?                            │
│ A: NO! They don't know the secret key.                          │
│    Without the key, they can't create valid signatures.         │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

### Chapter 17: JWT Best Practices

```
✅ DO:
┌─────────────────────────────────────────────────────────────────┐
│ • Use short expiration times (15 min - 1 hour)                  │
│ • Use refresh tokens for getting new access tokens              │
│ • Store secret key securely (env variables, vault)              │
│ • Use strong, long secret keys (256+ bits)                      │
│ • Validate all claims (exp, iss, aud)                           │
│ • Use HTTPS only                                                │
└─────────────────────────────────────────────────────────────────┘

❌ DON'T:
┌─────────────────────────────────────────────────────────────────┐
│ • Store sensitive data in payload (passwords, SSN)              │
│ • Use weak secrets ("secret", "password")                       │
│ • Skip expiration validation                                    │
│ • Store tokens in localStorage (XSS risk)                       │
│ • Use algorithm "none" in production                            │
│ • Have very long expiration (days/weeks)                        │
└─────────────────────────────────────────────────────────────────┘
```

---

### Chapter 18: Access Tokens vs Refresh Tokens

```
┌─────────────────────────────────────────────────────────────────┐
│              ACCESS TOKEN vs REFRESH TOKEN                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ACCESS TOKEN                      REFRESH TOKEN                 │
│  ┌─────────────────────┐          ┌─────────────────────┐       │
│  │ Short-lived (15min) │          │ Long-lived (7 days) │       │
│  │ Sent with every req │          │ Used only to get    │       │
│  │ Contains user info  │          │ new access tokens   │       │
│  │ Stored in memory    │          │ Stored securely     │       │
│  └─────────────────────┘          └─────────────────────┘       │
│                                                                  │
│  WHY TWO TOKENS?                                                │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ If access token is stolen, attacker has only 15 minutes │    │
│  │ Refresh token is used rarely, less exposure             │    │
│  │ Refresh token can be revoked server-side                │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

**Refresh Flow:**
```
┌────────┐                              ┌────────┐
│ Client │                              │ Server │
└───┬────┘                              └───┬────┘
    │                                       │
    │  API call with expired access token   │
    │ ─────────────────────────────────────►│
    │                                       │
    │  401 Unauthorized (token expired)     │
    │◄───────────────────────────────────── │
    │                                       │
    │  POST /auth/refresh                   │
    │  {refreshToken: "..."}                │
    │ ─────────────────────────────────────►│
    │                                       │
    │  New access token + new refresh token │
    │◄───────────────────────────────────── │
    │                                       │
    │  Retry original API call              │
    │ ─────────────────────────────────────►│
```

---

## Part 6: Authorization Patterns

---

### Chapter 19: Role-Based Access Control (RBAC)

```
┌─────────────────────────────────────────────────────────────────┐
│                    RBAC STRUCTURE                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  USERS        ROLES           PERMISSIONS                       │
│  ┌─────┐      ┌───────────┐   ┌─────────────────────┐           │
│  │John │ ───► │   ADMIN   │ ─►│ document:read       │           │
│  └─────┘      │           │   │ document:write      │           │
│               │           │   │ document:delete     │           │
│  ┌─────┐      │           │   │ user:read           │           │
│  │Jane │ ───► │           │   │ user:write          │           │
│  └─────┘      └───────────┘   │ user:delete         │           │
│                               └─────────────────────┘           │
│  ┌─────┐      ┌───────────┐   ┌─────────────────────┐           │
│  │Bob  │ ───► │   USER    │ ─►│ document:read       │           │
│  └─────┘      └───────────┘   └─────────────────────┘           │
│                                                                  │
│  ┌─────┐      ┌───────────┐   ┌─────────────────────┐           │
│  │Alice│ ───► │ MODERATOR │ ─►│ document:read       │           │
│  └─────┘      └───────────┘   │ document:write      │           │
│                               └─────────────────────┘           │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

**Checking Access:**
```
Can John delete a document?
├── John's role: ADMIN
├── ADMIN has permission: document:delete
└── ✅ Access granted!

Can Bob delete a document?
├── Bob's role: USER
├── USER has permission: document:read (only)
└── ❌ Access denied!
```

---

### Chapter 20: Attribute-Based Access Control (ABAC)

More flexible than RBAC - uses attributes to make decisions.

```
┌─────────────────────────────────────────────────────────────────┐
│                    ABAC STRUCTURE                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Subject Attributes    Resource Attributes    Environment       │
│  ┌─────────────────┐   ┌─────────────────┐   ┌──────────────┐   │
│  │ user.role       │   │ doc.owner       │   │ time         │   │
│  │ user.department │   │ doc.sensitivity │   │ ip_address   │   │
│  │ user.clearance  │   │ doc.department  │   │ device_type  │   │
│  └─────────────────┘   └─────────────────┘   └──────────────┘   │
│           │                    │                    │           │
│           └────────────────────┼────────────────────┘           │
│                                │                                │
│                         ┌──────▼──────┐                         │
│                         │   POLICY    │                         │
│                         │   ENGINE    │                         │
│                         └──────┬──────┘                         │
│                                │                                │
│                         ┌──────▼──────┐                         │
│                         │  DECISION   │                         │
│                         │ Allow/Deny  │                         │
│                         └─────────────┘                         │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

**Example Policy:**
```
ALLOW access IF:
  user.department == resource.department
  AND user.clearance >= resource.sensitivity
  AND time.hour >= 9 AND time.hour <= 17
  AND request.ip IN allowed_ips
```

---

### Chapter 21: Resource-Based Authorization

Sometimes authorization depends on the specific resource:

```
Document Ownership Check:
┌─────────────────────────────────────────────────────────────────┐
│                                                                  │
│  User: John (ID: 1)                                             │
│  Request: DELETE /documents/42                                   │
│                                                                  │
│  Document 42:                                                    │
│  ┌─────────────────────────────────────┐                        │
│  │ id: 42                              │                        │
│  │ title: "Budget Report"              │                        │
│  │ owner_id: 1  ← John's ID            │                        │
│  │ visibility: PRIVATE                 │                        │
│  └─────────────────────────────────────┘                        │
│                                                                  │
│  Authorization Logic:                                            │
│  ├── Is John the owner? (owner_id == user.id)                   │
│  ├── YES                                                         │
│  └── ✅ Allow deletion                                           │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘

Same request by Jane (ID: 2):
┌─────────────────────────────────────────────────────────────────┐
│  Authorization Logic:                                            │
│  ├── Is Jane the owner? (owner_id == user.id)                   │
│  ├── NO (1 ≠ 2)                                                  │
│  ├── Is Jane an admin?                                           │
│  ├── NO                                                          │
│  └── ❌ Deny deletion (403 Forbidden)                            │
└─────────────────────────────────────────────────────────────────┘
```

---

## Part 7: API Security Patterns

---

### Chapter 22: API Authentication Methods

```
┌─────────────────────────────────────────────────────────────────┐
│                  API AUTHENTICATION METHODS                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│ 1. API KEYS                                                     │
│    ┌───────────────────────────────────────────────────────┐    │
│    │ Header: X-API-Key: abc123xyz                          │    │
│    │                                                        │    │
│    │ Best for: Server-to-server, public APIs               │    │
│    │ Pros: Simple                                           │    │
│    │ Cons: No user context, hard to rotate                  │    │
│    └───────────────────────────────────────────────────────┘    │
│                                                                  │
│ 2. BEARER TOKENS (JWT)                                          │
│    ┌───────────────────────────────────────────────────────┐    │
│    │ Header: Authorization: Bearer eyJhbGc...              │    │
│    │                                                        │    │
│    │ Best for: User authentication, SPAs, mobile           │    │
│    │ Pros: Stateless, contains user info                    │    │
│    │ Cons: Can't revoke easily                              │    │
│    └───────────────────────────────────────────────────────┘    │
│                                                                  │
│ 3. BASIC AUTH                                                   │
│    ┌───────────────────────────────────────────────────────┐    │
│    │ Header: Authorization: Basic base64(user:pass)        │    │
│    │                                                        │    │
│    │ Best for: Simple internal APIs, development           │    │
│    │ Pros: Very simple                                      │    │
│    │ Cons: Credentials sent with every request              │    │
│    └───────────────────────────────────────────────────────┘    │
│                                                                  │
│ 4. OAUTH 2.0                                                    │
│    ┌───────────────────────────────────────────────────────┐    │
│    │ Delegated authorization for third-party access        │    │
│    │                                                        │    │
│    │ Best for: "Login with Google", third-party APIs       │    │
│    │ Pros: Secure, standardized                             │    │
│    │ Cons: Complex to implement                             │    │
│    └───────────────────────────────────────────────────────┘    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

### Chapter 23: Rate Limiting

Protecting your API from abuse:

```
┌─────────────────────────────────────────────────────────────────┐
│                      RATE LIMITING                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│ Without Rate Limiting:                                          │
│ ┌───────────────────────────────────────────────────────────┐   │
│ │ Attacker sends 10,000 requests/second                     │   │
│ │ Server overwhelmed → Crashes                              │   │
│ │ Or: Brute force attack tries all passwords                │   │
│ └───────────────────────────────────────────────────────────┘   │
│                                                                  │
│ With Rate Limiting:                                             │
│ ┌───────────────────────────────────────────────────────────┐   │
│ │ Rule: Max 100 requests per minute per IP                  │   │
│ │                                                           │   │
│ │ Request 1-100: ✅ Allowed                                 │   │
│ │ Request 101+: ❌ 429 Too Many Requests                    │   │
│ │                                                           │   │
│ │ Headers returned:                                         │   │
│ │ X-RateLimit-Limit: 100                                    │   │
│ │ X-RateLimit-Remaining: 0                                  │   │
│ │ X-RateLimit-Reset: 1640000000                             │   │
│ └───────────────────────────────────────────────────────────┘   │
│                                                                  │
│ Common Algorithms:                                              │
│ • Fixed Window: Reset count every minute                        │
│ • Sliding Window: Rolling 60-second window                      │
│ • Token Bucket: Tokens refill over time                         │
│ • Leaky Bucket: Constant output rate                            │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

### Chapter 24: Input Validation

```
┌─────────────────────────────────────────────────────────────────┐
│                     INPUT VALIDATION                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│ RULE: Never trust user input. Ever.                             │
│                                                                  │
│ ❌ TRUSTING INPUT:                                              │
│ ┌───────────────────────────────────────────────────────────┐   │
│ │ // User says age is 25, we believe them                   │   │
│ │ int age = request.getParameter("age"); // Could be -999!  │   │
│ └───────────────────────────────────────────────────────────┘   │
│                                                                  │
│ ✅ VALIDATING INPUT:                                            │
│ ┌───────────────────────────────────────────────────────────┐   │
│ │ @Min(0) @Max(150)                                         │   │
│ │ private int age;                                          │   │
│ │                                                           │   │
│ │ // Now: -999 → 400 Bad Request                            │   │
│ └───────────────────────────────────────────────────────────┘   │
│                                                                  │
│ TYPES OF VALIDATION:                                            │
│                                                                  │
│ 1. Type validation                                              │
│    └─ Is it actually a number? A date? An email?                │
│                                                                  │
│ 2. Range validation                                             │
│    └─ Is age between 0-150? Is amount positive?                 │
│                                                                  │
│ 3. Length validation                                            │
│    └─ Is username 3-50 characters? Is comment < 10000 chars?    │
│                                                                  │
│ 4. Format validation                                            │
│    └─ Is email valid? Is phone number in correct format?        │
│                                                                  │
│ 5. Business validation                                          │
│    └─ Does this user ID exist? Is this product in stock?        │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## Summary: Security Checklist

```
┌─────────────────────────────────────────────────────────────────┐
│                   SECURITY CHECKLIST                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│ AUTHENTICATION                                                   │
│ □ Secure password storage (bcrypt/argon2)                       │
│ □ Secure session/token management                               │
│ □ Multi-factor authentication option                            │
│ □ Account lockout after failed attempts                         │
│ □ Secure password reset flow                                    │
│                                                                  │
│ AUTHORIZATION                                                    │
│ □ Check permissions on every request                            │
│ □ Validate resource ownership                                   │
│ □ Principle of least privilege                                  │
│ □ Default deny approach                                         │
│                                                                  │
│ DATA PROTECTION                                                  │
│ □ HTTPS everywhere                                              │
│ □ Encrypt sensitive data at rest                                │
│ □ Never log sensitive data                                      │
│ □ Secure configuration management                               │
│                                                                  │
│ INPUT/OUTPUT                                                     │
│ □ Validate all input                                            │
│ □ Parameterized queries (prevent injection)                     │
│ □ Encode output (prevent XSS)                                   │
│ □ Proper error handling (no stack traces)                       │
│                                                                  │
│ API SECURITY                                                     │
│ □ Rate limiting                                                 │
│ □ CORS configured properly                                      │
│ □ Security headers set                                          │
│ □ API versioning                                                │
│                                                                  │
│ MONITORING                                                       │
│ □ Log security events                                           │
│ □ Alert on suspicious activity                                  │
│ □ Regular security audits                                       │
│ □ Dependency vulnerability scanning                             │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## What's Next?

After understanding these concepts, you're ready to:

1. **See how Spring Security implements these concepts** → Read `SECURITY_LEARNING_GUIDE.md`
2. **Practice implementing security** → Complete the `security-challenge/`
3. **Deep dive into specific topics** → Ask me about any concept you want to explore further!

---

**Questions to test your understanding:**

1. What's the difference between 401 and 403 status codes?
2. Why is bcrypt better than SHA-256 for password hashing?
3. Can you modify a JWT payload without invalidating it?
4. When would you use sessions vs tokens?
5. How does CSRF attack work, and why doesn't it affect JWT-based APIs?

Feel free to ask me to explain any concept in more detail! 🎓

