package com.learning.java_interview;

/**
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                                                                                                ║
 * ║                                    OOP CONCEPTS - INTERVIEW DEEP DIVE                                          ║
 * ║                                                                                                                ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                        1. ENCAPSULATION                                                        ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * DEFINITION:
 * Encapsulation is the bundling of data (fields) and methods that operate on the data into a single unit (class),
 * while restricting direct access to some of the object's components.
 *
 * KEY POINTS:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ • Data Hiding: Private fields, accessed only through public methods                                            │
 * │ • Controlled Access: Getters/Setters with validation logic                                                     │
 * │ • Flexibility: Internal implementation can change without affecting external code                              │
 * │ • Maintainability: Changes are localized within the class                                                      │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * INTERVIEW QUESTION: Why encapsulation is important?
 * ANSWER:
 * 1. Security: Prevent unauthorized access to data
 * 2. Flexibility: Change implementation without breaking client code
 * 3. Validation: Add validation in setters before modifying data
 * 4. Read-only/Write-only: Create immutable objects or write-only fields
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                        2. ABSTRACTION                                                          ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * DEFINITION:
 * Abstraction is the concept of hiding implementation complexity and showing only the essential features.
 *
 * ACHIEVED THROUGH:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ 1. ABSTRACT CLASSES                          │ 2. INTERFACES                                                   │
 * │    • Can have abstract + concrete methods    │    • Only abstract methods (before Java 8)                      │
 * │    • Can have constructors                   │    • Cannot have constructors                                   │
 * │    • Can have instance variables             │    • Only public static final variables                         │
 * │    • Single inheritance only                 │    • Multiple inheritance allowed                               │
 * │    • Use 'extends' keyword                   │    • Use 'implements' keyword                                   │
 * │    • Can have any access modifier            │    • Methods are public by default                              │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * INTERVIEW QUESTION: When to use Abstract Class vs Interface?
 * ANSWER:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ Use ABSTRACT CLASS when:                     │ Use INTERFACE when:                                             │
 * │ • Classes share common state (fields)        │ • Defining a contract/capability                                │
 * │ • Need constructors                          │ • Multiple inheritance is needed                                │
 * │ • Want to provide default implementations    │ • Unrelated classes need same behavior                          │
 * │ • Close relationship ("is-a")                │ • Loose coupling required                                       │
 * │ Example: Animal (Dog, Cat share properties)  │ Example: Comparable, Serializable                               │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                        3. INHERITANCE                                                          ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * DEFINITION:
 * Inheritance is a mechanism where one class acquires properties and behaviors of another class.
 *
 * TYPES OF INHERITANCE:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │  1. SINGLE              2. MULTILEVEL           3. HIERARCHICAL           4. MULTIPLE (Interface only)        │
 * │                                                                                                                │
 * │      [A]                    [A]                      [A]                    [A]     [B]                        │
 * │       │                      │                      / │ \                     \     /                          │
 * │       ▼                      ▼                     /  │  \                     \   /                           │
 * │      [B]                    [B]                  [B] [C] [D]                    [C]                            │
 * │                              │                                           (implements A, B)                    │
 * │                              ▼                                                                                 │
 * │                             [C]                                                                                │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * DIAMOND PROBLEM:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │        [A]  void display()                  Java Solution:                                                     │
 * │       /   \                                 • No multiple class inheritance                                    │
 * │      /     \                                • Interfaces with default methods:                                 │
 * │    [B]     [C]  both override display()       → Must override in implementing class                           │
 * │      \     /                                  → Or call specific: InterfaceA.super.method()                   │
 * │       \   /                                                                                                    │
 * │        [D]  Which display() to call?                                                                           │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                        4. POLYMORPHISM                                                         ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * DEFINITION:
 * Polymorphism means "many forms" - ability of an object to take many forms.
 *
 * TYPES:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │  1. COMPILE-TIME POLYMORPHISM (Static Binding)     2. RUNTIME POLYMORPHISM (Dynamic Binding)                  │
 * │     • Method Overloading                              • Method Overriding                                      │
 * │     • Operator Overloading (limited)                  • Resolved at runtime                                    │
 * │     • Resolved at compile time                        • Uses virtual method table (vtable)                     │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * METHOD OVERLOADING RULES:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ ✓ Different number of parameters                                                                               │
 * │ ✓ Different types of parameters                                                                                │
 * │ ✓ Different order of parameters                                                                                │
 * │ ✗ Cannot overload by return type alone                                                                         │
 * │ ✗ Cannot overload by access modifier alone                                                                     │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * METHOD OVERRIDING RULES:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ ✓ Same method signature (name + parameters)                                                                    │
 * │ ✓ Return type must be same or covariant (subtype)                                                              │
 * │ ✓ Access modifier must be same or less restrictive                                                             │
 * │ ✓ Can throw same, narrower, or no checked exceptions                                                           │
 * │ ✗ Cannot override final methods                                                                                │
 * │ ✗ Cannot override static methods (hiding, not overriding)                                                      │
 * │ ✗ Cannot override private methods                                                                              │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    5. INTERVIEW TRICKY QUESTIONS                                               ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * Q1: Can we override static methods?
 * A: No, static methods are hidden, not overridden. They belong to class, resolved at compile time.
 *
 * Q2: Can we override private methods?
 * A: No, private methods are not inherited, so cannot be overridden.
 *
 * Q3: Can constructor be inherited?
 * A: No, constructors are not inherited. But subclass constructor calls super() implicitly.
 *
 * Q4: What is covariant return type?
 * A: Overriding method can return subtype of original return type (Java 5+).
 *
 * Q5: Can we make constructor final?
 * A: No, constructors cannot be final, abstract, static, or synchronized.
 *
 * Q6: What is method hiding?
 * A: When subclass defines static method with same signature as parent's static method.
 *
 * Q7: Can abstract class have constructor?
 * A: Yes, called when subclass is instantiated.
 *
 * Q8: Can interface have constructor?
 * A: No, interfaces cannot have constructors.
 *
 * Q9: What is marker interface?
 * A: Interface with no methods, used to mark capability (e.g., Serializable, Cloneable).
 *
 * Q10: Difference between IS-A and HAS-A?
 * A: IS-A = Inheritance (Dog IS-A Animal), HAS-A = Composition (Car HAS-A Engine)
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                   6. EQUALS AND HASHCODE CONTRACT                                              ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * THE CONTRACT:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ 1. If a.equals(b) is TRUE, then a.hashCode() MUST equal b.hashCode()                                           │
 * │                                                                                                                │
 * │ 2. If a.hashCode() == b.hashCode(), equals() may or may not be true (collision possible)                       │
 * │                                                                                                                │
 * │ 3. If a.equals(b) is FALSE, hashCode() may or may not be different (but should be for performance)             │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * WHY IMPORTANT?
 * • HashMap, HashSet use hashCode() first to find bucket, then equals() to find exact object
 * • Breaking the contract = broken behavior in hash-based collections
 *
 * EXAMPLE OF BROKEN CONTRACT:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  // BAD: Override equals without hashCode                                                                      │
 * │  class BadKey {                                                                                                │
 * │      int id;                                                                                                   │
 * │      @Override                                                                                                 │
 * │      public boolean equals(Object o) { return id == ((BadKey)o).id; }                                          │
 * │      // hashCode() uses default Object.hashCode() - different for different instances!                        │
 * │  }                                                                                                             │
 * │                                                                                                                │
 * │  HashMap<BadKey, String> map = new HashMap<>();                                                                │
 * │  BadKey key1 = new BadKey(1);                                                                                  │
 * │  map.put(key1, "value");                                                                                       │
 * │  BadKey key2 = new BadKey(1);  // Same id, equals() returns true                                               │
 * │  map.get(key2);  // Returns NULL! Different hashCode, different bucket                                         │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * @author Java Interview Guide
 */
public class _01_OOPConcepts {

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════");
        System.out.println("                          OOP CONCEPTS DEMONSTRATION");
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════\n");

        // 1. Encapsulation Demo
        demonstrateEncapsulation();

        // 2. Abstraction Demo
        demonstrateAbstraction();

        // 3. Inheritance Demo
        demonstrateInheritance();

        // 4. Polymorphism Demo
        demonstratePolymorphism();

        // 5. equals/hashCode Demo
        demonstrateEqualsHashCode();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                              1. ENCAPSULATION DEMO
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateEncapsulation() {
        System.out.println("▶ 1. ENCAPSULATION DEMO:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        BankAccount account = new BankAccount("John", 1000);
        System.out.println("   Initial Balance: $" + account.getBalance());

        account.deposit(500);
        System.out.println("   After deposit $500: $" + account.getBalance());

        account.withdraw(200);
        System.out.println("   After withdraw $200: $" + account.getBalance());

        // account.balance = -5000;  // ❌ Cannot access - encapsulated!
        account.withdraw(5000);  // Validation in setter prevents negative balance
        System.out.println("   After invalid withdraw $5000: $" + account.getBalance());
        System.out.println();
    }

    /**
     * Well-encapsulated class with validation
     */
    static class BankAccount {
        private String owner;           // Private - hidden from outside
        private double balance;         // Private - cannot be directly modified

        public BankAccount(String owner, double initialBalance) {
            this.owner = owner;
            this.balance = initialBalance;
        }

        // Controlled access with validation
        public void deposit(double amount) {
            if (amount > 0) {
                balance += amount;
            }
        }

        public void withdraw(double amount) {
            if (amount > 0 && amount <= balance) {
                balance -= amount;
            } else {
                System.out.println("   [Invalid withdrawal rejected]");
            }
        }

        public double getBalance() {
            return balance;
        }

        public String getOwner() {
            return owner;
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                              2. ABSTRACTION DEMO
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateAbstraction() {
        System.out.println("▶ 2. ABSTRACTION DEMO:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // Using abstract class
        Shape circle = new Circle(5);
        Shape rectangle = new Rectangle(4, 6);

        System.out.println("   Circle area: " + circle.area());
        System.out.println("   Rectangle area: " + rectangle.area());

        // Using interface
        Drawable drawableCircle = new Circle(5);
        drawableCircle.draw();  // Circle knows HOW to draw itself

        System.out.println();
    }

    // Interface - defines WHAT to do (contract)
    interface Drawable {
        void draw();

        // Default method (Java 8+)
        default void drawWithBorder() {
            System.out.println("   [Drawing with border...]");
            draw();
        }
    }

    // Abstract class - defines partial implementation
    abstract static class Shape {
        protected String name;

        // Constructor in abstract class
        public Shape(String name) {
            this.name = name;
        }

        // Abstract method - MUST be implemented by subclass
        public abstract double area();

        // Concrete method - inherited as-is
        public void displayName() {
            System.out.println("   Shape: " + name);
        }
    }

    static class Circle extends Shape implements Drawable {
        private double radius;

        public Circle(double radius) {
            super("Circle");
            this.radius = radius;
        }

        @Override
        public double area() {
            return Math.PI * radius * radius;
        }

        @Override
        public void draw() {
            System.out.println("   Drawing circle with radius: " + radius);
        }
    }

    static class Rectangle extends Shape {
        private double width, height;

        public Rectangle(double width, double height) {
            super("Rectangle");
            this.width = width;
            this.height = height;
        }

        @Override
        public double area() {
            return width * height;
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                              3. INHERITANCE DEMO
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateInheritance() {
        System.out.println("▶ 3. INHERITANCE DEMO:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        Dog dog = new Dog("Buddy");
        dog.eat();      // Inherited from Animal
        dog.bark();     // Own method

        Cat cat = new Cat("Whiskers");
        cat.eat();      // Inherited from Animal
        cat.meow();     // Own method

        System.out.println();
    }

    static class Animal {
        protected String name;

        public Animal(String name) {
            this.name = name;
            System.out.println("   Animal constructor called for: " + name);
        }

        public void eat() {
            System.out.println("   " + name + " is eating");
        }
    }

    static class Dog extends Animal {
        public Dog(String name) {
            super(name);  // Must call parent constructor
            System.out.println("   Dog constructor called");
        }

        public void bark() {
            System.out.println("   " + name + " says: Woof!");
        }
    }

    static class Cat extends Animal {
        public Cat(String name) {
            super(name);
        }

        public void meow() {
            System.out.println("   " + name + " says: Meow!");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                              4. POLYMORPHISM DEMO
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstratePolymorphism() {
        System.out.println("▶ 4. POLYMORPHISM DEMO:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // Compile-time polymorphism (Overloading)
        Calculator calc = new Calculator();
        System.out.println("   add(5, 3) = " + calc.add(5, 3));
        System.out.println("   add(5, 3, 2) = " + calc.add(5, 3, 2));
        System.out.println("   add(5.5, 3.3) = " + calc.add(5.5, 3.3));

        // Runtime polymorphism (Overriding)
        System.out.println("\n   Runtime Polymorphism:");
        Vehicle vehicle1 = new Car();
        Vehicle vehicle2 = new Bike();

        vehicle1.start();  // Car's start() - resolved at runtime
        vehicle2.start();  // Bike's start() - resolved at runtime

        System.out.println();
    }

    // Method Overloading
    static class Calculator {
        public int add(int a, int b) {
            return a + b;
        }

        public int add(int a, int b, int c) {  // Different number of params
            return a + b + c;
        }

        public double add(double a, double b) {  // Different type of params
            return a + b;
        }
    }

    // Method Overriding
    static class Vehicle {
        public void start() {
            System.out.println("   Vehicle starting...");
        }
    }

    static class Car extends Vehicle {
        @Override
        public void start() {
            System.out.println("   Car starting with ignition...");
        }
    }

    static class Bike extends Vehicle {
        @Override
        public void start() {
            System.out.println("   Bike starting with kick...");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                        5. EQUALS AND HASHCODE DEMO
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateEqualsHashCode() {
        System.out.println("▶ 5. EQUALS AND HASHCODE DEMO:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        Employee emp1 = new Employee(1, "John");
        Employee emp2 = new Employee(1, "John");
        Employee emp3 = emp1;

        System.out.println("   emp1 == emp2 (reference): " + (emp1 == emp2));         // false
        System.out.println("   emp1.equals(emp2) (content): " + emp1.equals(emp2));   // true
        System.out.println("   emp1 == emp3 (same reference): " + (emp1 == emp3));    // true

        System.out.println("\n   emp1.hashCode(): " + emp1.hashCode());
        System.out.println("   emp2.hashCode(): " + emp2.hashCode());
        System.out.println("   HashCodes equal: " + (emp1.hashCode() == emp2.hashCode()));

        // HashMap demo
        java.util.HashMap<Employee, String> map = new java.util.HashMap<>();
        map.put(emp1, "Manager");
        System.out.println("\n   Getting value with emp2: " + map.get(emp2));  // Works because of proper hashCode!

        System.out.println();
    }

    static class Employee {
        private int id;
        private String name;

        public Employee(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Employee employee = (Employee) o;
            return id == employee.id &&
                    java.util.Objects.equals(name, employee.name);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(id, name);
        }
    }
}

