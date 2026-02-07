# 📊 Normalization - Interview Guide

> **Interviewer's Expectation**: This is a FAVORITE interview topic! They want to see if you understand database design principles. Be ready to normalize a given table to 3NF on a whiteboard.

---

## 📌 What is Normalization?

**Normalization** is the process of organizing data in a database to:
1. **Reduce data redundancy** (duplicate data)
2. **Eliminate data anomalies** (insert, update, delete problems)
3. **Ensure data integrity**
4. **Optimize storage**

### Interview Answer:
> "Normalization is a systematic approach to decomposing tables to eliminate data redundancy and undesirable characteristics like insertion, update, and deletion anomalies. It involves dividing large tables into smaller, related tables and defining relationships between them."

---

## 📌 Why Do We Need Normalization?

### Data Anomalies (Problems with Unnormalized Data)

Consider this **unnormalized** table:

```
┌─────────┬────────┬──────────────┬─────────┬────────────┬───────────┐
│ emp_id  │  name  │    email     │ dept_id │ dept_name  │ dept_head │
├─────────┼────────┼──────────────┼─────────┼────────────┼───────────┤
│    1    │  John  │ john@co.com  │    10   │    IT      │   Alice   │
│    2    │  Jane  │ jane@co.com  │    10   │    IT      │   Alice   │
│    3    │  Bob   │ bob@co.com   │    20   │    HR      │   Charlie │
│    4    │  Mike  │ mike@co.com  │    10   │    IT      │   Alice   │
└─────────┴────────┴──────────────┴─────────┴────────────┴───────────┘
```

### 1. Insertion Anomaly
**Problem**: Cannot add a new department without adding an employee.

```
❌ Want to add "Finance" department with no employees yet?
   Can't do it! emp_id is required (primary key).
```

### 2. Update Anomaly
**Problem**: Must update multiple rows for a single change.

```
❌ Alice (IT dept head) gets promoted to CTO.
   Must update 3 rows (John, Jane, Mike) — easy to miss one!
   Result: Inconsistent data
```

### 3. Deletion Anomaly
**Problem**: Deleting data causes unintended loss of other data.

```
❌ Bob leaves the company, we delete his record.
   Oops! We just lost all information about HR department!
```

### Solution: Normalize!

```
EMPLOYEES                    DEPARTMENTS
┌─────────┬──────┬─────────┐ ┌─────────┬───────────┬───────────┐
│ emp_id  │ name │ dept_id │ │ dept_id │ dept_name │ dept_head │
├─────────┼──────┼─────────┤ ├─────────┼───────────┼───────────┤
│    1    │ John │    10   │ │   10    │    IT     │   Alice   │
│    2    │ Jane │    10   │ │   20    │    HR     │   Charlie │
│    3    │ Bob  │    20   │ │   30    │  Finance  │   David   │ ← Can add!
│    4    │ Mike │    10   │ └─────────┴───────────┴───────────┘
└─────────┴──────┴─────────┘
```

---

## 📌 What is Denormalization?

**Denormalization** is the **intentional** introduction of redundancy to improve **read performance**.

### When to Denormalize:
1. **Read-heavy applications** (reporting, analytics)
2. **When JOINs are too expensive**
3. **Data warehouse/OLAP systems**
4. **Caching scenarios**

### Example:
```sql
-- Normalized: Requires JOIN for every read
SELECT e.name, d.dept_name 
FROM employees e 
JOIN departments d ON e.dept_id = d.dept_id;

-- Denormalized: Redundant but faster reads
SELECT name, dept_name FROM employees;  -- dept_name stored in employees
```

### Interview Answer:
> "Denormalization is a conscious trade-off. We accept some redundancy and update complexity in exchange for faster read performance. It's commonly used in read-heavy systems like data warehouses."

---

## 📌 Normal Forms Overview

```
┌────────────────────────────────────────────────────────────────┐
│                    NORMAL FORMS HIERARCHY                       │
├────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Unnormalized (UNF)                                            │
│        ↓                                                        │
│     1NF  ─── Remove repeating groups, atomic values            │
│        ↓                                                        │
│     2NF  ─── Remove partial dependencies                       │
│        ↓                                                        │
│     3NF  ─── Remove transitive dependencies                    │
│        ↓                                                        │
│    BCNF  ─── Every determinant is a candidate key              │
│        ↓                                                        │
│     4NF  ─── Remove multi-valued dependencies                  │
│        ↓                                                        │
│     5NF  ─── Remove join dependencies                          │
│                                                                 │
└────────────────────────────────────────────────────────────────┘

Note: Most interviews focus on 1NF → 3NF (sometimes BCNF)
```

---

# 🔷 First Normal Form (1NF)

## Rules for 1NF:

1. **Each column contains atomic (indivisible) values**
2. **No repeating groups or arrays**
3. **Each row is unique** (has a primary key)
4. **Each column has a single data type**

## ❌ NOT in 1NF:

```
┌─────────┬────────┬───────────────────────────┐
│ emp_id  │  name  │        phone_numbers      │
├─────────┼────────┼───────────────────────────┤
│    1    │  John  │ 123-456, 789-012, 345-678 │  ← Multiple values!
│    2    │  Jane  │ 111-222                   │
└─────────┴────────┴───────────────────────────┘

Also NOT in 1NF:
┌─────────┬────────┬────────┬────────┬────────┐
│ emp_id  │  name  │ phone1 │ phone2 │ phone3 │  ← Repeating columns!
├─────────┼────────┼────────┼────────┼────────┤
│    1    │  John  │ 123456 │ 789012 │ 345678 │
│    2    │  Jane  │ 111222 │  NULL  │  NULL  │
└─────────┴────────┴────────┴────────┴────────┘
```

## ✅ Converted to 1NF:

```
EMPLOYEES                    EMPLOYEE_PHONES
┌─────────┬────────┐         ┌─────────┬────────────┐
│ emp_id  │  name  │         │ emp_id  │   phone    │
├─────────┼────────┤         ├─────────┼────────────┤
│    1    │  John  │         │    1    │  123-456   │
│    2    │  Jane  │         │    1    │  789-012   │
└─────────┴────────┘         │    1    │  345-678   │
                             │    2    │  111-222   │
                             └─────────┴────────────┘
```

### Interview Answer for 1NF:
> "A table is in 1NF when each cell contains only atomic values - no lists, no repeating groups. Each row must be uniquely identifiable. I achieve this by creating separate tables for multi-valued attributes."

---

# 🔷 Second Normal Form (2NF)

## Rules for 2NF:

1. **Must be in 1NF**
2. **No partial dependencies** - every non-key attribute must depend on the **entire** primary key, not just part of it

> 2NF is only relevant when you have a **composite primary key**!

## ❌ NOT in 2NF:

```
Primary Key: (student_id, course_id)

┌────────────┬───────────┬──────────────┬───────────────┬───────┐
│ student_id │ course_id │ student_name │  course_name  │ grade │
├────────────┼───────────┼──────────────┼───────────────┼───────┤
│     1      │    101    │     John     │     Math      │   A   │
│     1      │    102    │     John     │    Science    │   B   │
│     2      │    101    │     Jane     │     Math      │   A   │
└────────────┴───────────┴──────────────┴───────────────┴───────┘

Problems:
- student_name depends only on student_id (PARTIAL dependency)
- course_name depends only on course_id (PARTIAL dependency)
- Only grade depends on the FULL key (student_id + course_id)
```

## ✅ Converted to 2NF:

```
STUDENTS                     COURSES                    ENROLLMENTS
┌────────────┬──────────┐   ┌───────────┬─────────────┐ ┌────────────┬───────────┬───────┐
│ student_id │   name   │   │ course_id │ course_name │ │ student_id │ course_id │ grade │
├────────────┼──────────┤   ├───────────┼─────────────┤ ├────────────┼───────────┼───────┤
│     1      │   John   │   │    101    │    Math     │ │     1      │    101    │   A   │
│     2      │   Jane   │   │    102    │   Science   │ │     1      │    102    │   B   │
└────────────┴──────────┘   └───────────┴─────────────┘ │     2      │    101    │   A   │
                                                        └────────────┴───────────┴───────┘
```

### Functional Dependencies Visualized:

```
BEFORE (Not 2NF):
┌─────────────────────────────────────┐
│          Composite Key              │
│   (student_id, course_id)           │
│            │                        │
│    ┌───────┴───────┐               │
│    ↓               ↓               │
│ student_name   course_name   grade │
│ (partial)      (partial)    (full) │
└─────────────────────────────────────┘

AFTER (2NF):
student_id → student_name (own table)
course_id → course_name (own table)
(student_id, course_id) → grade (own table)
```

### Interview Answer for 2NF:
> "2NF removes partial dependencies. With a composite key, every non-key attribute must depend on ALL parts of the key, not just some. I fix this by moving partially dependent attributes to their own tables."

---

# 🔷 Third Normal Form (3NF)

## Rules for 3NF:

1. **Must be in 2NF**
2. **No transitive dependencies** - non-key columns should not depend on other non-key columns

> If A → B → C, and A is the primary key, then C transitively depends on A through B. This violates 3NF!

## ❌ NOT in 3NF:

```
Primary Key: emp_id

┌─────────┬────────┬─────────┬────────────┬───────────┐
│ emp_id  │  name  │ dept_id │ dept_name  │ dept_head │
├─────────┼────────┼─────────┼────────────┼───────────┤
│    1    │  John  │    10   │    IT      │   Alice   │
│    2    │  Jane  │    10   │    IT      │   Alice   │
│    3    │  Bob   │    20   │    HR      │   Charlie │
└─────────┴────────┴─────────┴────────────┴───────────┘

Transitive Dependencies:
- emp_id → dept_id (direct dependency - OK)
- dept_id → dept_name (non-key depends on non-key - BAD!)
- dept_id → dept_head (non-key depends on non-key - BAD!)

Therefore:
- emp_id → dept_id → dept_name (TRANSITIVE!)
- emp_id → dept_id → dept_head (TRANSITIVE!)
```

## ✅ Converted to 3NF:

```
EMPLOYEES                    DEPARTMENTS
┌─────────┬────────┬─────────┐ ┌─────────┬────────────┬───────────┐
│ emp_id  │  name  │ dept_id │ │ dept_id │ dept_name  │ dept_head │
├─────────┼────────┼─────────┤ ├─────────┼────────────┼───────────┤
│    1    │  John  │    10   │ │   10    │    IT      │   Alice   │
│    2    │  Jane  │    10   │ │   20    │    HR      │   Charlie │
│    3    │  Bob   │    20   │ └─────────┴────────────┴───────────┘
└─────────┴────────┴─────────┘
```

### Transitive Dependency Visualized:

```
BEFORE (Not 3NF):
┌─────────────────────────────────────────┐
│  emp_id (PK)                            │
│     │                                   │
│     ↓                                   │
│   dept_id ──→ dept_name                 │
│     │              │                    │
│     └──────────────┼──→ dept_head       │
│                    │                    │
│  "Transitive chain through dept_id"    │
└─────────────────────────────────────────┘

AFTER (3NF):
Table 1: emp_id → name, dept_id
Table 2: dept_id → dept_name, dept_head
```

### Interview Answer for 3NF:
> "3NF eliminates transitive dependencies. Non-key columns should depend directly on the primary key, not on other non-key columns. I achieve this by moving the transitively dependent attributes to a new table with the intermediate attribute as its primary key."

---

# 🔷 Boyce-Codd Normal Form (BCNF)

## Rules for BCNF:

1. **Must be in 3NF**
2. **Every determinant must be a candidate key**

> A determinant is an attribute that determines other attributes. In BCNF, only candidate keys can be determinants.

## Difference between 3NF and BCNF:

- **3NF**: Non-prime attributes must depend only on candidate keys
- **BCNF**: ALL attributes (including prime) must depend only on candidate keys

## ❌ NOT in BCNF:

```
STUDENT_COURSES
Primary Key: (student_id, subject)
Candidate Keys: (student_id, subject), (student_id, professor)

┌────────────┬───────────┬───────────┐
│ student_id │  subject  │ professor │
├────────────┼───────────┼───────────┤
│     1      │   Math    │   Dr. A   │
│     1      │  Science  │   Dr. B   │
│     2      │   Math    │   Dr. A   │
│     2      │  History  │   Dr. C   │
└────────────┴───────────┴───────────┘

Constraint: Each professor teaches only ONE subject

Dependencies:
- (student_id, subject) → professor ✓ (candidate key)
- professor → subject (Dr. A always teaches Math)
  
Problem: professor determines subject, but professor is NOT a candidate key!
```

## ✅ Converted to BCNF:

```
PROFESSOR_SUBJECTS              STUDENT_PROFESSORS
┌───────────┬───────────┐       ┌────────────┬───────────┐
│ professor │  subject  │       │ student_id │ professor │
├───────────┼───────────┤       ├────────────┼───────────┤
│   Dr. A   │   Math    │       │     1      │   Dr. A   │
│   Dr. B   │  Science  │       │     1      │   Dr. B   │
│   Dr. C   │  History  │       │     2      │   Dr. A   │
└───────────┴───────────┘       │     2      │   Dr. C   │
                                └────────────┴───────────┘
```

### Interview Answer for BCNF:
> "BCNF is stricter than 3NF. It requires that every determinant (attribute that determines others) must be a candidate key. This handles edge cases where 3NF might still have some redundancy due to overlapping candidate keys."

---
