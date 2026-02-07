# 🗄️ Database Fundamentals - Interview Guide

> **Interviewer's Expectation**: They want to see if you understand the core building blocks. These questions filter out candidates who have only copy-pasted queries without understanding what a database actually is.

---

## 📌 What is a Database?

### Interview Answer:
A **database** is an organized collection of structured data stored electronically. It allows efficient storage, retrieval, modification, and deletion of data.

### What Interviewers Want to Hear:
- Not just "a place to store data"
- Mention **organized**, **structured**, and **efficient access**
- Bonus: Mention that databases ensure data integrity and support concurrent access

### Example:
```
Think of a database like a digital filing cabinet where:
- Each drawer = Table
- Each folder = Row/Record
- Each paper in folder = Column/Field
```

---

## 📌 What is DBMS?

### Interview Answer:
**DBMS (Database Management System)** is software that interacts with the database, applications, and users to capture and analyze data. It provides an interface to perform CRUD operations.

### Key Points to Mention:
1. **Manages data** - storage, retrieval, update
2. **Ensures security** - access control
3. **Maintains integrity** - constraints, validations
4. **Handles concurrency** - multiple users
5. **Provides backup & recovery**

### Examples of DBMS:
- MySQL, PostgreSQL, Oracle, SQL Server (RDBMS)
- MongoDB, Cassandra (NoSQL)

---

## 📌 Difference between DBMS and RDBMS

### ⭐ This is asked in almost EVERY interview!

| Feature | DBMS | RDBMS |
|---------|------|-------|
| **Data Storage** | Files (hierarchical/network) | Tables (relational) |
| **Relationships** | No relationships between data | Tables linked via foreign keys |
| **Normalization** | Not supported | Supported |
| **Data Redundancy** | High | Minimized through normalization |
| **ACID Properties** | May not support | Fully supports |
| **Examples** | File systems, XML | MySQL, PostgreSQL, Oracle |
| **Data Access** | Individual access | Multiple data accessed together |

### Key Interview Point:
> "RDBMS stores data in tables with relationships, supports ACID properties, and minimizes redundancy through normalization. Regular DBMS doesn't enforce relationships between data."

---

## 📌 Tables, Rows, and Columns

### Definitions:

**Table (Relation)**:
- A collection of related data organized in rows and columns
- Represents an entity (e.g., `employees`, `orders`, `products`)

**Row (Record/Tuple)**:
- A single entry in a table
- Represents one instance of the entity
- Example: One employee's complete information

**Column (Field/Attribute)**:
- A vertical entity containing specific information
- Has a defined data type
- Example: `employee_name`, `salary`, `hire_date`

### Visual Example:
```
┌─────────────────────────────────────────────────┐
│                  EMPLOYEES TABLE                 │
├──────┬────────────┬────────────┬────────────────┤
│  ID  │    NAME    │   SALARY   │   DEPARTMENT   │  ← Columns
├──────┼────────────┼────────────┼────────────────┤
│  1   │   John     │   50000    │      IT        │  ← Row 1
│  2   │   Jane     │   60000    │      HR        │  ← Row 2
│  3   │   Bob      │   55000    │      IT        │  ← Row 3
└──────┴────────────┴────────────┴────────────────┘
```

---

## 📌 Keys in Database

### 🔑 Primary Key

**Definition**: A column (or set of columns) that **uniquely identifies** each row in a table.

**Rules**:
1. Must be **UNIQUE** - no duplicates
2. Cannot be **NULL**
3. Only **ONE** primary key per table
4. Should be **immutable** (rarely changed)

**Interview Example**:
```sql
CREATE TABLE employees (
    employee_id INT PRIMARY KEY,  -- Primary Key
    name VARCHAR(100),
    email VARCHAR(100)
);
```

**What to Say**:
> "Primary key uniquely identifies each record. It's like an Aadhaar number for a person - unique and mandatory."

---

### 🔑 Foreign Key

**Definition**: A column that creates a **relationship** between two tables by referencing the primary key of another table.

**Purpose**:
- Maintains **referential integrity**
- Prevents orphan records
- Establishes parent-child relationships

**Interview Example**:
```sql
CREATE TABLE orders (
    order_id INT PRIMARY KEY,
    customer_id INT,
    order_date DATE,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);
```

**Visual Relationship**:
```
CUSTOMERS                         ORDERS
┌──────────────┬─────────┐       ┌──────────┬─────────────┐
│ customer_id  │  name   │       │ order_id │ customer_id │
├──────────────┼─────────┤       ├──────────┼─────────────┤
│      1       │  John   │◄──────│    101   │      1      │
│      2       │  Jane   │◄──────│    102   │      2      │
└──────────────┴─────────┘       │    103   │      1      │
         ▲                       └──────────┴─────────────┘
    Primary Key                      Foreign Key
```

**Interview Question**: What happens if you try to delete a customer who has orders?
> "It will fail due to referential integrity unless you use CASCADE DELETE or SET NULL."

---

### 🔑 Candidate Key

**Definition**: A column (or set of columns) that **CAN** uniquely identify a row. A table can have multiple candidate keys.

**Key Point**: Primary key is selected FROM candidate keys.

**Example**:
```
EMPLOYEES table:
- employee_id (unique)     → Candidate Key ✓ (chosen as Primary Key)
- email (unique)           → Candidate Key ✓
- phone_number (unique)    → Candidate Key ✓
- name (not unique)        → NOT a candidate key ✗
```

**Interview Answer**:
> "Candidate keys are all columns that COULD be primary keys. We choose one as the primary key, and the rest become alternate keys."

---

### 🔑 Composite Key

**Definition**: A primary key made up of **TWO OR MORE columns** together.

**When to Use**: When no single column can uniquely identify a row.

**Example**:
```sql
-- Student enrollment: One student can enroll in many courses
-- One course can have many students
-- But one student can enroll in a specific course only ONCE

CREATE TABLE enrollments (
    student_id INT,
    course_id INT,
    enrollment_date DATE,
    PRIMARY KEY (student_id, course_id)  -- Composite Key
);
```

**Interview Scenario**:
> "In a many-to-many relationship junction table, composite keys are commonly used. Neither student_id nor course_id alone can be unique, but together they form a unique combination."

---

### 🔑 Unique Key

**Definition**: Ensures all values in a column are **different** (like primary key, but allows ONE NULL).

**Differences from Primary Key**:

| Primary Key | Unique Key |
|-------------|------------|
| Only one per table | Multiple allowed |
| No NULL allowed | One NULL allowed |
| Creates clustered index | Creates non-clustered index |
| Used for record identification | Used for data integrity |

**Example**:
```sql
CREATE TABLE employees (
    id INT PRIMARY KEY,
    email VARCHAR(100) UNIQUE,      -- Unique Key
    phone VARCHAR(15) UNIQUE,       -- Another Unique Key
    name VARCHAR(100)               -- Not unique
);
```

---

## 📌 What is NULL?

### Definition:
NULL represents **absence of value** or **unknown data**. It is NOT:
- Zero (0)
- Empty string ('')
- Space (' ')

### ⚠️ Common Interview Trap:

**Question**: What is the result of `NULL = NULL`?

**Answer**: **Unknown (FALSE in most comparisons)**

```sql
-- This will NOT return rows where name is NULL
SELECT * FROM employees WHERE name = NULL;  -- WRONG!

-- Correct way
SELECT * FROM employees WHERE name IS NULL;  -- RIGHT!
```

### NULL Behavior in Operations:
```sql
-- Any arithmetic with NULL = NULL
SELECT 5 + NULL;      -- Returns NULL
SELECT NULL * 100;    -- Returns NULL

-- String concatenation with NULL
SELECT 'Hello' || NULL;  -- Returns NULL (in most DBs)

-- Aggregations ignore NULL
SELECT AVG(salary) FROM employees;  -- NULLs are excluded
SELECT COUNT(salary) FROM employees; -- Counts non-NULL only
SELECT COUNT(*) FROM employees;      -- Counts all rows including NULL
```

### Interview Gold:
> "NULL represents unknown or missing data. You cannot compare NULL using = or !=, you must use IS NULL or IS NOT NULL. Also, any arithmetic operation with NULL returns NULL."

---

## 📌 What is Schema?

### Definition:
A **schema** is the **logical structure** or blueprint of a database. It defines:
- Tables
- Columns and their data types
- Relationships
- Constraints
- Views, procedures, etc.

### Types of Schema:
1. **Physical Schema**: How data is stored physically (files, indexes)
2. **Logical Schema**: Structure of data (tables, relationships)
3. **View Schema**: How users see data (subsets, views)

### Example:
```sql
-- Creating a schema
CREATE SCHEMA ecommerce;

-- Creating table within schema
CREATE TABLE ecommerce.products (
    id INT PRIMARY KEY,
    name VARCHAR(100)
);
```

### Interview Point:
> "Schema is like an architectural blueprint of your database. It defines what data we store and how it's organized, but not the actual data itself."

---

## 📌 What is Metadata?

### Definition:
**Metadata** is "data about data" - information that describes the structure and properties of the actual data.

### Examples of Metadata:
- Table names
- Column names and data types
- Constraints (PK, FK, UNIQUE)
- Index information
- Row count statistics
- Creation date of tables

### How to Access (MySQL):
```sql
-- View all tables
SHOW TABLES;

-- View table structure
DESCRIBE employees;
DESC employees;

-- View from information schema
SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'employees';
```

---

## 📌 What is a Constraint?

### Definition:
**Constraints** are rules enforced on data columns to maintain **accuracy and integrity** of data.

### Types of Constraints:

| Constraint | Purpose | Example |
|------------|---------|---------|
| **NOT NULL** | Column cannot have NULL | `name VARCHAR(100) NOT NULL` |
| **UNIQUE** | All values must be different | `email VARCHAR(100) UNIQUE` |
| **PRIMARY KEY** | Unique identifier (NOT NULL + UNIQUE) | `id INT PRIMARY KEY` |
| **FOREIGN KEY** | Links to another table | `REFERENCES customers(id)` |
| **CHECK** | Validates against a condition | `CHECK (age >= 18)` |
| **DEFAULT** | Sets default value | `status VARCHAR(20) DEFAULT 'active'` |

### Complete Example:
```sql
CREATE TABLE employees (
    id INT PRIMARY KEY,                          -- PRIMARY KEY
    name VARCHAR(100) NOT NULL,                  -- NOT NULL
    email VARCHAR(100) UNIQUE,                   -- UNIQUE
    department_id INT,
    salary DECIMAL(10,2) CHECK (salary > 0),    -- CHECK
    status VARCHAR(20) DEFAULT 'active',         -- DEFAULT
    FOREIGN KEY (department_id) 
        REFERENCES departments(id)               -- FOREIGN KEY
);
```

---
