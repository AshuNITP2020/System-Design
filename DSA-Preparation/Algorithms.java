public class Algorithms {
    // ============================================================
// 1. TWO POINTERS (Opposite Direction)
// Use: Sorted arrays, finding pairs, palindromes
// ============================================================
public int[] twoSum(int[] arr, int target) {
    int left = 0, right = arr.length - 1;
    while (left < right) {
        int sum = arr[left] + arr[right];
        if (sum == target) {
            return new int[]{left, right};
        } else if (sum < target) {
            left++;
        } else {
            right--;
        }
    }
    return new int[]{-1, -1};
}

// ============================================================
// 2. SLIDING WINDOW (Variable Size)
// Use: Subarray/substring with condition
// ============================================================
public int slidingWindow(String s) {
    Map<Character, Integer> window = new HashMap<>();
    int left = 0, result = 0;
    
    for (int right = 0; right < s.length(); right++) {
        // Expand: add s[right] to window
        char c = s.charAt(right);
        window.put(c, window.getOrDefault(c, 0) + 1);
        
        // Shrink: while window is invalid
        while (windowInvalid(window)) {
            char leftChar = s.charAt(left);
            window.put(leftChar, window.get(leftChar) - 1);
            if (window.get(leftChar) == 0) {
                window.remove(leftChar);
            }
            left++;
        }
        
        // Update result
        result = Math.max(result, right - left + 1);
    }
    return result;
}

// ============================================================
// 3. BINARY SEARCH (Standard)
// Use: Finding element in sorted array
// ============================================================
public int binarySearch(int[] arr, int target) {
    int left = 0, right = arr.length - 1;
    
    while (left <= right) {
        int mid = left + (right - left) / 2;
        if (arr[mid] == target) {
            return mid;
        } else if (arr[mid] < target) {
            left = mid + 1;
        } else {
            right = mid - 1;
        }
    }
    return -1; // or return left for insertion point
}

// ============================================================
// 4. BINARY SEARCH ON ANSWER
// Use: Minimize/maximize answer problems (Koko eating bananas)
// ============================================================
public int binarySearchOnAnswer(int[] arr) {
    int left = minPossible, right = maxPossible;
    
    while (left < right) {
        int mid = left + (right - left) / 2;
        if (canAchieve(mid)) { // feasibility check
            right = mid;       // for finding minimum
            // left = mid + 1; // for finding maximum
        } else {
            left = mid + 1;    // for finding minimum
            // right = mid;    // for finding maximum
        }
    }
    return left;
}

// ============================================================
// 5. BFS (Breadth-First Search)
// Use: Shortest path (unweighted), level-order traversal
// ============================================================
public int bfs(Map<Integer, List<Integer>> graph, int start) {
    Set<Integer> visited = new HashSet<>();
    Queue<Integer> queue = new LinkedList<>();
    
    visited.add(start);
    queue.offer(start);
    int level = 0;
    
    while (!queue.isEmpty()) {
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            int node = queue.poll();
            // Process node here
            
            for (int neighbor : graph.getOrDefault(node, new ArrayList<>())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }
        level++;
    }
    return level;
}

// BFS for Grid (Number of Islands pattern)
public void bfsGrid(char[][] grid, int row, int col) {
    int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
    Queue<int[]> queue = new LinkedList<>();
    queue.offer(new int[]{row, col});
    grid[row][col] = '0'; // Mark visited
    
    while (!queue.isEmpty()) {
        int[] curr = queue.poll();
        for (int[] dir : directions) {
            int newRow = curr[0] + dir[0];
            int newCol = curr[1] + dir[1];
            if (newRow >= 0 && newRow < grid.length && 
                newCol >= 0 && newCol < grid[0].length && 
                grid[newRow][newCol] == '1') {
                grid[newRow][newCol] = '0';
                queue.offer(new int[]{newRow, newCol});
            }
        }
    }
}

// ============================================================
// 6. DFS (Depth-First Search)
// Use: All paths, connected components, tree traversals
// ============================================================
// Recursive
public void dfs(Map<Integer, List<Integer>> graph, int node, Set<Integer> visited) {
    visited.add(node);
    // Process node here
    
    for (int neighbor : graph.getOrDefault(node, new ArrayList<>())) {
        if (!visited.contains(neighbor)) {
            dfs(graph, neighbor, visited);
        }
    }
}

// Iterative
public void dfsIterative(Map<Integer, List<Integer>> graph, int start) {
    Set<Integer> visited = new HashSet<>();
    Stack<Integer> stack = new Stack<>();
    stack.push(start);
    
    while (!stack.isEmpty()) {
        int node = stack.pop();
        if (visited.contains(node)) continue;
        visited.add(node);
        // Process node here
        
        for (int neighbor : graph.getOrDefault(node, new ArrayList<>())) {
            if (!visited.contains(neighbor)) {
                stack.push(neighbor);
            }
        }
    }
}

// DFS for Grid
public void dfsGrid(char[][] grid, int row, int col) {
    if (row < 0 || row >= grid.length || col < 0 || col >= grid[0].length || grid[row][col] == '0') {
        return;
    }
    grid[row][col] = '0'; // Mark visited
    dfsGrid(grid, row + 1, col);
    dfsGrid(grid, row - 1, col);
    dfsGrid(grid, row, col + 1);
    dfsGrid(grid, row, col - 1);
}

// ============================================================
// 7. TOPOLOGICAL SORT (Kahn's Algorithm - BFS)
// Use: Prerequisites, dependencies, course schedule
// ============================================================
public int[] topologicalSort(int n, int[][] edges) {
    List<List<Integer>> graph = new ArrayList<>();
    int[] indegree = new int[n];
    
    for (int i = 0; i < n; i++) {
        graph.add(new ArrayList<>());
    }
    
    for (int[] edge : edges) {
        graph.get(edge[0]).add(edge[1]);
        indegree[edge[1]]++;
    }
    
    Queue<Integer> queue = new LinkedList<>();
    for (int i = 0; i < n; i++) {
        if (indegree[i] == 0) {
            queue.offer(i);
        }
    }
    
    int[] result = new int[n];
    int index = 0;
    
    while (!queue.isEmpty()) {
        int node = queue.poll();
        result[index++] = node;
        
        for (int neighbor : graph.get(node)) {
            indegree[neighbor]--;
            if (indegree[neighbor] == 0) {
                queue.offer(neighbor);
            }
        }
    }
    
    return index == n ? result : new int[0]; // Empty if cycle exists
}

// ============================================================
// 8. UNION FIND (Disjoint Set Union)
// Use: Connected components, cycle detection, grouping
// ============================================================
class UnionFind {
    private int[] parent;
    private int[] rank;
    private int components;
    
    public UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        components = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
    }
    
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // Path compression
        }
        return parent[x];
    }
    
    public boolean union(int x, int y) {
        int px = find(x), py = find(y);
        if (px == py) return false;
        
        // Union by rank
        if (rank[px] < rank[py]) {
            int temp = px; px = py; py = temp;
        }
        parent[py] = px;
        if (rank[px] == rank[py]) {
            rank[px]++;
        }
        components--;
        return true;
    }
    
    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }
    
    public int getComponents() {
        return components;
    }
}

// ============================================================
// 9. DIJKSTRA'S ALGORITHM
// Use: Shortest path in weighted graph (non-negative weights)
// ============================================================
public int[] dijkstra(List<List<int[]>> graph, int start, int n) {
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[start] = 0;
    
    // {distance, node}
    PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
    pq.offer(new int[]{0, start});
    
    while (!pq.isEmpty()) {
        int[] curr = pq.poll();
        int d = curr[0], u = curr[1];
        
        if (d > dist[u]) continue; // Skip outdated entries
        
        for (int[] edge : graph.get(u)) {
            int v = edge[0], weight = edge[1];
            if (dist[u] + weight < dist[v]) {
                dist[v] = dist[u] + weight;
                pq.offer(new int[]{dist[v], v});
            }
        }
    }
    
    return dist;
}

// ============================================================
// 10. BACKTRACKING TEMPLATE
// Use: Permutations, combinations, subsets, N-Queens
// ============================================================
public List<List<Integer>> backtrack(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    backtrackHelper(nums, new ArrayList<>(), result, new boolean[nums.length]);
    return result;
}

private void backtrackHelper(int[] nums, List<Integer> path, 
                             List<List<Integer>> result, boolean[] used) {
    // Base case
    if (path.size() == nums.length) {
        result.add(new ArrayList<>(path));
        return;
    }
    
    for (int i = 0; i < nums.length; i++) {
        if (used[i]) continue;
        
        // Make choice
        path.add(nums[i]);
        used[i] = true;
        
        // Recurse
        backtrackHelper(nums, path, result, used);
        
        // Undo choice (backtrack)
        path.remove(path.size() - 1);
        used[i] = false;
    }
}

// Subsets Pattern
public List<List<Integer>> subsets(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    subsetsHelper(nums, 0, new ArrayList<>(), result);
    return result;
}

private void subsetsHelper(int[] nums, int start, List<Integer> path, 
                           List<List<Integer>> result) {
    result.add(new ArrayList<>(path));
    
    for (int i = start; i < nums.length; i++) {
        path.add(nums[i]);
        subsetsHelper(nums, i + 1, path, result);
        path.remove(path.size() - 1);
    }
}

// Combination Sum Pattern
public List<List<Integer>> combinationSum(int[] candidates, int target) {
    List<List<Integer>> result = new ArrayList<>();
    combinationHelper(candidates, target, 0, new ArrayList<>(), result);
    return result;
}

private void combinationHelper(int[] candidates, int remain, int start,
                               List<Integer> path, List<List<Integer>> result) {
    if (remain == 0) {
        result.add(new ArrayList<>(path));
        return;
    }
    if (remain < 0) return;
    
    for (int i = start; i < candidates.length; i++) {
        path.add(candidates[i]);
        combinationHelper(candidates, remain - candidates[i], i, path, result);
        path.remove(path.size() - 1);
    }
}

// ============================================================
// 11. DYNAMIC PROGRAMMING TEMPLATES
// ============================================================

// 1D DP - Fibonacci/House Robber Style
public int dp1D(int n) {
    if (n <= 1) return n;
    int[] dp = new int[n + 1];
    dp[0] = 0;
    dp[1] = 1;
    
    for (int i = 2; i <= n; i++) {
        dp[i] = dp[i - 1] + dp[i - 2];
    }
    return dp[n];
}

// Space Optimized 1D DP
public int dp1DOptimized(int n) {
    if (n <= 1) return n;
    int prev2 = 0, prev1 = 1;
    
    for (int i = 2; i <= n; i++) {
        int curr = prev1 + prev2;
        prev2 = prev1;
        prev1 = curr;
    }
    return prev1;
}

// 2D DP - LCS Pattern
public int longestCommonSubsequence(String s1, String s2) {
    int m = s1.length(), n = s2.length();
    int[][] dp = new int[m + 1][n + 1];
    
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                dp[i][j] = dp[i - 1][j - 1] + 1;
            } else {
                dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
            }
        }
    }
    return dp[m][n];
}

// Grid DP - Unique Paths Pattern
public int uniquePaths(int m, int n) {
    int[][] dp = new int[m][n];
    
    // Base case: first row and column
    for (int i = 0; i < m; i++) dp[i][0] = 1;
    for (int j = 0; j < n; j++) dp[0][j] = 1;
    
    for (int i = 1; i < m; i++) {
        for (int j = 1; j < n; j++) {
            dp[i][j] = dp[i - 1][j] + dp[i][j - 1];
        }
    }
    return dp[m - 1][n - 1];
}

// 0/1 Knapsack
public int knapsack01(int[] weights, int[] values, int capacity) {
    int n = weights.length;
    int[][] dp = new int[n + 1][capacity + 1];
    
    for (int i = 1; i <= n; i++) {
        for (int w = 0; w <= capacity; w++) {
            if (weights[i - 1] <= w) {
                dp[i][w] = Math.max(dp[i - 1][w], 
                                    dp[i - 1][w - weights[i - 1]] + values[i - 1]);
            } else {
                dp[i][w] = dp[i - 1][w];
            }
        }
    }
    return dp[n][capacity];
}

// 0/1 Knapsack Space Optimized
public int knapsack01Optimized(int[] weights, int[] values, int capacity) {
    int n = weights.length;
    int[] dp = new int[capacity + 1];
    
    for (int i = 0; i < n; i++) {
        for (int w = capacity; w >= weights[i]; w--) { // Reverse order!
            dp[w] = Math.max(dp[w], dp[w - weights[i]] + values[i]);
        }
    }
    return dp[capacity];
}

// Unbounded Knapsack - Coin Change
public int coinChange(int[] coins, int amount) {
    int[] dp = new int[amount + 1];
    Arrays.fill(dp, amount + 1);
    dp[0] = 0;
    
    for (int coin : coins) {
        for (int x = coin; x <= amount; x++) {
            dp[x] = Math.min(dp[x], dp[x - coin] + 1);
        }
    }
    return dp[amount] > amount ? -1 : dp[amount];
}

// Longest Increasing Subsequence
public int lengthOfLIS(int[] nums) {
    int n = nums.length;
    int[] dp = new int[n];
    Arrays.fill(dp, 1);
    int maxLen = 1;
    
    for (int i = 1; i < n; i++) {
        for (int j = 0; j < i; j++) {
            if (nums[i] > nums[j]) {
                dp[i] = Math.max(dp[i], dp[j] + 1);
            }
        }
        maxLen = Math.max(maxLen, dp[i]);
    }
    return maxLen;
}

// LIS with Binary Search O(n log n)
public int lengthOfLISOptimized(int[] nums) {
    List<Integer> tails = new ArrayList<>();
    
    for (int num : nums) {
        int pos = Collections.binarySearch(tails, num);
        if (pos < 0) pos = -(pos + 1);
        
        if (pos == tails.size()) {
            tails.add(num);
        } else {
            tails.set(pos, num);
        }
    }
    return tails.size();
}

// ============================================================
// 12. MONOTONIC STACK
// Use: Next greater/smaller element, histogram
// ============================================================
public int[] nextGreaterElement(int[] nums) {
    int n = nums.length;
    int[] result = new int[n];
    Arrays.fill(result, -1);
    Stack<Integer> stack = new Stack<>(); // Stores indices
    
    for (int i = 0; i < n; i++) {
        while (!stack.isEmpty() && nums[i] > nums[stack.peek()]) {
            result[stack.pop()] = nums[i];
        }
        stack.push(i);
    }
    return result;
}

// Daily Temperatures Pattern
public int[] dailyTemperatures(int[] temps) {
    int n = temps.length;
    int[] result = new int[n];
    Stack<Integer> stack = new Stack<>();
    
    for (int i = 0; i < n; i++) {
        while (!stack.isEmpty() && temps[i] > temps[stack.peek()]) {
            int idx = stack.pop();
            result[idx] = i - idx;
        }
        stack.push(i);
    }
    return result;
}

// ============================================================
// 13. TRIE (Prefix Tree)
// Use: Autocomplete, prefix search, word dictionary
// ============================================================
class TrieNode {
    TrieNode[] children = new TrieNode[26];
    boolean isEnd = false;
}

class Trie {
    private TrieNode root;
    
    public Trie() {
        root = new TrieNode();
    }
    
    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) {
                node.children[idx] = new TrieNode();
            }
            node = node.children[idx];
        }
        node.isEnd = true;
    }
    
    public boolean search(String word) {
        TrieNode node = find(word);
        return node != null && node.isEnd;
    }
    
    public boolean startsWith(String prefix) {
        return find(prefix) != null;
    }
    
    private TrieNode find(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) {
                return null;
            }
            node = node.children[idx];
        }
        return node;
    }
}

// ============================================================
// 14. TWO HEAPS (for Median)
// Use: Running median, scheduling
// ============================================================
class MedianFinder {
    private PriorityQueue<Integer> small; // Max heap
    private PriorityQueue<Integer> large; // Min heap
    
    public MedianFinder() {
        small = new PriorityQueue<>(Collections.reverseOrder());
        large = new PriorityQueue<>();
    }
    
    public void addNum(int num) {
        small.offer(num);
        
        // Balance: ensure small's max <= large's min
        if (!small.isEmpty() && !large.isEmpty() && small.peek() > large.peek()) {
            large.offer(small.poll());
        }
        
        // Balance sizes
        if (small.size() > large.size() + 1) {
            large.offer(small.poll());
        }
        if (large.size() > small.size()) {
            small.offer(large.poll());
        }
    }
    
    public double findMedian() {
        if (small.size() > large.size()) {
            return small.peek();
        }
        return (small.peek() + large.peek()) / 2.0;
    }
}

// ============================================================
// 15. MERGE INTERVALS
// Use: Overlapping intervals, meeting rooms
// ============================================================
public int[][] mergeIntervals(int[][] intervals) {
    if (intervals.length <= 1) return intervals;
    
    Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
    List<int[]> merged = new ArrayList<>();
    
    for (int[] interval : intervals) {
        if (merged.isEmpty() || merged.get(merged.size() - 1)[1] < interval[0]) {
            merged.add(interval);
        } else {
            merged.get(merged.size() - 1)[1] = 
                Math.max(merged.get(merged.size() - 1)[1], interval[1]);
        }
    }
    
    return merged.toArray(new int[merged.size()][]);
}

// ============================================================
// 16. TREE TRAVERSALS
// ============================================================
// Inorder (Left -> Root -> Right) - BST gives sorted order
public void inorder(TreeNode root, List<Integer> result) {
    if (root == null) return;
    inorder(root.left, result);
    result.add(root.val);
    inorder(root.right, result);
}

// Preorder (Root -> Left -> Right) - Good for copying trees
public void preorder(TreeNode root, List<Integer> result) {
    if (root == null) return;
    result.add(root.val);
    preorder(root.left, result);
    preorder(root.right, result);
}

// Postorder (Left -> Right -> Root) - Good for deletion
public void postorder(TreeNode root, List<Integer> result) {
    if (root == null) return;
    postorder(root.left, result);
    postorder(root.right, result);
    result.add(root.val);
}

// Level Order (BFS)
public List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;
    
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    
    while (!queue.isEmpty()) {
        int size = queue.size();
        List<Integer> level = new ArrayList<>();
        
        for (int i = 0; i < size; i++) {
            TreeNode node = queue.poll();
            level.add(node.val);
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        result.add(level);
    }
    return result;
}

// ============================================================
// 17. FAST & SLOW POINTERS (Floyd's Algorithm)
// Use: Cycle detection, middle of linked list
// ============================================================
// Detect Cycle
public boolean hasCycle(ListNode head) {
    if (head == null) return false;
    ListNode slow = head, fast = head;
    
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) return true;
    }
    return false;
}

// Find Cycle Start
public ListNode detectCycle(ListNode head) {
    if (head == null) return null;
    ListNode slow = head, fast = head;
    
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) {
            slow = head;
            while (slow != fast) {
                slow = slow.next;
                fast = fast.next;
            }
            return slow;
        }
    }
    return null;
}

// Find Middle
public ListNode findMiddle(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }
    return slow;
}

// ============================================================
// 18. REVERSE LINKED LIST
// ============================================================
// Iterative
public ListNode reverseList(ListNode head) {
    ListNode prev = null, curr = head;
    
    while (curr != null) {
        ListNode next = curr.next;
        curr.next = prev;
        prev = curr;
        curr = next;
    }
    return prev;
}

// Recursive
public ListNode reverseListRecursive(ListNode head) {
    if (head == null || head.next == null) return head;
    
    ListNode newHead = reverseListRecursive(head.next);
    head.next.next = head;
    head.next = null;
    return newHead;
}

// ============================================================
// 19. PREFIX SUM
// Use: Range sum queries, subarray sum equals K
// ============================================================
public int subarraySum(int[] nums, int k) {
    Map<Integer, Integer> prefixCount = new HashMap<>();
    prefixCount.put(0, 1);
    
    int sum = 0, count = 0;
    for (int num : nums) {
        sum += num;
        count += prefixCount.getOrDefault(sum - k, 0);
        prefixCount.put(sum, prefixCount.getOrDefault(sum, 0) + 1);
    }
    return count;
}

// ============================================================
// 20. BIT MANIPULATION
// ============================================================
// Common Operations
// n & (n - 1)    -> Remove lowest set bit
// n & (-n)       -> Get lowest set bit
// n | (n + 1)    -> Set lowest unset bit
// n ^ n = 0      -> XOR same number is 0
// a ^ b ^ a = b  -> XOR cancels out

// Check if bit is set at position i
public boolean isBitSet(int n, int i) {
    return ((n >> i) & 1) == 1;
}

// Set bit at position i
public int setBit(int n, int i) {
    return n | (1 << i);
}

// Clear bit at position i
public int clearBit(int n, int i) {
    return n & ~(1 << i);
}

// Toggle bit at position i
public int toggleBit(int n, int i) {
    return n ^ (1 << i);
}

// Count set bits
public int countSetBits(int n) {
    int count = 0;
    while (n != 0) {
        n &= (n - 1); // Remove lowest set bit
        count++;
    }
    return count;
}

// Single Number (find element appearing once, others twice)
public int singleNumber(int[] nums) {
    int result = 0;
    for (int num : nums) {
        result ^= num;
    }
    return result;
}
}
