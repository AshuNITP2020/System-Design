# 🪟 SQL Window Functions - Complete Guide

> **Window functions perform calculations across a set of rows related to the current row, WITHOUT collapsing rows like GROUP BY.**

---

## 📌 What is a Window Function?

A **window function** operates on a "window" (subset) of rows and returns a value for each row. Unlike `GROUP BY`, it doesn't reduce the number of rows returned.

### Basic Syntax

```sql
function_name() OVER (
    [PARTITION BY column1, column2, ...]    -- Divides rows into groups
    [ORDER BY column1 [ASC|DESC], ...]       -- Orders rows within partition
    [frame_clause]                           -- Defines row range
)
```

### Visual Representation

```
Without Window Function:        With Window Function:
┌────────────────────┐         ┌────────────────────────────┐
│ name  │ salary     │         │ name  │ salary │ dept_avg  │
├───────┼────────────┤         ├───────┼────────┼───────────┤
│ John  │ 50000      │         │ John  │ 50000  │ 55000     │
│ Jane  │ 60000      │         │ Jane  │ 60000  │ 55000     │
│ Bob   │ 55000      │         │ Bob   │ 55000  │ 62500     │
│ Alice │ 70000      │         │ Alice │ 70000  │ 62500     │
└───────┴────────────┘         └───────┴────────┴───────────┘
                               Each row keeps its data + window calculation
```

---

## 📌 Sample Table for Examples

```sql
-- EMPLOYEES table
CREATE TABLE employees (
    emp_id INT PRIMARY KEY,
    name VARCHAR(100),
    salary DECIMAL(10,2),
    dept_id INT,
    hire_date DATE
);

INSERT INTO employees VALUES
(1, 'John', 50000, 1, '2020-01-15'),
(2, 'Jane', 60000, 1, '2020-06-20'),
(3, 'Bob', 55000, 2, '2021-03-10'),
(4, 'Alice', 70000, 2, '2019-11-05'),
(5, 'Charlie', 45000, 3, '2022-02-28'),
(6, 'David', 52000, 3, '2021-08-15'),
(7, 'Eve', 48000, 1, '2023-01-10');
```

---

# 🔷 Types of Window Functions

## 1️⃣ Ranking Functions

### ROW_NUMBER()

Assigns a **unique sequential number** to each row. No ties - always 1, 2, 3...

```sql
SELECT 
    name, 
    salary,
    ROW_NUMBER() OVER (ORDER BY salary DESC) AS row_num
FROM employees;
```

**Result:**
```
┌─────────┬────────┬─────────┐
│  name   │ salary │ row_num │
├─────────┼────────┼─────────┤
│ Alice   │ 70000  │    1    │
│ Jane    │ 60000  │    2    │
│ Bob     │ 55000  │    3    │
│ David   │ 52000  │    4    │
│ John    │ 50000  │    5    │
│ Eve     │ 48000  │    6    │
│ Charlie │ 45000  │    7    │
└─────────┴────────┴─────────┘
```

---

### RANK()

Assigns rank with **gaps for ties**.

```sql
-- Let's say John and David both have 50000 salary
SELECT 
    name, 
    salary,
    RANK() OVER (ORDER BY salary DESC) AS rank
FROM employees;
```

**With ties:**
```
┌─────────┬────────┬──────┐
│  name   │ salary │ rank │
├─────────┼────────┼──────┤
│ Alice   │ 70000  │  1   │
│ Jane    │ 60000  │  2   │
│ Bob     │ 55000  │  3   │
│ John    │ 50000  │  4   │  ← Same salary
│ David   │ 50000  │  4   │  ← Same rank
│ Eve     │ 48000  │  6   │  ← Skips 5!
│ Charlie │ 45000  │  7   │
└─────────┴────────┴──────┘
```

---

### DENSE_RANK()

Assigns rank **without gaps** for ties.

```sql
SELECT 
    name, 
    salary,
    DENSE_RANK() OVER (ORDER BY salary DESC) AS dense_rank
FROM employees;
```

**With ties:**
```
┌─────────┬────────┬────────────┐
│  name   │ salary │ dense_rank │
├─────────┼────────┼────────────┤
│ Alice   │ 70000  │     1      │
│ Jane    │ 60000  │     2      │
│ Bob     │ 55000  │     3      │
│ John    │ 50000  │     4      │  ← Same salary
│ David   │ 50000  │     4      │  ← Same rank
│ Eve     │ 48000  │     5      │  ← No gap!
│ Charlie │ 45000  │     6      │
└─────────┴────────┴────────────┘
```

### Comparison: ROW_NUMBER vs RANK vs DENSE_RANK

```sql
SELECT 
    name, 
    salary,
    ROW_NUMBER() OVER (ORDER BY salary DESC) AS row_num,
    RANK() OVER (ORDER BY salary DESC) AS rank,
    DENSE_RANK() OVER (ORDER BY salary DESC) AS dense_rank
FROM employees;
```

```
┌─────────┬────────┬─────────┬──────┬────────────┐
│  name   │ salary │ row_num │ rank │ dense_rank │
├─────────┼────────┼─────────┼──────┼────────────┤
│ Alice   │ 70000  │    1    │  1   │     1      │
│ Jane    │ 60000  │    2    │  2   │     2      │
│ John    │ 50000  │    3    │  3   │     3      │
│ David   │ 50000  │    4    │  3   │     3      │  ← Key difference
│ Eve     │ 48000  │    5    │  5   │     4      │  ← RANK skips, DENSE doesn't
│ Charlie │ 45000  │    6    │  6   │     5      │
└─────────┴────────┴─────────┴──────┴────────────┘
```

---

### NTILE(n)

Divides rows into **n equal groups** (buckets).

```sql
SELECT 
    name, 
    salary,
    NTILE(3) OVER (ORDER BY salary DESC) AS salary_tier
FROM employees;
```

**Result:**
```
┌─────────┬────────┬─────────────┐
│  name   │ salary │ salary_tier │
├─────────┼────────┼─────────────┤
│ Alice   │ 70000  │      1      │  ← Top tier
│ Jane    │ 60000  │      1      │  ← Top tier
│ Bob     │ 55000  │      1      │  ← Top tier
│ David   │ 52000  │      2      │  ← Middle tier
│ John    │ 50000  │      2      │  ← Middle tier
│ Eve     │ 48000  │      3      │  ← Bottom tier
│ Charlie │ 45000  │      3      │  ← Bottom tier
└─────────┴────────┴─────────────┘
```

**Use Case:** Creating quartiles, percentiles, salary bands

---

### PERCENT_RANK()

Returns the **relative rank as a percentage** (0 to 1).

```sql
SELECT 
    name, 
    salary,
    PERCENT_RANK() OVER (ORDER BY salary DESC) AS percent_rank
FROM employees;
```

**Formula:** `(rank - 1) / (total_rows - 1)`

---

## 2️⃣ Aggregate Window Functions

### SUM() OVER

```sql
-- Running total (cumulative sum)
SELECT 
    name,
    salary,
    SUM(salary) OVER (ORDER BY emp_id) AS running_total
FROM employees;
```

**Result:**
```
┌─────────┬────────┬───────────────┐
│  name   │ salary │ running_total │
├─────────┼────────┼───────────────┤
│ John    │ 50000  │    50000      │
│ Jane    │ 60000  │   110000      │
│ Bob     │ 55000  │   165000      │
│ Alice   │ 70000  │   235000      │
│ Charlie │ 45000  │   280000      │
│ David   │ 52000  │   332000      │
│ Eve     │ 48000  │   380000      │
└─────────┴────────┴───────────────┘
```

---

### SUM() with PARTITION BY

```sql
-- Total salary per department (repeated for each row)
SELECT 
    name,
    dept_id,
    salary,
    SUM(salary) OVER (PARTITION BY dept_id) AS dept_total
FROM employees;
```

**Result:**
```
┌─────────┬─────────┬────────┬────────────┐
│  name   │ dept_id │ salary │ dept_total │
├─────────┼─────────┼────────┼────────────┤
│ John    │    1    │ 50000  │   158000   │
│ Jane    │    1    │ 60000  │   158000   │
│ Eve     │    1    │ 48000  │   158000   │
│ Bob     │    2    │ 55000  │   125000   │
│ Alice   │    2    │ 70000  │   125000   │
│ Charlie │    3    │ 45000  │    97000   │
│ David   │    3    │ 52000  │    97000   │
└─────────┴─────────┴────────┴────────────┘
```

---

### AVG() OVER

```sql
-- Average salary per department
SELECT 
    name,
    dept_id,
    salary,
    ROUND(AVG(salary) OVER (PARTITION BY dept_id), 2) AS dept_avg
FROM employees;
```

---

### COUNT() OVER

```sql
-- Count employees per department
SELECT 
    name,
    dept_id,
    COUNT(*) OVER (PARTITION BY dept_id) AS dept_count
FROM employees;
```

---

### MAX() / MIN() OVER

```sql
-- Highest salary in each department
SELECT 
    name,
    dept_id,
    salary,
    MAX(salary) OVER (PARTITION BY dept_id) AS dept_max,
    MIN(salary) OVER (PARTITION BY dept_id) AS dept_min
FROM employees;
```

---

## 3️⃣ Value Functions

### LAG() - Access Previous Row

```sql
SELECT 
    name,
    salary,
    LAG(salary, 1) OVER (ORDER BY emp_id) AS prev_salary,
    LAG(salary, 2) OVER (ORDER BY emp_id) AS two_rows_back
FROM employees;
```

**Result:**
```
┌─────────┬────────┬─────────────┬───────────────┐
│  name   │ salary │ prev_salary │ two_rows_back │
├─────────┼────────┼─────────────┼───────────────┤
│ John    │ 50000  │    NULL     │     NULL      │
│ Jane    │ 60000  │   50000     │     NULL      │
│ Bob     │ 55000  │   60000     │    50000      │
│ Alice   │ 70000  │   55000     │    60000      │
│ Charlie │ 45000  │   70000     │    55000      │
│ David   │ 52000  │   45000     │    70000      │
│ Eve     │ 48000  │   52000     │    45000      │
└─────────┴────────┴─────────────┴───────────────┘
```

**Syntax:** `LAG(column, offset, default_value)`

---

### LEAD() - Access Next Row

```sql
SELECT 
    name,
    salary,
    LEAD(salary, 1) OVER (ORDER BY emp_id) AS next_salary
FROM employees;
```

**Result:**
```
┌─────────┬────────┬─────────────┐
│  name   │ salary │ next_salary │
├─────────┼────────┼─────────────┤
│ John    │ 50000  │   60000     │
│ Jane    │ 60000  │   55000     │
│ Bob     │ 55000  │   70000     │
│ Alice   │ 70000  │   45000     │
│ Charlie │ 45000  │   52000     │
│ David   │ 52000  │   48000     │
│ Eve     │ 48000  │   NULL      │
└─────────┴────────┴─────────────┘
```

---

### FIRST_VALUE() / LAST_VALUE()

```sql
-- First and last salary in each department
SELECT 
    name,
    dept_id,
    salary,
    FIRST_VALUE(salary) OVER (
        PARTITION BY dept_id 
        ORDER BY salary DESC
    ) AS highest_in_dept,
    LAST_VALUE(salary) OVER (
        PARTITION BY dept_id 
        ORDER BY salary DESC
        ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING
    ) AS lowest_in_dept
FROM employees;
```

> ⚠️ **Important:** `LAST_VALUE` requires explicit frame clause to work correctly!

---

### NTH_VALUE()

```sql
-- Get the 2nd highest salary in each department
SELECT 
    name,
    dept_id,
    salary,
    NTH_VALUE(salary, 2) OVER (
        PARTITION BY dept_id 
        ORDER BY salary DESC
        ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING
    ) AS second_highest
FROM employees;
```

---

# 🔷 PARTITION BY vs GROUP BY

### Key Differences

| GROUP BY | PARTITION BY |
|----------|--------------|
| Collapses rows into groups | Keeps all rows |
| One row per group | All rows with calculation |
| Uses aggregate functions | Uses window functions |
| No access to individual rows | Full access to all columns |

### Example Comparison

```sql
-- GROUP BY: Returns 3 rows (one per department)
SELECT 
    dept_id, 
    SUM(salary) AS total_salary
FROM employees 
GROUP BY dept_id;
```

**Result:** 3 rows
```
┌─────────┬──────────────┐
│ dept_id │ total_salary │
├─────────┼──────────────┤
│    1    │    158000    │
│    2    │    125000    │
│    3    │     97000    │
└─────────┴──────────────┘
```

```sql
-- PARTITION BY: Returns ALL 7 rows with department totals
SELECT 
    name, 
    salary, 
    dept_id,
    SUM(salary) OVER (PARTITION BY dept_id) AS dept_total
FROM employees;
```

**Result:** 7 rows
```
┌─────────┬────────┬─────────┬────────────┐
│  name   │ salary │ dept_id │ dept_total │
├─────────┼────────┼─────────┼────────────┤
│ John    │ 50000  │    1    │   158000   │
│ Jane    │ 60000  │    1    │   158000   │
│ Eve     │ 48000  │    1    │   158000   │
│ Bob     │ 55000  │    2    │   125000   │
│ Alice   │ 70000  │    2    │   125000   │
│ Charlie │ 45000  │    3    │    97000   │
│ David   │ 52000  │    3    │    97000   │
└─────────┴────────┴─────────┴────────────┘
```

---

# 🔷 Common Interview Questions

## 1. Find Second Highest Salary

```sql
-- Using DENSE_RANK (handles duplicates correctly)
SELECT salary FROM (
    SELECT 
        salary,
        DENSE_RANK() OVER (ORDER BY salary DESC) AS rnk
    FROM employees
) ranked
WHERE rnk = 2;
```

---

## 2. Find Nth Highest Salary

```sql
-- Generalized for any N (e.g., 3rd highest)
SELECT salary FROM (
    SELECT 
        salary,
        DENSE_RANK() OVER (ORDER BY salary DESC) AS rnk
    FROM employees
) ranked
WHERE rnk = 3;  -- Change N here
```

---

## 3. Top N Salaries per Department

```sql
-- Top 2 earners in each department
SELECT * FROM (
    SELECT 
        name,
        dept_id,
        salary,
        ROW_NUMBER() OVER (
            PARTITION BY dept_id 
            ORDER BY salary DESC
        ) AS rn
    FROM employees
) ranked
WHERE rn <= 2;
```

---

## 4. Running Total / Cumulative Sum

```sql
SELECT 
    name,
    salary,
    SUM(salary) OVER (ORDER BY emp_id) AS running_total
FROM employees;
```

---

## 5. Percentage of Total

```sql
SELECT 
    name,
    salary,
    ROUND(salary * 100.0 / SUM(salary) OVER (), 2) AS percent_of_total
FROM employees;
```

---

## 6. Salary Difference from Previous Employee

```sql
SELECT 
    name,
    salary,
    salary - LAG(salary) OVER (ORDER BY emp_id) AS diff_from_prev
FROM employees;
```

---

## 7. Find Employees Earning More Than Department Average

```sql
SELECT * FROM (
    SELECT 
        name,
        dept_id,
        salary,
        AVG(salary) OVER (PARTITION BY dept_id) AS dept_avg
    FROM employees
) sub
WHERE salary > dept_avg;
```

---

## 8. Moving Average (3-day)

```sql
SELECT 
    name,
    salary,
    ROUND(AVG(salary) OVER (
        ORDER BY emp_id 
        ROWS BETWEEN 2 PRECEDING AND CURRENT ROW
    ), 2) AS moving_avg_3
FROM employees;
```

---

## 9. Year-over-Year Comparison

```sql
SELECT 
    name,
    YEAR(hire_date) AS year,
    salary,
    LAG(salary) OVER (ORDER BY hire_date) AS prev_salary,
    salary - LAG(salary) OVER (ORDER BY hire_date) AS yoy_change
FROM employees;
```

---

## 10. Rank Reset by Category (Partition)

```sql
-- Rank employees within each department
SELECT 
    name,
    dept_id,
    salary,
    RANK() OVER (PARTITION BY dept_id ORDER BY salary DESC) AS dept_rank
FROM employees;
```

---

# 🔷 Named Windows (Reusable Window Definitions)

```sql
SELECT 
    name,
    salary,
    ROW_NUMBER() OVER w AS row_num,
    RANK() OVER w AS rank,
    DENSE_RANK() OVER w AS dense_rank
FROM employees
WINDOW w AS (ORDER BY salary DESC);
```

---

# 🔷 Performance Considerations

1. **Indexes**: Create indexes on columns used in `PARTITION BY` and `ORDER BY`
2. **Partitioning**: Large `PARTITION BY` operations can be memory-intensive
3. **Frame Size**: Large frames (UNBOUNDED) are slower than bounded frames
4. **Multiple Windows**: Define named windows to avoid recalculation

---

# 🔷 Quick Reference Cheat Sheet

```
┌──────────────────────────────────────────────────────────────────┐
│                    WINDOW FUNCTION TYPES                          │
├──────────────────────────────────────────────────────────────────┤
│                                                                   │
│  RANKING:           AGGREGATE:          VALUE:                    │
│  ─────────          ──────────          ──────                    │
│  ROW_NUMBER()       SUM()               LAG()                     │
│  RANK()             AVG()               LEAD()                    │
│  DENSE_RANK()       COUNT()             FIRST_VALUE()             │
│  NTILE()            MAX()               LAST_VALUE()              │
│  PERCENT_RANK()     MIN()               NTH_VALUE()               │
│                                                                   │
├──────────────────────────────────────────────────────────────────┤
│                    BASIC SYNTAX                                   │
├──────────────────────────────────────────────────────────────────┤
│                                                                   │
│  function() OVER (                                                │
│      PARTITION BY col1    -- Group rows                           │
│      ORDER BY col2        -- Sort within group                    │
│      ROWS BETWEEN         -- Define frame                         │
│  )                                                                │
│                                                                   │
├──────────────────────────────────────────────────────────────────┤
│                    FRAME CLAUSES                                  │
├──────────────────────────────────────────────────────────────────┤
│                                                                   │
│  UNBOUNDED PRECEDING  ←  All rows before                          │
│  n PRECEDING          ←  n rows before                            │
│  CURRENT ROW          ←  Current row                              │
│  n FOLLOWING          →  n rows after                             │
│  UNBOUNDED FOLLOWING  →  All rows after                           │
│                                                                   │
└──────────────────────────────────────────────────────────────────┘
```

---

# 🔷 Common Mistakes to Avoid

1. **Forgetting ORDER BY** - Most ranking functions need ORDER BY
2. **LAST_VALUE without frame** - Default frame excludes following rows
3. **Using RANK when ROW_NUMBER needed** - Ties can cause issues
4. **Not handling NULLs** - LAG/LEAD return NULL at boundaries
5. **Confusing ROWS vs RANGE** - ROWS = physical, RANGE = logical

---

Happy Learning! 🚀

