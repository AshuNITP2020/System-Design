// package com.learning.java_interview;

// import java.util.*;
// import java.util.concurrent.*;

// /**
//  * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
//  * ║                                                                                                                ║
//  * ║                    COLLECTIONS INTERNAL WORKING - DEEP DIVE (Before & After Java 8)                           ║
//  * ║                                                                                                                ║
//  * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
//  *
//  *
//  * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
//  * ║                                    1. ARRAYLIST INTERNAL WORKING                                              ║
//  * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
//  *
//  * INTERNAL STRUCTURE:
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                                │
//  * │   ArrayList = Dynamic Array (Object[] elementData)                                                             │
//  * │                                                                                                                │
//  * │   ┌──────┬──────┬──────┬──────┬──────┬──────┬──────┬──────┬──────┬──────┐                                      │
//  * │   │  A   │  B   │  C   │  D   │  E   │ null │ null │ null │ null │ null │                                      │
//  * │   └──────┴──────┴──────┴──────┴──────┴──────┴──────┴──────┴──────┴──────┘                                      │
//  * │     [0]    [1]    [2]    [3]    [4]    [5]    [6]    [7]    [8]    [9]                                         │
//  * │                                                                                                                │
//  * │   size = 5 (actual elements)                                                                                   │
//  * │   capacity = 10 (array length)                                                                                 │
//  * │                                                                                                                │
//  * │   KEY FIELDS:                                                                                                  │
//  * │   • Object[] elementData     → Backing array                                                                   │
//  * │   • int size                 → Number of elements                                                              │
//  * │   • int modCount             → Structural modification count (fail-fast)                                       │
//  * │                                                                                                                │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  * GROWTH MECHANISM:
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                                │
//  * │   BEFORE JAVA 8:  newCapacity = (oldCapacity * 3) / 2 + 1   ≈ 150% + 1                                         │
//  * │   JAVA 8+:        newCapacity = oldCapacity + (oldCapacity >> 1)  ≈ 150% (50% growth)                          │
//  * │                                                                                                                │
//  * │   Growth Example (Java 8+):                                                                                    │
//  * │   10 → 15 → 22 → 33 → 49 → 73 → 109 → ...                                                                      │
//  * │                                                                                                                │
//  * │   private void grow(int minCapacity) {                                                                         │
//  * │       int oldCapacity = elementData.length;                                                                    │
//  * │       int newCapacity = oldCapacity + (oldCapacity >> 1);  // 1.5x                                             │
//  * │       if (newCapacity - minCapacity < 0)                                                                       │
//  * │           newCapacity = minCapacity;                                                                           │
//  * │       elementData = Arrays.copyOf(elementData, newCapacity);                                                   │
//  * │   }                                                                                                            │
//  * │                                                                                                                │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  * LAZY INITIALIZATION (Java 8+):
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │   BEFORE JAVA 8: new ArrayList() → Created array of size 10 immediately                                       │
//  * │   JAVA 8+:       new ArrayList() → Empty array, grows to 10 on first add                                      │
//  * │                                                                                                                │
//  * │   private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};                                        │
//  * │   private static final int DEFAULT_CAPACITY = 10;                                                              │
//  * │                                                                                                                │
//  * │   public ArrayList() {                                                                                         │
//  * │       this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;  // Empty!                                         │
//  * │   }                                                                                                            │
//  * │                                                                                                                │
//  * │   // On first add, capacity becomes 10 (DEFAULT_CAPACITY)                                                      │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  * TIME COMPLEXITY:
//  * ┌────────────────────┬─────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │   Operation        │   Complexity                                                                               │
//  * ├────────────────────┼─────────────────────────────────────────────────────────────────────────────────────────────┤
//  * │   get(index)       │   O(1) - Direct array access: elementData[index]                                           │
//  * │   set(index)       │   O(1) - Direct array update                                                               │
//  * │   add(element)     │   O(1) amortized - O(n) when resize needed                                                 │
//  * │   add(index, elem) │   O(n) - System.arraycopy to shift elements right                                          │
//  * │   remove(index)    │   O(n) - System.arraycopy to shift elements left                                           │
//  * │   remove(object)   │   O(n) - Search + shift                                                                    │
//  * │   contains/indexOf │   O(n) - Linear search                                                                     │
//  * │   clear()          │   O(n) - Null out all references for GC                                                    │
//  * └────────────────────┴─────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  *
//  * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
//  * ║                                    2. LINKEDLIST INTERNAL WORKING                                             ║
//  * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
//  *
//  * INTERNAL STRUCTURE (Doubly Linked List):
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                                │
//  * │   LinkedList<E> implements List<E>, Deque<E>                                                                   │
//  * │                                                                                                                │
//  * │   first ──►┌───────────────┐     ┌───────────────┐     ┌───────────────┐◄── last                               │
//  * │            │ prev = null   │◄────│ prev          │◄────│ prev          │                                       │
//  * │            │ item = A      │     │ item = B      │     │ item = C      │                                       │
//  * │            │ next ─────────│────►│ next ─────────│────►│ next = null   │                                       │
//  * │            └───────────────┘     └───────────────┘     └───────────────┘                                       │
//  * │                                                                                                                │
//  * │   Node<E> Structure:                                                                                           │
//  * │   private static class Node<E> {                                                                               │
//  * │       E item;           // The element                                                                         │
//  * │       Node<E> next;     // Reference to next node                                                              │
//  * │       Node<E> prev;     // Reference to previous node                                                          │
//  * │   }                                                                                                            │
//  * │                                                                                                                │
//  * │   KEY FIELDS:                                                                                                  │
//  * │   • Node<E> first  → Points to first node                                                                      │
//  * │   • Node<E> last   → Points to last node                                                                       │
//  * │   • int size       → Number of elements                                                                        │
//  * │                                                                                                                │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  * OPTIMIZED TRAVERSAL:
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │   get(index) optimization - traverse from closer end:                                                          │
//  * │                                                                                                                │
//  * │   Node<E> node(int index) {                                                                                    │
//  * │       if (index < (size >> 1)) {    // First half: start from head                                             │
//  * │           Node<E> x = first;                                                                                   │
//  * │           for (int i = 0; i < index; i++) x = x.next;                                                          │
//  * │           return x;                                                                                            │
//  * │       } else {                      // Second half: start from tail                                            │
//  * │           Node<E> x = last;                                                                                    │
//  * │           for (int i = size - 1; i > index; i--) x = x.prev;                                                   │
//  * │           return x;                                                                                            │
//  * │       }                                                                                                        │
//  * │   }                                                                                                            │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  * TIME COMPLEXITY:
//  * ┌────────────────────┬─────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │   Operation        │   Complexity                                                                               │
//  * ├────────────────────┼─────────────────────────────────────────────────────────────────────────────────────────────┤
//  * │   addFirst/Last    │   O(1) - Just update pointers                                                              │
//  * │   removeFirst/Last │   O(1) - Just update pointers                                                              │
//  * │   get(index)       │   O(n) - Traverse (but optimized from closer end → O(n/2))                                 │
//  * │   add(index, elem) │   O(n) - Find position + O(1) insertion                                                    │
//  * │   remove(index)    │   O(n) - Find position + O(1) removal                                                      │
//  * │   contains         │   O(n) - Linear search                                                                     │
//  * └────────────────────┴─────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  * MEMORY OVERHEAD:
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │   Each element in LinkedList requires:                                                                         │
//  * │   • Object reference (element): 8 bytes (64-bit JVM)                                                           │
//  * │   • Next pointer: 8 bytes                                                                                      │
//  * │   • Prev pointer: 8 bytes                                                                                      │
//  * │   • Node object header: 16 bytes                                                                               │
//  * │   • Total: ~40 bytes overhead per element (vs ~4-8 bytes for ArrayList reference)                              │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  *
//  * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
//  * ║                                    3. HASHMAP INTERNAL WORKING                                                 ║
//  * ║                                    (BEFORE vs AFTER JAVA 8) ⭐⭐⭐                                              ║
//  * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
//  *
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                                │
//  * │   ╔═══════════════════════════════════════════════════════════════════════════════════════════════════════════╗│
//  * │   ║                           BEFORE JAVA 8 (Array + LinkedList only)                                        ║│
//  * │   ╚═══════════════════════════════════════════════════════════════════════════════════════════════════════════╝│
//  * │                                                                                                                │
//  * │   Entry<K,V>[] table                                                                                           │
//  * │   ┌──────────────────────────────────────────────────────────────────────────────────────────────┐              │
//  * │   │  [0]      [1]      [2]      [3]      [4]      [5]      [6]      ...    [n-1]                │              │
//  * │   │   │        │        │        │        │        │        │                │                  │              │
//  * │   │   ▼        ▼        ▼        ▼        ▼        ▼        ▼                ▼                  │              │
//  * │   │  null    Entry    null    Entry    null     Entry    null              null                │              │
//  * │   │            │                 │                 │                                           │              │
//  * │   │            ▼                 ▼                 ▼                                           │              │
//  * │   │          Entry           Entry            Entry  (LinkedList)                              │              │
//  * │   │            │                                 │                                             │              │
//  * │   │            ▼                                 ▼                                             │              │
//  * │   │          null                             Entry  ← PROBLEM: O(n) worst case!              │              │
//  * │   └──────────────────────────────────────────────────────────────────────────────────────────────┘              │
//  * │                                                                                                                │
//  * │   Entry Structure (Pre-Java 8):                                                                                │
//  * │   static class Entry<K,V> implements Map.Entry<K,V> {                                                          │
//  * │       final K key;                                                                                             │
//  * │       V value;                                                                                                 │
//  * │       Entry<K,V> next;    // Next entry in chain                                                               │
//  * │       final int hash;     // Cached hash                                                                       │
//  * │   }                                                                                                            │
//  * │                                                                                                                │
//  * │   PROBLEM: Hash collision attack could degrade to O(n) for every operation!                                    │
//  * │            Malicious keys with same hashCode → long chain → DoS vulnerability                                  │
//  * │                                                                                                                │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                                │
//  * │   ╔═══════════════════════════════════════════════════════════════════════════════════════════════════════════╗│
//  * │   ║                           JAVA 8+ (Array + LinkedList + Red-Black Tree) ⭐                               ║│
//  * │   ╚═══════════════════════════════════════════════════════════════════════════════════════════════════════════╝│
//  * │                                                                                                                │
//  * │   Node<K,V>[] table                                                                                            │
//  * │   ┌──────────────────────────────────────────────────────────────────────────────────────────────┐              │
//  * │   │  [0]      [1]      [2]      [3]      [4]      [5]      [6]      ...    [n-1]                │              │
//  * │   │   │        │        │        │        │        │        │                │                  │              │
//  * │   │   ▼        ▼        ▼        ▼        ▼        ▼        ▼                ▼                  │              │
//  * │   │  null    Node     null    TreeNode  null     Node     null              null               │              │
//  * │   │            │               ╱   ╲               │                                           │              │
//  * │   │            ▼           TreeNode TreeNode       ▼                                           │              │
//  * │   │          Node           ╱   ╲                Node  (LinkedList if ≤8)                      │              │
//  * │   │            │        TreeNode TreeNode          │                                           │              │
//  * │   │            ▼                                   ▼                                           │              │
//  * │   │          null      (Red-Black Tree if >8)   null                                           │              │
//  * │   └──────────────────────────────────────────────────────────────────────────────────────────────┘              │
//  * │                                                                                                                │
//  * │   Node Structure (Java 8+):                                                                                    │
//  * │   static class Node<K,V> implements Map.Entry<K,V> {                                                           │
//  * │       final int hash;                                                                                          │
//  * │       final K key;                                                                                             │
//  * │       V value;                                                                                                 │
//  * │       Node<K,V> next;                                                                                          │
//  * │   }                                                                                                            │
//  * │                                                                                                                │
//  * │   TreeNode Structure (Java 8+):                                                                                │
//  * │   static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {                                          │
//  * │       TreeNode<K,V> parent;                                                                                    │
//  * │       TreeNode<K,V> left;                                                                                      │
//  * │       TreeNode<K,V> right;                                                                                     │
//  * │       TreeNode<K,V> prev;                                                                                      │
//  * │       boolean red;          // Color for Red-Black tree balancing                                              │
//  * │   }                                                                                                            │
//  * │                                                                                                                │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  * TREEIFICATION THRESHOLDS:
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                                │
//  * │   static final int TREEIFY_THRESHOLD = 8;        // Convert to tree when bucket has > 8 nodes                  │
//  * │   static final int UNTREEIFY_THRESHOLD = 6;      // Convert back to list when bucket has < 6 nodes             │
//  * │   static final int MIN_TREEIFY_CAPACITY = 64;    // Minimum table size for treeification                       │
//  * │                                                                                                                │
//  * │   Why 8?                                                                                                       │
//  * │   • Average case: Random distribution rarely gets >8 collisions                                                │
//  * │   • TreeNode is ~2x size of Node (more memory overhead)                                                        │
//  * │   • At 8 nodes: O(8) for list ≈ O(log 8) = O(3) for tree (tree starts winning)                                 │
//  * │                                                                                                                │
//  * │   Why untreeify at 6 (not 8)?                                                                                  │
//  * │   • Hysteresis: Prevents oscillation between tree/list                                                         │
//  * │   • If treeify at 8 and untreeify at 8 → constant conversion overhead                                          │
//  * │                                                                                                                │
//  * │   Treeification Process:                                                                                       │
//  * │   1. Check if table.length >= MIN_TREEIFY_CAPACITY (64)                                                        │
//  * │   2. If not, resize() instead (better distribution may help)                                                   │
//  * │   3. If yes, convert LinkedList to Red-Black Tree                                                              │
//  * │                                                                                                                │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  * HASH CALCULATION (Before vs After Java 8):
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                                │
//  * │   BEFORE JAVA 8 (Complex):                                                                                     │
//  * │   static int hash(int h) {                                                                                     │
//  * │       h ^= (h >>> 20) ^ (h >>> 12);                                                                            │
//  * │       return h ^ (h >>> 7) ^ (h >>> 4);                                                                        │
//  * │   }                                                                                                            │
//  * │                                                                                                                │
//  * │   JAVA 8+ (Simplified):                                                                                        │
//  * │   static final int hash(Object key) {                                                                          │
//  * │       int h;                                                                                                   │
//  * │       return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);                                            │
//  * │   }                                                                                                            │
//  * │                                                                                                                │
//  * │   Why XOR with upper 16 bits?                                                                                  │
//  * │   • Spreads impact of higher bits to lower bits                                                                │
//  * │   • Important because bucket index = hash & (n-1) only uses lower bits                                         │
//  * │   • Reduces collisions when hashCodes differ only in higher bits                                               │
//  * │                                                                                                                │
//  * │   Example:                                                                                                     │
//  * │   hashCode = 0x12345678                                                                                        │
//  * │   h >>> 16 = 0x00001234                                                                                        │
//  * │   h ^ (h >>> 16) = 0x1234444C  (spreads high bits influence to low bits)                                       │
//  * │                                                                                                                │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  * TIME COMPLEXITY COMPARISON:
//  * ┌────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                               │
//  * │                        │        BEFORE JAVA 8           │         JAVA 8+                                     │
//  * │   ─────────────────────┼────────────────────────────────┼─────────────────────────────────────────────────────│
//  * │   Best Case            │        O(1)                    │         O(1)                                        │
//  * │   Average Case         │        O(1)                    │         O(1)                                        │
//  * │   Worst Case           │        O(n) - LinkedList       │         O(log n) - Red-Black Tree ⭐                │
//  * │                                                                                                               │
//  * └────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  * RESIZING MECHANISM:
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                                │
//  * │   When: size > threshold (capacity × loadFactor)                                                               │
//  * │   How: newCapacity = oldCapacity << 1 (double the size)                                                        │
//  * │                                                                                                                │
//  * │   BEFORE JAVA 8: Rehash all entries, recalculate bucket positions                                              │
//  * │                                                                                                                │
//  * │   JAVA 8+ OPTIMIZATION:                                                                                        │
//  * │   • Since capacity is power of 2, entry either stays in same bucket OR moves to (oldIndex + oldCapacity)       │
//  * │   • Only need to check one bit (no full rehash needed)                                                         │
//  * │                                                                                                                │
//  * │   Example (capacity 16 → 32):                                                                                  │
//  * │   hash = 0b...10101 (binary)                                                                                   │
//  * │   oldIndex = hash & 15 (0b1111) = 5                                                                            │
//  * │   newIndex = hash & 31 (0b11111) = 5 or 21                                                                     │
//  * │   Check bit: (hash & oldCapacity) == 0 ? stay : move                                                           │
//  * │                                                                                                                │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  *
//  * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
//  * ║                                    4. LINKEDHASHMAP INTERNAL WORKING                                          ║
//  * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
//  *
//  * INTERNAL STRUCTURE:
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                                │
//  * │   LinkedHashMap extends HashMap, adds doubly-linked list for ordering                                          │
//  * │                                                                                                                │
//  * │   HashMap buckets (same as HashMap):                                                                           │
//  * │   [0] → Entry → Entry                                                                                          │
//  * │   [1] → null                                                                                                   │
//  * │   [2] → Entry                                                                                                  │
//  * │   ...                                                                                                          │
//  * │                                                                                                                │
//  * │   PLUS: Doubly-linked list connecting all entries:                                                             │
//  * │                                                                                                                │
//  * │   head ──►┌───────┐    ┌───────┐    ┌───────┐    ┌───────┐◄── tail                                             │
//  * │           │ K1:V1 │◄──►│ K2:V2 │◄──►│ K3:V3 │◄──►│ K4:V4 │                                                     │
//  * │           └───────┘    └───────┘    └───────┘    └───────┘                                                     │
//  * │           (before)     (before)     (before)     (after)                                                       │
//  * │                                                                                                                │
//  * │   Entry Structure:                                                                                             │
//  * │   static class Entry<K,V> extends HashMap.Node<K,V> {                                                          │
//  * │       Entry<K,V> before;    // Previous entry in linked list                                                   │
//  * │       Entry<K,V> after;     // Next entry in linked list                                                       │
//  * │   }                                                                                                            │
//  * │                                                                                                                │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  * ORDERING MODES:
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                                │
//  * │   1. INSERTION ORDER (default):                                                                                │
//  * │      new LinkedHashMap<>()                                                                                     │
//  * │      → Entries ordered by insertion sequence                                                                   │
//  * │                                                                                                                │
//  * │   2. ACCESS ORDER (for LRU cache):                                                                             │
//  * │      new LinkedHashMap<>(16, 0.75f, true)  // accessOrder = true                                               │
//  * │      → Most recently accessed entry moves to end                                                               │
//  * │                                                                                                                │
//  * │   LRU Cache Implementation:                                                                                    │
//  * │   class LRUCache<K,V> extends LinkedHashMap<K,V> {                                                             │
//  * │       private final int maxSize;                                                                               │
//  * │                                                                                                                │
//  * │       LRUCache(int maxSize) {                                                                                  │
//  * │           super(16, 0.75f, true);  // accessOrder = true                                                       │
//  * │           this.maxSize = maxSize;                                                                              │
//  * │       }                                                                                                        │
//  * │                                                                                                                │
//  * │       @Override                                                                                                │
//  * │       protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {                                             │
//  * │           return size() > maxSize;  // Remove oldest when full                                                 │
//  * │       }                                                                                                        │
//  * │   }                                                                                                            │
//  * │                                                                                                                │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  *
//  * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
//  * ║                                    5. TREEMAP INTERNAL WORKING                                                ║
//  * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
//  *
//  * INTERNAL STRUCTURE (Red-Black Tree):
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                                │
//  * │   TreeMap uses a Red-Black Tree (self-balancing BST)                                                           │
//  * │                                                                                                                │
//  * │                           ┌────────────────┐                                                                   │
//  * │                           │    50 (BLACK)  │  ← root                                                           │
//  * │                           └────────────────┘                                                                   │
//  * │                          ╱                  ╲                                                                  │
//  * │               ┌────────────────┐    ┌────────────────┐                                                         │
//  * │               │   25 (RED)     │    │    75 (RED)    │                                                         │
//  * │               └────────────────┘    └────────────────┘                                                         │
//  * │              ╱            ╲              ╱            ╲                                                        │
//  * │      ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐                                                    │
//  * │      │10 (BLACK)│  │30 (BLACK)│  │60 (BLACK)│  │90 (BLACK)│                                                    │
//  * │      └──────────┘  └──────────┘  └──────────┘  └──────────┘                                                    │
//  * │                                                                                                                │
//  * │   Entry Structure:                                                                                             │
//  * │   static final class Entry<K,V> implements Map.Entry<K,V> {                                                    │
//  * │       K key;                                                                                                   │
//  * │       V value;                                                                                                 │
//  * │       Entry<K,V> left;                                                                                         │
//  * │       Entry<K,V> right;                                                                                        │
//  * │       Entry<K,V> parent;                                                                                       │
//  * │       boolean color = BLACK;  // true = RED, false = BLACK                                                     │
//  * │   }                                                                                                            │
//  * │                                                                                                                │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  * RED-BLACK TREE PROPERTIES:
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                                │
//  * │   1. Every node is either RED or BLACK                                                                         │
//  * │   2. Root is always BLACK                                                                                      │
//  * │   3. All leaves (NIL) are BLACK                                                                                │
//  * │   4. RED node cannot have RED child (no two consecutive reds)                                                  │
//  * │   5. Every path from root to leaf has same number of BLACK nodes                                               │
//  * │                                                                                                                │
//  * │   These properties guarantee: height ≤ 2 × log₂(n+1)                                                           │
//  * │   → All operations O(log n) guaranteed!                                                                        │
//  * │                                                                                                                │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  * TIME COMPLEXITY:
//  * ┌────────────────────┬─────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │   Operation        │   Complexity                                                                               │
//  * ├────────────────────┼─────────────────────────────────────────────────────────────────────────────────────────────┤
//  * │   get(key)         │   O(log n) - Binary search                                                                 │
//  * │   put(key, value)  │   O(log n) - Find position + rebalance                                                     │
//  * │   remove(key)      │   O(log n) - Find + rebalance                                                              │
//  * │   firstKey/lastKey │   O(log n) - Traverse left/right                                                           │
//  * │   floorKey/ceilingKey │ O(log n) - Nearest key operations                                                       │
//  * │   subMap/headMap   │   O(log n) - View operations                                                               │
//  * └────────────────────┴─────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  *
//  * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
//  * ║                                    6. CONCURRENTHASHMAP INTERNAL WORKING                                      ║
//  * ║                                    (BEFORE vs AFTER JAVA 8) ⭐⭐⭐                                              ║
//  * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
//  *
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                                │
//  * │   ╔═══════════════════════════════════════════════════════════════════════════════════════════════════════════╗│
//  * │   ║                           BEFORE JAVA 8 (Segment-based locking)                                          ║│
//  * │   ╚═══════════════════════════════════════════════════════════════════════════════════════════════════════════╝│
//  * │                                                                                                                │
//  * │   Structure: Array of Segments, each Segment is a mini-HashMap with its own lock                               │
//  * │                                                                                                                │
//  * │   Segment[] segments (default 16 segments)                                                                     │
//  * │   ┌─────────────────────────────────────────────────────────────────────────────────────────────────┐           │
//  * │   │ Segment[0]   Segment[1]   Segment[2]   Segment[3]  ...  Segment[15]                            │           │
//  * │   │     🔒            🔒            🔒            🔒              🔒                               │           │
//  * │   │   ┌───┐        ┌───┐        ┌───┐        ┌───┐          ┌───┐                                  │           │
//  * │   │   │ H │        │ H │        │ H │        │ H │          │ H │  (each has HashEntry[] table)    │           │
//  * │   │   └───┘        └───┘        └───┘        └───┘          └───┘                                  │           │
//  * │   └─────────────────────────────────────────────────────────────────────────────────────────────────┘           │
//  * │                                                                                                                │
//  * │   class Segment<K,V> extends ReentrantLock {                                                                   │
//  * │       volatile HashEntry<K,V>[] table;                                                                         │
//  * │       int count;                                                                                               │
//  * │       int threshold;                                                                                           │
//  * │   }                                                                                                            │
//  * │                                                                                                                │
//  * │   Concurrency Level: Up to 16 threads can write simultaneously (one per segment)                               │
//  * │   Limitation: Fixed number of segments, uneven distribution possible                                           │
//  * │                                                                                                                │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                                │
//  * │   ╔═══════════════════════════════════════════════════════════════════════════════════════════════════════════╗│
//  * │   ║                           JAVA 8+ (Node-based with CAS + synchronized) ⭐                                ║│
//  * │   ╚═══════════════════════════════════════════════════════════════════════════════════════════════════════════╝│
//  * │                                                                                                                │
//  * │   Structure: Same as HashMap (Node[] + LinkedList/Tree) but with fine-grained locking                          │
//  * │                                                                                                                │
//  * │   Node<K,V>[] table                                                                                            │
//  * │   ┌─────────────────────────────────────────────────────────────────────────────────────────────────┐           │
//  * │   │  [0]🔒   [1]🔒   [2]🔒   [3]🔒   [4]🔒   [5]🔒   ...   [n-1]🔒                                 │           │
//  * │   │    │       │       │       │       │       │             │                                     │           │
//  * │   │    ▼       ▼       ▼       ▼       ▼       ▼             ▼                                     │           │
//  * │   │  Node    Node   TreeNode  null   Node    null          null                                    │           │
//  * │   │    │              ╱  ╲              │                                                          │           │
//  * │   │    ▼         TreeNode               ▼                                                          │           │
//  * │   │  Node                              null                                                        │           │
//  * │   └─────────────────────────────────────────────────────────────────────────────────────────────────┘           │
//  * │                                                                                                                │
//  * │   KEY CHANGES:                                                                                                 │
//  * │   1. Lock per bucket (not per segment) → Much finer granularity                                                │
//  * │   2. CAS (Compare-And-Swap) for lock-free reads and simple updates                                             │
//  * │   3. synchronized block only when modifying bucket head                                                        │
//  * │   4. Supports treeification (like HashMap) for O(log n) worst case                                             │
//  * │                                                                                                                │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  * GET OPERATION (LOCK-FREE):
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                                │
//  * │   public V get(Object key) {                                                                                   │
//  * │       Node<K,V>[] tab; Node<K,V> e;                                                                            │
//  * │       int h = spread(key.hashCode());                                                                          │
//  * │       if ((tab = table) != null && (e = tabAt(tab, (n-1) & h)) != null) {                                      │
//  * │           // Uses volatile read (tabAt uses Unsafe.getObjectVolatile)                                          │
//  * │           // No lock needed! Just traverse the chain                                                           │
//  * │           if (e.hash == h && key.equals(e.key)) return e.val;                                                  │
//  * │           while ((e = e.next) != null) {                                                                       │
//  * │               if (e.hash == h && key.equals(e.key)) return e.val;                                              │
//  * │           }                                                                                                    │
//  * │       }                                                                                                        │
//  * │       return null;                                                                                             │
//  * │   }                                                                                                            │
//  * │                                                                                                                │
//  * │   No locking! Uses volatile reads for visibility.                                                              │
//  * │                                                                                                                │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  * PUT OPERATION:
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                                │
//  * │   1. If bucket empty: CAS to insert (lock-free)                                                                │
//  * │      if (casTabAt(tab, i, null, new Node<>(hash, key, value))) → Success!                                      │
//  * │                                                                                                                │
//  * │   2. If bucket not empty: synchronized on first node                                                           │
//  * │      synchronized (f) {  // f = first node in bucket                                                           │
//  * │          // Traverse and update/insert                                                                         │
//  * │      }                                                                                                         │
//  * │                                                                                                                │
//  * │   3. If tree: Use tree insertion with lock on root                                                             │
//  * │                                                                                                                │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  * COMPARISON TABLE:
//  * ┌────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                               │
//  * │   Feature                    │   Before Java 8              │   Java 8+                                       │
//  * │   ──────────────────────────┼──────────────────────────────┼─────────────────────────────────────────────────│
//  * │   Locking Granularity        │   Segment (16 by default)    │   Per bucket (millions possible)                │
//  * │   Read Locking               │   No lock (volatile)         │   No lock (volatile)                            │
//  * │   Write Locking              │   Segment lock               │   CAS + synchronized on bucket                  │
//  * │   Collision Handling         │   LinkedList only            │   LinkedList + Red-Black Tree                   │
//  * │   Worst Case                 │   O(n)                       │   O(log n)                                      │
//  * │   Memory Overhead            │   Segment objects            │   None (uses bucket heads)                      │
//  * │   Concurrency Level          │   Fixed (16)                 │   Dynamic (based on buckets)                    │
//  * │                                                                                                               │
//  * └────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  *
//  * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
//  * ║                                    7. HASHSET / TREESET / LINKEDHASHSET INTERNAL                              ║
//  * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
//  *
//  * INTERNAL STRUCTURE:
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                                │
//  * │   All Set implementations are backed by Map implementations!                                                   │
//  * │                                                                                                                │
//  * │   ┌───────────────────────────────────────────────────────────────────────────────────────────────────────────┐│
//  * │   │  HashSet<E>       →  Backed by HashMap<E, Object>                                                        ││
//  * │   │  TreeSet<E>       →  Backed by TreeMap<E, Object>                                                        ││
//  * │   │  LinkedHashSet<E> →  Backed by LinkedHashMap<E, Object>                                                  ││
//  * │   └───────────────────────────────────────────────────────────────────────────────────────────────────────────┘│
//  * │                                                                                                                │
//  * │   HashSet Source:                                                                                              │
//  * │   public class HashSet<E> extends AbstractSet<E> implements Set<E> {                                           │
//  * │       private transient HashMap<E,Object> map;                                                                 │
//  * │       private static final Object PRESENT = new Object();  // Dummy value                                      │
//  * │                                                                                                                │
//  * │       public boolean add(E e) {                                                                                │
//  * │           return map.put(e, PRESENT) == null;  // Key = element, Value = dummy                                 │
//  * │       }                                                                                                        │
//  * │                                                                                                                │
//  * │       public boolean contains(Object o) {                                                                      │
//  * │           return map.containsKey(o);                                                                           │
//  * │       }                                                                                                        │
//  * │                                                                                                                │
//  * │       public boolean remove(Object o) {                                                                        │
//  * │           return map.remove(o) == PRESENT;                                                                     │
//  * │       }                                                                                                        │
//  * │   }                                                                                                            │
//  * │                                                                                                                │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  * WHY USE DUMMY VALUE?
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                                │
//  * │   • Maps don't allow duplicate keys → perfect for Set uniqueness                                               │
//  * │   • add() returns true if element was new (map.put returns null for new key)                                   │
//  * │   • Using static final PRESENT saves memory (same object for all entries)                                      │
//  * │   • Alternative: Using Boolean.TRUE would also work but PRESENT is cleaner                                     │
//  * │                                                                                                                │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  *
//  * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
//  * ║                                    8. PRIORITYQUEUE (HEAP) INTERNAL WORKING                                   ║
//  * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
//  *
//  * INTERNAL STRUCTURE (Binary Heap):
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                                │
//  * │   PriorityQueue uses a Binary Min-Heap (by default) stored in an array                                         │
//  * │                                                                                                                │
//  * │   Heap Property: Parent ≤ Children (Min-Heap)                                                                  │
//  * │                                                                                                                │
//  * │   Tree Visualization:           Array Representation:                                                          │
//  * │                                                                                                                │
//  * │          ┌───┐                  ┌───┬───┬───┬───┬───┬───┬───┐                                                   │
//  * │          │ 1 │                  │ 1 │ 3 │ 2 │ 7 │ 5 │ 6 │ 4 │                                                   │
//  * │          └───┘                  └───┴───┴───┴───┴───┴───┴───┘                                                   │
//  * │         ╱     ╲                  [0] [1] [2] [3] [4] [5] [6]                                                    │
//  * │      ┌───┐   ┌───┐                                                                                             │
//  * │      │ 3 │   │ 2 │              Index Relationships:                                                           │
//  * │      └───┘   └───┘              • Parent of node i:      (i-1) / 2                                             │
//  * │     ╱   ╲   ╱   ╲               • Left child of node i:  2*i + 1                                               │
//  * │  ┌───┐ ┌───┐ ┌───┐ ┌───┐       • Right child of node i: 2*i + 2                                               │
//  * │  │ 7 │ │ 5 │ │ 6 │ │ 4 │                                                                                       │
//  * │  └───┘ └───┘ └───┘ └───┘                                                                                       │
//  * │                                                                                                                │
//  * │   Object[] queue;    // Backing array                                                                          │
//  * │   int size;          // Number of elements                                                                     │
//  * │                                                                                                                │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  * OPERATIONS:
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                                │
//  * │   OFFER/ADD (Insert):                         POLL (Remove Min):                                               │
//  * │   1. Add element at end (size)                1. Store root (min element)                                      │
//  * │   2. Sift Up: Compare with parent             2. Move last element to root                                     │
//  * │   3. Swap if child < parent                   3. Sift Down: Compare with children                              │
//  * │   4. Repeat until heap property restored      4. Swap with smaller child                                       │
//  * │                                               5. Repeat until heap property restored                           │
//  * │   Time: O(log n)                              Time: O(log n)                                                   │
//  * │                                                                                                                │
//  * │   PEEK:                                       CONTAINS:                                                        │
//  * │   Return queue[0]                             Linear scan: O(n)                                                │
//  * │   Time: O(1)                                                                                                   │
//  * │                                                                                                                │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  * SIFT UP (Heapify Up):
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                                │
//  * │   private void siftUp(int k, E x) {                                                                            │
//  * │       while (k > 0) {                                                                                          │
//  * │           int parent = (k - 1) >>> 1;  // Parent index                                                         │
//  * │           Object e = queue[parent];                                                                            │
//  * │           if (comparator.compare(x, (E) e) >= 0)  // x >= parent, stop                                         │
//  * │               break;                                                                                           │
//  * │           queue[k] = e;                           // Move parent down                                          │
//  * │           k = parent;                             // Move up                                                   │
//  * │       }                                                                                                        │
//  * │       queue[k] = x;                                                                                            │
//  * │   }                                                                                                            │
//  * │                                                                                                                │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  *
//  * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
//  * ║                                    9. ARRAYDEQUE INTERNAL WORKING                                             ║
//  * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
//  *
//  * INTERNAL STRUCTURE (Circular Array):
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                                │
//  * │   ArrayDeque uses a resizable circular array (ring buffer)                                                     │
//  * │                                                                                                                │
//  * │   Object[] elements;                                                                                           │
//  * │   int head;        // Index of first element                                                                   │
//  * │   int tail;        // Index where next element will be added at end                                            │
//  * │                                                                                                                │
//  * │   Circular Array:                                                                                              │
//  * │   ┌─────┬─────┬─────┬─────┬─────┬─────┬─────┬─────┐                                                            │
//  * │   │  E  │  F  │  G  │ null│ null│  C  │  D  │  E  │                                                            │
//  * │   └─────┴─────┴─────┴─────┴─────┴─────┴─────┴─────┘                                                            │
//  * │     [0]   [1]   [2]   [3]   [4]   [5]   [6]   [7]                                                              │
//  * │                              ↑           ↑                                                                     │
//  * │                            tail        head                                                                    │
//  * │                                                                                                                │
//  * │   Logical view: [C, D, E, E, F, G]                                                                             │
//  * │                  ↑ head        ↑ before tail                                                                   │
//  * │                                                                                                                │
//  * │   Index Calculation (circular):                                                                                │
//  * │   • Next index: (i + 1) & (length - 1)   // Wrap around using bitwise AND                                      │
//  * │   • Prev index: (i - 1) & (length - 1)   // Works because length is power of 2                                 │
//  * │                                                                                                                │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  * WHY ARRAYDEQUE OVER LINKEDLIST FOR STACK/QUEUE?
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                                │
//  * │   ArrayDeque Advantages:                                                                                       │
//  * │   • Better cache locality (contiguous memory)                                                                  │
//  * │   • Less memory overhead (no node objects)                                                                     │
//  * │   • Faster operations (no object allocation for each element)                                                  │
//  * │                                                                                                                │
//  * │   LinkedList Advantages:                                                                                       │
//  * │   • No capacity limits (no resizing needed)                                                                    │
//  * │   • Constant time insertion at arbitrary positions (if you have the node)                                      │
//  * │                                                                                                                │
//  * │   Recommendation: Use ArrayDeque for Stack/Queue operations unless you need List features                      │
//  * │                                                                                                                │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  *
//  * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
//  * ║                                    10. VECTOR & STACK (LEGACY)                                                ║
//  * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
//  *
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                                │
//  * │   VECTOR:                                                                                                      │
//  * │   • Synchronized version of ArrayList                                                                          │
//  * │   • Every method is synchronized (full lock)                                                                   │
//  * │   • Growth: 100% (doubles capacity) vs ArrayList's 50%                                                         │
//  * │   • Legacy: Use Collections.synchronizedList(new ArrayList<>()) or CopyOnWriteArrayList                        │
//  * │                                                                                                                │
//  * │   STACK:                                                                                                       │
//  * │   • Extends Vector (inherits synchronization)                                                                  │
//  * │   • LIFO operations: push(), pop(), peek()                                                                     │
//  * │   • Legacy: Use ArrayDeque instead (Javadoc recommends this)                                                   │
//  * │                                                                                                                │
//  * │   Why avoid Vector/Stack?                                                                                      │
//  * │   • Full synchronization is overkill for most uses                                                             │
//  * │   • Stack extends Vector (bad inheritance - Stack IS-A Vector?)                                                │
//  * │   • ArrayDeque is faster for stack operations                                                                  │
//  * │   • ConcurrentHashMap/CopyOnWriteArrayList are better for concurrency                                          │
//  * │                                                                                                                │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  *
//  * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
//  * ║                                    11. JAVA 8+ COLLECTION ENHANCEMENTS ⭐⭐⭐                                  ║
//  * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
//  *
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                                │
//  * │   ╔═══════════════════════════════════════════════════════════════════════════════════════════════════════════╗│
//  * │   ║                                    NEW DEFAULT METHODS                                                    ║│
//  * │   ╚═══════════════════════════════════════════════════════════════════════════════════════════════════════════╝│
//  * │                                                                                                                │
//  * │   ITERABLE (applies to all Collections):                                                                       │
//  * │   ────────────────────────────────────────                                                                     │
//  * │   • forEach(Consumer)           - Iterate with lambda                                                          │
//  * │   • spliterator()               - For parallel processing                                                      │
//  * │                                                                                                                │
//  * │   COLLECTION:                                                                                                  │
//  * │   ────────────────────────────────────────                                                                     │
//  * │   • removeIf(Predicate)         - Remove elements matching condition                                           │
//  * │   • stream() / parallelStream() - Create stream                                                                │
//  * │                                                                                                                │
//  * │   LIST:                                                                                                        │
//  * │   ────────────────────────────────────────                                                                     │
//  * │   • replaceAll(UnaryOperator)   - Transform all elements in-place                                              │
//  * │   • sort(Comparator)            - Sort in-place (vs Collections.sort)                                          │
//  * │                                                                                                                │
//  * │   MAP:                                                                                                         │
//  * │   ────────────────────────────────────────                                                                     │
//  * │   • forEach(BiConsumer)                  - Iterate key-value pairs                                             │
//  * │   • getOrDefault(key, defaultValue)      - Get with fallback                                                   │
//  * │   • putIfAbsent(key, value)              - Add only if key absent                                              │
//  * │   • remove(key, value)                   - Remove only if value matches                                        │
//  * │   • replace(key, value)                  - Replace only if key exists                                          │
//  * │   • replace(key, oldValue, newValue)     - Replace only if value matches                                       │
//  * │   • replaceAll(BiFunction)               - Transform all values                                                │
//  * │   • compute(key, BiFunction)             - Compute new value                                                   │
//  * │   • computeIfAbsent(key, Function)       - Compute if key absent                                               │
//  * │   • computeIfPresent(key, BiFunction)    - Compute if key present                                              │
//  * │   • merge(key, value, BiFunction)        - Merge with existing value                                           │
//  * │                                                                                                                │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                                │
//  * │   ╔═══════════════════════════════════════════════════════════════════════════════════════════════════════════╗│
//  * │   ║                                    BEFORE vs AFTER JAVA 8 EXAMPLES                                        ║│
//  * │   ╚═══════════════════════════════════════════════════════════════════════════════════════════════════════════╝│
//  * │                                                                                                                │
//  * │   1. ITERATING:                                                                                                │
//  * │   ───────────────                                                                                              │
//  * │   BEFORE:                                    AFTER:                                                            │
//  * │   for (String s : list) {                    list.forEach(s -> System.out.println(s));                         │
//  * │       System.out.println(s);                 // or                                                             │
//  * │   }                                          list.forEach(System.out::println);                                │
//  * │                                                                                                                │
//  * │   2. REMOVING ELEMENTS:                                                                                        │
//  * │   ───────────────                                                                                              │
//  * │   BEFORE:                                    AFTER:                                                            │
//  * │   Iterator<String> it = list.iterator();     list.removeIf(s -> s.isEmpty());                                  │
//  * │   while (it.hasNext()) {                                                                                       │
//  * │       if (it.next().isEmpty())                                                                                 │
//  * │           it.remove();                                                                                         │
//  * │   }                                                                                                            │
//  * │                                                                                                                │
//  * │   3. TRANSFORMING LIST:                                                                                        │
//  * │   ───────────────                                                                                              │
//  * │   BEFORE:                                    AFTER:                                                            │
//  * │   for (int i = 0; i < list.size(); i++) {    list.replaceAll(String::toUpperCase);                             │
//  * │       list.set(i, list.get(i).toUpperCase());                                                                  │
//  * │   }                                                                                                            │
//  * │                                                                                                                │
//  * │   4. SORTING:                                                                                                  │
//  * │   ───────────────                                                                                              │
//  * │   BEFORE:                                    AFTER:                                                            │
//  * │   Collections.sort(list,                     list.sort(Comparator.comparing(String::length));                  │
//  * │       new Comparator<String>() {                                                                               │
//  * │           public int compare(String a, String b) {                                                             │
//  * │               return a.length() - b.length();                                                                  │
//  * │           }                                                                                                    │
//  * │       });                                                                                                      │
//  * │                                                                                                                │
//  * │   5. MAP OPERATIONS:                                                                                           │
//  * │   ───────────────                                                                                              │
//  * │   BEFORE:                                    AFTER:                                                            │
//  * │   if (!map.containsKey(key)) {               map.putIfAbsent(key, value);                                      │
//  * │       map.put(key, value);                                                                                     │
//  * │   }                                                                                                            │
//  * │                                                                                                                │
//  * │   String val = map.get(key);                 String val = map.getOrDefault(key, "default");                    │
//  * │   if (val == null) val = "default";                                                                            │
//  * │                                                                                                                │
//  * │   if (map.containsKey(key)) {                map.computeIfPresent(key, (k, v) -> v + 1);                       │
//  * │       map.put(key, map.get(key) + 1);                                                                          │
//  * │   }                                                                                                            │
//  * │                                                                                                                │
//  * │   List<String> list = map.get(key);          map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);      │
//  * │   if (list == null) {                                                                                          │
//  * │       list = new ArrayList<>();                                                                                │
//  * │       map.put(key, list);                                                                                      │
//  * │   }                                                                                                            │
//  * │   list.add(value);                                                                                             │
//  * │                                                                                                                │
//  * │   6. MERGING VALUES:                                                                                           │
//  * │   ───────────────                                                                                              │
//  * │   BEFORE:                                    AFTER:                                                            │
//  * │   Integer count = map.get(word);             map.merge(word, 1, Integer::sum);                                 │
//  * │   if (count == null) {                                                                                         │
//  * │       map.put(word, 1);                                                                                        │
//  * │   } else {                                                                                                     │
//  * │       map.put(word, count + 1);                                                                                │
//  * │   }                                                                                                            │
//  * │                                                                                                                │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  *
//  * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
//  * ║                                    12. SPLITERATOR (Java 8+)                                                  ║
//  * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
//  *
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                                │
//  * │   Spliterator = Splitting Iterator (for parallel processing)                                                   │
//  * │                                                                                                                │
//  * │   Key Methods:                                                                                                 │
//  * │   • trySplit()           - Attempts to partition elements for parallel processing                              │
//  * │   • tryAdvance(Consumer) - Process one element if available                                                    │
//  * │   • forEachRemaining()   - Process all remaining elements                                                      │
//  * │   • estimateSize()       - Estimate remaining elements                                                         │
//  * │   • characteristics()    - Returns bitfield of characteristics                                                 │
//  * │                                                                                                                │
//  * │   Characteristics:                                                                                             │
//  * │   • ORDERED    - Elements have defined encounter order                                                         │
//  * │   • DISTINCT   - No duplicates                                                                                 │
//  * │   • SORTED     - Elements are sorted                                                                           │
//  * │   • SIZED      - Known size                                                                                    │
//  * │   • NONNULL    - No null elements                                                                              │
//  * │   • IMMUTABLE  - Source cannot be modified                                                                     │
//  * │   • CONCURRENT - Can be modified during traversal                                                              │
//  * │   • SUBSIZED   - Splits are also SIZED                                                                         │
//  * │                                                                                                                │
//  * │   Why Spliterator?                                                                                             │
//  * │   • Iterator designed for sequential processing                                                                │
//  * │   • Spliterator enables efficient parallel stream processing                                                   │
//  * │   • Each collection provides optimized spliterator (ArrayList splits by index ranges)                          │
//  * │                                                                                                                │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  *
//  * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
//  * ║                                    13. COLLECTIONS FACTORY METHODS (Java 9+)                                  ║
//  * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
//  *
//  * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
//  * │                                                                                                                │
//  * │   BEFORE JAVA 9:                             JAVA 9+:                                                          │
//  * │   ──────────────                             ────────                                                          │
//  * │   List<String> list =                        List<String> list = List.of("A", "B", "C");                       │
//  * │       Arrays.asList("A", "B", "C");          // Immutable!                                                     │
//  * │   // or                                                                                                        │
//  * │   List<String> list = new ArrayList<>();     Set<String> set = Set.of("A", "B", "C");                          │
//  * │   list.add("A");                             // Immutable, no duplicates allowed!                              │
//  * │   list.add("B");                                                                                               │
//  * │   list.add("C");                             Map<String, Integer> map = Map.of(                                │
//  * │                                                  "A", 1, "B", 2, "C", 3);                                      │
//  * │   Set<String> set = new HashSet<>();         // Immutable!                                                     │
//  * │   set.add("A");                                                                                                │
//  * │   set.add("B");                              Map<String, Integer> map = Map.ofEntries(                         │
//  * │                                                  Map.entry("A", 1),                                            │
//  * │   Map<String, Integer> map =                     Map.entry("B", 2));                                           │
//  * │       new HashMap<>();                       // For more than 10 entries                                       │
//  * │   map.put("A", 1);                                                                                             │
//  * │   map.put("B", 2);                                                                                             │
//  * │                                                                                                                │
//  * │   Key Points:                                                                                                  │
//  * │   • List.of(), Set.of(), Map.of() create IMMUTABLE collections                                                 │
//  * │   • No null elements allowed                                                                                   │
//  * │   • Set.of() and Map.of() reject duplicates (IllegalArgumentException)                                         │
//  * │   • Backed by optimized implementations (not ArrayList/HashSet/HashMap)                                        │
//  * │                                                                                                                │
//  * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
//  *
//  *
//  * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
//  * ║                                    14. INTERVIEW QUESTIONS                                                    ║
//  * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
//  *
//  * Q1: Why HashMap uses Red-Black Tree after Java 8 instead of AVL Tree?
//  * A: Red-Black Tree requires fewer rotations during insert/delete (less strict balancing).
//  *    AVL is more strictly balanced → faster lookups but slower modifications.
//  *    HashMap does more inserts than lookups typically, so RB Tree is better choice.
//  *
//  * Q2: What is the difference between HashMap's capacity and size?
//  * A: Size = actual number of entries (key-value pairs) in the map.
//  *    Capacity = length of internal array (bucket count), always power of 2.
//  *    threshold = capacity × loadFactor (when to resize).
//  *
//  * Q3: Why are HashMap's capacity always powers of 2?
//  * A: For efficient bucket calculation: index = hash & (capacity - 1)
//  *    This bitwise AND is faster than modulo and works correctly only for powers of 2.
//  *    (capacity - 1) creates a bitmask (e.g., 16-1 = 15 = 0b1111).
//  *
//  * Q4: Explain lazy initialization in Java 8 ArrayList.
//  * A: Before Java 8: new ArrayList() allocated array of size 10 immediately.
//  *    Java 8+: Empty array used initially, grows to 10 on first add().
//  *    Benefits: Saves memory when ArrayList is created but never used.
//  *
//  * Q5: Why does ConcurrentHashMap not allow null keys/values?
//  * A: In concurrent environment, can't distinguish between "key not found" (returns null)
//  *    and "key found with null value". No ambiguity in single-threaded HashMap.
//  *    Also, containsKey() followed by get() is not atomic → race condition.
//  *
//  * Q6: What happens when you insert more than 8 elements with same hashCode in HashMap (Java 8+)?
//  * A: First, it checks if table capacity ≥ 64 (MIN_TREEIFY_CAPACITY).
//  *    If yes: LinkedList converts to Red-Black Tree (treeification).
//  *    If no: HashMap resizes instead (might help distribution).
//  *
//  * Q7: Why use ArrayDeque instead of Stack class?
//  * A: Stack extends Vector → all methods synchronized (unnecessary overhead).
//  *    ArrayDeque is not synchronized, faster, and follows Deque interface.
//  *    Stack has problematic inheritance (search() method returns 1-based index).
//  *
//  * Q8: How does LinkedHashMap maintain insertion order?
//  * A: Extends HashMap but each Entry has before/after pointers.
//  *    Maintains doubly-linked list of all entries in insertion order.
//  *    head points to oldest, tail to newest entry.
//  *
//  * Q9: Explain the difference between compute() and merge() in Map.
//  * A: compute(key, (k,v) -> ...): v is current value (null if absent), can return null to remove.
//  *    merge(key, value, (oldV, newV) -> ...): Only called if key exists, value is always provided.
//  *    merge is simpler for combining values, compute is more flexible.
//  *
//  * Q10: What is the internal difference between HashSet and HashMap?
//  * A: HashSet internally uses HashMap where:
//  *     - Set element becomes the key
//  *     - Value is a static dummy object (PRESENT = new Object())
//  *     - add(e) calls map.put(e, PRESENT) and checks if return was null
//  *
//  *
//  * @author Java Interview Guide
//  */
// public class _10_CollectionsInternalDeepDive {

//     public static void main(String[] args) {
//         System.out.println("═══════════════════════════════════════════════════════════════════════════════════");
//         System.out.println("                 COLLECTIONS INTERNAL WORKING - DEEP DIVE");
//         System.out.println("═══════════════════════════════════════════════════════════════════════════════════\n");

//         // 1. ArrayList Internals
//         demonstrateArrayListInternals();

//         // 2. LinkedList Internals
//         demonstrateLinkedListInternals();

//         // 3. HashMap Internals (Java 8+)
//         demonstrateHashMapInternals();

//         // 4. LinkedHashMap for LRU Cache
//         demonstrateLRUCache();

//         // // 5. TreeMap Internals
//         // demonstrateTreeMapInternals();

//         // // 6. PriorityQueue Internals
//         // demonstratePriorityQueueInternals();

//         // // 7. Java 8+ Collection Enhancements
//         // demonstrateJava8Enhancements();

//         // // 8. ConcurrentHashMap Demo
//         // demonstrateConcurrentHashMap();
//     }

//     // ═══════════════════════════════════════════════════════════════════════════════════
//     //                         1. ARRAYLIST INTERNALS
//     // ═══════════════════════════════════════════════════════════════════════════════════

//     static void demonstrateArrayListInternals() {
//         System.out.println("▶ 1. ARRAYLIST INTERNALS:");
//         System.out.println("─────────────────────────────────────────────────────────────────────────────────");

//         // Java 8+ lazy initialization
//         ArrayList<String> list = new ArrayList<>();
//         System.out.println("   After new ArrayList(): size = " + list.size());
//         System.out.println("   (Internal array is empty until first add - Java 8+ lazy init)");

//         // Observe capacity growth
//         ArrayList<Integer> numbers = new ArrayList<>();
//         System.out.println("\n   Capacity growth demonstration:");
//         System.out.println("   Adding elements and observing size...");

//         for (int i = 0; i < 20; i++) {
//             numbers.add(i);
//             if (i == 0 || i == 10 || i == 15 || i == 19) {
//                 System.out.println("   After adding " + (i + 1) + " elements: size = " + numbers.size());
//             }
//         }

//         // Show that get() is O(1)
//         long start = System.nanoTime();
//         for (int i = 0; i < 100000; i++) {
//             numbers.get(10);  // Always same index
//         }
//         long time = System.nanoTime() - start;
//         System.out.println("\n   100,000 get(10) operations: " + (time / 1_000_000) + "ms (O(1) access)");

//         // ensureCapacity for optimization
//         ArrayList<Integer> optimized = new ArrayList<>();
//         optimized.ensureCapacity(1000);  // Pre-allocate to avoid resizing
//         System.out.println("   Tip: Use ensureCapacity(n) if you know approximate size");

//         System.out.println();
//     }

//     // ═══════════════════════════════════════════════════════════════════════════════════
//     //                         2. LINKEDLIST INTERNALS
//     // ═══════════════════════════════════════════════════════════════════════════════════

//     static void demonstrateLinkedListInternals() {
//         System.out.println("▶ 2. LINKEDLIST INTERNALS:");
//         System.out.println("─────────────────────────────────────────────────────────────────────────────────");

//         LinkedList<String> list = new LinkedList<>();
//         list.add("A");
//         list.add("B");
//         list.add("C");

//         System.out.println("   LinkedList: " + list);
//         System.out.println("   First (head): " + list.getFirst());
//         System.out.println("   Last (tail): " + list.getLast());

//         // Demonstrate O(1) add at ends
//         System.out.println("\n   Adding at ends (O(1)):");
//         list.addFirst("HEAD");
//         list.addLast("TAIL");
//         System.out.println("   After addFirst/addLast: " + list);

//         // Show optimized traversal
//         LinkedList<Integer> numbers = new LinkedList<>();
//         for (int i = 0; i < 10000; i++) {
//             numbers.add(i);
//         }

//         long start = System.nanoTime();
//         numbers.get(5000);  // Middle - will traverse from closer end
//         long timeMiddle = System.nanoTime() - start;

//         start = System.nanoTime();
//         numbers.get(9999);  // End - traverses from tail
//         long timeEnd = System.nanoTime() - start;

//         System.out.println("\n   get(5000) time: " + timeMiddle + " ns");
//         System.out.println("   get(9999) time: " + timeEnd + " ns (faster - closer to tail)");
//         System.out.println("   Note: LinkedList optimizes by traversing from closer end");

//         System.out.println();
//     }

//     // ═══════════════════════════════════════════════════════════════════════════════════
//     //                         3. HASHMAP INTERNALS
//     // ═══════════════════════════════════════════════════════════════════════════════════

//     static void demonstrateHashMapInternals() {
//         System.out.println("▶ 3. HASHMAP INTERNALS (Java 8+):");
//         System.out.println("─────────────────────────────────────────────────────────────────────────────────");

//         // Show hash spreading
//         String key = "testKey";
//         int originalHash = key.hashCode();
//         int spreadHash = originalHash ^ (originalHash >>> 16);

//         System.out.println("   Key: \"" + key + "\"");
//         System.out.println("   Original hashCode(): " + originalHash + " (binary: " +
//                 Integer.toBinaryString(originalHash) + ")");
//         System.out.println("   Spread hash (h ^ h>>>16): " + spreadHash);
//         System.out.println("   Bucket index (hash & 15): " + (spreadHash & 15));

//         // Demonstrate treeification threshold
//         System.out.println("\n   Treeification (Java 8+):");
//         System.out.println("   • TREEIFY_THRESHOLD = 8 (convert list→tree when bucket has >8 nodes)");
//         System.out.println("   • UNTREEIFY_THRESHOLD = 6 (convert tree→list when bucket has <6 nodes)");
//         System.out.println("   • MIN_TREEIFY_CAPACITY = 64 (minimum table size for tree conversion)");

//         // Show collision handling
//         HashMap<CollidingKey, String> map = new HashMap<>();
//         System.out.println("\n   Collision demonstration (all keys have same hashCode=42):");

//         for (int i = 0; i < 10; i++) {
//             map.put(new CollidingKey(i), "Value" + i);
//         }
//         System.out.println("   Added 10 entries with same hashCode");
//         System.out.println("   All in same bucket! After 8: converts to Red-Black Tree → O(log n) lookup");

//         // Verify retrieval
//         System.out.println("   get(key5) = " + map.get(new CollidingKey(5)));

//         System.out.println();
//     }

//     static class CollidingKey {
//         int id;

//         CollidingKey(int id) {
//             this.id = id;
//         }

//         @Override
//         public int hashCode() {
//             return 42;  // All same hashCode - causes collision
//         }

//         @Override
//         public boolean equals(Object o) {
//             if (this == o) return true;
//             if (!(o instanceof CollidingKey)) return false;
//             return id == ((CollidingKey) o).id;
//         }
//     }

//     // ═══════════════════════════════════════════════════════════════════════════════════
//     //                         4. LINKEDHASHMAP - LRU CACHE
//     // ═══════════════════════════════════════════════════════════════════════════════════

//     static void demonstrateLRUCache() {
//         System.out.println("▶ 4. LINKEDHASHMAP - LRU CACHE:");
//         System.out.println("─────────────────────────────────────────────────────────────────────────────────");

//         // LRU Cache with max 3 entries
//         LinkedHashMap<String, String> lruCache = new LinkedHashMap<>(16, 0.75f, true) {
//             @Override
//             protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
//                 boolean shouldRemove = size() > 3;
//                 if (shouldRemove) {
//                     System.out.println("   Evicting: " + eldest.getKey() + " (LRU)");
//                 }
//                 return shouldRemove;
//             }
//         };

//         System.out.println("   LRU Cache (max size = 3):");

//         lruCache.put("A", "Value-A");
//         lruCache.put("B", "Value-B");
//         lruCache.put("C", "Value-C");
//         System.out.println("   After adding A, B, C: " + lruCache.keySet());

//         // Access A - moves it to end (most recently used)
//         lruCache.get("A");
//         System.out.println("   After accessing A: " + lruCache.keySet() + " (A moved to end)");

//         // Add D - should evict B (least recently used)
//         System.out.print("   Adding D: ");
//         lruCache.put("D", "Value-D");

//             // ═══════════════════════════════════════════════════════════════════════════════════
//     //                         5. TREEMAP INTERNALS
//     // ═══════════════════════════════════════════════════════════════════════════════════

//     static void demonstrateTreeMapInternals() {
//         System.out.println("\n▶ 5. TREEMAP INTERNALS (Red-Black Tree):");
//         System.out.println("─────────────────────────────────────────────────────────────────────────────────");

//         TreeMap<Integer, String> treeMap = new TreeMap<>();

//         // Insert elements - internally creates Red-Black Tree
//         System.out.println("   Inserting elements: 50, 25, 75, 10, 30, 60, 90");
//         treeMap.put(50, "Fifty");
//         treeMap.put(25, "Twenty-Five");
//         treeMap.put(75, "Seventy-Five");
//         treeMap.put(10, "Ten");
//         treeMap.put(30, "Thirty");
//         treeMap.put(60, "Sixty");
//         treeMap.put(90, "Ninety");

//         System.out.println("\n   TreeMap (sorted order): " + treeMap.keySet());
//         System.out.println("   Note: Keys automatically sorted due to Red-Black Tree structure");

//         // Navigation operations - O(log n)
//         System.out.println("\n   Navigation Operations (O(log n)):");
//         System.out.println("   firstKey(): " + treeMap.firstKey());
//         System.out.println("   lastKey(): " + treeMap.lastKey());
//         System.out.println("   floorKey(55): " + treeMap.floorKey(55) + " (greatest key ≤ 55)");
//         System.out.println("   ceilingKey(55): " + treeMap.ceilingKey(55) + " (smallest key ≥ 55)");
//         System.out.println("   lowerKey(50): " + treeMap.lowerKey(50) + " (greatest key < 50)");
//         System.out.println("   higherKey(50): " + treeMap.higherKey(50) + " (smallest key > 50)");

//         // SubMap views
//         System.out.println("\n   SubMap Views:");
//         System.out.println("   headMap(50): " + treeMap.headMap(50).keySet() + " (keys < 50)");
//         System.out.println("   tailMap(50): " + treeMap.tailMap(50).keySet() + " (keys ≥ 50)");
//         System.out.println("   subMap(25, 75): " + treeMap.subMap(25, 75).keySet() + " (25 ≤ keys < 75)");

//         // Descending order
//         System.out.println("\n   descendingKeySet(): " + treeMap.descendingKeySet());

//         // Custom Comparator
//         System.out.println("\n   TreeMap with Custom Comparator (reverse order):");
//         TreeMap<Integer, String> reverseMap = new TreeMap<>(Comparator.reverseOrder());
//         reverseMap.putAll(treeMap);
//         System.out.println("   Reverse order: " + reverseMap.keySet());

//         // Time complexity demonstration
//         TreeMap<Integer, String> largeTree = new TreeMap<>();
//         for (int i = 0; i < 100000; i++) {
//             largeTree.put(i, "Value" + i);
//         }

//         long start = System.nanoTime();
//         largeTree.get(50000);  // O(log n) = O(log
//     }
// }