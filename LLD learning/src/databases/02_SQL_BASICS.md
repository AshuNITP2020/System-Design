# 📝 SQL Basics - Interview Guide

> **Interviewer's Expectation**: Can you write and understand SQL? Do you know the difference between DDL and DML? These questions test your practical SQL knowledge.

---

## 📌 SQL Overview

**SQL (Structured Query Language)** is the standard language for interacting with relational databases.

### SQL Categories:

```
┌─────────────────────────────────────────────────────────────┐
│                         SQL                                  │
├─────────────┬─────────────┬──────────┬──────────┬───────────┤
│    DDL      │    DML      │   DQL    │   TCL    │    DCL    │
├─────────────┼─────────────┼──────────┼──────────┼───────────┤
│ CREATE      │ INSERT      │ SELECT   │ COMMIT   │ GRANT     │
│ ALTER       │ UPDATE      │          │ ROLLBACK │ REVOKE    │
│ DROP        │ DELETE      │          │ SAVEPOINT│           │
│ TRUNCATE    │             │          │          │           │
│ RENAME      │             │          │          │           │
└─────────────┴─────────────┴──────────┴──────────┴───────────┘
```

---

# 🔷 DDL - Data Definition Language

> **What interviewer wants**: Understanding of structure operations

DDL commands define/modify the **structure** of database objects.

---

## 📌 CREATE

Creates new database objects (tables, views, indexes, etc.)

```sql
-- Create Database
CREATE DATABASE company;

-- Create Table
CREATE TABLE employees (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    salary DECIMAL(10, 2),
    department_id INT,
    hire_date DATE DEFAULT CURRENT_DATE,
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

-- Create Index
CREATE INDEX idx_employee_name ON employees(name);

-- Create View
CREATE VIEW high_earners AS
SELECT * FROM employees WHERE salary > 100000;
```

---

## 📌 ALTER

Modifies existing database objects.

```sql
-- Add a column
ALTER TABLE employees ADD COLUMN phone VARCHAR(15);

-- Modify column data type
ALTER TABLE employees MODIFY COLUMN phone VARCHAR(20);

-- Rename column
ALTER TABLE employees RENAME COLUMN phone TO contact_number;

-- Drop column
ALTER TABLE employees DROP COLUMN contact_number;

-- Add constraint
ALTER TABLE employees ADD CONSTRAINT chk_salary CHECK (salary > 0);

-- Drop constraint
ALTER TABLE employees DROP CONSTRAINT chk_salary;

-- Add foreign key
ALTER TABLE employees 
ADD CONSTRAINT fk_dept 
FOREIGN KEY (department_id) REFERENCES departments(id);
```

---

## 📌 DROP

**Permanently removes** database objects.

```sql
-- Drop table (removes structure AND data)
DROP TABLE employees;

-- Drop only if exists (prevents error)
DROP TABLE IF EXISTS employees;

-- Drop database
DROP DATABASE company;

-- Drop index
DROP INDEX idx_employee_name ON employees;

-- Drop view
DROP VIEW high_earners;
```

### ⚠️ Interview Warning:
> "DROP is irreversible! In production, always have backups before using DROP."

---

## 📌 TRUNCATE

Removes **all rows** from a table but keeps the structure.

```sql
TRUNCATE TABLE employees;
```

---

## 📌 DELETE vs TRUNCATE vs DROP

### ⭐ This is asked in 90% of interviews!

| Feature             | DELETE            | TRUNCATE | DROP |
|---------            |--------           |----------|------|
| **What it removes** | Specific rows     | All rows | Entire table |
| **WHERE clause**    | ✅ Supported      | ❌ Not supported | ❌ Not applicable |
| **Table structure** | Preserved         | Preserved | Removed |
| **Speed**           | Slower            | Faster | Fastest |
| **Transaction log** | Logs each row     | Minimal logging | Minimal logging |
| **Rollback**        | ✅ Can rollback   | ❌ Cannot rollback* | ❌ Cannot rollback |
| **Triggers**        | ✅ Fires triggers | ❌ No triggers | ❌ No triggers |
| **Auto-increment**  | Not reset         | Reset to initial | N/A |
| **Foreign key**     | Respects FK       | Fails if FK exists | Fails if FK exists |

*In some databases like SQL Server, TRUNCATE can be rolled back within a transaction.

### Interview-Ready Answer:
```
"DELETE removes specific rows, logs each deletion, fires triggers, and can be 
rolled back. TRUNCATE removes all rows quickly with minimal logging, resets 
auto-increment, but cannot be rolled back. DROP removes the entire table 
structure along with data."
```

### When to Use What:
```
DELETE  → Remove specific rows, need rollback capability
TRUNCATE → Remove all rows quickly, don't need triggers
DROP    → Don't need the table anymore
```

---

## 📌 What Happens When You Drop a Table?

### Interview Answer:

When you execute `DROP TABLE`:

1. **Data is permanently deleted** - All rows are gone
2. **Structure is removed** - Table definition deleted from metadata
3. **Indexes are dropped** - All associated indexes removed
4. **Constraints removed** - PK, FK, CHECK constraints gone
5. **Views become invalid** - Views referencing this table break
6. **Triggers are dropped** - Associated triggers removed
7. **Permissions lost** - Access rights for the table removed
8. **Space reclaimed** - Disk space is freed

```sql
-- Before: Table exists with data
SELECT * FROM employees;  -- Returns 1000 rows

-- Drop
DROP TABLE employees;

-- After: Table doesn't exist
SELECT * FROM employees;  -- ERROR: Table doesn't exist
```

---

# 🔷 DML - Data Manipulation Language

> **What interviewer wants**: Can you manipulate data correctly?

DML commands manipulate **data within tables**.

---

## 📌 INSERT

Adds new rows to a table.

```sql
-- Insert single row (all columns)
INSERT INTO employees VALUES (1, 'John', 'john@email.com', 50000, 1, '2024-01-15');

-- Insert with specific columns (recommended)
INSERT INTO employees (name, email, salary, department_id)
VALUES ('Jane', 'jane@email.com', 60000, 2);

-- Insert multiple rows
INSERT INTO employees (name, email, salary)
VALUES 
    ('Alice', 'alice@email.com', 55000),
    ('Bob', 'bob@email.com', 52000),
    ('Charlie', 'charlie@email.com', 58000);

-- Insert from SELECT (copy data)
INSERT INTO employees_backup
SELECT * FROM employees WHERE department_id = 1;
```

---

## 📌 UPDATE

Modifies existing rows.

```sql
-- Update specific rows
UPDATE employees 
SET salary = 65000 
WHERE id = 1;

-- Update multiple columns
UPDATE employees 
SET salary = 70000, department_id = 3 
WHERE name = 'John';

-- Update with calculation
UPDATE employees 
SET salary = salary * 1.10 
WHERE department_id = 2;

-- Update all rows (DANGEROUS - no WHERE!)
UPDATE employees SET status = 'active';

-- Update with subquery
UPDATE employees 
SET salary = (SELECT AVG(salary) FROM employees)
WHERE performance_rating < 3;
```

### ⚠️ Interview Warning:
> "Always use WHERE with UPDATE unless you intentionally want to update ALL rows. Forgetting WHERE clause is a common production disaster!"

---

## 📌 DELETE

Removes rows from a table.

```sql
-- Delete specific rows
DELETE FROM employees WHERE id = 1;

-- Delete with multiple conditions
DELETE FROM employees 
WHERE department_id = 5 AND hire_date < '2020-01-01';

-- Delete all rows (use TRUNCATE instead for speed)
DELETE FROM employees;

-- Delete with subquery
DELETE FROM employees 
WHERE department_id IN (
    SELECT id FROM departments WHERE name = 'Obsolete'
);
```

---

## 📌 DELETE vs UPDATE

| Feature | DELETE | UPDATE |
|---------|--------|--------|
| **Purpose** | Remove rows | Modify existing data |
| **Row existence** | Row no longer exists | Row still exists with new values |
| **Auto-increment** | No effect | No effect |
| **Use case** | Remove outdated/unwanted data | Correct or change data values |

### Interview Question: "When to soft delete vs hard delete?"

**Hard Delete (DELETE)**:
```sql
DELETE FROM users WHERE id = 1;
```

**Soft Delete (UPDATE)**:
```sql
UPDATE users SET is_deleted = true, deleted_at = NOW() WHERE id = 1;
```

**Answer**:
> "Soft delete is preferred when you need audit trails, data recovery possibility, or maintaining referential integrity. Hard delete when you need to truly remove data (GDPR compliance, storage optimization)."

---

# 🔷 DQL - Data Query Language

> **What interviewer wants**: Can you retrieve data effectively?

### SQL Query Execution Order:
```
1. FROM       - Choose tables
2. WHERE      - Filter rows
3. GROUP BY   - Group rows
4. HAVING     - Filter groups
5. SELECT     - Choose columns
6. ORDER BY   - Sort results
7. LIMIT      - Limit rows
```

---

# 🔷 TCL - Transaction Control Language

> **What interviewer wants**: Do you understand transaction safety?

---

## 📌 COMMIT

Saves all changes made in the current transaction permanently.

```sql
START TRANSACTION;

UPDATE accounts SET balance = balance - 1000 WHERE id = 1;
UPDATE accounts SET balance = balance + 1000 WHERE id = 2;

COMMIT;  -- Both updates are now permanent
```

---

## 📌 ROLLBACK

Undoes all changes made in the current transaction.

```sql
START TRANSACTION;

DELETE FROM employees WHERE department_id = 5;

-- Oops! Wrong department!
ROLLBACK;  -- All deletes are undone
```

---

## 📌 SAVEPOINT

Creates a point within a transaction to rollback to.

```sql
START TRANSACTION;

INSERT INTO orders VALUES (1, 'Order 1');
SAVEPOINT sp1;

INSERT INTO orders VALUES (2, 'Order 2');
SAVEPOINT sp2;

INSERT INTO orders VALUES (3, 'Order 3');

-- Something went wrong with order 3
ROLLBACK TO sp2;  -- Order 3 is undone, 1 and 2 remain

COMMIT;  -- Orders 1 and 2 are saved
```

### Interview Example:
```sql
-- Bank transfer with savepoints
START TRANSACTION;

UPDATE accounts SET balance = balance - 1000 WHERE id = 1;
SAVEPOINT after_debit;

UPDATE accounts SET balance = balance + 1000 WHERE id = 2;

-- If credit fails, rollback only the credit
-- ROLLBACK TO after_debit;

COMMIT;
```

---

# 🔷 DCL - Data Control Language

> **What interviewer wants**: Do you understand database security?

---

## 📌 GRANT

Gives specific permissions to users.

```sql
-- Grant SELECT permission
GRANT SELECT ON employees TO 'readonly_user'@'localhost';

-- Grant multiple permissions
GRANT SELECT, INSERT, UPDATE ON employees TO 'data_entry'@'localhost';

-- Grant all permissions
GRANT ALL PRIVILEGES ON company.* TO 'admin'@'localhost';

-- Grant with option to grant to others
GRANT SELECT ON employees TO 'manager'@'localhost' WITH GRANT OPTION;

-- Grant to role (MySQL 8+)
CREATE ROLE 'analyst';
GRANT SELECT ON company.* TO 'analyst';
GRANT 'analyst' TO 'john'@'localhost';
```

---

## 📌 REVOKE

Removes previously granted permissions.

```sql
-- Revoke specific permission
REVOKE INSERT ON employees FROM 'data_entry'@'localhost';

-- Revoke all permissions
REVOKE ALL PRIVILEGES ON company.* FROM 'admin'@'localhost';

-- Revoke grant option
REVOKE GRANT OPTION ON employees FROM 'manager'@'localhost';
```

---

## 📌 Common Privileges

| Privilege | Description |
|-----------|-------------|
| SELECT | Read data |
| INSERT | Add new rows |
| UPDATE | Modify existing rows |
| DELETE | Remove rows |
| CREATE | Create tables/databases |
| DROP | Remove tables/databases |
| ALTER | Modify table structure |
| INDEX | Create/drop indexes |
| GRANT OPTION | Grant permissions to others |
| ALL PRIVILEGES | All of the above |

---

# 🎯 Quick Interview Cheat Sheet

## SQL Categories Memory Aid:
```
DDL - "Define"    → CREATE, ALTER, DROP, TRUNCATE
DML - "Manipulate"→ INSERT, UPDATE, DELETE
DQL - "Query"     → SELECT
TCL - "Transaction"→ COMMIT, ROLLBACK, SAVEPOINT
DCL - "Control"   → GRANT, REVOKE
```

## Key Differences to Remember:

| Question | Answer |
|----------|--------|
| DELETE vs TRUNCATE? | DELETE is slower, can rollback, respects WHERE; TRUNCATE is faster, can't rollback, removes all |
| WHERE vs HAVING? | WHERE filters rows, HAVING filters groups after GROUP BY |
| DROP vs TRUNCATE? | DROP removes structure+data; TRUNCATE keeps structure |
| COMMIT vs ROLLBACK? | COMMIT saves changes permanently; ROLLBACK undoes changes |
| GRANT vs REVOKE? | GRANT gives permissions; REVOKE removes them |

---

## 💡 Pro Interview Tips

1. **Always mention WHERE clause importance**
   > "I always double-check my WHERE clause before UPDATE or DELETE to avoid affecting unintended rows."

2. **Know execution order**
   > FROM → WHERE → GROUP BY → HAVING → SELECT → ORDER BY → LIMIT

3. **Understand auto-commit**
   > "In MySQL, auto-commit is ON by default. Each statement is a transaction unless you explicitly START TRANSACTION."

4. **Security mindset**
   > "I follow the principle of least privilege - users get only the permissions they absolutely need."

5. **Practice writing queries by hand** - Interviewers often give you a whiteboard or paper!

