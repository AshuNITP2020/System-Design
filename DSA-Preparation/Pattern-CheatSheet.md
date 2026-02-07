# 🧠 DSA Pattern Cheat Sheet

> Quick reference for all patterns and templates in one place

---

## 📌 Pattern Recognition Guide

| If You See... | Think About... | Technique |
|---------------|----------------|-----------|
| "Subarray sum" | Prefix sum, sliding window | HashMap for prefix sums |
| "Substring" | Sliding window | Two pointers with HashMap |
| "K elements" | Heap | Min-heap for k largest |
| "Sorted array" | Binary search, two pointers | Start from both ends |
| "All combinations/permutations" | Backtracking | Recursion with choices |
| "Tree traversal" | DFS/BFS | Recursion or Queue |
| "Shortest path (unweighted)" | BFS | Level-by-level |
| "Shortest path (weighted)" | Dijkstra | Priority Queue |
| "Connected components" | Union Find, DFS | Pick based on dynamism |
| "Dependencies/Prerequisites" | Topological Sort | Kahn's Algorithm |
| "Optimal substructure" | Dynamic Programming | Define states, transitions |
| "Palindrome" | Two pointers, DP | Expand from center |
| "Next greater/smaller" | Monotonic Stack | Process in reverse |
| "Prefix search" | Trie | Build prefix tree |
| "Intervals" | Sort + Merge | Sort by start/end |
| "Meeting rooms" | Heap or Line Sweep | Track overlaps |

---

## 🔧 Code Templates

### 1. Two Pointers (Opposite Direction)

```python
def two_pointer(arr, target):
    left, right = 0, len(arr) - 1
    while left < right:
        current_sum = arr[left] + arr[right]
        if current_sum == target:
            return [left, right]
        elif current_sum < target:
            left += 1
        else:
            right -= 1
    return []
```

### 2. Sliding Window (Variable Size)

```python
def sliding_window(s):
    left = 0
    window = {}  # or set
    result = 0
    
    for right in range(len(s)):
        # Expand: add s[right] to window
        window[s[right]] = window.get(s[right], 0) + 1
        
        # Shrink: while window is invalid
        while window_invalid():
            window[s[left]] -= 1
            if window[s[left]] == 0:
                del window[s[left]]
            left += 1
        
        # Update result
        result = max(result, right - left + 1)
    
    return result
```

### 3. Binary Search (Standard)

```python
def binary_search(arr, target):
    left, right = 0, len(arr) - 1
    
    while left <= right:
        mid = left + (right - left) // 2
        if arr[mid] == target:
            return mid
        elif arr[mid] < target:
            left = mid + 1
        else:
            right = mid - 1
    
    return -1  # or left for insertion point
```

### 4. Binary Search on Answer

```python
def binary_search_answer(arr, target):
    left, right = min_possible, max_possible
    
    while left < right:
        mid = left + (right - left) // 2
        if can_achieve(mid):  # feasibility check
            right = mid  # for minimum
            # left = mid + 1  # for maximum
        else:
            left = mid + 1  # for minimum
            # right = mid  # for maximum
    
    return left
```

### 5. BFS (Breadth-First Search)

```python
from collections import deque

def bfs(graph, start):
    visited = {start}
    queue = deque([start])
    level = 0
    
    while queue:
        size = len(queue)
        for _ in range(size):
            node = queue.popleft()
            # Process node
            for neighbor in graph[node]:
                if neighbor not in visited:
                    visited.add(neighbor)
                    queue.append(neighbor)
        level += 1
    
    return level
```

### 6. DFS (Depth-First Search)

```python
def dfs(graph, node, visited):
    visited.add(node)
    # Process node
    
    for neighbor in graph[node]:
        if neighbor not in visited:
            dfs(graph, neighbor, visited)

# Iterative version
def dfs_iterative(graph, start):
    visited = set()
    stack = [start]
    
    while stack:
        node = stack.pop()
        if node in visited:
            continue
        visited.add(node)
        # Process node
        for neighbor in graph[node]:
            stack.append(neighbor)
```

### 7. Topological Sort (Kahn's Algorithm)

```python
from collections import deque

def topological_sort(n, edges):
    graph = [[] for _ in range(n)]
    indegree = [0] * n
    
    for u, v in edges:
        graph[u].append(v)
        indegree[v] += 1
    
    queue = deque([i for i in range(n) if indegree[i] == 0])
    result = []
    
    while queue:
        node = queue.popleft()
        result.append(node)
        for neighbor in graph[node]:
            indegree[neighbor] -= 1
            if indegree[neighbor] == 0:
                queue.append(neighbor)
    
    return result if len(result) == n else []  # Empty = cycle exists
```

### 8. Union Find

```python
class UnionFind:
    def __init__(self, n):
        self.parent = list(range(n))
        self.rank = [0] * n
        self.components = n
    
    def find(self, x):
        if self.parent[x] != x:
            self.parent[x] = self.find(self.parent[x])  # Path compression
        return self.parent[x]
    
    def union(self, x, y):
        px, py = self.find(x), self.find(y)
        if px == py:
            return False
        # Union by rank
        if self.rank[px] < self.rank[py]:
            px, py = py, px
        self.parent[py] = px
        if self.rank[px] == self.rank[py]:
            self.rank[px] += 1
        self.components -= 1
        return True
```

### 9. Dijkstra's Algorithm

```python
import heapq

def dijkstra(graph, start, n):
    dist = [float('inf')] * n
    dist[start] = 0
    heap = [(0, start)]
    
    while heap:
        d, u = heapq.heappop(heap)
        if d > dist[u]:
            continue
        for v, weight in graph[u]:
            if dist[u] + weight < dist[v]:
                dist[v] = dist[u] + weight
                heapq.heappush(heap, (dist[v], v))
    
    return dist
```

### 10. Backtracking Template

```python
def backtrack(path, choices):
    # Base case
    if is_solution(path):
        result.append(path[:])
        return
    
    for i, choice in enumerate(choices):
        if not is_valid(choice):
            continue
        
        # Make choice
        path.append(choice)
        
        # Recurse (might need to modify choices)
        backtrack(path, get_next_choices(i, choices))
        
        # Undo choice
        path.pop()

# Usage
result = []
backtrack([], initial_choices)
```

### 11. Dynamic Programming Templates

#### 1D DP (Linear)
```python
def dp_1d(n):
    dp = [0] * (n + 1)
    dp[0] = base_case
    
    for i in range(1, n + 1):
        dp[i] = transition(dp[i-1], dp[i-2], ...)
    
    return dp[n]
```

#### 2D DP (Grid/String)
```python
def dp_2d(s1, s2):
    m, n = len(s1), len(s2)
    dp = [[0] * (n + 1) for _ in range(m + 1)]
    
    # Base cases
    for i in range(m + 1):
        dp[i][0] = base_case_row
    for j in range(n + 1):
        dp[0][j] = base_case_col
    
    # Fill DP table
    for i in range(1, m + 1):
        for j in range(1, n + 1):
            if condition:
                dp[i][j] = dp[i-1][j-1] + 1
            else:
                dp[i][j] = max(dp[i-1][j], dp[i][j-1])
    
    return dp[m][n]
```

#### 0/1 Knapsack
```python
def knapsack(weights, values, capacity):
    n = len(weights)
    dp = [0] * (capacity + 1)
    
    for i in range(n):
        for w in range(capacity, weights[i] - 1, -1):  # Reverse!
            dp[w] = max(dp[w], dp[w - weights[i]] + values[i])
    
    return dp[capacity]
```

### 12. Monotonic Stack

```python
def next_greater(nums):
    n = len(nums)
    result = [-1] * n
    stack = []  # Stores indices
    
    for i in range(n):
        while stack and nums[i] > nums[stack[-1]]:
            idx = stack.pop()
            result[idx] = nums[i]
        stack.append(i)
    
    return result
```

### 13. Trie

```python
class TrieNode:
    def __init__(self):
        self.children = {}
        self.is_end = False

class Trie:
    def __init__(self):
        self.root = TrieNode()
    
    def insert(self, word):
        node = self.root
        for char in word:
            if char not in node.children:
                node.children[char] = TrieNode()
            node = node.children[char]
        node.is_end = True
    
    def search(self, word):
        node = self._find(word)
        return node is not None and node.is_end
    
    def starts_with(self, prefix):
        return self._find(prefix) is not None
    
    def _find(self, word):
        node = self.root
        for char in word:
            if char not in node.children:
                return None
            node = node.children[char]
        return node
```

### 14. Two Heaps (for Median)

```python
import heapq

class MedianFinder:
    def __init__(self):
        self.small = []  # Max heap (negate values)
        self.large = []  # Min heap
    
    def addNum(self, num):
        heapq.heappush(self.small, -num)
        
        # Balance: ensure small's max <= large's min
        if self.small and self.large and -self.small[0] > self.large[0]:
            heapq.heappush(self.large, -heapq.heappop(self.small))
        
        # Balance sizes (small can have at most 1 more)
        if len(self.small) > len(self.large) + 1:
            heapq.heappush(self.large, -heapq.heappop(self.small))
        if len(self.large) > len(self.small):
            heapq.heappush(self.small, -heapq.heappop(self.large))
    
    def findMedian(self):
        if len(self.small) > len(self.large):
            return -self.small[0]
        return (-self.small[0] + self.large[0]) / 2
```

### 15. Merge Intervals

```python
def merge_intervals(intervals):
    intervals.sort(key=lambda x: x[0])
    merged = []
    
    for interval in intervals:
        if not merged or merged[-1][1] < interval[0]:
            merged.append(interval)
        else:
            merged[-1][1] = max(merged[-1][1], interval[1])
    
    return merged
```

---

## ⚡ Complexity Quick Reference

### Time Complexities

| Operation | Array | LinkedList | HashMap | Heap | BST (balanced) |
|-----------|-------|------------|---------|------|----------------|
| Access    | O(1)  | O(n)       | O(1)    | -    | O(log n)       |
| Search    | O(n)  | O(n)       | O(1)*   | O(n) | O(log n)       |
| Insert    | O(n)  | O(1)       | O(1)*   | O(log n) | O(log n)   |
| Delete    | O(n)  | O(1)       | O(1)*   | O(log n) | O(log n)   |

*Amortized

### Common Algorithm Complexities

| Algorithm | Time | Space |
|-----------|------|-------|
| Binary Search | O(log n) | O(1) |
| BFS/DFS | O(V + E) | O(V) |
| Dijkstra | O((V + E) log V) | O(V) |
| Topological Sort | O(V + E) | O(V) |
| Union Find | O(α(n)) ≈ O(1) | O(n) |
| Quick Sort | O(n log n) avg | O(log n) |
| Merge Sort | O(n log n) | O(n) |
| Heap Operations | O(log n) | O(1) |

---

## 🎯 Problem-Solving Checklist

```
□ Read problem twice
□ Identify input/output format
□ Check constraints (size → complexity hint)
□ Draw examples
□ Identify pattern
□ Think of edge cases
□ Explain approach before coding
□ Write clean code
□ Test with examples
□ Analyze complexity
```

---

## 🔥 Edge Cases to Always Check

| Type | Edge Cases |
|------|------------|
| Array | Empty, single element, all same, sorted, reverse sorted |
| String | Empty, single char, all same chars, spaces |
| Tree | Null root, single node, skewed tree |
| Graph | Disconnected, self-loop, multiple edges |
| Number | Zero, negative, MIN_INT, MAX_INT, overflow |
| Linked List | Empty, single node, cycle |

---

*Print this out and keep handy during practice!*

