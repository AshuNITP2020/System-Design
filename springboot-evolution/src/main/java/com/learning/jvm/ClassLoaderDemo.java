package com.learning.jvm;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

/**
 * ╔═══════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                                                                                       ║
 * ║                              CLASS LOADER - COMPLETE GUIDE                                            ║
 * ║                                                                                                       ║
 * ╚═══════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * Class Loader is a subsystem of JVM responsible for loading class files into memory.
 * It's the first component that comes into action when you run a Java program.
 *
 *
 * ╔═══════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                1. CLASS LOADER HIERARCHY                                               ║
 * ╚═══════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *                         ┌─────────────────────────────────────────┐
 *                         │       BOOTSTRAP CLASS LOADER            │
 *                         │       (Primordial Class Loader)         │
 *                         │                                         │
 *                         │  • Written in native code (C/C++)       │
 *                         │  • Loads core Java classes              │
 *                         │  • Location: $JAVA_HOME/lib/rt.jar      │
 *                         │  • Returns NULL in Java code            │
 *                         │  • Classes: java.lang.*, java.util.*    │
 *                         └───────────────────┬─────────────────────┘
 *                                             │ parent
 *                                             ▼
 *                         ┌─────────────────────────────────────────┐
 *                         │       EXTENSION CLASS LOADER            │
 *                         │    (Platform Class Loader in Java 9+)   │
 *                         │                                         │
 *                         │  • sun.misc.Launcher$ExtClassLoader     │
 *                         │  • Loads extension classes              │
 *                         │  • Location: $JAVA_HOME/lib/ext         │
 *                         │  • Or java.ext.dirs system property     │
 *                         └───────────────────┬─────────────────────┘
 *                                             │ parent
 *                                             ▼
 *                         ┌─────────────────────────────────────────┐
 *                         │       APPLICATION CLASS LOADER          │
 *                         │         (System Class Loader)           │
 *                         │                                         │
 *                         │  • sun.misc.Launcher$AppClassLoader     │
 *                         │  • Loads application classes            │
 *                         │  • Location: CLASSPATH                  │
 *                         │  • Your classes and third-party libs    │
 *                         └───────────────────┬─────────────────────┘
 *                                             │ parent
 *                                             ▼
 *                         ┌─────────────────────────────────────────┐
 *                         │        CUSTOM CLASS LOADERS             │
 *                         │                                         │
 *                         │  • User-defined class loaders           │
 *                         │  • Used by: App servers, OSGi, plugins  │
 *                         │  • Example: Tomcat's WebappClassLoader  │
 *                         └─────────────────────────────────────────┘
 *
 *
 * ╔═══════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                              2. THREE PRINCIPLES OF CLASS LOADING                                      ║
 * ╚═══════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  PRINCIPLE 1: DELEGATION HIERARCHY (Parent-First Delegation)                                           │
 * ├─────────────────────────────────────────────────────────────────────────────────────────────────────────┤
 * │                                                                                                         │
 * │  When a class needs to be loaded:                                                                       │
 * │                                                                                                         │
 * │  1. Request comes to Application ClassLoader                                                            │
 * │     │                                                                                                   │
 * │     ▼ "Can you load MyClass?"                                                                           │
 * │  2. Delegates to Extension ClassLoader (parent)                                                         │
 * │     │                                                                                                   │
 * │     ▼ "Can you load MyClass?"                                                                           │
 * │  3. Delegates to Bootstrap ClassLoader (parent)                                                         │
 * │     │                                                                                                   │
 * │     ▼ Bootstrap searches in rt.jar                                                                      │
 * │  4. NOT FOUND → Back to Extension                                                                       │
 * │     │                                                                                                   │
 * │     ▼ Extension searches in ext directory                                                               │
 * │  5. NOT FOUND → Back to Application                                                                     │
 * │     │                                                                                                   │
 * │     ▼ Application searches in CLASSPATH                                                                 │
 * │  6. FOUND! → Load and return the class                                                                  │
 * │     (If NOT FOUND → ClassNotFoundException)                                                             │
 * │                                                                                                         │
 * │  WHY? Security! Prevents malicious code from replacing core Java classes.                               │
 * │        You cannot create your own java.lang.String and have it loaded.                                  │
 * │                                                                                                         │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  PRINCIPLE 2: VISIBILITY                                                                                │
 * ├─────────────────────────────────────────────────────────────────────────────────────────────────────────┤
 * │                                                                                                         │
 * │  • Child ClassLoader CAN see classes loaded by Parent                                                   │
 * │  • Parent ClassLoader CANNOT see classes loaded by Child                                                │
 * │                                                                                                         │
 * │  Example:                                                                                               │
 * │  ┌───────────────────────┐                                                                              │
 * │  │ Bootstrap (java.lang.String) │ ◄── Can see: Only its own classes                                    │
 * │  └───────────────────────┘                                                                              │
 * │            ▲                                                                                            │
 * │  ┌─────────┴─────────────┐                                                                              │
 * │  │ Application (MyClass) │ ◄── Can see: java.lang.String + its own classes                              │
 * │  └───────────────────────┘                                                                              │
 * │                                                                                                         │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │  PRINCIPLE 3: UNIQUENESS                                                                                │
 * ├─────────────────────────────────────────────────────────────────────────────────────────────────────────┤
 * │                                                                                                         │
 * │  • A class loaded by Parent ClassLoader will NOT be loaded again by Child                               │
 * │  • Ensures a class is loaded only ONCE per ClassLoader hierarchy                                        │
 * │  • Class identity = Class name + ClassLoader instance                                                   │
 * │                                                                                                         │
 * │  Note: Two different ClassLoaders CAN load the same class separately!                                   │
 * │        These will be treated as DIFFERENT classes (even with same name)                                 │
 * │                                                                                                         │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔═══════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                              3. CLASS LOADING PHASES                                                   ║
 * ╚═══════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                         │
 * │    ┌───────────┐      ┌───────────────────────────────────────┐      ┌────────────────────┐             │
 * │    │  LOADING  │ ───► │              LINKING                  │ ───► │  INITIALIZATION    │             │
 * │    └───────────┘      │  ┌─────────┬─────────┬─────────────┐  │      └────────────────────┘             │
 * │                       │  │ VERIFY  │ PREPARE │   RESOLVE   │  │                                         │
 * │                       │  └─────────┴─────────┴─────────────┘  │                                         │
 * │                       └───────────────────────────────────────┘                                         │
 * │                                                                                                         │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * PHASE 1: LOADING
 * ─────────────────
 * • Read .class file binary data
 * • Create java.lang.Class object in Method Area
 * • Store class metadata (superclass, interfaces, fields, methods)
 *
 * PHASE 2: LINKING
 * ─────────────────
 *   2a. VERIFY
 *       • Bytecode verification (magic number: CAFEBABE)
 *       • Type checking
 *       • Stack map verification
 *       • Ensures .class file is valid and won't harm JVM
 *
 *   2b. PREPARE
 *       • Allocate memory for static variables
 *       • Assign DEFAULT values (not actual values!)
 *         - int → 0
 *         - boolean → false
 *         - Object → null
 *
 *   2c. RESOLVE (Optional - can be lazy)
 *       • Replace symbolic references with direct references
 *       • Symbolic: "java/lang/Object"
 *       • Direct: Actual memory address
 *
 * PHASE 3: INITIALIZATION
 * ─────────────────────────
 * • Execute static initializers (static blocks)
 * • Assign actual values to static variables
 * • Execute &lt;clinit&gt; method (class initialization method)
 *
 *
 * ╔═══════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                              4. WHEN IS A CLASS LOADED?                                                ║
 * ╚═══════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * Classes are loaded LAZILY (on first active use). Active use includes:
 *
 * ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 * │                                                                                                         │
 * │  1. Creating an instance:          new MyClass()                                                        │
 * │  2. Accessing static field:        MyClass.staticField                                                  │
 * │  3. Calling static method:         MyClass.staticMethod()                                               │
 * │  4. Reflection:                    Class.forName("MyClass")                                             │
 * │  5. Initializing subclass:         (Parent is initialized first)                                        │
 * │  6. Main class:                    java MyClass                                                         │
 * │                                                                                                         │
 * │  NOT active use (won't trigger initialization):                                                         │
 * │  • Accessing static final COMPILE-TIME constants                                                        │
 * │  • Array creation of the type                                                                           │
 * │  • Referencing the Class object without initialization                                                  │
 * │                                                                                                         │
 * └─────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * @author Learning Guide
 * @see JvmArchitectureGuide
 */
public class ClassLoaderDemo {

    // Static block - executed during class initialization
    static {
        System.out.println(">>> ClassLoaderDemo static block executed - Class is being INITIALIZED");
    }

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════════╗");
        System.out.println("║                  CLASS LOADER DEMONSTRATIONS                      ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════╝\n");

        demonstrateClassLoaderHierarchy();
        demonstrateWhatEachLoaderLoads();
        demonstrateClassLoaderMethods();
        demonstrateLazyLoading();
        demonstrateClassIdentity();
    }

    /**
     * ═══════════════════════════════════════════════════════════════════════════════════
     * DEMO 1: ClassLoader Hierarchy
     * ═══════════════════════════════════════════════════════════════════════════════════
     */
    private static void demonstrateClassLoaderHierarchy() {
        System.out.println("┌─────────────────────────────────────────────────────────────────┐");
        System.out.println("│  DEMO 1: ClassLoader Hierarchy                                  │");
        System.out.println("└─────────────────────────────────────────────────────────────────┘\n");

        // Get ClassLoader of this class
        ClassLoader applicationLoader = ClassLoaderDemo.class.getClassLoader();
        System.out.println("  This class (ClassLoaderDemo) is loaded by:");
        System.out.println("  └── " + applicationLoader);
        System.out.println("      (Application/System ClassLoader)\n");

        // Traverse hierarchy
        System.out.println("  Complete ClassLoader Hierarchy:\n");
        ClassLoader current = applicationLoader;
        int level = 1;

        while (current != null) {
            String indent = "  " + "   ".repeat(level);
            System.out.println(indent + "Level " + level + ": " + current.getClass().getName());
            current = current.getParent();
            level++;
        }
        System.out.println("  " + "   ".repeat(level) + "Level " + level + ": null (Bootstrap ClassLoader - native code)");

        System.out.println("\n  ═══════════════════════════════════════════════════════════════\n");
    }

    /**
     * ═══════════════════════════════════════════════════════════════════════════════════
     * DEMO 2: What each ClassLoader loads
     * ═══════════════════════════════════════════════════════════════════════════════════
     */
    private static void demonstrateWhatEachLoaderLoads() {
        System.out.println("┌─────────────────────────────────────────────────────────────────┐");
        System.out.println("│  DEMO 2: What Each ClassLoader Loads                            │");
        System.out.println("└─────────────────────────────────────────────────────────────────┘\n");

        // Bootstrap ClassLoader (returns null)
        System.out.println("  ▶ Classes loaded by BOOTSTRAP ClassLoader:");
        System.out.println("    (These return null for getClassLoader())\n");

        Class<?>[] bootstrapClasses = {
                String.class,
                Integer.class,
                Object.class,
                System.class,
                ClassLoader.class
        };

        for (Class<?> clazz : bootstrapClasses) {
            System.out.println("    • " + clazz.getName() + " → " + clazz.getClassLoader());
        }

        System.out.println("\n  ▶ Classes loaded by APPLICATION ClassLoader:\n");

        // Application ClassLoader
        System.out.println("    • " + ClassLoaderDemo.class.getName() + " → " +
                ClassLoaderDemo.class.getClassLoader());

        // Try to find Extension ClassLoader classes (Java 8)
        System.out.println("\n  ▶ Extension/Platform ClassLoader:");
        System.out.println("    • Parent of Application ClassLoader: " +
                ClassLoaderDemo.class.getClassLoader().getParent());

        System.out.println("\n  ═══════════════════════════════════════════════════════════════\n");
    }

    /**
     * ═══════════════════════════════════════════════════════════════════════════════════
     * DEMO 3: ClassLoader Methods
     * ═══════════════════════════════════════════════════════════════════════════════════
     */
    private static void demonstrateClassLoaderMethods() {
        System.out.println("┌─────────────────────────────────────────────────────────────────┐");
        System.out.println("│  DEMO 3: ClassLoader Methods                                    │");
        System.out.println("└─────────────────────────────────────────────────────────────────┘\n");

        ClassLoader classLoader = ClassLoaderDemo.class.getClassLoader();

        // 1. loadClass() - follows delegation model
        System.out.println("  ▶ loadClass() - Loads a class following delegation model:");
        try {
            Class<?> stringClass = classLoader.loadClass("java.lang.String");
            System.out.println("    Loaded: " + stringClass.getName());
            System.out.println("    Actually loaded by: " + stringClass.getClassLoader() + " (Bootstrap)\n");
        } catch (ClassNotFoundException e) {
            System.out.println("    Class not found: " + e.getMessage());
        }

        // 2. getResource() - Find resources
        System.out.println("  ▶ getResource() - Finds resources in classpath:");
        URL resource = classLoader.getResource("java/lang/String.class");
        System.out.println("    String.class location: " + resource + "\n");

        // 3. getResourceAsStream() - Get resource as InputStream
        System.out.println("  ▶ getResourceAsStream() - Gets resource as stream:");
        try (InputStream is = classLoader.getResourceAsStream("java/lang/String.class")) {
            if (is != null) {
                System.out.println("    String.class stream available, bytes: " + is.available() + "\n");
            }
        } catch (IOException e) {
            System.out.println("    Error: " + e.getMessage());
        }

        // 4. getResources() - Find all resources with given name
        System.out.println("  ▶ getResources() - Finds all resources with given name:");
        try {
            Enumeration<URL> resources = classLoader.getResources("META-INF/MANIFEST.MF");
            int count = 0;
            while (resources.hasMoreElements()) {
                count++;
                if (count <= 3) { // Print first 3
                    System.out.println("    " + count + ". " + resources.nextElement());
                } else {
                    resources.nextElement();
                }
            }
            if (count > 3) {
                System.out.println("    ... and " + (count - 3) + " more");
            }
        } catch (IOException e) {
            System.out.println("    Error: " + e.getMessage());
        }

        System.out.println("\n  ═══════════════════════════════════════════════════════════════\n");
    }

    /**
     * ═══════════════════════════════════════════════════════════════════════════════════
     * DEMO 4: Lazy Loading (Classes are loaded on first use)
     * ═══════════════════════════════════════════════════════════════════════════════════
     */
    private static void demonstrateLazyLoading() {
        System.out.println("┌─────────────────────────────────────────────────────────────────┐");
        System.out.println("│  DEMO 4: Lazy Loading - Classes Load on First Use              │");
        System.out.println("└─────────────────────────────────────────────────────────────────┘\n");

        System.out.println("  ▶ LazyClass has NOT been loaded yet...\n");
        System.out.println("  ▶ Now accessing LazyClass.staticField:\n");

        // This triggers loading and initialization of LazyClass
        String value = LazyClass.staticField;
        System.out.println("\n  ▶ Value retrieved: " + value);

        System.out.println("\n  ▶ Accessing staticField again (class already loaded):\n");
        String value2 = LazyClass.staticField;
        System.out.println("  ▶ Value retrieved: " + value2);
        System.out.println("    (Notice: static block did NOT execute again!)\n");

        // Compile-time constant - does NOT trigger initialization
        System.out.println("  ▶ Accessing COMPILE-TIME constant (does NOT trigger init):");
        System.out.println("    CompileTimeConstantClass.CONSTANT = " +
                CompileTimeConstantClass.CONSTANT);
        System.out.println("    (Notice: static block of CompileTimeConstantClass did NOT execute!)\n");

        System.out.println("  ═══════════════════════════════════════════════════════════════\n");
    }

    /**
     * ═══════════════════════════════════════════════════════════════════════════════════
     * DEMO 5: Class Identity (Class = Name + ClassLoader)
     * ═══════════════════════════════════════════════════════════════════════════════════
     */
    private static void demonstrateClassIdentity() {
        System.out.println("┌─────────────────────────────────────────────────────────────────┐");
        System.out.println("│  DEMO 5: Class Identity = ClassName + ClassLoader              │");
        System.out.println("└─────────────────────────────────────────────────────────────────┘\n");

        // Same class loaded by same ClassLoader = Same Class object
        Class<?> class1 = String.class;
        Class<?> class2 = String.class;

        System.out.println("  ▶ Same class, same ClassLoader:");
        System.out.println("    String.class (1): " + System.identityHashCode(class1));
        System.out.println("    String.class (2): " + System.identityHashCode(class2));
        System.out.println("    Same object? " + (class1 == class2) + "\n");

        // Class identity includes ClassLoader
        System.out.println("  ▶ Class Identity Formula:");
        System.out.println("    ┌──────────────────────────────────────────────────────────┐");
        System.out.println("    │  Class Identity = Fully Qualified Name + ClassLoader     │");
        System.out.println("    │                                                          │");
        System.out.println("    │  Example:                                                │");
        System.out.println("    │  com.example.MyClass loaded by AppClassLoader            │");
        System.out.println("    │  ≠                                                       │");
        System.out.println("    │  com.example.MyClass loaded by CustomClassLoader         │");
        System.out.println("    │                                                          │");
        System.out.println("    │  (Even though same name, they are DIFFERENT classes!)    │");
        System.out.println("    └──────────────────────────────────────────────────────────┘\n");

        System.out.println("  ═══════════════════════════════════════════════════════════════\n");
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    // Helper Classes for Demonstrations
    // ═══════════════════════════════════════════════════════════════════════════════════

    /**
     * This class demonstrates lazy loading.
     * Its static block will only execute when the class is first used.
     */
    static class LazyClass {
        static String staticField = "Hello from LazyClass!";

        static {
            System.out.println("    >>> LazyClass static block executed - Class LOADED and INITIALIZED!");
        }
    }

    /**
     * Demonstrates that accessing compile-time constants doesn't trigger initialization.
     */
    static class CompileTimeConstantClass {
        // Compile-time constant (static final + primitive/String + constant expression)
        static final String CONSTANT = "I am a compile-time constant";

        static {
            System.out.println("    >>> CompileTimeConstantClass static block executed!");
        }

        // This is NOT a compile-time constant (requires runtime computation)
        static final String NOT_CONSTANT = new String("I am NOT a compile-time constant");
    }
}


/**
 * ╔═══════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                              INTERVIEW QUESTIONS - CLASS LOADER                                        ║
 * ╚═══════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * Q1: What is a ClassLoader? Why do we need it?
 * ─────────────────────────────────────────────
 * A: ClassLoader is a JVM subsystem that loads class files into memory at runtime.
 *    It's needed because:
 *    - Java classes are loaded on-demand (lazy loading)
 *    - Provides namespace isolation (same class name, different loaders)
 *    - Enables dynamic loading (plugins, hot deployment)
 *    - Security (sandbox, delegation prevents tampering core classes)
 *
 *
 * Q2: Explain the ClassLoader hierarchy and delegation model.
 * ───────────────────────────────────────────────────────────
 * A: Three main ClassLoaders in hierarchy:
 *    1. Bootstrap (native) → Loads core Java classes (rt.jar)
 *    2. Extension/Platform → Loads extension classes (lib/ext)
 *    3. Application/System → Loads application classes (classpath)
 *
 *    Delegation: Child delegates to parent first.
 *    AppClassLoader → ExtClassLoader → Bootstrap → Not Found → Back down
 *
 *
 * Q3: What happens if I create my own java.lang.String class?
 * ───────────────────────────────────────────────────────────
 * A: It will NOT be loaded! Due to delegation, the request goes to Bootstrap
 *    ClassLoader first, which loads the real java.lang.String from rt.jar.
 *    Your custom String will never be loaded. This is a SECURITY feature.
 *
 *
 * Q4: When is a class loaded and initialized?
 * ───────────────────────────────────────────
 * A: Loaded on first "active use":
 *    - Creating instance (new)
 *    - Accessing static field (not compile-time constant)
 *    - Calling static method
 *    - Reflection (Class.forName)
 *    - Subclass initialization
 *    - Main class
 *
 *
 * Q5: What is the difference between Class.forName() and ClassLoader.loadClass()?
 * ───────────────────────────────────────────────────────────────────────────────
 * A: Class.forName("MyClass"):
 *    - Loads AND initializes the class
 *    - Runs static blocks
 *    - Used for: JDBC driver loading
 *
 *    ClassLoader.loadClass("MyClass"):
 *    - Only loads, does NOT initialize
 *    - Static blocks don't run until actual use
 *    - Used for: Lazy loading scenarios
 *
 *
 * Q6: Can same class be loaded by different ClassLoaders?
 * ───────────────────────────────────────────────────────
 * A: Yes! And they will be treated as DIFFERENT classes.
 *    Class Identity = Fully Qualified Name + ClassLoader
 *    This enables:
 *    - Different versions of same library
 *    - Application isolation in app servers
 *    - Plugin architectures
 *
 *
 * Q7: What is ContextClassLoader?
 * ───────────────────────────────
 * A: Thread.currentThread().getContextClassLoader()
 *    - Each thread has a context ClassLoader
 *    - Used by frameworks to load classes in thread context
 *    - Solves "class loader inversion" problem
 *    - Default: Same as parent thread's context ClassLoader
 *
 *
 * Q8: Common ClassLoader exceptions?
 * ──────────────────────────────────
 * A: - ClassNotFoundException: Class not found in classpath
 *    - NoClassDefFoundError: Class was present at compile time but missing at runtime
 *    - LinkageError: Class already loaded by different ClassLoader
 *    - ClassCastException: Same class loaded by different ClassLoaders
 *
 */

