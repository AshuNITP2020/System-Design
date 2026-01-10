# ⚡ Indexing & Performance - Interview Guide

> **Interviewer's Expectation**: This separates juniors from seniors. Understanding indexes and query optimization shows you can work with production-scale databases.

---

## 📌 What is an Index?

An **index** is a data structure that improves the speed of data retrieval operations on a database table at the cost of additional storage and write performance.

### Analogy:
```
Think of a book:
- Without index: Read every page to find "Database" (Full Table Scan)
- With index: Go to index page, find "Database → page 42" (Index Seek)

A database index works the same way!
```

### Interview Answer:
> "An index is like a book's index - it's a separate data structure that maintains pointers to the actual data, allowing the database to find rows without scanning the entire table. It trades storage space and write performance for faster reads."

---

## 📌 Why Do We Use Indexes?

### Without Index (Full Table Scan):
```
Query: SELECT * FROM employees WHERE name = 'John';

Process: Check every single row
Row 1: Alice  → No
Row 2: Bob    → No
Row 3: John   → Yes ✓
Row 4: Jane   → No
...
Row 1,000,000: David → No

Time Complexity: O(n) - Must scan all rows
```

### With Index (Index Seek):
```
Query: SELECT * FROM employees WHERE name = 'John';

Process: Look up in B-Tree index
         [M]
        /   \
      [D]   [R]
      / \   / \
    [A] [J] [P] [T]
         ↓
       John → Row pointer → Direct access

Time Complexity: O(log n) - Much faster!
```

---

## 📌 How Indexes Work Internally

Most databases use **B-Tree** (Balanced Tree) or **B+ Tree** for indexes.

### B+ Tree Structure:

```
                    [50]                     ← Root Node
                   /    \
              [20|30]  [70|90]               ← Branch Nodes
              /  |  \    |  \  \
           [10] [25] [35] [60] [80] [95]     ← Leaf Nodes
            ↓    ↓    ↓    ↓    ↓    ↓
          Data Data Data Data Data Data      ← Actual Row Pointers

Properties:
- Balanced: All leaf nodes at same level
- Sorted: Values in order for range queries
- Leaf nodes: Contain pointers to actual data
- Branch nodes: Guide the search
```

### Index Lookup Process:
```sql
SELECT * FROM employees WHERE emp_id = 35;

Step 1: Start at root [50]
        35 < 50, go left

Step 2: At [20|30]
        35 > 30, go right

Step 3: At leaf [35]
        Found! Follow pointer to row data
```

---

## 📌 Types of Indexes

### ⭐ Clustered Index

**Definition**: Determines the **physical order** of data in the table. Only ONE per table.

**Key Characteristics**:
- Data rows are stored in index order
- Like a phone book sorted by name
- Primary key creates clustered index by default (in most RDBMS)
- Finding by clustered index = Direct disk access

```sql
-- Creates clustered index (MySQL InnoDB)
CREATE TABLE employees (
    emp_id INT PRIMARY KEY,  -- Automatically clustered
    name VARCHAR(100)
);

-- Explicit clustered index (SQL Server)
CREATE CLUSTERED INDEX idx_emp_id ON employees(emp_id);
```

**Visual**:
```
Clustered Index on emp_id:

Index Structure:              Physical Data Order:
    [50]                      ┌─────┬───────┐
   /    \                     │ 10  │ Alice │
 [20]   [80]                  │ 20  │ Bob   │
  ↓      ↓                    │ 30  │ Carol │ ← Sorted by emp_id
┌────────────┐                │ 50  │ David │
│ Data rows  │                │ 80  │ Eve   │
│ sorted by  │                └─────┴───────┘
│ emp_id     │
└────────────┘
```

---

### ⭐ Non-Clustered Index

**Definition**: Separate structure that contains index keys + pointers to data rows. Can have MULTIPLE per table.

**Key Characteristics**:
- Doesn't change physical data order
- Like a book's index at the end
- Contains: Index key → Row pointer
- Requires extra lookup to get data (bookmark lookup)

```sql
-- Create non-clustered index
CREATE INDEX idx_name ON employees(name);

-- Composite non-clustered index
CREATE INDEX idx_name_dept ON employees(name, department);
```

**Visual**:
```
Non-Clustered Index on name:

Index Structure:              Physical Data (unchanged):
    [John]                    ┌─────┬───────┐
   /      \                   │ 50  │ David │
[Alice] [Mike]                │ 10  │ Alice │
   ↓       ↓                  │ 80  │ Eve   │ ← NOT sorted by name
   │       │                  │ 30  │ John  │
   │       │                  │ 20  │ Mike  │
   ▼       ▼                  └─────┴───────┘
Pointer  Pointer
to row   to row
```

---

### 📌 Clustered vs Non-Clustered Index

### ⭐ This comparison is asked in EVERY interview!

| Feature | Clustered | Non-Clustered |
|---------|-----------|---------------|
| **Physical order** | Defines data order | Doesn't affect order |
| **Number per table** | Only 1 | Multiple allowed |
| **Speed (exact match)** | Faster (direct access) | Slower (extra lookup) |
| **Speed (range query)** | Faster (sequential) | Depends |
| **Storage** | No extra (IS the data) | Extra storage needed |
| **Default for** | Primary Key | Non-PK columns |
| **Leaf nodes contain** | Actual data | Pointers to data |

### When to Use What:

```
Clustered Index:
✅ Columns frequently used in ORDER BY
✅ Columns frequently used in range queries (BETWEEN, <, >)
✅ Columns with high uniqueness
✅ Primary key (default)

Non-Clustered Index:
✅ Columns frequently used in WHERE clause
✅ Columns used in JOINs
✅ Columns used in GROUP BY
✅ When you need multiple indexes
```

---

## 📌 Other Index Types

### Unique Index
```sql
CREATE UNIQUE INDEX idx_email ON users(email);
-- Prevents duplicate values
```

### Composite (Multi-column) Index
```sql
CREATE INDEX idx_name_dept ON employees(last_name, first_name, department);

-- ✅ Uses index:
SELECT * FROM employees WHERE last_name = 'Smith';
SELECT * FROM employees WHERE last_name = 'Smith' AND first_name = 'John';

-- ❌ May NOT use index (leftmost column missing):
SELECT * FROM employees WHERE first_name = 'John';
SELECT * FROM employees WHERE department = 'IT';
```

**Leftmost Prefix Rule**: Index on (A, B, C) can be used for:
- (A)
- (A, B)
- (A, B, C)
- NOT for (B), (C), (B, C)

### Covering Index
```sql
CREATE INDEX idx_cover ON employees(department, salary, name);

-- This query is "covered" - no table lookup needed!
SELECT name, salary FROM employees WHERE department = 'IT';
-- All columns are in the index
```

### Full-Text Index
```sql
CREATE FULLTEXT INDEX idx_content ON articles(title, body);

SELECT * FROM articles 
WHERE MATCH(title, body) AGAINST('database optimization');
```

### Hash Index
```sql
-- In MySQL, Memory storage engine
CREATE TABLE cache (
    key_col VARCHAR(100),
    value_col TEXT,
    INDEX USING HASH (key_col)
);

-- ✅ Excellent for equality: WHERE key = 'abc'
-- ❌ Useless for range: WHERE key > 'abc'
```

---

## 📌 Pros & Cons of Indexing

### ✅ Advantages:

1. **Faster SELECT queries** - O(log n) instead of O(n)
2. **Faster JOINs** - Quick key lookups
3. **Faster ORDER BY** - Pre-sorted data
4. **Faster GROUP BY** - Efficient grouping
5. **Unique constraint enforcement**

### ❌ Disadvantages:

1. **Slower INSERT/UPDATE/DELETE** - Index must be updated
2. **Extra storage space** - Index structures need disk space
3. **Maintenance overhead** - Index fragmentation
4. **Query optimizer confusion** - Too many indexes can confuse optimizer

---

## 📌 When NOT to Use Indexes

### ⭐ Very Important Interview Question!

**Don't create indexes when:**

1. **Small tables** - Full scan is faster than index lookup
   ```sql
   -- Table with 100 rows? Don't bother indexing
   ```

2. **Low cardinality columns** - Boolean, gender, status
   ```sql
   -- 'status' has only 'active'/'inactive' - poor index candidate
   SELECT * FROM users WHERE status = 'active';  -- Returns 50% of rows
   ```

3. **Frequently updated columns** - Index rebuild overhead
   ```sql
   -- 'view_count' updated on every page view - don't index
   ```

4. **Rarely queried columns** - No benefit, only cost
   ```sql
   -- 'internal_notes' field never in WHERE clause
   ```

5. **When most rows are returned** - Optimizer will ignore index
   ```sql
   -- Query returns 80% of table - full scan is better
   ```

### Interview Answer:
> "Indexes are not free - they cost storage and slow down writes. I avoid indexing small tables, low-cardinality columns like boolean fields, frequently updated columns, and columns that aren't used in WHERE, JOIN, or ORDER BY clauses."

---

# 🔷 QUERY OPTIMIZATION

## 📌 How Do You Optimize a Query?

### The Optimization Process:

```
1. Identify slow query (logs, monitoring)
           ↓
2. Analyze with EXPLAIN
           ↓
3. Check index usage
           ↓
4. Rewrite query if needed
           ↓
5. Add/modify indexes
           ↓
6. Test and measure
           ↓
7. Monitor in production
```

---

## 📌 Query Execution Plan (EXPLAIN)

### What is EXPLAIN?

EXPLAIN shows **how the database will execute your query** - which indexes, join types, row estimates.

```sql
EXPLAIN SELECT e.name, d.dept_name
FROM employees e
JOIN departments d ON e.dept_id = d.dept_id
WHERE e.salary > 50000;
```

### Sample EXPLAIN Output (MySQL):

```
+----+-------------+-------+------+---------------+------+---------+------+------+-------------+
| id | select_type | table | type | possible_keys | key  | key_len | ref  | rows | Extra       |
+----+-------------+-------+------+---------------+------+---------+------+------+-------------+
|  1 | SIMPLE      | e     | ALL  | NULL          | NULL | NULL    | NULL | 1000 | Using where |
|  1 | SIMPLE      | d     | ref  | PRIMARY       | PRIMARY| 4     | e.dept_id| 1 | NULL       |
+----+-------------+-------+------+---------------+------+---------+------+------+-------------+
```

### Key EXPLAIN Columns:

| Column | What It Shows | What to Look For |
|--------|---------------|------------------|
| **type** | Join type | ALL is bad, const/ref/range are good |
| **possible_keys** | Indexes available | Should have options |
| **key** | Index actually used | Should NOT be NULL |
| **rows** | Estimated rows scanned | Lower is better |
| **Extra** | Additional info | "Using index" is good, "Using filesort" is bad |

### Access Types (Best to Worst):
```
const  → Single row (PK lookup)
eq_ref → One row per table (unique index)
ref    → Multiple rows (non-unique index)
range  → Index range scan
index  → Full index scan
ALL    → Full table scan (WORST!)
```

---

## 📌 What Causes Slow Queries?

### Common Culprits:

#### 1. Missing Indexes
```sql
-- SLOW: Full table scan
SELECT * FROM orders WHERE customer_email = 'john@email.com';

-- FIX: Add index
CREATE INDEX idx_email ON orders(customer_email);
```

#### 2. SELECT * (Fetching unnecessary columns)
```sql
-- SLOW: Fetches all columns, can't use covering index
SELECT * FROM employees WHERE department = 'IT';

-- BETTER: Only needed columns
SELECT name, salary FROM employees WHERE department = 'IT';
```

#### 3. Functions on Indexed Columns
```sql
-- SLOW: Index on hire_date is NOT used!
SELECT * FROM employees WHERE YEAR(hire_date) = 2024;

-- BETTER: Index CAN be used
SELECT * FROM employees 
WHERE hire_date >= '2024-01-01' AND hire_date < '2025-01-01';
```

#### 4. Leading Wildcards in LIKE
```sql
-- SLOW: Can't use index
SELECT * FROM products WHERE name LIKE '%phone%';

-- FASTER: Can use index (prefix match)
SELECT * FROM products WHERE name LIKE 'phone%';
```

#### 5. Implicit Type Conversion
```sql
-- SLOW: String column compared with number
SELECT * FROM users WHERE phone = 1234567890;  -- phone is VARCHAR

-- BETTER: Match types
SELECT * FROM users WHERE phone = '1234567890';
```

#### 6. OR Conditions (Sometimes)
```sql
-- SLOW: May not use indexes efficiently
SELECT * FROM employees WHERE dept_id = 1 OR dept_id = 2;

-- SOMETIMES FASTER: Using IN
SELECT * FROM employees WHERE dept_id IN (1, 2);

-- OR: UNION for complex cases
SELECT * FROM employees WHERE dept_id = 1
UNION ALL
SELECT * FROM employees WHERE dept_id = 2;
```

#### 7. Subqueries Instead of JOINs
```sql
-- SLOW: Subquery executed for each row
SELECT * FROM orders 
WHERE customer_id IN (SELECT id FROM customers WHERE country = 'USA');

-- FASTER: JOIN
SELECT o.* FROM orders o
JOIN customers c ON o.customer_id = c.id
WHERE c.country = 'USA';
```

#### 8. No LIMIT on Large Result Sets
```sql
-- SLOW: Fetches all million rows
SELECT * FROM logs WHERE date > '2024-01-01';

-- BETTER: Limit results
SELECT * FROM logs WHERE date > '2024-01-01' LIMIT 1000;
```

---

## 📌 Query Optimization Techniques

### 1. Use Appropriate Indexes
```sql
-- Analyze which columns to index
-- Look at WHERE, JOIN ON, ORDER BY, GROUP BY clauses
```

### 2. Use Covering Indexes
```sql
-- All columns in SELECT are in the index
CREATE INDEX idx_cover ON orders(status, order_date, total);

SELECT status, order_date, total 
FROM orders 
WHERE status = 'completed';
-- No table lookup needed!
```

### 3. Optimize JOINs
```sql
-- Ensure JOIN columns are indexed
-- Join smaller result set first
-- Use appropriate JOIN type
```

### 4. Use Pagination
```sql
-- SLOW: OFFSET for large values
SELECT * FROM products ORDER BY id LIMIT 10 OFFSET 100000;

-- FASTER: Keyset pagination
SELECT * FROM products 
WHERE id > 100000 
ORDER BY id 
LIMIT 10;
```

### 5. Batch Operations
```sql
-- SLOW: 1000 individual inserts
INSERT INTO logs VALUES (...);
INSERT INTO logs VALUES (...);
-- ... 998 more

-- FASTER: Batch insert
INSERT INTO logs VALUES 
    (...),
    (...),
    -- ... more rows
    (...);
```

### 6. Denormalize for Read Performance
```sql
-- Normalized: Requires JOIN every time
SELECT o.*, c.name 
FROM orders o 
JOIN customers c ON o.customer_id = c.id;

-- Denormalized: Redundant but faster
SELECT order_id, customer_name, total 
FROM orders;  -- customer_name stored in orders
```

---

## 📌 Index Strategy Questions

### "How many indexes should a table have?"

**Interview Answer**:
> "There's no fixed number. It depends on:
> 1. Read vs Write ratio - Write-heavy tables need fewer indexes
> 2. Query patterns - Index columns that appear in WHERE, JOIN, ORDER BY
> 3. Table size - Small tables don't need many indexes
> 4. Cardinality - High cardinality columns benefit more
> 
> As a guideline, I start with indexes on primary key, foreign keys, and frequently filtered columns, then add more based on slow query analysis."

### "When would you drop an index?"

**Interview Answer**:
> "I'd drop an index when:
> 1. It's never used (check `sys.dm_db_index_usage_stats` in SQL Server or `performance_schema` in MySQL)
> 2. It duplicates another index
> 3. Write performance is suffering and the index isn't providing read benefits
> 4. Storage space is critical"

---

# 🎯 Quick Interview Cheat Sheet

## Index Types:
```
Clustered       → Physical data order, 1 per table
Non-clustered   → Separate structure, multiple allowed
Unique          → No duplicates
Composite       → Multiple columns
Covering        → Contains all query columns
Full-text       → Text search
Hash            → Equality only, O(1)
```

## When Index Helps:
```
✅ WHERE clause filtering
✅ JOIN conditions
✅ ORDER BY sorting
✅ GROUP BY grouping
✅ DISTINCT elimination
```

## When Index Hurts:
```
❌ Small tables
❌ Low cardinality columns
❌ Frequently updated columns
❌ Write-heavy workloads
❌ Functions on indexed columns
```

## EXPLAIN Red Flags:
```
❌ type = ALL (full table scan)
❌ key = NULL (no index used)
❌ rows = large number
❌ Extra = "Using filesort"
❌ Extra = "Using temporary"
```

---

## 💡 Pro Interview Tips

1. **Always mention trade-offs**
   > "Indexes speed up reads but slow down writes"

2. **Show you understand the internals**
   > "B-Tree provides O(log n) lookups..."

3. **Demonstrate practical experience**
   > "In my last project, I reduced query time from 5s to 50ms by adding a composite index on..."

4. **Know your database**
   - MySQL: InnoDB uses clustered index on PK
   - PostgreSQL: All indexes are non-clustered
   - SQL Server: Can choose clustered index column

5. **Common follow-up questions**:
   - "What's index fragmentation?"
   - "How do you monitor index usage?"
   - "What's a covering index?"
   - "Explain B-Tree vs B+ Tree"

