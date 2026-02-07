# 🔗 SQL Queries, JOINs & Subqueries - Interview Guide

> **Interviewer's Expectation**: This is where they separate talkers from doers. Can you WRITE queries? Do you understand JOINs deeply? This section has the MOST practical questions.

---

## 📌 Sample Tables for Examples

Throughout this guide, we'll use these tables:

```sql
-- EMPLOYEES table
CREATE TABLE employees (
    emp_id INT PRIMARY KEY,
    name VARCHAR(100),
    salary DECIMAL(10,2),
    dept_id INT,
    manager_id INT,
    hire_date DATE
);

-- Sample data
INSERT INTO employees VALUES
(1, 'John', 50000, 1, NULL, '2020-01-15'),
(2, 'Jane', 60000, 1, 1, '2020-06-20'),
(3, 'Bob', 55000, 2, 1, '2021-03-10'),
(4, 'Alice', 70000, 2, 3, '2019-11-05'),
(5, 'Charlie', 45000, 3, 1, '2022-02-28'),
(6, 'David', 52000, NULL, 2, '2021-08-15');  -- No department

-- DEPARTMENTS table
CREATE TABLE departments (
    dept_id INT PRIMARY KEY,
    dept_name VARCHAR(100),
    location VARCHAR(100)
);

INSERT INTO departments VALUES
(1, 'Engineering', 'New York'),
(2, 'Marketing', 'Los Angeles'),
(3, 'Sales', 'Chicago'),
(4, 'HR', 'Boston');  -- No employees
```

---

# 🔷 BASIC QUERIES (Warm-up Questions)

## 📌 Find all employees with salary > 50,000

```sql
SELECT * FROM employees WHERE salary > 50000;

-- Result:
-- Jane   - 60000
-- Bob    - 55000
-- Alice  - 70000
-- David  - 52000
```

**Interview Follow-up**: "What if I want >= 50000?"
```sql
SELECT * FROM employees WHERE salary >= 50000;
-- Now includes John (50000)
```

---

## 📌 Get distinct values from a column

```sql
-- Distinct departments
SELECT DISTINCT dept_id FROM employees;

-- Distinct with NULL handling
SELECT DISTINCT dept_id FROM employees WHERE dept_id IS NOT NULL;

-- Count distinct values
SELECT COUNT(DISTINCT dept_id) AS unique_departments FROM employees;
```

**Interview Trap**: "What about NULL values?"
> "DISTINCT includes NULL as a distinct value. COUNT(DISTINCT column) excludes NULLs."

---

## 📌 Count number of rows in a table

```sql
-- Count all rows (includes NULL)
SELECT COUNT(*) FROM employees;  -- 6

-- Count non-NULL values in a column
SELECT COUNT(dept_id) FROM employees;  -- 5 (David has NULL)

-- Count with condition
SELECT COUNT(*) FROM employees WHERE salary > 50000;  -- 4
```

---

# 🔷 JOINs (EXTREMELY IMPORTANT!)

> **Interview Reality**: You WILL be asked to write JOIN queries. Know these cold!

## 📌 What is a JOIN?

A **JOIN** combines rows from two or more tables based on a related column.

### Visual Representation:
```
EMPLOYEES                    DEPARTMENTS
┌────┬───────┬─────────┐     ┌─────────┬─────────────┐
│ id │ name  │ dept_id │     │ dept_id │  dept_name  │
├────┼───────┼─────────┤     ├─────────┼─────────────┤
│ 1  │ John  │    1    │────▶│    1    │ Engineering │
│ 2  │ Jane  │    1    │────▶│    1    │ Engineering │
│ 3  │ Bob   │    2    │────▶│    2    │ Marketing   │
│ 4  │ Alice │    2    │────▶│    2    │ Marketing   │
│ 5  │Charlie│    3    │────▶│    3    │ Sales       │
│ 6  │ David │  NULL   │     │    4    │ HR          │◀── No match
└────┴───────┴─────────┘     └─────────┴─────────────┘
```

---

## 📌 INNER JOIN

Returns only **matching rows** from both tables.

```sql
SELECT e.name, e.salary, d.dept_name
FROM employees e
INNER JOIN departments d ON e.dept_id = d.dept_id;
```

**Result**:
```
┌─────────┬────────┬─────────────┐
│  name   │ salary │  dept_name  │
├─────────┼────────┼─────────────┤
│ John    │ 50000  │ Engineering │
│ Jane    │ 60000  │ Engineering │
│ Bob     │ 55000  │ Marketing   │
│ Alice   │ 70000  │ Marketing   │
│ Charlie │ 45000  │ Sales       │
└─────────┴────────┴─────────────┘
```

**Notice**: David (NULL dept_id) and HR department (no employees) are NOT included!

### Visual:
```
     INNER JOIN
    ┌───────────┐
    │    ███    │
    │   █████   │
    │    ███    │
    └───────────┘
  Only the overlap
```

---

## 📌 LEFT JOIN (LEFT OUTER JOIN)

Returns **all rows from left table** + matching rows from right table.

```sql
SELECT e.name, e.salary, d.dept_name
FROM employees e
LEFT JOIN departments d ON e.dept_id = d.dept_id;
```

**Result**:
```
┌─────────┬────────┬─────────────┐
│  name   │ salary │  dept_name  │
├─────────┼────────┼─────────────┤
│ John    │ 50000  │ Engineering │
│ Jane    │ 60000  │ Engineering │
│ Bob     │ 55000  │ Marketing   │
│ Alice   │ 70000  │ Marketing   │
│ Charlie │ 45000  │ Sales       │
│ David   │ 52000  │ NULL        │◀── Included with NULL
└─────────┴────────┴─────────────┘
```

**Notice**: David is included even though he has no department (NULL for dept_name)!

### Visual:
```
     LEFT JOIN
    ┌───────────┐
    │ ████      │
    │ ██████    │
    │ ████      │
    └───────────┘
  All left + match
```

---

## 📌 RIGHT JOIN (RIGHT OUTER JOIN)

Returns all rows from **right table** + matching rows from left table.

```sql
SELECT e.name, d.dept_name
FROM employees e
RIGHT JOIN departments d ON e.dept_id = d.dept_id;
```

**Result**:
```
┌─────────┬─────────────┐
│  name   │  dept_name  │
├─────────┼─────────────┤
│ John    │ Engineering │
│ Jane    │ Engineering │
│ Bob     │ Marketing   │
│ Alice   │ Marketing   │
│ Charlie │ Sales       │
│ NULL    │ HR          │◀── HR included with NULL employee
└─────────┴─────────────┘
```

**Notice**: HR department included even though it has no employees!

### Visual:
```
    RIGHT JOIN
    ┌───────────┐
    │      ████ │
    │    ██████ │
    │      ████ │
    └───────────┘
  All right + match
```

---

## 📌 FULL OUTER JOIN

Returns **all rows** from both tables. (Not supported in MySQL - use UNION)

```sql
-- PostgreSQL, SQL Server
SELECT e.name, d.dept_name
FROM employees e
FULL OUTER JOIN departments d ON e.dept_id = d.dept_id;

-- MySQL (simulate with UNION)
SELECT e.name, d.dept_name
FROM employees e
LEFT JOIN departments d ON e.dept_id = d.dept_id
UNION
SELECT e.name, d.dept_name
FROM employees e
RIGHT JOIN departments d ON e.dept_id = d.dept_id;
```

**Result**:
```
┌─────────┬─────────────┐
│  name   │  dept_name  │
├─────────┼─────────────┤
│ John    │ Engineering │
│ Jane    │ Engineering │
│ Bob     │ Marketing   │
│ Alice   │ Marketing   │
│ Charlie │ Sales       │
│ David   │ NULL        │◀── Employee without dept
│ NULL    │ HR          │◀── Dept without employees
└─────────┴─────────────┘
```

### Visual:
```
   FULL OUTER JOIN
    ┌───────────┐
    │ █████████ │
    │ █████████ │
    │ █████████ │
    └───────────┘
   Everything!
```

---

## 📌 CROSS JOIN (Cartesian Product)

Returns **every combination** of rows from both tables.

```sql
SELECT e.name, d.dept_name
FROM employees e
CROSS JOIN departments d;

-- Equivalent to:
SELECT e.name, d.dept_name
FROM employees e, departments d;
```

**Result**: 6 employees × 4 departments = 24 rows!

```
John - Engineering
John - Marketing
John - Sales
John - HR
Jane - Engineering
... (24 total combinations)
```

**Interview Question**: "When would you use CROSS JOIN?"
> "For generating all possible combinations - like creating a schedule grid of all employees with all time slots, or generating test data."

---

## 📌 SELF JOIN

Joining a table with **itself**.

```sql
-- Find employees and their managers
SELECT 
    e.name AS employee,
    m.name AS manager
FROM employees e
LEFT JOIN employees m ON e.manager_id = m.emp_id;
```

**Result**:
```
┌──────────┬─────────┐
│ employee │ manager │
├──────────┼─────────┤
│ John     │ NULL    │  -- No manager (top level)
│ Jane     │ John    │
│ Bob      │ John    │
│ Alice    │ Bob     │
│ Charlie  │ John    │
│ David    │ Jane    │
└──────────┴─────────┘
```

### ⭐ Classic Interview Question: "Find employees who earn more than their manager"

```sql
SELECT e.name AS employee, e.salary AS emp_salary,
       m.name AS manager, m.salary AS mgr_salary
FROM employees e
JOIN employees m ON e.manager_id = m.emp_id
WHERE e.salary > m.salary;
```

---

## 📌 Difference: INNER JOIN vs LEFT JOIN

### ⭐ Probably the #1 most asked JOIN question!

| INNER JOIN | LEFT JOIN |
|------------|-----------|
| Only matching rows | All from left + matching from right |
| No NULLs from join | NULLs for non-matching right |
| Use when you need ONLY matched data | Use when you need ALL from primary table |
| Generally faster | Slightly slower due to NULL handling |

### When to use what:

**INNER JOIN** - "Give me employees WITH departments only"
```sql
SELECT e.name, d.dept_name
FROM employees e
INNER JOIN departments d ON e.dept_id = d.dept_id;
-- David excluded (no department)
```

**LEFT JOIN** - "Give me ALL employees, with department if available"
```sql
SELECT e.name, d.dept_name
FROM employees e
LEFT JOIN departments d ON e.dept_id = d.dept_id;
-- David included (NULL for department)
```

---

## 📌 Multiple JOINs

```sql
-- Employees with department and manager name
SELECT 
    e.name AS employee,
    d.dept_name AS department,
    m.name AS manager
FROM employees e
LEFT JOIN departments d ON e.dept_id = d.dept_id
LEFT JOIN employees m ON e.manager_id = m.emp_id;
```

---

# 🔷 PRACTICAL INTERVIEW QUERIES

## 📌 Find Second Highest Salary

### Multiple Approaches (Know at least 2!)

**Approach 1: Using LIMIT OFFSET**
```sql
SELECT DISTINCT salary 
FROM employees 
ORDER BY salary DESC 
LIMIT 1 OFFSET 1;
```

**Approach 2: Using Subquery**
```sql
SELECT MAX(salary) 
FROM employees 
WHERE salary < (SELECT MAX(salary) FROM employees);
```

**Approach 3: Using NOT IN**
```sql
SELECT MAX(salary) 
FROM employees 
WHERE salary NOT IN (SELECT MAX(salary) FROM employees);
```

**Approach 4: Using DENSE_RANK() (Modern)**
```sql
SELECT salary FROM (
    SELECT salary, DENSE_RANK() OVER (ORDER BY salary DESC) AS rnk
    FROM employees
) ranked
WHERE rnk = 2;
```

---

## 📌 Find Nth Highest Salary (Generalized)

```sql
-- Find 3rd highest salary
SELECT DISTINCT salary 
FROM employees 
ORDER BY salary DESC 
LIMIT 1 OFFSET 2;  -- N-1

-- Using window function
SELECT salary FROM (
    SELECT salary, DENSE_RANK() OVER (ORDER BY salary DESC) AS rnk
    FROM employees
) ranked
WHERE rnk = 3;  -- N
```

---

## 📌 Find Duplicate Records

```sql
-- Find duplicate names
SELECT name, COUNT(*) as count
FROM employees
GROUP BY name
HAVING COUNT(*) > 1;

-- Find all duplicate records with details
SELECT * FROM employees
WHERE name IN (
    SELECT name FROM employees
    GROUP BY name
    HAVING COUNT(*) > 1
);
```

---

## 📌 Delete Duplicate Records (Keep One)

```sql
-- Keep the one with lowest ID
DELETE e1 FROM employees e1
INNER JOIN employees e2 
ON e1.name = e2.name AND e1.emp_id > e2.emp_id;

-- Using ROW_NUMBER (Modern approach)
DELETE FROM employees
WHERE emp_id NOT IN (
    SELECT MIN(emp_id)
    FROM employees
    GROUP BY name
);
```

---

## 📌 Find Employees with No Manager

```sql
SELECT * FROM employees WHERE manager_id IS NULL;
```

---

## 📌 Find Departments with No Employees

```sql
-- Using LEFT JOIN
SELECT d.*
FROM departments d
LEFT JOIN employees e ON d.dept_id = e.dept_id
WHERE e.emp_id IS NULL;

-- Using NOT EXISTS
SELECT * FROM departments d
WHERE NOT EXISTS (
    SELECT 1 FROM employees e WHERE e.dept_id = d.dept_id
);

-- Using NOT IN
SELECT * FROM departments
WHERE dept_id NOT IN (
    SELECT DISTINCT dept_id FROM employees WHERE dept_id IS NOT NULL
);
```

---

## 📌 Find Top N Salaries per Department

```sql
-- Top 2 salaries per department using window function
SELECT * FROM (
    SELECT 
        e.*,
        d.dept_name,
        DENSE_RANK() OVER (PARTITION BY e.dept_id ORDER BY e.salary DESC) AS rnk
    FROM employees e
    JOIN departments d ON e.dept_id = d.dept_id
) ranked
WHERE rnk <= 2;
```

---

## 📌 Running Total / Cumulative Sum

```sql
SELECT 
    name,
    salary,
    SUM(salary) OVER (ORDER BY emp_id) AS running_total
FROM employees;
```
