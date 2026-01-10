# 🏗️ Database Design & Modeling - Interview Guide

> **Interviewer's Expectation**: They want to see if you can design a database from scratch. Be ready to draw ER diagrams on a whiteboard and create table schemas for common systems.

---

## 📌 What is Database Design?

**Database design** is the process of creating a detailed data model of a database. It involves:
1. Understanding requirements
2. Creating conceptual model (ER diagram)
3. Translating to logical model (tables, relationships)
4. Implementing physical model (SQL, indexes)

### Design Process:
```
┌─────────────────┐
│   Requirements  │
│   Gathering     │
└────────┬────────┘
         ↓
┌─────────────────┐
│   Conceptual    │  ← ER Diagram
│     Model       │
└────────┬────────┘
         ↓
┌─────────────────┐
│    Logical      │  ← Tables, Relationships
│     Model       │
└────────┬────────┘
         ↓
┌─────────────────┐
│    Physical     │  ← SQL DDL, Indexes
│     Model       │
└─────────────────┘
```

---

# 🔷 ER (Entity-Relationship) Diagrams

## 📌 What is an ER Diagram?

An **ER diagram** is a visual representation of entities (tables) and their relationships. It's the blueprint for your database.

### Components:

```
┌─────────────────────────────────────────────────────────────┐
│                    ER DIAGRAM COMPONENTS                     │
├─────────────────┬───────────────────────────────────────────┤
│   ENTITY        │  [Rectangle]  Table/Object                │
│   ATTRIBUTE     │  (Oval)       Column/Property             │
│   RELATIONSHIP  │  <Diamond>    Connection between entities │
│   Primary Key   │  (Underlined) Unique identifier           │
└─────────────────┴───────────────────────────────────────────┘
```

### Simple Example:

```
    (emp_id)   (name)   (salary)
       │         │         │
       └────┬────┴────┬────┘
            │         │
       ┌────┴─────────┴────┐
       │     EMPLOYEE      │
       └─────────┬─────────┘
                 │
            works_in
                 │
       ┌─────────┴─────────┐
       │    DEPARTMENT     │
       └────┬─────────┬────┘
            │         │
       └────┴────┬────┴────┘
       │         │         │
   (dept_id)  (name)  (location)
```

---

## 📌 Entities and Attributes

### Entity Types:

**Strong Entity**: Exists independently
```
EMPLOYEE, PRODUCT, CUSTOMER
- Has its own primary key
- Can exist without other entities
```

**Weak Entity**: Depends on another entity
```
ORDER_ITEM (depends on ORDER)
- No meaningful primary key alone
- Uses parent's key as part of its key
```

### Attribute Types:

```
1. Simple Attribute      → Single value (name, age)
2. Composite Attribute   → Divided (address → street, city, zip)
3. Derived Attribute     → Calculated (age from birthdate)
4. Multi-valued Attribute → Multiple values (skills, phones)
5. Key Attribute         → Unique identifier (employee_id)
```

### Example:
```
EMPLOYEE
├── emp_id (KEY)
├── name
│   ├── first_name
│   └── last_name
├── email
├── birth_date
├── age (DERIVED from birth_date)
└── phone_numbers (MULTI-VALUED)
```

---

## 📌 Relationships

### Relationship Definition:
A **relationship** describes how entities are connected.

### Example:
```
EMPLOYEE ──works_in──► DEPARTMENT
STUDENT ──enrolls_in──► COURSE
CUSTOMER ──places──► ORDER
```

---

## 📌 Cardinality (VERY IMPORTANT!)

**Cardinality** defines how many instances of one entity relate to instances of another.

### Types of Cardinality:

### 1. One-to-One (1:1)

```
One entity instance relates to exactly ONE instance of another.

Example: Person ↔ Passport
- One person has one passport
- One passport belongs to one person

┌──────────┐    1         1    ┌──────────┐
│  PERSON  │─────────has───────│ PASSPORT │
└──────────┘                   └──────────┘

Implementation:
Option 1: Put FK in either table
Option 2: Merge into single table
```

```sql
-- Option 1: FK in one table
CREATE TABLE persons (
    person_id INT PRIMARY KEY,
    name VARCHAR(100)
);

CREATE TABLE passports (
    passport_id INT PRIMARY KEY,
    passport_number VARCHAR(20),
    person_id INT UNIQUE,  -- UNIQUE ensures 1:1
    FOREIGN KEY (person_id) REFERENCES persons(person_id)
);
```

---

### 2. One-to-Many (1:M)

```
One entity instance relates to MANY instances of another.

Example: Department ↔ Employee
- One department has many employees
- One employee belongs to one department

┌────────────┐    1         M    ┌──────────┐
│ DEPARTMENT │────────has────────│ EMPLOYEE │
└────────────┘                   └──────────┘

Implementation: FK goes in the "many" side
```

```sql
CREATE TABLE departments (
    dept_id INT PRIMARY KEY,
    dept_name VARCHAR(100)
);

CREATE TABLE employees (
    emp_id INT PRIMARY KEY,
    name VARCHAR(100),
    dept_id INT,  -- FK to department
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id)
);
```

---

### 3. Many-to-Many (M:M)

```
Multiple instances of one entity relate to multiple instances of another.

Example: Student ↔ Course
- One student can enroll in many courses
- One course can have many students

┌──────────┐    M         M    ┌──────────┐
│ STUDENT  │────enrolls────────│  COURSE  │
└──────────┘                   └──────────┘

Implementation: Requires JUNCTION TABLE (bridge table)
```

```sql
CREATE TABLE students (
    student_id INT PRIMARY KEY,
    name VARCHAR(100)
);

CREATE TABLE courses (
    course_id INT PRIMARY KEY,
    course_name VARCHAR(100)
);

-- Junction table (bridge table)
CREATE TABLE enrollments (
    student_id INT,
    course_id INT,
    enrollment_date DATE,
    grade VARCHAR(2),
    PRIMARY KEY (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
);
```

---

### Cardinality Notation:

```
Chen Notation:      1 ────────── M
Crow's Foot:        ──┤├────────<─

┌─────────────────────────────────────────┐
│         CROW'S FOOT NOTATION            │
├─────────────────────────────────────────┤
│   ───○──  Zero or one                   │
│   ───|──  Exactly one                   │
│   ───<──  Many                          │
│   ──○<──  Zero or many                  │
│   ──|<──  One or many                   │
└─────────────────────────────────────────┘
```

---

## 📌 Cardinality Summary Table

| Relationship | Example | Implementation |
|--------------|---------|----------------|
| **1:1** | Person-Passport | FK with UNIQUE, or merge tables |
| **1:M** | Dept-Employee | FK in the "many" table |
| **M:M** | Student-Course | Junction table with two FKs |

---

# 🔷 DATABASE DESIGN EXAMPLES

## 📌 Design: Library Management System

### Requirements:
- Books with authors, publishers
- Members who can borrow books
- Track borrowing history

### ER Diagram (Text Representation):

```
┌──────────┐         ┌───────────┐         ┌──────────┐
│  AUTHOR  │───writes───│   BOOK   │───published_by───│ PUBLISHER│
└──────────┘    M:M  └───────────┘    M:1  └──────────┘
                            │
                        borrowed_by
                           M:M
                            │
                      ┌─────┴─────┐
                      │  MEMBER   │
                      └───────────┘
```

### Tables:

```sql
-- Publishers
CREATE TABLE publishers (
    publisher_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    address VARCHAR(500)
);

-- Authors
CREATE TABLE authors (
    author_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    birth_date DATE
);

-- Books
CREATE TABLE books (
    book_id INT PRIMARY KEY AUTO_INCREMENT,
    isbn VARCHAR(20) UNIQUE NOT NULL,
    title VARCHAR(300) NOT NULL,
    publisher_id INT,
    publication_year INT,
    copies_available INT DEFAULT 0,
    FOREIGN KEY (publisher_id) REFERENCES publishers(publisher_id)
);

-- Book-Author Junction (M:M)
CREATE TABLE book_authors (
    book_id INT,
    author_id INT,
    PRIMARY KEY (book_id, author_id),
    FOREIGN KEY (book_id) REFERENCES books(book_id),
    FOREIGN KEY (author_id) REFERENCES authors(author_id)
);

-- Members
CREATE TABLE members (
    member_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    email VARCHAR(200) UNIQUE,
    phone VARCHAR(20),
    membership_date DATE DEFAULT CURRENT_DATE,
    status ENUM('active', 'suspended', 'expired') DEFAULT 'active'
);

-- Borrowings (M:M with extra attributes)
CREATE TABLE borrowings (
    borrowing_id INT PRIMARY KEY AUTO_INCREMENT,
    book_id INT NOT NULL,
    member_id INT NOT NULL,
    borrow_date DATE NOT NULL DEFAULT CURRENT_DATE,
    due_date DATE NOT NULL,
    return_date DATE,
    fine_amount DECIMAL(10,2) DEFAULT 0,
    FOREIGN KEY (book_id) REFERENCES books(book_id),
    FOREIGN KEY (member_id) REFERENCES members(member_id)
);

-- Indexes for common queries
CREATE INDEX idx_book_title ON books(title);
CREATE INDEX idx_member_email ON members(email);
CREATE INDEX idx_borrowing_dates ON borrowings(borrow_date, due_date);
```

---

## 📌 Design: E-Commerce Website

### Requirements:
- Products with categories
- Customers who can place orders
- Orders with multiple items
- Payments and shipping

### ER Diagram:

```
┌───────────┐         ┌───────────┐
│ CATEGORY  │───has───│  PRODUCT  │
└───────────┘   1:M   └───────────┘
                           │
                      ┌────┴────┐
                      │         │
                 contains    reviews
                   M:M        1:M
                      │         │
               ┌──────┴─┐   ┌───┴────┐
               │ ORDER  │   │ REVIEW │
               └────┬───┘   └────────┘
                    │
                places
                  1:M
                    │
              ┌─────┴─────┐
              │ CUSTOMER  │
              └─────┬─────┘
                    │
               has_address
                  1:M
                    │
              ┌─────┴─────┐
              │  ADDRESS  │
              └───────────┘
```

### Tables:

```sql
-- Categories
CREATE TABLE categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    parent_category_id INT,
    FOREIGN KEY (parent_category_id) REFERENCES categories(category_id)
);

-- Products
CREATE TABLE products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(300) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INT DEFAULT 0,
    category_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

-- Customers
CREATE TABLE customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(200) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Addresses
CREATE TABLE addresses (
    address_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    address_type ENUM('billing', 'shipping') NOT NULL,
    street VARCHAR(300),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    is_default BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

-- Orders
CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    shipping_address_id INT,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('pending', 'processing', 'shipped', 'delivered', 'cancelled') DEFAULT 'pending',
    subtotal DECIMAL(10,2),
    tax DECIMAL(10,2),
    shipping_cost DECIMAL(10,2),
    total DECIMAL(10,2),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (shipping_address_id) REFERENCES addresses(address_id)
);

-- Order Items (Junction table)
CREATE TABLE order_items (
    order_item_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- Payments
CREATE TABLE payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    payment_method ENUM('credit_card', 'debit_card', 'upi', 'net_banking', 'cod') NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status ENUM('pending', 'completed', 'failed', 'refunded') DEFAULT 'pending',
    transaction_id VARCHAR(100),
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

-- Reviews
CREATE TABLE reviews (
    review_id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    customer_id INT NOT NULL,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    UNIQUE (product_id, customer_id)  -- One review per customer per product
);

-- Indexes
CREATE INDEX idx_product_category ON products(category_id);
CREATE INDEX idx_order_customer ON orders(customer_id);
CREATE INDEX idx_order_status ON orders(status);
CREATE INDEX idx_review_product ON reviews(product_id);
```

---

## 📌 Design: Student Management System

### Requirements:
- Students with personal info
- Courses with teachers
- Enrollments and grades
- Departments and programs

### Tables:

```sql
-- Departments
CREATE TABLE departments (
    dept_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    head_faculty_id INT  -- Will be FK to faculty
);

-- Faculty
CREATE TABLE faculty (
    faculty_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    email VARCHAR(200) UNIQUE,
    dept_id INT,
    designation VARCHAR(100),
    hire_date DATE,
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id)
);

-- Add FK to departments after faculty table exists
ALTER TABLE departments 
ADD FOREIGN KEY (head_faculty_id) REFERENCES faculty(faculty_id);

-- Students
CREATE TABLE students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    roll_number VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    email VARCHAR(200) UNIQUE,
    phone VARCHAR(20),
    date_of_birth DATE,
    dept_id INT,
    admission_year INT,
    current_semester INT,
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id)
);

-- Courses
CREATE TABLE courses (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    course_code VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    credits INT NOT NULL,
    dept_id INT,
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id)
);

-- Course Offerings (course + semester + faculty)
CREATE TABLE course_offerings (
    offering_id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL,
    faculty_id INT NOT NULL,
    semester VARCHAR(20) NOT NULL,  -- e.g., "Fall 2024"
    academic_year INT NOT NULL,
    max_students INT DEFAULT 60,
    FOREIGN KEY (course_id) REFERENCES courses(course_id),
    FOREIGN KEY (faculty_id) REFERENCES faculty(faculty_id),
    UNIQUE (course_id, semester, academic_year)
);

-- Enrollments
CREATE TABLE enrollments (
    enrollment_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    offering_id INT NOT NULL,
    enrollment_date DATE DEFAULT CURRENT_DATE,
    grade VARCHAR(2),
    status ENUM('enrolled', 'dropped', 'completed') DEFAULT 'enrolled',
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (offering_id) REFERENCES course_offerings(offering_id),
    UNIQUE (student_id, offering_id)
);

-- Attendance
CREATE TABLE attendance (
    attendance_id INT PRIMARY KEY AUTO_INCREMENT,
    enrollment_id INT NOT NULL,
    date DATE NOT NULL,
    status ENUM('present', 'absent', 'late') NOT NULL,
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id),
    UNIQUE (enrollment_id, date)
);
```

---

# 🔷 DATABASE DESIGN BEST PRACTICES

## 📌 Design Guidelines

### 1. Start with Requirements
- List all entities and their attributes
- Identify relationships
- Note business rules

### 2. Follow Naming Conventions
```sql
-- Good: Consistent, descriptive
customer_id, order_date, is_active

-- Bad: Inconsistent, vague
CustID, date1, flag
```

### 3. Choose Appropriate Data Types
```sql
-- Use smallest sufficient type
status TINYINT                    -- Not INT for 0/1
price DECIMAL(10,2)              -- Not FLOAT for money
uuid CHAR(36)                    -- Fixed length for UUID
email VARCHAR(200)               -- Variable length
```

### 4. Define Constraints
```sql
-- Always have primary keys
-- Add foreign keys for relationships
-- Use NOT NULL where required
-- Add CHECK constraints for valid values
-- Use UNIQUE for natural keys
```

### 5. Plan for Indexes
```sql
-- Index columns used in:
WHERE clauses
JOIN conditions
ORDER BY
GROUP BY
```

### 6. Consider Future Growth
```sql
-- Use AUTO_INCREMENT for IDs
-- Plan for data archival
-- Consider partitioning for large tables
```

---

# 🎯 Quick Interview Cheat Sheet

## ER Diagram Components:
```
Entity      → Table (Rectangle)
Attribute   → Column (Oval)
Relationship→ Connection (Diamond)
```

## Cardinality:
```
1:1  → FK with UNIQUE, or merge tables
1:M  → FK in the "many" side
M:M  → Junction table with two FKs
```

## Design Process:
```
1. Requirements → What data to store?
2. Conceptual   → ER diagram
3. Logical      → Tables, relationships
4. Physical     → SQL, indexes
```

## Common Design Patterns:
```
Self-referencing → Employee-Manager (same table)
Junction table   → Many-to-many relationships
Soft delete      → is_deleted flag instead of DELETE
Audit columns    → created_at, updated_at, created_by
```

---

## 💡 Pro Interview Tips

1. **Ask clarifying questions**
   - "How many users are expected?"
   - "What are the main queries?"
   - "Does order matter?"

2. **Think out loud**
   > "I'm identifying entities: User, Product, Order..."

3. **Start simple, then refine**
   - Get basic tables first
   - Add constraints and indexes
   - Consider edge cases

4. **Know common patterns**:
   - E-commerce: Customer → Order → OrderItem → Product
   - Social: User → Post → Comment, User → Follow → User
   - Booking: User → Booking → Resource (rooms, seats)

5. **Mention trade-offs**:
   - "I'm denormalizing for read performance here"
   - "This many-to-many might need a junction table"

6. **Practice these designs**:
   - Library system
   - E-commerce
   - Social media
   - Booking system (hotel/flight)
   - Chat application

