# 🔄 Transactions & Concurrency - Interview Guide

> **Interviewer's Expectation**: ACID properties are asked in almost every interview! They want to see if you understand database reliability and can handle concurrent access scenarios.

---

## 📌 What is a Transaction?

A **transaction** is a sequence of database operations that are treated as a **single logical unit of work**. Either ALL operations succeed, or NONE do.

### Example - Bank Transfer:

```sql
-- Transfer $100 from Account A to Account B
START TRANSACTION;

UPDATE accounts SET balance = balance - 100 WHERE account_id = 'A';
UPDATE accounts SET balance = balance + 100 WHERE account_id = 'B';

COMMIT;  -- Both operations are saved

-- OR if something goes wrong:
ROLLBACK;  -- Both operations are undone
```

### Why Transactions Matter:

```
WITHOUT TRANSACTION:
Step 1: Deduct $100 from A     ✓ Success
Step 2: Add $100 to B          ✗ System crash!
Result: $100 vanished! A lost money, B didn't receive it.

WITH TRANSACTION:
Step 1: Deduct $100 from A     ✓ Success
Step 2: Add $100 to B          ✗ System crash!
ROLLBACK automatically
Result: A still has original balance. No money lost.
```

---

# 🔷 ACID Properties

### ⭐ This is asked in 95% of database interviews!

ACID is an acronym for four properties that guarantee reliable transaction processing.

```
┌────────────────────────────────────────────────────────┐
│                     A C I D                            │
├──────────────┬──────────────┬───────────┬─────────────┤
│  Atomicity   │ Consistency  │ Isolation │ Durability  │
│              │              │           │             │
│   All or     │  Valid state │ Concurrent│ Committed   │
│   Nothing    │  to valid    │ isolation │ data persists│
└──────────────┴──────────────┴───────────┴─────────────┘
```

---

## 📌 Atomicity - "All or Nothing"

**Definition**: A transaction is an **indivisible unit**. Either ALL operations complete successfully, or NONE do.

### Real-World Analogy:
> Think of ATM withdrawal: You either get cash AND your balance is deducted, OR neither happens. Never one without the other.

### How It's Achieved:
- **Transaction logs** - Record all changes
- **Undo logs** - Store original values for rollback
- **Write-ahead logging (WAL)** - Log before applying changes

### Interview Answer:
> "Atomicity ensures that a transaction is treated as a single, indivisible unit. If any part fails, the entire transaction is rolled back to maintain data integrity. This is implemented using transaction logs and undo mechanisms."

---

## 📌 Consistency - "Valid State to Valid State"

**Definition**: A transaction takes the database from one **valid state** to another **valid state**, maintaining all defined rules and constraints.

### Real-World Analogy:
> A bank transfer keeps total money constant. If A has $1000 and B has $500, after transfer A→B of $100, total should still be $1500.

### What Consistency Ensures:
- All constraints are satisfied (PK, FK, CHECK, UNIQUE)
- Triggers execute properly
- Business rules are maintained
- Referential integrity preserved

### Interview Answer:
> "Consistency ensures the database transitions from one valid state to another. All constraints, triggers, and rules must be satisfied. If a transaction would violate any integrity rule, it's rolled back."

---

## 📌 Isolation - "Concurrent Transactions Don't Interfere"

**Definition**: Concurrent transactions execute as if they were running **sequentially** (one after another), without interfering with each other.

### Real-World Analogy:
> Two people editing the same document shouldn't see half-complete changes from each other.

### Without Isolation:

```
Time    Transaction 1              Transaction 2
─────────────────────────────────────────────────────
T1      Read balance: $1000
T2                                 Read balance: $1000
T3      Withdraw $100
T4                                 Withdraw $100
T5      Balance = $900
T6                                 Balance = $900
T7      COMMIT
T8                                 COMMIT

Result: Both withdrew $100, but balance is $900 instead of $800!
Lost update problem!
```

### With Isolation:

```
Time    Transaction 1              Transaction 2
─────────────────────────────────────────────────────
T1      Read balance: $1000
T2      Withdraw $100
T3      Balance = $900
T4      COMMIT
T5                                 Read balance: $900
T6                                 Withdraw $100
T7                                 Balance = $800
T8                                 COMMIT

Result: Correct! Balance is $800.
```

### How It's Achieved:
- **Locks** - Prevent concurrent access
- **MVCC** (Multi-Version Concurrency Control) - Maintain versions
- **Timestamps** - Order transactions
- **Isolation levels** - Different degrees of isolation

---

## 📌 Durability - "Committed Data Survives"

**Definition**: Once a transaction is **committed**, the changes are **permanent** and will survive system failures (crashes, power loss, etc.).

### Real-World Analogy:
> Once you save a document and see "Saved successfully", it should be there even after a power outage.

### Example:

```sql
START TRANSACTION;
UPDATE accounts SET balance = balance - 100 WHERE account_id = 'A';
INSERT INTO audit_log VALUES ('Transfer', 100, NOW());
COMMIT;  -- After this point, changes are PERMANENT

-- Even if server crashes right after COMMIT,
-- when it restarts, the changes will be there!
```

### How It's Achieved:
- **Write-ahead logging (WAL)** - Log to disk before COMMIT
- **Transaction logs** - Record all committed changes
- **Checkpoints** - Periodic snapshots
- **Disk writes** - Ensure data reaches persistent storage

### Interview Answer:
> "Durability guarantees that once a transaction commits, its changes persist even after system crashes. This is achieved through write-ahead logging where changes are written to disk logs before COMMIT returns, allowing recovery after failures."

---

# 🔷 ISOLATION LEVELS

### ⭐ Very Important! Know all four levels and their trade-offs!

Isolation levels define the degree to which concurrent transactions are isolated from each other.

```
┌─────────────────────────────────────────────────────────────────────┐
│                    ISOLATION LEVELS                                  │
│                                                                      │
│  Read Uncommitted ──→ Read Committed ──→ Repeatable Read ──→ Serializable
│                                                                      │
│  ← Less Isolation                          More Isolation →          │
│  ← Better Performance                      Worse Performance →       │
│  ← More Concurrency                        Less Concurrency →        │
│  ← More Anomalies                          Fewer Anomalies →         │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 📌 1. Read Uncommitted (Lowest Isolation)

**Allows**: Reading data that other transactions have modified but **not yet committed**.

### Characteristics:
- **Dirty reads**: YES ❌
- **Non-repeatable reads**: YES ❌
- **Phantom reads**: YES ❌
- **Performance**: Best
- **Use case**: Almost never (analytics where accuracy isn't critical)

```sql
SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;

-- Transaction 1                    -- Transaction 2
UPDATE accounts SET balance = 500   
WHERE id = 1;
-- NOT committed yet!
                                    SELECT balance FROM accounts 
                                    WHERE id = 1;
                                    -- Reads 500 (uncommitted!)
ROLLBACK;
                                    -- Transaction 2 used data that
                                    -- never existed! DIRTY READ!
```

---

## 📌 2. Read Committed (Default in PostgreSQL, Oracle, SQL Server)

**Allows**: Reading only **committed** data. Each read gets the latest committed value.

### Characteristics:
- **Dirty reads**: NO ✓
- **Non-repeatable reads**: YES ❌
- **Phantom reads**: YES ❌
- **Performance**: Good
- **Use case**: Most OLTP applications

```sql
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;

-- Transaction 1                    -- Transaction 2
                                    SELECT balance FROM accounts 
                                    WHERE id = 1;
                                    -- Reads 1000
UPDATE accounts SET balance = 500   
WHERE id = 1;
COMMIT;
                                    SELECT balance FROM accounts 
                                    WHERE id = 1;
                                    -- Reads 500! (Different!)
                                    -- NON-REPEATABLE READ!
```

---

## 📌 3. Repeatable Read (Default in MySQL InnoDB)

**Guarantees**: Same query returns **same results** within a transaction.

### Characteristics:
- **Dirty reads**: NO ✓
- **Non-repeatable reads**: NO ✓
- **Phantom reads**: YES* ❌ (MySQL InnoDB prevents this too)
- **Performance**: Moderate
- **Use case**: Financial calculations, reports

```sql
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;

-- Transaction 1                    -- Transaction 2
START TRANSACTION;
SELECT balance FROM accounts 
WHERE id = 1;
-- Reads 1000
                                    UPDATE accounts SET balance = 500 
                                    WHERE id = 1;
                                    COMMIT;
SELECT balance FROM accounts 
WHERE id = 1;
-- Still reads 1000! ✓
-- Repeatable read guaranteed!
COMMIT;
```

---

## 📌 4. Serializable (Highest Isolation)

**Guarantees**: Transactions execute as if they were run **one after another** (serially).

### Characteristics:
- **Dirty reads**: NO ✓
- **Non-repeatable reads**: NO ✓
- **Phantom reads**: NO ✓
- **Performance**: Worst (lots of blocking/locking)
- **Use case**: Critical financial transactions, inventory systems

```sql
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;

-- Transaction 1                    -- Transaction 2
START TRANSACTION;
SELECT COUNT(*) FROM orders 
WHERE status = 'pending';
-- Returns 10
                                    INSERT INTO orders (status) 
                                    VALUES ('pending');
                                    -- BLOCKED! Waits for T1

SELECT COUNT(*) FROM orders 
WHERE status = 'pending';
-- Still returns 10 ✓
-- No phantom read!
COMMIT;
                                    -- Now T2 can proceed
```

---

## 📌 Isolation Levels Summary Table

### ⭐ Memorize this table!

| Isolation Level | Dirty Read | Non-Repeatable Read | Phantom Read |
|-----------------|------------|---------------------|--------------|
| Read Uncommitted | ✅ Possible | ✅ Possible | ✅ Possible |
| Read Committed | ❌ Prevented | ✅ Possible | ✅ Possible |
| Repeatable Read | ❌ Prevented | ❌ Prevented | ✅ Possible* |
| Serializable | ❌ Prevented | ❌ Prevented | ❌ Prevented |

*MySQL InnoDB prevents phantom reads at Repeatable Read using gap locking.

---

# 🔷 CONCURRENCY PROBLEMS

## 📌 Dirty Read

**Problem**: Reading uncommitted data from another transaction.

```
Transaction 1                    Transaction 2
─────────────────────────────────────────────────
UPDATE salary = 50000
(not committed)
                                 SELECT salary → 50000 (dirty!)
ROLLBACK
                                 Using invalid data! ❌
```

**Solution**: Use Read Committed or higher isolation level.

---

## 📌 Non-Repeatable Read

**Problem**: Same query returns different results within a transaction.

```
Transaction 1                    Transaction 2
─────────────────────────────────────────────────
SELECT balance → 1000
                                 UPDATE balance = 500
                                 COMMIT
SELECT balance → 500
Different result! ❌
```

**Solution**: Use Repeatable Read or higher isolation level.

---

## 📌 Phantom Read

**Problem**: New rows appear/disappear between queries in same transaction.

```
Transaction 1                    Transaction 2
─────────────────────────────────────────────────
SELECT COUNT(*) WHERE status='new' → 10
                                 INSERT INTO orders(status='new')
                                 COMMIT
SELECT COUNT(*) WHERE status='new' → 11
Phantom row appeared! ❌
```

**Solution**: Use Serializable isolation level.

---

## 📌 Lost Update

**Problem**: Two transactions update the same row, one overwrites the other.

```
Transaction 1                    Transaction 2
─────────────────────────────────────────────────
SELECT balance → 1000            SELECT balance → 1000
Calculate: 1000 + 100
                                 Calculate: 1000 + 50
UPDATE balance = 1100
                                 UPDATE balance = 1050
COMMIT                           COMMIT

Expected: 1150, Actual: 1050 (T1's update lost!) ❌
```

**Solution**: 
- Pessimistic locking (SELECT FOR UPDATE)
- Optimistic locking (version column)
- Serializable isolation

---

## 📌 Deadlock

**Problem**: Two transactions wait for each other indefinitely.

```
Transaction 1                    Transaction 2
─────────────────────────────────────────────────
LOCK Table A                     LOCK Table B
Try LOCK Table B (waits...)      Try LOCK Table A (waits...)

Both waiting forever! DEADLOCK! ❌
```

### Visual:

```
         T1 ─────────→ Resource A ←───────── T2
          │                                   │
          │  "I need B"         "I need A"   │
          │                                   │
          ↓                                   ↓
         T1 ←───────── Resource B ─────────→ T2
                    CIRCULAR WAIT!
```

### Deadlock Prevention:

```sql
-- 1. Lock in consistent order (always A before B)
-- 2. Use lock timeouts
SET innodb_lock_wait_timeout = 5;  -- MySQL

-- 3. Keep transactions short
-- 4. Access less data

-- 5. Use SELECT FOR UPDATE NOWAIT (fails immediately if locked)
SELECT * FROM accounts WHERE id = 1 FOR UPDATE NOWAIT;
```

### Interview Answer:
> "A deadlock occurs when two or more transactions are waiting for each other to release locks, creating a circular dependency. Databases detect deadlocks and automatically roll back one transaction (the victim). I prevent deadlocks by: accessing tables in consistent order, keeping transactions short, and using lock timeouts."

---

# 🔷 LOCKING MECHANISMS

## 📌 Types of Locks

### Shared Lock (S Lock / Read Lock)
- Multiple transactions can hold simultaneously
- Allows reading, prevents writing
- Used by SELECT statements (in some isolation levels)

```sql
-- Multiple transactions can read simultaneously
SELECT * FROM products WHERE id = 1;  -- Gets shared lock
```

### Exclusive Lock (X Lock / Write Lock)
- Only one transaction can hold
- Prevents both reading and writing by others
- Used by INSERT, UPDATE, DELETE

```sql
UPDATE products SET price = 100 WHERE id = 1;  -- Gets exclusive lock
```

### Lock Compatibility Matrix:

|  | Shared (S) | Exclusive (X) |
|--|------------|---------------|
| **Shared (S)** | ✅ Compatible | ❌ Conflict |
| **Exclusive (X)** | ❌ Conflict | ❌ Conflict |

---

## 📌 Lock Granularity

```
Row Lock     → Locks single row (most granular)
Page Lock    → Locks page of data
Table Lock   → Locks entire table (least granular)
```

### Trade-offs:

| Lock Type | Concurrency | Overhead |
|-----------|-------------|----------|
| Row | High | High |
| Page | Medium | Medium |
| Table | Low | Low |

---

## 📌 Optimistic vs Pessimistic Locking

### Pessimistic Locking ("Assume conflict will happen")

```sql
-- Lock the row explicitly
SELECT * FROM accounts WHERE id = 1 FOR UPDATE;

-- Now only this transaction can modify
UPDATE accounts SET balance = balance - 100 WHERE id = 1;

COMMIT;  -- Lock released
```

**Use when**: High contention expected, conflicts are common.

### Optimistic Locking ("Assume conflict won't happen")

```sql
-- Add version column
ALTER TABLE accounts ADD COLUMN version INT DEFAULT 0;

-- Read with version
SELECT balance, version FROM accounts WHERE id = 1;
-- balance = 1000, version = 5

-- Update only if version matches
UPDATE accounts 
SET balance = 900, version = version + 1 
WHERE id = 1 AND version = 5;

-- If rows_affected = 0, someone else updated! Retry.
```

**Use when**: Low contention, conflicts are rare.

### Comparison:

| Pessimistic | Optimistic |
|-------------|------------|
| Locks immediately | Checks at commit time |
| Blocks other transactions | Doesn't block |
| Good for high contention | Good for low contention |
| Can cause deadlocks | No deadlocks |
| Uses DB locks | Uses version/timestamp |

---

# 🎯 Quick Interview Cheat Sheet

## ACID Summary:
```
A - Atomicity:   All or nothing
C - Consistency: Valid → Valid state
I - Isolation:   No interference
D - Durability:  Committed = Permanent
```

## Isolation Levels (Low → High):
```
Read Uncommitted → Read Committed → Repeatable Read → Serializable
     Most                                              Most
   Permissive                                        Restrictive
```

## Concurrency Problems:
```
Dirty Read        → Reading uncommitted data
Non-repeatable    → Same query, different results
Phantom Read      → Rows appear/disappear
Lost Update       → Updates overwritten
Deadlock          → Circular wait for locks
```

## Locking:
```
Shared (S)    → Read lock, multiple holders OK
Exclusive (X) → Write lock, single holder only
Optimistic    → Version check at commit
Pessimistic   → Lock immediately (FOR UPDATE)
```

---

## 💡 Pro Interview Tips

1. **Give real-world examples**
   > "Atomicity is like a bank transfer - either both debit and credit happen, or neither does."

2. **Know your database defaults**
   - MySQL InnoDB: Repeatable Read
   - PostgreSQL: Read Committed
   - SQL Server: Read Committed
   - Oracle: Read Committed

3. **Explain trade-offs**
   > "Higher isolation = more consistency but less concurrency and performance."

4. **Connect concepts**
   > "Dirty reads happen at Read Uncommitted, so we use at least Read Committed in production."

5. **Be ready for scenario questions**:
   - "How would you prevent lost updates?"
   - "Your app shows inconsistent data - which isolation level would you use?"
   - "Two users are editing the same record - how do you handle this?"

6. **Know how to check/set isolation level**:
   ```sql
   -- MySQL
   SELECT @@transaction_ISOLATION;
   SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
   
   -- PostgreSQL
   SHOW transaction_isolation;
   SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
   ```

