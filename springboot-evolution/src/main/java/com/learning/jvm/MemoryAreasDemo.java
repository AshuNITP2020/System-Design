package com.learning.jvm;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;

/**
 * ╔═══════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                                                                                       ║
 * ║                          JVM MEMORY AREAS - COMPLETE GUIDE                                            ║
 * ║                                                                                                       ║
 * ╚═══════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *
 * ╔═══════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                1. JVM MEMORY STRUCTURE OVERVIEW                                        ║
 * ╚═══════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * ┌────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                     JVM MEMORY                                                        │
 * ├────────────────────────────────────────────────────────────────────────────────────────────────────────┤
 * │                                                                                                        │
 * │  ┌──────────────────────────────────────────────────────────────────────────────────────────────────┐  │
 * │  │                          SHARED MEMORY (All Threads Access)                                      │  │
 * │  │                                                                                                  │  │
 * │  │  ┌────────────────────────────────┐  ┌──────────────────────────────────────────────────────┐    │  │
 * │  │  │        METHOD AREA             │  │                      HEAP                            │    │  │
 * │  │  │    (Metaspace in Java 8+)      │  │                                                      │    │  │
 * │  │  │                                │  │  ┌─────────────────────────┬──────────────────────┐  │    │  │
 * │  │  │  • Class metadata              │  │  │    YOUNG GENERATION     │    OLD GENERATION    │  │    │  │
 * │  │  │  • Static variables            │  │  │                         │     (TENURED)        │  │    │  │
 * │  │  │  • Runtime constant pool       │  │  │  ┌──────┬──────┬──────┐ │                      │  │    │  │
 * │  │  │  • Field/method info           │  │  │  │ Eden │  S0  │  S1  │ │   Long-lived         │  │    │  │
 * │  │  │  • Method bytecode             │  │  │  │      │      │      │ │   objects            │  │    │  │
 * │  │  │                                │  │  │  └──────┴──────┴──────┘ │                      │  │    │  │
 * │  │  │  JVM Flag:                     │  │  └─────────────────────────┴──────────────────────┘  │    │  │
 * │  │  │  -XX:MetaspaceSize             │  │                                                      │    │  │
 * │  │  │  -XX:MaxMetaspaceSize          │  │  JVM Flags: -Xms (initial), -Xmx (maximum)           │    │  │
 * │  │  │                                │  │                                                      │    │  │
 * │  │  └────────────────────────────────┘  └──────────────────────────────────────────────────────┘    │  │
 * │  │                                                                                                  │  │
 * │  └──────────────────────────────────────────────────────────────────────────────────────────────────┘  │
 * │                                                                                                        │
 * │  ┌──────────────────────────────────────────────────────────────────────────────────────────────────┐  │
 * │  │                          THREAD-LOCAL MEMORY (Per Thread)                                        │  │
 * │  │                                                                                                  │  │
 * │  │  ┌──────────────────────┐  ┌────────────────────────┐  ┌────────────────────────────────────┐    │  │
 * │  │  │    PC REGISTER       │  │      JVM STACK         │  │    NATIVE METHOD STACK             │    │  │
 * │  │  │                      │  │                        │  │                                    │    │  │
 * │  │  │  Current instruction │  │  One frame per method: │  │  For native (C/C++) methods       │    │  │
 * │  │  │  address being       │  │                        │  │  called via JNI                   │    │  │
 * │  │  │  executed            │  │  ┌──────────────────┐  │  │                                    │    │  │
 * │  │  │                      │  │  │ Local Variables  │  │  │  JVM Flag: -Xss                    │    │  │
 * │  │  │  (Undefined for      │  │  │ Operand Stack    │  │  │  (Same as JVM Stack)               │    │  │
 * │  │  │   native methods)    │  │  │ Frame Data       │  │  │                                    │    │  │
 * │  │  │                      │  │  └──────────────────┘  │  │                                    │    │  │
 * │  │  │                      │  │                        │  │                                    │    │  │
 * │  │  │                      │  │  JVM Flag: -Xss        │  │                                    │    │  │
 * │  │  │                      │  │  (stack size)          │  │                                    │    │  │
 * │  │  └──────────────────────┘  └────────────────────────┘  └────────────────────────────────────┘    │  │
 * │  │                                                                                                  │  │
 * │  └──────────────────────────────────────────────────────────────────────────────────────────────────┘  │
 * │                                                                                                        │
 * └────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔═══════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                2. HEAP MEMORY DETAILED                                                 ║
 * ╚═══════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * The Heap is where ALL objects and arrays are allocated.
 *
 * ┌────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                        HEAP STRUCTURE                                                  │
 * ├────────────────────────────────────────────────────────────────────────────────────────────────────────┤
 * │                                                                                                        │
 * │  ┌───────────────────────────────────────────────────┐  ┌─────────────────────────────────────────┐    │
 * │  │              YOUNG GENERATION                     │  │          OLD GENERATION                 │    │
 * │  │              (Minor GC occurs here)               │  │          (Major GC occurs here)         │    │
 * │  │                                                   │  │                                         │    │
 * │  │  ┌─────────────────────────────────────────────┐  │  │   Objects that survived multiple GCs    │    │
 * │  │  │                   EDEN                      │  │  │   are promoted here.                    │    │
 * │  │  │                                             │  │  │                                         │    │
 * │  │  │   • New objects are created here            │  │  │   Default promotion threshold: 15      │    │
 * │  │  │   • Most objects die here (short-lived)     │  │  │   (-XX:MaxTenuringThreshold)           │    │
 * │  │  │   • When full, triggers Minor GC            │  │  │                                         │    │
 * │  │  │                                             │  │  │   Large objects may go directly here    │    │
 * │  │  └─────────────────────────────────────────────┘  │  │   (-XX:PretenureSizeThreshold)          │    │
 * │  │                                                   │  │                                         │    │
 * │  │  ┌──────────────────┐  ┌──────────────────────┐   │  │                                         │    │
 * │  │  │   SURVIVOR S0    │  │    SURVIVOR S1       │   │  │                                         │    │
 * │  │  │   (From Space)   │  │    (To Space)        │   │  │                                         │    │
 * │  │  │                  │  │                      │   │  │                                         │    │
 * │  │  │  Objects that    │  │  S0 and S1 swap      │   │  │                                         │    │
 * │  │  │  survived Minor  │  │  roles after each    │   │  │                                         │    │
 * │  │  │  GC are copied   │  │  Minor GC            │   │  │                                         │    │
 * │  │  │  here            │  │                      │   │  │                                         │    │
 * │  │  │                  │  │  One is always empty │   │  │                                         │    │
 * │  │  └──────────────────┘  └──────────────────────┘   │  │                                         │    │
 * │  │                                                   │  │                                         │    │
 * │  │  -XX:NewRatio (Young:Old ratio)                   │  │                                         │    │
 * │  │  -XX:SurvivorRatio (Eden:Survivor ratio)          │  │                                         │    │
 * │  │  -Xmn (Young generation size)                     │  │                                         │    │
 * │  │                                                   │  │                                         │    │
 * │  └───────────────────────────────────────────────────┘  └─────────────────────────────────────────┘    │
 * │                                                                                                        │
 * │  Object Lifecycle in Heap:                                                                             │
 * │  ─────────────────────────                                                                             │
 * │                                                                                                        │
 * │  new Object()  ─────►  EDEN  ─────►  Minor GC  ─────►  S0/S1  ─────►  After N GCs  ─────►  OLD         │
 * │                          │              │                 │                                            │
 * │                          ▼              ▼                 ▼                                            │
 * │                     (If dead)      (If dead)          (If dead)                                        │
 * │                      Collected      Collected          Collected                                       │
 * │                                                                                                        │
 * └────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔═══════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                3. STACK MEMORY DETAILED                                                ║
 * ╚═══════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * Each thread has its own JVM Stack. Stack stores FRAMES - one per method call.
 *
 * ┌────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                    JVM STACK (Per Thread)                                              │
 * ├────────────────────────────────────────────────────────────────────────────────────────────────────────┤
 * │                                                                                                        │
 * │  When method3() calls method2() which calls method1():                                                 │
 * │                                                                                                        │
 * │           ┌────────────────────────────────────────────────────────┐                                   │
 * │           │  FRAME: method3()                       ◄─ Top (Current) │                                 │
 * │           │  ┌────────────────────────────────────────────────────┐ │                                  │
 * │           │  │ LOCAL VARIABLE ARRAY                               │ │                                  │
 * │           │  │ [0] this    (if instance method)                   │ │                                  │
 * │           │  │ [1] param1                                         │ │                                  │
 * │           │  │ [2] localVar                                       │ │                                  │
 * │           │  └────────────────────────────────────────────────────┘ │                                  │
 * │           │  ┌────────────────────────────────────────────────────┐ │                                  │
 * │           │  │ OPERAND STACK                                      │ │                                  │
 * │           │  │ (Working area for bytecode instructions)           │ │                                  │
 * │           │  │ Push/Pop operations for arithmetic, etc.           │ │                                  │
 * │           │  └────────────────────────────────────────────────────┘ │                                  │
 * │           │  ┌────────────────────────────────────────────────────┐ │                                  │
 * │           │  │ FRAME DATA                                         │ │                                  │
 * │           │  │ • Constant pool reference                          │ │                                  │
 * │           │  │ • Exception table                                  │ │                                  │
 * │           │  │ • Return address                                   │ │                                  │
 * │           │  └────────────────────────────────────────────────────┘ │                                  │
 * │           └────────────────────────────────────────────────────────┘                                   │
 * │           ┌────────────────────────────────────────────────────────┐                                   │
 * │           │  FRAME: method2()                                      │                                   │
 * │           │  ...                                                   │                                   │
 * │           └────────────────────────────────────────────────────────┘                                   │
 * │           ┌────────────────────────────────────────────────────────┐                                   │
 * │           │  FRAME: method1()                       ◄─ Bottom      │                                   │
 * │           │  ...                                                   │                                   │
 * │           └────────────────────────────────────────────────────────┘                                   │
 * │                                                                                                        │
 * │  Stack grows DOWNWARD (towards lower addresses)                                                        │
 * │  Default size: ~1MB (platform dependent)                                                               │
 * │  JVM Flag: -Xss (e.g., -Xss512k, -Xss2m)                                                              │
 * │                                                                                                        │
 * │  StackOverflowError: When stack is full (usually deep recursion)                                       │
 * │                                                                                                        │
 * └────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔═══════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                4. STACK vs HEAP COMPARISON                                             ║
 * ╚═══════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * ┌─────────────────────┬─────────────────────────────────┬─────────────────────────────────────────────────┐
 * │      Aspect         │            STACK                │                    HEAP                         │
 * ├─────────────────────┼─────────────────────────────────┼─────────────────────────────────────────────────┤
 * │ Storage             │ Local variables, references,    │ Objects, instance variables,                    │
 * │                     │ method parameters, primitives   │ arrays                                          │
 * ├─────────────────────┼─────────────────────────────────┼─────────────────────────────────────────────────┤
 * │ Scope               │ Per Thread                      │ Shared across all threads                       │
 * ├─────────────────────┼─────────────────────────────────┼─────────────────────────────────────────────────┤
 * │ Lifetime            │ Method execution duration       │ Until garbage collected                         │
 * ├─────────────────────┼─────────────────────────────────┼─────────────────────────────────────────────────┤
 * │ Access Speed        │ FASTER (LIFO, simple pointer)   │ SLOWER (random access, GC overhead)             │
 * ├─────────────────────┼─────────────────────────────────┼─────────────────────────────────────────────────┤
 * │ Size                │ Small (~1MB default)            │ Large (configurable, can be GBs)                │
 * ├─────────────────────┼─────────────────────────────────┼─────────────────────────────────────────────────┤
 * │ Allocation          │ Compile-time known              │ Runtime determined                              │
 * ├─────────────────────┼─────────────────────────────────┼─────────────────────────────────────────────────┤
 * │ Memory Management   │ Automatic (method exit)         │ Garbage Collector                               │
 * ├─────────────────────┼─────────────────────────────────┼─────────────────────────────────────────────────┤
 * │ Thread Safety       │ Thread-safe (private to thread) │ Not inherently thread-safe                      │
 * ├─────────────────────┼─────────────────────────────────┼─────────────────────────────────────────────────┤
 * │ Error               │ StackOverflowError              │ OutOfMemoryError                                │
 * ├─────────────────────┼─────────────────────────────────┼─────────────────────────────────────────────────┤
 * │ JVM Flags           │ -Xss                            │ -Xms, -Xmx, -Xmn                                │
 * └─────────────────────┴─────────────────────────────────┴─────────────────────────────────────────────────┘
 *
 *
 * @author Learning Guide
 * @see JvmArchitectureGuide
 * @see GarbageCollectionDemo
 */
public class MemoryAreasDemo {

    // Static variable - stored in Method Area (Metaspace)
    private static String staticField = "I am in Method Area (Metaspace)";

    // Instance variable - stored in Heap (inside the object)
    private String instanceField = "I am in Heap";

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════════╗");
        System.out.println("║              JVM MEMORY AREAS DEMONSTRATIONS                      ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════╝\n");

        demonstrateStackVsHeap();
        demonstrateMemoryInfo();
        demonstrateMemoryPools();
        demonstrateStackOverflow();
        demonstrateObjectAllocation();
    }

    /**
     * ═══════════════════════════════════════════════════════════════════════════════════
     * DEMO 1: Stack vs Heap - What goes where
     * ═══════════════════════════════════════════════════════════════════════════════════
     */
    private static void demonstrateStackVsHeap() {
        System.out.println("┌─────────────────────────────────────────────────────────────────┐");
        System.out.println("│  DEMO 1: Stack vs Heap - What Goes Where                        │");
        System.out.println("└─────────────────────────────────────────────────────────────────┘\n");

        // Local primitive - stored in STACK
        int primitiveLocal = 42;

        // Local reference - reference in STACK, object in HEAP
        String stringObject = "Hello World";

        // Local reference to array - reference in STACK, array in HEAP
        int[] arrayObject = {1, 2, 3, 4, 5};

        // Create an object - reference in STACK, object in HEAP
        MemoryAreasDemo demoObject = new MemoryAreasDemo();

        System.out.println("  ┌────────────────────────────────────────────────────────────────┐");
        System.out.println("  │                    MEMORY LAYOUT                               │");
        System.out.println("  └────────────────────────────────────────────────────────────────┘");
        System.out.println();
        System.out.println("  METHOD AREA (Metaspace):");
        System.out.println("  ┌────────────────────────────────────────────────────────────────┐");
        System.out.println("  │ MemoryAreasDemo.class metadata                                 │");
        System.out.println("  │ static String staticField = \"" + staticField.substring(0, 20) + "...\"│");
        System.out.println("  │ Method bytecode (main, demonstrateStackVsHeap, etc.)          │");
        System.out.println("  │ Constant pool                                                  │");
        System.out.println("  └────────────────────────────────────────────────────────────────┘");
        System.out.println();
        System.out.println("  STACK (main thread):");
        System.out.println("  ┌────────────────────────────────────────────────────────────────┐");
        System.out.println("  │ Frame: demonstrateStackVsHeap()                                │");
        System.out.println("  │   primitiveLocal = " + primitiveLocal + " (primitive - stored directly)      │");
        System.out.println("  │   stringObject = ref@" + System.identityHashCode(stringObject) + " ────────────────────────┐ │");
        System.out.println("  │   arrayObject = ref@" + System.identityHashCode(arrayObject) + " ─────────────────────────┼─┤");
        System.out.println("  │   demoObject = ref@" + System.identityHashCode(demoObject) + " ──────────────────────────┼─┼┐│");
        System.out.println("  └────────────────────────────────────────────────────────────────┼─┼┼┘");
        System.out.println("                                                                   │ ││");
        System.out.println("  HEAP:                                                            │ ││");
        System.out.println("  ┌────────────────────────────────────────────────────────────────┼─┼┼┐");
        System.out.println("  │ String \"Hello World\" ◄─────────────────────────────────────────┘ ││ │");
        System.out.println("  │ int[] {1, 2, 3, 4, 5} ◄───────────────────────────────────────────┘│ │");
        System.out.println("  │ MemoryAreasDemo object ◄──────────────────────────────────────────┘ │");
        System.out.println("  │   └── instanceField = \"I am in Heap\"                               │");
        System.out.println("  └────────────────────────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("  ═══════════════════════════════════════════════════════════════\n");
    }

    /**
     * ═══════════════════════════════════════════════════════════════════════════════════
     * DEMO 2: Runtime Memory Information
     * ═══════════════════════════════════════════════════════════════════════════════════
     */
    private static void demonstrateMemoryInfo() {
        System.out.println("┌─────────────────────────────────────────────────────────────────┐");
        System.out.println("│  DEMO 2: Runtime Memory Information                             │");
        System.out.println("└─────────────────────────────────────────────────────────────────┘\n");

        Runtime runtime = Runtime.getRuntime();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

        // Heap Memory
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        System.out.println("  ▶ HEAP Memory:");
        System.out.println("    ┌─────────────────────────────────────────────────────────────┐");
        System.out.println("    │ Initial (-Xms):  " + formatBytes(heapUsage.getInit()));
        System.out.println("    │ Used:            " + formatBytes(heapUsage.getUsed()));
        System.out.println("    │ Committed:       " + formatBytes(heapUsage.getCommitted()));
        System.out.println("    │ Maximum (-Xmx):  " + formatBytes(heapUsage.getMax()));
        System.out.println("    └─────────────────────────────────────────────────────────────┘\n");

        // Non-Heap Memory (Metaspace, Code Cache, etc.)
        MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();
        System.out.println("  ▶ NON-HEAP Memory (Metaspace + Code Cache):");
        System.out.println("    ┌─────────────────────────────────────────────────────────────┐");
        System.out.println("    │ Initial:   " + formatBytes(nonHeapUsage.getInit()));
        System.out.println("    │ Used:      " + formatBytes(nonHeapUsage.getUsed()));
        System.out.println("    │ Committed: " + formatBytes(nonHeapUsage.getCommitted()));
        System.out.println("    │ Maximum:   " + (nonHeapUsage.getMax() == -1 ? "Unlimited" : formatBytes(nonHeapUsage.getMax())));
        System.out.println("    └─────────────────────────────────────────────────────────────┘\n");

        // Runtime info (simpler API)
        System.out.println("  ▶ Runtime Memory Info:");
        System.out.println("    ┌─────────────────────────────────────────────────────────────┐");
        System.out.println("    │ Max Memory (Xmx):     " + formatBytes(runtime.maxMemory()));
        System.out.println("    │ Total Memory:         " + formatBytes(runtime.totalMemory()));
        System.out.println("    │ Free Memory:          " + formatBytes(runtime.freeMemory()));
        System.out.println("    │ Used Memory:          " + formatBytes(runtime.totalMemory() - runtime.freeMemory()));
        System.out.println("    └─────────────────────────────────────────────────────────────┘\n");

        System.out.println("  ═══════════════════════════════════════════════════════════════\n");
    }

    /**
     * ═══════════════════════════════════════════════════════════════════════════════════
     * DEMO 3: Memory Pools (Eden, Survivor, Old Gen, Metaspace, etc.)
     * ═══════════════════════════════════════════════════════════════════════════════════
     */
    private static void demonstrateMemoryPools() {
        System.out.println("┌─────────────────────────────────────────────────────────────────┐");
        System.out.println("│  DEMO 3: Memory Pools (Varies by GC)                            │");
        System.out.println("└─────────────────────────────────────────────────────────────────┘\n");

        List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();

        System.out.println("  ▶ Available Memory Pools:\n");

        for (MemoryPoolMXBean pool : pools) {
            MemoryUsage usage = pool.getUsage();
            String type = pool.getType().toString();

            System.out.println("    Pool: " + pool.getName());
            System.out.println("    ┌─────────────────────────────────────────────────────────────┐");
            System.out.println("    │ Type:      " + type);
            System.out.println("    │ Used:      " + formatBytes(usage.getUsed()));
            System.out.println("    │ Committed: " + formatBytes(usage.getCommitted()));
            System.out.println("    │ Max:       " + (usage.getMax() == -1 ? "Unlimited" : formatBytes(usage.getMax())));
            System.out.println("    └─────────────────────────────────────────────────────────────┘\n");
        }

        System.out.println("  ═══════════════════════════════════════════════════════════════\n");
    }

    /**
     * ═══════════════════════════════════════════════════════════════════════════════════
     * DEMO 4: Stack Overflow Demonstration
     * ═══════════════════════════════════════════════════════════════════════════════════
     */
    private static void demonstrateStackOverflow() {
        System.out.println("┌─────────────────────────────────────────────────────────────────┐");
        System.out.println("│  DEMO 4: StackOverflowError Demonstration                       │");
        System.out.println("└─────────────────────────────────────────────────────────────────┘\n");

        System.out.println("  ▶ Each method call creates a new frame on the stack.");
        System.out.println("  ▶ Infinite recursion exhausts the stack → StackOverflowError\n");

        System.out.println("  Code Example:");
        System.out.println("  ┌─────────────────────────────────────────────────────────────┐");
        System.out.println("  │ public void infiniteRecursion() {                          │");
        System.out.println("  │     infiniteRecursion(); // Stack frame added each call    │");
        System.out.println("  │ }                                                          │");
        System.out.println("  └─────────────────────────────────────────────────────────────┘\n");

        // Count how deep we can go
        int maxDepth = countStackDepth(0);
        System.out.println("  ▶ Maximum recursion depth before StackOverflowError: ~" + maxDepth);
        System.out.println("    (Varies based on -Xss setting and frame size)\n");

        System.out.println("  JVM Flag to adjust stack size:");
        System.out.println("    -Xss512k  (512 KB stack)");
        System.out.println("    -Xss1m   (1 MB stack - common default)");
        System.out.println("    -Xss2m   (2 MB stack)\n");

        System.out.println("  ═══════════════════════════════════════════════════════════════\n");
    }

    private static int countStackDepth(int depth) {
        try {
            return countStackDepth(depth + 1);
        } catch (StackOverflowError e) {
            return depth;
        }
    }

    /**
     * ═══════════════════════════════════════════════════════════════════════════════════
     * DEMO 5: Object Allocation and Memory
     * ═══════════════════════════════════════════════════════════════════════════════════
     */
    private static void demonstrateObjectAllocation() {
        System.out.println("┌─────────────────────────────────────────────────────────────────┐");
        System.out.println("│  DEMO 5: Object Allocation and Heap Usage                       │");
        System.out.println("└─────────────────────────────────────────────────────────────────┘\n");

        Runtime runtime = Runtime.getRuntime();

        // Force GC to get baseline
        System.gc();
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}

        long beforeUsed = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("  ▶ Memory before allocation: " + formatBytes(beforeUsed));

        // Allocate objects
        List<byte[]> objects = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            objects.add(new byte[10_000]); // 10KB each = 1MB total
        }

        long afterUsed = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("  ▶ Memory after allocating 100 x 10KB objects: " + formatBytes(afterUsed));
        System.out.println("  ▶ Memory used by objects: ~" + formatBytes(afterUsed - beforeUsed));

        // Clear references
        objects.clear();
        objects = null;

        // Request GC
        System.gc();
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}

        long afterGC = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("  ▶ Memory after GC: " + formatBytes(afterGC));
        System.out.println("  ▶ Memory freed: ~" + formatBytes(afterUsed - afterGC) + "\n");

        System.out.println("  Note: System.gc() is just a HINT to the JVM. GC may not run immediately.\n");

        System.out.println("  ═══════════════════════════════════════════════════════════════\n");
    }

    /**
     * Utility method to format bytes into human-readable format.
     */
    private static String formatBytes(long bytes) {
        if (bytes < 0) return "N/A";
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.2f %sB", bytes / Math.pow(1024, exp), pre);
    }
}


/**
 * ╔═══════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                              INTERVIEW QUESTIONS - MEMORY AREAS                                        ║
 * ╚═══════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * Q1: What are the different memory areas in JVM?
 * ───────────────────────────────────────────────
 * A: SHARED: Method Area (Metaspace), Heap
 *    PER-THREAD: JVM Stack, PC Register, Native Method Stack
 *
 *
 * Q2: What's the difference between Stack and Heap?
 * ─────────────────────────────────────────────────
 * A: Stack: Per-thread, stores frames/locals/primitives, LIFO, fast, auto-cleanup
 *    Heap: Shared, stores objects, GC managed, slower, larger
 *
 *
 * Q3: Where are static variables stored?
 * ──────────────────────────────────────
 * A: Method Area (Metaspace in Java 8+)
 *
 *
 * Q4: Where are local variables stored?
 * ─────────────────────────────────────
 * A: JVM Stack (in the frame of the executing method)
 *
 *
 * Q5: Where are objects stored?
 * ─────────────────────────────
 * A: Heap (always)
 *
 *
 * Q6: What is PermGen and why was it removed?
 * ───────────────────────────────────────────
 * A: PermGen (Permanent Generation) stored class metadata in Java 7 and earlier.
 *    It was fixed size and often caused OutOfMemoryError.
 *    Replaced by Metaspace in Java 8, which uses native memory and auto-grows.
 *
 *
 * Q7: What causes StackOverflowError?
 * ───────────────────────────────────
 * A: When the stack is full, usually due to:
 *    - Infinite recursion
 *    - Very deep recursion
 *    - Large stack frames (many local variables)
 *
 *
 * Q8: What causes OutOfMemoryError: Java heap space?
 * ──────────────────────────────────────────────────
 * A: When heap is exhausted, usually due to:
 *    - Memory leak (objects not being released)
 *    - Creating too many objects
 *    - Large objects
 *    - Insufficient -Xmx setting
 *
 *
 * Q9: What is the String Pool and where is it located?
 * ────────────────────────────────────────────────────
 * A: String Pool is a special area for string literals.
 *    Java 6: PermGen
 *    Java 7+: Heap (can be garbage collected)
 *
 *
 * Q10: What are JVM flags for memory tuning?
 * ──────────────────────────────────────────
 * A: -Xms: Initial heap size
 *    -Xmx: Maximum heap size
 *    -Xmn: Young generation size
 *    -Xss: Stack size per thread
 *    -XX:MetaspaceSize: Initial Metaspace
 *    -XX:MaxMetaspaceSize: Maximum Metaspace
 *
 */

