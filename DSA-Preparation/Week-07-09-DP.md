# Week 7-9: Dynamic Programming

## Overview
- **Focus**: 1D DP, 2D DP, String DP, Knapsack, Interval DP, State Machine DP
- **Total Problems**: 80
- **Easy**: 10 | **Medium**: 48 | **Hard**: 22

---

## Week 7: 1D Dynamic Programming

### Day 1-2: Fibonacci Style & Linear DP (14 problems)

| # | Problem | Difficulty | Pattern | Status |
|---|---------|------------|---------|--------|
| 1 | [Climbing Stairs](https://leetcode.com/problems/climbing-stairs/) | 🟢 Easy | Fibonacci | ⬜ |
| 2 | [Min Cost Climbing Stairs](https://leetcode.com/problems/min-cost-climbing-stairs/) | 🟢 Easy | Fibonacci | ⬜ |
| 3 | [House Robber](https://leetcode.com/problems/house-robber/) | 🟡 Medium | Linear DP | ⬜ |
| 4 | [House Robber II](https://leetcode.com/problems/house-robber-ii/) | 🟡 Medium | Circular DP | ⬜ |
| 5 | [Delete and Earn](https://leetcode.com/problems/delete-and-earn/) | 🟡 Medium | House Robber Variant | ⬜ |
| 6 | [Decode Ways](https://leetcode.com/problems/decode-ways/) | 🟡 Medium | Fibonacci Variant | ⬜ |
| 7 | [N-th Tribonacci Number](https://leetcode.com/problems/n-th-tribonacci-number/) | 🟢 Easy | Fibonacci | ⬜ |
| 8 | [Longest Increasing Subsequence](https://leetcode.com/problems/longest-increasing-subsequence/) | 🟡 Medium | LIS | ⬜ |
| 9 | [Number of Longest Increasing Subsequence](https://leetcode.com/problems/number-of-longest-increasing-subsequence/) | 🟡 Medium | LIS Variant | ⬜ |
| 10 | [Russian Doll Envelopes](https://leetcode.com/problems/russian-doll-envelopes/) | 🔴 Hard | LIS + Binary Search | ⬜ |
| 11 | [Maximum Subarray](https://leetcode.com/problems/maximum-subarray/) | 🟡 Medium | Kadane's | ⬜ |
| 12 | [Maximum Product Subarray](https://leetcode.com/problems/maximum-product-subarray/) | 🟡 Medium | Kadane's Variant | ⬜ |
| 13 | [Word Break](https://leetcode.com/problems/word-break/) | 🟡 Medium | Linear DP | ⬜ |
| 14 | [Word Break II](https://leetcode.com/problems/word-break-ii/) | 🔴 Hard | DP + Backtracking | ⬜ |

### Day 3-4: Decision Making DP (12 problems)

| # | Problem | Difficulty | Pattern | Status |
|---|---------|------------|---------|--------|
| 15 | [Best Time to Buy and Sell Stock](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/) | 🟢 Easy | Single Transaction | ⬜ |
| 16 | [Best Time to Buy and Sell Stock II](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-ii/) | 🟡 Medium | Unlimited Transactions | ⬜ |
| 17 | [Best Time to Buy and Sell Stock III](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iii/) | 🔴 Hard | Two Transactions | ⬜ |
| 18 | [Best Time to Buy and Sell Stock IV](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iv/) | 🔴 Hard | K Transactions | ⬜ |
| 19 | [Best Time to Buy and Sell Stock with Cooldown](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-cooldown/) | 🟡 Medium | State Machine | ⬜ |
| 20 | [Best Time to Buy and Sell Stock with Transaction Fee](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-transaction-fee/) | 🟡 Medium | State Machine | ⬜ |
| 21 | [Jump Game](https://leetcode.com/problems/jump-game/) | 🟡 Medium | Greedy/DP | ⬜ |
| 22 | [Jump Game II](https://leetcode.com/problems/jump-game-ii/) | 🟡 Medium | Greedy/DP | ⬜ |
| 23 | [Jump Game III](https://leetcode.com/problems/jump-game-iii/) | 🟡 Medium | BFS/DFS | ⬜ |
| 24 | [Jump Game IV](https://leetcode.com/problems/jump-game-iv/) | 🔴 Hard | BFS | ⬜ |
| 25 | [Frog Jump](https://leetcode.com/problems/frog-jump/) | 🔴 Hard | DP + HashMap | ⬜ |
| 26 | [Coin Change](https://leetcode.com/problems/coin-change/) | 🟡 Medium | Unbounded Knapsack | ⬜ |

---

## Week 8: 2D Dynamic Programming

### Day 1-2: Grid DP (13 problems)

| # | Problem | Difficulty | Pattern | Status |
|---|---------|------------|---------|--------|
| 27 | [Unique Paths](https://leetcode.com/problems/unique-paths/) | 🟡 Medium | Grid DP | ⬜ |
| 28 | [Unique Paths II](https://leetcode.com/problems/unique-paths-ii/) | 🟡 Medium | Grid DP | ⬜ |
| 29 | [Minimum Path Sum](https://leetcode.com/problems/minimum-path-sum/) | 🟡 Medium | Grid DP | ⬜ |
| 30 | [Triangle](https://leetcode.com/problems/triangle/) | 🟡 Medium | Bottom-Up DP | ⬜ |
| 31 | [Dungeon Game](https://leetcode.com/problems/dungeon-game/) | 🔴 Hard | Reverse Grid DP | ⬜ |
| 32 | [Cherry Pickup](https://leetcode.com/problems/cherry-pickup/) | 🔴 Hard | 3D DP | ⬜ |
| 33 | [Cherry Pickup II](https://leetcode.com/problems/cherry-pickup-ii/) | 🔴 Hard | 3D DP | ⬜ |
| 34 | [Out of Boundary Paths](https://leetcode.com/problems/out-of-boundary-paths/) | 🟡 Medium | Grid DP + Memoization | ⬜ |
| 35 | [Knight Probability in Chessboard](https://leetcode.com/problems/knight-probability-in-chessboard/) | 🟡 Medium | Grid DP | ⬜ |
| 36 | [Maximal Square](https://leetcode.com/problems/maximal-square/) | 🟡 Medium | Grid DP | ⬜ |
| 37 | [Count Square Submatrices with All Ones](https://leetcode.com/problems/count-square-submatrices-with-all-ones/) | 🟡 Medium | Grid DP | ⬜ |
| 38 | [Longest Line of Consecutive One in Matrix](https://leetcode.com/problems/longest-line-of-consecutive-one-in-matrix/) | 🟡 Medium | Grid DP | ⬜ |
| 39 | [Range Sum Query 2D - Immutable](https://leetcode.com/problems/range-sum-query-2d-immutable/) | 🟡 Medium | Prefix Sum 2D | ⬜ |

### Day 3-4: String DP (14 problems)

| # | Problem | Difficulty | Pattern | Status |
|---|---------|------------|---------|--------|
| 40 | [Longest Common Subsequence](https://leetcode.com/problems/longest-common-subsequence/) | 🟡 Medium | Classic 2D DP | ⬜ |
| 41 | [Longest Palindromic Substring](https://leetcode.com/problems/longest-palindromic-substring/) | 🟡 Medium | Expand from Center/DP | ⬜ |
| 42 | [Longest Palindromic Subsequence](https://leetcode.com/problems/longest-palindromic-subsequence/) | 🟡 Medium | LCS Variant | ⬜ |
| 43 | [Palindrome Partitioning II](https://leetcode.com/problems/palindrome-partitioning-ii/) | 🔴 Hard | DP | ⬜ |
| 44 | [Edit Distance](https://leetcode.com/problems/edit-distance/) | 🟡 Medium | Classic 2D DP | ⬜ |
| 45 | [Delete Operation for Two Strings](https://leetcode.com/problems/delete-operation-for-two-strings/) | 🟡 Medium | LCS Variant | ⬜ |
| 46 | [Minimum ASCII Delete Sum for Two Strings](https://leetcode.com/problems/minimum-ascii-delete-sum-for-two-strings/) | 🟡 Medium | LCS Variant | ⬜ |
| 47 | [Distinct Subsequences](https://leetcode.com/problems/distinct-subsequences/) | 🔴 Hard | String DP | ⬜ |
| 48 | [Interleaving String](https://leetcode.com/problems/interleaving-string/) | 🟡 Medium | 2D DP | ⬜ |
| 49 | [Shortest Common Supersequence](https://leetcode.com/problems/shortest-common-supersequence/) | 🔴 Hard | LCS + Reconstruction | ⬜ |
| 50 | [Wildcard Matching](https://leetcode.com/problems/wildcard-matching/) | 🔴 Hard | String DP | ⬜ |
| 51 | [Regular Expression Matching](https://leetcode.com/problems/regular-expression-matching/) | 🔴 Hard | String DP | ⬜ |
| 52 | [Palindrome Partitioning](https://leetcode.com/problems/palindrome-partitioning/) | 🟡 Medium | DP + Backtracking | ⬜ |
| 53 | [Scramble String](https://leetcode.com/problems/scramble-string/) | 🔴 Hard | 3D DP | ⬜ |

---

## Week 9: Advanced DP

### Day 1-2: Knapsack Problems (12 problems)

| # | Problem | Difficulty | Pattern | Status |
|---|---------|------------|---------|--------|
| 54 | [Partition Equal Subset Sum](https://leetcode.com/problems/partition-equal-subset-sum/) | 🟡 Medium | 0/1 Knapsack | ⬜ |
| 55 | [Target Sum](https://leetcode.com/problems/target-sum/) | 🟡 Medium | 0/1 Knapsack | ⬜ |
| 56 | [Coin Change](https://leetcode.com/problems/coin-change/) | 🟡 Medium | Unbounded Knapsack | ⬜ |
| 57 | [Coin Change II](https://leetcode.com/problems/coin-change-ii/) | 🟡 Medium | Unbounded Knapsack | ⬜ |
| 58 | [Perfect Squares](https://leetcode.com/problems/perfect-squares/) | 🟡 Medium | Unbounded Knapsack | ⬜ |
| 59 | [Combination Sum IV](https://leetcode.com/problems/combination-sum-iv/) | 🟡 Medium | Unbounded Knapsack | ⬜ |
| 60 | [Ones and Zeroes](https://leetcode.com/problems/ones-and-zeroes/) | 🟡 Medium | 2D Knapsack | ⬜ |
| 61 | [Last Stone Weight II](https://leetcode.com/problems/last-stone-weight-ii/) | 🟡 Medium | 0/1 Knapsack | ⬜ |
| 62 | [Profitable Schemes](https://leetcode.com/problems/profitable-schemes/) | 🔴 Hard | 3D Knapsack | ⬜ |
| 63 | [Number of Ways to Earn Points](https://leetcode.com/problems/number-of-ways-to-earn-points/) | 🔴 Hard | Bounded Knapsack | ⬜ |
| 64 | [Minimize the Difference Between Target and Chosen Elements](https://leetcode.com/problems/minimize-the-difference-between-target-and-chosen-elements/) | 🟡 Medium | Knapsack Variant | ⬜ |
| 65 | [Tallest Billboard](https://leetcode.com/problems/tallest-billboard/) | 🔴 Hard | Knapsack Variant | ⬜ |

### Day 3-4: Interval DP & Other (15 problems)

| # | Problem | Difficulty | Pattern | Status |
|---|---------|------------|---------|--------|
| 66 | [Burst Balloons](https://leetcode.com/problems/burst-balloons/) | 🔴 Hard | Interval DP | ⬜ |
| 67 | [Minimum Cost Tree From Leaf Values](https://leetcode.com/problems/minimum-cost-tree-from-leaf-values/) | 🟡 Medium | Interval DP / Stack | ⬜ |
| 68 | [Stone Game](https://leetcode.com/problems/stone-game/) | 🟡 Medium | Interval DP | ⬜ |
| 69 | [Stone Game II](https://leetcode.com/problems/stone-game-ii/) | 🟡 Medium | Interval DP | ⬜ |
| 70 | [Stone Game III](https://leetcode.com/problems/stone-game-iii/) | 🔴 Hard | Game Theory DP | ⬜ |
| 71 | [Predict the Winner](https://leetcode.com/problems/predict-the-winner/) | 🟡 Medium | Interval DP | ⬜ |
| 72 | [Minimum Cost to Merge Stones](https://leetcode.com/problems/minimum-cost-to-merge-stones/) | 🔴 Hard | Interval DP | ⬜ |
| 73 | [Arithmetic Slices II - Subsequence](https://leetcode.com/problems/arithmetic-slices-ii-subsequence/) | 🔴 Hard | DP + HashMap | ⬜ |
| 74 | [Arithmetic Slices](https://leetcode.com/problems/arithmetic-slices/) | 🟡 Medium | Linear DP | ⬜ |
| 75 | [House Robber III](https://leetcode.com/problems/house-robber-iii/) | 🟡 Medium | Tree DP | ⬜ |
| 76 | [Unique Binary Search Trees](https://leetcode.com/problems/unique-binary-search-trees/) | 🟡 Medium | Catalan Numbers | ⬜ |
| 77 | [Unique Binary Search Trees II](https://leetcode.com/problems/unique-binary-search-trees-ii/) | 🟡 Medium | DP + Recursion | ⬜ |
| 78 | [Count Different Palindromic Subsequences](https://leetcode.com/problems/count-different-palindromic-subsequences/) | 🔴 Hard | DP | ⬜ |
| 79 | [Longest Valid Parentheses](https://leetcode.com/problems/longest-valid-parentheses/) | 🔴 Hard | DP / Stack | ⬜ |
| 80 | [Paint House](https://leetcode.com/problems/paint-house/) | 🟡 Medium | Linear DP | ⬜ |

---

## Summary - Week 7-9

| Topic | Easy | Medium | Hard | Total |
|-------|------|--------|------|-------|
| Fibonacci & Linear DP | 3 | 9 | 2 | 14 |
| Decision Making DP | 1 | 8 | 3 | 12 |
| Grid DP | 0 | 11 | 2 | 13 |
| String DP | 0 | 8 | 6 | 14 |
| Knapsack Problems | 0 | 9 | 3 | 12 |
| Interval DP & Other | 0 | 9 | 6 | 15 |
| **Total** | **4** | **54** | **22** | **80** |

---

## Key DP Patterns to Remember

### 1. Fibonacci Style
```
dp[i] depends on dp[i-1], dp[i-2], etc.
Examples: Climbing Stairs, House Robber
```

### 2. Kadane's Algorithm (Maximum Subarray)
```python
def maxSubarray(nums):
    max_sum = curr_sum = nums[0]
    for num in nums[1:]:
        curr_sum = max(num, curr_sum + num)
        max_sum = max(max_sum, curr_sum)
    return max_sum
```

### 3. LCS (Longest Common Subsequence)
```python
def LCS(s1, s2):
    m, n = len(s1), len(s2)
    dp = [[0] * (n + 1) for _ in range(m + 1)]
    
    for i in range(1, m + 1):
        for j in range(1, n + 1):
            if s1[i-1] == s2[j-1]:
                dp[i][j] = dp[i-1][j-1] + 1
            else:
                dp[i][j] = max(dp[i-1][j], dp[i][j-1])
    
    return dp[m][n]
```

### 4. 0/1 Knapsack
```python
def knapsack(weights, values, capacity):
    n = len(weights)
    dp = [[0] * (capacity + 1) for _ in range(n + 1)]
    
    for i in range(1, n + 1):
        for w in range(capacity + 1):
            if weights[i-1] <= w:
                dp[i][w] = max(dp[i-1][w], 
                               dp[i-1][w - weights[i-1]] + values[i-1])
            else:
                dp[i][w] = dp[i-1][w]
    
    return dp[n][capacity]
```

### 5. Unbounded Knapsack (Coin Change)
```python
def coinChange(coins, amount):
    dp = [float('inf')] * (amount + 1)
    dp[0] = 0
    
    for coin in coins:
        for x in range(coin, amount + 1):
            dp[x] = min(dp[x], dp[x - coin] + 1)
    
    return dp[amount] if dp[amount] != float('inf') else -1
```

### 6. Interval DP
```python
# General Template for Interval DP
def intervalDP(arr):
    n = len(arr)
    dp = [[0] * n for _ in range(n)]
    
    # Length of interval
    for length in range(2, n + 1):
        for i in range(n - length + 1):
            j = i + length - 1
            for k in range(i, j):  # Partition point
                dp[i][j] = min(dp[i][j], 
                               dp[i][k] + dp[k+1][j] + cost(i, j, k))
    
    return dp[0][n-1]
```

### 7. State Machine DP (Stock Problems)
```python
# Buy and Sell Stock with Cooldown
def maxProfit(prices):
    hold = -prices[0]  # Holding stock
    sold = 0           # Just sold
    rest = 0           # Cooldown
    
    for price in prices[1:]:
        prev_hold = hold
        hold = max(hold, rest - price)  # Buy
        rest = max(rest, sold)           # Cooldown
        sold = prev_hold + price         # Sell
    
    return max(sold, rest)
```

---

## DP Problem-Solving Framework

```
1. IDENTIFY if it's a DP problem
   - "Count ways to..."
   - "Find minimum/maximum..."
   - "Is it possible to..."
   - Optimal substructure + overlapping subproblems

2. DEFINE the state
   - What parameters uniquely identify a subproblem?
   - dp[i] = answer for first i elements
   - dp[i][j] = answer for subproblem with parameters i, j

3. WRITE the recurrence relation
   - How does dp[i] relate to smaller subproblems?
   
4. IDENTIFY base cases
   - What are the smallest subproblems?
   - dp[0] = ?, dp[1] = ?

5. DETERMINE iteration order
   - Bottom-up: smaller → larger
   - Top-down: use memoization

6. OPTIMIZE space if needed
   - Often can reduce 2D → 1D
```

---

*Progress: ___ / 80 completed*

