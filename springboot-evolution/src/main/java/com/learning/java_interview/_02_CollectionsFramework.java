package com.learning.java_interview;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                                                                                                ║
 * ║                              COLLECTIONS FRAMEWORK - INTERVIEW DEEP DIVE                                       ║
 * ║                                                                                                                ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    1. COLLECTIONS HIERARCHY                                                    ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *                                           java.lang.Iterable (I)
 *                                                   │
 *                                          java.util.Collection (I)
 *                                 ┌────────────────┼────────────────┐
 *                                 │                │                │
 *                              List (I)         Set (I)         Queue (I)
 *                                 │                │                │
 *                    ┌────────────┼────────┐       │         ┌──────┴──────┐
 *                    │            │        │       │         │             │
 *                ArrayList   LinkedList  Vector    │    PriorityQueue   Deque (I)
 *                    │            │        │       │                       │
 *                    │            │     Stack      │                  ArrayDeque
 *                    │            │                │                  LinkedList
 *                    │            │     ┌──────────┼──────────┐
 *                    │            │     │          │          │
 *                    │            │  HashSet    TreeSet   LinkedHashSet
 *                    │            │     │
 *                    │            │  LinkedHashSet
 *                    │            │
 *                    └────────────┴─────── (LinkedList implements both List and Deque)
 *
 *
 *                                              Map (I)
 *                               ┌────────────────┼────────────────┐
 *                               │                │                │
 *                           HashMap          TreeMap       LinkedHashMap
 *                               │
 *                        LinkedHashMap
 *                               │
 *                         Hashtable (legacy, synchronized)
 *                               │
 *                      ConcurrentHashMap (concurrent)
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    2. LIST IMPLEMENTATIONS COMPARISON                                          ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  ┌────────────────────┬─────────────────────────┬─────────────────────────┬─────────────────────────┐
 *  │     Operation      │       ArrayList         │       LinkedList        │        Vector           │
 *  ├────────────────────┼─────────────────────────┼─────────────────────────┼─────────────────────────┤
 *  │ Internal Structure │ Dynamic Array           │ Doubly Linked List      │ Dynamic Array           │
 *  │ get(index)         │ O(1) - Direct access    │ O(n) - Traverse         │ O(1)                    │
 *  │ add(element)       │ O(1) amortized          │ O(1)                    │ O(1)                    │
 *  │ add(index, elem)   │ O(n) - Shift elements   │ O(n) - Traverse         │ O(n)                    │
 *  │ remove(index)      │ O(n) - Shift elements   │ O(n) - Traverse         │ O(n)                    │
 *  │ Thread Safety      │ Not synchronized        │ Not synchronized        │ Synchronized            │
 *  │ Growth             │ 50% (oldCap + oldCap/2) │ No pre-allocation       │ 100% (doubles)          │
 *  │ Memory             │ Less overhead           │ More (node pointers)    │ Less overhead           │
 *  │ Use When           │ Frequent reads          │ Frequent insertions     │ Legacy (avoid)          │
 *  └────────────────────┴─────────────────────────┴─────────────────────────┴─────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    3. HASHMAP INTERNAL WORKING ⭐                                              ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * STRUCTURE (Java 8+):
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                                │
 * │   HashMap = Array of Buckets (Node<K,V>[] table)                                                               │
 * │                                                                                                                │
 * │   Index:    [0]        [1]        [2]        [3]        [4]        [5]   ...  [n-1]                            │
 * │              │          │          │          │          │          │                                          │
 * │              ▼          ▼          ▼          ▼          ▼          ▼                                          │
 * │            null       Node       null       Node       null       Node                                         │
 * │                        │                     │                     │                                           │
 * │                        ▼                     ▼                     ▼                                           │
 * │                       Node                 Node                 TreeNode  (when > 8 nodes)                     │
 * │                        │                                          │                                           │
 * │                        ▼                                      [Red-Black Tree]                                 │
 * │                      null                                                                                      │
 * │                                                                                                                │
 * │   Node Structure:                                                                                              │
 * │   ┌────────────────────────────────────────┐                                                                   │
 * │   │  hash  │  key  │  value  │  next       │                                                                   │
 * │   └────────────────────────────────────────┘                                                                   │
 * │                                                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * PUT OPERATION:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  1. Calculate hash:   hash = key.hashCode() ^ (key.hashCode() >>> 16)  // Spread bits                          │
 * │                                                                                                                │
 * │  2. Find bucket:      index = hash & (n-1)  // Same as hash % n, but faster (n must be power of 2)             │
 * │                                                                                                                │
 * │  3. If bucket empty:  Create new Node, put at index                                                            │
 * │                                                                                                                │
 * │  4. If bucket has nodes:                                                                                       │
 * │     a. If key exists (hash match + equals): Update value                                                       │
 * │     b. If LinkedList: Append to end, convert to Tree if > TREEIFY_THRESHOLD (8)                                │
 * │     c. If Tree: Insert into Red-Black Tree                                                                     │
 * │                                                                                                                │
 * │  5. If size > threshold (capacity * loadFactor): Resize (double capacity)                                      │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * GET OPERATION:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  1. Calculate index from hash                                                                                  │
 * │  2. If bucket empty: return null                                                                               │
 * │  3. If first node matches (hash + equals): return value                                                        │
 * │  4. If LinkedList: Traverse until match or null - O(n)                                                         │
 * │  5. If Tree: Search in tree - O(log n)                                                                         │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * KEY CONSTANTS:
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  DEFAULT_INITIAL_CAPACITY = 16        // Must be power of 2                                                    │
 * │  DEFAULT_LOAD_FACTOR = 0.75f          // When to resize                                                        │
 * │  TREEIFY_THRESHOLD = 8                // Convert to tree when > 8 nodes in bucket                              │
 * │  UNTREEIFY_THRESHOLD = 6              // Convert back to list when < 6                                         │
 * │  MIN_TREEIFY_CAPACITY = 64            // Minimum capacity for treeification                                    │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                              4. HASHMAP vs CONCURRENTHASHMAP vs HASHTABLE                                      ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  ┌────────────────────────┬─────────────────────────┬─────────────────────────┬───────────────────────────────┐
 *  │        Feature         │        HashMap          │       Hashtable         │      ConcurrentHashMap        │
 *  ├────────────────────────┼─────────────────────────┼─────────────────────────┼───────────────────────────────┤
 *  │ Thread Safety          │ No                      │ Yes (full sync)         │ Yes (segment-level)           │
 *  │ Null Key               │ 1 null key allowed      │ Not allowed             │ Not allowed                   │
 *  │ Null Value             │ Allowed                 │ Not allowed             │ Not allowed                   │
 *  │ Performance            │ Best (single-threaded)  │ Slow (full lock)        │ Best (concurrent)             │
 *  │ Locking                │ None                    │ Entire map              │ Bucket-level (Java 8+)        │
 *  │ Iterator               │ Fail-fast               │ Fail-fast               │ Fail-safe                     │
 *  │ Introduced             │ Java 1.2                │ Java 1.0                │ Java 1.5                      │
 *  │ Usage                  │ Single thread           │ Legacy (avoid)          │ Multi-threaded                │
 *  └────────────────────────┴─────────────────────────┴─────────────────────────┴───────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    5. SET IMPLEMENTATIONS                                                      ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  ┌────────────────────────┬─────────────────────────┬─────────────────────────┬───────────────────────────────┐
 *  │        Feature         │        HashSet          │        TreeSet          │       LinkedHashSet           │
 *  ├────────────────────────┼─────────────────────────┼─────────────────────────┼───────────────────────────────┤
 *  │ Internal Structure     │ HashMap (key=element)   │ TreeMap (Red-Black)     │ LinkedHashMap                 │
 *  │ Ordering               │ No order                │ Sorted (natural/comp)   │ Insertion order               │
 *  │ Null Elements          │ 1 null allowed          │ No null (NPE in comp)   │ 1 null allowed                │
 *  │ add/remove/contains    │ O(1)                    │ O(log n)                │ O(1)                          │
 *  │ Use When               │ No ordering needed      │ Sorted order needed     │ Insertion order needed        │
 *  └────────────────────────┴─────────────────────────┴─────────────────────────┴───────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    6. FAIL-FAST vs FAIL-SAFE                                                   ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │  FAIL-FAST                                              FAIL-SAFE                                              │
 *  │  ──────────                                              ─────────                                              │
 *  │  • Throws ConcurrentModificationException               • Never throws CME                                     │
 *  │  • Works on original collection                         • Works on clone/copy                                  │
 *  │  • Uses modCount to detect changes                      • May not reflect latest changes                       │
 *  │  • Collections: ArrayList, HashMap, HashSet             • Collections: ConcurrentHashMap,                      │
 *  │                                                           CopyOnWriteArrayList                                 │
 *  │                                                                                                                │
 *  │  Example:                                                Example:                                              │
 *  │  ┌──────────────────────────────────┐                   ┌──────────────────────────────────┐                   │
 *  │  │ List<String> list = new ArrayList<>();              │ List<String> list =              │                   │
 *  │  │ list.add("A");                                      │   new CopyOnWriteArrayList<>();  │                   │
 *  │  │ for (String s : list) {                             │ list.add("A");                   │                   │
 *  │  │     list.add("B"); // CME!                          │ for (String s : list) {          │                   │
 *  │  │ }                                                   │     list.add("B"); // OK!        │                   │
 *  │  └──────────────────────────────────┘                   └──────────────────────────────────┘                   │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    7. INTERVIEW QUESTIONS                                                      ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * Q1: Why is initial capacity of HashMap a power of 2?
 * A: For efficient bucket calculation using bitwise AND: index = hash & (n-1), which is faster than modulo.
 *
 * Q2: What happens when two keys have same hashCode?
 * A: Collision! They go to same bucket. Stored in linked list (or tree if > 8). get() uses equals() to find exact key.
 *
 * Q3: Why load factor of 0.75?
 * A: Trade-off between space and time. Lower = more space, fewer collisions. Higher = less space, more collisions.
 *
 * Q4: Why convert LinkedList to Tree at 8?
 * A: At 8 nodes, O(n) linked list traversal becomes expensive. Tree gives O(log n). Tree overhead not worth it below 8.
 *
 * Q5: Why can't we use primitive as key in HashMap?
 * A: Generics require objects. Primitives are auto-boxed to wrappers (int → Integer).
 *
 * Q6: What if key object is modified after insertion?
 * A: BUG! hashCode changes, object may not be found in same bucket. Always use immutable keys.
 *
 * Q7: Difference between Comparable and Comparator?
 * A: Comparable: natural ordering in class itself (compareTo). Comparator: external ordering logic (compare).
 *
 * Q8: How to make collection thread-safe?
 * A: Collections.synchronizedList/Map/Set, or use ConcurrentHashMap, CopyOnWriteArrayList.
 *
 *
 * @author Java Interview Guide
 */
public class _02_CollectionsFramework {

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════");
        System.out.println("                     COLLECTIONS FRAMEWORK DEMONSTRATION");
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════\n");

        // 1. ArrayList vs LinkedList
        demonstrateListComparison();

        // 2. HashMap Internal Working
        demonstrateHashMapWorking();

        // 3. Set Implementations
        demonstrateSetImplementations();

        // 4. Fail-fast vs Fail-safe
        demonstrateFailFastFailSafe();

        // 5. Comparable vs Comparator
        demonstrateComparableComparator();

        // 6. Concurrent Collections
        demonstrateConcurrentCollections();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         1. ARRAYLIST vs LINKEDLIST
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateListComparison() {
        System.out.println("▶ 1. ARRAYLIST vs LINKEDLIST PERFORMANCE:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        ArrayList<Integer> arrayList = new ArrayList<>();
        LinkedList<Integer> linkedList = new LinkedList<>();
        int size = 100000;

        // Add elements
        long start = System.nanoTime();
        for (int i = 0; i < size; i++) arrayList.add(i);
        long arrayListAdd = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < size; i++) linkedList.add(i);
        long linkedListAdd = System.nanoTime() - start;

        System.out.printf("   Add %d elements: ArrayList=%dms, LinkedList=%dms%n",
                size, arrayListAdd / 1_000_000, linkedListAdd / 1_000_000);

        // Random access
        start = System.nanoTime();
        for (int i = 0; i < 10000; i++) arrayList.get(50000);
        long arrayListGet = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < 10000; i++) linkedList.get(50000);
        long linkedListGet = System.nanoTime() - start;

        System.out.printf("   Get middle element 10000 times: ArrayList=%dms, LinkedList=%dms%n",
                arrayListGet / 1_000_000, linkedListGet / 1_000_000);

        // Insert at beginning
        start = System.nanoTime();
        for (int i = 0; i < 10000; i++) arrayList.add(0, i);
        long arrayListInsert = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < 10000; i++) linkedList.addFirst(i);
        long linkedListInsert = System.nanoTime() - start;

        System.out.printf("   Insert at beginning 10000 times: ArrayList=%dms, LinkedList=%dms%n",
                arrayListInsert / 1_000_000, linkedListInsert / 1_000_000);

        System.out.println("\n   📌 Conclusion: ArrayList for reads, LinkedList for frequent insertions\n");
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         2. HASHMAP INTERNAL WORKING
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateHashMapWorking() {
        System.out.println("▶ 2. HASHMAP INTERNAL WORKING:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        HashMap<String, Integer> map = new HashMap<>(16, 0.75f);

        // Show how hash is calculated
        String key = "Hello";
        int hash = key.hashCode();
        int betterHash = hash ^ (hash >>> 16);  // Spread high bits
        int bucket = betterHash & 15;           // 15 = capacity - 1

        System.out.println("   Key: \"" + key + "\"");
        System.out.println("   hashCode(): " + hash);
        System.out.println("   Improved hash (spread bits): " + betterHash);
        System.out.println("   Bucket index (hash & 15): " + bucket);

        // Demonstrate collision
        System.out.println("\n   Collision Demo:");
        CollidingKey k1 = new CollidingKey(1, "A");
        CollidingKey k2 = new CollidingKey(2, "B");
        System.out.println("   k1.hashCode() = " + k1.hashCode());
        System.out.println("   k2.hashCode() = " + k2.hashCode());
        System.out.println("   Same hashCode, different keys → Collision! (stored in same bucket)");

        HashMap<CollidingKey, String> collisionMap = new HashMap<>();
        collisionMap.put(k1, "Value1");
        collisionMap.put(k2, "Value2");
        System.out.println("   get(k1) = " + collisionMap.get(k1));
        System.out.println("   get(k2) = " + collisionMap.get(k2));
        System.out.println("   Both retrieved correctly using equals()!\n");
    }

    // Custom key class that creates collisions
    static class CollidingKey {
        int id;
        String name;

        CollidingKey(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public int hashCode() {
            return 42;  // Same hashCode for all! (BAD in real code)
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CollidingKey)) return false;
            CollidingKey that = (CollidingKey) o;
            return id == that.id && Objects.equals(name, that.name);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         3. SET IMPLEMENTATIONS
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateSetImplementations() {
        System.out.println("▶ 3. SET IMPLEMENTATIONS:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // HashSet - no order
        Set<String> hashSet = new HashSet<>();
        hashSet.add("Banana");
        hashSet.add("Apple");
        hashSet.add("Cherry");
        hashSet.add("Apple");  // Duplicate - ignored
        System.out.println("   HashSet (no order): " + hashSet);

        // LinkedHashSet - insertion order
        Set<String> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.add("Banana");
        linkedHashSet.add("Apple");
        linkedHashSet.add("Cherry");
        System.out.println("   LinkedHashSet (insertion order): " + linkedHashSet);

        // TreeSet - sorted order
        Set<String> treeSet = new TreeSet<>();
        treeSet.add("Banana");
        treeSet.add("Apple");
        treeSet.add("Cherry");
        System.out.println("   TreeSet (sorted order): " + treeSet);

        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         4. FAIL-FAST vs FAIL-SAFE
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateFailFastFailSafe() {
        System.out.println("▶ 4. FAIL-FAST vs FAIL-SAFE ITERATORS:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // Fail-fast example
        System.out.println("   Fail-fast (ArrayList):");
        List<String> arrayList = new ArrayList<>(Arrays.asList("A", "B", "C"));
        try {
            for (String s : arrayList) {
                if (s.equals("B")) {
                    arrayList.add("D");  // Modifying during iteration
                }
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("   ❌ ConcurrentModificationException thrown!");
        }

        // Fail-safe example
        System.out.println("\n   Fail-safe (CopyOnWriteArrayList):");
        List<String> cowList = new CopyOnWriteArrayList<>(Arrays.asList("A", "B", "C"));
        for (String s : cowList) {
            if (s.equals("B")) {
                cowList.add("D");  // Works! Iterates on copy
            }
        }
        System.out.println("   ✓ No exception! Final list: " + cowList);

        // Proper way to remove during iteration
        System.out.println("\n   Proper way to modify during iteration (using Iterator.remove()):");
        List<String> list = new ArrayList<>(Arrays.asList("A", "B", "C"));
        Iterator<String> iter = list.iterator();
        while (iter.hasNext()) {
            String s = iter.next();
            if (s.equals("B")) {
                iter.remove();  // Safe removal
            }
        }
        System.out.println("   List after removal: " + list);
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         5. COMPARABLE vs COMPARATOR
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateComparableComparator() {
        System.out.println("▶ 5. COMPARABLE vs COMPARATOR:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(3, "Charlie", 50000));
        employees.add(new Employee(1, "Alice", 60000));
        employees.add(new Employee(2, "Bob", 55000));

        // Using Comparable (natural order - by ID)
        Collections.sort(employees);
        System.out.println("   Sorted by ID (Comparable - natural order):");
        employees.forEach(e -> System.out.println("   " + e));

        // Using Comparator (custom order - by name)
        employees.sort(Comparator.comparing(e -> e.name));
        System.out.println("\n   Sorted by Name (Comparator):");
        employees.forEach(e -> System.out.println("   " + e));

        // Using Comparator (by salary descending)
        employees.sort(Comparator.comparingDouble((Employee e) -> e.salary).reversed());
        System.out.println("\n   Sorted by Salary descending (Comparator):");
        employees.forEach(e -> System.out.println("   " + e));

        // Chained comparators
        employees.sort(Comparator
                .comparingDouble((Employee e) -> e.salary)
                .thenComparing(e -> e.name));
        System.out.println("\n   Sorted by Salary, then by Name (Chained Comparators):");
        employees.forEach(e -> System.out.println("   " + e));
        System.out.println();
    }

    static class Employee implements Comparable<Employee> {
        int id;
        String name;
        double salary;

        Employee(int id, String name, double salary) {
            this.id = id;
            this.name = name;
            this.salary = salary;
        }

        @Override
        public int compareTo(Employee other) {
            return Integer.compare(this.id, other.id);  // Natural order by ID
        }

        @Override
        public String toString() {
            return String.format("Employee{id=%d, name='%s', salary=%.0f}", id, name, salary);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         6. CONCURRENT COLLECTIONS
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateConcurrentCollections() {
        System.out.println("▶ 6. CONCURRENT COLLECTIONS:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // ConcurrentHashMap
        ConcurrentHashMap<String, Integer> concurrentMap = new ConcurrentHashMap<>();
        concurrentMap.put("A", 1);
        concurrentMap.put("B", 2);

        // Atomic operations
        concurrentMap.putIfAbsent("C", 3);  // Add only if absent
        concurrentMap.computeIfAbsent("D", k -> 4);  // Compute if absent
        concurrentMap.merge("A", 10, Integer::sum);  // Merge with existing

        System.out.println("   ConcurrentHashMap: " + concurrentMap);
        System.out.println("   Note: No null keys/values allowed in ConcurrentHashMap");

        // CopyOnWriteArrayList
        CopyOnWriteArrayList<String> cowList = new CopyOnWriteArrayList<>();
        cowList.add("Item1");
        cowList.add("Item2");
        System.out.println("\n   CopyOnWriteArrayList: " + cowList);
        System.out.println("   Note: Good for read-heavy, write-rare scenarios");

        // Synchronized wrapper (alternative)
        List<String> syncList = Collections.synchronizedList(new ArrayList<>());
        syncList.add("Sync1");
        System.out.println("\n   Collections.synchronizedList: " + syncList);
        System.out.println("   Note: Must manually synchronize when iterating");

        System.out.println();
    }
}

