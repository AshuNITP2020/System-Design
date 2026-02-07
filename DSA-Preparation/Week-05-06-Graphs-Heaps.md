# Week 5-6: Graphs & Heaps

## Overview
- **Focus**: Graph BFS/DFS, Topological Sort, Union Find, Shortest Path, Heaps/Priority Queue
- **Total Problems**: 55
- **Easy**: 8 | **Medium**: 32 | **Hard**: 15

---

## Day 1-2: Graph BFS/DFS Basics (12 problems)

| # | Problem | Difficulty | Pattern | Status |
|---|---------|------------|---------|--------|
| 1 | [Number of Islands](https://leetcode.com/problems/number-of-islands/) | 🟡 Medium | DFS/BFS | ⬜ |
| 2 | [Clone Graph](https://leetcode.com/problems/clone-graph/) | 🟡 Medium | BFS/DFS + HashMap | ⬜ |
| 3 | [Max Area of Island](https://leetcode.com/problems/max-area-of-island/) | 🟡 Medium | DFS | ⬜ |
| 4 | [Pacific Atlantic Water Flow](https://leetcode.com/problems/pacific-atlantic-water-flow/) | 🟡 Medium | DFS from boundaries | ⬜ |
| 5 | [Surrounded Regions](https://leetcode.com/problems/surrounded-regions/) | 🟡 Medium | DFS from boundaries | ⬜ |
| 6 | [Rotting Oranges](https://leetcode.com/problems/rotting-oranges/) | 🟡 Medium | Multi-source BFS | ⬜ |
| 7 | [Walls and Gates](https://leetcode.com/problems/walls-and-gates/) | 🟡 Medium | Multi-source BFS | ⬜ |
| 8 | [Number of Closed Islands](https://leetcode.com/problems/number-of-closed-islands/) | 🟡 Medium | DFS | ⬜ |
| 9 | [Flood Fill](https://leetcode.com/problems/flood-fill/) | 🟢 Easy | DFS/BFS | ⬜ |
| 10 | [01 Matrix](https://leetcode.com/problems/01-matrix/) | 🟡 Medium | Multi-source BFS | ⬜ |
| 11 | [Word Ladder](https://leetcode.com/problems/word-ladder/) | 🔴 Hard | BFS | ⬜ |
| 12 | [Word Ladder II](https://leetcode.com/problems/word-ladder-ii/) | 🔴 Hard | BFS + Backtracking | ⬜ |

---

## Day 3-4: Topological Sort & Directed Graphs (10 problems)

| # | Problem | Difficulty | Pattern | Status |
|---|---------|------------|---------|--------|
| 13 | [Course Schedule](https://leetcode.com/problems/course-schedule/) | 🟡 Medium | Topological Sort (Cycle) | ⬜ |
| 14 | [Course Schedule II](https://leetcode.com/problems/course-schedule-ii/) | 🟡 Medium | Topological Sort (Order) | ⬜ |
| 15 | [Course Schedule IV](https://leetcode.com/problems/course-schedule-iv/) | 🟡 Medium | Floyd-Warshall / BFS | ⬜ |
| 16 | [Alien Dictionary](https://leetcode.com/problems/alien-dictionary/) | 🔴 Hard | Topological Sort | ⬜ |
| 17 | [Find Eventual Safe States](https://leetcode.com/problems/find-eventual-safe-states/) | 🟡 Medium | Reverse Graph + Topo | ⬜ |
| 18 | [Minimum Height Trees](https://leetcode.com/problems/minimum-height-trees/) | 🟡 Medium | Topological Sort | ⬜ |
| 19 | [Parallel Courses](https://leetcode.com/problems/parallel-courses/) | 🟡 Medium | Topological Sort | ⬜ |
| 20 | [All Ancestors of a Node in DAG](https://leetcode.com/problems/all-ancestors-of-a-node-in-a-directed-acyclic-graph/) | 🟡 Medium | Topological Sort | ⬜ |
| 21 | [Loud and Rich](https://leetcode.com/problems/loud-and-rich/) | 🟡 Medium | Topological Sort | ⬜ |
| 22 | [Sequence Reconstruction](https://leetcode.com/problems/sequence-reconstruction/) | 🟡 Medium | Topological Sort | ⬜ |

---

## Day 5-6: Union Find (10 problems)

| # | Problem | Difficulty | Pattern | Status |
|---|---------|------------|---------|--------|
| 23 | [Number of Connected Components in Undirected Graph](https://leetcode.com/problems/number-of-connected-components-in-an-undirected-graph/) | 🟡 Medium | Union Find | ⬜ |
| 24 | [Redundant Connection](https://leetcode.com/problems/redundant-connection/) | 🟡 Medium | Union Find | ⬜ |
| 25 | [Redundant Connection II](https://leetcode.com/problems/redundant-connection-ii/) | 🔴 Hard | Union Find | ⬜ |
| 26 | [Graph Valid Tree](https://leetcode.com/problems/graph-valid-tree/) | 🟡 Medium | Union Find | ⬜ |
| 27 | [Accounts Merge](https://leetcode.com/problems/accounts-merge/) | 🟡 Medium | Union Find + HashMap | ⬜ |
| 28 | [Longest Consecutive Sequence](https://leetcode.com/problems/longest-consecutive-sequence/) | 🟡 Medium | Union Find / Hash Set | ⬜ |
| 29 | [Most Stones Removed with Same Row or Column](https://leetcode.com/problems/most-stones-removed-with-same-row-or-column/) | 🟡 Medium | Union Find | ⬜ |
| 30 | [Satisfiability of Equality Equations](https://leetcode.com/problems/satisfiability-of-equality-equations/) | 🟡 Medium | Union Find | ⬜ |
| 31 | [Number of Operations to Make Network Connected](https://leetcode.com/problems/number-of-operations-to-make-network-connected/) | 🟡 Medium | Union Find | ⬜ |
| 32 | [Swim in Rising Water](https://leetcode.com/problems/swim-in-rising-water/) | 🔴 Hard | Union Find + Binary Search | ⬜ |

---

## Day 7-8: Shortest Path Algorithms (10 problems)

| # | Problem | Difficulty | Pattern | Status |
|---|---------|------------|---------|--------|
| 33 | [Network Delay Time](https://leetcode.com/problems/network-delay-time/) | 🟡 Medium | Dijkstra | ⬜ |
| 34 | [Cheapest Flights Within K Stops](https://leetcode.com/problems/cheapest-flights-within-k-stops/) | 🟡 Medium | Bellman-Ford / BFS | ⬜ |
| 35 | [Path with Maximum Probability](https://leetcode.com/problems/path-with-maximum-probability/) | 🟡 Medium | Dijkstra (Modified) | ⬜ |
| 36 | [Find the City With Smallest Number of Neighbors](https://leetcode.com/problems/find-the-city-with-the-smallest-number-of-neighbors-at-a-threshold-distance/) | 🟡 Medium | Floyd-Warshall | ⬜ |
| 37 | [Path With Minimum Effort](https://leetcode.com/problems/path-with-minimum-effort/) | 🟡 Medium | Dijkstra / Binary Search | ⬜ |
| 38 | [Shortest Path in Binary Matrix](https://leetcode.com/problems/shortest-path-in-binary-matrix/) | 🟡 Medium | BFS | ⬜ |
| 39 | [Shortest Path to Get All Keys](https://leetcode.com/problems/shortest-path-to-get-all-keys/) | 🔴 Hard | BFS + Bitmask | ⬜ |
| 40 | [Minimum Cost to Make at Least One Valid Path](https://leetcode.com/problems/minimum-cost-to-make-at-least-one-valid-path-in-a-grid/) | 🔴 Hard | 0-1 BFS | ⬜ |
| 41 | [Shortest Path Visiting All Nodes](https://leetcode.com/problems/shortest-path-visiting-all-nodes/) | 🔴 Hard | BFS + Bitmask | ⬜ |
| 42 | [Reachable Nodes In Subdivided Graph](https://leetcode.com/problems/reachable-nodes-in-subdivided-graph/) | 🔴 Hard | Dijkstra | ⬜ |

---

## Day 9-10: Heaps/Priority Queue (13 problems)

| # | Problem | Difficulty | Pattern | Status |
|---|---------|------------|---------|--------|
| 43 | [Kth Largest Element in an Array](https://leetcode.com/problems/kth-largest-element-in-an-array/) | 🟡 Medium | Min Heap / QuickSelect | ⬜ |
| 44 | [Top K Frequent Elements](https://leetcode.com/problems/top-k-frequent-elements/) | 🟡 Medium | Heap / Bucket Sort | ⬜ |
| 45 | [Find K Pairs with Smallest Sums](https://leetcode.com/problems/find-k-pairs-with-smallest-sums/) | 🟡 Medium | Min Heap | ⬜ |
| 46 | [K Closest Points to Origin](https://leetcode.com/problems/k-closest-points-to-origin/) | 🟡 Medium | Max Heap | ⬜ |
| 47 | [Find Median from Data Stream](https://leetcode.com/problems/find-median-from-data-stream/) | 🔴 Hard | Two Heaps | ⬜ |
| 48 | [Sliding Window Median](https://leetcode.com/problems/sliding-window-median/) | 🔴 Hard | Two Heaps | ⬜ |
| 49 | [Task Scheduler](https://leetcode.com/problems/task-scheduler/) | 🟡 Medium | Heap + Greedy | ⬜ |
| 50 | [Reorganize String](https://leetcode.com/problems/reorganize-string/) | 🟡 Medium | Heap + Greedy | ⬜ |
| 51 | [Last Stone Weight](https://leetcode.com/problems/last-stone-weight/) | 🟢 Easy | Max Heap | ⬜ |
| 52 | [Kth Smallest Element in a Sorted Matrix](https://leetcode.com/problems/kth-smallest-element-in-a-sorted-matrix/) | 🟡 Medium | Min Heap | ⬜ |
| 53 | [Ugly Number II](https://leetcode.com/problems/ugly-number-ii/) | 🟡 Medium | Heap / DP | ⬜ |
| 54 | [IPO](https://leetcode.com/problems/ipo/) | 🔴 Hard | Two Heaps + Greedy | ⬜ |
| 55 | [Smallest Range Covering Elements from K Lists](https://leetcode.com/problems/smallest-range-covering-elements-from-k-lists/) | 🔴 Hard | Min Heap | ⬜ |

---

## Summary - Week 5-6

| Topic | Easy | Medium | Hard | Total |
|-------|------|--------|------|-------|
| Graph BFS/DFS | 1 | 9 | 2 | 12 |
| Topological Sort | 0 | 9 | 1 | 10 |
| Union Find | 0 | 8 | 2 | 10 |
| Shortest Path | 0 | 6 | 4 | 10 |
| Heaps/Priority Queue | 1 | 9 | 3 | 13 |
| **Total** | **2** | **41** | **12** | **55** |

---

## Key Patterns to Remember

### Graph BFS/DFS
```python
# BFS Template
def bfs(graph, start):
    visited = set([start])
    queue = deque([start])
    while queue:
        node = queue.popleft()
        for neighbor in graph[node]:
            if neighbor not in visited:
                visited.add(neighbor)
                queue.append(neighbor)

# DFS Template
def dfs(graph, node, visited):
    visited.add(node)
    for neighbor in graph[node]:
        if neighbor not in visited:
            dfs(graph, neighbor, visited)
```

### Topological Sort
```python
# Kahn's Algorithm (BFS)
def topological_sort(graph, n):
    indegree = [0] * n
    for node in graph:
        for neighbor in graph[node]:
            indegree[neighbor] += 1
    
    queue = deque([i for i in range(n) if indegree[i] == 0])
    result = []
    
    while queue:
        node = queue.popleft()
        result.append(node)
        for neighbor in graph[node]:
            indegree[neighbor] -= 1
            if indegree[neighbor] == 0:
                queue.append(neighbor)
    
    return result if len(result) == n else []  # Empty if cycle exists
```

### Union Find
```python
class UnionFind:
    def __init__(self, n):
        self.parent = list(range(n))
        self.rank = [0] * n
    
    def find(self, x):
        if self.parent[x] != x:
            self.parent[x] = self.find(self.parent[x])  # Path compression
        return self.parent[x]
    
    def union(self, x, y):
        px, py = self.find(x), self.find(y)
        if px == py:
            return False  # Already connected
        if self.rank[px] < self.rank[py]:
            px, py = py, px
        self.parent[py] = px
        if self.rank[px] == self.rank[py]:
            self.rank[px] += 1
        return True
```

### Dijkstra's Algorithm
```python
def dijkstra(graph, start):
    dist = {node: float('inf') for node in graph}
    dist[start] = 0
    heap = [(0, start)]
    
    while heap:
        d, node = heapq.heappop(heap)
        if d > dist[node]:
            continue
        for neighbor, weight in graph[node]:
            new_dist = dist[node] + weight
            if new_dist < dist[neighbor]:
                dist[neighbor] = new_dist
                heapq.heappush(heap, (new_dist, neighbor))
    
    return dist
```

### Two Heaps Pattern (for Median)
```python
class MedianFinder:
    def __init__(self):
        self.small = []  # Max heap (use negative values)
        self.large = []  # Min heap
    
    def addNum(self, num):
        heapq.heappush(self.small, -num)
        heapq.heappush(self.large, -heapq.heappop(self.small))
        
        if len(self.large) > len(self.small):
            heapq.heappush(self.small, -heapq.heappop(self.large))
    
    def findMedian(self):
        if len(self.small) > len(self.large):
            return -self.small[0]
        return (-self.small[0] + self.large[0]) / 2
```

---

*Progress: ___ / 55 completed*

