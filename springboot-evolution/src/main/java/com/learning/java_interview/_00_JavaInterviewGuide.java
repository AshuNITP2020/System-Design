package com.learning.java_interview;

/**
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                                                                                                ║
 * ║                     JAVA INTERVIEW PREPARATION GUIDE - 4 YEARS EXPERIENCE                                      ║
 * ║                                                                                                                ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * This comprehensive package covers all essential Java topics expected from a 4-year experienced developer.
 *
 * ┌────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                        PACKAGE STRUCTURE                                                       │
 * ├────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
 * │                                                                                                                │
 * │  📁 com.learning.java_interview                                                                                │
 * │  │                                                                                                             │
 * │  ├── 📄 _00_JavaInterviewGuide.java        ← YOU ARE HERE (Overview & Roadmap)                                 │
 * │  │                                                                                                             │
 * │  ├── 📄 _01_OOPConcepts.java               ← OOP: Abstraction, Encapsulation, Inheritance, Polymorphism        │
 * │  │                                                                                                             │
 * │  ├── 📄 _02_CollectionsFramework.java      ← List, Set, Map, Queue + Internal Working                          │
 * │  │                                                                                                             │
 * │  ├── 📄 _03_MultithreadingConcurrency.java ← Threads, Executors, Locks, Concurrent Collections                 │
 * │  │                                                                                                             │
 * │  ├── 📄 _04_Java8Features.java             ← Lambda, Streams, Optional, Functional Interfaces                  │
 * │  │                                                                                                             │
 * │  ├── 📄 _05_ExceptionHandling.java         ← Checked/Unchecked, Best Practices, Custom Exceptions              │
 * │  │                                                                                                             │
 * │  ├── 📄 _06_StringAndImmutability.java     ← String Pool, StringBuilder, Immutability                          │
 * │  │                                                                                                             │
 * │  ├── 📄 _07_DesignPatterns.java            ← Singleton, Factory, Builder, Observer, Strategy                   │
 * │  │                                                                                                             │
 * │  ├── 📄 _08_SOLIDPrinciples.java           ← SOLID with Real-World Examples                                    │
 * │  │                                                                                                             │
 * │  └── 📄 _09_CommonCodingQuestions.java     ← Frequently Asked Coding Problems                                  │
 * │                                                                                                                │
 * └────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    INTERVIEW EXPECTATIONS BY EXPERIENCE                                        ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * ┌───────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │ EXPERIENCE │              EXPECTED SKILLS                           │           INTERVIEW FOCUS              │
 * ├────────────┼────────────────────────────────────────────────────────┼────────────────────────────────────────┤
 * │ 0-2 Years  │ • Core Java basics                                     │ • OOP concepts                         │
 * │ (Fresher)  │ • Collections basics                                   │ • Basic data structures                │
 * │            │ • Exception handling                                   │ • Simple coding problems               │
 * │            │ • Basic multithreading                                 │ • String manipulation                  │
 * ├────────────┼────────────────────────────────────────────────────────┼────────────────────────────────────────┤
 * │ 2-4 Years  │ • Java 8 features (Streams, Lambda)                    │ • Internal working of collections      │
 * │ (Junior)   │ • Collections internal working                         │ • Multithreading scenarios             │
 * │            │ • Concurrent programming                               │ • Design patterns                      │
 * │            │ • Basic design patterns                                │ • Exception best practices             │
 * ├────────────┼────────────────────────────────────────────────────────┼────────────────────────────────────────┤
 * │ 4-6 Years  │ • Advanced multithreading                              │ • System design basics                 │
 * │ (Mid-Sr)   │ • Performance tuning                                   │ • Memory management & GC               │
 * │    ★       │ • Memory management & GC                               │ • SOLID principles                     │
 * │ YOU ARE    │ • SOLID principles                                     │ • Complex coding problems              │
 * │   HERE     │ • Design patterns in practice                          │ • Code review scenarios                │
 * │            │ • Code optimization                                    │ • Real-world problem solving           │
 * ├────────────┼────────────────────────────────────────────────────────┼────────────────────────────────────────┤
 * │ 6+ Years   │ • Architecture decisions                               │ • System design                        │
 * │ (Senior)   │ • Technology evaluation                                │ • Leadership scenarios                 │
 * │            │ • Mentoring ability                                    │ • Trade-off discussions                │
 * │            │ • Cross-cutting concerns                               │ • Performance optimization             │
 * └────────────┴────────────────────────────────────────────────────────┴────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                          QUICK REVISION TOPICS                                                 ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                         1. OOP PILLARS (Quick Reference)                                       │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  ┌──────────────────┬───────────────────────────────────────────────────────────────────────────────────────────┐
 *  │ ENCAPSULATION    │ Bundling data (fields) and methods that operate on the data into a single unit (class)   │
 *  │                  │ Hide internal state, expose through getters/setters                                       │
 *  │                  │ Example: Private fields with public methods                                               │
 *  ├──────────────────┼───────────────────────────────────────────────────────────────────────────────────────────┤
 *  │ ABSTRACTION      │ Hiding implementation complexity, showing only essential features                         │
 *  │                  │ Achieved through: Abstract classes and Interfaces                                         │
 *  │                  │ Example: Car interface - start(), stop() without knowing engine internals                 │
 *  ├──────────────────┼───────────────────────────────────────────────────────────────────────────────────────────┤
 *  │ INHERITANCE      │ Mechanism where one class acquires properties and behaviors of another                    │
 *  │                  │ Types: Single, Multilevel, Hierarchical (NO multiple inheritance with classes)            │
 *  │                  │ Example: Employee extends Person                                                          │
 *  ├──────────────────┼───────────────────────────────────────────────────────────────────────────────────────────┤
 *  │ POLYMORPHISM     │ Same interface, different implementations                                                 │
 *  │                  │ Compile-time: Method Overloading (same method name, different parameters)                 │
 *  │                  │ Runtime: Method Overriding (subclass provides specific implementation)                    │
 *  └──────────────────┴───────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                     2. COLLECTIONS HIERARCHY (Quick Reference)                                 │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *                                              Iterable (I)
 *                                                  │
 *                                             Collection (I)
 *                                     ┌───────────┼───────────┐
 *                                     │           │           │
 *                                  List (I)    Set (I)    Queue (I)
 *                                     │           │           │
 *                         ┌───────────┼───────┐   │     ┌─────┴─────┐
 *                         │           │       │   │     │           │
 *                     ArrayList  LinkedList Vector│  PriorityQ  Deque (I)
 *                                             │   │               │
 *                                          Stack  │          ArrayDeque
 *                                                 │
 *                                    ┌────────────┼────────────┐
 *                                    │            │            │
 *                                 HashSet    TreeSet    LinkedHashSet
 *                                    │
 *                              LinkedHashSet
 *
 *
 *                                              Map (I) [Not part of Collection]
 *                                     ┌───────────┼───────────┐
 *                                     │           │           │
 *                                  HashMap    TreeMap    LinkedHashMap
 *                                     │
 *                              LinkedHashMap
 *                                     │
 *                                HashTable (legacy)
 *                                     │
 *                               ConcurrentHashMap
 *
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                         3. ACCESS MODIFIERS                                                    │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  ┌───────────────┬───────────────┬───────────────┬───────────────┬───────────────┐
 *  │   Modifier    │  Same Class   │ Same Package  │   Subclass    │   Everywhere  │
 *  ├───────────────┼───────────────┼───────────────┼───────────────┼───────────────┤
 *  │   private     │      ✓        │      ✗        │      ✗        │      ✗        │
 *  │   default     │      ✓        │      ✓        │      ✗        │      ✗        │
 *  │   protected   │      ✓        │      ✓        │      ✓        │      ✗        │
 *  │   public      │      ✓        │      ✓        │      ✓        │      ✓        │
 *  └───────────────┴───────────────┴───────────────┴───────────────┴───────────────┘
 *
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                         4. JAVA KEYWORDS QUICK REFERENCE                                       │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  ┌───────────────┬───────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │ final         │ Variable: constant | Method: cannot override | Class: cannot inherit                         │
 *  ├───────────────┼───────────────────────────────────────────────────────────────────────────────────────────────┤
 *  │ finally       │ Block that always executes after try-catch (cleanup code)                                    │
 *  ├───────────────┼───────────────────────────────────────────────────────────────────────────────────────────────┤
 *  │ finalize      │ Method called by GC before object destruction (deprecated in Java 9+)                        │
 *  ├───────────────┼───────────────────────────────────────────────────────────────────────────────────────────────┤
 *  │ static        │ Belongs to class, not instance | Shared across all objects                                   │
 *  ├───────────────┼───────────────────────────────────────────────────────────────────────────────────────────────┤
 *  │ transient     │ Variable excluded from serialization                                                         │
 *  ├───────────────┼───────────────────────────────────────────────────────────────────────────────────────────────┤
 *  │ volatile      │ Variable always read from main memory (visibility guarantee in multithreading)               │
 *  ├───────────────┼───────────────────────────────────────────────────────────────────────────────────────────────┤
 *  │ synchronized  │ Method/block can be accessed by only one thread at a time                                    │
 *  ├───────────────┼───────────────────────────────────────────────────────────────────────────────────────────────┤
 *  │ native        │ Method implemented in platform-dependent code (C/C++)                                        │
 *  ├───────────────┼───────────────────────────────────────────────────────────────────────────────────────────────┤
 *  │ strictfp      │ Ensures floating-point calculations are consistent across platforms                          │
 *  └───────────────┴───────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                    5. JAVA 8+ FEATURES QUICK REFERENCE                                         │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  JAVA 8 (2014):
 *  • Lambda Expressions        : (a, b) -> a + b
 *  • Functional Interfaces     : @FunctionalInterface, Predicate, Function, Consumer, Supplier
 *  • Stream API                : filter(), map(), reduce(), collect()
 *  • Optional                  : Avoid null pointer exceptions
 *  • Default Methods           : Interface methods with implementation
 *  • Method References         : ClassName::methodName
 *  • Date/Time API             : LocalDate, LocalTime, LocalDateTime
 *
 *  JAVA 9 (2017):
 *  • Module System (JPMS)      : module-info.java
 *  • JShell (REPL)             : Interactive Java
 *  • Collection Factory        : List.of(), Set.of(), Map.of()
 *  • Private Interface Methods : Private methods in interfaces
 *
 *  JAVA 10 (2018):
 *  • var keyword               : Local variable type inference
 *
 *  JAVA 11 (2018 LTS):
 *  • String Methods            : isBlank(), lines(), strip(), repeat()
 *  • Files.readString()        : Read file to string
 *  • HTTP Client               : New HTTP/2 client
 *
 *  JAVA 14-16:
 *  • Records                   : record Person(String name, int age) {}
 *  • Pattern Matching          : if (obj instanceof String s)
 *  • Text Blocks               : """ multi-line strings """
 *  • Sealed Classes            : sealed class Shape permits Circle, Rectangle
 *
 *  JAVA 17 (2021 LTS):
 *  • Sealed Classes (final)
 *  • Pattern Matching Switch   : switch expression with patterns
 *
 *  JAVA 21 (2023 LTS):
 *  • Virtual Threads           : Thread.startVirtualThread()
 *  • Record Patterns           : Deconstruct records in pattern matching
 *  • Sequenced Collections     : SequencedCollection, getFirst(), getLast()
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    MOST ASKED INTERVIEW QUESTIONS (4 YOE)                                      ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                    CORE JAVA & OOP                                                          │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *  1. What is the difference between Abstract Class and Interface?
 *  2. Why is String immutable in Java?
 *  3. What is the difference between == and equals()?
 *  4. Explain method overloading vs method overriding.
 *  5. What is diamond problem and how does Java solve it?
 *  6. Can we override static methods? Why or why not?
 *  7. What is the difference between fail-fast and fail-safe iterators?
 *  8. Explain the contract between equals() and hashCode().
 *
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                    COLLECTIONS                                                              │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *  1. How does HashMap work internally?
 *  2. What is the difference between HashMap and ConcurrentHashMap?
 *  3. Why is initial capacity and load factor important in HashMap?
 *  4. What is the difference between ArrayList and LinkedList?
 *  5. How does TreeMap maintain sorted order?
 *  6. What happens when two keys have the same hashCode in HashMap?
 *  7. Why do we need to override equals() and hashCode() for custom keys?
 *
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                    MULTITHREADING                                                           │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *  1. What is the difference between Thread and Runnable?
 *  2. Explain the lifecycle of a thread.
 *  3. What is the difference between synchronized method and synchronized block?
 *  4. What is deadlock? How to prevent it?
 *  5. What is the difference between wait(), notify() and sleep()?
 *  6. Explain volatile keyword.
 *  7. What is ThreadLocal?
 *  8. What is the difference between Callable and Runnable?
 *  9. Explain ExecutorService and its thread pools.
 *  10. What are atomic variables?
 *
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                    JAVA 8                                                                   │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *  1. What is a functional interface?
 *  2. Explain Stream API and its operations.
 *  3. What is the difference between map() and flatMap()?
 *  4. What is Optional and why should we use it?
 *  5. Explain method references.
 *  6. What is the difference between Collection and Stream?
 *  7. What are default methods in interfaces?
 *  8. Difference between intermediate and terminal operations.
 *
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                    MEMORY & JVM                                                             │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *  1. What is the difference between Stack and Heap memory?
 *  2. Explain Garbage Collection and its algorithms.
 *  3. What is memory leak in Java?
 *  4. How to analyze OutOfMemoryError?
 *  5. What JVM parameters do you use in production?
 *  6. What is the difference between G1 and ZGC?
 *
 *
 * @author Java Interview Guide
 * @version 4.0
 * @see _01_OOPConcepts
 * @see _02_CollectionsFramework
 * @see _03_MultithreadingConcurrency
 * @see _07_Java8Features
 */
public class _00_JavaInterviewGuide {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║            JAVA INTERVIEW PREPARATION - 4 YEARS EXPERIENCE                     ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("📚 This package contains comprehensive Java interview preparation materials.");
        System.out.println();
        System.out.println("📁 Files in this package:");
        System.out.println("   ├── _00_JavaInterviewGuide.java        - Overview (You are here!)");
        System.out.println("   ├── _01_OOPConcepts.java               - OOP Deep Dive");
        System.out.println("   ├── _02_CollectionsFramework.java      - Collections Internal Working");
        System.out.println("   ├── _03_MultithreadingConcurrency.java - Multithreading & Concurrency");
        System.out.println("   ├── _04_Java8Features.java             - Java 8+ Features");
        System.out.println("   ├── _05_ExceptionHandling.java         - Exception Best Practices");
        System.out.println("   ├── _06_StringAndImmutability.java     - String Pool & Immutability");
        System.out.println("   ├── _07_DesignPatterns.java            - Common Design Patterns");
        System.out.println("   ├── _08_SOLIDPrinciples.java           - SOLID Principles");
        System.out.println("   └── _09_CommonCodingQuestions.java     - Coding Problems");
        System.out.println();
        System.out.println("💡 Tip: Read the Javadoc comments in each file for detailed explanations!");
        System.out.println();
        
        // Display Java version info
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════");
        System.out.println("Current Java Environment:");
        System.out.println("  Java Version: " + System.getProperty("java.version"));
        System.out.println("  Java Vendor:  " + System.getProperty("java.vendor"));
        System.out.println("  JVM Name:     " + System.getProperty("java.vm.name"));
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════");
    }
}

