# 🚀 Real-World Scenarios & Advanced Topics - Interview Guide

> **Interviewer's Expectation**: These questions separate senior developers from juniors. They test your practical experience with production databases and scalability challenges.

---

# 🔷 REAL-WORLD SCENARIO QUESTIONS

## 📌 How Would You Handle Millions of Users?

### The Complete Answer:

```
┌─────────────────────────────────────────────────────────────┐
│              SCALING STRATEGY FOR MILLIONS OF USERS          │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  1. READ REPLICAS         - Distribute read load            │
│  2. CACHING               - Redis/Memcached for hot data    │
│  3. DATABASE SHARDING     - Partition data across servers   │
│  4. CONNECTION POOLING    - Reuse database connections      │
│  5. QUERY OPTIMIZATION    - Indexes, query tuning           │
│  6. ASYNC PROCESSING      - Queue non-critical operations   │
│  7. CDN                   - Cache static content            │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### Detailed Strategy:

#### 1. Read Replicas
```
Master (Writes) ──► Replica 1 (Reads)
                ──► Replica 2 (Reads)
                ──► Replica 3 (Reads)

Application routes:
- INSERT/UPDATE/DELETE → Master
- SELECT → Any Replica
```

```java
// Application-level read/write splitting
@Transactional(readOnly = true)  // Goes to replica
public User findUser(Long id) { ... }

@Transactional                    // Goes to master
public void updateUser(User user) { ... }
```

#### 2. Caching Layer
```
User Request
     │
     ▼
┌─────────────┐    HIT    Return cached data
│    Cache    │─────────►
│   (Redis)   │
└──────┬──────┘
       │ MISS
       ▼
┌─────────────┐
│   Database  │──► Store in cache ──► Return
└─────────────┘
```

```python
def get_user(user_id):
    # Check cache first
    cached = redis.get(f"user:{user_id}")
    if cached:
        return json.loads(cached)
    
    # Cache miss - fetch from DB
    user = db.query("SELECT * FROM users WHERE id = ?", user_id)
    
    # Store in cache for 1 hour
    redis.setex(f"user:{user_id}", 3600, json.dumps(user))
    return user
```

#### 3. Database Sharding
```
User ID 1-1M     → Shard 1
User ID 1M-2M    → Shard 2
User ID 2M-3M    → Shard 3
...
```

#### 4. Connection Pooling
```
Without pooling:          With pooling:
Request → New connection  Request → Reuse connection
Request → New connection  Request → Reuse connection
Request → New connection  Request → Reuse connection
(Expensive!)             (Efficient!)
```

```yaml
# HikariCP configuration
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
```

### Interview Answer:
> "To handle millions of users, I'd implement a multi-layered approach:
> 1. Add read replicas to distribute read load
> 2. Implement caching with Redis for frequently accessed data
> 3. Use connection pooling to reduce connection overhead
> 4. Optimize queries and add appropriate indexes
> 5. Consider sharding for horizontal scaling
> 6. Use async processing for non-critical operations
> 7. Monitor and tune based on actual bottlenecks"

---

## 📌 How Do You Prevent Duplicate Data?

### Multiple Strategies:

#### 1. Database Constraints (First line of defense)
```sql
-- Unique constraint
CREATE TABLE users (
    id INT PRIMARY KEY,
    email VARCHAR(200) UNIQUE,     -- Prevents duplicate emails
    phone VARCHAR(20) UNIQUE       -- Prevents duplicate phones
);

-- Composite unique constraint
CREATE TABLE user_roles (
    user_id INT,
    role_id INT,
    UNIQUE (user_id, role_id)      -- Same role can't be assigned twice
);
```

#### 2. Idempotency Keys (For API requests)
```sql
-- Store operation IDs to prevent duplicate processing
CREATE TABLE processed_requests (
    idempotency_key VARCHAR(100) PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Before processing a request:
INSERT INTO processed_requests (idempotency_key) 
VALUES ('request-abc-123');
-- If this fails (duplicate), request was already processed
```

```python
def create_order(request):
    idempotency_key = request.headers.get('Idempotency-Key')
    
    try:
        # Try to insert the key
        db.execute("INSERT INTO processed_requests VALUES (?)", idempotency_key)
    except DuplicateKeyError:
        # Already processed, return existing result
        return get_existing_order(idempotency_key)
    
    # Process the order
    order = process_new_order(request)
    return order
```

#### 3. Upsert Operations
```sql
-- MySQL: INSERT ... ON DUPLICATE KEY UPDATE
INSERT INTO users (email, name, updated_at)
VALUES ('john@email.com', 'John', NOW())
ON DUPLICATE KEY UPDATE name = VALUES(name), updated_at = NOW();

-- PostgreSQL: INSERT ... ON CONFLICT
INSERT INTO users (email, name)
VALUES ('john@email.com', 'John')
ON CONFLICT (email) DO UPDATE SET name = EXCLUDED.name;
```

#### 4. Application-Level Deduplication
```python
# Check before insert
def create_user(email, name):
    existing = db.query("SELECT id FROM users WHERE email = ?", email)
    if existing:
        raise UserAlreadyExistsError()
    
    # Use transaction to prevent race condition
    with db.transaction():
        db.execute("INSERT INTO users (email, name) VALUES (?, ?)", email, name)
```

### Interview Answer:
> "I prevent duplicates at multiple levels:
> 1. Database constraints (UNIQUE) as the last line of defense
> 2. Idempotency keys for API requests to prevent duplicate processing
> 3. UPSERT operations when I want to update if exists
> 4. Application-level checks within transactions for better error handling
> The database constraint is most important because it works even if application logic fails."

---

## 📌 How Do You Store Passwords Securely?

### ⚠️ NEVER Store Plain Text Passwords!

### The Correct Approach:

```
┌─────────────────────────────────────────────────────────────┐
│           SECURE PASSWORD STORAGE                            │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  User enters: "MyPassword123"                               │
│                    │                                         │
│                    ▼                                         │
│  Add random salt: "MyPassword123" + "x7Kj9mNp"              │
│                    │                                         │
│                    ▼                                         │
│  Hash with bcrypt/argon2:                                   │
│  "$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4.XPz7..."   │
│                    │                                         │
│                    ▼                                         │
│  Store hash in database (NEVER the original password)       │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### Implementation:

```python
import bcrypt

# Storing a password
def hash_password(plain_password):
    # Generate salt and hash (salt is embedded in result)
    salt = bcrypt.gensalt(rounds=12)  # Cost factor 12
    hashed = bcrypt.hashpw(plain_password.encode('utf-8'), salt)
    return hashed.decode('utf-8')

# Verifying a password
def verify_password(plain_password, hashed_password):
    return bcrypt.checkpw(
        plain_password.encode('utf-8'),
        hashed_password.encode('utf-8')
    )
```

```java
// Java with BCrypt
import org.mindrot.jbcrypt.BCrypt;

// Hash password
String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));

// Verify password
boolean isValid = BCrypt.checkpw(plainPassword, hashedPassword);
```

### Database Schema:
```sql
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(200) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,  -- Store hash, NOT password
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- NEVER do this:
-- password VARCHAR(100)  -- Plain text! DANGEROUS!
```

### Key Points:
1. **Use bcrypt, Argon2, or scrypt** - Not MD5 or SHA (too fast to crack)
2. **Salt is automatic** in bcrypt - Don't implement yourself
3. **Use appropriate cost factor** - 10-12 for bcrypt (balance security/speed)
4. **Never log passwords** - Even in error messages
5. **Use HTTPS** - Encrypt in transit

### Interview Answer:
> "I never store passwords in plain text. I use bcrypt or Argon2 to hash passwords with a high cost factor (12 rounds). These algorithms are intentionally slow to prevent brute-force attacks and include automatic salting to prevent rainbow table attacks. The hash is stored in the database, and during login, I hash the provided password and compare hashes."

---

## 📌 How Would You Design a Logging System?

### Requirements:
- High write throughput (thousands of logs per second)
- Searchable logs
- Retention policy (delete old logs)
- Minimal impact on application performance

### Architecture:

```
┌─────────────────────────────────────────────────────────────┐
│                 LOGGING SYSTEM ARCHITECTURE                  │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  Applications                                                │
│      │                                                       │
│      ▼                                                       │
│  ┌─────────────────────┐                                    │
│  │  Message Queue      │  (Kafka/RabbitMQ)                  │
│  │  - Buffering        │                                    │
│  │  - Async processing │                                    │
│  └──────────┬──────────┘                                    │
│             │                                                │
│             ▼                                                │
│  ┌─────────────────────┐     ┌─────────────────────┐       │
│  │   Log Processor     │────►│  Elasticsearch      │       │
│  │   (Consumer)        │     │  - Full-text search │       │
│  └─────────────────────┘     │  - Aggregations     │       │
│                              └─────────────────────┘       │
│                                        │                    │
│                                        ▼                    │
│                              ┌─────────────────────┐       │
│                              │      Kibana         │       │
│                              │  - Visualization    │       │
│                              │  - Dashboards       │       │
│                              └─────────────────────┘       │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### Database Design (if using RDBMS):

```sql
-- Partitioned by date for easy cleanup
CREATE TABLE logs (
    id BIGINT AUTO_INCREMENT,
    timestamp DATETIME NOT NULL,
    level ENUM('DEBUG', 'INFO', 'WARN', 'ERROR', 'FATAL') NOT NULL,
    service_name VARCHAR(100) NOT NULL,
    message TEXT,
    metadata JSON,
    trace_id VARCHAR(36),
    PRIMARY KEY (id, timestamp)
) PARTITION BY RANGE (TO_DAYS(timestamp)) (
    PARTITION p_2024_01 VALUES LESS THAN (TO_DAYS('2024-02-01')),
    PARTITION p_2024_02 VALUES LESS THAN (TO_DAYS('2024-03-01')),
    -- Add more partitions as needed
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- Drop old partitions for cleanup
ALTER TABLE logs DROP PARTITION p_2024_01;
```

### Using NoSQL (Elasticsearch):
```json
{
    "timestamp": "2024-01-15T10:30:00Z",
    "level": "ERROR",
    "service": "payment-service",
    "message": "Payment failed for order 12345",
    "metadata": {
        "order_id": 12345,
        "error_code": "INSUFFICIENT_FUNDS",
        "user_id": 789
    },
    "trace_id": "abc-123-xyz"
}
```

### Best Practices:
1. **Async logging** - Don't block application
2. **Structured logging** - JSON format for easy parsing
3. **Correlation IDs** - Track requests across services
4. **Log levels** - DEBUG, INFO, WARN, ERROR
5. **Retention policy** - Auto-delete old logs

---

## 📌 How Would You Migrate Data from One DB to Another?

### Migration Steps:

```
┌─────────────────────────────────────────────────────────────┐
│               DATABASE MIGRATION STRATEGY                    │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  Phase 1: PREPARE                                           │
│  ├─ Analyze source schema                                   │
│  ├─ Design target schema                                    │
│  ├─ Create mapping document                                 │
│  └─ Set up target database                                  │
│                                                              │
│  Phase 2: MIGRATE (Dual-Write)                              │
│  ├─ Initial data copy                                       │
│  ├─ Enable dual-write (write to both DBs)                  │
│  ├─ Sync historical data                                    │
│  └─ Verify data consistency                                 │
│                                                              │
│  Phase 3: SWITCH                                            │
│  ├─ Switch reads to new DB                                  │
│  ├─ Monitor for issues                                      │
│  ├─ Disable writes to old DB                                │
│  └─ Cleanup                                                 │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### Zero-Downtime Migration:

```python
# Phase 1: Dual-write
class UserRepository:
    def save(self, user):
        # Write to old DB
        old_db.save(user)
        
        # Write to new DB
        try:
            new_db.save(user)
        except Exception as e:
            log.error("New DB write failed", e)
            # Don't fail the request, just log
    
    def find(self, user_id):
        # Read from old DB (still primary)
        return old_db.find(user_id)

# Phase 2: Switch reads
class UserRepository:
    def find(self, user_id):
        # Read from new DB (now primary)
        user = new_db.find(user_id)
        
        # Fallback to old DB if needed
        if not user:
            user = old_db.find(user_id)
            if user:
                new_db.save(user)  # Backfill
        
        return user
```

### Tools:
- **AWS DMS** - Database Migration Service
- **pg_dump/pg_restore** - PostgreSQL
- **mysqldump** - MySQL
- **Custom scripts** - For complex transformations

### Interview Answer:
> "For a zero-downtime migration, I'd use a phased approach:
> 1. Set up the new database with the target schema
> 2. Implement dual-write - all writes go to both databases
> 3. Backfill historical data using batch jobs
> 4. Verify data consistency between both databases
> 5. Switch reads to the new database (with fallback)
> 6. Monitor for issues
> 7. Finally disable writes to the old database
> 
> This approach ensures no data loss and allows rollback if issues arise."

---

# 🔷 ADVANCED TOPICS

## 📌 Partitioning vs Sharding

### Partitioning (Single Database)

```
┌─────────────────────────────────────────────────────────────┐
│                    SINGLE DATABASE                           │
├─────────────────────────────────────────────────────────────┤
│   orders table                                               │
│   ├── Partition 2023 (Jan-Dec 2023)                         │
│   ├── Partition 2024 (Jan-Dec 2024)                         │
│   └── Partition 2025 (Jan-Dec 2025)                         │
│                                                              │
│   All partitions on SAME server                             │
└─────────────────────────────────────────────────────────────┘
```

```sql
-- Range partitioning by date
CREATE TABLE orders (
    order_id INT,
    customer_id INT,
    order_date DATE,
    total DECIMAL(10,2)
) PARTITION BY RANGE (YEAR(order_date)) (
    PARTITION p2023 VALUES LESS THAN (2024),
    PARTITION p2024 VALUES LESS THAN (2025),
    PARTITION p2025 VALUES LESS THAN (2026)
);

-- List partitioning by region
CREATE TABLE customers (
    customer_id INT,
    name VARCHAR(100),
    region VARCHAR(50)
) PARTITION BY LIST (region) (
    PARTITION p_north VALUES IN ('Delhi', 'Punjab', 'UP'),
    PARTITION p_south VALUES IN ('Karnataka', 'TN', 'Kerala'),
    PARTITION p_west VALUES IN ('Maharashtra', 'Gujarat')
);
```

### Sharding (Multiple Databases)

```
┌─────────────────────────────────────────────────────────────┐
│                       SHARDING                               │
├─────────────────┬─────────────────┬─────────────────────────┤
│    Shard 1      │    Shard 2      │       Shard 3           │
│  (Server 1)     │   (Server 2)    │     (Server 3)          │
├─────────────────┼─────────────────┼─────────────────────────┤
│ Users A-H       │ Users I-P       │ Users Q-Z               │
│ Orders for A-H  │ Orders for I-P  │ Orders for Q-Z          │
└─────────────────┴─────────────────┴─────────────────────────┘
       │                  │                    │
       └──────────────────┼────────────────────┘
                          │
                   Application Layer
                   (Routes to correct shard)
```

### Comparison:

| Partitioning | Sharding |
|--------------|----------|
| Single database server | Multiple database servers |
| Easier to manage | Complex to manage |
| Limited by single server | Horizontally scalable |
| Automatic query routing | Application routes queries |
| Shared resources | Independent resources |

### Interview Answer:
> "Partitioning divides a table into smaller pieces within the same database server - it improves query performance and maintenance but doesn't scale beyond one server. Sharding distributes data across multiple database servers - it provides horizontal scalability but requires application-level routing and makes cross-shard queries complex."

---

## 📌 Database Replication

### Types of Replication:

```
1. MASTER-SLAVE (Primary-Replica)
   
   Master ──writes──► Slave 1
      │            ──► Slave 2
      │            ──► Slave 3
      │
   (All writes)    (Reads distributed)


2. MASTER-MASTER (Multi-Primary)

   Master 1 ◄───sync───► Master 2
       │                     │
   (Writes)              (Writes)
   
   Both accept writes, sync bidirectionally


3. SYNCHRONOUS vs ASYNCHRONOUS

   Synchronous:  Master ─wait─► Slave acknowledges ─► Commit
   (Slower, consistent)
   
   Asynchronous: Master ─fire─► Continue ─► Slave receives later
   (Faster, may lose data on crash)
```

### Configuration Example (MySQL):

```sql
-- On Master
CREATE USER 'repl_user'@'%' IDENTIFIED BY 'password';
GRANT REPLICATION SLAVE ON *.* TO 'repl_user'@'%';

-- On Slave
CHANGE MASTER TO
    MASTER_HOST='master_ip',
    MASTER_USER='repl_user',
    MASTER_PASSWORD='password',
    MASTER_LOG_FILE='mysql-bin.000001',
    MASTER_LOG_POS=0;
START SLAVE;
```

---

## 📌 CAP Theorem (Deep Dive)

```
                    Consistency
                        │
                   ┌────┴────┐
                   │         │
                  CA        CP
                   │         │
         Availability ─────── Partition Tolerance
                        │
                       AP

In distributed systems, you can only have 2 of 3:
- CA: Consistent + Available (but no partition tolerance)
- CP: Consistent + Partition-tolerant (but may be unavailable)
- AP: Available + Partition-tolerant (but eventually consistent)
```

### Real-World Examples:

| Type | Databases | Trade-off |
|------|-----------|-----------|
| **CA** | Traditional RDBMS (single node) | No horizontal scaling |
| **CP** | MongoDB, HBase, Redis Cluster | May reject writes during partition |
| **AP** | Cassandra, DynamoDB, CouchDB | Eventual consistency |

### Interview Answer:
> "CAP theorem states that in the presence of a network partition, you must choose between consistency and availability. Since network partitions are inevitable in distributed systems, we practically choose between CP (consistent but may be unavailable during partitions) and AP (always available but eventually consistent). MongoDB is CP - it may become unavailable during network issues. Cassandra is AP - it stays available but data might be temporarily inconsistent."

---

## 📌 Read Replicas

### Architecture:

```
            ┌──────────────┐
            │   Primary    │
            │   (Master)   │
            │   Writes     │
            └──────┬───────┘
                   │ Replication
       ┌───────────┼───────────┐
       │           │           │
       ▼           ▼           ▼
┌──────────┐ ┌──────────┐ ┌──────────┐
│ Replica  │ │ Replica  │ │ Replica  │
│   (R1)   │ │   (R2)   │ │   (R3)   │
│  Reads   │ │  Reads   │ │  Reads   │
└──────────┘ └──────────┘ └──────────┘
```

### Benefits:
1. **Scale reads** - Distribute read load
2. **Fault tolerance** - Promote replica if primary fails
3. **Geographic distribution** - Replicas closer to users
4. **Reporting** - Run heavy queries on replica

### Considerations:
- **Replication lag** - Replicas may be slightly behind
- **Read-after-write consistency** - May not see your own writes immediately
- **Connection routing** - Application must route correctly

---

## 📌 Database Locking

### Lock Types:

```sql
-- Shared Lock (Read Lock)
SELECT * FROM accounts WHERE id = 1 LOCK IN SHARE MODE;
-- Others can read, but not write

-- Exclusive Lock (Write Lock)
SELECT * FROM accounts WHERE id = 1 FOR UPDATE;
-- No one else can read or write

-- Row-level locks (InnoDB)
UPDATE accounts SET balance = 100 WHERE id = 1;
-- Only locks row with id = 1

-- Table-level locks
LOCK TABLES accounts WRITE;
-- Locks entire table
```

### Lock Escalation:
```
Row Lock → Page Lock → Table Lock

When too many row locks exist, database may escalate to table lock
for efficiency. This can hurt concurrency.
```

---

## 📌 Horizontal vs Vertical Scaling

### Vertical Scaling (Scale Up)

```
Before:                 After:
┌───────────────┐      ┌───────────────────────┐
│  4 CPU        │      │  16 CPU               │
│  16GB RAM     │ ───► │  64GB RAM             │
│  500GB SSD    │      │  2TB SSD              │
└───────────────┘      └───────────────────────┘
    Server                 Bigger Server
```

**Pros**: Simple, no code changes
**Cons**: Has limits, expensive at scale, single point of failure

### Horizontal Scaling (Scale Out)

```
Before:                 After:
┌───────────────┐      ┌─────────┐ ┌─────────┐ ┌─────────┐
│   Server      │      │ Server1 │ │ Server2 │ │ Server3 │
└───────────────┘ ───► └─────────┘ └─────────┘ └─────────┘
                              Multiple Servers
```

**Pros**: Unlimited scaling, fault tolerant
**Cons**: Complex, requires distributed logic, cross-server queries

### Comparison:

| Vertical Scaling | Horizontal Scaling |
|-----------------|-------------------|
| Add more CPU/RAM | Add more servers |
| Simple | Complex |
| Has hardware limits | Virtually unlimited |
| Single point of failure | Fault tolerant |
| Expensive at scale | Cost-effective |
| No code changes | May need code changes |

---

# 🎯 Quick Interview Cheat Sheet

## Scaling Strategies:
```
Read-heavy  → Read replicas, Caching
Write-heavy → Sharding, Partitioning
Both        → Caching + Sharding + Replicas
```

## CAP Choices:
```
Banking/Finance → CP (Consistency critical)
Social Media    → AP (Availability critical)
Analytics       → CA (Single node often sufficient)
```

## Migration Strategy:
```
1. Dual-write to both databases
2. Backfill historical data
3. Switch reads to new database
4. Disable writes to old database
```

## Password Storage:
```
NEVER: Plain text, MD5, SHA-1
ALWAYS: bcrypt, Argon2, scrypt with high cost factor
```

---

## 💡 Pro Interview Tips

1. **Give specific numbers when possible**
   > "I'd use bcrypt with 12 rounds, which takes about 250ms to hash"

2. **Mention tools you've used**
   > "In my last project, we used AWS DMS for migration..."

3. **Discuss trade-offs**
   > "Sharding gives us scale but complicates joins..."

4. **Show you understand production concerns**
   > "We need to consider replication lag when reading from replicas..."

5. **Common advanced questions**:
   - "How do you handle hot spots in sharding?"
   - "What's your backup strategy?"
   - "How do you monitor database health?"
   - "Explain eventual consistency"
   - "How would you debug a slow query in production?"

