package com.learning.jvm;

/**
 * ╔═══════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                                                                                       ║
 * ║                            JAVA, JVM, JRE, JDK - COMPLETE GUIDE                                       ║
 * ║                                                                                                       ║
 * ╚═══════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * This package provides comprehensive learning materials about Java's core architecture.
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                    PACKAGE CONTENTS                                                     │
 * ├─────────────────────────────────────────────────────────────────────────────────────────────────────────┤
 * │  1. JvmArchitectureGuide.java    - This file, main overview                                            │
 * │  2. ClassLoaderDemo.java         - ClassLoader hierarchy and examples                                   │
 * │  3. MemoryAreasDemo.java         - Stack, Heap, Method Area demonstrations                              │
 * │  4. GarbageCollectionDemo.java   - GC algorithms and practical examples                                 │
 * │  5. StringPoolDemo.java          - String pool and immutability                                         │
 * │  6. JvmTuningGuide.java          - JVM flags and performance tuning                                     │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔═══════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    1. WHAT IS JAVA?                                                    ║
 * ╚═══════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * Java is a HIGH-LEVEL, CLASS-BASED, OBJECT-ORIENTED programming language designed with:
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  CORE PRINCIPLES                                                                                        │
 * ├─────────────────────────────────────────────────────────────────────────────────────────────────────────┤
 * │                                                                                                         │
 * │  1. WORA (Write Once, Run Anywhere)                                                                     │
 * │     └── Compile once to bytecode, run on any platform with JVM                                          │
 * │                                                                                                         │
 * │  2. Platform Independence                                                                               │
 * │     └── JVM abstracts the underlying OS/hardware differences                                            │
 * │                                                                                                         │
 * │  3. Object-Oriented                                                                                     │
 * │     └── Everything is an object (except primitives)                                                     │
 * │                                                                                                         │
 * │  4. Automatic Memory Management                                                                         │
 * │     └── Garbage Collection handles memory deallocation                                                  │
 * │                                                                                                         │
 * │  5. Strong Type Safety                                                                                  │
 * │     └── Compile-time and runtime type checking                                                          │
 * │                                                                                                         │
 * │  6. Security                                                                                            │
 * │     └── Bytecode verification, Security Manager, sandboxing                                             │
 * │                                                                                                         │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * HISTORY:
 * - Created by James Gosling at Sun Microsystems in 1995
 * - Originally called "Oak" (after a tree outside Gosling's office)
 * - Sun Microsystems acquired by Oracle Corporation in 2010
 *
 *
 * ╔═══════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                              2. JDK vs JRE vs JVM RELATIONSHIP                                         ║
 * ╚═══════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                            JDK                                                          │
 * │  (Java Development Kit - Complete Development Environment)                                              │
 * │                                                                                                         │
 * │  ┌───────────────────────────────────────────────────────────────────────────────────────────────────┐  │
 * │  │                                          JRE                                                      │  │
 * │  │  (Java Runtime Environment - For Running Java Programs)                                           │  │
 * │  │                                                                                                   │  │
 * │  │  ┌─────────────────────────────────────────────────────────────────────────────────────────────┐  │  │
 * │  │  │                                        JVM                                                  │  │  │
 * │  │  │  (Java Virtual Machine - Executes Bytecode)                                                 │  │  │
 * │  │  │                                                                                             │  │  │
 * │  │  │  • Class Loader Subsystem                                                                   │  │  │
 * │  │  │  • Runtime Data Areas (Heap, Stack, Method Area, PC Register, Native Stack)                 │  │  │
 * │  │  │  • Execution Engine (Interpreter + JIT Compiler + GC)                                       │  │  │
 * │  │  │  • Native Method Interface (JNI)                                                            │  │  │
 * │  │  │                                                                                             │  │  │
 * │  │  └─────────────────────────────────────────────────────────────────────────────────────────────┘  │  │
 * │  │                                                                                                   │  │
 * │  │  + Java Class Libraries (rt.jar, charsets.jar, etc.)                                              │  │
 * │  │  + Supporting Files (security, properties, resources)                                             │  │
 * │  │                                                                                                   │  │
 * │  └───────────────────────────────────────────────────────────────────────────────────────────────────┘  │
 * │                                                                                                         │
 * │  + Development Tools:                                                                                   │
 * │    • javac      - Java Compiler (source → bytecode)                                                     │
 * │    • java       - Java Launcher                                                                         │
 * │    • javadoc    - Documentation Generator                                                               │
 * │    • jar        - Archive Tool                                                                          │
 * │    • jdb        - Java Debugger                                                                         │
 * │    • javap      - Class File Disassembler                                                               │
 * │    • jconsole   - JMX-compliant Monitoring Tool                                                         │
 * │    • jvisualvm  - Visual Monitoring Tool                                                                │
 * │    • jmap       - Memory Map Tool                                                                       │
 * │    • jstack     - Stack Trace Tool                                                                      │
 * │    • jstat      - JVM Statistics Monitoring                                                             │
 * │    • jshell     - Java REPL (Java 9+)                                                                   │
 * │                                                                                                         │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * COMPARISON TABLE:
 * ┌──────────────────┬─────────────────────┬─────────────────────┬─────────────────────┐
 * │     Aspect       │        JVM          │        JRE          │        JDK          │
 * ├──────────────────┼─────────────────────┼─────────────────────┼─────────────────────┤
 * │ Full Form        │ Java Virtual Machine│ Java Runtime Env    │ Java Development Kit│
 * │ Purpose          │ Execute bytecode    │ Run Java apps       │ Develop Java apps   │
 * │ Contains JVM?    │ IS the JVM          │ ✓                   │ ✓                   │
 * │ Java Libraries?  │ ✗                   │ ✓                   │ ✓                   │
 * │ Compiler (javac)?│ ✗                   │ ✗                   │ ✓                   │
 * │ Debug Tools?     │ ✗                   │ ✗                   │ ✓                   │
 * │ Who needs it?    │ (Internal)          │ End Users           │ Developers          │
 * │ Platform Dep?    │ Yes                 │ Yes                 │ Yes                 │
 * └──────────────────┴─────────────────────┴─────────────────────┴─────────────────────┘
 *
 *
 * ╔═══════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    3. JVM ARCHITECTURE                                                 ║
 * ╚═══════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *                                    ┌─────────────────────────┐
 *                                    │      .class files       │
 *                                    │       (Bytecode)        │
 *                                    └───────────┬─────────────┘
 *                                                │
 *                                                ▼
 * ┌────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                               CLASS LOADER SUBSYSTEM                                                   │
 * │  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────────┐                                 │
 * │  │    LOADING      │───►│    LINKING      │───►│   INITIALIZATION    │                                 │
 * │  │                 │    │                 │    │                     │                                 │
 * │  │ • Bootstrap     │    │ • Verify        │    │ • Execute static    │                                 │
 * │  │ • Extension     │    │ • Prepare       │    │   blocks            │                                 │
 * │  │ • Application   │    │ • Resolve       │    │ • Initialize static │                                 │
 * │  │                 │    │   (optional)    │    │   variables         │                                 │
 * │  └─────────────────┘    └─────────────────┘    └─────────────────────┘                                 │
 * └────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *                                                │
 *                                                ▼
 * ┌────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                  RUNTIME DATA AREAS                                                    │
 * │                                                                                                        │
 * │  ┌──────────────────────────────────────────────────────────────────────────────────────────────────┐  │
 * │  │                           SHARED AMONG ALL THREADS                                               │  │
 * │  │  ┌──────────────────────────────────┐  ┌──────────────────────────────────────────────────────┐  │  │
 * │  │  │         METHOD AREA              │  │                      HEAP                            │  │  │
 * │  │  │     (Metaspace in Java 8+)       │  │  ┌────────────────────────┬───────────────────────┐  │  │  │
 * │  │  │                                  │  │  │    Young Generation    │    Old Generation     │  │  │  │
 * │  │  │  • Class structure/metadata      │  │  │  ┌──────┬──────┬─────┐│                       │  │  │  │
 * │  │  │  • Runtime constant pool         │  │  │  │ Eden │  S0  │ S1  ││     (Tenured)         │  │  │  │
 * │  │  │  • Field and method data         │  │  │  └──────┴──────┴─────┘│                       │  │  │  │
 * │  │  │  • Method bytecode               │  │  └────────────────────────┴───────────────────────┘  │  │  │
 * │  │  │  • Static variables              │  │                                                      │  │  │
 * │  │  └──────────────────────────────────┘  └──────────────────────────────────────────────────────┘  │  │
 * │  └──────────────────────────────────────────────────────────────────────────────────────────────────┘  │
 * │                                                                                                        │
 * │  ┌──────────────────────────────────────────────────────────────────────────────────────────────────┐  │
 * │  │                              PER THREAD (Thread-Local)                                           │  │
 * │  │  ┌────────────────────┐  ┌────────────────────────┐  ┌────────────────────────────────────────┐  │  │
 * │  │  │    PC REGISTER     │  │      JVM STACK         │  │       NATIVE METHOD STACK              │  │  │
 * │  │  │                    │  │                        │  │                                        │  │  │
 * │  │  │ Address of current │  │  Stack Frames:         │  │  For native (C/C++) method calls      │  │  │
 * │  │  │ executing bytecode │  │  • Local Variables     │  │  via JNI                               │  │  │
 * │  │  │ instruction        │  │  • Operand Stack       │  │                                        │  │  │
 * │  │  │                    │  │  • Frame Data          │  │                                        │  │  │
 * │  │  └────────────────────┘  └────────────────────────┘  └────────────────────────────────────────┘  │  │
 * │  └──────────────────────────────────────────────────────────────────────────────────────────────────┘  │
 * └────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *                                                │
 *                                                ▼
 * ┌────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                    EXECUTION ENGINE                                                    │
 * │  ┌────────────────────────┐  ┌────────────────────────┐  ┌────────────────────────────────────────┐    │
 * │  │     INTERPRETER        │  │     JIT COMPILER       │  │       GARBAGE COLLECTOR                │    │
 * │  │                        │  │                        │  │                                        │    │
 * │  │  Executes bytecode     │  │  Compiles hot code to  │  │  • Serial GC                           │    │
 * │  │  line by line          │  │  native machine code   │  │  • Parallel GC                         │    │
 * │  │                        │  │                        │  │  • G1 GC (default Java 9+)             │    │
 * │  │  (Slower but instant)  │  │  • C1 (Client)         │  │  • ZGC (Java 11+)                      │    │
 * │  │                        │  │  • C2 (Server)         │  │  • Shenandoah (Java 12+)               │    │
 * │  │                        │  │  (Faster after warmup) │  │                                        │    │
 * │  └────────────────────────┘  └────────────────────────┘  └────────────────────────────────────────┘    │
 * └────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *                                                │
 *                                                ▼
 * ┌────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                           JAVA NATIVE INTERFACE (JNI)                                                  │
 * │                           (Bridge to native C/C++ libraries)                                           │
 * └────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔═══════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                               4. JAVA CODE EXECUTION FLOW                                              ║
 * ╚═══════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                         │
 * │   Step 1: WRITE                     Step 2: COMPILE                    Step 3: EXECUTE                  │
 * │                                                                                                         │
 * │   ┌─────────────────┐              ┌─────────────────┐              ┌─────────────────┐                 │
 * │   │   Hello.java    │   javac      │   Hello.class   │    java      │  Machine Code   │                 │
 * │   │  (Source Code)  │  ─────────►  │   (Bytecode)    │  ─────────►  │  (Native Code)  │                 │
 * │   │                 │  (Compile)   │                 │   (JVM)      │                 │                 │
 * │   │  Human-readable │              │  Platform-      │              │  Platform-      │                 │
 * │   │  Java code      │              │  independent    │              │  specific       │                 │
 * │   └─────────────────┘              └─────────────────┘              └─────────────────┘                 │
 * │                                                                                                         │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * DETAILED EXECUTION STEPS when you run: java MyClass
 * ─────────────────────────────────────────────────────
 *
 * 1. OS loads JVM into memory
 *    └── JVM is a native application (platform-specific binary)
 *
 * 2. JVM initializes Runtime Data Areas
 *    └── Creates Heap, Method Area, allocates thread stacks
 *
 * 3. Class Loader loads MyClass.class
 *    ├── Loading: Reads bytecode, creates java.lang.Class object
 *    ├── Linking:
 *    │   ├── Verify: Validates bytecode format and security
 *    │   ├── Prepare: Allocates memory for static fields (default values)
 *    │   └── Resolve: Converts symbolic references to direct references
 *    └── Initialization: Executes static initializers and static blocks
 *
 * 4. JVM locates main() method
 *    └── Must be: public static void main(String[] args)
 *
 * 5. Creates main thread with its own:
 *    ├── JVM Stack
 *    ├── PC Register
 *    └── Native Method Stack
 *
 * 6. Execution Engine executes main() bytecode
 *    ├── Initially: Interpreter executes line by line
 *    └── Hot code: JIT compiles to native for better performance
 *
 * 7. Shutdown when main() completes
 *    ├── All non-daemon threads finish
 *    ├── Shutdown hooks execute
 *    └── JVM process terminates
 *
 *
 * ╔═══════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    5. WHY JVM IS "VIRTUAL"                                             ║
 * ╚═══════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * JVM is called "Virtual Machine" because:
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                         │
 * │  1. SPECIFICATION, NOT IMPLEMENTATION                                                                   │
 * │     └── JVM is defined by a specification document                                                      │
 * │     └── Multiple vendors can implement it differently (HotSpot, OpenJ9, GraalVM)                        │
 * │                                                                                                         │
 * │  2. ABSTRACTS HARDWARE                                                                                  │
 * │     └── Creates a "virtual computer" on top of physical hardware                                        │
 * │     └── Java code doesn't know/care about underlying CPU architecture                                   │
 * │                                                                                                         │
 * │  3. PLATFORM ABSTRACTION                                                                                │
 * │     └── Same bytecode runs on Windows, Linux, macOS                                                     │
 * │     └── JVM handles all platform-specific operations                                                    │
 * │                                                                                                         │
 * │  4. HAS ITS OWN "HARDWARE"                                                                              │
 * │     └── Virtual registers (PC Register)                                                                 │
 * │     └── Virtual memory (Heap, Stack)                                                                    │
 * │     └── Virtual instruction set (Bytecode)                                                              │
 * │                                                                                                         │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * IMPORTANT DISTINCTION:
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  Java (the language)  → Platform INDEPENDENT (bytecode runs anywhere)  │
 * │  JVM (the runtime)    → Platform DEPENDENT (native binary per OS)      │
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔═══════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    6. JDK DISTRIBUTIONS                                                ║
 * ╚═══════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * LTS (Long-Term Support) Versions:
 * ─────────────────────────────────
 * • Java 8  (2014) - Most widely used, still supported
 * • Java 11 (2018) - First LTS after Oracle license change
 * • Java 17 (2021) - Current recommended LTS
 * • Java 21 (2023) - Latest LTS
 *
 * Popular JDK Vendors:
 * ┌────────────────────┬──────────────────────────┬───────────────────────────┐
 * │      Vendor        │      Distribution        │         License           │
 * ├────────────────────┼──────────────────────────┼───────────────────────────┤
 * │ Oracle             │ Oracle JDK               │ Commercial (free for dev) │
 * │ Oracle             │ OpenJDK                  │ GPL v2 + Classpath        │
 * │ Eclipse Foundation │ Eclipse Temurin          │ Open Source               │
 * │ Amazon             │ Amazon Corretto          │ Open Source               │
 * │ Azul Systems       │ Azul Zulu                │ Open Source               │
 * │ Microsoft          │ Microsoft OpenJDK        │ Open Source               │
 * │ Red Hat            │ Red Hat OpenJDK          │ Open Source               │
 * │ BellSoft           │ Liberica JDK             │ Open Source               │
 * │ SAP                │ SapMachine               │ Open Source               │
 * └────────────────────┴──────────────────────────┴───────────────────────────┘
 *
 *
 * @author Learning Guide
 * @see ClassLoaderDemo
 * @see MemoryAreasDemo
 * @see GarbageCollectionDemo
 * @see StringPoolDemo
 * @see JvmTuningGuide
 */
public class JvmArchitectureGuide {

    /**
     * Main method demonstrating basic JVM information retrieval.
     */
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════════╗");
        System.out.println("║              JVM RUNTIME INFORMATION                              ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════╝");
        System.out.println();

        // JVM Information
        System.out.println("▶ JVM Information:");
        System.out.println("  ├── JVM Name:    " + System.getProperty("java.vm.name"));
        System.out.println("  ├── JVM Vendor:  " + System.getProperty("java.vm.vendor"));
        System.out.println("  ├── JVM Version: " + System.getProperty("java.vm.version"));
        System.out.println("  └── JVM Spec:    " + System.getProperty("java.vm.specification.version"));
        System.out.println();

        // Java Information
        System.out.println("▶ Java Information:");
        System.out.println("  ├── Java Version: " + System.getProperty("java.version"));
        System.out.println("  ├── Java Vendor:  " + System.getProperty("java.vendor"));
        System.out.println("  └── Java Home:    " + System.getProperty("java.home"));
        System.out.println();

        // Runtime Information
        Runtime runtime = Runtime.getRuntime();
        System.out.println("▶ Memory Information:");
        System.out.println("  ├── Available Processors: " + runtime.availableProcessors());
        System.out.println("  ├── Max Memory (Xmx):     " + formatBytes(runtime.maxMemory()));
        System.out.println("  ├── Total Memory:         " + formatBytes(runtime.totalMemory()));
        System.out.println("  └── Free Memory:          " + formatBytes(runtime.freeMemory()));
        System.out.println();

        // Operating System
        System.out.println("▶ Operating System:");
        System.out.println("  ├── OS Name:    " + System.getProperty("os.name"));
        System.out.println("  ├── OS Version: " + System.getProperty("os.version"));
        System.out.println("  └── OS Arch:    " + System.getProperty("os.arch"));
        System.out.println();

        // ClassLoader Information
        System.out.println("▶ ClassLoader Hierarchy:");
        ClassLoader classLoader = JvmArchitectureGuide.class.getClassLoader();
        System.out.println("  ├── This class loaded by: " + classLoader);
        System.out.println("  ├── Parent ClassLoader:   " + classLoader.getParent());
        System.out.println("  └── Bootstrap (null):     " + classLoader.getParent().getParent());
        System.out.println();

        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.println("Explore other classes in this package for detailed demonstrations!");
    }

    /**
     * Utility method to format bytes into human-readable format.
     */
    private static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.2f %sB", bytes / Math.pow(1024, exp), pre);
    }
}

