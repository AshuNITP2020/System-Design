# 🔷 NoSQL Databases - Interview Guide

> **Interviewer's Expectation**: NoSQL is increasingly asked, especially for scalability discussions. Know when to use SQL vs NoSQL and understand the major types.

---

## 📌 What is NoSQL?

**NoSQL** (Not Only SQL) refers to databases that store data in formats other than traditional relational tables. They're designed for:
- **Scalability** (horizontal)
- **Flexibility** (schema-less)
- **High performance** (specific use cases)
- **Big data** and real-time applications

### Interview Answer:
> "NoSQL databases are non-relational databases designed for specific data models and flexible schemas. They excel at horizontal scaling and handling large volumes of unstructured or semi-structured data. The name means 'Not Only SQL' - they may or may not use SQL-like queries."

---

## 📌 SQL vs NoSQL

### ⭐ This comparison is asked in almost every interview!

| Feature | SQL (RDBMS) | NoSQL |
|---------|-------------|-------|
| **Data Model** | Tables (rows/columns) | Documents, key-value, graph, etc. |
| **Schema** | Fixed, predefined | Flexible, dynamic |
| **Scaling** | Vertical (bigger server) | Horizontal (more servers) |
| **ACID** | Full ACID support | Varies (BASE model common) |
| **Joins** | Yes, multi-table | Usually no joins |
| **Query Language** | SQL (standardized) | Varies by database |
| **Best For** | Structured data, complex queries | Unstructured data, simple queries |
| **Examples** | MySQL, PostgreSQL, Oracle | MongoDB, Redis, Cassandra |

### Visual Comparison:

```
SQL (Table Structure):           NoSQL (Document Structure):
┌────┬─────────┬────────┐        {
│ id │  name   │ salary │          "_id": "1",
├────┼─────────┼────────┤          "name": "John",
│ 1  │  John   │ 50000  │          "salary": 50000,
│ 2  │  Jane   │ 60000  │          "skills": ["Java", "Python"],
└────┴─────────┴────────┘          "address": {
                                     "city": "NYC",
All rows have same columns!          "zip": "10001"
                                   }
                                 }
                                 Each document can be different!
```

---

## 📌 When to Use SQL vs NoSQL

### Use SQL When:
1. **Structured data** with clear relationships
2. **ACID compliance** is critical (banking, financial)
3. **Complex queries** with multiple JOINs
4. **Data integrity** is paramount
5. **Well-defined schema** unlikely to change

**Examples**: E-commerce transactions, banking, ERP systems

### Use NoSQL When:
1. **Massive scale** required (millions of users)
2. **Flexible schema** - data structure changes frequently
3. **High write throughput** needed
4. **Unstructured/semi-structured data** (JSON, logs)
5. **Horizontal scaling** is priority
6. **Simple queries** without complex JOINs

**Examples**: Social media feeds, real-time analytics, IoT, caching

### Interview Answer:
> "I choose SQL when I need strong consistency, complex transactions, and data relationships. I choose NoSQL for scalability, flexible schemas, and when my data is naturally unstructured. Often, modern systems use both - SQL for transactional data and NoSQL for caching or analytics."

---

## 📌 Types of NoSQL Databases

```
┌─────────────────────────────────────────────────────────────────┐
│                     NoSQL DATABASE TYPES                         │
├─────────────────┬──────────────────┬─────────────────┬──────────┤
│   Document DB   │   Key-Value      │  Column-Family  │   Graph  │
├─────────────────┼──────────────────┼─────────────────┼──────────┤
│    MongoDB      │     Redis        │   Cassandra     │   Neo4j  │
│    CouchDB      │    Memcached     │     HBase       │  Amazon  │
│   Firebase      │   DynamoDB       │   ScyllaDB      │  Neptune │
└─────────────────┴──────────────────┴─────────────────┴──────────┘
```

---

# 🔷 Document Databases (MongoDB)

## 📌 What is a Document Database?

Stores data as **documents** (usually JSON/BSON). Each document can have different structure.

### Key Concepts:

| SQL Term | MongoDB Term |
|----------|--------------|
| Database | Database |
| Table | Collection |
| Row | Document |
| Column | Field |
| Index | Index |
| Primary Key | _id |

### Document Structure:
```json
{
    "_id": ObjectId("507f1f77bcf86cd799439011"),
    "name": "John Doe",
    "email": "john@example.com",
    "age": 30,
    "address": {
        "street": "123 Main St",
        "city": "New York",
        "zip": "10001"
    },
    "skills": ["JavaScript", "Python", "MongoDB"],
    "experience": [
        {
            "company": "Tech Corp",
            "years": 3
        },
        {
            "company": "Startup Inc",
            "years": 2
        }
    ]
}
```

---

## 📌 MongoDB Basics

### CRUD Operations:

```javascript
// INSERT (Create)
db.users.insertOne({
    name: "John",
    email: "john@example.com",
    age: 30
});

db.users.insertMany([
    { name: "Jane", age: 25 },
    { name: "Bob", age: 35 }
]);

// SELECT (Read)
db.users.find({});                     // All documents
db.users.find({ age: 30 });            // Filter
db.users.find({ age: { $gt: 25 } });   // Greater than
db.users.findOne({ name: "John" });    // Single document

// UPDATE
db.users.updateOne(
    { name: "John" },                  // Filter
    { $set: { age: 31 } }              // Update
);

db.users.updateMany(
    { status: "active" },
    { $set: { verified: true } }
);

// DELETE
db.users.deleteOne({ name: "John" });
db.users.deleteMany({ status: "inactive" });
```

### Query Operators:
```javascript
// Comparison
$eq, $ne, $gt, $gte, $lt, $lte, $in, $nin

// Logical
$and, $or, $not, $nor

// Examples:
db.users.find({ age: { $gte: 18, $lte: 65 } });
db.users.find({ $or: [{ status: "active" }, { role: "admin" }] });
db.users.find({ skills: { $in: ["JavaScript", "Python"] } });
```

---

## 📌 What is BSON?

**BSON** (Binary JSON) is MongoDB's storage format.

### JSON vs BSON:

| JSON | BSON |
|------|------|
| Text-based | Binary format |
| Human-readable | Machine-optimized |
| Limited types | More types (Date, ObjectId, Binary) |
| Slower to parse | Faster to parse |
| Standard format | MongoDB specific |

### BSON-specific Types:
```javascript
{
    "_id": ObjectId("507f1f77bcf86cd799439011"),  // 12-byte unique ID
    "created": new Date(),                         // Date type
    "data": BinData(0, "base64data"),             // Binary data
    "decimal": NumberDecimal("123.45"),           // Precise decimal
    "long": NumberLong("9223372036854775807")     // 64-bit integer
}
```

---

## 📌 Indexing in MongoDB

```javascript
// Create index
db.users.createIndex({ email: 1 });           // Ascending
db.users.createIndex({ name: -1 });           // Descending
db.users.createIndex({ name: 1, age: -1 });   // Compound

// Unique index
db.users.createIndex({ email: 1 }, { unique: true });

// Text index (for search)
db.articles.createIndex({ content: "text" });

// View indexes
db.users.getIndexes();

// Drop index
db.users.dropIndex("email_1");
```

---

## 📌 Aggregation Pipeline

MongoDB's equivalent of SQL GROUP BY and complex queries.

```javascript
db.orders.aggregate([
    // Stage 1: Filter
    { $match: { status: "completed" } },
    
    // Stage 2: Group
    { $group: {
        _id: "$customer_id",
        totalAmount: { $sum: "$amount" },
        orderCount: { $sum: 1 },
        avgAmount: { $avg: "$amount" }
    }},
    
    // Stage 3: Sort
    { $sort: { totalAmount: -1 } },
    
    // Stage 4: Limit
    { $limit: 10 }
]);
```

---

# 🔷 Key-Value Stores (Redis)

## 📌 What is a Key-Value Store?

Simplest NoSQL type - stores data as **key-value pairs**. Think of it as a giant hash map.

```
┌─────────────┬───────────────────────────┐
│    KEY      │          VALUE            │
├─────────────┼───────────────────────────┤
│ user:1      │ {"name":"John","age":30}  │
│ session:abc │ {"userId":1,"expires":..} │
│ cache:home  │ "<html>...</html>"        │
└─────────────┴───────────────────────────┘
```

---

## 📌 Redis Basics

**Redis** (Remote Dictionary Server) is the most popular key-value store.

### Data Types:
```
1. Strings  - Simple key-value
2. Lists    - Ordered list of strings
3. Sets     - Unordered unique strings
4. Hashes   - Field-value pairs (like objects)
5. Sorted Sets - Sets with score for sorting
```

### Basic Commands:

```bash
# Strings
SET user:1 "John"
GET user:1                    # Returns "John"
SET session:token "xyz" EX 3600  # Expires in 1 hour

# Increment/Decrement
SET pageviews 0
INCR pageviews               # 1
INCRBY pageviews 10          # 11

# Lists (Queue/Stack)
LPUSH queue "task1"          # Push left
RPUSH queue "task2"          # Push right
LPOP queue                   # Pop left
LRANGE queue 0 -1            # Get all

# Hashes (Objects)
HSET user:1 name "John"
HSET user:1 age 30
HGET user:1 name             # "John"
HGETALL user:1               # All fields

# Sets (Unique values)
SADD tags "javascript"
SADD tags "python"
SMEMBERS tags               # Get all members
SISMEMBER tags "python"     # Check membership

# Sorted Sets (Leaderboard)
ZADD leaderboard 100 "player1"
ZADD leaderboard 200 "player2"
ZRANGE leaderboard 0 -1     # Sorted by score
```

---

## 📌 Redis Use Cases

### 1. Caching
```bash
# Check cache first
GET product:123
# If null, fetch from DB and cache
SET product:123 "{...}" EX 3600
```

### 2. Session Storage
```bash
SET session:abc123 "{userId:1,cart:[...]}" EX 7200
```

### 3. Rate Limiting
```bash
INCR api:user:1:requests
EXPIRE api:user:1:requests 60
# Check if > 100 requests per minute
```

### 4. Real-time Leaderboards
```bash
ZADD leaderboard 500 "player1"
ZREVRANGE leaderboard 0 9   # Top 10
```

### 5. Pub/Sub Messaging
```bash
# Publisher
PUBLISH notifications "New message!"

# Subscriber
SUBSCRIBE notifications
```

---

# 🔷 Column-Family Stores (Cassandra)

## 📌 What is a Column-Family Store?

Stores data in **columns** rather than rows. Optimized for reading/writing large amounts of data across many servers.

### Row-Based vs Column-Based:

```
ROW-BASED (Traditional):
┌────┬───────┬────────┬────────────┐
│ id │ name  │ salary │ department │
├────┼───────┼────────┼────────────┤
│ 1  │ John  │ 50000  │ IT         │
│ 2  │ Jane  │ 60000  │ HR         │
│ 3  │ Bob   │ 55000  │ IT         │
└────┴───────┴────────┴────────────┘
Read row 1 → Get all columns

COLUMN-BASED:
┌────┬───────────────────┐
│ id │ 1, 2, 3           │
├────┼───────────────────┤
│name│ John, Jane, Bob   │
├────┼───────────────────┤
│sal │ 50000, 60000,...  │
└────┴───────────────────┘
Read salary column → Get all salaries (efficient!)
```

### When to Use:
- Analytics (aggregate specific columns)
- Time-series data
- Write-heavy workloads
- Massive scale (petabytes)

---

## 📌 Cassandra Basics

```sql
-- Create keyspace (like database)
CREATE KEYSPACE my_app
WITH replication = {
    'class': 'SimpleStrategy',
    'replication_factor': 3
};

-- Create table
CREATE TABLE users (
    user_id UUID PRIMARY KEY,
    name TEXT,
    email TEXT,
    created_at TIMESTAMP
);

-- Insert
INSERT INTO users (user_id, name, email, created_at)
VALUES (uuid(), 'John', 'john@email.com', toTimestamp(now()));

-- Query
SELECT * FROM users WHERE user_id = some-uuid;
```

---

# 🔷 Graph Databases (Neo4j)

## 📌 What is a Graph Database?

Stores data as **nodes** (entities) and **edges** (relationships). Perfect for highly connected data.

### Structure:
```
        ┌──────────┐
        │  John    │
        │  (User)  │
        └────┬─────┘
             │
      FRIENDS_WITH
             │
             ▼
        ┌──────────┐      WORKS_AT      ┌──────────┐
        │   Jane   │ ─────────────────► │  Google  │
        │  (User)  │                    │(Company) │
        └──────────┘                    └──────────┘
```

### Use Cases:
- Social networks
- Recommendation engines
- Fraud detection
- Knowledge graphs

---

## 📌 Neo4j Basics (Cypher Query Language)

```cypher
// Create nodes
CREATE (john:User {name: 'John', age: 30})
CREATE (jane:User {name: 'Jane', age: 25})
CREATE (google:Company {name: 'Google'})

// Create relationships
CREATE (john)-[:FRIENDS_WITH]->(jane)
CREATE (jane)-[:WORKS_AT {since: 2020}]->(google)

// Query: Find John's friends
MATCH (john:User {name: 'John'})-[:FRIENDS_WITH]->(friend)
RETURN friend.name

// Query: Find friends of friends
MATCH (john:User {name: 'John'})-[:FRIENDS_WITH*2]->(fof)
RETURN DISTINCT fof.name

// Query: Shortest path
MATCH path = shortestPath(
    (john:User {name: 'John'})-[*]-(google:Company {name: 'Google'})
)
RETURN path
```

---

# 🔷 BASE vs ACID

## 📌 What is BASE?

NoSQL often follows **BASE** instead of ACID:

| ACID | BASE |
|------|------|
| **A**tomicity | **B**asically **A**vailable |
| **C**onsistency | **S**oft state |
| **I**solation | **E**ventually consistent |
| **D**urability | |

### BASE Explained:

1. **Basically Available**: System guarantees availability (might return stale data)
2. **Soft State**: State may change over time due to eventual consistency
3. **Eventually Consistent**: System will become consistent given enough time

### Interview Answer:
> "ACID provides strong consistency at the cost of availability and performance. BASE provides availability and partition tolerance, accepting that data might be temporarily inconsistent. Most NoSQL databases use BASE because it allows horizontal scaling. The choice depends on whether you need immediate consistency (banking) or can tolerate eventual consistency (social media likes)."

---

# 🔷 CAP Theorem

## 📌 What is CAP Theorem?

In a distributed system, you can only guarantee **two out of three**:

```
            Consistency
               /\
              /  \
             /    \
            /  ??  \
           /________\
    Availability   Partition
                   Tolerance
```

- **C (Consistency)**: All nodes see the same data at the same time
- **A (Availability)**: Every request gets a response (success/failure)
- **P (Partition Tolerance)**: System works despite network failures

### The Trade-off:
```
CA - Consistency + Availability (No partition tolerance)
    → Traditional RDBMS (PostgreSQL, MySQL)
    → Works only when network is perfect

CP - Consistency + Partition Tolerance (May be unavailable)
    → MongoDB, HBase
    → May reject writes during partition

AP - Availability + Partition Tolerance (May be inconsistent)
    → Cassandra, DynamoDB, CouchDB
    → Always available, eventually consistent
```

### Interview Answer:
> "CAP theorem states that in a distributed database, you can only guarantee two of: Consistency, Availability, and Partition Tolerance. Since network partitions are unavoidable in distributed systems, we usually choose between CP (consistent but may be unavailable) or AP (always available but eventually consistent). MongoDB is CP, Cassandra is AP."

---

# 🎯 Quick Interview Cheat Sheet

## NoSQL Types:
```
Document      → MongoDB (JSON documents)
Key-Value     → Redis (caching, sessions)
Column-Family → Cassandra (analytics, time-series)
Graph         → Neo4j (relationships, social)
```

## SQL vs NoSQL Decision:
```
SQL when:  ACID, complex queries, structured data
NoSQL when: Scale, flexibility, simple queries
```

## MongoDB Terms:
```
Collection = Table
Document = Row
Field = Column
_id = Primary Key
BSON = Binary JSON
```

## Redis Commands:
```
SET/GET       → Strings
LPUSH/RPOP    → Lists
SADD/SMEMBERS → Sets
HSET/HGET     → Hashes
ZADD/ZRANGE   → Sorted Sets
```

## CAP Theorem:
```
CA → Traditional SQL (no partition tolerance)
CP → MongoDB (consistent, may be unavailable)
AP → Cassandra (available, eventually consistent)
```

---

## 💡 Pro Interview Tips

1. **Don't say "NoSQL is better than SQL"**
   > "Each has its use case. I'd use SQL for financial transactions and NoSQL for a user activity feed."

2. **Know at least one NoSQL DB deeply**
   - MongoDB operations
   - Redis data types
   - Basic queries

3. **Discuss scalability**
   > "NoSQL databases typically scale horizontally by adding more servers, while SQL scales vertically."

4. **Mention real-world examples**:
   - Instagram: Cassandra for feeds
   - Twitter: Redis for timelines
   - Netflix: Cassandra for everything

5. **Common follow-up questions**:
   - "How would you migrate from SQL to NoSQL?"
   - "When would you use Redis over MongoDB?"
   - "Explain eventual consistency"
   - "What is sharding?"

