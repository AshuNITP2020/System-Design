package com.learning.java_interview;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                                                                                                ║
 * ║                         MULTITHREADING & CONCURRENCY - INTERVIEW DEEP DIVE                                     ║
 * ║                                                                                                                ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    1. THREAD LIFECYCLE                                                         ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │                                        THREAD LIFECYCLE                                                        │
 *  │                                                                                                                │
 *  │                              ┌─────────────┐                                                                   │
 *  │                              │     NEW     │                                                                   │
 *  │                              │ (Created)   │                                                                   │
 *  │                              └──────┬──────┘                                                                   │
 *  │                                     │ start()                                                                  │
 *  │                                     ▼                                                                          │
 *  │                              ┌─────────────┐                                                                   │
 *  │                    ┌────────►│  RUNNABLE   │◄────────┐                                                         │
 *  │                    │         │ (Ready/Run) │         │                                                         │
 *  │                    │         └──────┬──────┘         │                                                         │
 *  │                    │                │                │                                                         │
 *  │         notify()   │                │                │  sleep() done                                           │
 *  │         notifyAll()│                │                │  wait() timeout                                         │
 *  │         I/O done   │                │                │  join() done                                            │
 *  │         Lock avail │                │                │  lock acquired                                          │
 *  │                    │                │                │                                                         │
 *  │              ┌─────┴─────┐          │         ┌──────┴──────┐                                                  │
 *  │              │  WAITING  │◄─────────┼────────►│   TIMED     │                                                  │
 *  │              │           │ wait()   │ sleep() │   WAITING   │                                                  │
 *  │              │           │ join()   │ wait(t) │             │                                                  │
 *  │              └───────────┘          │ join(t) └─────────────┘                                                  │
 *  │                                     │                                                                          │
 *  │              ┌───────────┐          │                                                                          │
 *  │              │  BLOCKED  │◄─────────┤ waiting for lock                                                         │
 *  │              │           │          │                                                                          │
 *  │              └───────────┘          │                                                                          │
 *  │                                     │ run() completes                                                          │
 *  │                                     ▼                                                                          │
 *  │                              ┌─────────────┐                                                                   │
 *  │                              │ TERMINATED  │                                                                   │
 *  │                              │   (Dead)    │                                                                   │
 *  │                              └─────────────┘                                                                   │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                2. THREAD vs RUNNABLE vs CALLABLE                                               ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  ┌────────────────────────┬─────────────────────────┬─────────────────────────┬───────────────────────────────┐
 *  │        Feature         │  extends Thread         │  implements Runnable    │  implements Callable<V>       │
 *  ├────────────────────────┼─────────────────────────┼─────────────────────────┼───────────────────────────────┤
 *  │ Method                 │ run()                   │ run()                   │ call()                        │
 *  │ Return Value           │ void                    │ void                    │ V (generic)                   │
 *  │ Exception              │ Cannot throw checked    │ Cannot throw checked    │ Can throw Exception           │
 *  │ Multiple Inheritance   │ No (extends used)       │ Yes (implements)        │ Yes (implements)              │
 *  │ Future Support         │ No                      │ No                      │ Yes (with ExecutorService)    │
 *  │ Use With               │ Thread class            │ Thread, ExecutorService │ ExecutorService only          │
 *  └────────────────────────┴─────────────────────────┴─────────────────────────┴───────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                3. SYNCHRONIZED KEYWORD                                                         ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │  SYNCHRONIZED METHOD                           SYNCHRONIZED BLOCK                                              │
 *  │  ───────────────────                           ──────────────────                                              │
 *  │  • Lock on entire method                       • Lock on specific block                                        │
 *  │  • Lock on 'this' (instance method)            • Can specify any object as lock                                │
 *  │  • Lock on Class object (static method)        • More granular control                                         │
 *  │  • Less flexible                               • Better performance                                            │
 *  │                                                                                                                │
 *  │  synchronized void method() {                  void method() {                                                 │
 *  │      // entire method locked                       // code without lock                                        │
 *  │  }                                                 synchronized(lockObject) {                                  │
 *  │                                                        // only this block locked                               │
 *  │  static synchronized void staticMethod() {         }                                                           │
 *  │      // lock on Class object                       // code without lock                                        │
 *  │  }                                             }                                                               │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                4. VOLATILE vs SYNCHRONIZED vs ATOMIC                                           ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  ┌────────────────────────┬─────────────────────────┬─────────────────────────┬───────────────────────────────┐
 *  │        Feature         │       volatile          │      synchronized       │         Atomic                │
 *  ├────────────────────────┼─────────────────────────┼─────────────────────────┼───────────────────────────────┤
 *  │ Visibility             │ ✓                       │ ✓                       │ ✓                             │
 *  │ Atomicity              │ ✗ (only read/write)     │ ✓                       │ ✓                             │
 *  │ Blocking               │ No                      │ Yes                     │ No (lock-free)                │
 *  │ Compound Operations    │ Not safe (i++)          │ Safe                    │ Safe (incrementAndGet)        │
 *  │ Performance            │ Best                    │ Slowest                 │ Good                          │
 *  │ Use Case               │ Flags, single write     │ Complex critical section│ Counters, accumulators        │
 *  └────────────────────────┴─────────────────────────┴─────────────────────────┴───────────────────────────────┘
 *
 *  volatile Problem:
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │  volatile int count = 0;                                                                                       │
 *  │  count++;  // NOT ATOMIC! This is: read count → increment → write count (3 operations)                         │
 *  │                                                                                                                │
 *  │  Thread 1: read count (0) → increment (1) → [CONTEXT SWITCH]                                                   │
 *  │  Thread 2: read count (0) → increment (1) → write (1)                                                          │
 *  │  Thread 1: [RESUME] → write (1)                                                                                │
 *  │  Expected: 2, Actual: 1 (RACE CONDITION!)                                                                      │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                5. WAIT(), NOTIFY(), NOTIFYALL()                                                ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │  Method           │ Description                               │ Important Notes                               │
 *  │  ─────────────────┼───────────────────────────────────────────┼───────────────────────────────────────────────│
 *  │  wait()           │ Releases lock, waits for notify           │ Must be called inside synchronized block      │
 *  │  notify()         │ Wakes up ONE waiting thread               │ Must be called inside synchronized block      │
 *  │  notifyAll()      │ Wakes up ALL waiting threads              │ Preferred to avoid missed notifications       │
 *  │                                                                                                                │
 *  │  sleep() vs wait()                                                                                             │
 *  │  ┌─────────────────────┬──────────────────────────────────────────────────────────────────────────────────┐    │
 *  │  │      sleep()        │                        wait()                                                   │    │
 *  │  ├─────────────────────┼──────────────────────────────────────────────────────────────────────────────────┤    │
 *  │  │ Thread class method │ Object class method                                                             │    │
 *  │  │ Does NOT release    │ RELEASES lock                                                                   │    │
 *  │  │ lock                │                                                                                 │    │
 *  │  │ Wakes after time    │ Wakes on notify/notifyAll/timeout                                               │    │
 *  │  │ Can call anywhere   │ Must be in synchronized block                                                   │    │
 *  │  └─────────────────────┴──────────────────────────────────────────────────────────────────────────────────┘    │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                6. EXECUTOR FRAMEWORK                                                           ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  Thread Pool Types:
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │  newFixedThreadPool(n)      │ Fixed number of threads, unbounded queue                                         │
 *  │                             │ Use: Known number of concurrent tasks                                            │
 *  │                             │                                                                                  │
 *  │  newCachedThreadPool()      │ Creates threads as needed, reuses idle threads                                   │
 *  │                             │ Use: Many short-lived tasks                                                      │
 *  │                             │                                                                                  │
 *  │  newSingleThreadExecutor()  │ Single thread, guarantees sequential execution                                   │
 *  │                             │ Use: Tasks must run sequentially                                                 │
 *  │                             │                                                                                  │
 *  │  newScheduledThreadPool(n)  │ Fixed threads for scheduling                                                     │
 *  │                             │ Use: Periodic or delayed tasks                                                   │
 *  │                             │                                                                                  │
 *  │  newWorkStealingPool()      │ Uses ForkJoinPool, work-stealing algorithm                                       │
 *  │  (Java 8+)                  │ Use: Parallel processing, CPU-bound tasks                                        │
 *  │                             │                                                                                  │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                7. DEADLOCK                                                                     ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  Deadlock Conditions (ALL 4 required):
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │  1. Mutual Exclusion  : Resource can only be held by one thread                                                │
 *  │  2. Hold and Wait     : Thread holds resource while waiting for another                                        │
 *  │  3. No Preemption     : Resource cannot be forcibly taken                                                      │
 *  │  4. Circular Wait     : Thread 1 waits for Thread 2, Thread 2 waits for Thread 1                               │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  Deadlock Example:
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │  Thread 1                               Thread 2                                                               │
 *  │  ─────────                               ─────────                                                               │
 *  │  synchronized(lockA) {                  synchronized(lockB) {                                                  │
 *  │      // has lockA                           // has lockB                                                       │
 *  │      synchronized(lockB) {                  synchronized(lockA) {                                              │
 *  │          // waits for lockB                     // waits for lockA                                             │
 *  │      }                                      }                                                                  │
 *  │  }                                      }                                                                      │
 *  │                                                                                                                │
 *  │  DEADLOCK! Both threads waiting forever for each other's lock.                                                 │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *  Prevention:
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │  1. Lock Ordering       : Always acquire locks in same order                                                   │
 *  │  2. Lock Timeout        : Use tryLock() with timeout                                                           │
 *  │  3. Avoid Nested Locks  : Minimize holding multiple locks                                                      │
 *  │  4. Use Higher-Level    : Use concurrent utilities instead of manual synchronization                           │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                8. THREADLOCAL                                                                  ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │  ThreadLocal provides thread-local variables - each thread has its own independent copy.                       │
 *  │                                                                                                                │
 *  │  Use Cases:                                                                                                    │
 *  │  • User session in web applications                                                                            │
 *  │  • Database connections per thread                                                                             │
 *  │  • Transaction context                                                                                         │
 *  │  • SimpleDateFormat (not thread-safe)                                                                          │
 *  │                                                                                                                │
 *  │  ⚠️ WARNING: Memory leak if not removed!                                                                       │
 *  │  Always call threadLocal.remove() when done, especially in thread pools!                                       │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                9. INTERVIEW QUESTIONS                                                          ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * Q1: What is the difference between process and thread?
 * A: Process: Independent, own memory space. Thread: Lightweight, shares memory with other threads in same process.
 *
 * Q2: Can we start a thread twice?
 * A: No, IllegalThreadStateException. Once a thread completes, it cannot be restarted.
 *
 * Q3: What is daemon thread?
 * A: Background thread that doesn't prevent JVM from exiting. Set using setDaemon(true) before start().
 *
 * Q4: What is thread starvation?
 * A: When a thread cannot get CPU time because other threads are constantly getting it.
 *
 * Q5: What is livelock?
 * A: Two threads keep responding to each other's actions without making progress (like two people in a corridor).
 *
 * Q6: What is spurious wakeup?
 * A: Thread wakes up without notify/notifyAll. Always use wait() in a while loop to check condition.
 *
 * Q7: Why wait() must be in synchronized block?
 * A: To ensure condition check and wait are atomic, preventing race conditions.
 *
 * Q8: What is the difference between ReentrantLock and synchronized?
 * A: ReentrantLock offers: tryLock, interruptible lock, fairness policy, multiple conditions.
 *
 *
 * @author Java Interview Guide
 */
public class _03_MultithreadingConcurrency {

    public static void main(String[] args) throws Exception {
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════");
        System.out.println("                  MULTITHREADING & CONCURRENCY DEMONSTRATION");
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════\n");

        // 1. Thread Creation
        demonstrateThreadCreation();

        // 2. Synchronized
        demonstrateSynchronized();

        // 3. Volatile vs Atomic
        demonstrateVolatileAtomic();

        // 4. Wait/Notify (Producer-Consumer)
        demonstrateWaitNotify();

        // 5. ExecutorService
        demonstrateExecutorService();

        // 6. Locks
        demonstrateLocks();

        // 7. ThreadLocal
        demonstrateThreadLocal();

        // 8. CompletableFuture
        demonstrateCompletableFuture();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         1. THREAD CREATION METHODS
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateThreadCreation() throws InterruptedException {
        System.out.println("▶ 1. THREAD CREATION METHODS:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // Method 1: Extend Thread class
        Thread thread1 = new MyThread();
        thread1.start();

        // Method 2: Implement Runnable
        Thread thread2 = new Thread(new MyRunnable());
        thread2.start();

        // Method 3: Lambda (Java 8+)
        Thread thread3 = new Thread(() -> System.out.println("   Lambda Thread running"));
        thread3.start();

        // Wait for all threads to complete
        thread1.join();
        thread2.join();
        thread3.join();
        System.out.println();
    }

    static class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("   MyThread (extends Thread) running");
        }
    }

    static class MyRunnable implements Runnable {
        @Override
        public void run() {
            System.out.println("   MyRunnable (implements Runnable) running");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         2. SYNCHRONIZED DEMO
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateSynchronized() throws InterruptedException {
        System.out.println("▶ 2. SYNCHRONIZED DEMO (Race Condition):");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // Without synchronization
        Counter unsafeCounter = new Counter();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) unsafeCounter.incrementUnsafe();
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) unsafeCounter.incrementUnsafe();
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("   Unsafe counter (expected 20000): " + unsafeCounter.getCount());

        // With synchronization
        Counter safeCounter = new Counter();
        Thread t3 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) safeCounter.incrementSafe();
        });
        Thread t4 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) safeCounter.incrementSafe();
        });
        t3.start();
        t4.start();
        t3.join();
        t4.join();
        System.out.println("   Safe counter (expected 20000): " + safeCounter.getCount());
        System.out.println();
    }

    static class Counter {
        private int count = 0;

        // Not thread-safe!
        public void incrementUnsafe() {
            count++;  // Read → Increment → Write (not atomic)
        }

        // Thread-safe
        public synchronized void incrementSafe() {
            count++;
        }

        public int getCount() {
            return count;
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         3. VOLATILE vs ATOMIC
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateVolatileAtomic() throws InterruptedException {
        System.out.println("▶ 3. VOLATILE vs ATOMIC:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // Volatile - visibility only
        VolatileCounter volatileCounter = new VolatileCounter();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) volatileCounter.increment();
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) volatileCounter.increment();
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("   Volatile counter (expected 20000, may be less): " + volatileCounter.count);

        // AtomicInteger - atomic operations
        AtomicCounter atomicCounter = new AtomicCounter();
        Thread t3 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) atomicCounter.increment();
        });
        Thread t4 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) atomicCounter.increment();
        });
        t3.start();
        t4.start();
        t3.join();
        t4.join();
        System.out.println("   Atomic counter (expected 20000): " + atomicCounter.count.get());
        System.out.println();
    }

    static class VolatileCounter {
        volatile int count = 0;  // Visibility yes, atomicity no!

        void increment() {
            count++;  // Still not atomic!
        }
    }

    static class AtomicCounter {
        AtomicInteger count = new AtomicInteger(0);

        void increment() {
            count.incrementAndGet();  // Atomic operation!
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         4. WAIT/NOTIFY (Producer-Consumer)
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateWaitNotify() throws InterruptedException {
        System.out.println("▶ 4. PRODUCER-CONSUMER (wait/notify):");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        SharedBuffer buffer = new SharedBuffer();

        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                try {
                    buffer.produce(i);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread consumer = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                try {
                    buffer.consume();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        System.out.println();
    }

    static class SharedBuffer {
        private int data;
        private boolean hasData = false;

        public synchronized void produce(int value) throws InterruptedException {
            while (hasData) {  // Use while, not if (spurious wakeup protection)
                wait();  // Release lock and wait
            }
            data = value;
            hasData = true;
            System.out.println("   Produced: " + value);
            notify();  // Wake up consumer
        }

        public synchronized void consume() throws InterruptedException {
            while (!hasData) {
                wait();
            }
            System.out.println("   Consumed: " + data);
            hasData = false;
            notify();  // Wake up producer
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         5. EXECUTOR SERVICE
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateExecutorService() throws Exception {
        System.out.println("▶ 5. EXECUTOR SERVICE:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // Fixed thread pool
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Submit Runnable
        executor.submit(() -> System.out.println("   Runnable task executed"));

        // Submit Callable (returns result)
        Future<Integer> future = executor.submit(() -> {
            Thread.sleep(100);
            return 42;
        });
        System.out.println("   Callable result: " + future.get());

        // Submit multiple tasks
        System.out.println("   Submitting 5 tasks to pool of 3 threads:");
        for (int i = 1; i <= 5; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("   Task " + taskId + " running on " + Thread.currentThread().getName());
            });
        }

        // Shutdown properly
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         6. LOCKS (ReentrantLock, ReadWriteLock)
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateLocks() {
        System.out.println("▶ 6. LOCKS:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // ReentrantLock
        Lock lock = new ReentrantLock();
        lock.lock();
        try {
            System.out.println("   ReentrantLock acquired");
        } finally {
            lock.unlock();  // Always unlock in finally!
        }

        // TryLock with timeout
        try {
            if (lock.tryLock(100, TimeUnit.MILLISECONDS)) {
                try {
                    System.out.println("   TryLock succeeded");
                } finally {
                    lock.unlock();
                }
            } else {
                System.out.println("   TryLock failed (timeout)");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // ReadWriteLock
        ReadWriteLock rwLock = new ReentrantReadWriteLock();
        rwLock.readLock().lock();
        try {
            System.out.println("   ReadLock acquired (multiple readers allowed)");
        } finally {
            rwLock.readLock().unlock();
        }

        rwLock.writeLock().lock();
        try {
            System.out.println("   WriteLock acquired (exclusive)");
        } finally {
            rwLock.writeLock().unlock();
        }
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         7. THREADLOCAL
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateThreadLocal() throws InterruptedException {
        System.out.println("▶ 7. THREADLOCAL:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        ThreadLocal<String> threadLocal = ThreadLocal.withInitial(() -> "default");

        Thread t1 = new Thread(() -> {
            threadLocal.set("Thread-1 value");
            System.out.println("   " + Thread.currentThread().getName() + ": " + threadLocal.get());
            threadLocal.remove();  // Important! Prevent memory leak
        });

        Thread t2 = new Thread(() -> {
            threadLocal.set("Thread-2 value");
            System.out.println("   " + Thread.currentThread().getName() + ": " + threadLocal.get());
            threadLocal.remove();
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("   Main thread: " + threadLocal.get());  // Still "default"
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         8. COMPLETABLEFUTURE
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateCompletableFuture() throws Exception {
        System.out.println("▶ 8. COMPLETABLEFUTURE (Java 8+):");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // Basic async
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> "Hello");
        System.out.println("   Async result: " + cf1.get());

        // Chaining
        CompletableFuture<String> cf2 = CompletableFuture
                .supplyAsync(() -> "Hello")
                .thenApply(s -> s + " World")
                .thenApply(String::toUpperCase);
        System.out.println("   Chained result: " + cf2.get());

        // Combining multiple futures
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "World");

        CompletableFuture<String> combined = future1.thenCombine(future2, (s1, s2) -> s1 + " " + s2);
        System.out.println("   Combined result: " + combined.get());

        // Exception handling
        CompletableFuture<String> withError = CompletableFuture
                .supplyAsync(() -> {
                    if (true) throw new RuntimeException("Oops!");
                    return "Success";
                })
                .exceptionally(ex -> "Error: " + ex.getMessage());
        System.out.println("   With error handling: " + withError.get());

        System.out.println();
    }
}

