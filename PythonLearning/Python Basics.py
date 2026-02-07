# ============================================================
#                    PYTHON BASICS - COMPLETE GUIDE
# ============================================================

# ============================================================
# 1. PRINT STATEMENTS & COMMENTS
# ============================================================

print("Hello, World!")  # This is a single-line comment

"""
This is a 
multi-line comment
(also called docstring)
"""

# ============================================================
# 2. VARIABLES & DATA TYPES
# ============================================================

# Strings (text)
name = "Ashutosh"
greeting = 'Hello'  # Single or double quotes both work

# Integers (whole numbers)
age = 25
year = 2024

# Floats (decimal numbers)
height = 5.9
price = 99.99

# Booleans (True/False)
is_learning = True
is_expert = False

# None (represents "nothing" or "no value")
result = None

# Check types with type()
print(type(name))       # <class 'str'>
print(type(age))        # <class 'int'>
print(type(height))     # <class 'float'>
print(type(is_learning)) # <class 'bool'>

# ============================================================
# 3. STRING OPERATIONS
# ============================================================

text = "Python Programming"

# String methods
print(text.lower())        # python programming
print(text.upper())        # PYTHON PROGRAMMING
print(text.replace("Python", "Java"))  # Java Programming
print(len(text))           # 18 (length)

# String slicing [start:end:step]
print(text[0])             # P (first character)
print(text[-1])            # g (last character)
print(text[0:6])           # Python (first 6 chars)
print(text[::-1])          # gnimmargorP nohtyP (reversed)

# String formatting (f-strings - recommended!)
name = "Ashutosh"
age = 25
print(f"My name is {name} and I am {age} years old")

# String concatenation
first = "Hello"
second = "World"
combined = first + " " + second  # Hello World

# ============================================================
# 4. OPERATORS
# ============================================================

# Arithmetic Operators
a, b = 10, 3
print(a + b)    # 13  - Addition
print(a - b)    # 7   - Subtraction
print(a * b)    # 30  - Multiplication
print(a / b)    # 3.33 - Division
print(a // b)   # 3   - Floor Division (no decimal)
print(a % b)    # 1   - Modulus (remainder)
print(a ** b)   # 1000 - Exponent (10³)

# Comparison Operators (return True/False)
x, y = 10, 5
print(x == y)   # False - Equal
print(x != y)   # True  - Not Equal
print(x > y)    # True  - Greater than
print(x < y)    # False - Less than
print(x >= y)   # True  - Greater or equal
print(x <= y)   # False - Less or equal

# Logical Operators
print(True and False)   # False - Both must be True
print(True or False)    # True  - At least one True
print(not True)         # False - Opposite

# Assignment Operators (shortcuts)
num = 10
num += 5    # num = num + 5 → 15
num -= 3    # num = num - 3 → 12
num *= 2    # num = num * 2 → 24
num /= 4    # num = num / 4 → 6.0

# ============================================================
# 5. CONTROL FLOW (if/elif/else)
# ============================================================

age = 18

if age < 13:
    print("You are a child")
elif age < 20:
    print("You are a teenager")
elif age < 60:
    print("You are an adult")
else:
    print("You are a senior")

# One-liner (Ternary operator)
status = "Adult" if age >= 18 else "Minor"
print(status)

# Multiple conditions
score = 85
if score >= 90 and score <= 100:
    grade = "A"
elif score >= 80:
    grade = "B"
elif score >= 70:
    grade = "C"
else:
    grade = "F"
print(f"Grade: {grade}")

# ============================================================
# 6. LOOPS
# ============================================================

# FOR LOOP - iterate over a sequence
print("--- For Loop ---")
fruits = ["apple", "banana", "cherry"]
for fruit in fruits:
    print(fruit)

# For loop with range
print("--- Range Loop ---")
for i in range(5):       # 0, 1, 2, 3, 4
    print(i)

for i in range(2, 6):    # 2, 3, 4, 5
    print(i)

for i in range(0, 10, 2): # 0, 2, 4, 6, 8 (step of 2)
    print(i)

# WHILE LOOP - repeat while condition is True
print("--- While Loop ---")
count = 0
while count < 5:
    print(count)
    count += 1

# Loop control statements
print("--- Break & Continue ---")
for i in range(10):
    if i == 3:
        continue    # Skip this iteration
    if i == 7:
        break       # Exit the loop
    print(i)        # Prints: 0, 1, 2, 4, 5, 6

# ============================================================
# 7. DATA STRUCTURES
# ============================================================

# --- LISTS (ordered, mutable, allows duplicates) ---
print("--- Lists ---")
fruits = ["apple", "banana", "cherry"]
numbers = [1, 2, 3, 4, 5]
mixed = [1, "hello", 3.14, True]

# List operations
fruits.append("orange")      # Add to end
fruits.insert(1, "mango")    # Insert at position
fruits.remove("banana")      # Remove by value
popped = fruits.pop()        # Remove & return last item
print(fruits[0])             # Access by index
print(len(fruits))           # Length
print("apple" in fruits)     # Check if exists → True

# List slicing
nums = [0, 1, 2, 3, 4, 5]
print(nums[1:4])    # [1, 2, 3]
print(nums[:3])     # [0, 1, 2]
print(nums[3:])     # [3, 4, 5]

# --- TUPLES (ordered, immutable, allows duplicates) ---
print("--- Tuples ---")
coordinates = (10, 20)
colors = ("red", "green", "blue")
print(coordinates[0])   # 10
# coordinates[0] = 5    # ERROR! Tuples can't be changed

# Tuple unpacking
x, y = coordinates
print(f"x={x}, y={y}")

# --- DICTIONARIES (key-value pairs, unordered, mutable) ---
print("--- Dictionaries ---")
person = {
    "name": "Ashutosh",
    "age": 25,
    "city": "Delhi"
}

# Dictionary operations
print(person["name"])           # Access by key
print(person.get("age"))        # Safe access (returns None if not found)
person["email"] = "a@email.com" # Add new key-value
person["age"] = 26              # Update value
del person["city"]              # Delete key
print(person.keys())            # All keys
print(person.values())          # All values
print(person.items())           # All key-value pairs

# Loop through dictionary
for key, value in person.items():
    print(f"{key}: {value}")

# --- SETS (unordered, no duplicates) ---
print("--- Sets ---")
unique_nums = {1, 2, 3, 3, 4}  # Duplicates auto-removed → {1, 2, 3, 4}
fruits_set = {"apple", "banana", "cherry"}

fruits_set.add("orange")
fruits_set.remove("banana")
print("apple" in fruits_set)   # True

# Set operations
set1 = {1, 2, 3}
set2 = {2, 3, 4}
print(set1 | set2)   # Union: {1, 2, 3, 4}
print(set1 & set2)   # Intersection: {2, 3}
print(set1 - set2)   # Difference: {1}

# ============================================================
# 8. FUNCTIONS
# ============================================================

# Basic function
def greet():
    print("Hello!")

greet()  # Call the function

# Function with parameters
def greet_person(name):
    print(f"Hello, {name}!")

greet_person("Ashutosh")

# Function with return value
def add(a, b):
    return a + b

result = add(5, 3)
print(result)  # 8

# Default parameters
def greet_with_default(name="Guest"):
    print(f"Hello, {name}!")

greet_with_default()           # Hello, Guest!
greet_with_default("Ashutosh") # Hello, Ashutosh!

# Multiple return values
def get_stats(numbers):
    return min(numbers), max(numbers), sum(numbers)

minimum, maximum, total = get_stats([1, 2, 3, 4, 5])
print(f"Min: {minimum}, Max: {maximum}, Total: {total}")

# *args (variable number of arguments)
def sum_all(*numbers):
    return sum(numbers)

print(sum_all(1, 2, 3, 4, 5))  # 15

# **kwargs (keyword arguments)
def print_info(**info):
    for key, value in info.items():
        print(f"{key}: {value}")

print_info(name="Ashutosh", age=25, city="Delhi")

# Lambda functions (anonymous/one-liner functions)
square = lambda x: x ** 2
print(square(5))  # 25

add = lambda a, b: a + b
print(add(3, 4))  # 7

# ============================================================
# 9. LIST COMPREHENSIONS (Elegant way to create lists)
# ============================================================

# Traditional way
squares = []
for i in range(5):
    squares.append(i ** 2)

# List comprehension way
squares = [i ** 2 for i in range(5)]  # [0, 1, 4, 9, 16]

# With condition
evens = [i for i in range(10) if i % 2 == 0]  # [0, 2, 4, 6, 8]

# Dictionary comprehension
squares_dict = {i: i**2 for i in range(5)}  # {0:0, 1:1, 2:4, 3:9, 4:16}

# ============================================================
# 10. USER INPUT
# ============================================================

# Uncomment to test (will pause for input)
# name = input("Enter your name: ")
# print(f"Hello, {name}!")

# age = int(input("Enter your age: "))  # Convert to integer
# height = float(input("Enter height: ")) # Convert to float

# ============================================================
# 11. ERROR HANDLING (try/except)
# ============================================================

try:
    result = 10 / 0
except ZeroDivisionError:
    print("Cannot divide by zero!")
except Exception as e:
    print(f"An error occurred: {e}")
else:
    print("No errors!")  # Runs if no exception
finally:
    print("This always runs")  # Cleanup code

# Practical example
def safe_divide(a, b):
    try:
        return a / b
    except ZeroDivisionError:
        return "Cannot divide by zero"

print(safe_divide(10, 2))   # 5.0
print(safe_divide(10, 0))   # Cannot divide by zero

# ============================================================
# 12. FILE HANDLING
# ============================================================

# Writing to a file
# with open("example.txt", "w") as file:
#     file.write("Hello, World!\n")
#     file.write("This is Python!")

# Reading from a file
# with open("example.txt", "r") as file:
#     content = file.read()
#     print(content)

# Reading line by line
# with open("example.txt", "r") as file:
#     for line in file:
#         print(line.strip())

# Append to file
# with open("example.txt", "a") as file:
#     file.write("\nAppended line")

# ============================================================
# 13. USEFUL BUILT-IN FUNCTIONS
# ============================================================

numbers = [3, 1, 4, 1, 5, 9, 2, 6]

print(len(numbers))      # 8 - Length
print(min(numbers))      # 1 - Minimum
print(max(numbers))      # 9 - Maximum
print(sum(numbers))      # 31 - Sum
print(sorted(numbers))   # [1, 1, 2, 3, 4, 5, 6, 9] - Sorted copy
print(abs(-5))           # 5 - Absolute value
print(round(3.7))        # 4 - Round
print(pow(2, 3))         # 8 - Power (2³)

# Enumerate (get index and value)
fruits = ["apple", "banana", "cherry"]
for index, fruit in enumerate(fruits):
    print(f"{index}: {fruit}")

# Zip (combine multiple lists)
names = ["Alice", "Bob"]
ages = [25, 30]
for name, age in zip(names, ages):
    print(f"{name} is {age}")

# Map (apply function to all items)
nums = [1, 2, 3, 4]
squared = list(map(lambda x: x**2, nums))  # [1, 4, 9, 16]

# Filter (keep items matching condition)
nums = [1, 2, 3, 4, 5, 6]
evens = list(filter(lambda x: x % 2 == 0, nums))  # [2, 4, 6]

# ============================================================
# END OF BASICS - You're ready for Advanced Topics!
# ============================================================

print("\n" + "="*50)
print("🎉 Congratulations! You've covered Python Basics!")
print("="*50)
print("""
Next: Advanced Topics
1. Object-Oriented Programming (Classes)
2. Modules and Packages
3. Decorators
4. Generators
5. Regular Expressions
6. Working with APIs
7. Database Operations
8. And more!
""")