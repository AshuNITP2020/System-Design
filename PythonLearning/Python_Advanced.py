
# ============================================================
#                    ADVANCED PYTHON TOPICS
# ============================================================

# ============================================================
# 14. OBJECT-ORIENTED PROGRAMMING (CLASSES)
# ============================================================

"""
OOP is a programming paradigm that uses "objects" to model 
real-world entities. Classes are blueprints for creating objects.

Key Concepts:
- Encapsulation: Bundling data and methods together
- Inheritance: Child class inherits from parent class
- Polymorphism: Same method name, different behavior
- Abstraction: Hiding complex implementation details
"""

# Basic Class
class Person:
    # Class attribute (shared by all instances)
    species = "Human"
    
    # Constructor - initializes the object
    def __init__(self, name, age):
        self.name = name      # Instance attribute
        self.age = age
    
    # Instance method
    def greet(self):
        return f"Hello, I'm {self.name} and I'm {self.age} years old"
    
    # String representation
    def __str__(self):
        return f"Person({self.name}, {self.age})"
    
    # Class method - works with class, not instance
    @classmethod
    def get_species(cls):
        return cls.species
    
    # Static method - doesn't need class or instance
    @staticmethod
    def is_adult(age):
        return age >= 18

# Creating objects (instances)
person1 = Person("Ashutosh", 25)
person2 = Person("Priya", 22)

print(person1.greet())              # Hello, I'm Ashutosh and I'm 25 years old
print(person1.name)                 # Ashutosh
print(Person.get_species())         # Human
print(Person.is_adult(20))          # True

# Inheritance - creating child class from parent
class Student(Person):
    def __init__(self, name, age, grade):
        super().__init__(name, age)  # Call parent constructor
        self.grade = grade
    
    # Method overriding
    def greet(self):
        return f"Hi, I'm {self.name}, a student in grade {self.grade}"
    
    # New method specific to Student
    def study(self, subject):
        return f"{self.name} is studying {subject}"

class Teacher(Person):
    def __init__(self, name, age, subject):
        super().__init__(name, age)
        self.subject = subject
    
    def greet(self):
        return f"Hello, I'm {self.name}, I teach {self.subject}"

# Polymorphism in action
student = Student("Rahul", 16, 10)
teacher = Teacher("Dr. Sharma", 45, "Physics")

# Same method name, different behavior
print(student.greet())  # Hi, I'm Rahul, a student in grade 10
print(teacher.greet())  # Hello, I'm Dr. Sharma, I teach Physics

# Private attributes (convention: prefix with _)
class BankAccount:
    def __init__(self, owner, balance=0):
        self.owner = owner
        self._balance = balance  # Protected (convention)
        self.__pin = 1234        # Private (name mangling)
    
    def deposit(self, amount):
        if amount > 0:
            self._balance += amount
            return f"Deposited ₹{amount}. New balance: ₹{self._balance}"
        return "Invalid amount"
    
    def withdraw(self, amount):
        if 0 < amount <= self._balance:
            self._balance -= amount
            return f"Withdrew ₹{amount}. New balance: ₹{self._balance}"
        return "Insufficient funds or invalid amount"
    
    def get_balance(self):
        return self._balance

account = BankAccount("Ashutosh", 1000)
print(account.deposit(500))    # Deposited ₹500. New balance: ₹1500
print(account.withdraw(200))   # Withdrew ₹200. New balance: ₹1300

# ============================================================
# 15. MODULES AND PACKAGES
# ============================================================

"""
Modules: Python files containing reusable code
Packages: Directories containing multiple modules

Creating a Module (save as my_math.py):
    def add(a, b):
        return a + b
    
    PI = 3.14159
    
    class Calculator:
        def multiply(self, a, b):
            return a * b

Package Structure:
    my_package/
        __init__.py      # Makes it a package (can be empty)
        module1.py
        module2.py
        subpackage/
            __init__.py
            module3.py
"""

# --- Using Built-in Modules ---

# Math module
import math
print(math.pi)           # 3.141592653589793
print(math.sqrt(16))     # 4.0
print(math.ceil(4.2))    # 5
print(math.floor(4.8))   # 4

# Random module
import random
print(random.randint(1, 10))         # Random int between 1-10
print(random.choice(['a', 'b', 'c'])) # Random choice
print(random.random())                # Random float 0-1

# Datetime module
from datetime import datetime, timedelta
now = datetime.now()
print(now.strftime("%Y-%m-%d %H:%M:%S"))  # 2024-01-15 14:30:00
tomorrow = now + timedelta(days=1)
print(f"Tomorrow: {tomorrow.date()}")

# OS module
import os
print(os.getcwd())              # Current working directory
# print(os.listdir('.'))        # List files in directory
# os.makedirs('new_folder')     # Create directory

# JSON module
import json

# Python dict to JSON string
data = {"name": "Ashutosh", "age": 25, "skills": ["Python", "Java"]}
json_string = json.dumps(data, indent=2)
print(json_string)

# JSON string to Python dict
parsed_data = json.loads(json_string)
print(parsed_data["name"])  # Ashutosh

# Different import styles
import math                        # Import entire module
from math import sqrt, pi          # Import specific items
from math import sqrt as square_root  # Import with alias
import math as m                   # Module alias
# from math import *               # Import all (not recommended)

# ============================================================
# 16. DECORATORS
# ============================================================

"""
Decorators are functions that modify the behavior of other 
functions without changing their code. They use the @ syntax.
"""

# Basic decorator
def my_decorator(func):
    def wrapper(*args, **kwargs):
        print("Before function call")
        result = func(*args, **kwargs)
        print("After function call")
        return result
    return wrapper

@my_decorator
def say_hello(name):
    print(f"Hello, {name}!")

say_hello("Ashutosh")
# Output:
# Before function call
# Hello, Ashutosh!
# After function call

# Practical Example 1: Timer decorator
import time

def timer(func):
    def wrapper(*args, **kwargs):
        start = time.time()
        result = func(*args, **kwargs)
        end = time.time()
        print(f"{func.__name__} took {end - start:.4f} seconds")
        return result
    return wrapper

@timer
def slow_function():
    time.sleep(0.5)  # Simulate slow operation
    return "Done"

# slow_function()  # Uncomment to test

# Practical Example 2: Logger decorator
def logger(func):
    def wrapper(*args, **kwargs):
        print(f"Calling {func.__name__} with args={args}, kwargs={kwargs}")
        result = func(*args, **kwargs)
        print(f"{func.__name__} returned {result}")
        return result
    return wrapper

@logger
def add_numbers(a, b):
    return a + b

add_numbers(5, 3)
# Calling add_numbers with args=(5, 3), kwargs={}
# add_numbers returned 8

# Decorator with arguments
def repeat(times):
    def decorator(func):
        def wrapper(*args, **kwargs):
            for _ in range(times):
                result = func(*args, **kwargs)
            return result
        return wrapper
    return decorator

@repeat(times=3)
def greet_decorated(name):
    print(f"Hello, {name}!")

greet_decorated("World")  # Prints "Hello, World!" 3 times

# ============================================================
# 17. GENERATORS
# ============================================================

"""
Generators are functions that produce a sequence of values 
lazily (one at a time), saving memory for large datasets.
They use 'yield' instead of 'return'.
"""

# Generator function
def count_up_to(n):
    i = 1
    while i <= n:
        yield i      # Pauses here, returns value, resumes on next call
        i += 1

# Using the generator
counter = count_up_to(5)
print(next(counter))  # 1
print(next(counter))  # 2
print(next(counter))  # 3

# Or iterate through it
print("Counting to 5:")
for num in count_up_to(5):
    print(num)  # 1, 2, 3, 4, 5

# Practical Example: Reading large files line by line
def read_large_file(file_path):
    """Generator to read file line by line (memory efficient)"""
    with open(file_path, 'r') as file:
        for line in file:
            yield line.strip()

# Generator expression (like list comprehension but lazy)
squares_list = [x**2 for x in range(1000000)]  # Creates entire list in memory
squares_gen = (x**2 for x in range(1000000))   # Creates values on-demand

# Infinite generator
def infinite_counter():
    num = 0
    while True:
        yield num
        num += 1

# Fibonacci generator
def fibonacci():
    a, b = 0, 1
    while True:
        yield a
        a, b = b, a + b

# Get first 10 Fibonacci numbers
fib = fibonacci()
fib_numbers = [next(fib) for _ in range(10)]
print(f"First 10 Fibonacci: {fib_numbers}")  # [0, 1, 1, 2, 3, 5, 8, 13, 21, 34]

# ============================================================
# 18. REGULAR EXPRESSIONS
# ============================================================

"""
Regular expressions (regex) are patterns used to match 
character combinations in strings.

Common patterns:
    \d  - digit (0-9)
    \w  - word character (a-z, A-Z, 0-9, _)
    \s  - whitespace
    .   - any character except newline
    *   - 0 or more
    +   - 1 or more
    ?   - 0 or 1
    ^   - start of string
    $   - end of string
    []  - character class
    ()  - grouping
"""

import re

text = "Contact us at support@example.com or sales@company.org"

# Find all email addresses
emails = re.findall(r'[\w.-]+@[\w.-]+', text)
print(f"Emails found: {emails}")  # ['support@example.com', 'sales@company.org']

# Check if pattern matches
phone = "123-456-7890"
if re.match(r'\d{3}-\d{3}-\d{4}', phone):
    print("Valid phone number format")

# Search for pattern
text2 = "The price is $49.99"
match = re.search(r'\$[\d.]+', text2)
if match:
    print(f"Price found: {match.group()}")  # $49.99

# Replace pattern
text3 = "Hello World World"
new_text = re.sub(r'World', 'Python', text3)
print(new_text)  # Hello Python Python

# Split by pattern
text4 = "apple, banana; cherry:grape"
fruits_list = re.split(r'[,;:]\s*', text4)
print(fruits_list)  # ['apple', 'banana', 'cherry', 'grape']

# Groups
pattern = r'(\d{2})/(\d{2})/(\d{4})'
date_string = "Today is 25/12/2024"
match = re.search(pattern, date_string)
if match:
    day, month, year = match.groups()
    print(f"Day: {day}, Month: {month}, Year: {year}")

# Common regex patterns
patterns = {
    'email': r'^[\w.-]+@[\w.-]+\.\w+$',
    'phone': r'^\d{10}$',
    'url': r'https?://[\w.-]+(?:/[\w.-]*)*',
    'date': r'\d{2}/\d{2}/\d{4}',
    'ip_address': r'\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}',
}

def validate_email(email):
    pattern = r'^[\w.-]+@[\w.-]+\.\w+$'
    return bool(re.match(pattern, email))

print(validate_email("test@example.com"))   # True
print(validate_email("invalid-email"))       # False

# ============================================================
# 19. WORKING WITH APIs
# ============================================================

"""
APIs (Application Programming Interfaces) allow programs to 
communicate with web services. We use the 'requests' library.

Install: pip install requests

HTTP Methods:
    GET    - Retrieve data
    POST   - Send data
    PUT    - Update data
    DELETE - Remove data
"""

# Note: Uncomment the code below to test (requires internet & requests library)

# import requests

# --- GET Request (Retrieve data) ---
# response = requests.get('https://jsonplaceholder.typicode.com/posts/1')
# 
# print(f"Status Code: {response.status_code}")  # 200 = Success
# print(f"Response JSON: {response.json()}")
# 
# # Access specific data
# data = response.json()
# print(f"Title: {data['title']}")

# --- GET with parameters ---
# params = {'userId': 1}
# response = requests.get('https://jsonplaceholder.typicode.com/posts', params=params)
# posts = response.json()
# print(f"Found {len(posts)} posts")

# --- POST Request (Send data) ---
# new_post = {
#     'title': 'My New Post',
#     'body': 'This is the content',
#     'userId': 1
# }
# response = requests.post(
#     'https://jsonplaceholder.typicode.com/posts',
#     json=new_post
# )
# print(f"Created: {response.json()}")

# --- Headers and Authentication ---
# headers = {
#     'Authorization': 'Bearer your_token_here',
#     'Content-Type': 'application/json'
# }
# response = requests.get('https://api.example.com/data', headers=headers)

# --- Error Handling for APIs ---
# try:
#     response = requests.get('https://api.example.com/data', timeout=5)
#     response.raise_for_status()  # Raises exception for 4xx/5xx status
#     data = response.json()
# except requests.exceptions.RequestException as e:
#     print(f"API Error: {e}")

# Practical Example: Weather API (pseudocode)
"""
def get_weather(city):
    api_key = 'your_api_key'
    url = f'https://api.openweathermap.org/data/2.5/weather?q={city}&appid={api_key}'
    
    response = requests.get(url)
    if response.status_code == 200:
        data = response.json()
        return {
            'city': data['name'],
            'temperature': data['main']['temp'] - 273.15,  # Kelvin to Celsius
            'description': data['weather'][0]['description']
        }
    return None
"""

# ============================================================
# 20. DATABASE OPERATIONS
# ============================================================

"""
Python can work with various databases:
    - SQLite (built-in, file-based)
    - MySQL (pip install mysql-connector-python)
    - PostgreSQL (pip install psycopg2)
    - MongoDB (pip install pymongo)

We'll use SQLite as it's built into Python.
"""

import sqlite3

# --- Connect to database (creates if not exists) ---
# conn = sqlite3.connect('my_database.db')  # File-based
# conn = sqlite3.connect(':memory:')  # In-memory (for testing)

# Using in-memory database for demonstration
conn = sqlite3.connect(':memory:')
cursor = conn.cursor()

# --- Create Table ---
cursor.execute('''
    CREATE TABLE IF NOT EXISTS users (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        email TEXT UNIQUE,
        age INTEGER,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    )
''')
conn.commit()

# --- INSERT data ---
cursor.execute('''
    INSERT INTO users (name, email, age) VALUES (?, ?, ?)
''', ('Ashutosh', 'ashutosh@email.com', 25))

# Insert multiple rows
users_data = [
    ('Priya', 'priya@email.com', 22),
    ('Rahul', 'rahul@email.com', 28),
    ('Sneha', 'sneha@email.com', 24)
]
cursor.executemany('INSERT INTO users (name, email, age) VALUES (?, ?, ?)', users_data)
conn.commit()

# --- SELECT data ---
# Fetch all
cursor.execute('SELECT * FROM users')
all_users = cursor.fetchall()
print("All users:", all_users)

# Fetch one
cursor.execute('SELECT * FROM users WHERE id = ?', (1,))
user = cursor.fetchone()
print("User 1:", user)

# Fetch with condition
cursor.execute('SELECT name, age FROM users WHERE age > ?', (23,))
older_users = cursor.fetchall()
print("Users older than 23:", older_users)

# --- UPDATE data ---
cursor.execute('''
    UPDATE users SET age = ? WHERE name = ?
''', (26, 'Ashutosh'))
conn.commit()

# --- DELETE data ---
# cursor.execute('DELETE FROM users WHERE id = ?', (4,))
# conn.commit()

# --- Using context manager (recommended) ---
def get_all_users():
    with sqlite3.connect(':memory:') as conn:
        cursor = conn.cursor()
        cursor.execute('SELECT * FROM users')
        return cursor.fetchall()

# --- Close connection ---
conn.close()

# ============================================================
# 21. ADDITIONAL ADVANCED TOPICS
# ============================================================

# --- Context Managers (with statement) ---
"""
Context managers handle setup and cleanup automatically.
Useful for files, database connections, locks, etc.
"""

class FileManager:
    def __init__(self, filename, mode):
        self.filename = filename
        self.mode = mode
        self.file = None
    
    def __enter__(self):
        self.file = open(self.filename, self.mode)
        return self.file
    
    def __exit__(self, exc_type, exc_val, exc_tb):
        if self.file:
            self.file.close()

# Usage:
# with FileManager('test.txt', 'w') as f:
#     f.write('Hello!')

# Using contextlib (simpler way)
from contextlib import contextmanager

@contextmanager
def timer_context():
    start = time.time()
    yield
    end = time.time()
    print(f"Elapsed: {end - start:.4f} seconds")

# with timer_context():
#     time.sleep(0.5)  # Some operation

# --- Type Hints (Python 3.5+) ---
"""
Type hints make code more readable and help with IDE autocomplete.
They don't enforce types at runtime (use mypy for static checking).
"""

def greet_typed(name: str, age: int) -> str:
    return f"Hello {name}, you are {age} years old"

def process_items(items: list[str]) -> dict[str, int]:
    return {item: len(item) for item in items}

from typing import Optional, Union, List, Dict

def find_user(user_id: int) -> Optional[dict]:
    """Returns user dict or None if not found"""
    users = {1: {"name": "Ashutosh"}}
    return users.get(user_id)

def process_value(value: Union[int, str]) -> str:
    """Accepts either int or str"""
    return str(value)

# --- Dataclasses (Python 3.7+) ---
"""
Dataclasses reduce boilerplate for classes that mainly store data.
"""

from dataclasses import dataclass, field

@dataclass
class Product:
    name: str
    price: float
    quantity: int = 0
    
    def total_value(self) -> float:
        return self.price * self.quantity

product = Product("Laptop", 999.99, 5)
print(product)  # Product(name='Laptop', price=999.99, quantity=5)
print(product.total_value())  # 4999.95

# With default factory for mutable defaults
@dataclass
class ShoppingCart:
    items: List[Product] = field(default_factory=list)
    
    def add_item(self, item: Product):
        self.items.append(item)
    
    def total(self) -> float:
        return sum(item.total_value() for item in self.items)

# --- Async/Await (Asynchronous Programming) ---
"""
Async programming allows concurrent execution of I/O-bound tasks.
Useful for web scraping, API calls, file operations.
"""

import asyncio

async def fetch_data(delay: int, name: str) -> str:
    print(f"Starting {name}...")
    await asyncio.sleep(delay)  # Simulates I/O operation
    print(f"Completed {name}")
    return f"Data from {name}"

async def main():
    # Run tasks concurrently
    results = await asyncio.gather(
        fetch_data(2, "Task 1"),
        fetch_data(1, "Task 2"),
        fetch_data(3, "Task 3")
    )
    print(f"Results: {results}")

# Run async code
# asyncio.run(main())

# ============================================================
# END OF ADVANCED TOPICS
# ============================================================

print("\n" + "="*50)
print("🚀 Amazing! You've now covered Advanced Python!")
print("="*50)
print("""
You've learned:
✅ Object-Oriented Programming
✅ Modules and Packages  
✅ Decorators
✅ Generators
✅ Regular Expressions
✅ Working with APIs
✅ Database Operations
✅ Context Managers
✅ Type Hints
✅ Dataclasses
✅ Async/Await

Next Steps:
• Practice building real projects
• Explore frameworks (Django, Flask, FastAPI)
• Learn testing (pytest, unittest)
• Study design patterns
• Contribute to open source

Happy Coding! 🐍
""")