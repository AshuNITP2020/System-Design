package com.learning.java_interview;

/**
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                                                                                                ║
 * ║                              STRING & IMMUTABILITY - INTERVIEW DEEP DIVE                                       ║
 * ║                                                                                                                ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    1. WHY STRING IS IMMUTABLE?                                                 ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │  1. SECURITY                                                                                                   │
 *  │     └── Strings are used for: passwords, file paths, network URLs, class loading                              │
 *  │     └── If mutable, could be changed after validation but before use                                          │
 *  │                                                                                                                │
 *  │  2. STRING POOL / INTERNING                                                                                    │
 *  │     └── Multiple references can safely share same String object                                                │
 *  │     └── Saves memory and improves performance                                                                  │
 *  │                                                                                                                │
 *  │  3. HASHCODE CACHING                                                                                           │
 *  │     └── hashCode() is computed once and cached                                                                 │
 *  │     └── Essential for HashMap/HashSet performance                                                              │
 *  │                                                                                                                │
 *  │  4. THREAD SAFETY                                                                                              │
 *  │     └── Immutable objects are inherently thread-safe                                                           │
 *  │     └── No synchronization needed                                                                              │
 *  │                                                                                                                │
 *  │  5. CLASS LOADING                                                                                              │
 *  │     └── Class names are strings                                                                                │
 *  │     └── Must be constant for security                                                                          │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    2. HOW STRING IS MADE IMMUTABLE                                             ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │  1. CLASS IS FINAL                                                                                             │
 *  │     public final class String { }                                                                              │
 *  │     └── Cannot be extended, so no subclass can override methods                                                │
 *  │                                                                                                                │
 *  │  2. CHARACTER ARRAY IS PRIVATE FINAL                                                                           │
 *  │     private final char[] value;  // Java 8 and below                                                           │
 *  │     private final byte[] value;  // Java 9+ (compact strings)                                                  │
 *  │     └── Cannot be reassigned                                                                                   │
 *  │     └── No getter exposes the internal array                                                                   │
 *  │                                                                                                                │
 *  │  3. NO MUTATING METHODS                                                                                        │
 *  │     └── All methods return NEW String objects                                                                  │
 *  │     └── Original string is never modified                                                                      │
 *  │                                                                                                                │
 *  │  4. DEFENSIVE COPY                                                                                             │
 *  │     └── Constructor copies input array                                                                         │
 *  │     └── Original array cannot affect String                                                                    │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    3. STRING POOL (STRING INTERN POOL)                                         ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │  HEAP MEMORY                                                                                                   │
 *  │  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐   │
 *  │  │                                                                                                         │   │
 *  │  │  STRING POOL (inside Heap since Java 7)                                                                 │   │
 *  │  │  ┌─────────────────────────────────────────────────────────────────────────────────────────────────┐    │   │
 *  │  │  │  "Hello"  ←── s1, s2 (both point here)                                                          │    │   │
 *  │  │  │  "World"  ←── s4                                                                                │    │   │
 *  │  │  │  "Java"                                                                                         │    │   │
 *  │  │  └─────────────────────────────────────────────────────────────────────────────────────────────────┘    │   │
 *  │  │                                                                                                         │   │
 *  │  │  REGULAR HEAP                                                                                           │   │
 *  │  │  ┌─────────────────────────────────────────────────────────────────────────────────────────────────┐    │   │
 *  │  │  │  String Object "Hello"  ←── s3 (new String("Hello"))                                            │    │   │
 *  │  │  └─────────────────────────────────────────────────────────────────────────────────────────────────┘    │   │
 *  │  │                                                                                                         │   │
 *  │  └─────────────────────────────────────────────────────────────────────────────────────────────────────────┘   │
 *  │                                                                                                                │
 *  │  String s1 = "Hello";           // Goes to pool, returns reference to pool                                    │
 *  │  String s2 = "Hello";           // Same "Hello" found in pool, returns same reference                         │
 *  │  String s3 = new String("Hello"); // Creates NEW object in heap (outside pool)                                │
 *  │  String s4 = "World";           // New entry in pool                                                          │
 *  │                                                                                                                │
 *  │  s1 == s2       // true  (same reference from pool)                                                           │
 *  │  s1 == s3       // false (different objects)                                                                  │
 *  │  s1.equals(s3)  // true  (same content)                                                                       │
 *  │                                                                                                                │
 *  │  s3.intern()    // Returns reference from pool (adds to pool if not present)                                  │
 *  │  s3.intern() == s1  // true                                                                                   │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                4. STRING vs STRINGBUILDER vs STRINGBUFFER                                      ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  ┌────────────────────────┬─────────────────────────┬─────────────────────────┬───────────────────────────────┐
 *  │        Feature         │        String           │      StringBuilder      │        StringBuffer           │
 *  ├────────────────────────┼─────────────────────────┼─────────────────────────┼───────────────────────────────┤
 *  │ Mutability             │ Immutable               │ Mutable                 │ Mutable                       │
 *  │ Thread Safety          │ Yes (immutable)         │ No                      │ Yes (synchronized)            │
 *  │ Performance            │ Slow for concatenation  │ Fastest                 │ Slower (sync overhead)        │
 *  │ Storage                │ String Pool + Heap      │ Heap only               │ Heap only                     │
 *  │ Since                  │ JDK 1.0                 │ JDK 1.5                 │ JDK 1.0                       │
 *  │ Memory                 │ Creates new objects     │ Modifies in place       │ Modifies in place             │
 *  │ Use Case               │ Few modifications       │ Many modifications      │ Multi-threaded modifications  │
 *  │                        │ Thread-safe needed      │ Single-threaded         │                               │
 *  └────────────────────────┴─────────────────────────┴─────────────────────────┴───────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    5. STRING CONCATENATION INTERNALS                                           ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │  COMPILE-TIME CONCATENATION:                                                                                   │
 *  │  String s = "Hello" + " " + "World";  // Compiler optimizes to "Hello World"                                   │
 *  │                                                                                                                │
 *  │  RUNTIME CONCATENATION (before Java 9):                                                                        │
 *  │  String s = s1 + s2;  // Compiler converts to:                                                                 │
 *  │  String s = new StringBuilder().append(s1).append(s2).toString();                                              │
 *  │                                                                                                                │
 *  │  RUNTIME CONCATENATION (Java 9+):                                                                              │
 *  │  Uses invokedynamic for better optimization                                                                    │
 *  │                                                                                                                │
 *  │  LOOP CONCATENATION PROBLEM:                                                                                   │
 *  │  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐   │
 *  │  │  // BAD: Creates new StringBuilder each iteration                                                       │   │
 *  │  │  String result = "";                                                                                    │   │
 *  │  │  for (String s : list) {                                                                                │   │
 *  │  │      result += s;  // new StringBuilder().append(result).append(s).toString()                           │   │
 *  │  │  }                                                                                                      │   │
 *  │  │                                                                                                         │   │
 *  │  │  // GOOD: Single StringBuilder                                                                          │   │
 *  │  │  StringBuilder sb = new StringBuilder();                                                                │   │
 *  │  │  for (String s : list) {                                                                                │   │
 *  │  │      sb.append(s);                                                                                      │   │
 *  │  │  }                                                                                                      │   │
 *  │  │  String result = sb.toString();                                                                         │   │
 *  │  └─────────────────────────────────────────────────────────────────────────────────────────────────────────┘   │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    6. HOW TO CREATE IMMUTABLE CLASS                                            ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │  RULES FOR IMMUTABILITY:                                                                                       │
 *  │                                                                                                                │
 *  │  1. Make class FINAL (no subclassing)                                                                          │
 *  │     public final class ImmutableClass { }                                                                      │
 *  │                                                                                                                │
 *  │  2. Make all fields PRIVATE and FINAL                                                                          │
 *  │     private final String name;                                                                                 │
 *  │     private final int age;                                                                                     │
 *  │                                                                                                                │
 *  │  3. NO SETTER methods                                                                                          │
 *  │                                                                                                                │
 *  │  4. Initialize all fields via CONSTRUCTOR                                                                      │
 *  │                                                                                                                │
 *  │  5. DEFENSIVE COPY for mutable objects:                                                                        │
 *  │     • In constructor: copy input objects                                                                       │
 *  │     • In getters: return copy, not reference                                                                   │
 *  │                                                                                                                │
 *  │  6. Don't allow subclasses to override methods                                                                 │
 *  │     • Make class final, OR                                                                                     │
 *  │     • Make all methods final                                                                                   │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    7. INTERVIEW QUESTIONS                                                      ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * Q1: How many objects created: String s = new String("Hello");
 * A: Up to 2: one in pool (if "Hello" not already there), one in heap (from new).
 *
 * Q2: What is string interning?
 * A: Process of storing only one copy of each distinct string in pool. intern() method.
 *
 * Q3: Why String is popular as HashMap key?
 * A: Immutable (hashCode won't change), hashCode cached (fast lookups).
 *
 * Q4: Difference between == and equals() for String?
 * A: == compares references, equals() compares content.
 *
 * Q5: What happens when you do: String s = "a" + "b" + "c";
 * A: Compiler optimizes to "abc" (constant folding) - only ONE string created.
 *
 * Q6: Is String a primitive?
 * A: No, it's a reference type (object), but has special support in JVM.
 *
 * Q7: What is String.valueOf() vs toString()?
 * A: valueOf() handles null (returns "null"), toString() throws NPE.
 *
 * Q8: Why prefer StringBuilder over StringBuffer?
 * A: StringBuilder is faster (no synchronization) - use unless thread safety needed.
 *
 *
 * @author Java Interview Guide
 */
public class _06_StringAndImmutability {

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════");
        System.out.println("                    STRING & IMMUTABILITY DEMONSTRATION");
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════\n");

        // 1. String Pool Demo
        demonstrateStringPool();

        // 2. String Immutability Demo
        demonstrateImmutability();

        // 3. String vs StringBuilder Performance
        demonstratePerformance();

        // 4. Important String Methods
        demonstrateStringMethods();

        // 5. Custom Immutable Class
        demonstrateCustomImmutableClass();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         1. STRING POOL DEMO
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateStringPool() {
        System.out.println("▶ 1. STRING POOL DEMO:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // Literal strings go to pool
        String s1 = "Hello";
        String s2 = "Hello";
        System.out.println("   s1 = \"Hello\", s2 = \"Hello\"");
        System.out.println("   s1 == s2 (same pool reference): " + (s1 == s2));  // true

        // new creates new object in heap
        String s3 = new String("Hello");
        System.out.println("\n   s3 = new String(\"Hello\")");
        System.out.println("   s1 == s3 (different objects): " + (s1 == s3));  // false
        System.out.println("   s1.equals(s3) (same content): " + s1.equals(s3));  // true

        // intern() returns pool reference
        String s4 = s3.intern();
        System.out.println("\n   s4 = s3.intern()");
        System.out.println("   s1 == s4 (intern returns pool ref): " + (s1 == s4));  // true

        // Concatenation
        String s5 = "Hel" + "lo";  // Compile-time constant
        System.out.println("\n   s5 = \"Hel\" + \"lo\" (compile-time optimization)");
        System.out.println("   s1 == s5: " + (s1 == s5));  // true

        String prefix = "Hel";
        String s6 = prefix + "lo";  // Runtime concatenation
        System.out.println("\n   String prefix = \"Hel\"; s6 = prefix + \"lo\" (runtime)");
        System.out.println("   s1 == s6: " + (s1 == s6));  // false (new object created)
        System.out.println("   s1 == s6.intern(): " + (s1 == s6.intern()));  // true

        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         2. STRING IMMUTABILITY DEMO
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateImmutability() {
        System.out.println("▶ 2. STRING IMMUTABILITY DEMO:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        String original = "Hello";
        System.out.println("   original = \"Hello\"");
        System.out.println("   original.hashCode() = " + original.hashCode());

        // All methods return NEW strings
        String upper = original.toUpperCase();
        String replaced = original.replace('H', 'J');
        String concat = original.concat(" World");

        System.out.println("\n   After operations:");
        System.out.println("   original.toUpperCase() = \"" + upper + "\"");
        System.out.println("   original.replace('H','J') = \"" + replaced + "\"");
        System.out.println("   original.concat(\" World\") = \"" + concat + "\"");
        System.out.println("\n   original is UNCHANGED: \"" + original + "\"");
        System.out.println("   original.hashCode() still = " + original.hashCode());

        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         3. STRING vs STRINGBUILDER PERFORMANCE
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstratePerformance() {
        System.out.println("▶ 3. STRING vs STRINGBUILDER PERFORMANCE:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        int iterations = 50000;

        // String concatenation (slow)
        long start = System.currentTimeMillis();
        String str = "";
        for (int i = 0; i < iterations; i++) {
            str += "a";  // Creates new String each time!
        }
        long stringTime = System.currentTimeMillis() - start;

        // StringBuilder (fast)
        start = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < iterations; i++) {
            sb.append("a");  // Modifies same object
        }
        String result = sb.toString();
        long sbTime = System.currentTimeMillis() - start;

        // StringBuffer (thread-safe, slightly slower than StringBuilder)
        start = System.currentTimeMillis();
        StringBuffer sbuf = new StringBuffer();
        for (int i = 0; i < iterations; i++) {
            sbuf.append("a");
        }
        String resultBuf = sbuf.toString();
        long sbufTime = System.currentTimeMillis() - start;

        System.out.println("   " + iterations + " concatenations:");
        System.out.println("   String +=          : " + stringTime + " ms");
        System.out.println("   StringBuilder      : " + sbTime + " ms");
        System.out.println("   StringBuffer       : " + sbufTime + " ms");
        System.out.println("\n   📌 StringBuilder is ~" + (stringTime / Math.max(1, sbTime)) + "x faster than String +=");

        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         4. IMPORTANT STRING METHODS
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateStringMethods() {
        System.out.println("▶ 4. IMPORTANT STRING METHODS:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        String str = "  Hello World  ";

        // Basic methods
        System.out.println("   String: \"" + str + "\"");
        System.out.println("   length(): " + str.length());
        System.out.println("   charAt(2): '" + str.charAt(2) + "'");
        System.out.println("   substring(2, 7): \"" + str.substring(2, 7) + "\"");

        // Searching
        System.out.println("\n   Searching:");
        System.out.println("   indexOf('o'): " + str.indexOf('o'));
        System.out.println("   lastIndexOf('o'): " + str.lastIndexOf('o'));
        System.out.println("   contains(\"World\"): " + str.contains("World"));

        // Comparison
        System.out.println("\n   Comparison:");
        System.out.println("   equals(\"  Hello World  \"): " + str.equals("  Hello World  "));
        System.out.println("   equalsIgnoreCase(\"  hello world  \"): " + str.equalsIgnoreCase("  hello world  "));
        System.out.println("   compareTo(\"Apple\"): " + str.trim().compareTo("Apple"));

        // Modification (returns new String)
        System.out.println("\n   Modification:");
        System.out.println("   trim(): \"" + str.trim() + "\"");
        System.out.println("   strip() (Java 11): \"" + str.strip() + "\"");
        System.out.println("   toLowerCase(): \"" + str.toLowerCase() + "\"");
        System.out.println("   toUpperCase(): \"" + str.toUpperCase() + "\"");
        System.out.println("   replace('l','x'): \"" + str.replace('l', 'x') + "\"");

        // Splitting and Joining
        System.out.println("\n   Splitting and Joining:");
        String csv = "apple,banana,cherry";
        String[] parts = csv.split(",");
        System.out.println("   \"" + csv + "\".split(\",\"): " + java.util.Arrays.toString(parts));
        System.out.println("   String.join(\"-\", parts): " + String.join("-", parts));

        // Java 11+ methods
        System.out.println("\n   Java 11+ Methods:");
        System.out.println("   \"  \".isBlank(): " + "  ".isBlank());
        System.out.println("   \"Hi\".repeat(3): " + "Hi".repeat(3));
        System.out.println("   \"a\\nb\\nc\".lines().count(): " + "a\nb\nc".lines().count());

        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         5. CUSTOM IMMUTABLE CLASS
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateCustomImmutableClass() {
        System.out.println("▶ 5. CUSTOM IMMUTABLE CLASS:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        java.util.Date birthDate = new java.util.Date();
        java.util.List<String> hobbies = new java.util.ArrayList<>();
        hobbies.add("Reading");
        hobbies.add("Gaming");

        ImmutablePerson person = new ImmutablePerson("John", 30, birthDate, hobbies);

        System.out.println("   Created: " + person);

        // Try to modify original objects
        birthDate.setTime(0);  // Modify original date
        hobbies.add("Hacking");  // Modify original list

        System.out.println("   After modifying original date and list:");
        System.out.println("   Person is UNCHANGED: " + person);
        System.out.println("   Person's hobbies: " + person.getHobbies());

        // Try to modify returned list
        try {
            person.getHobbies().add("Evil");
        } catch (UnsupportedOperationException e) {
            System.out.println("   Cannot modify returned hobbies list (unmodifiable)");
        }

        System.out.println();
    }

    /**
     * Example of a properly implemented immutable class
     */
    static final class ImmutablePerson {  // 1. Class is FINAL
        private final String name;         // 2. Fields are PRIVATE and FINAL
        private final int age;
        private final java.util.Date birthDate;
        private final java.util.List<String> hobbies;

        // 3. Initialize via constructor with defensive copying
        public ImmutablePerson(String name, int age, java.util.Date birthDate,
                               java.util.List<String> hobbies) {
            this.name = name;
            this.age = age;
            // Defensive copy of mutable Date
            this.birthDate = new java.util.Date(birthDate.getTime());
            // Defensive copy of mutable List
            this.hobbies = new java.util.ArrayList<>(hobbies);
        }

        // 4. No setters

        // 5. Getters return copies of mutable objects
        public String getName() {
            return name;  // String is immutable, no copy needed
        }

        public int getAge() {
            return age;  // primitives are safe
        }

        public java.util.Date getBirthDate() {
            return new java.util.Date(birthDate.getTime());  // Return copy
        }

        public java.util.List<String> getHobbies() {
            return java.util.Collections.unmodifiableList(hobbies);  // Return unmodifiable view
        }

        @Override
        public String toString() {
            return "ImmutablePerson{name='" + name + "', age=" + age + "}";
        }
    }
}

