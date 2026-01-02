package com.learning.streams;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import java.util.stream.Collector;
// import java.util.stream.Collectors;

/**
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                                                                                                ║
 * ║                              COLLECT() & COLLECTORS - COMPREHENSIVE GUIDE                                      ║
 * ║                                    ALL TYPES & USAGE EXAMPLES                                                  ║
 * ║                                                                                                                ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * WHAT IS collect()?
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  • Terminal operation that collects stream elements into a result container                                      │
 * │  • Most flexible terminal operation - can collect to any Collection type                                        │
 * │  • Uses Collector interface to specify how to accumulate elements                                               │
 * │  • Syntax: stream.collect(Collector<T, A, R>)                                                                   │
 * │    - T: Type of input elements                                                                                  │
 * │    - A: Type of accumulator (mutable container)                                                                 │
 * │    - R: Type of result                                                                                          │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * WHAT IS Collectors?
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  • Utility class providing ready-to-use Collector implementations                                               │
 * │  • Contains static factory methods for common collection operations                                             │
 * │  • Located in: java.util.stream.Collectors                                                                      │
 * │  • Note: This class is named Collectors, so we use fully qualified name java.util.stream.Collectors            │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * COLLECTOR CATEGORIES:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  1. TO COLLECTIONS    - toList(), toSet(), toMap(), toCollection()                                             │
 * │  2. JOINING           - joining()                                                                              │
 * │  3. GROUPING           - groupingBy(), partitioningBy()                                                         │
 * │  4. AGGREGATING        - counting(), summingInt(), averagingDouble(), minBy(), maxBy()                         │
 * │  5. SUMMARIZING        - summarizingInt(), summarizingDouble(), summarizingLong()                                │
 * │  6. REDUCING           - reducing()                                                                             │
 * │  7. CUSTOM             - Collector.of()                                                                          │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * @author Collectors Guide
 */
public class Collectors {

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════");
        System.out.println("                    COLLECT() & COLLECTORS - COMPREHENSIVE GUIDE");
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════\n");

        demonstrateBasicCollectors();
        demonstrateJoiningCollectors();
        demonstrateGroupingCollectors();
        demonstrateAggregatingCollectors();
        demonstrateSummarizingCollectors();
        demonstrateReducingCollectors();
        demonstrateChainingCollectors();
        demonstrateCustomCollectors();
        demonstrateAdvancedUsages();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                     1. BASIC COLLECTORS - TO COLLECTIONS
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateBasicCollectors() {
        System.out.println("╔═══════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                      1. BASIC COLLECTORS - TO COLLECTIONS                         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════════╝\n");

        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David", "Alice", "Bob");

        // ───────────────────────────────────────────────────────────────────────────────
        // THEORY: toList()
        // ───────────────────────────────────────────────────────────────────────────────
        // • Collects stream elements into a List
        // • Preserves insertion order
        // • Allows duplicate elements
        // • Returns mutable List (ArrayList implementation)
        // • Most commonly used collector
        // • Syntax: collect(Collectors.toList())
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("1.1 toList() - Collect to List:");
        List<String> list = names.stream()
                .filter(n -> n.length() > 3)
                .collect(java.util.stream.Collectors.toList());
        System.out.println("   Input: " + names);
        System.out.println("   collect(toList()): " + list);
        System.out.println("   Note: Preserves order, allows duplicates");
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // THEORY: toSet()
        // ───────────────────────────────────────────────────────────────────────────────
        // • Collects stream elements into a Set
        // • Automatically removes duplicates (uses equals() and hashCode())
        // • Order not guaranteed (uses HashSet by default)
        // • Returns mutable Set
        // • Use when you need unique elements
        // • Syntax: collect(Collectors.toSet())
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("1.2 toSet() - Collect to Set (removes duplicates):");
        Set<String> set = names.stream()
                .collect(java.util.stream.Collectors.toSet());
        System.out.println("   Input: " + names);
        System.out.println("   collect(toSet()): " + set);
        System.out.println("   Note: No duplicates, order not guaranteed (HashSet)");
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // THEORY: toCollection(Supplier)
        // ───────────────────────────────────────────────────────────────────────────────
        // • Collects to a specific Collection implementation
        // • Provides control over which Collection type to use
        // • Takes a Supplier that creates the collection (e.g., LinkedList::new)
        // • Useful when you need specific characteristics:
        //   - LinkedList: Insertion order, fast insertions
        //   - TreeSet: Sorted order, no duplicates
        //   - LinkedHashSet: Insertion order + no duplicates
        // • Syntax: collect(Collectors.toCollection(Supplier))
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("1.3 toCollection(Supplier) - Collect to specific Collection type:");
        LinkedList<String> linkedList = names.stream()
                .collect(java.util.stream.Collectors.toCollection(LinkedList::new));
        System.out.println("   collect(toCollection(LinkedList::new)): " + linkedList);

        TreeSet<String> treeSet = names.stream()
                .collect(java.util.stream.Collectors.toCollection(TreeSet::new));
        System.out.println("   collect(toCollection(TreeSet::new)): " + treeSet);
        System.out.println("   Note: Use when you need specific Collection implementation");
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // THEORY: toMap(keyMapper, valueMapper)
        // ───────────────────────────────────────────────────────────────────────────────
        // • Collects stream elements into a Map
        // • Requires two functions:
        //   - keyMapper: Function to extract key from element
        //   - valueMapper: Function to extract value from element
        // • Throws IllegalStateException if duplicate keys found
        // • Returns HashMap (order not guaranteed)
        // • Use when you need key-value pairs
        // • Syntax: collect(Collectors.toMap(keyMapper, valueMapper))
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("1.4 toMap(keyMapper, valueMapper) - Collect to Map:");
        List<Employee> employees = createEmployees();
        Map<String, Double> nameToSalary = employees.stream()
                .collect(java.util.stream.Collectors.toMap(
                        Employee::getName,
                        Employee::getSalary));
        System.out.println("   collect(toMap(name, salary)): " + nameToSalary);
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // THEORY: toMap(keyMapper, valueMapper, mergeFunction)
        // ───────────────────────────────────────────────────────────────────────────────
        // • Same as toMap() but handles duplicate keys
        // • mergeFunction: BinaryOperator that resolves conflicts when duplicate keys
        // • mergeFunction receives: (existingValue, newValue) -> resolvedValue
        // • Common merge strategies:
        //   - (old, new) -> new : Keep latest value
        //   - (old, new) -> old : Keep first value
        //   - (old, new) -> old + new : Combine values (if numeric)
        //   - (old, new) -> Math.max(old, new) : Keep maximum
        // • Syntax: collect(Collectors.toMap(keyMapper, valueMapper, mergeFunction))
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("1.5 toMap(..., mergeFunction) - Handle duplicate keys:");
        List<Employee> duplicateNames = Arrays.asList(
                new Employee("Alice", "IT", 60000),
                new Employee("Alice", "HR", 65000) // Duplicate key
        );
        Map<String, Double> withMerge = duplicateNames.stream()
                .collect(java.util.stream.Collectors.toMap(
                        Employee::getName,
                        Employee::getSalary,
                        (existing, replacement) -> Math.max(existing, replacement) // Keep higher salary
                ));
        System.out.println("   Duplicate keys handled with merge (keep max): " + withMerge);
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // THEORY: toMap(..., mapSupplier)
        // ───────────────────────────────────────────────────────────────────────────────
        // • Same as toMap() but allows specifying Map implementation
        // • mapSupplier: Supplier that creates the Map instance
        // • Useful for:
        //   - LinkedHashMap: Preserve insertion order
        //   - TreeMap: Maintain sorted order
        //   - ConcurrentHashMap: Thread-safe map
        // • Syntax: collect(Collectors.toMap(keyMapper, valueMapper, mergeFunction, mapSupplier))
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("1.6 toMap(..., mapSupplier) - Specify Map implementation:");
        LinkedHashMap<String, Double> linkedMap = employees.stream()
                .collect(java.util.stream.Collectors.toMap(
                        Employee::getName,
                        Employee::getSalary,
                        (e1, e2) -> e1, // Merge function (if duplicates)
                        LinkedHashMap::new // Preserve insertion order
                ));
        System.out.println("   collect(toMap(..., LinkedHashMap::new)): " + linkedMap);
        System.out.println("   Note: Preserves insertion order");
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // THEORY: toUnmodifiableList/Set/Map (Java 10+)
        // ───────────────────────────────────────────────────────────────────────────────
        // • Creates immutable collections
        // • Returns unmodifiable view (throws UnsupportedOperationException on modification)
        // • Thread-safe (immutable)
        // • Useful for returning from methods when you don't want caller to modify
        // • Better than Collections.unmodifiableList() - returns truly immutable collection
        // • Syntax: collect(Collectors.toUnmodifiableList())
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("1.7 toUnmodifiableList/Set/Map - Java 10+ (Immutable):");
        List<String> unmodifiable = names.stream()
                .filter(n -> n.length() > 3)
                .collect(java.util.stream.Collectors.toUnmodifiableList());
        System.out.println("   collect(toUnmodifiableList()): " + unmodifiable);
        System.out.println("   Note: Returns immutable collection (throws UnsupportedOperationException on modify)");
        System.out.println();

        System.out.println("═══════════════════════════════════════════════════════════════════════════════════\n");
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                     2. JOINING COLLECTORS
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateJoiningCollectors() {
        System.out.println("╔═══════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                             2. JOINING COLLECTORS                                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════════╝\n");

        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");

        // ───────────────────────────────────────────────────────────────────────────────
        // THEORY: joining()
        // ───────────────────────────────────────────────────────────────────────────────
        // • Concatenates stream elements into a single String
        // • No delimiter - elements are concatenated directly
        // • Only works with Stream<String>
        // • Use for simple string concatenation
        // • Syntax: collect(Collectors.joining())
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("2.1 joining() - Simple concatenation:");
        String joined = names.stream()
                .collect(java.util.stream.Collectors.joining());
        System.out.println("   Input: " + names);
        System.out.println("   collect(joining()): " + joined);
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // THEORY: joining(delimiter)
        // ───────────────────────────────────────────────────────────────────────────────
        // • Concatenates with a delimiter between elements
        // • delimiter: String inserted between each pair of elements
        // • Common delimiters: ", ", " | ", "-", "\n" (newline)
        // • Use for CSV, formatted output, etc.
        // • Syntax: collect(Collectors.joining(delimiter))
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("2.2 joining(delimiter) - With delimiter:");
        String withDelimiter = names.stream()
                .collect(java.util.stream.Collectors.joining(", "));
        System.out.println("   collect(joining(\", \")): " + withDelimiter);
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // THEORY: joining(delimiter, prefix, suffix)
        // ───────────────────────────────────────────────────────────────────────────────
        // • Concatenates with delimiter, prefix, and suffix
        // • prefix: String added at the beginning
        // • suffix: String added at the end
        // • Useful for:
        //   - SQL IN clauses: "WHERE name IN ('a', 'b', 'c')"
        //   - JSON arrays: "[\"a\", \"b\", \"c\"]"
        //   - Formatted lists: "[a, b, c]"
        // • Syntax: collect(Collectors.joining(delimiter, prefix, suffix))
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("2.3 joining(delimiter, prefix, suffix) - With prefix and suffix:");
        String withPrefixSuffix = names.stream()
                .collect(java.util.stream.Collectors.joining(", ", "[", "]"));
        System.out.println("   collect(joining(\", \", \"[\", \"]\")): " + withPrefixSuffix);
        System.out.println();

        // Real-world example
        System.out.println("Real-world example - SQL IN clause:");
        String sqlInClause = names.stream()
                .map(name -> "'" + name + "'")
                .collect(java.util.stream.Collectors.joining(", ", "WHERE name IN (", ")"));
        System.out.println("   " + sqlInClause);
        System.out.println();

        System.out.println("═══════════════════════════════════════════════════════════════════════════════════\n");
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                     3. GROUPING COLLECTORS
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateGroupingCollectors() {
        System.out.println("╔═══════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                           3. GROUPING COLLECTORS                                    ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════════╝\n");

        List<Employee> employees = createEmployees();

        // ───────────────────────────────────────────────────────────────────────────────
        // THEORY: groupingBy(classifier)
        // ───────────────────────────────────────────────────────────────────────────────
        // • Groups elements by a classification function
        // • classifier: Function that extracts the grouping key from each element
        // • Returns Map<K, List<T>> where:
        //   - K: Type of grouping key
        //   - List<T>: All elements that share the same key
        // • Default downstream collector is toList()
        // • Returns HashMap (order not guaranteed)
        // • Use for: Grouping by category, department, status, etc.
        // • Syntax: collect(Collectors.groupingBy(classifier))
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("3.1 groupingBy(classifier) - Simple grouping:");
        Map<String, List<Employee>> byDepartment = employees.stream()
                .collect(java.util.stream.Collectors.groupingBy(Employee::getDepartment));
        System.out.println("   Group by department:");
        byDepartment.forEach((dept, emps) ->
                System.out.println("     " + dept + ": " + emps.stream()
                        .map(Employee::getName)
                        .collect(java.util.stream.Collectors.joining(", "))));
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // THEORY: groupingBy(classifier, downstream)
        // ───────────────────────────────────────────────────────────────────────────────
        // • Groups elements and applies a downstream collector to each group
        // • downstream: Collector applied to each group
        // • Common downstream collectors:
        //   - counting(): Count elements in each group
        //   - summingInt/Double(): Sum values in each group
        //   - averagingInt/Double(): Average values in each group
        //   - maxBy()/minBy(): Find max/min in each group
        //   - toSet(): Collect to Set (remove duplicates)
        // • Returns Map<K, R> where R is result of downstream collector
        // • Syntax: collect(Collectors.groupingBy(classifier, downstream))
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("3.2 groupingBy(classifier, downstream) - Grouping with downstream collector:");
        
        // Count employees per department
        Map<String, Long> countByDept = employees.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Employee::getDepartment,
                        java.util.stream.Collectors.counting()));
        System.out.println("   Count by department: " + countByDept);
        System.out.println();

        // Average salary per department
        Map<String, Double> avgSalaryByDept = employees.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Employee::getDepartment,
                        java.util.stream.Collectors.averagingDouble(Employee::getSalary)));
        System.out.println("   Average salary by department: " + avgSalaryByDept);
        System.out.println();

        // Sum salary per department
        Map<String, Double> sumSalaryByDept = employees.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Employee::getDepartment,
                        java.util.stream.Collectors.summingDouble(Employee::getSalary)));
        System.out.println("   Sum salary by department: " + sumSalaryByDept);
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // THEORY: groupingBy(classifier, mapFactory, downstream)
        // ───────────────────────────────────────────────────────────────────────────────
        // • Same as groupingBy() but allows specifying Map implementation
        // • mapFactory: Supplier that creates the Map instance
        // • Useful for:
        //   - LinkedHashMap: Preserve insertion order of groups
        //   - TreeMap: Sort groups by key
        // • Syntax: collect(Collectors.groupingBy(classifier, mapFactory, downstream))
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("3.3 groupingBy(..., mapFactory) - Specify Map implementation:");
        LinkedHashMap<String, List<Employee>> orderedGroups = employees.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Employee::getDepartment,
                        LinkedHashMap::new, // Preserve insertion order
                        java.util.stream.Collectors.toList()));
        System.out.println("   Ordered groups (LinkedHashMap): " + orderedGroups.keySet());
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // THEORY: partitioningBy(predicate)
        // ───────────────────────────────────────────────────────────────────────────────
        // • Special case of groupingBy with boolean keys
        // • Partitions elements into two groups: true and false
        // • predicate: Function that returns boolean for each element
        // • Returns Map<Boolean, List<T>>
        // • More efficient than groupingBy for boolean classification
        // • Use for: Split into two groups based on condition
        // • Syntax: collect(Collectors.partitioningBy(predicate))
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("3.4 partitioningBy(predicate) - Partition into two groups:");
        Map<Boolean, List<Employee>> partitioned = employees.stream()
                .collect(java.util.stream.Collectors.partitioningBy(e -> e.getSalary() > 55000));
        System.out.println("   Partition by salary > 55000:");
        System.out.println("     High earners: " + partitioned.get(true).size());
        System.out.println("     Others: " + partitioned.get(false).size());
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // THEORY: partitioningBy(predicate, downstream)
        // ───────────────────────────────────────────────────────────────────────────────
        // • Partitions elements and applies downstream collector
        // • Useful for: Counting, summing, averaging each partition
        // • Syntax: collect(Collectors.partitioningBy(predicate, downstream))
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("3.5 partitioningBy(predicate, downstream) - Partition with downstream:");
        Map<Boolean, Long> countByPartition = employees.stream()
                .collect(java.util.stream.Collectors.partitioningBy(
                        e -> e.getSalary() > 55000,
                        java.util.stream.Collectors.counting()));
        System.out.println("   Count by partition: " + countByPartition);
        System.out.println();

        // Multi-level grouping
        System.out.println("3.6 Multi-level grouping (groupingBy within groupingBy):");
        Map<String, Map<String, List<Employee>>> multiLevel = employees.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Employee::getDepartment,
                        java.util.stream.Collectors.groupingBy(e -> 
                                e.getSalary() > 60000 ? "High" : "Low")));
        System.out.println("   Group by department, then by salary level:");
        multiLevel.forEach((dept, salaryGroups) -> {
            System.out.println("     " + dept + ":");
            salaryGroups.forEach((level, emps) ->
                    System.out.println("       " + level + ": " + emps.size() + " employees"));
        });
        System.out.println();

        System.out.println("═══════════════════════════════════════════════════════════════════════════════════\n");
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                     4. AGGREGATING COLLECTORS
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateAggregatingCollectors() {
        System.out.println("╔═══════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                         4. AGGREGATING COLLECTORS                                     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════════╝\n");

        List<Employee> employees = createEmployees();

        // ───────────────────────────────────────────────────────────────────────────────
        // THEORY: counting()
        // ───────────────────────────────────────────────────────────────────────────────
        // • Counts the number of elements in the stream
        // • Returns Long (not int)
        // • Equivalent to: mapToLong(e -> 1L).sum()
        // • Commonly used as downstream collector in groupingBy
        // • Syntax: collect(Collectors.counting())
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("4.1 counting() - Count elements:");
        Long count = employees.stream()
                .collect(java.util.stream.Collectors.counting());
        System.out.println("   Total employees: " + count);
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // THEORY: summingInt/Double/Long(mapper)
        // ───────────────────────────────────────────────────────────────────────────────
        // • Sums numeric values extracted by mapper function
        // • summingInt: Returns Integer
        // • summingDouble: Returns Double
        // • summingLong: Returns Long
        // • mapper: Function to extract numeric value from element
        // • Returns 0 if stream is empty
        // • Syntax: collect(Collectors.summingInt(mapper))
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("4.2 summingInt/Double/Long(mapper) - Sum numeric values:");
        Double totalSalary = employees.stream()
                .collect(java.util.stream.Collectors.summingDouble(Employee::getSalary));
        System.out.println("   Total salary (summingDouble): " + totalSalary);

        Integer totalLength = employees.stream()
                .map(Employee::getName)
                .collect(java.util.stream.Collectors.summingInt(String::length));
        System.out.println("   Total name length (summingInt): " + totalLength);
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // THEORY: averagingInt/Double/Long(mapper)
        // ───────────────────────────────────────────────────────────────────────────────
        // • Calculates average of numeric values
        // • averagingInt: Returns Double (average of ints)
        // • averagingDouble: Returns Double
        // • averagingLong: Returns Double (average of longs)
        // • Returns Double.NaN if stream is empty
        // • mapper: Function to extract numeric value
        // • Syntax: collect(Collectors.averagingInt(mapper))
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("4.3 averagingInt/Double/Long(mapper) - Calculate average:");
        Double avgSalary = employees.stream()
                .collect(java.util.stream.Collectors.averagingDouble(Employee::getSalary));
        System.out.println("   Average salary: " + avgSalary);
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // THEORY: minBy(comparator)
        // ───────────────────────────────────────────────────────────────────────────────
        // • Finds minimum element based on comparator
        // • Returns Optional<T> (empty if stream is empty)
        // • comparator: Comparator to determine ordering
        // • Commonly used as downstream collector
        // • Syntax: collect(Collectors.minBy(comparator))
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("4.4 minBy(comparator) - Find minimum:");
        Optional<Employee> minSalary = employees.stream()
                .collect(java.util.stream.Collectors.minBy(
                        Comparator.comparing(Employee::getSalary)));
        System.out.println("   Employee with minimum salary: " + 
                minSalary.map(Employee::getName).orElse("None"));
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // THEORY: maxBy(comparator)
        // ───────────────────────────────────────────────────────────────────────────────
        // • Finds maximum element based on comparator
        // • Returns Optional<T> (empty if stream is empty)
        // • comparator: Comparator to determine ordering
        // • Commonly used as downstream collector
        // • Syntax: collect(Collectors.maxBy(comparator))
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("4.5 maxBy(comparator) - Find maximum:");
        Optional<Employee> maxSalary = employees.stream()
                .collect(java.util.stream.Collectors.maxBy(
                        Comparator.comparing(Employee::getSalary)));
        System.out.println("   Employee with maximum salary: " + 
                maxSalary.map(Employee::getName).orElse("None"));
        System.out.println();

        // Combined example: Min and max salary per department
        System.out.println("4.6 Combined - Min and max salary per department:");
        Map<String, Optional<Employee>> minByDept = employees.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Employee::getDepartment,
                        java.util.stream.Collectors.minBy(
                                Comparator.comparing(Employee::getSalary))));
        minByDept.forEach((dept, emp) ->
                System.out.println("   " + dept + " - Min: " + 
                        emp.map(Employee::getName).orElse("None")));
        System.out.println();

        System.out.println("═══════════════════════════════════════════════════════════════════════════════════\n");
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                     5. SUMMARIZING COLLECTORS
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateSummarizingCollectors() {
        System.out.println("╔═══════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                        5. SUMMARIZING COLLECTORS                                    ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════════╝\n");

        List<Employee> employees = createEmployees();

        // ───────────────────────────────────────────────────────────────────────────────
        // THEORY: summarizingInt/Double/Long(mapper)
        // ───────────────────────────────────────────────────────────────────────────────
        // • Provides comprehensive statistics in one operation
        // • Returns *SummaryStatistics object containing:
        //   - getCount(): Number of elements
        //   - getSum(): Sum of values
        //   - getAverage(): Average of values
        //   - getMin(): Minimum value
        //   - getMax(): Maximum value
        // • More efficient than multiple separate operations
        // • Useful for reporting, analytics
        // • Syntax: collect(Collectors.summarizingInt(mapper))
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("5.1 summarizingDouble(mapper) - Comprehensive statistics:");
        DoubleSummaryStatistics stats = employees.stream()
                .collect(java.util.stream.Collectors.summarizingDouble(Employee::getSalary));
        System.out.println("   Salary Statistics:");
        System.out.println("     Count: " + stats.getCount());
        System.out.println("     Sum: " + stats.getSum());
        System.out.println("     Average: " + stats.getAverage());
        System.out.println("     Min: " + stats.getMin());
        System.out.println("     Max: " + stats.getMax());
        System.out.println();

        // Summarizing per group
        System.out.println("5.2 Summarizing per group (groupingBy + summarizingDouble):");
        Map<String, DoubleSummaryStatistics> statsByDept = employees.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Employee::getDepartment,
                        java.util.stream.Collectors.summarizingDouble(Employee::getSalary)));
        statsByDept.forEach((dept, deptStats) -> {
            System.out.println("   " + dept + ":");
            System.out.println("     Count: " + deptStats.getCount());
            System.out.println("     Avg: " + deptStats.getAverage());
            System.out.println("     Min: " + deptStats.getMin());
            System.out.println("     Max: " + deptStats.getMax());
        });
        System.out.println();

        System.out.println("═══════════════════════════════════════════════════════════════════════════════════\n");
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                     6. REDUCING COLLECTORS
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateReducingCollectors() {
        System.out.println("╔═══════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                          6. REDUCING COLLECTORS                                     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════════╝\n");

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        // ───────────────────────────────────────────────────────────────────────────────
        // THEORY: reducing(identity, op)
        // ───────────────────────────────────────────────────────────────────────────────
        // • General-purpose reduction operation
        // • identity: Initial value (also used if stream is empty)
        // • op: BinaryOperator that combines two values
        // • Similar to Stream.reduce() but as a Collector
        // • Useful as downstream collector in groupingBy
        // • Syntax: collect(Collectors.reducing(identity, op))
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("6.1 reducing(identity, op) - General reduction:");
        Integer sum = numbers.stream()
                .collect(java.util.stream.Collectors.reducing(0, Integer::sum));
        System.out.println("   Sum using reducing: " + sum);
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // THEORY: reducing(mapper, op)
        // ───────────────────────────────────────────────────────────────────────────────
        // • Reduces after mapping each element
        // • mapper: Function to transform element before reduction
        // • op: BinaryOperator for reduction
        // • Returns Optional (empty if stream is empty)
        // • Syntax: collect(Collectors.reducing(mapper, op))
        // ───────────────────────────────────────────────────────────────────────────────
        // System.out.println("6.2 reducing(mapper, op) - Reduce after mapping:");
        // Optional<Integer> sumOfSquares = numbers.stream()
        //         .collect(java.util.stream.Collectors.reducing(
        //                 n -> n * n,        // mapper: square each number
        //                 Integer::sum));    // op: sum them
        // System.out.println("   Sum of squares: " + sumOfSquares.orElse(0));
        // System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // THEORY: reducing(identity, mapper, op)
        // ───────────────────────────────────────────────────────────────────────────────
        // • Most general form: identity + mapper + op
        // • Combines mapping and reduction in one collector
        // • More efficient than separate map() and reduce()
        // • Syntax: collect(Collectors.reducing(identity, mapper, op))
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("6.3 reducing(identity, mapper, op) - Full form:");
        Integer product = numbers.stream()
                .collect(java.util.stream.Collectors.reducing(
                        1,                 // identity
                        n -> n * 2,        // mapper: double each number
                        (a, b) -> a * b)); // op: multiply
        System.out.println("   Product of doubled numbers: " + product);
        System.out.println();

        // Real-world: Total salary per department using reducing
        System.out.println("6.4 Real-world: Total salary per department:");
        List<Employee> employees = createEmployees();
        Map<String, Double> totalByDept = employees.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Employee::getDepartment,
                        java.util.stream.Collectors.reducing(
                                0.0,
                                Employee::getSalary,
                                Double::sum)));
        System.out.println("   " + totalByDept);
        System.out.println();

        System.out.println("═══════════════════════════════════════════════════════════════════════════════════\n");
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                     7. CHAINING COLLECTORS
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateChainingCollectors() {
        System.out.println("╔═══════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                         7. CHAINING COLLECTORS                                       ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════════╝\n");

        List<Employee> employees = createEmployees();

        // ───────────────────────────────────────────────────────────────────────────────
        // THEORY: Chaining Collectors
        // ───────────────────────────────────────────────────────────────────────────────
        // • Collectors can be chained as downstream collectors
        // • First collector groups/partitions, second collector processes each group
        // • Common patterns:
        //   - groupingBy + counting: Count per group
        //   - groupingBy + averaging: Average per group
        //   - groupingBy + maxBy/minBy: Find max/min per group
        //   - groupingBy + toSet: Collect to Set per group
        //   - groupingBy + mapping: Transform values per group
        // • Syntax: collect(Collectors.groupingBy(..., downstream))
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("7.1 groupingBy + counting - Count per group:");
        Map<String, Long> countByDept = employees.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Employee::getDepartment,
                        java.util.stream.Collectors.counting()));
        System.out.println("   " + countByDept);
        System.out.println();

        System.out.println("7.2 groupingBy + averagingDouble - Average per group:");
        Map<String, Double> avgByDept = employees.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Employee::getDepartment,
                        java.util.stream.Collectors.averagingDouble(Employee::getSalary)));
        System.out.println("   " + avgByDept);
        System.out.println();

        System.out.println("7.3 groupingBy + maxBy - Maximum per group:");
        Map<String, Optional<Employee>> maxByDept = employees.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Employee::getDepartment,
                        java.util.stream.Collectors.maxBy(
                                Comparator.comparing(Employee::getSalary))));
        maxByDept.forEach((dept, emp) ->
                System.out.println("   " + dept + ": " + 
                        emp.map(Employee::getName).orElse("None")));
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // THEORY: mapping(mapper, downstream)
        // ───────────────────────────────────────────────────────────────────────────────
        // • Applies mapper function before downstream collector
        // • Useful for transforming elements within groups
        // • Commonly used with groupingBy
        // • Syntax: collect(Collectors.mapping(mapper, downstream))
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("7.4 groupingBy + mapping - Transform values per group:");
        Map<String, List<String>> namesByDept = employees.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Employee::getDepartment,
                        java.util.stream.Collectors.mapping(
                                Employee::getName,
                                java.util.stream.Collectors.toList())));
        System.out.println("   Names by department: " + namesByDept);
        System.out.println();

        System.out.println("7.5 groupingBy + mapping + joining - Join names per group:");
        Map<String, String> joinedNamesByDept = employees.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Employee::getDepartment,
                        java.util.stream.Collectors.mapping(
                                Employee::getName,
                                java.util.stream.Collectors.joining(", "))));
        System.out.println("   " + joinedNamesByDept);
        System.out.println();

        // Complex chaining: Group by dept, then collect names to Set
        System.out.println("7.6 Complex: Group by dept, collect names to Set:");
        Map<String, Set<String>> nameSetByDept = employees.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Employee::getDepartment,
                        java.util.stream.Collectors.mapping(
                                Employee::getName,
                                java.util.stream.Collectors.toSet())));
        System.out.println("   " + nameSetByDept);
        System.out.println();

        System.out.println("═══════════════════════════════════════════════════════════════════════════════════\n");
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                     8. CUSTOM COLLECTORS
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateCustomCollectors() {
        System.out.println("╔═══════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                          8. CUSTOM COLLECTORS                                        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════════╝\n");

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // ───────────────────────────────────────────────────────────────────────────────
        // THEORY: Collector.of()
        // ───────────────────────────────────────────────────────────────────────────────
        // • Creates a custom Collector
        // • Requires four functions:
        //   1. supplier: Creates accumulator container
        //   2. accumulator: Adds element to container
        //   3. combiner: Combines two containers (for parallel streams)
        //   4. finisher: Transforms accumulator to final result
        // • Syntax: Collector.of(supplier, accumulator, combiner, finisher)
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("8.1 Custom Collector - Collect to comma-separated String:");
        Collector<Integer, StringBuilder, String> commaCollector = Collector.of(
                StringBuilder::new,                    // supplier: create accumulator
                (sb, i) -> {                           // accumulator: add element
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(i);
                },
                (sb1, sb2) -> {                       // combiner: combine for parallel
                    if (sb1.length() > 0 && sb2.length() > 0) sb1.append(", ");
                    return sb1.append(sb2);
                },
                StringBuilder::toString               // finisher: transform to result
        );
        String result = numbers.stream()
                .filter(n -> n % 2 == 0)
                .collect(commaCollector);
        System.out.println("   Even numbers: " + result);
        System.out.println();

        // Custom collector: Collect first 3 elements
        System.out.println("8.2 Custom Collector - Collect first N elements:");
        Collector<Integer, List<Integer>, List<Integer>> firstNCollector = Collector.of(
                ArrayList::new,                       // supplier
                (list, elem) -> {                      // accumulator
                    if (list.size() < 3) list.add(elem);
                },
                (list1, list2) -> {                   // combiner
                    list1.addAll(list2);
                    return list1.stream().limit(3).collect(java.util.stream.Collectors.toList());
                },
                list -> list.stream().limit(3).collect(java.util.stream.Collectors.toList()) // finisher
        );
        List<Integer> first3 = numbers.stream()
                .collect(firstNCollector);
        System.out.println("   First 3: " + first3);
        System.out.println();

        System.out.println("═══════════════════════════════════════════════════════════════════════════════════\n");
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                     9. ADVANCED USAGES
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateAdvancedUsages() {
        System.out.println("╔═══════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                         9. ADVANCED USAGES & BEST PRACTICES                       ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════════╝\n");

        List<Employee> employees = createEmployees();

        // Real-world: Complex report generation
        System.out.println("9.1 Real-world: Department Report (Complex chaining):");
        Map<String, Map<String, Object>> deptReport = employees.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Employee::getDepartment,
                        java.util.stream.Collectors.collectingAndThen(
                                java.util.stream.Collectors.toList(),
                                list -> {
                                    Map<String, Object> stats = new HashMap<>();
                                    DoubleSummaryStatistics salaryStats = list.stream()
                                            .mapToDouble(Employee::getSalary)
                                            .summaryStatistics();
                                    stats.put("count", list.size());
                                    stats.put("avgSalary", salaryStats.getAverage());
                                    stats.put("minSalary", salaryStats.getMin());
                                    stats.put("maxSalary", salaryStats.getMax());
                                    stats.put("totalSalary", salaryStats.getSum());
                                    stats.put("employees", list.stream()
                                            .map(Employee::getName)
                                            .collect(java.util.stream.Collectors.joining(", ")));
                                    return stats;
                                })));
        deptReport.forEach((dept, stats) -> {
            System.out.println("   " + dept + ":");
            stats.forEach((key, value) -> System.out.println("     " + key + ": " + value));
        });
        System.out.println();

        // ───────────────────────────────────────────────────────────────────────────────
        // THEORY: collectingAndThen(downstream, finisher)
        // ───────────────────────────────────────────────────────────────────────────────
        // • Applies a finishing function to the result of downstream collector
        // • Useful for: Making collections immutable, transforming final result
        // • Syntax: collect(Collectors.collectingAndThen(downstream, finisher))
        // ───────────────────────────────────────────────────────────────────────────────
        System.out.println("9.2 collectingAndThen - Make result immutable:");
        List<String> immutableNames = employees.stream()
                .map(Employee::getName)
                .collect(java.util.stream.Collectors.collectingAndThen(
                        java.util.stream.Collectors.toList(),
                        Collections::unmodifiableList));
        System.out.println("   Immutable list created");
        System.out.println();

        // Best practices
        System.out.println("9.3 BEST PRACTICES:");
        System.out.println("   ✓ Use toList() for most cases (simple and efficient)");
        System.out.println("   ✓ Use toSet() when you need unique elements");
        System.out.println("   ✓ Use toCollection() when you need specific Collection type");
        System.out.println("   ✓ Use groupingBy() for categorization");
        System.out.println("   ✓ Use partitioningBy() for boolean splits");
        System.out.println("   ✓ Chain collectors for complex aggregations");
        System.out.println("   ✓ Use summarizing* for comprehensive statistics");
        System.out.println("   ✓ Prefer built-in collectors over custom ones");
        System.out.println("   ✓ Use collectingAndThen() for final transformations");
        System.out.println();

        System.out.println("═══════════════════════════════════════════════════════════════════════════════════\n");
    }

    // Helper method to create sample employees
    static List<Employee> createEmployees() {
        return Arrays.asList(
                new Employee("Alice", "IT", 60000),
                new Employee("Bob", "HR", 50000),
                new Employee("Charlie", "IT", 70000),
                new Employee("Diana", "HR", 55000),
                new Employee("Eve", "IT", 65000),
                new Employee("Frank", "Finance", 60000),
                new Employee("Grace", "IT", 75000),
                new Employee("Henry", "Finance", 58000)
        );
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