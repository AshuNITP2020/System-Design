# 📚 Database Interview Preparation Guide

> Complete, structured database interview guide covering fresher to experienced level topics.

---

## 🎯 Quick Navigation

| # | Topic | Difficulty | Priority |
|---|-------|------------|----------|
| 01 | [Database Fundamentals](./01_DATABASE_FUNDAMENTALS.md) | Beginner | ⭐⭐⭐⭐⭐ |
| 02 | [SQL Basics](./02_SQL_BASICS.md) | Beginner | ⭐⭐⭐⭐⭐ |
| 03 | [SQL Queries, JOINs & Subqueries](./03_SQL_QUERIES_JOINS_SUBQUERIES.md) | Intermediate | ⭐⭐⭐⭐⭐ |
| 04 | [Normalization](./04_NORMALIZATION.md) | Intermediate | ⭐⭐⭐⭐⭐ |
| 05 | [Indexing & Performance](./05_INDEXING_AND_PERFORMANCE.md) | Intermediate | ⭐⭐⭐⭐⭐ |
| 06 | [Transactions & Concurrency](./06_TRANSACTIONS_AND_CONCURRENCY.md) | Intermediate | ⭐⭐⭐⭐⭐ |
| 07 | [Views, Triggers & Stored Procedures](./07_VIEWS_TRIGGERS_STORED_PROCEDURES.md) | Intermediate | ⭐⭐⭐ |
| 08 | [NoSQL Databases](./08_NOSQL_DATABASES.md) | Intermediate | ⭐⭐⭐⭐ |
| 09 | [Database Design & Modeling](./09_DATABASE_DESIGN_AND_MODELING.md) | Intermediate | ⭐⭐⭐⭐ |
| 10 | [Real-world Scenarios & Advanced](./10_REAL_WORLD_SCENARIOS_AND_ADVANCED.md) | Advanced | ⭐⭐⭐⭐ |

---

## 🚀 How to Use This Guide

### For Freshers (0-1 years):
1. Start with **01_DATABASE_FUNDAMENTALS** - Master the basics
2. Move to **02_SQL_BASICS** - Learn DDL, DML, DQL
3. Practice **03_SQL_QUERIES** - Focus on JOINs
4. Understand **04_NORMALIZATION** - Know up to 3NF
5. Learn **06_TRANSACTIONS** - ACID properties

### For 1-3 Years Experience:
- Everything above, plus:
- Deep dive into **05_INDEXING_AND_PERFORMANCE**
- Study **07_VIEWS_TRIGGERS_STORED_PROCEDURES**
- Learn **08_NOSQL_DATABASES** basics
- Practice **09_DATABASE_DESIGN**

### For 3+ Years Experience:
- Everything above, plus:
- Master **10_REAL_WORLD_SCENARIOS**
- Be ready to discuss scaling, sharding, replication
- Know when to use SQL vs NoSQL

---

## 📋 Most Asked Topics (Priority Order)

### 🔥 Asked in Almost Every Interview:
1. **ACID Properties** - Know all four with examples
2. **JOINs** - Be ready to write JOIN queries
3. **Normalization** - Normalize a table to 3NF
4. **Indexes** - Clustered vs Non-clustered
5. **SQL vs NoSQL** - When to use what

### ⚡ Commonly Asked:
1. **DELETE vs TRUNCATE vs DROP**
2. **WHERE vs HAVING**
3. **Second highest salary query**
4. **Primary Key vs Unique Key**
5. **Isolation Levels**

### 💡 For Senior Positions:
1. **Scaling strategies** - How to handle millions of users
2. **CAP Theorem** - Understand trade-offs
3. **Sharding vs Partitioning**
4. **Database Design** - Design an e-commerce/social media DB
5. **Query Optimization** - Explain EXPLAIN output

---

## 🎯 Quick Revision Cheat Sheets

### SQL Categories:
```
DDL (Define)    → CREATE, ALTER, DROP, TRUNCATE
DML (Manipulate)→ INSERT, UPDATE, DELETE
DQL (Query)     → SELECT
TCL (Transaction)→ COMMIT, ROLLBACK, SAVEPOINT
DCL (Control)   → GRANT, REVOKE
```

### ACID:
```
A → Atomicity   (All or Nothing)
C → Consistency (Valid to Valid state)
I → Isolation   (Concurrent isolation)
D → Durability  (Committed = Permanent)
```

### Normal Forms:
```
1NF → Atomic values, no repeating groups
2NF → 1NF + No partial dependencies
3NF → 2NF + No transitive dependencies
```

### JOIN Types:
```
INNER → Only matching rows
LEFT  → All left + matching right
RIGHT → All right + matching left
FULL  → All from both tables
```

### Isolation Levels (Low → High):
```
Read Uncommitted → Read Committed → Repeatable Read → Serializable
```

### NoSQL Types:
```
Document   → MongoDB
Key-Value  → Redis
Column     → Cassandra
Graph      → Neo4j
```

---

## 💻 Practice Recommendations

### Online Practice:
- **LeetCode** - Database problems
- **HackerRank** - SQL challenges
- **SQLZoo** - Interactive SQL tutorial
- **Mode Analytics** - SQL practice

### Local Practice:
- Install MySQL/PostgreSQL locally
- Create sample tables from the guides
- Practice writing queries by hand
- Use EXPLAIN to understand query plans

### Projects to Build:
1. Library Management System
2. E-commerce Database
3. Social Media Schema
4. Booking System (Hotel/Flight)

---

## 📝 Interview Day Tips

1. **Clarify requirements** before designing
2. **Think out loud** - Show your thought process
3. **Start simple, then optimize**
4. **Know trade-offs** - No solution is perfect
5. **Practice whiteboard writing** - Many interviews are offline
6. **Ask about constraints** - Scale, consistency needs

---

## 📁 File Structure

```
databases/
├── README.md                              # This file
├── 01_DATABASE_FUNDAMENTALS.md            # DBMS, Keys, Constraints
├── 02_SQL_BASICS.md                       # DDL, DML, DQL, TCL, DCL
├── 03_SQL_QUERIES_JOINS_SUBQUERIES.md     # JOINs, Subqueries, Aggregations
├── 04_NORMALIZATION.md                    # 1NF, 2NF, 3NF, BCNF
├── 05_INDEXING_AND_PERFORMANCE.md         # Indexes, Query Optimization
├── 06_TRANSACTIONS_AND_CONCURRENCY.md     # ACID, Isolation, Locking
├── 07_VIEWS_TRIGGERS_STORED_PROCEDURES.md # Views, Triggers, SPs
├── 08_NOSQL_DATABASES.md                  # MongoDB, Redis, Cassandra
├── 09_DATABASE_DESIGN_AND_MODELING.md     # ER Diagrams, Schema Design
└── 10_REAL_WORLD_SCENARIOS_AND_ADVANCED.md# Scaling, Migration, Advanced
```

---

> **Note**: These guides are designed from an interview perspective. Each topic includes what interviewers typically expect and how to answer effectively.

Happy Learning! 🚀

