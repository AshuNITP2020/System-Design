package com.learning.java_interview;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                                                                                                ║
 * ║                                    JAVA 8+ FEATURES - INTERVIEW DEEP DIVE                                      ║
 * ║                                                                                                                ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    1. FUNCTIONAL INTERFACES                                                    ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * DEFINITION:
 * An interface with EXACTLY ONE abstract method. Can have multiple default/static methods.
 * @FunctionalInterface annotation is optional but recommended.
 *
 * BUILT-IN FUNCTIONAL INTERFACES:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │  Interface          │ Method              │ Input    │ Output   │ Use Case                                    │
 * │  ───────────────────┼─────────────────────┼──────────┼──────────┼─────────────────────────────────────────────│
 * │  Predicate<T>       │ test(T t)           │ T        │ boolean  │ Filtering, matching conditions              │
 * │  Function<T,R>      │ apply(T t)          │ T        │ R        │ Transformation, mapping                     │
 * │  Consumer<T>        │ accept(T t)         │ T        │ void     │ forEach, side effects                       │
 * │  Supplier<T>        │ get()               │ none     │ T        │ Factory, lazy evaluation                    │
 * │  BiFunction<T,U,R>  │ apply(T t, U u)     │ T, U     │ R        │ Two input transformation                    │
 * │  BiPredicate<T,U>   │ test(T t, U u)      │ T, U     │ boolean  │ Two input condition                         │
 * │  BiConsumer<T,U>    │ accept(T t, U u)    │ T, U     │ void     │ Two input side effect                       │
 * │  UnaryOperator<T>   │ apply(T t)          │ T        │ T        │ Same type transformation                    │
 * │  BinaryOperator<T>  │ apply(T t1, T t2)   │ T, T     │ T        │ Reduce, combine same types                  │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    2. LAMBDA EXPRESSIONS                                                       ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * SYNTAX:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │  (parameters) -> expression                    // Single expression                                            │
 * │  (parameters) -> { statements; }               // Multiple statements                                          │
 * │                                                                                                                │
 * │  Examples:                                                                                                     │
 * │  () -> System.out.println("Hello")             // No parameters                                                │
 * │  x -> x * x                                    // Single parameter (parentheses optional)                      │
 * │  (x, y) -> x + y                               // Multiple parameters                                          │
 * │  (String s) -> s.length()                      // Explicit type                                                │
 * │  (int a, int b) -> { return a + b; }           // Block with return                                            │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * LAMBDA SCOPE:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  • Can access final or effectively final local variables                                                       │
 * │  • Can access instance/static variables                                                                        │
 * │  • 'this' refers to enclosing class (not lambda itself)                                                        │
 * │  • Cannot modify local variables (must be effectively final)                                                   │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    3. METHOD REFERENCES                                                        ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * TYPES:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │  Type                          │ Syntax                    │ Lambda Equivalent                                │
 * │  ──────────────────────────────┼───────────────────────────┼──────────────────────────────────────────────────│
 * │  Static method                 │ ClassName::staticMethod   │ (args) -> ClassName.staticMethod(args)          │
 * │  Instance method (object)      │ object::instanceMethod    │ (args) -> object.instanceMethod(args)           │
 * │  Instance method (class)       │ ClassName::instanceMethod │ (obj, args) -> obj.instanceMethod(args)         │
 * │  Constructor                   │ ClassName::new            │ (args) -> new ClassName(args)                   │
 * │                                                                                                                │
 * │  Examples:                                                                                                     │
 * │  String::length                // s -> s.length()                                                              │
 * │  System.out::println           // x -> System.out.println(x)                                                   │
 * │  Integer::parseInt             // s -> Integer.parseInt(s)                                                     │
 * │  ArrayList::new                // () -> new ArrayList()                                                        │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    4. STREAM API                                                               ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * WHAT IS STREAM?
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  • NOT a data structure - it's a pipeline for processing data                                                  │
 * │  • Doesn't store elements - processes from source                                                              │
 * │  • Lazy evaluation - intermediate operations are not executed until terminal operation                         │
 * │  • Can be consumed only once                                                                                   │
 * │  • Can be sequential or parallel                                                                               │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * STREAM OPERATIONS:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │  INTERMEDIATE (Lazy, return Stream)          │  TERMINAL (Eager, trigger execution)                           │
 * │  ────────────────────────────────────────────┼───────────────────────────────────────────────────────────────  │
 * │  filter(Predicate)     - Filter elements     │  forEach(Consumer)    - Iterate elements                       │
 * │  map(Function)         - Transform elements  │  collect(Collector)   - Collect to collection                  │
 * │  flatMap(Function)     - Flatten nested      │  reduce(BinaryOp)     - Reduce to single value                 │
 * │  distinct()            - Remove duplicates   │  count()              - Count elements                         │
 * │  sorted()              - Sort elements       │  findFirst()          - Get first element                      │
 * │  sorted(Comparator)    - Custom sort         │  findAny()            - Get any element                        │
 * │  limit(n)              - Take first n        │  anyMatch(Predicate)  - Check if any matches                   │
 * │  skip(n)               - Skip first n        │  allMatch(Predicate)  - Check if all match                     │
 * │  peek(Consumer)        - Debug/inspect       │  noneMatch(Predicate) - Check if none match                    │
 * │                                              │  min(Comparator)      - Find minimum                           │
 * │                                              │  max(Comparator)      - Find maximum                           │
 * │                                              │  toArray()            - Convert to array                       │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * MAP vs FLATMAP:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │  map():      One-to-one transformation                                                                         │
 * │              Input: Stream<T>  →  Output: Stream<R>                                                            │
 * │              Example: Stream<String> → Stream<Integer> (string to length)                                      │
 * │                                                                                                                │
 * │  flatMap():  One-to-many transformation, then flatten                                                          │
 * │              Input: Stream<T>  →  Output: Stream<R> (flattened)                                                │
 * │              Example: Stream<List<String>> → Stream<String>                                                    │
 * │                                                                                                                │
 * │  ┌─────────────────────────────────────────────────────────────────────────────────────────┐                   │
 * │  │  List<List<Integer>> nested = [[1,2], [3,4], [5,6]]                                     │                   │
 * │  │                                                                                         │                   │
 * │  │  map(x -> x):        Stream<List<Integer>> → [[1,2], [3,4], [5,6]]  (still nested)     │                   │
 * │  │  flatMap(x -> x.stream()): Stream<Integer> → [1, 2, 3, 4, 5, 6]     (flattened!)       │                   │
 * │  └─────────────────────────────────────────────────────────────────────────────────────────┘                   │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    5. OPTIONAL                                                                 ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * WHY OPTIONAL?
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  • Avoid NullPointerException                                                                                  │
 * │  • Force explicit handling of absent values                                                                    │
 * │  • Self-documenting code (method returns Optional = might be absent)                                           │
 * │  • Fluent API for null checks                                                                                  │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * OPTIONAL METHODS:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │  Creation:                                                                                                     │
 * │  Optional.of(value)         - Creates Optional (throws NPE if null)                                            │
 * │  Optional.ofNullable(value) - Creates Optional (empty if null)                                                 │
 * │  Optional.empty()           - Creates empty Optional                                                           │
 * │                                                                                                                │
 * │  Checking:                                                                                                     │
 * │  isPresent()                - Returns true if value present                                                    │
 * │  isEmpty()   (Java 11+)     - Returns true if value absent                                                     │
 * │                                                                                                                │
 * │  Retrieving:                                                                                                   │
 * │  get()                      - Returns value (throws NoSuchElementException if empty)                           │
 * │  orElse(default)            - Returns value or default                                                         │
 * │  orElseGet(Supplier)        - Returns value or lazy default                                                    │
 * │  orElseThrow()              - Returns value or throws exception                                                │
 * │  orElseThrow(Supplier)      - Returns value or throws custom exception                                         │
 * │                                                                                                                │
 * │  Transforming:                                                                                                 │
 * │  map(Function)              - Transform if present                                                             │
 * │  flatMap(Function)          - Transform returning Optional                                                     │
 * │  filter(Predicate)          - Filter based on condition                                                        │
 * │                                                                                                                │
 * │  Consuming:                                                                                                    │
 * │  ifPresent(Consumer)        - Execute if present                                                               │
 * │  ifPresentOrElse(Consumer, Runnable) (Java 9+) - Execute based on presence                                     │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    6. DEFAULT & STATIC METHODS IN INTERFACE                                    ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │  DEFAULT METHODS:                              STATIC METHODS:                                                 │
 * │  • Provide implementation in interface         • Utility methods in interface                                  │
 * │  • Can be overridden                           • Cannot be overridden                                          │
 * │  • Called on instance                          • Called on interface                                           │
 * │  • Enable interface evolution                  • Helper methods                                                │
 * │                                                                                                                │
 * │  interface MyInterface {                       interface MyInterface {                                         │
 * │      default void log(String msg) {                static void helper() {                                      │
 * │          System.out.println(msg);                      System.out.println("Helper");                           │
 * │      }                                             }                                                           │
 * │  }                                             }                                                               │
 * │                                                                                                                │
 * │  obj.log("Hello");  // Called on instance      MyInterface.helper();  // Called on interface                   │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    7. DATE/TIME API (java.time)                                                ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │  Class                │ Description                          │ Example                                        │
 * │  ─────────────────────┼──────────────────────────────────────┼────────────────────────────────────────────────│
 * │  LocalDate            │ Date without time/timezone           │ 2024-01-15                                     │
 * │  LocalTime            │ Time without date/timezone           │ 14:30:45                                       │
 * │  LocalDateTime        │ Date + Time without timezone         │ 2024-01-15T14:30:45                            │
 * │  ZonedDateTime        │ Date + Time + Timezone               │ 2024-01-15T14:30:45+05:30[Asia/Kolkata]        │
 * │  Instant              │ Machine timestamp (epoch seconds)    │ 2024-01-15T09:00:45Z                           │
 * │  Duration             │ Time-based amount                    │ PT2H30M (2 hours 30 minutes)                   │
 * │  Period               │ Date-based amount                    │ P1Y2M3D (1 year 2 months 3 days)               │
 * │                                                                                                                │
 * │  All classes are IMMUTABLE and THREAD-SAFE                                                                     │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    8. INTERVIEW QUESTIONS                                                      ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * Q1: Difference between Collection and Stream?
 * A: Collection: Stores data, eager, can iterate multiple times
 *    Stream: Processes data, lazy, single use, supports parallel
 *
 * Q2: What is lazy evaluation in Stream?
 * A: Intermediate operations are not executed until a terminal operation is called.
 *    This allows optimization like short-circuiting (findFirst stops when found).
 *
 * Q3: Difference between map() and flatMap()?
 * A: map: One-to-one, wraps result. flatMap: One-to-many, flattens nested structures.
 *
 * Q4: When to use orElse vs orElseGet?
 * A: orElse: Default always evaluated (even if not used)
 *    orElseGet: Default only evaluated if needed (lazy)
 *    Use orElseGet when default is expensive to create.
 *
 * Q5: Can we reuse a Stream?
 * A: No, IllegalStateException. Create new stream for each operation.
 *
 * Q6: What is effectively final?
 * A: Variable that is not modified after initialization. Required for lambda capture.
 *
 * Q7: Difference between intermediate and terminal operations?
 * A: Intermediate: Return Stream, lazy (filter, map)
 *    Terminal: Return non-Stream, trigger execution (collect, forEach)
 *
 * Q8: What is the purpose of peek()?
 * A: Debugging - allows seeing elements as they flow through pipeline without modifying them.
 *
 *
 * @author Java Interview Guide
 */
public class _04_Java8Features {

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════");
        System.out.println("                        JAVA 8+ FEATURES DEMONSTRATION");
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════\n");

        // 1. Functional Interfaces
        demonstrateFunctionalInterfaces();

        // 2. Lambda Expressions
        demonstrateLambdaExpressions();

        // 3. Method References
        demonstrateMethodReferences();

        // 4. Stream API
        demonstrateStreamAPI();

        // 5. Optional
        demonstrateOptional();

        // 6. Collectors
        demonstrateCollectors();

        // 7. Date/Time API
        demonstrateDateTimeAPI();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         1. FUNCTIONAL INTERFACES
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateFunctionalInterfaces() {
        System.out.println("▶ 1. FUNCTIONAL INTERFACES:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // Predicate<T> - test(T t) -> boolean
        Predicate<Integer> isEven = n -> n % 2 == 0;
        System.out.println("   Predicate - isEven(4): " + isEven.test(4));
        System.out.println("   Predicate - isEven(5): " + isEven.test(5));

        // Predicate chaining
        Predicate<Integer> isPositive = n -> n > 0;
        Predicate<Integer> isPositiveEven = isEven.and(isPositive);
        System.out.println("   Predicate chain - isPositiveEven(-4): " + isPositiveEven.test(-4));
        System.out.println("   Predicate chain - isPositiveEven(4): " + isPositiveEven.test(4));

        // Function<T, R> - apply(T t) -> R
        Function<String, Integer> stringLength = String::length;
        System.out.println("\n   Function - length of 'Hello': " + stringLength.apply("Hello"));

        // Function chaining
        Function<Integer, Integer> doubleIt = n -> n * 2;
        Function<String, Integer> lengthThenDouble = stringLength.andThen(doubleIt);
        System.out.println("   Function chain - length then double 'Hello': " + lengthThenDouble.apply("Hello"));

        // Consumer<T> - accept(T t) -> void
        Consumer<String> printer = s -> System.out.println("   Consumer - printing: " + s);
        printer.accept("Hello Consumer!");

        // Supplier<T> - get() -> T
        Supplier<Double> randomSupplier = Math::random;
        System.out.println("   Supplier - random: " + randomSupplier.get());

        // BiFunction<T, U, R>
        BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
        System.out.println("   BiFunction - add(3, 5): " + add.apply(3, 5));

        // UnaryOperator<T> (special Function<T, T>)
        UnaryOperator<Integer> square = n -> n * n;
        System.out.println("   UnaryOperator - square(5): " + square.apply(5));

        // BinaryOperator<T> (special BiFunction<T, T, T>)
        BinaryOperator<Integer> max = Integer::max;
        System.out.println("   BinaryOperator - max(10, 20): " + max.apply(10, 20));

        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         2. LAMBDA EXPRESSIONS
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateLambdaExpressions() {
        System.out.println("▶ 2. LAMBDA EXPRESSIONS:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // No parameters
        Runnable noParam = () -> System.out.println("   No parameters lambda");
        noParam.run();

        // Single parameter (parentheses optional)
        Consumer<String> singleParam = s -> System.out.println("   Single param: " + s);
        singleParam.accept("Hello");

        // Multiple parameters
        BiFunction<Integer, Integer, Integer> multiParam = (a, b) -> a + b;
        System.out.println("   Multiple params (3+5): " + multiParam.apply(3, 5));

        // Block body with return
        BiFunction<Integer, Integer, Integer> blockBody = (a, b) -> {
            int sum = a + b;
            return sum * 2;
        };
        System.out.println("   Block body ((3+5)*2): " + blockBody.apply(3, 5));

        // Lambda capturing variables (must be effectively final)
        String prefix = "Result: ";  // Effectively final
        Function<Integer, String> capturing = n -> prefix + n;
        System.out.println("   Capturing variable: " + capturing.apply(42));

        // Before Java 8 (anonymous class)
        Comparator<String> beforeJava8 = new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.length() - s2.length();
            }
        };

        // After Java 8 (lambda)
        Comparator<String> afterJava8 = (s1, s2) -> s1.length() - s2.length();

        List<String> words = Arrays.asList("apple", "pie", "banana");
        words.sort(afterJava8);
        System.out.println("   Sorted by length: " + words);

        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         3. METHOD REFERENCES
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateMethodReferences() {
        System.out.println("▶ 3. METHOD REFERENCES:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // Static method reference: ClassName::staticMethod
        Function<String, Integer> parseInt = Integer::parseInt;
        System.out.println("   Static method ref - parseInt('123'): " + parseInt.apply("123"));

        // Instance method reference (on specific object): object::instanceMethod
        String greeting = "Hello";
        Supplier<Integer> lengthSupplier = greeting::length;
        System.out.println("   Instance method ref (object) - greeting.length(): " + lengthSupplier.get());

        // Instance method reference (on class): ClassName::instanceMethod
        Function<String, String> toUpper = String::toUpperCase;
        System.out.println("   Instance method ref (class) - toUpperCase('hello'): " + toUpper.apply("hello"));

        // Constructor reference: ClassName::new
        Supplier<ArrayList<String>> listSupplier = ArrayList::new;
        ArrayList<String> newList = listSupplier.get();
        System.out.println("   Constructor ref - new ArrayList: " + newList);

        // With parameters
        Function<String, StringBuilder> sbBuilder = StringBuilder::new;
        StringBuilder sb = sbBuilder.apply("Initial");
        System.out.println("   Constructor ref with param: " + sb);

        // Practical example
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

        // Lambda
        names.forEach(name -> System.out.println("   Lambda: " + name));

        // Method reference (cleaner)
        System.out.print("   Method ref: ");
        names.forEach(System.out::print);
        System.out.println();

        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         4. STREAM API
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateStreamAPI() {
        System.out.println("▶ 4. STREAM API:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // filter - keep elements matching condition
        List<Integer> evens = numbers.stream()
                .filter(n -> n % 2 == 0)
                .collect(Collectors.toList());
        System.out.println("   filter (evens): " + evens);

        // map - transform elements
        List<Integer> squared = numbers.stream()
                .map(n -> n * n)
                .collect(Collectors.toList());
        System.out.println("   map (squared): " + squared);

        // reduce - combine to single value
        int sum = numbers.stream()
                .reduce(0, Integer::sum);
        System.out.println("   reduce (sum): " + sum);

        // sorted
        List<Integer> sorted = Arrays.asList(5, 2, 8, 1, 9).stream()
                .sorted()
                .collect(Collectors.toList());
        System.out.println("   sorted: " + sorted);

        // distinct
        List<Integer> distinct = Arrays.asList(1, 2, 2, 3, 3, 3).stream()
                .distinct()
                .collect(Collectors.toList());
        System.out.println("   distinct: " + distinct);

        // limit & skip
        List<Integer> limited = numbers.stream()
                .skip(2)
                .limit(5)
                .collect(Collectors.toList());
        System.out.println("   skip(2).limit(5): " + limited);

        // flatMap - flatten nested structures
        List<List<Integer>> nested = Arrays.asList(
                Arrays.asList(1, 2),
                Arrays.asList(3, 4),
                Arrays.asList(5, 6)
        );
        List<Integer> flattened = nested.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        System.out.println("   flatMap (flattened): " + flattened);

        // findFirst, findAny
        Optional<Integer> first = numbers.stream()
                .filter(n -> n > 5)
                .findFirst();
        System.out.println("   findFirst (>5): " + first.orElse(-1));

        // anyMatch, allMatch, noneMatch
        boolean anyEven = numbers.stream().anyMatch(n -> n % 2 == 0);
        boolean allPositive = numbers.stream().allMatch(n -> n > 0);
        boolean noneNegative = numbers.stream().noneMatch(n -> n < 0);
        System.out.println("   anyMatch(even): " + anyEven + ", allMatch(positive): " + allPositive);

        // count, min, max
        long count = numbers.stream().filter(n -> n > 5).count();
        Optional<Integer> max = numbers.stream().max(Integer::compare);
        System.out.println("   count(>5): " + count + ", max: " + max.orElse(-1));

        // Chaining multiple operations
        List<String> result = Arrays.asList("apple", "banana", "cherry", "date").stream()
                .filter(s -> s.length() > 4)
                .map(String::toUpperCase)
                .sorted()
                .collect(Collectors.toList());
        System.out.println("   Chained operations: " + result);

        // Parallel stream
        long parallelSum = numbers.parallelStream()
                .mapToLong(Integer::longValue)
                .sum();
        System.out.println("   Parallel stream sum: " + parallelSum);

        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         5. OPTIONAL
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateOptional() {
        System.out.println("▶ 5. OPTIONAL:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // Creating Optional
        Optional<String> present = Optional.of("Hello");
        Optional<String> empty = Optional.empty();
        Optional<String> nullable = Optional.ofNullable(null);

        System.out.println("   present.isPresent(): " + present.isPresent());
        System.out.println("   empty.isPresent(): " + empty.isPresent());

        // Getting values
        System.out.println("   present.get(): " + present.get());
        System.out.println("   empty.orElse('default'): " + empty.orElse("default"));

        // orElse vs orElseGet
        String orElseResult = empty.orElse(getDefault());  // getDefault() ALWAYS called
        String orElseGetResult = empty.orElseGet(() -> getDefault());  // Called only if empty
        // In this case both return same, but orElseGet is lazy

        // orElseThrow
        try {
            empty.orElseThrow(() -> new RuntimeException("Value not present"));
        } catch (RuntimeException e) {
            System.out.println("   orElseThrow caught: " + e.getMessage());
        }

        // map - transform if present
        Optional<Integer> length = present.map(String::length);
        System.out.println("   present.map(length): " + length.orElse(0));

        // filter - keep if matches condition
        Optional<String> filtered = present.filter(s -> s.startsWith("H"));
        System.out.println("   present.filter(startsWith H): " + filtered.orElse("not found"));

        // flatMap - for nested Optionals
        Optional<Optional<String>> nested = Optional.of(Optional.of("Nested"));
        Optional<String> flatMapped = nested.flatMap(o -> o);
        System.out.println("   flatMap nested Optional: " + flatMapped.orElse("not found"));

        // ifPresent
        present.ifPresent(s -> System.out.println("   ifPresent: " + s));

        // Practical example: null-safe chaining
        User user = new User("John", new Address("New York"));
        String city = Optional.ofNullable(user)
                .map(User::getAddress)
                .map(Address::getCity)
                .orElse("Unknown");
        System.out.println("   Null-safe city: " + city);

        System.out.println();
    }

    static String getDefault() {
        return "default";
    }

    static class User {
        String name;
        Address address;

        User(String name, Address address) {
            this.name = name;
            this.address = address;
        }

        Address getAddress() {
            return address;
        }
    }

    static class Address {
        String city;

        Address(String city) {
            this.city = city;
        }

        String getCity() {
            return city;
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         6. COLLECTORS
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateCollectors() {
        System.out.println("▶ 6. COLLECTORS:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        List<Employee> employees = Arrays.asList(
                new Employee("Alice", "IT", 60000),
                new Employee("Bob", "HR", 50000),
                new Employee("Charlie", "IT", 70000),
                new Employee("Diana", "HR", 55000),
                new Employee("Eve", "IT", 65000)
        );

        // toList, toSet
        List<String> names = employees.stream()
                .map(e -> e.name)
                .collect(Collectors.toList());
        System.out.println("   toList (names): " + names);

        // toMap
        Map<String, Double> nameSalary = employees.stream()
                .collect(Collectors.toMap(e -> e.name, e -> e.salary));
        System.out.println("   toMap (name->salary): " + nameSalary);

        // joining
        String joined = employees.stream()
                .map(e -> e.name)
                .collect(Collectors.joining(", "));
        System.out.println("   joining: " + joined);

        // groupingBy
        Map<String, List<Employee>> byDept = employees.stream()
                .collect(Collectors.groupingBy(e -> e.department));
        System.out.println("   groupingBy (dept): " + byDept.keySet());

        // groupingBy with counting
        Map<String, Long> countByDept = employees.stream()
                .collect(Collectors.groupingBy(e -> e.department, Collectors.counting()));
        System.out.println("   groupingBy + counting: " + countByDept);

        // groupingBy with averaging
        Map<String, Double> avgSalaryByDept = employees.stream()
                .collect(Collectors.groupingBy(e -> e.department,
                        Collectors.averagingDouble(e -> e.salary)));
        System.out.println("   groupingBy + averaging: " + avgSalaryByDept);

        // partitioningBy (boolean grouping)
        Map<Boolean, List<Employee>> partitioned = employees.stream()
                .collect(Collectors.partitioningBy(e -> e.salary > 55000));
        System.out.println("   partitioningBy (salary>55000): " + partitioned.get(true).size() + " high earners");

        // summarizingDouble
        DoubleSummaryStatistics stats = employees.stream()
                .collect(Collectors.summarizingDouble(e -> e.salary));
        System.out.println("   Statistics - Count: " + stats.getCount() +
                ", Avg: " + stats.getAverage() + ", Max: " + stats.getMax());

        // reducing
        Optional<Employee> highestPaid = employees.stream()
                .collect(Collectors.maxBy(Comparator.comparing(e -> e.salary)));
        System.out.println("   Highest paid: " + highestPaid.map(e -> e.name).orElse("none"));

        System.out.println();
    }

    static class Employee {
        String name;
        String department;
        double salary;

        Employee(String name, String department, double salary) {
            this.name = name;
            this.department = department;
            this.salary = salary;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         7. DATE/TIME API
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateDateTimeAPI() {
        System.out.println("▶ 7. DATE/TIME API (java.time):");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // LocalDate
        LocalDate today = LocalDate.now();
        LocalDate specific = LocalDate.of(2024, 1, 15);
        System.out.println("   LocalDate.now(): " + today);
        System.out.println("   LocalDate.of(2024,1,15): " + specific);

        // LocalTime
        LocalTime now = LocalTime.now();
        LocalTime specific_time = LocalTime.of(14, 30, 45);
        System.out.println("   LocalTime.now(): " + now);
        System.out.println("   LocalTime.of(14,30,45): " + specific_time);

        // LocalDateTime
        LocalDateTime dateTime = LocalDateTime.now();
        System.out.println("   LocalDateTime.now(): " + dateTime);

        // ZonedDateTime
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        System.out.println("   ZonedDateTime (Asia/Kolkata): " + zonedDateTime);

        // Instant (machine timestamp)
        Instant instant = Instant.now();
        System.out.println("   Instant.now(): " + instant);

        // Date manipulation
        LocalDate tomorrow = today.plusDays(1);
        LocalDate nextMonth = today.plusMonths(1);
        LocalDate lastYear = today.minusYears(1);
        System.out.println("   tomorrow: " + tomorrow + ", nextMonth: " + nextMonth);

        // Period (date-based)
        Period period = Period.between(specific, today);
        System.out.println("   Period between dates: " + period);

        // Duration (time-based)
        Duration duration = Duration.ofHours(2).plusMinutes(30);
        System.out.println("   Duration: " + duration);

        // Formatting
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formatted = dateTime.format(formatter);
        System.out.println("   Formatted: " + formatted);

        // Parsing
        LocalDate parsed = LocalDate.parse("2024-01-15");
        System.out.println("   Parsed: " + parsed);

        // Comparison
        boolean isBefore = specific.isBefore(today);
        boolean isAfter = specific.isAfter(today);
        System.out.println("   2024-01-15 isBefore today: " + isBefore);

        System.out.println();
    }
}

