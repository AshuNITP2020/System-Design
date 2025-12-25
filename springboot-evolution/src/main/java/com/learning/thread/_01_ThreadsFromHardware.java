package com.learning.thread;

/**
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                                                                                                ║
 * ║                      THREADS: FROM HARDWARE TO SOFTWARE - A DEEP DIVE                                          ║
 * ║                                                                                                                ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * This guide explores threads from the ground up - starting from CPU hardware, understanding
 * why threads were needed, and how they evolved into what we use today.
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                1. THE EARLY DAYS: SINGLE PROCESS SYSTEMS                                       ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  TIMELINE OF COMPUTING EVOLUTION:
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │  1940s-1950s: Batch Processing Era                                                                             │
 *  │  ─────────────────────────────────────────                                                                     │
 *  │  • One program ran at a time (NO multitasking)                                                                 │
 *  │  • CPU sat idle during I/O operations (waiting for punch cards, tape drives)                                   │
 *  │  • Extremely wasteful - CPUs were EXPENSIVE!                                                                   │
 *  │                                                                                                                │
 *  │        ┌─────────────────────────────────────────────────────────────────┐                                     │
 *  │        │ Program A runs │ IDLE (I/O) │ Program A │ IDLE │ Program B      │                                     │
 *  │        │                │  waiting   │ continues │      │  runs          │                                     │
 *  │        └─────────────────────────────────────────────────────────────────┘                                     │
 *  │                           ↑ CPU doing NOTHING while waiting for I/O!                                           │
 *  │                                                                                                                │
 *  │  1960s: Multiprogramming Era                                                                                   │
 *  │  ─────────────────────────────────────────                                                                     │
 *  │  • Multiple programs loaded in memory                                                                          │
 *  │  • When one program waits for I/O, switch to another                                                           │
 *  │  • Birth of the PROCESS concept                                                                                │
 *  │                                                                                                                │
 *  │        ┌─────────────────────────────────────────────────────────────────┐                                     │
 *  │        │ Process A │ Process B │ Process A │ Process C │ Process B      │                                     │
 *  │        │  (runs)   │  (A waits)│  (resumed)│  (B waits)│  (resumed)     │                                     │
 *  │        └─────────────────────────────────────────────────────────────────┘                                     │
 *  │                      ↑ CPU stays BUSY by switching between processes!                                          │
 *  │                                                                                                                │
 *  │  1970s-1980s: Time-Sharing Systems                                                                             │
 *  │  ─────────────────────────────────────────                                                                     │
 *  │  • Multiple users share one computer                                                                           │
 *  │  • Each gets a "time slice" of CPU                                                                             │
 *  │  • Unix born (1969) - process-based multitasking                                                               │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                2. WHY THREADS? THE PROBLEM WITH PROCESSES                                      ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  PROCESS LIMITATIONS THAT LED TO THREADS:
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │  Problem 1: HEAVYWEIGHT CREATION                                                                               │
 *  │  ────────────────────────────────                                                                              │
 *  │  Creating a new process requires:                                                                              │
 *  │  • Allocating new memory space                                                                                 │
 *  │  • Copying page tables                                                                                         │
 *  │  • Setting up file descriptors                                                                                 │
 *  │  • Creating new PCB (Process Control Block)                                                                    │
 *  │  → Takes ~10,000 - 100,000 CPU cycles!                                                                         │
 *  │                                                                                                                │
 *  │  Problem 2: EXPENSIVE CONTEXT SWITCHING                                                                        │
 *  │  ───────────────────────────────────                                                                           │
 *  │  Switching between processes:                                                                                  │
 *  │  • Save entire CPU state (all registers)                                                                       │
 *  │  • Flush TLB (Translation Lookaside Buffer)                                                                    │
 *  │  • Switch memory mappings                                                                                      │
 *  │  • Flush CPU caches (different address space!)                                                                 │
 *  │  → Takes ~1,000 - 10,000 CPU cycles                                                                            │
 *  │                                                                                                                │
 *  │  Problem 3: COMMUNICATION IS HARD                                                                              │
 *  │  ─────────────────────────────────                                                                             │
 *  │  Processes have isolated memory:                                                                               │
 *  │  • Need IPC (Inter-Process Communication)                                                                      │
 *  │  • Pipes, Sockets, Shared Memory (complex!)                                                                    │
 *  │  • Kernel involvement = more overhead                                                                          │
 *  │                                                                                                                │
 *  │  ┌─────────────────────┐           ┌─────────────────────┐                                                     │
 *  │  │     Process A       │           │     Process B       │                                                     │
 *  │  │  ┌───────────────┐  │           │  ┌───────────────┐  │                                                     │
 *  │  │  │ Memory Space  │  │   IPC     │  │ Memory Space  │  │                                                     │
 *  │  │  │ (Isolated)    │◄─┼───────────┼──┤  (Isolated)   │  │                                                     │
 *  │  │  └───────────────┘  │ (SLOW!)   │  └───────────────┘  │                                                     │
 *  │  └─────────────────────┘           └─────────────────────┘                                                     │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 *  THE SOLUTION: THREADS (Lightweight Processes)
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │  1980s-1990s: Birth of Threads                                                                                 │
 *  │  ────────────────────────────────                                                                              │
 *  │  Key Insight: Multiple execution paths WITHIN a single process                                                 │
 *  │  • Share memory space (communication is FAST - just read/write shared variables)                               │
 *  │  • Share file descriptors, code, static data                                                                   │
 *  │  • Each thread has its own: Stack, Registers, Program Counter                                                  │
 *  │                                                                                                                │
 *  │                   ┌─────────────────────────────────────────────────┐                                          │
 *  │                   │              PROCESS                            │                                          │
 *  │                   │  ┌─────────────────────────────────────────┐    │                                          │
 *  │                   │  │         SHARED RESOURCES                │    │                                          │
 *  │                   │  │  • Code Segment                         │    │                                          │
 *  │                   │  │  • Data Segment (Global Variables)      │    │                                          │
 *  │                   │  │  • Heap Memory                          │    │                                          │
 *  │                   │  │  • Open Files                           │    │                                          │
 *  │                   │  └─────────────────────────────────────────┘    │                                          │
 *  │                   │                                                 │                                          │
 *  │                   │  ┌──────────┐ ┌──────────┐ ┌──────────┐        │                                          │
 *  │                   │  │ Thread 1 │ │ Thread 2 │ │ Thread 3 │        │                                          │
 *  │                   │  │──────────│ │──────────│ │──────────│        │                                          │
 *  │                   │  │ Stack    │ │ Stack    │ │ Stack    │        │                                          │
 *  │                   │  │ Registers│ │ Registers│ │ Registers│        │                                          │
 *  │                   │  │ PC       │ │ PC       │ │ PC       │        │                                          │
 *  │                   │  └──────────┘ └──────────┘ └──────────┘        │                                          │
 *  │                   └─────────────────────────────────────────────────┘                                          │
 *  │                                                                                                                │
 *  │  Benefits:                                                                                                     │
 *  │  • Thread creation: ~100 CPU cycles (vs 10,000+ for process)                                                   │
 *  │  • Context switch: ~100 cycles (no TLB flush, same address space)                                              │
 *  │  • Communication: Direct memory access (no kernel involvement)                                                 │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                3. CPU ARCHITECTURE DEEP DIVE                                                   ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  BASIC CPU COMPONENTS:
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │  ┌─────────────────────────────────────────────────────────────────────────────┐                               │
 *  │  │                              CPU (Processor)                                │                               │
 *  │  │                                                                             │                               │
 *  │  │  ┌────────────────────────────────────────┐  ┌──────────────────────────┐   │                               │
 *  │  │  │         CONTROL UNIT (CU)              │  │    REGISTERS             │   │                               │
 *  │  │  │  • Fetches instructions                │  │  • Program Counter (PC)  │   │                               │
 *  │  │  │  • Decodes instructions                │  │  • Stack Pointer (SP)    │   │                               │
 *  │  │  │  • Controls execution flow             │  │  • General Purpose Regs  │   │                               │
 *  │  │  │  • Manages instruction pipeline        │  │  • Status Register       │   │                               │
 *  │  │  └────────────────────────────────────────┘  └──────────────────────────┘   │                               │
 *  │  │                                                                             │                               │
 *  │  │  ┌────────────────────────────────────────┐  ┌──────────────────────────┐   │                               │
 *  │  │  │         ALU (Arithmetic Logic Unit)    │  │    CACHE (L1/L2)         │   │                               │
 *  │  │  │  • Addition, Subtraction               │  │  • L1 Data Cache         │   │                               │
 *  │  │  │  • Multiplication, Division            │  │  • L1 Instruction Cache  │   │                               │
 *  │  │  │  • AND, OR, XOR, NOT                   │  │  • L2 Unified Cache      │   │                               │
 *  │  │  │  • Comparison operations               │  │  • Faster than RAM!      │   │                               │
 *  │  │  └────────────────────────────────────────┘  └──────────────────────────┘   │                               │
 *  │  │                                                                             │                               │
 *  │  └─────────────────────────────────────────────────────────────────────────────┘                               │
 *  │                                                                                                                │
 *  │  KEY REGISTERS FOR THREAD EXECUTION:                                                                           │
 *  │  ┌────────────────────────┬────────────────────────────────────────────────────────────────┐                   │
 *  │  │  Register              │  Purpose                                                       │                   │
 *  │  ├────────────────────────┼────────────────────────────────────────────────────────────────┤                   │
 *  │  │  Program Counter (PC)  │  Address of NEXT instruction to execute                        │                   │
 *  │  │  Stack Pointer (SP)    │  Top of current stack (for function calls, local vars)         │                   │
 *  │  │  Base Pointer (BP)     │  Base of current stack frame                                   │                   │
 *  │  │  General Purpose       │  RAX, RBX, RCX, RDX... (x86-64) - hold data being processed    │                   │
 *  │  │  Status/Flags          │  Zero flag, Carry flag, Overflow flag (ALU results)            │                   │
 *  │  └────────────────────────┴────────────────────────────────────────────────────────────────┘                   │
 *  │                                                                                                                │
 *  │  THREAD CONTEXT = The set of register values that define the thread's execution state                          │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 *  MEMORY HIERARCHY (Speed vs Size Tradeoff):
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │                        ┌───────────────┐                                                                       │
 *  │                        │   REGISTERS   │  ← Fastest (0 cycles), ~1KB                                           │
 *  │                        │   (in CPU)    │                                                                       │
 *  │                        └───────┬───────┘                                                                       │
 *  │                                │                                                                               │
 *  │                        ┌───────▼───────┐                                                                       │
 *  │                        │   L1 CACHE    │  ← ~1-4 cycles, ~64KB per core                                        │
 *  │                        │   (per core)  │                                                                       │
 *  │                        └───────┬───────┘                                                                       │
 *  │                                │                                                                               │
 *  │                        ┌───────▼───────┐                                                                       │
 *  │                        │   L2 CACHE    │  ← ~10-20 cycles, ~256KB-1MB per core                                 │
 *  │                        │   (per core)  │                                                                       │
 *  │                        └───────┬───────┘                                                                       │
 *  │                                │                                                                               │
 *  │                        ┌───────▼───────┐                                                                       │
 *  │                        │   L3 CACHE    │  ← ~40-75 cycles, ~8-64MB shared                                      │
 *  │                        │   (shared)    │                                                                       │
 *  │                        └───────┬───────┘                                                                       │
 *  │                                │                                                                               │
 *  │                        ┌───────▼───────┐                                                                       │
 *  │                        │   MAIN RAM    │  ← ~100-300 cycles, ~8-128GB                                          │
 *  │                        │   (DDR4/DDR5) │                                                                       │
 *  │                        └───────┬───────┘                                                                       │
 *  │                                │                                                                               │
 *  │                        ┌───────▼───────┐                                                                       │
 *  │                        │   SSD/DISK    │  ← ~10,000-1,000,000+ cycles, TBs                                     │
 *  │                        │   (Storage)   │                                                                       │
 *  │                        └───────────────┘                                                                       │
 *  │                                                                                                                │
 *  │  WHY THIS MATTERS FOR THREADS:                                                                                 │
 *  │  • Threads sharing data in L1/L2 cache = EXTREMELY FAST communication                                          │
 *  │  • Threads on different cores = Need L3 or RAM (slower)                                                        │
 *  │  • Cache coherence protocols ensure data consistency (MESI protocol)                                           │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                4. CONTEXT SWITCHING - THE MAGIC BEHIND MULTITHREADING                          ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  WHAT HAPPENS DURING A CONTEXT SWITCH:
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │  Thread A running...                                                                                           │
 *  │       │                                                                                                        │
 *  │       ▼                                                                                                        │
 *  │  ┌────────────────────────────────────────────────────────────────────┐                                        │
 *  │  │ 1. TRIGGER (Timer interrupt, I/O wait, yield(), or higher priority)│                                        │
 *  │  └────────────────────────────────────────────────────────────────────┘                                        │
 *  │       │                                                                                                        │
 *  │       ▼                                                                                                        │
 *  │  ┌────────────────────────────────────────────────────────────────────┐                                        │
 *  │  │ 2. SAVE Thread A's Context (to Thread Control Block - TCB)         │                                        │
 *  │  │    • All CPU registers (RAX, RBX, RCX, RDX, RSI, RDI...)          │                                        │
 *  │  │    • Program Counter (where to resume)                             │                                        │
 *  │  │    • Stack Pointer (current stack position)                        │                                        │
 *  │  │    • Status/Flags register                                         │                                        │
 *  │  │    • FPU/SIMD registers (if used)                                  │                                        │
 *  │  └────────────────────────────────────────────────────────────────────┘                                        │
 *  │       │                                                                                                        │
 *  │       ▼                                                                                                        │
 *  │  ┌────────────────────────────────────────────────────────────────────┐                                        │
 *  │  │ 3. SCHEDULER selects next thread (Thread B)                        │                                        │
 *  │  │    • Round-robin, Priority-based, CFS, etc.                        │                                        │
 *  │  └────────────────────────────────────────────────────────────────────┘                                        │
 *  │       │                                                                                                        │
 *  │       ▼                                                                                                        │
 *  │  ┌────────────────────────────────────────────────────────────────────┐                                        │
 *  │  │ 4. RESTORE Thread B's Context (from its TCB)                       │                                        │
 *  │  │    • Load all registers with Thread B's values                     │                                        │
 *  │  │    • Set PC to Thread B's last position                            │                                        │
 *  │  │    • Point SP to Thread B's stack                                  │                                        │
 *  │  └────────────────────────────────────────────────────────────────────┘                                        │
 *  │       │                                                                                                        │
 *  │       ▼                                                                                                        │
 *  │  Thread B resumes execution exactly where it left off!                                                         │
 *  │                                                                                                                │
 *  │                                                                                                                │
 *  │  THREAD CONTROL BLOCK (TCB) - One per thread:                                                                  │
 *  │  ┌──────────────────────────────────────────────────────────────────────────────────────────┐                  │
 *  │  │  TCB for Thread X                                                                       │                  │
 *  │  │  ┌─────────────────────────────────────────────────────────────────────────────────────┐│                  │
 *  │  │  │ Thread ID         │  Unique identifier                                             ││                  │
 *  │  │  │ Thread State      │  RUNNING / READY / BLOCKED / WAITING                           ││                  │
 *  │  │  │ Program Counter   │  Next instruction address                                      ││                  │
 *  │  │  │ Stack Pointer     │  Current stack top                                             ││                  │
 *  │  │  │ CPU Registers     │  All register values                                           ││                  │
 *  │  │  │ Priority          │  Scheduling priority                                           ││                  │
 *  │  │  │ Parent Process    │  Which process owns this thread                                ││                  │
 *  │  │  │ Pointer to Stack  │  Memory location of thread's stack                             ││                  │
 *  │  │  └─────────────────────────────────────────────────────────────────────────────────────┘│                  │
 *  │  └──────────────────────────────────────────────────────────────────────────────────────────┘                  │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                5. SINGLE-CORE vs MULTI-CORE                                                    ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  SINGLE-CORE CPU: CONCURRENCY (not parallelism)
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │  Only ONE thread executes at any instant. "Multithreading" is an ILLUSION!                                     │
 *  │                                                                                                                │
 *  │  Time →                                                                                                        │
 *  │  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────┐       │
 *  │  │ T1 │ T2 │ T1 │ T3 │ T1 │ T2 │ T3 │ T1 │ T2 │ T3 │ T1 │ T2 │ T1 │ T3 │                              │       │
 *  │  └─────────────────────────────────────────────────────────────────────────────────────────────────────┘       │
 *  │    ↑                                                                                                           │
 *  │    Rapid switching creates ILLUSION of simultaneous execution                                                  │
 *  │    (Time slice = ~1-10 milliseconds typically)                                                                 │
 *  │                                                                                                                │
 *  │  Why even have threads on single-core?                                                                         │
 *  │  • I/O Wait: While Thread A waits for disk, Thread B can run                                                   │
 *  │  • Responsiveness: UI thread remains responsive while worker processes                                         │
 *  │  • Code Organization: Separate concerns into different threads                                                 │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 *  MULTI-CORE CPU: TRUE PARALLELISM
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │  Multiple threads execute SIMULTANEOUSLY on different cores!                                                   │
 *  │                                                                                                                │
 *  │  ┌───────────────────────────────────────────────────────────────────────────────────┐                         │
 *  │  │                         Multi-Core CPU                                            │                         │
 *  │  │                                                                                   │                         │
 *  │  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐  │                         │
 *  │  │  │     CORE 0      │  │     CORE 1      │  │     CORE 2      │  │    CORE 3    │  │                         │
 *  │  │  │  ┌───────────┐  │  │  ┌───────────┐  │  │  ┌───────────┐  │  │ ┌───────────┐│  │                         │
 *  │  │  │  │ Thread A  │  │  │  │ Thread B  │  │  │  │ Thread C  │  │  │ │ Thread D  ││  │                         │
 *  │  │  │  │ (running) │  │  │  │ (running) │  │  │  │ (running) │  │  │ │ (running) ││  │                         │
 *  │  │  │  └───────────┘  │  │  └───────────┘  │  │  └───────────┘  │  │ └───────────┘│  │                         │
 *  │  │  │  L1/L2 Cache    │  │  L1/L2 Cache    │  │  L1/L2 Cache    │  │ L1/L2 Cache  │  │                         │
 *  │  │  └─────────────────┘  └─────────────────┘  └─────────────────┘  └──────────────┘  │                         │
 *  │  │                                                                                   │                         │
 *  │  │  ┌───────────────────────────────────────────────────────────────────────────┐    │                         │
 *  │  │  │                          L3 CACHE (Shared)                                │    │                         │
 *  │  │  └───────────────────────────────────────────────────────────────────────────┘    │                         │
 *  │  └───────────────────────────────────────────────────────────────────────────────────┘                         │
 *  │                                                                                                                │
 *  │  Time →                                                                                                        │
 *  │  Core 0: │████ Thread A ████│████ Thread E ████│████ Thread A ████│                                            │
 *  │  Core 1: │████ Thread B ████│████ Thread B ████│████ Thread F ████│                                            │
 *  │  Core 2: │████ Thread C ████│████ Thread C ████│████ Thread C ████│                                            │
 *  │  Core 3: │████ Thread D ████│████ Thread G ████│████ Thread D ████│                                            │
 *  │                                                                                                                │
 *  │  → 4 threads running TRULY in parallel at any moment!                                                          │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                6. HYPER-THREADING (SMT)                                                        ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  HYPER-THREADING / SMT (Simultaneous Multi-Threading):
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │  Intel's Hyper-Threading / AMD's SMT: One physical core appears as TWO logical cores                           │
 *  │                                                                                                                │
 *  │  HOW IT WORKS:                                                                                                 │
 *  │  ┌────────────────────────────────────────────────────────────────────────────────────────────────┐            │
 *  │  │                              Physical Core                                                     │            │
 *  │  │                                                                                                │            │
 *  │  │  ┌───────────────────────┐  ┌───────────────────────┐      ┌─────────────────────────────────┐ │            │
 *  │  │  │   Thread State 1      │  │   Thread State 2      │      │     SHARED RESOURCES            │ │            │
 *  │  │  │  (Logical Core 0)     │  │  (Logical Core 1)     │      │  ┌──────────────────────────┐   │ │            │
 *  │  │  │  ┌────────────────┐   │  │  ┌────────────────┐   │      │  │ Execution Units (ALU)    │   │ │            │
 *  │  │  │  │ PC, SP, Regs   │   │  │  │ PC, SP, Regs   │   │      │  │ Floating Point Unit      │   │ │            │
 *  │  │  │  │ (Dedicated)    │   │  │  │ (Dedicated)    │   │      │  │ Cache (L1, L2)           │   │ │            │
 *  │  │  │  └────────────────┘   │  │  └────────────────┘   │      │  │ Branch Predictor         │   │ │            │
 *  │  │  └───────────────────────┘  └───────────────────────┘      │  └──────────────────────────┘   │ │            │
 *  │  │                                                            └─────────────────────────────────┘ │            │
 *  │  └────────────────────────────────────────────────────────────────────────────────────────────────┘            │
 *  │                                                                                                                │
 *  │  WHY IT HELPS:                                                                                                 │
 *  │  • When Thread 1 stalls (cache miss, memory fetch), Thread 2 uses the execution units                          │
 *  │  • Keeps CPU pipeline busy instead of waiting                                                                  │
 *  │  • ~15-30% performance improvement (NOT 100% - resources are shared!)                                          │
 *  │                                                                                                                │
 *  │  Without HT:                With HT:                                                                           │
 *  │  ┌────────────────────┐     ┌────────────────────┐                                                             │
 *  │  │ T1 │STALL│ T1 │STL│     │ T1 │ T2  │ T1 │ T2 │                                                             │
 *  │  └────────────────────┘     └────────────────────┘                                                             │
 *  │       ↑ CPU idle            ↑ Thread 2 fills the gaps!                                                         │
 *  │                                                                                                                │
 *  │  EXAMPLE: 4-core CPU with Hyper-Threading                                                                      │
 *  │  • 4 Physical cores                                                                                            │
 *  │  • 8 Logical cores (what OS sees)                                                                              │
 *  │  • Can run 8 threads "simultaneously"                                                                          │
 *  │  • In Java: Runtime.getRuntime().availableProcessors() returns 8                                               │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                7. KERNEL THREADS vs USER THREADS                                               ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  THREAD IMPLEMENTATION MODELS:
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │  1. USER-LEVEL THREADS (Green Threads)                                                                         │
 *  │  ───────────────────────────────────────                                                                       │
 *  │  • Managed entirely by user-space library                                                                      │
 *  │  • Kernel doesn't know they exist!                                                                             │
 *  │  • Fast creation/switching (no kernel calls)                                                                   │
 *  │  • BUT: One thread blocks → entire process blocks                                                              │
 *  │  • BUT: Cannot use multiple CPU cores                                                                          │
 *  │                                                                                                                │
 *  │     User Space    │   ┌────────────────────────────────────────────┐                                           │
 *  │                   │   │          Thread Library                    │                                           │
 *  │                   │   │  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐      │                                           │
 *  │                   │   │  │ UT1  │ │ UT2  │ │ UT3  │ │ UT4  │      │                                           │
 *  │                   │   │  └──────┘ └──────┘ └──────┘ └──────┘      │                                           │
 *  │                   │   └─────────────────┬──────────────────────────┘                                           │
 *  │  ─────────────────┼─────────────────────│────────────────────────────────                                      │
 *  │     Kernel Space  │                     ▼                                                                      │
 *  │                   │   ┌────────────────────────────────────────────┐                                           │
 *  │                   │   │     ONE Kernel Thread (Process)            │                                           │
 *  │                   │   └────────────────────────────────────────────┘                                           │
 *  │                                                                                                                │
 *  │                                                                                                                │
 *  │  2. KERNEL-LEVEL THREADS (Native Threads)                                                                      │
 *  │  ─────────────────────────────────────────                                                                     │
 *  │  • Managed by the operating system kernel                                                                      │
 *  │  • Kernel aware of each thread                                                                                 │
 *  │  • Can utilize multiple CPU cores                                                                              │
 *  │  • One thread blocks → others continue                                                                         │
 *  │  • Slower creation/switching (kernel calls)                                                                    │
 *  │                                                                                                                │
 *  │     User Space    │   ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐                                                      │
 *  │                   │   │ App  │ │ App  │ │ App  │ │ App  │                                                      │
 *  │                   │   │ Code │ │ Code │ │ Code │ │ Code │                                                      │
 *  │                   │   └──┬───┘ └──┬───┘ └──┬───┘ └──┬───┘                                                      │
 *  │  ─────────────────┼──────│────────│────────│────────│──────────────────                                        │
 *  │     Kernel Space  │      ▼        ▼        ▼        ▼                                                          │
 *  │                   │   ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐                                                      │
 *  │                   │   │ KT1  │ │ KT2  │ │ KT3  │ │ KT4  │                                                      │
 *  │                   │   └──────┘ └──────┘ └──────┘ └──────┘                                                      │
 *  │                                                                                                                │
 *  │                                                                                                                │
 *  │  3. HYBRID (M:N) MODEL - Best of both worlds                                                                   │
 *  │  ──────────────────────────────────────────                                                                    │
 *  │  • M user threads mapped to N kernel threads (M >= N)                                                          │
 *  │  • User-level scheduling for fast switching                                                                    │
 *  │  • Kernel threads for true parallelism                                                                         │
 *  │  • Complex to implement                                                                                        │
 *  │                                                                                                                │
 *  │     User Space    │   ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐                                    │
 *  │                   │   │ UT1  │ │ UT2  │ │ UT3  │ │ UT4  │ │ UT5  │ │ UT6  │                                    │
 *  │                   │   └──┬───┘ └──┬───┘ └──┬───┘ └──┬───┘ └──┬───┘ └──┬───┘                                    │
 *  │                   │      └────┬───┘        └───┬────┘        └───┬────┘                                        │
 *  │  ─────────────────┼───────────│───────────────│─────────────────│──────────                                    │
 *  │     Kernel Space  │           ▼               ▼                 ▼                                              │
 *  │                   │       ┌──────┐        ┌──────┐          ┌──────┐                                           │
 *  │                   │       │ KT1  │        │ KT2  │          │ KT3  │                                           │
 *  │                   │       └──────┘        └──────┘          └──────┘                                           │
 *  │                                                                                                                │
 *  │                                                                                                                │
 *  │  JAVA'S THREAD MODEL:                                                                                          │
 *  │  • Early Java (Green Threads): User-level threads                                                              │
 *  │  • Modern Java: 1:1 mapping to native OS threads (kernel threads)                                              │
 *  │  • Java 21+: Virtual Threads (M:N model with millions of threads!)                                             │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                8. CACHE COHERENCE & MEMORY BARRIERS                                            ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  THE CACHE COHERENCE PROBLEM:
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │  When multiple cores have cached copies of the same memory location:                                           │
 *  │                                                                                                                │
 *  │     Core 0 Cache           Core 1 Cache            Main Memory                                                 │
 *  │     ┌──────────┐           ┌──────────┐            ┌──────────┐                                                │
 *  │     │ X = 100  │           │ X = 100  │            │ X = 100  │                                                │
 *  │     └──────────┘           └──────────┘            └──────────┘                                                │
 *  │                                                                                                                │
 *  │  Core 0 updates X:                                                                                             │
 *  │     Core 0 Cache           Core 1 Cache            Main Memory                                                 │
 *  │     ┌──────────┐           ┌──────────┐            ┌──────────┐                                                │
 *  │     │ X = 200  │           │ X = 100  │ ← STALE!   │ X = ???  │                                                │
 *  │     └──────────┘           └──────────┘            └──────────┘                                                │
 *  │                                                                                                                │
 *  │  MESI Protocol ensures coherence:                                                                              │
 *  │  • M (Modified): Cache has modified data, must write back                                                      │
 *  │  • E (Exclusive): Cache has clean data, only copy                                                              │
 *  │  • S (Shared): Multiple caches have clean copies                                                               │
 *  │  • I (Invalid): Cache line is invalid                                                                          │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 *  MEMORY BARRIERS (FENCES):
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │  CPUs reorder instructions for performance. Memory barriers prevent this:                                      │
 *  │                                                                                                                │
 *  │  Without barrier (CPU reorders):     With barrier (order enforced):                                            │
 *  │  ┌─────────────────────────────┐     ┌─────────────────────────────┐                                           │
 *  │  │ 1. data = 42;               │     │ 1. data = 42;               │                                           │
 *  │  │ 2. ready = true;            │     │ 2. MEMORY_BARRIER           │                                           │
 *  │  │                             │     │ 3. ready = true;            │                                           │
 *  │  │ CPU might execute 2 before 1│     │ 2 MUST happen before 3      │                                           │
 *  │  └─────────────────────────────┘     └─────────────────────────────┘                                           │
 *  │                                                                                                                │
 *  │  In Java:                                                                                                      │
 *  │  • volatile: Guarantees visibility AND ordering (memory barrier)                                               │
 *  │  • synchronized: Implicit memory barriers at entry/exit                                                        │
 *  │  • Atomic classes: Use memory barriers internally                                                              │
 *  │                                                                                                                │
 *  │  This is WHY volatile matters at the hardware level!                                                           │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                9. CPU SCHEDULING ALGORITHMS                                                    ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  HOW THE OS DECIDES WHICH THREAD RUNS:
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │  1. ROUND ROBIN (Time Slicing)                                                                                 │
 *  │  ────────────────────────────────                                                                              │
 *  │  Each thread gets equal time slice (quantum), then moves to back of queue.                                     │
 *  │                                                                                                                │
 *  │    Queue: [T1] → [T2] → [T3] → [T1] → [T2] → [T3] → ...                                                        │
 *  │                                                                                                                │
 *  │                                                                                                                │
 *  │  2. PRIORITY SCHEDULING                                                                                        │
 *  │  ────────────────────────────                                                                                  │
 *  │  Higher priority threads run first.                                                                            │
 *  │                                                                                                                │
 *  │    High Priority: [T1] → [T3] → [T1] → [T3] → ...                                                              │
 *  │    Low Priority:  [T2] (starving!)                                                                             │
 *  │                                                                                                                │
 *  │  Problem: Starvation. Solution: Priority aging (increase priority over time)                                   │
 *  │                                                                                                                │
 *  │                                                                                                                │
 *  │  3. COMPLETELY FAIR SCHEDULER (CFS) - Linux                                                                    │
 *  │  ────────────────────────────────────────────                                                                  │
 *  │  Tracks "virtual runtime" - how much CPU time each thread has used.                                            │
 *  │  Always runs thread with LOWEST virtual runtime.                                                               │
 *  │  Uses Red-Black tree for O(log n) scheduling.                                                                  │
 *  │                                                                                                                │
 *  │    Virtual Runtime: T1=50ms, T2=30ms, T3=45ms                                                                  │
 *  │    → T2 runs next (lowest vruntime)                                                                            │
 *  │                                                                                                                │
 *  │                                                                                                                │
 *  │  4. MULTILEVEL FEEDBACK QUEUE                                                                                  │
 *  │  ──────────────────────────────                                                                                │
 *  │  Multiple queues with different priorities and time slices.                                                    │
 *  │  Threads move between queues based on behavior.                                                                │
 *  │                                                                                                                │
 *  │    Queue 0 (Highest): Short quantum, interactive tasks                                                         │
 *  │    Queue 1 (Medium):  Medium quantum                                                                           │
 *  │    Queue 2 (Lowest):  Long quantum, CPU-bound tasks                                                            │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                               10. THREAD STACK - DEEP DIVE                                                     ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  EACH THREAD HAS ITS OWN STACK:
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │   HIGH MEMORY                                                                                                  │
 *  │        ▲                                                                                                       │
 *  │        │     ┌─────────────────────────────────────────────────┐                                               │
 *  │        │     │            Thread 1 Stack                       │                                               │
 *  │        │     │  ┌───────────────────────────────────────────┐  │                                               │
 *  │        │     │  │ Stack Frame: main()                       │  │                                               │
 *  │        │     │  │   - Return address                        │  │                                               │
 *  │        │     │  │   - Local variables                       │  │                                               │
 *  │        │     │  │   - Parameters                            │  │                                               │
 *  │        │     │  ├───────────────────────────────────────────┤  │                                               │
 *  │        │     │  │ Stack Frame: methodA()                    │  │                                               │
 *  │        │     │  │   - Return address                        │  │                                               │
 *  │        │     │  │   - Local variables                       │  │                                               │
 *  │        │     │  ├───────────────────────────────────────────┤  │                                               │
 *  │        │     │  │ Stack Frame: methodB()                    │  │  ← Stack Pointer (SP)                         │
 *  │        │     │  │   - Return address                        │  │                                               │
 *  │        │     │  │   - Local variables                       │  │                                               │
 *  │        │     │  └───────────────────────────────────────────┘  │                                               │
 *  │        │     │                  ▼ (grows down)                 │                                               │
 *  │        │     │           [available space]                     │                                               │
 *  │        │     └─────────────────────────────────────────────────┘                                               │
 *  │        │                                                                                                       │
 *  │        │     ┌─────────────────────────────────────────────────┐                                               │
 *  │        │     │            Thread 2 Stack                       │                                               │
 *  │        │     │  (Same structure, completely independent)       │                                               │
 *  │        │     └─────────────────────────────────────────────────┘                                               │
 *  │        │                                                                                                       │
 *  │   LOW MEMORY                                                                                                   │
 *  │                                                                                                                │
 *  │  DEFAULT STACK SIZES:                                                                                          │
 *  │  • Linux: 8 MB (ulimit -s)                                                                                     │
 *  │  • Windows: 1 MB                                                                                               │
 *  │  • JVM: 256KB - 1MB (platform dependent, -Xss to configure)                                                    │
 *  │                                                                                                                │
 *  │  STACK OVERFLOW: When recursion goes too deep or too many local variables!                                     │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                               11. JAVA VIRTUAL THREADS (Project Loom - Java 21+)                               ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  THE EVOLUTION: Platform Threads → Virtual Threads
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │  PLATFORM THREADS (Traditional)                                                                                │
 *  │  ─────────────────────────────────                                                                             │
 *  │  • 1:1 with OS threads                                                                                         │
 *  │  • Heavy: ~1MB stack each                                                                                      │
 *  │  • Limited: ~thousands on a system                                                                             │
 *  │  • Expensive context switching                                                                                 │
 *  │                                                                                                                │
 *  │                                                                                                                │
 *  │  VIRTUAL THREADS (Java 21+)                                                                                    │
 *  │  ────────────────────────────                                                                                  │
 *  │  • M:N model (millions of virtual threads : few carrier threads)                                               │
 *  │  • Lightweight: ~few KB each                                                                                   │
 *  │  • Can have MILLIONS!                                                                                          │
 *  │  • JVM manages scheduling (not OS)                                                                             │
 *  │                                                                                                                │
 *  │     ┌──────────────────────────────────────────────────────────────────────┐                                   │
 *  │     │                        JVM                                           │                                   │
 *  │     │  ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐ ... (millions)     │                                   │
 *  │     │  │VT 1 │ │VT 2 │ │VT 3 │ │VT 4 │ │VT 5 │ │VT 6 │                    │                                   │
 *  │     │  └──┬──┘ └──┬──┘ └──┬──┘ └──┬──┘ └──┬──┘ └──┬──┘                    │                                   │
 *  │     │     └───────┼───────┴───────┼───────┴───────┘                        │                                   │
 *  │     │             │               │                                        │                                   │
 *  │     │        ┌────▼────┐     ┌────▼────┐                                   │                                   │
 *  │     │        │Carrier  │     │Carrier  │  (ForkJoinPool)                   │                                   │
 *  │     │        │Thread 1 │     │Thread 2 │                                   │                                   │
 *  │     │        └────┬────┘     └────┬────┘                                   │                                   │
 *  │     └─────────────│───────────────│────────────────────────────────────────┘                                   │
 *  │                   │               │                                                                            │
 *  │     ┌─────────────▼───────────────▼────────────────────────────────────────┐                                   │
 *  │     │                      OS Kernel                                       │                                   │
 *  │     │           ┌─────────┐  ┌─────────┐                                   │                                   │
 *  │     │           │OS Thread│  │OS Thread│                                   │                                   │
 *  │     │           └─────────┘  └─────────┘                                   │                                   │
 *  │     └──────────────────────────────────────────────────────────────────────┘                                   │
 *  │                                                                                                                │
 *  │  Creating Virtual Threads:                                                                                     │
 *  │  Thread.startVirtualThread(() -> { ... });                                                                     │
 *  │  Executors.newVirtualThreadPerTaskExecutor();                                                                  │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * @author Thread & Hardware Deep Dive
 * @see _03_MultithreadingConcurrency for Java-specific threading concepts
 */
public class _01_ThreadsFromHardware {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║              THREADS FROM HARDWARE - DEMONSTRATION                                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════════════╝\n");

        // 1. Show available processors (logical cores)
        showSystemInfo();

        // 2. Demonstrate context switch overhead
        demonstrateContextSwitchOverhead();

        // 3. Demonstrate thread stack isolation
        demonstrateStackIsolation();

        // 4. Demonstrate cache effects
        demonstrateCacheEffects();

        // 5. Demonstrate virtual threads (Java 21+)
        demonstrateVirtualThreads();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         1. SYSTEM INFORMATION
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void showSystemInfo() {
        System.out.println("▶ 1. SYSTEM INFORMATION:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        int processors = Runtime.getRuntime().availableProcessors();
        long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");

        System.out.println("   Available Processors (Logical Cores): " + processors);
        System.out.println("   Max JVM Memory: " + maxMemory + " MB");
        System.out.println("   OS: " + osName + " (" + osArch + ")");
        System.out.println();

        // This shows if Hyper-Threading is likely enabled
        if (processors % 2 == 0 && processors >= 4) {
            System.out.println("   💡 If you have " + (processors / 2) + " physical cores, Hyper-Threading is likely enabled!");
        }
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         2. CONTEXT SWITCH OVERHEAD
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateContextSwitchOverhead() {
        System.out.println("▶ 2. CONTEXT SWITCH OVERHEAD DEMONSTRATION:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        int iterations = 1_000_000;

        // Single thread execution
        long startSingle = System.nanoTime();
        long counter1 = 0;
        for (int i = 0; i < iterations; i++) {
            counter1++;
        }
        long endSingle = System.nanoTime();
        long singleTime = endSingle - startSingle;

        // Use counter1 to prevent compiler optimization
        if (counter1 < 0) System.out.println("Never happens");

        // Two threads with frequent yielding (forcing context switches)
        long[] counter2 = {0};
        long[] counter3 = {0};

        long startMulti = System.nanoTime();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < iterations / 2; i++) {
                counter2[0]++;
                if (i % 100 == 0) Thread.yield(); // Force potential context switch
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < iterations / 2; i++) {
                counter3[0]++;
                if (i % 100 == 0) Thread.yield();
            }
        });

        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        long endMulti = System.nanoTime();
        long multiTime = endMulti - startMulti;

        System.out.println("   Single thread time: " + (singleTime / 1_000_000.0) + " ms");
        System.out.println("   Two threads time:   " + (multiTime / 1_000_000.0) + " ms");
        System.out.println("   Overhead ratio:     " + String.format("%.2f", (double) multiTime / singleTime) + "x");
        System.out.println();
        System.out.println("   💡 Context switching adds overhead! For CPU-bound tasks,");
        System.out.println("      more threads != always faster.");
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         3. STACK ISOLATION
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateStackIsolation() {
        System.out.println("▶ 3. THREAD STACK ISOLATION:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // Each thread has its own stack - local variables are isolated
        Thread t1 = new Thread(() -> {
            int localVar = 100;  // This is on Thread 1's stack
            System.out.println("   Thread 1 local variable: " + localVar);
            try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            System.out.println("   Thread 1 local variable (after sleep): " + localVar);  // Still 100
        });

        Thread t2 = new Thread(() -> {
            int localVar = 999;  // Different variable, on Thread 2's stack
            System.out.println("   Thread 2 local variable: " + localVar);
            try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            System.out.println("   Thread 2 local variable (after sleep): " + localVar);  // Still 999
        });

        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println();
        System.out.println("   💡 Each thread's 'localVar' is completely independent!");
        System.out.println("      They exist in separate stack memory regions.");
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         4. CACHE EFFECTS (False Sharing)
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateCacheEffects() {
        System.out.println("▶ 4. CPU CACHE EFFECTS (False Sharing):");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // False sharing: Two threads updating adjacent memory locations
        // causes cache line invalidation
        final long[] counters = new long[16];  // All on same or adjacent cache lines
        int iterations = 10_000_000;

        // Bad: counters[0] and counters[1] are likely on the same cache line (64 bytes)
        long startBad = System.nanoTime();
        Thread t1Bad = new Thread(() -> {
            for (int i = 0; i < iterations; i++) counters[0]++;
        });
        Thread t2Bad = new Thread(() -> {
            for (int i = 0; i < iterations; i++) counters[1]++;
        });
        t1Bad.start();
        t2Bad.start();
        try {
            t1Bad.join();
            t2Bad.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        long badTime = System.nanoTime() - startBad;

        // Better: counters[0] and counters[8] are likely on different cache lines
        long startGood = System.nanoTime();
        Thread t1Good = new Thread(() -> {
            for (int i = 0; i < iterations; i++) counters[0]++;
        });
        Thread t2Good = new Thread(() -> {
            for (int i = 0; i < iterations; i++) counters[8]++;  // 8 longs apart = 64 bytes
        });
        t1Good.start();
        t2Good.start();
        try {
            t1Good.join();
            t2Good.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        long goodTime = System.nanoTime() - startGood;

        System.out.println("   Adjacent elements (false sharing):  " + (badTime / 1_000_000.0) + " ms");
        System.out.println("   Separated elements (no sharing):    " + (goodTime / 1_000_000.0) + " ms");
        System.out.println("   Performance difference: " + String.format("%.2f", (double) badTime / goodTime) + "x");
        System.out.println();
        System.out.println("   💡 False sharing occurs when threads modify different variables");
        System.out.println("      that share the same CPU cache line (typically 64 bytes).");
        System.out.println("      This causes constant cache invalidation between cores!");
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         5. VIRTUAL THREADS (Java 21+)
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateVirtualThreads() {
        System.out.println("▶ 5. VIRTUAL THREADS (Java 21+):");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // Check if virtual threads are available (Java 21+)
        int javaVersion = Runtime.version().feature();

        if (javaVersion >= 21) {
            System.out.println("   ✓ Java " + javaVersion + " detected - Virtual Threads available!");
            System.out.println();

            // Create many virtual threads using reflection (for compilation compatibility)
            int threadCount = 100_000;
            long start = System.nanoTime();

            try {
                // Use reflection to call Thread.startVirtualThread (for compile compatibility with older Java)
                java.lang.reflect.Method startVirtualThread =
                    Thread.class.getMethod("startVirtualThread", Runnable.class);

                Thread[] threads = new Thread[threadCount];
                for (int i = 0; i < threadCount; i++) {
                    Runnable task = () -> {
                        try { Thread.sleep(1); }
                        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                    };
                    threads[i] = (Thread) startVirtualThread.invoke(null, task);
                }

                // Wait for all to complete
                for (Thread thread : threads) {
                    try { thread.join(); }
                    catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }

                long elapsed = System.nanoTime() - start;
                System.out.println("   Created and ran " + threadCount + " virtual threads");
                System.out.println("   Total time: " + (elapsed / 1_000_000.0) + " ms");
                System.out.println("   Time per thread: " + (elapsed / threadCount / 1000.0) + " μs");
            } catch (Exception e) {
                System.out.println("   Error creating virtual threads: " + e.getMessage());
            }
        } else {
            System.out.println("   ⚠ Java " + javaVersion + " detected.");
            System.out.println("   Virtual Threads require Java 21+");
            System.out.println();
            System.out.println("   To use Virtual Threads, upgrade to Java 21 or later:");
            System.out.println("   • Thread.startVirtualThread(() -> { ... });");
            System.out.println("   • Executors.newVirtualThreadPerTaskExecutor();");
            System.out.println("   • Thread.ofVirtual().start(() -> { ... });");
        }
        System.out.println();
    }
}

