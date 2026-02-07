package com.learning.thread;

/**
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                                                                                                ║
 * ║                      THREADS: FROM BASICS TO EVOLUTION - A COMPLETE GUIDE                                      ║
 * ║                                                                                                                ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                     PART 1: THE BASICS                                                         ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  1.1 WHAT IS A CPU?                                                                                            │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  CPU (Central Processing Unit) = The "brain" of the computer that executes instructions.
 *
 *  ┌─────────────────────────────────────────────────────────────────────┐
 *  │                         CPU                                         │
 *  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐              │
 *  │  │ Control Unit │  │     ALU      │  │  Registers   │              │
 *  │  │ (fetches &   │  │ (does math   │  │ (tiny fast   │              │
 *  │  │  decodes)    │  │  & logic)    │  │  memory)     │              │
 *  │  └──────────────┘  └──────────────┘  └──────────────┘              │
 *  └─────────────────────────────────────────────────────────────────────┘
 *
 *  KEY POINT: A CPU can only do ONE thing at a time (per core).
 *
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  1.2 WHAT IS A PROGRAM?                                                                                        │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  Program = A file containing instructions (code) stored on disk.
 *            It's STATIC - just sitting there doing nothing.
 *
 *            Example: /usr/bin/firefox, MyApp.jar, chrome.exe
 *
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  1.3 WHAT IS A PROCESS?                                                                                        │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  Process = A program that is RUNNING. When you double-click an app, it becomes a process.
 *
 *  Each process gets its own:
 *  ┌─────────────────────────────────────────────────────────────────────┐
 *  │                         PROCESS                                     │
 *  │  ┌──────────────────────────────────────────────────────────────┐   │
 *  │  │  Memory Space (ISOLATED from other processes)                │   │
 *  │  │  ├── Code (the program instructions)                         │   │
 *  │  │  ├── Data (global variables)                                 │   │
 *  │  │  ├── Heap (dynamically allocated memory)                     │   │
 *  │  │  └── Stack (function calls, local variables)                 │   │
 *  │  └──────────────────────────────────────────────────────────────┘   │
 *  │  • Process ID (PID) - unique identifier                             │
 *  │  • Open files, network connections                                  │
 *  │  • Security permissions                                             │
 *  └─────────────────────────────────────────────────────────────────────┘
 *
 *  KEY POINT: Processes are ISOLATED. Process A cannot directly access Process B's memory.
 *             This is good for security, but makes communication between them slow.
 *
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  1.4 WHAT IS A THREAD?                                                                                         │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  Thread = A "lightweight process" - an independent path of execution WITHIN a process.
 *
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                              PROCESS                                                    │
 *  │  ┌──────────────────────────────────────────────────────────────────────────────────┐   │
 *  │  │              SHARED BY ALL THREADS                                               │   │
 *  │  │  • Code (same program)                                                           │   │
 *  │  │  • Data (global variables)                                                       │   │
 *  │  │  • Heap (shared memory)                                                          │   │
 *  │  │  • Open files                                                                    │   │
 *  │  └──────────────────────────────────────────────────────────────────────────────────┘   │
 *  │                                                                                         │
 *  │  ┌──────────────┐   ┌──────────────┐   ┌──────────────┐                                │
 *  │  │   Thread 1   │   │   Thread 2   │   │   Thread 3   │                                │
 *  │  │  ──────────  │   │  ──────────  │   │  ──────────  │                                │
 *  │  │  Own Stack   │   │  Own Stack   │   │  Own Stack   │  ← Each thread has its own     │
 *  │  │  Own PC      │   │  Own PC      │   │  Own PC      │    Stack & Program Counter     │
 *  │  │  Own Regs    │   │  Own Regs    │   │  Own Regs    │                                │
 *  │  └──────────────┘   └──────────────┘   └──────────────┘                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  KEY POINTS:
 *  • Threads SHARE memory → Communication is FAST (just read/write shared variables)
 *  • Threads SHARE memory → Must be careful about conflicts (race conditions)
 *  • Each thread has its own Stack → Local variables are private to each thread
 *
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                              PART 2: THE EVOLUTION OF THREADS                                                  ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  ERA 1: BATCH PROCESSING (1950s-1960s) - No Multitasking                                                       │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  ONE program ran at a time. Next program waited until current one finished.
 *
 *  Time ──────────────────────────────────────────────────────────────────►
 *  ┌──────────────────┐                    ┌──────────────────┐
 *  │    Program A     │      (IDLE)        │    Program B     │
 *  │    running       │   waiting for A    │    running       │
 *  └──────────────────┘                    └──────────────────┘
 *
 *  PROBLEM: When Program A waited for I/O (disk, printer), CPU sat IDLE.
 *           CPUs were expensive! Wasting CPU time = wasting money.
 *
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  ERA 2: MULTIPROGRAMMING (1960s) - Multiple Programs in Memory                                                 │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  IDEA: Keep multiple programs in memory. When one waits for I/O, run another!
 *
 *  Time ──────────────────────────────────────────────────────────────────►
 *  ┌────────┐  ┌────────┐  ┌────────┐  ┌────────┐  ┌────────┐
 *  │ Prog A │  │ Prog B │  │ Prog A │  │ Prog C │  │ Prog B │
 *  │ (runs) │  │(A wait)│  │(resume)│  │(B wait)│  │(resume)│
 *  └────────┘  └────────┘  └────────┘  └────────┘  └────────┘
 *
 *  CPU stays busy by switching! Birth of the PROCESS concept.
 *
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  ERA 3: TIME-SHARING / UNIX (1969-1970s) - Interactive Computing                                               │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  UNIX introduced TIME SLICING: Each process gets a small "slice" of CPU time
 *  (e.g., 10 milliseconds), then switches to the next process.
 *
 *  ┌────────────────────────────────────────────────────────────────────────────────────┐
 *  │                           TIME SLICING                                             │
 *  │                                                                                    │
 *  │  Time →                                                                            │
 *  │  ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐                  │
 *  │  │ P1  │ │ P2  │ │ P3  │ │ P1  │ │ P2  │ │ P3  │ │ P1  │ │ P2  │ ...              │
 *  │  │10ms │ │10ms │ │10ms │ │10ms │ │10ms │ │10ms │ │10ms │ │10ms │                  │
 *  │  └─────┘ └─────┘ └─────┘ └─────┘ └─────┘ └─────┘ └─────┘ └─────┘                  │
 *  │                                                                                    │
 *  │  This happens so fast that users feel all programs run "simultaneously"!           │
 *  │  Multiple users could share ONE computer through terminals.                        │
 *  └────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  KEY INNOVATION: Preemptive multitasking - OS forcibly takes CPU away after time slice.
 *
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  ERA 4: THREADS INVENTED (1980s-1990s) - Lightweight Processes                                                 │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  PROBLEM WITH PROCESSES:
 *  ┌────────────────────────────────────────────────────────────────────────────────────┐
 *  │  1. SLOW TO CREATE: Need to allocate new memory, copy page tables, etc.           │
 *  │     → Takes ~10,000+ CPU cycles                                                   │
 *  │                                                                                    │
 *  │  2. SLOW TO SWITCH: Need to save/restore entire memory context                    │
 *  │     → Takes ~1,000-10,000 CPU cycles                                              │
 *  │                                                                                    │
 *  │  3. HARD TO COMMUNICATE: Processes have isolated memory                           │
 *  │     → Need complex IPC (pipes, sockets, shared memory)                            │
 *  └────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  SOLUTION: THREADS!
 *  ┌────────────────────────────────────────────────────────────────────────────────────┐
 *  │  1. FAST TO CREATE: Just allocate a stack (~100 CPU cycles)                       │
 *  │                                                                                    │
 *  │  2. FAST TO SWITCH: Only save/restore registers and stack pointer (~100 cycles)   │
 *  │                                                                                    │
 *  │  3. EASY TO COMMUNICATE: Threads share memory - just read/write variables!        │
 *  └────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  Thread implementations:
 *  • 1980s: User-level threads (managed by libraries, invisible to OS)
 *  • 1990s: Kernel threads (managed by OS, can use multiple CPUs)
 *  • POSIX Threads (pthreads) standardized in 1995
 *
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  ERA 5: MULTI-CORE CPUs (2000s) - True Parallelism                                                             │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  BEFORE (Single-Core):
 *  Only ONE thread runs at any instant. "Multithreading" was an ILLUSION via time-slicing.
 *
 *  AFTER (Multi-Core):
 *  Multiple threads run TRULY SIMULTANEOUSLY on different cores!
 *
 *  ┌────────────────────────────────────────────────────────────────────────────────────┐
 *  │                          MULTI-CORE CPU                                            │
 *  │                                                                                    │
 *  │   ┌───────────┐  ┌───────────┐  ┌───────────┐  ┌───────────┐                      │
 *  │   │  CORE 0   │  │  CORE 1   │  │  CORE 2   │  │  CORE 3   │                      │
 *  │   │ ┌───────┐ │  │ ┌───────┐ │  │ ┌───────┐ │  │ ┌───────┐ │                      │
 *  │   │ │Thread │ │  │ │Thread │ │  │ │Thread │ │  │ │Thread │ │                      │
 *  │   │ │   A   │ │  │ │   B   │ │  │ │   C   │ │  │ │   D   │ │                      │
 *  │   │ └───────┘ │  │ └───────┘ │  │ └───────┘ │  │ └───────┘ │                      │
 *  │   └───────────┘  └───────────┘  └───────────┘  └───────────┘                      │
 *  │                                                                                    │
 *  │   → 4 threads running TRULY in parallel!                                           │
 *  │                                                                                    │
 *  └────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  Timeline:
 *  • 2001: IBM POWER4 - first dual-core chip
 *  • 2005: Intel Pentium D - consumer dual-core
 *  • 2006: Intel Core 2 Duo
 *  • 2007: Intel Core 2 Quad (4 cores)
 *  • Today: 8, 16, 32, 64+ cores common!
 *
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  ERA 6: HYPER-THREADING / SMT (2002+) - Two Threads Per Core                                                   │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  Intel's Hyper-Threading: One physical core can run TWO threads "simultaneously"
 *  by sharing execution units.
 *
 *  ┌────────────────────────────────────────────────────────────────────────────────────┐
 *  │                      PHYSICAL CORE with Hyper-Threading                            │
 *  │                                                                                    │
 *  │   ┌─────────────────────────────────────────────────────────────────────────────┐  │
 *  │   │                                                                             │  │
 *  │   │  ┌─────────────┐    ┌─────────────┐         ┌───────────────────────────┐   │  │
 *  │   │  │ Thread 1    │    │ Thread 2    │         │   SHARED RESOURCES        │   │  │
 *  │   │  │ State       │    │ State       │         │   • Execution Units (ALU) │   │  │
 *  │   │  │ (Registers, │    │ (Registers, │         │   • Cache                 │   │  │
 *  │   │  │  PC, SP)    │    │  PC, SP)    │         │   • Branch Predictor      │   │  │
 *  │   │  └─────────────┘    └─────────────┘         └───────────────────────────┘   │  │
 *  │   │                                                                             │  │
 *  │   └─────────────────────────────────────────────────────────────────────────────┘  │
 *  │                                                                                    │
 *  │  WHY IT HELPS: When Thread 1 is waiting (memory fetch, cache miss),               │
 *  │                Thread 2 can use the execution units instead of wasting them.       │
 *  │                                                                                    │
 *  │  PERFORMANCE GAIN: ~15-30% improvement (not 100% - resources are shared)           │
 *  │                                                                                    │
 *  │  EXAMPLE: 4-core CPU with Hyper-Threading                                          │
 *  │           = 4 physical cores, 8 logical cores (what OS and Java see)               │
 *  │                                                                                    │
 *  └────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  ERA 7: JAVA'S THREAD EVOLUTION (1995-2023)                                                                    │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  ┌────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                    │
 *  │  JAVA 1.0 (1995): Green Threads                                                    │
 *  │  ───────────────────────────────                                                   │
 *  │  • User-level threads managed by JVM (not by OS)                                   │
 *  │  • OS sees only ONE thread (the JVM process)                                       │
 *  │  • Cannot use multiple CPU cores!                                                  │
 *  │  • If one thread blocks on I/O → entire JVM blocks                                 │
 *  │                                                                                    │
 *  │                                                                                    │
 *  │  JAVA 1.2+ (1998): Native Threads (1:1 Model)                                      │
 *  │  ──────────────────────────────────────────────                                    │
 *  │  • Each Java thread = one OS/kernel thread                                         │
 *  │  • Can use multiple CPU cores!                                                     │
 *  │  • But: Each thread uses ~1MB stack memory                                         │
 *  │  • But: Creating/switching threads is expensive (OS kernel calls)                  │
 *  │  • Practical limit: ~thousands of threads per JVM                                  │
 *  │                                                                                    │
 *  │                                                                                    │
 *  │  JAVA 5 (2004): java.util.concurrent                                               │
 *  │  ─────────────────────────────────────                                             │
 *  │  • ExecutorService, ThreadPool                                                     │
 *  │  • Reuse threads instead of creating new ones                                      │
 *  │  • Concurrent collections (ConcurrentHashMap, etc.)                                │
 *  │  • Locks, Semaphores, CountDownLatch                                               │
 *  │                                                                                    │
 *  │                                                                                    │
 *  │  JAVA 7 (2011): Fork/Join Framework                                                │
 *  │  ────────────────────────────────────                                              │
 *  │  • Work-stealing algorithm for parallel computation                                │
 *  │  • ForkJoinPool for divide-and-conquer tasks                                       │
 *  │                                                                                    │
 *  │                                                                                    │
 *  │  JAVA 8 (2014): CompletableFuture & Parallel Streams                               │
 *  │  ────────────────────────────────────────────────────                              │
 *  │  • Async programming made easier                                                   │
 *  │  • list.parallelStream() for easy parallelism                                      │
 *  │                                                                                    │
 *  │                                                                                    │
 *  │  JAVA 21 (2023): VIRTUAL THREADS (Project Loom) ← BIGGEST CHANGE SINCE 1.2!        │
 *  │  ────────────────────────────────────────────────                                  │
 *  │  • Lightweight threads managed by JVM (not OS)                                     │
 *  │  • Each virtual thread uses only ~few KB (not 1MB!)                                │
 *  │  • Can have MILLIONS of virtual threads                                            │
 *  │  • M:N model: millions of virtual threads → few OS carrier threads                 │
 *  │                                                                                    │
 *  └────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  ERA 8: VIRTUAL THREADS (Java 21+, 2023) - The Future                                                          │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  TRADITIONAL (Platform) THREADS:
 *  ┌────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                    │
 *  │  Java Thread 1  ─────────────────►  OS Thread 1                                    │
 *  │  Java Thread 2  ─────────────────►  OS Thread 2                                    │
 *  │  Java Thread 3  ─────────────────►  OS Thread 3                                    │
 *  │                                                                                    │
 *  │  1:1 mapping. Each Java thread = 1 OS thread = ~1MB memory.                        │
 *  │  Limit: ~10,000 threads before running out of memory/resources.                    │
 *  │                                                                                    │
 *  └────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  VIRTUAL THREADS (Java 21+):
 *  ┌────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                    │
 *  │  Virtual Thread 1  ──┐                                                             │
 *  │  Virtual Thread 2  ──┼──►  Carrier Thread 1  ───►  OS Thread 1                     │
 *  │  Virtual Thread 3  ──┘                                                             │
 *  │  Virtual Thread 4  ──┐                                                             │
 *  │  Virtual Thread 5  ──┼──►  Carrier Thread 2  ───►  OS Thread 2                     │
 *  │  Virtual Thread 6  ──┘                                                             │
 *  │  ...                                                                               │
 *  │  (millions!)                                                                       │
 *  │                                                                                    │
 *  │  M:N mapping. Millions of virtual threads → few carrier threads → few OS threads  │
 *  │  Each virtual thread = ~few KB. Can have MILLIONS!                                 │
 *  │                                                                                    │
 *  └────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  HOW TO USE:
 *  ┌────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                    │
 *  │  // Create a virtual thread                                                        │
 *  │  Thread.startVirtualThread(() -> {                                                 │
 *  │      System.out.println("Hello from virtual thread!");                             │
 *  │  });                                                                               │
 *  │                                                                                    │
 *  │  // Or use an executor                                                             │
 *  │  try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {                │
 *  │      executor.submit(() -> doSomething());                                         │
 *  │  }                                                                                 │
 *  │                                                                                    │
 *  └────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                              PART 3: KEY CONCEPTS                                                              ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  3.1 CONCURRENCY vs PARALLELISM                                                                                │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  CONCURRENCY (Single-Core):
 *  ┌────────────────────────────────────────────────────────────────────────────────────┐
 *  │  Multiple tasks make progress by taking turns (time-slicing)                       │
 *  │  Only ONE runs at any instant. It's an ILLUSION of simultaneous execution.         │
 *  │                                                                                    │
 *  │  Time →  [T1][T2][T1][T3][T1][T2][T3][T1]...                                        │
 *  │                                                                                    │
 *  │  GOOD FOR: I/O-bound tasks (while one waits for I/O, others can run)               │
 *  └────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  PARALLELISM (Multi-Core):
 *  ┌────────────────────────────────────────────────────────────────────────────────────┐
 *  │  Multiple tasks run TRULY at the same time on different cores.                     │
 *  │                                                                                    │
 *  │  Core 0:  [  Task 1  ][  Task 1  ][  Task 1  ]                                     │
 *  │  Core 1:  [  Task 2  ][  Task 2  ][  Task 2  ]                                     │
 *  │  Core 2:  [  Task 3  ][  Task 3  ][  Task 3  ]                                     │
 *  │  Core 3:  [  Task 4  ][  Task 4  ][  Task 4  ]                                     │
 *  │                                                                                    │
 *  │  GOOD FOR: CPU-bound tasks (computation-heavy work)                                │
 *  └────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  3.2 CONTEXT SWITCHING                                                                                         │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  When CPU switches from running Thread A to Thread B, it must:
 *
 *  ┌────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                    │
 *  │  1. SAVE Thread A's state:                                                         │
 *  │     • All CPU registers (where Thread A was in its computation)                    │
 *  │     • Program Counter (which instruction Thread A was about to execute)            │
 *  │     • Stack Pointer (where Thread A's stack is)                                    │
 *  │                                                                                    │
 *  │  2. RESTORE Thread B's state:                                                      │
 *  │     • Load Thread B's registers, PC, SP                                            │
 *  │     • Thread B continues exactly where it left off                                 │
 *  │                                                                                    │
 *  └────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  COST:
 *  • Thread switch (same process): ~100-1000 CPU cycles
 *  • Process switch: ~1000-10,000 CPU cycles (more state to save, cache flush)
 *
 *  This is WHY threads are "lightweight" - less state to save/restore than processes.
 *
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  3.3 THREAD STATES                                                                                             │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *                         ┌─────────────┐
 *                         │     NEW     │
 *                         │ (created)   │
 *                         └──────┬──────┘
 *                                │ start()
 *                                ▼
 *                         ┌─────────────┐
 *               ┌────────►│  RUNNABLE   │◄────────┐
 *               │         │ (ready/run) │         │
 *               │         └──────┬──────┘         │
 *               │                │                │
 *    notify()   │                │                │  sleep done
 *    I/O done   │                │                │  lock acquired
 *               │                │                │
 *         ┌─────┴─────┐          │         ┌──────┴──────┐
 *         │  WAITING  │◄─────────┼────────►│   BLOCKED   │
 *         │ (wait())  │  wait()  │  lock   │(waiting for │
 *         └───────────┘          │         │   lock)     │
 *                                │         └─────────────┘
 *                                │
 *                                │ run() completes
 *                                ▼
 *                         ┌─────────────┐
 *                         │ TERMINATED  │
 *                         │   (dead)    │
 *                         └─────────────┘
 *
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                        SUMMARY TIMELINE                                                        ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  ┌──────────┬───────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │   Year   │   Milestone                                                                                       │
 *  ├──────────┼───────────────────────────────────────────────────────────────────────────────────────────────────┤
 *  │  1950s   │   Batch Processing - one program at a time                                                        │
 *  │  1960s   │   Multiprogramming - multiple programs in memory, switch on I/O                                   │
 *  │  1969    │   Unix created - time-sharing, processes, preemptive multitasking                                 │
 *  │  1980s   │   Threads invented - lightweight processes sharing memory                                         │
 *  │  1995    │   Java 1.0 with Green Threads (user-level, single-core only)                                      │
 *  │  1995    │   POSIX Threads (pthreads) standardized                                                           │
 *  │  1998    │   Java 1.2 switches to Native Threads (1:1 with OS threads)                                       │
 *  │  2002    │   Intel Hyper-Threading - 2 threads per core                                                      │
 *  │  2004    │   Java 5: java.util.concurrent (ExecutorService, ThreadPool)                                      │
 *  │  2005    │   Multi-core CPUs become mainstream (Intel Pentium D)                                             │
 *  │  2011    │   Java 7: Fork/Join Framework                                                                     │
 *  │  2014    │   Java 8: CompletableFuture, Parallel Streams                                                     │
 *  │  2023    │   Java 21: Virtual Threads - millions of lightweight threads!                                     │
 *  └──────────┴───────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * @author Thread Evolution Guide
 * @see _03_MultithreadingConcurrency for Java-specific threading concepts and code examples
 */
public class _01_ThreadsFromHardware {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║             THREADS: FROM BASICS TO EVOLUTION - DEMO                               ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════════════╝\n");

        // 1. Basic System Info
        showSystemInfo();

        // 2. Process vs Thread Demo
        demonstrateProcessVsThread();

        // 3. Thread States Demo
        demonstrateThreadStates();

        // 4. Virtual Threads Demo (Java 21+)
        demonstrateVirtualThreads();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         1. SYSTEM INFORMATION
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void showSystemInfo() {
        System.out.println("▶ 1. YOUR SYSTEM:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        int processors = Runtime.getRuntime().availableProcessors();
        long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        String javaVersion = System.getProperty("java.version");

        System.out.println("   Logical Cores (threads CPU can run in parallel): " + processors);
        System.out.println("   JVM Max Memory: " + maxMemory + " MB");
        System.out.println("   Java Version: " + javaVersion);

        if (processors % 2 == 0 && processors >= 4) {
            System.out.println("   → If you have " + (processors / 2) + " physical cores, Hyper-Threading is likely enabled!");
        }
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         2. PROCESS vs THREAD
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateProcessVsThread() {
        System.out.println("▶ 2. THREAD SHARED MEMORY DEMO:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // Shared variable between threads (possible because threads share heap)
        final int[] sharedCounter = {0};

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                sharedCounter[0]++;  // Accessing shared memory
            }
            System.out.println("   Thread 1 finished, counter = " + sharedCounter[0]);
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                sharedCounter[0]++;  // Same shared memory!
            }
            System.out.println("   Thread 2 finished, counter = " + sharedCounter[0]);
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("   Final counter value: " + sharedCounter[0]);
        System.out.println("   (Expected 2000, but may be less due to race condition!)");
        System.out.println();
        System.out.println("   💡 KEY INSIGHT: Threads share memory - that's why they can both modify");
        System.out.println("      sharedCounter. Processes CANNOT do this directly!");
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         3. THREAD STATES
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateThreadStates() {
        System.out.println("▶ 3. THREAD STATES DEMO:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(100);  // TIMED_WAITING state
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        System.out.println("   After creation:  " + thread.getState());  // NEW

        thread.start();
        System.out.println("   After start():   " + thread.getState());  // RUNNABLE

        try {
            Thread.sleep(50);  // Let it enter sleep
            System.out.println("   During sleep():  " + thread.getState());  // TIMED_WAITING

            thread.join();
            System.out.println("   After join():    " + thread.getState());  // TERMINATED
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         4. VIRTUAL THREADS
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateVirtualThreads() {
        System.out.println("▶ 4. VIRTUAL THREADS (Java 21+):");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        int javaVersion = Runtime.version().feature();

        if (javaVersion >= 21) {
            System.out.println("   ✓ Java " + javaVersion + " detected - Virtual Threads available!");
            System.out.println();

            int threadCount = 10_000;
            long start = System.nanoTime();

            try {
                // Use reflection for compile compatibility with older Java
                java.lang.reflect.Method startVirtualThread =
                    Thread.class.getMethod("startVirtualThread", Runnable.class);

                Thread[] threads = new Thread[threadCount];
                for (int i = 0; i < threadCount; i++) {
                    Runnable task = () -> {
                        try { Thread.sleep(10); }
                        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                    };
                    threads[i] = (Thread) startVirtualThread.invoke(null, task);
                }

                for (Thread thread : threads) {
                    thread.join();
                }

                long elapsed = System.nanoTime() - start;
                System.out.println("   Created and ran " + threadCount + " virtual threads");
                System.out.println("   Total time: " + (elapsed / 1_000_000.0) + " ms");
                System.out.println();
                System.out.println("   💡 Try creating 10,000 platform threads - it would use ~10GB memory!");
                System.out.println("      Virtual threads use only ~few KB each.");

            } catch (Exception e) {
                System.out.println("   Error: " + e.getMessage());
            }
        } else {
            System.out.println("   Current Java version: " + javaVersion);
            System.out.println("   Virtual Threads require Java 21+");
            System.out.println();
            System.out.println("   EVOLUTION OF JAVA THREADS:");
            System.out.println("   ┌────────────────────────────────────────────────────────────────────────┐");
            System.out.println("   │ Java 1.0 (1995): Green Threads - JVM-managed, single-core only        │");
            System.out.println("   │ Java 1.2 (1998): Native Threads - 1:1 with OS threads, multi-core     │");
            System.out.println("   │ Java 5   (2004): ExecutorService, ThreadPool - reuse threads          │");
            System.out.println("   │ Java 21  (2023): Virtual Threads - millions of lightweight threads!   │");
            System.out.println("   └────────────────────────────────────────────────────────────────────────┘");
        }
        System.out.println();
    }
}
