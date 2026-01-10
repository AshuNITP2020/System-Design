# 📋 Views, Triggers & Stored Procedures - Interview Guide

> **Interviewer's Expectation**: These topics test your understanding of database-level logic and abstraction. Knowing when to use these vs application code shows database maturity.

---

# 🔷 VIEWS

## 📌 What is a View?

A **view** is a **virtual table** based on the result of a SQL query. It doesn't store data itself - it's just a saved query that you can treat like a table.

### Real-World Analogy:
> Think of a view as a **window** into your data. The actual data stays in the tables; the view just provides a different way to look at it.

### Creating a View:

```sql
-- Simple view
CREATE VIEW active_employees AS
SELECT emp_id, name, salary, department
FROM employees
WHERE status = 'active';

-- Using the view
SELECT * FROM active_employees;
SELECT name FROM active_employees WHERE salary > 50000;
```

### View with JOIN:

```sql
CREATE VIEW employee_details AS
SELECT 
    e.emp_id,
    e.name,
    e.salary,
    d.dept_name,
    m.name AS manager_name
FROM employees e
LEFT JOIN departments d ON e.dept_id = d.dept_id
LEFT JOIN employees m ON e.manager_id = m.emp_id;

-- Now complex join is simplified
SELECT * FROM employee_details WHERE dept_name = 'Engineering';
```

---

## 📌 Why Use Views?

### 1. **Simplify Complex Queries**
```sql
-- Instead of writing this every time:
SELECT e.name, d.dept_name, p.project_name
FROM employees e
JOIN departments d ON e.dept_id = d.dept_id
JOIN project_assignments pa ON e.emp_id = pa.emp_id
JOIN projects p ON pa.project_id = p.project_id
WHERE e.status = 'active';

-- Create a view once:
CREATE VIEW active_employee_projects AS ...;

-- Then simply:
SELECT * FROM active_employee_projects;
```

### 2. **Security / Access Control**
```sql
-- Hide sensitive salary data
CREATE VIEW employee_public_info AS
SELECT emp_id, name, department, hire_date
FROM employees;
-- No salary column!

GRANT SELECT ON employee_public_info TO 'public_user';
-- User can only see non-sensitive data
```

### 3. **Data Abstraction**
```sql
-- Application uses view, not underlying tables
-- You can change table structure without breaking app
CREATE VIEW customer_orders AS
SELECT c.name, o.order_date, o.total
FROM customers c
JOIN orders o ON c.id = o.customer_id;

-- Later, if you rename tables or columns,
-- just update the view definition
```

### 4. **Consistent Calculations**
```sql
CREATE VIEW order_summary AS
SELECT 
    order_id,
    subtotal,
    subtotal * 0.18 AS tax,
    subtotal * 1.18 AS total
FROM orders;
-- Tax calculation is consistent everywhere
```

---

## 📌 View vs Table

### ⭐ Common Interview Question!

| View | Table |
|------|-------|
| Virtual (no data storage) | Physical (stores data) |
| Always shows latest data | Data persists until changed |
| Defined by SQL query | Defined by schema |
| No indexes (usually) | Can have indexes |
| Slower (query runs each time) | Faster (direct access) |
| Cannot have constraints | Can have PK, FK, etc. |
| No INSERT/UPDATE (mostly) | Full CRUD support |

### Interview Answer:
> "A view is a virtual table - it doesn't store data but executes its defining query each time. Views are useful for simplifying queries, providing security by hiding columns, and abstracting database structure. Tables actually store the data."

---

## 📌 Updatable vs Non-Updatable Views

### Updatable View:
Can INSERT, UPDATE, DELETE through the view.

**Requirements for Updatable View**:
1. Based on single table
2. No GROUP BY, DISTINCT, aggregate functions
3. No subqueries in SELECT
4. All NOT NULL columns included
5. No JOIN (in some databases)

```sql
-- Updatable view (simple, single table)
CREATE VIEW high_salary_employees AS
SELECT emp_id, name, salary
FROM employees
WHERE salary > 50000;

-- Can update through view
UPDATE high_salary_employees SET salary = 60000 WHERE emp_id = 1;
```

### Non-Updatable View:
Read-only; cannot modify data through it.

```sql
-- Non-updatable (has JOIN and aggregate)
CREATE VIEW department_stats AS
SELECT d.dept_name, COUNT(*) AS emp_count, AVG(e.salary) AS avg_salary
FROM departments d
JOIN employees e ON d.dept_id = e.dept_id
GROUP BY d.dept_name;

-- Cannot INSERT/UPDATE - what would it mean?
UPDATE department_stats SET avg_salary = 60000;  -- ERROR!
```

### WITH CHECK OPTION:
Ensures INSERT/UPDATE through view satisfies view condition.

```sql
CREATE VIEW high_salary_employees AS
SELECT emp_id, name, salary
FROM employees
WHERE salary > 50000
WITH CHECK OPTION;

-- This works:
UPDATE high_salary_employees SET salary = 60000 WHERE emp_id = 1;

-- This fails (would violate view condition):
UPDATE high_salary_employees SET salary = 40000 WHERE emp_id = 1;
-- ERROR: Check option violation
```

---

## 📌 Materialized Views

**Definition**: A view that **stores the result** physically (caches the data).

```sql
-- PostgreSQL
CREATE MATERIALIZED VIEW monthly_sales AS
SELECT 
    DATE_TRUNC('month', order_date) AS month,
    SUM(total) AS total_sales
FROM orders
GROUP BY DATE_TRUNC('month', order_date);

-- Refresh when needed
REFRESH MATERIALIZED VIEW monthly_sales;
```

### View vs Materialized View:

| Regular View | Materialized View |
|--------------|-------------------|
| Query runs each time | Stores results |
| Always current | May be stale |
| No storage | Uses storage |
| Slower | Faster |
| No refresh needed | Must refresh |

**Use Materialized Views for**:
- Complex aggregations
- Reports that don't need real-time data
- Data warehouse queries

---

# 🔷 STORED PROCEDURES

## 📌 What is a Stored Procedure?

A **stored procedure** is a set of SQL statements stored in the database that can be executed as a single unit. It's like a function that lives in the database.

### Creating a Stored Procedure:

```sql
-- MySQL syntax
DELIMITER //

CREATE PROCEDURE GetEmployeesByDept(IN dept_id INT)
BEGIN
    SELECT emp_id, name, salary
    FROM employees
    WHERE department_id = dept_id;
END //

DELIMITER ;

-- Calling the procedure
CALL GetEmployeesByDept(10);
```

### Stored Procedure with Output:

```sql
DELIMITER //

CREATE PROCEDURE GetEmployeeStats(
    IN dept_id INT,
    OUT emp_count INT,
    OUT avg_salary DECIMAL(10,2)
)
BEGIN
    SELECT COUNT(*), AVG(salary) INTO emp_count, avg_salary
    FROM employees
    WHERE department_id = dept_id;
END //

DELIMITER ;

-- Calling with output variables
CALL GetEmployeeStats(10, @count, @avg);
SELECT @count, @avg;
```

### Stored Procedure with Logic:

```sql
DELIMITER //

CREATE PROCEDURE TransferFunds(
    IN from_account INT,
    IN to_account INT,
    IN amount DECIMAL(10,2),
    OUT status VARCHAR(50)
)
BEGIN
    DECLARE from_balance DECIMAL(10,2);
    
    -- Start transaction
    START TRANSACTION;
    
    -- Check balance
    SELECT balance INTO from_balance 
    FROM accounts WHERE account_id = from_account;
    
    IF from_balance < amount THEN
        SET status = 'Insufficient funds';
        ROLLBACK;
    ELSE
        -- Perform transfer
        UPDATE accounts SET balance = balance - amount 
        WHERE account_id = from_account;
        
        UPDATE accounts SET balance = balance + amount 
        WHERE account_id = to_account;
        
        SET status = 'Success';
        COMMIT;
    END IF;
END //

DELIMITER ;
```

---

## 📌 Advantages of Stored Procedures

### ✅ Advantages:

1. **Performance**
   - Pre-compiled and cached
   - Reduced network traffic (single call vs multiple statements)
   ```sql
   -- Instead of 10 SQL calls from application
   CALL ProcessMonthEndReport();  -- One call
   ```

2. **Security**
   - Users can execute procedure without direct table access
   - SQL injection prevention (parameterized)
   ```sql
   GRANT EXECUTE ON TransferFunds TO 'app_user';
   -- User can transfer but can't see/modify tables directly
   ```

3. **Reusability**
   - Write once, call from multiple applications
   - Consistent business logic

4. **Maintainability**
   - Change logic in one place
   - No application redeployment needed

5. **Transaction Management**
   - Complex transactions in single unit
   - Better error handling

---

## 📌 Disadvantages of Stored Procedures

### ❌ Disadvantages:

1. **Debugging Difficulty**
   - Harder to debug than application code
   - Limited IDE support

2. **Version Control**
   - Not easily managed in Git
   - Database migrations become complex

3. **Portability**
   - Syntax varies by database
   - MySQL procedure != PostgreSQL procedure

4. **Testing**
   - Harder to unit test
   - Requires database connection

5. **Business Logic Split**
   - Logic in DB + Application = confusion
   - "Where is this calculation done?"

---

## 📌 Stored Procedure vs Function

| Stored Procedure | Function |
|-----------------|----------|
| May or may not return value | Must return a value |
| Can modify database (INSERT, UPDATE) | Usually read-only |
| Called with CALL | Can be used in SELECT |
| Can return multiple result sets | Returns single value/table |
| Cannot use in WHERE clause | Can use in WHERE, SELECT |

```sql
-- Function example (MySQL)
DELIMITER //
CREATE FUNCTION CalculateTax(amount DECIMAL(10,2))
RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
    RETURN amount * 0.18;
END //
DELIMITER ;

-- Using function
SELECT product_name, price, CalculateTax(price) AS tax FROM products;
```

---

# 🔷 TRIGGERS

## 📌 What is a Trigger?

A **trigger** is a stored program that **automatically executes** when a specific event occurs on a table (INSERT, UPDATE, DELETE).

### Real-World Analogy:
> A trigger is like a motion sensor light - you don't manually turn it on; it activates automatically when someone walks by.

### Creating a Trigger:

```sql
-- Audit log trigger
CREATE TRIGGER employee_audit
AFTER UPDATE ON employees
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (
        table_name, 
        action, 
        old_value, 
        new_value, 
        changed_at
    )
    VALUES (
        'employees',
        'UPDATE',
        CONCAT('Salary was: ', OLD.salary),
        CONCAT('Salary is: ', NEW.salary),
        NOW()
    );
END;
```

---

## 📌 Types of Triggers

### By Timing:

**BEFORE Trigger** - Executes before the operation
```sql
CREATE TRIGGER validate_salary
BEFORE INSERT ON employees
FOR EACH ROW
BEGIN
    IF NEW.salary < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Salary cannot be negative';
    END IF;
END;
```

**AFTER Trigger** - Executes after the operation
```sql
CREATE TRIGGER update_inventory
AFTER INSERT ON order_items
FOR EACH ROW
BEGIN
    UPDATE products 
    SET stock = stock - NEW.quantity
    WHERE product_id = NEW.product_id;
END;
```

### By Event:

```sql
-- INSERT trigger
CREATE TRIGGER after_employee_insert
AFTER INSERT ON employees
FOR EACH ROW ...

-- UPDATE trigger
CREATE TRIGGER after_employee_update
AFTER UPDATE ON employees
FOR EACH ROW ...

-- DELETE trigger
CREATE TRIGGER after_employee_delete
AFTER DELETE ON employees
FOR EACH ROW ...
```

---

## 📌 OLD and NEW Keywords

In triggers, you can access:
- **OLD.column** - Value before the change (UPDATE, DELETE)
- **NEW.column** - Value after the change (INSERT, UPDATE)

```sql
CREATE TRIGGER salary_change_log
AFTER UPDATE ON employees
FOR EACH ROW
BEGIN
    IF OLD.salary != NEW.salary THEN
        INSERT INTO salary_history (
            emp_id,
            old_salary,
            new_salary,
            changed_at
        )
        VALUES (
            OLD.emp_id,
            OLD.salary,
            NEW.salary,
            NOW()
        );
    END IF;
END;
```

| Trigger Event | OLD | NEW |
|--------------|-----|-----|
| INSERT | ❌ N/A | ✅ Available |
| UPDATE | ✅ Available | ✅ Available |
| DELETE | ✅ Available | ❌ N/A |

---

## 📌 Common Trigger Use Cases

### 1. Audit Logging
```sql
CREATE TRIGGER audit_changes
AFTER UPDATE ON sensitive_data
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (table_name, user, action, timestamp)
    VALUES ('sensitive_data', CURRENT_USER(), 'UPDATE', NOW());
END;
```

### 2. Data Validation
```sql
CREATE TRIGGER validate_email
BEFORE INSERT ON users
FOR EACH ROW
BEGIN
    IF NEW.email NOT LIKE '%@%.%' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Invalid email format';
    END IF;
END;
```

### 3. Maintaining Derived Data
```sql
CREATE TRIGGER update_order_total
AFTER INSERT ON order_items
FOR EACH ROW
BEGIN
    UPDATE orders
    SET total = (SELECT SUM(quantity * price) 
                 FROM order_items 
                 WHERE order_id = NEW.order_id)
    WHERE order_id = NEW.order_id;
END;
```

### 4. Enforcing Business Rules
```sql
CREATE TRIGGER prevent_self_manager
BEFORE INSERT ON employees
FOR EACH ROW
BEGIN
    IF NEW.manager_id = NEW.emp_id THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Employee cannot be their own manager';
    END IF;
END;
```

---

## 📌 Trigger Best Practices

### ✅ Do:
- Keep triggers simple and fast
- Use for auditing and logging
- Document all triggers
- Test thoroughly

### ❌ Don't:
- Put complex business logic in triggers
- Create triggers that trigger other triggers (cascading)
- Use triggers when a constraint would work
- Forget about triggers (hidden logic!)

---

## 📌 Trigger vs Constraint vs Application Logic

| Use | When |
|-----|------|
| **Constraint** | Simple rules (NOT NULL, CHECK, FK) |
| **Trigger** | Complex validation, audit logging |
| **Application** | Business logic, user interaction |

```sql
-- Use CONSTRAINT for simple rules:
salary DECIMAL(10,2) CHECK (salary >= 0)

-- Use TRIGGER for complex rules:
-- "Manager can only have max 10 direct reports"

-- Use APPLICATION for:
-- "Send email when employee is promoted"
```

---

# 🎯 Quick Interview Cheat Sheet

## Views:
```
View = Virtual table (saved query)
✅ Simplify queries
✅ Security (hide columns)
✅ Abstraction
❌ No indexes
❌ Performance overhead
```

## Stored Procedures:
```
SP = Saved SQL program
✅ Performance (pre-compiled)
✅ Security (parameterized)
✅ Reusability
❌ Hard to debug
❌ Not portable
```

## Triggers:
```
Trigger = Auto-execute on event
Types: BEFORE/AFTER + INSERT/UPDATE/DELETE
OLD = value before change
NEW = value after change
✅ Audit logging
✅ Derived data
❌ Hidden logic
❌ Performance impact
```

---

## 💡 Pro Interview Tips

1. **Know when to use what**
   > "I'd use a view for simplifying queries, a stored procedure for complex operations that need transaction control, and a trigger for automatic auditing."

2. **Discuss trade-offs**
   > "Stored procedures are faster but harder to version control. I prefer application code for complex business logic."

3. **Show practical experience**
   > "In my project, we used triggers for maintaining audit trails of all data changes."

4. **Common follow-up questions**:
   - "Can you update a view with JOINs?"
   - "What's the difference between BEFORE and AFTER triggers?"
   - "When would you NOT use a stored procedure?"
   - "How do you handle errors in stored procedures?"

5. **Be ready to write**:
   - A simple view definition
   - A stored procedure with parameters
   - A trigger for audit logging

