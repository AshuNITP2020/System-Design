package com.learning.streams;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import java.util.stream.Collectors;

/**
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                                                                                                ║
 * ║                          STREAM API - INTERMEDIATE & TERMINAL OPERATIONS                                       ║
 * ║                                    COMPREHENSIVE GUIDE WITH EXAMPLES                                            ║
 * ║                                                                                                                ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * WHAT IS A STREAM?
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  • NOT a data structure - it's a pipeline for processing data                                                  │
 * │  • Doesn't store elements - processes from source (Collection, Array, I/O)                                     │
 * │  • Lazy evaluation - intermediate operations are NOT executed until terminal operation                         │
 * │  • Can be consumed only ONCE (IllegalStateException if reused)                                                 │
 * │  • Can be sequential or parallel                                                                               │
 * │  • Functional programming style - no side effects (ideally)                                                   │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * STREAM PIPELINE STRUCTURE:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │  SOURCE → INTERMEDIATE → INTERMEDIATE → ... → TERMINAL                                                        │
 * │  (Collection)  (filter)    (map)              (collect)                                                       │
 * │                                                                                                                │
 * │  Example:                                                                                                      │
 * │  list.stream()                                                                                                │
 * │      .filter(x -> x > 10)      ← Intermediate (lazy)                                                          │
 * │      .map(x -> x * 2)          ← Intermediate (lazy)                                                          │
 * │      .sorted()                 ← Intermediate (lazy)                                                          │
 * │      .collect(toList())        ← Terminal (triggers execution)                                               │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * KEY DIFFERENCES:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │  INTERMEDIATE OPERATIONS:                      TERMINAL OPERATIONS:                                            │
 * │  • Return Stream<T>                           • Return non-Stream result                                       │
 * │  • Lazy (not executed immediately)            • Eager (triggers execution)                                     │
 * │  • Can be chained                              • Ends the pipeline                                             │
 * │  • Stateless or Stateful                       • Consumes the stream                                            │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * @author Stream API Guide
 */
public class StreamOperationsGuide {

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════");
        System.out.println("              STREAM API - INTERMEDIATE & TERMINAL OPERATIONS GUIDE");
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════\n");

        demonstrateIntermediateOperations();
        demonstrateTerminalOperations();
        demonstrateStreamCreation();
        demonstrateParallelStreams();
        demonstrateCollectors();
        demonstrateAdvancedConcepts();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                     INTERMEDIATE OPERATIONS (LAZY)
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateIntermediateOperations() {
        System.out.println("╔═══════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    INTERMEDIATE OPERATIONS (LAZY EVALUATION)                      ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════════╝\n");

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 2, 3, 4);

        // ───────────────────────────────────────────────────────────────────────────────
        // 1. filter(Predicate<T>) - Filter elements based on condition
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("1. filter(Predicate<T>) - Keep elements matching condition:");
        List<Integer> evens = numbers.stream()
                .filter(n -> n % 2 == 0)
                .collect(Collectors.toList());
        System.out.println("   Input: " + numbers);
        System.out.println("   filter(n -> n % 2 == 0): " + evens);
        System.out.println();

        // Multiple filters (chained)
        List<Integer> filtered = numbers.stream()
                .filter(n -> n > 3)
                .filter(n -> n < 8)
                .collect(Collectors.toList());
        System.out.println("   Chained filters (n > 3 && n < 8): " + filtered);
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // 2. map(Function<T, R>) - Transform each element (one-to-one)
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("2. map(Function<T, R>) - Transform each element:");
        List<String> words = Arrays.asList("apple", "banana", "cherry", "date");
        List<Integer> lengths = words.stream()
                .map(String::length)
                .collect(Collectors.toList());
        System.out.println("   Input: " + words);
        System.out.println("   map(String::length): " + lengths);
        System.out.println();

        // Transform to different type
        List<String> upperCase = words.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        System.out.println("   map(String::toUpperCase): " + upperCase);
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // 3. flatMap(Function<T, Stream<R>>) - Flatten nested structures
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("3. flatMap(Function<T, Stream<R>>) - Flatten nested structures:");
        List<List<Integer>> nested = Arrays.asList(
                Arrays.asList(1, 2, 3),
                Arrays.asList(4, 5),
                Arrays.asList(6, 7, 8, 9)
        );
        System.out.println("   Input (nested): " + nested);

        // Using map (doesn't flatten)
        List<Stream<Integer>> mapped = nested.stream()
                .map(List::stream)
                .collect(Collectors.toList());
        System.out.println("   map(List::stream): Still nested Stream<Stream<Integer>>");

        // Using flatMap (flattens)
        List<Integer> flattened = nested.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        System.out.println("   flatMap(List::stream): " + flattened);
        System.out.println();

        // Real-world example: Extract all words from sentences
        List<String> sentences = Arrays.asList(
                "Hello World",
                "Java Streams",
                "Functional Programming"
        );
        List<String> allWords = sentences.stream()
                .flatMap(sentence -> Arrays.stream(sentence.split(" ")))
                .collect(Collectors.toList());
        System.out.println("   Sentences: " + sentences);
        System.out.println("   All words (flatMap): " + allWords);
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // 4. distinct() - Remove duplicates (uses equals() and hashCode())
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("4. distinct() - Remove duplicates:");
        List<Integer> withDuplicates = Arrays.asList(1, 2, 2, 3, 3, 3, 4, 5, 5);
        List<Integer> unique = withDuplicates.stream()
                .distinct()
                .collect(Collectors.toList());
        System.out.println("   Input: " + withDuplicates);
        System.out.println("   distinct(): " + unique);
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // 5. sorted() - Sort elements (natural order)
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("5. sorted() - Sort in natural order:");
        List<Integer> unsorted = Arrays.asList(5, 2, 8, 1, 9, 3);
        List<Integer> sorted = unsorted.stream()
                .sorted()
                .collect(Collectors.toList());
        System.out.println("   Input: " + unsorted);
        System.out.println("   sorted(): " + sorted);
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // 6. sorted(Comparator<T>) - Custom sorting
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("6. sorted(Comparator<T>) - Custom sorting:");
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");
        List<String> sortedByLength = names.stream()
                .sorted(Comparator.comparing(String::length))
                .collect(Collectors.toList());
        System.out.println("   Input: " + names);
        System.out.println("   sorted(by length): " + sortedByLength);

        // Reverse order
        List<String> reverseSorted = names.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        System.out.println("   sorted(reverse): " + reverseSorted);
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // 7. limit(long n) - Take first n elements
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("7. limit(long n) - Take first n elements:");
        List<Integer> limited = numbers.stream()
                .limit(5)
                .collect(Collectors.toList());
        System.out.println("   Input: " + numbers);
        System.out.println("   limit(5): " + limited);
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // 8. skip(long n) - Skip first n elements
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("8. skip(long n) - Skip first n elements:");
        List<Integer> skipped = numbers.stream()
                .skip(5)
                .collect(Collectors.toList());
        System.out.println("   Input: " + numbers);
        System.out.println("   skip(5): " + skipped);
        System.out.println();

        // Combined: skip and limit (pagination)
        List<Integer> paginated = numbers.stream()
                .skip(2)
                .limit(5)
                .collect(Collectors.toList());
        System.out.println("   skip(2).limit(5) [pagination]: " + paginated);
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // 9. peek(Consumer<T>) - Debug/inspect elements (side effect)
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("9. peek(Consumer<T>) - Debug/inspect elements:");
        System.out.print("   Processing with peek: ");
        List<Integer> peeked = numbers.stream()
                .filter(n -> n > 5)
                .peek(n -> System.out.print(n + " "))
                .map(n -> n * 2)
                .collect(Collectors.toList());
        System.out.println();
        System.out.println("   Result: " + peeked);
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // 10. takeWhile(Predicate<T>) - Java 9+ - Take while condition is true
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("10. takeWhile(Predicate<T>) - Java 9+ - Take while condition is true:");
        List<Integer> ordered = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<Integer> taken = ordered.stream()
                .takeWhile(n -> n < 6)
                .collect(Collectors.toList());
        System.out.println("   Input: " + ordered);
        System.out.println("   takeWhile(n < 6): " + taken);
        System.out.println("   Note: Stops at first element that doesn't match");
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // 11. dropWhile(Predicate<T>) - Java 9+ - Drop while condition is true
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("11. dropWhile(Predicate<T>) - Java 9+ - Drop while condition is true:");
        List<Integer> dropped = ordered.stream()
                .dropWhile(n -> n < 6)
                .collect(Collectors.toList());
        System.out.println("   Input: " + ordered);
        System.out.println("   dropWhile(n < 6): " + dropped);
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // STATEFUL vs STATELESS INTERMEDIATE OPERATIONS
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("STATEFUL vs STATELESS Operations:");
        System.out.println("   STATELESS (can process elements independently):");
        System.out.println("   • filter(), map(), flatMap(), peek()");
        System.out.println();
        System.out.println("   STATEFUL (need to see all/elements to process):");
        System.out.println("   • distinct(), sorted(), limit(), skip()");
        System.out.println("   • Stateful operations may require buffering");
        System.out.println();

        System.out.println("═══════════════════════════════════════════════════════════════════════════════════\n");
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                     TERMINAL OPERATIONS (EAGER)
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateTerminalOperations() {
        System.out.println("╔═══════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                      TERMINAL OPERATIONS (EAGER EVALUATION)                        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════════╝\n");

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // ───────────────────────────────────────────────────────────────────────────────
        // 1. forEach(Consumer<T>) - Iterate and perform side effect
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("1. forEach(Consumer<T>) - Iterate and perform side effect:");
        System.out.print("   ");
        numbers.stream()
                .filter(n -> n % 2 == 0)
                .forEach(n -> System.out.print(n + " "));
        System.out.println();
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // 2. collect(Collector<T, A, R>) - Collect to Collection
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("2. collect(Collector) - Collect to Collection:");
        List<Integer> collected = numbers.stream()
                .filter(n -> n > 5)
                .collect(Collectors.toList());
        System.out.println("   collect(toList()): " + collected);

        Set<Integer> toSet = numbers.stream()
                .collect(Collectors.toSet());
        System.out.println("   collect(toSet()): " + toSet);
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // 3. reduce(identity, BinaryOperator<T>) - Reduce to single value
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("3. reduce(identity, BinaryOperator) - Reduce to single value:");
        int sum = numbers.stream()
                .reduce(0, Integer::sum);
        System.out.println("   reduce(0, Integer::sum): " + sum);

        int product = numbers.stream()
                .limit(5)
                .reduce(1, (a, b) -> a * b);
        System.out.println("   reduce(1, multiply) [first 5]: " + product);

        // reduce without identity (returns Optional)
        Optional<Integer> max = numbers.stream()
                .reduce(Integer::max);
        System.out.println("   reduce(Integer::max) [no identity]: " + max.orElse(-1));
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // 4. count() - Count elements
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("4. count() - Count elements:");
        long count = numbers.stream()
                .filter(n -> n > 5)
                .count();
        System.out.println("   count(>5): " + count);
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // 5. findFirst() - Get first element (returns Optional)
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("5. findFirst() - Get first element:");
        Optional<Integer> first = numbers.stream()
                .filter(n -> n > 5)
                .findFirst();
        System.out.println("   findFirst(>5): " + first.orElse(-1));
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // 6. findAny() - Get any element (useful in parallel streams)
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("6. findAny() - Get any element:");
        Optional<Integer> any = numbers.stream()
                .filter(n -> n > 5)
                .findAny();
        System.out.println("   findAny(>5): " + any.orElse(-1));
        System.out.println("   Note: In parallel streams, findAny() is faster than findFirst()");
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // 7. anyMatch(Predicate<T>) - Check if any element matches
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("7. anyMatch(Predicate) - Check if any element matches:");
        boolean hasEven = numbers.stream()
                .anyMatch(n -> n % 2 == 0);
        System.out.println("   anyMatch(even): " + hasEven);

        boolean hasNegative = numbers.stream()
                .anyMatch(n -> n < 0);
        System.out.println("   anyMatch(negative): " + hasNegative);
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // 8. allMatch(Predicate<T>) - Check if all elements match
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("8. allMatch(Predicate) - Check if all elements match:");
        boolean allPositive = numbers.stream()
                .allMatch(n -> n > 0);
        System.out.println("   allMatch(positive): " + allPositive);

        boolean allEven = numbers.stream()
                .allMatch(n -> n % 2 == 0);
        System.out.println("   allMatch(even): " + allEven);
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // 9. noneMatch(Predicate<T>) - Check if no elements match
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("9. noneMatch(Predicate) - Check if no elements match:");
        boolean noNegative = numbers.stream()
                .noneMatch(n -> n < 0);
        System.out.println("   noneMatch(negative): " + noNegative);

        boolean noOdd = numbers.stream()
                .noneMatch(n -> n % 2 != 0);
        System.out.println("   noneMatch(odd): " + noOdd);
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // 10. min(Comparator<T>) - Find minimum (returns Optional)
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("10. min(Comparator) - Find minimum:");
        Optional<Integer> min = numbers.stream()
                .min(Integer::compare);
        System.out.println("   min(): " + min.orElse(-1));

        Optional<String> shortest = Arrays.asList("apple", "pie", "banana").stream()
                .min(Comparator.comparing(String::length));
        System.out.println("   min(by length): " + shortest.orElse("none"));
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // 11. max(Comparator<T>) - Find maximum (returns Optional)
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("11. max(Comparator) - Find maximum:");
        // Optional<Integer> max = numbers.stream()
        //         .max(Integer::compare);
        // System.out.println("   max(): " + max.orElse(-1));
        // System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // 12. toArray() - Convert to array
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("12. toArray() - Convert to array:");
        Integer[] array = numbers.stream()
                .filter(n -> n > 5)
                .toArray(Integer[]::new);
        System.out.println("   toArray(): " + Arrays.toString(array));
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // SHORT-CIRCUIT TERMINAL OPERATIONS
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("SHORT-CIRCUIT Operations (may not process all elements):");
        System.out.println("   • findFirst(), findAny()");
        System.out.println("   • anyMatch(), allMatch(), noneMatch()");
        System.out.println("   • limit() [intermediate but short-circuit]");
        System.out.println();

        System.out.println("═══════════════════════════════════════════════════════════════════════════════════\n");
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                     STREAM CREATION
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateStreamCreation() {
        System.out.println("╔═══════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                              STREAM CREATION METHODS                              ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════════╝\n");

        // From Collection
        System.out.println("1. From Collection:");
        List<String> list = Arrays.asList("a", "b", "c");
        Stream<String> stream1 = list.stream();
        System.out.println("   list.stream()");

        // From Array
        System.out.println("\n2. From Array:");
        String[] array = {"a", "b", "c"};
        Stream<String> stream2 = Arrays.stream(array);
        System.out.println("   Arrays.stream(array)");

        // Using Stream.of()
        System.out.println("\n3. Using Stream.of():");
        Stream<String> stream3 = Stream.of("a", "b", "c");
        System.out.println("   Stream.of(\"a\", \"b\", \"c\")");

        // Using Stream.builder()
        System.out.println("\n4. Using Stream.builder():");
        Stream<String> stream4 = Stream.<String>builder()
                .add("a")
                .add("b")
                .add("c")
                .build();
        System.out.println("   Stream.builder().add(...).build()");

        // Infinite streams
        System.out.println("\n5. Infinite Streams:");
        Stream<Integer> infinite = Stream.iterate(0, n -> n + 2);
        List<Integer> first10 = infinite.limit(10).collect(Collectors.toList());
        System.out.println("   Stream.iterate(0, n -> n + 2).limit(10): " + first10);

        Stream<Integer> random = Stream.generate(() -> (int) (Math.random() * 100));
        List<Integer> random10 = random.limit(10).collect(Collectors.toList());
        System.out.println("   Stream.generate(() -> random).limit(10): " + random10);

        // From String
        System.out.println("\n6. From String (chars):");
        IntStream charStream = "Hello".chars();
        System.out.println("   \"Hello\".chars()");

        // Empty stream
        System.out.println("\n7. Empty Stream:");
        Stream<String> empty = Stream.empty();
        System.out.println("   Stream.empty()");

        System.out.println("\n═══════════════════════════════════════════════════════════════════════════════════\n");
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                     PARALLEL STREAMS
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateParallelStreams() {
        System.out.println("╔═══════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                              PARALLEL STREAMS                                      ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════════╝\n");

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);

        System.out.println("Sequential Stream:");
        long start = System.currentTimeMillis();
        int sequentialSum = numbers.stream()
                .mapToInt(n -> {
                    // Simulate some work
                    try { Thread.sleep(10); } catch (InterruptedException e) {}
                    return n * 2;
                })
                .sum();
        long sequentialTime = System.currentTimeMillis() - start;
        System.out.println("   Sum: " + sequentialSum + ", Time: " + sequentialTime + "ms");

        System.out.println("\nParallel Stream:");
        start = System.currentTimeMillis();
        int parallelSum = numbers.parallelStream()
                .mapToInt(n -> {
                    // Simulate some work
                    try { Thread.sleep(10); } catch (InterruptedException e) {}
                    return n * 2;
                })
                .sum();
        long parallelTime = System.currentTimeMillis() - start;
        System.out.println("   Sum: " + parallelSum + ", Time: " + parallelTime + "ms");

        System.out.println("\nWhen to use Parallel Streams:");
        System.out.println("   ✓ Large datasets");
        System.out.println("   ✓ CPU-intensive operations");
        System.out.println("   ✓ Independent operations (no shared state)");
        System.out.println("   ✗ Small datasets (overhead > benefit)");
        System.out.println("   ✗ Operations with side effects");
        System.out.println("   ✗ Ordered operations (sorted, limit)");

        System.out.println("\n═══════════════════════════════════════════════════════════════════════════════════\n");
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                     COLLECTORS (ADVANCED)
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateCollectors() {
        System.out.println("╔═══════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                              COLLECTORS (ADVANCED)                                 ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════════╝\n");

        List<Employee> employees = Arrays.asList(
                new Employee("Alice", "IT", 60000),
                new Employee("Bob", "HR", 50000),
                new Employee("Charlie", "IT", 70000),
                new Employee("Diana", "HR", 55000),
                new Employee("Eve", "IT", 65000),
                new Employee("Frank", "Finance", 60000)
        );

        // groupingBy
        System.out.println("1. groupingBy - Group by department:");
        Map<String, List<Employee>> byDept = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment));
        byDept.forEach((dept, emps) ->
                System.out.println("   " + dept + ": " + emps.stream()
                        .map(Employee::getName)
                        .collect(Collectors.joining(", "))));
        System.out.println();

        // groupingBy with counting
        System.out.println("2. groupingBy + counting - Count by department:");
        Map<String, Long> countByDept = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.counting()));
        System.out.println("   " + countByDept);
        System.out.println();

        // groupingBy with averaging
        System.out.println("3. groupingBy + averaging - Average salary by department:");
        Map<String, Double> avgSalary = employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.averagingDouble(Employee::getSalary)));
        System.out.println("   " + avgSalary);
        System.out.println();

        // partitioningBy
        System.out.println("4. partitioningBy - Partition by condition:");
        Map<Boolean, List<Employee>> partitioned = employees.stream()
                .collect(Collectors.partitioningBy(e -> e.getSalary() > 55000));
        System.out.println("   High earners (>55000): " + partitioned.get(true).size());
        System.out.println("   Others: " + partitioned.get(false).size());
        System.out.println();

        // joining
        System.out.println("5. joining - Join strings:");
        String joined = employees.stream()
                .map(Employee::getName)
                .collect(Collectors.joining(", ", "[", "]"));
        System.out.println("   " + joined);
        System.out.println();

        // summarizingDouble
        System.out.println("6. summarizingDouble - Statistics:");
        DoubleSummaryStatistics stats = employees.stream()
                .collect(Collectors.summarizingDouble(Employee::getSalary));
        System.out.println("   Count: " + stats.getCount());
        System.out.println("   Sum: " + stats.getSum());
        System.out.println("   Average: " + stats.getAverage());
        System.out.println("   Min: " + stats.getMin());
        System.out.println("   Max: " + stats.getMax());
        System.out.println();

        System.out.println("═══════════════════════════════════════════════════════════════════════════════════\n");
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                     ADVANCED CONCEPTS
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateAdvancedConcepts() {
        System.out.println("╔═══════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                            ADVANCED STREAM CONCEPTS                                ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════════╝\n");

        // Lazy Evaluation Demonstration
        System.out.println("1. LAZY EVALUATION - Intermediate operations don't execute until terminal:");
        System.out.println("   Stream pipeline is built but NOT executed:");
        Stream<Integer> lazyStream = Arrays.asList(1, 2, 3, 4, 5).stream()
                .filter(n -> {
                    System.out.println("   Filtering: " + n);
                    return n > 2;
                })
                .map(n -> {
                    System.out.println("   Mapping: " + n);
                    return n * 2;
                });
        System.out.println("   (No output above - stream not executed yet)");
        System.out.println("   Now calling terminal operation:");
        List<Integer> result = lazyStream.collect(Collectors.toList());
        System.out.println("   Result: " + result);
        System.out.println();

        // Stream Reuse (Illegal)
        System.out.println("2. STREAM REUSE - Streams can only be consumed once:");
        Stream<Integer> stream = Stream.of(1, 2, 3);
        stream.forEach(System.out::println);
        try {
            stream.forEach(System.out::println); // IllegalStateException
        } catch (IllegalStateException e) {
            System.out.println("   ✓ Caught IllegalStateException: " + e.getMessage());
        }
        System.out.println();

        // Primitive Streams
        System.out.println("3. PRIMITIVE STREAMS - Specialized streams for primitives:");
        int[] intArray = {1, 2, 3, 4, 5};
        int sum = Arrays.stream(intArray)
                .sum(); // No boxing/unboxing
        System.out.println("   IntStream.sum(): " + sum);

        double average = Arrays.stream(intArray)
                .average()
                .orElse(0.0);
        System.out.println("   IntStream.average(): " + average);
        System.out.println();

        // Stream vs Collection
        System.out.println("4. STREAM vs COLLECTION:");
        System.out.println("   ┌─────────────────────┬──────────────────────┬─────────────────────┐");
        System.out.println("   │ Feature             │ Collection           │ Stream              │");
        System.out.println("   ├─────────────────────┼──────────────────────┼─────────────────────┤");
        System.out.println("   │ Stores data         │ Yes                  │ No                  │");
        System.out.println("   │ Can iterate multiple│ Yes                  │ No (single use)     │");
        System.out.println("   │ External iteration  │ Yes                  │ No (internal)       │");
        System.out.println("   │ Eager evaluation    │ Yes                  │ No (lazy)           │");
        System.out.println("   │ Parallel support    │ Manual               │ Built-in            │");
        System.out.println("   └─────────────────────┴──────────────────────┴─────────────────────┘");
        System.out.println();

        // Best Practices
        System.out.println("5. BEST PRACTICES:");
        System.out.println("   ✓ Use method references when possible: map(String::length)");
        System.out.println("   ✓ Keep operations stateless (no shared mutable state)");
        System.out.println("   ✓ Avoid side effects in intermediate operations");
        System.out.println("   ✓ Use parallel streams only for large datasets");
        System.out.println("   ✓ Prefer primitive streams (IntStream) to avoid boxing");
        System.out.println("   ✓ Use findFirst() for ordered, findAny() for parallel");
        System.out.println();

        System.out.println("═══════════════════════════════════════════════════════════════════════════════════\n");
    }

    // Helper class for examples
    static class Employee {
        private String name;
        private String department;
        private double salary;

        Employee(String name, String department, double salary) {
            this.name = name;
            this.department = department;
            this.salary = salary;
        }

        String getName() { return name; }
        String getDepartment() { return department; }
        double getSalary() { return salary; }

        @Override
        public String toString() {
            return name + "(" + department + ", $" + salary + ")";
        }
    }
}