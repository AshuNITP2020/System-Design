package com.learning.java_interview;

import java.io.*;
import java.sql.SQLException;

/**
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                                                                                                ║
 * ║                                EXCEPTION HANDLING - INTERVIEW DEEP DIVE                                        ║
 * ║                                                                                                                ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                    1. EXCEPTION HIERARCHY                                                      ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *                                           java.lang.Object
 *                                                  │
 *                                           java.lang.Throwable
 *                                          ┌───────┴───────┐
 *                                          │               │
 *                                      Exception        Error
 *                                          │               │
 *                          ┌───────────────┼───────┐       ├── OutOfMemoryError
 *                          │               │       │       ├── StackOverflowError
 *                   RuntimeException  IOException  │       ├── VirtualMachineError
 *                          │               │   SQLException└── NoClassDefFoundError
 *          ┌───────────────┼───────┐       │
 *          │               │       │       ├── FileNotFoundException
 *   NullPointerEx  ArrayIndexOutOf │       └── EOFException
 *                  BoundsException │
 *                          IllegalArgumentException
 *                          ClassCastException
 *                          NumberFormatException
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                2. CHECKED vs UNCHECKED EXCEPTIONS                                              ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  ┌────────────────────────┬─────────────────────────────────────┬─────────────────────────────────────────────────┐
 *  │        Feature         │       CHECKED Exception             │        UNCHECKED Exception                      │
 *  ├────────────────────────┼─────────────────────────────────────┼─────────────────────────────────────────────────┤
 *  │ Parent Class           │ Exception (not RuntimeException)    │ RuntimeException, Error                         │
 *  │ Compile-time Check     │ Yes (must handle or declare)        │ No                                              │
 *  │ Recovery               │ Usually recoverable                 │ Usually programming error/fatal                 │
 *  │ Handle Required        │ Must catch or declare throws        │ Optional                                        │
 *  │ Examples               │ IOException, SQLException,          │ NullPointerException, ArrayIndexOutOfBounds,    │
 *  │                        │ FileNotFoundException               │ IllegalArgumentException, ClassCastException    │
 *  │ When to Use            │ External factors (file, network)    │ Programming bugs, logic errors                  │
 *  └────────────────────────┴─────────────────────────────────────┴─────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                3. EXCEPTION vs ERROR                                                           ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  ┌────────────────────────┬─────────────────────────────────────┬─────────────────────────────────────────────────┐
 *  │        Feature         │          Exception                  │               Error                             │
 *  ├────────────────────────┼─────────────────────────────────────┼─────────────────────────────────────────────────┤
 *  │ Nature                 │ Application-level issues            │ System/JVM level issues                         │
 *  │ Recovery               │ Usually recoverable                 │ Usually NOT recoverable                         │
 *  │ Should Catch?          │ Yes (checked), Maybe (unchecked)    │ Generally NO                                    │
 *  │ Caused By              │ Application code, external factors  │ JVM, system resources                           │
 *  │ Examples               │ IOException, NullPointerException   │ OutOfMemoryError, StackOverflowError            │
 *  └────────────────────────┴─────────────────────────────────────┴─────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                4. TRY-CATCH-FINALLY RULES                                                      ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │  VALID COMBINATIONS:                                                                                           │
 *  │  • try + catch                                                                                                 │
 *  │  • try + finally                                                                                               │
 *  │  • try + catch + finally                                                                                       │
 *  │  • try + multiple catch                                                                                        │
 *  │  • try + multi-catch (Java 7+): catch (IOException | SQLException e)                                           │
 *  │                                                                                                                │
 *  │  FINALLY RULES:                                                                                                │
 *  │  • Always executes (even after return in try/catch)                                                            │
 *  │  • Exception: System.exit(), JVM crash, infinite loop, thread kill                                             │
 *  │  • Finally return overrides try/catch return                                                                   │
 *  │  • Used for cleanup (closing resources)                                                                        │
 *  │                                                                                                                │
 *  │  CATCH ORDER:                                                                                                  │
 *  │  • More specific exceptions must come BEFORE more general                                                      │
 *  │  • catch(Exception e) must be last (if present)                                                                │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                5. TRY-WITH-RESOURCES (Java 7+)                                                 ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │  BEFORE Java 7:                               JAVA 7+:                                                         │
 *  │  ───────────────                               ───────                                                          │
 *  │  BufferedReader br = null;                    try (BufferedReader br =                                         │
 *  │  try {                                             new BufferedReader(new FileReader("f"))) {                  │
 *  │      br = new BufferedReader(                     return br.readLine();                                        │
 *  │          new FileReader("file"));             }  // Auto-closed!                                               │
 *  │      return br.readLine();                                                                                     │
 *  │  } finally {                                                                                                   │
 *  │      if (br != null) {                                                                                         │
 *  │          try { br.close(); }                                                                                   │
 *  │          catch (IOException e) {}                                                                              │
 *  │      }                                                                                                         │
 *  │  }                                                                                                             │
 *  │                                                                                                                │
 *  │  REQUIREMENTS:                                                                                                 │
 *  │  • Resource must implement AutoCloseable or Closeable                                                          │
 *  │  • Resources closed in REVERSE order of declaration                                                            │
 *  │  • Java 9+: Can use effectively final variables declared outside                                               │
 *  │                                                                                                                │
 *  │  SUPPRESSED EXCEPTIONS:                                                                                        │
 *  │  • If both try block and close() throw exceptions                                                              │
 *  │  • Primary exception: from try block                                                                           │
 *  │  • Suppressed exception: from close() - accessible via getSuppressed()                                         │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                6. THROW vs THROWS                                                              ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  ┌────────────────────────┬─────────────────────────────────────┬─────────────────────────────────────────────────┐
 *  │        Feature         │            throw                    │              throws                             │
 *  ├────────────────────────┼─────────────────────────────────────┼─────────────────────────────────────────────────┤
 *  │ Purpose                │ Actually throw an exception         │ Declare that method may throw                   │
 *  │ Location               │ Inside method body                  │ In method signature                             │
 *  │ Syntax                 │ throw new Exception();              │ void method() throws Exception                  │
 *  │ Number                 │ Single exception at a time          │ Multiple exceptions (comma separated)           │
 *  │ Followed By            │ Exception object (instance)         │ Exception class names                           │
 *  │ Execution              │ Immediately throws                  │ Declaration only                                │
 *  └────────────────────────┴─────────────────────────────────────┴─────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                7. BEST PRACTICES                                                               ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 *  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
 *  │                                                                                                                │
 *  │  DO:                                                                                                           │
 *  │  ───                                                                                                           │
 *  │  ✓ Use specific exceptions (not generic Exception)                                                             │
 *  │  ✓ Use try-with-resources for AutoCloseable resources                                                          │
 *  │  ✓ Log exceptions with full stack trace                                                                        │
 *  │  ✓ Throw early, catch late                                                                                     │
 *  │  ✓ Document exceptions with @throws in Javadoc                                                                 │
 *  │  ✓ Preserve original exception when re-throwing (cause)                                                        │
 *  │  ✓ Clean up resources in finally or try-with-resources                                                         │
 *  │                                                                                                                │
 *  │  DON'T:                                                                                                        │
 *  │  ───────                                                                                                       │
 *  │  ✗ Catch Exception or Throwable (too broad)                                                                    │
 *  │  ✗ Empty catch blocks (swallow exceptions)                                                                     │
 *  │  ✗ Use exceptions for flow control                                                                             │
 *  │  ✗ Throw exceptions from finally block                                                                         │
 *  │  ✗ Log and throw (double handling)                                                                             │
 *  │  ✗ Catch and ignore without comment                                                                            │
 *  │                                                                                                                │
 *  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 *
 * ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
 * ║                                8. INTERVIEW QUESTIONS                                                          ║
 * ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
 *
 * Q1: Can finally block be skipped?
 * A: Only in: System.exit(), JVM crash, infinite loop before finally, thread killed.
 *
 * Q2: What happens if exception in finally block?
 * A: Original exception is lost, finally's exception propagates. (BAD - avoid throwing in finally)
 *
 * Q3: Can we have try without catch?
 * A: Yes, with finally: try { } finally { }
 *
 * Q4: What is exception chaining?
 * A: Wrapping original exception as cause: throw new CustomException("msg", originalException);
 *
 * Q5: Can we re-throw an exception?
 * A: Yes, either same exception or wrap in new one with cause.
 *
 * Q6: Difference between final, finally, finalize?
 * A: final: keyword for constants, finalize: deprecated GC method, finally: exception cleanup block
 *
 * Q7: When to use checked vs unchecked?
 * A: Checked: recoverable external issues. Unchecked: programming errors, bugs.
 *
 * Q8: Can constructor throw exception?
 * A: Yes, both checked and unchecked.
 *
 *
 * @author Java Interview Guide
 */
public class _05_ExceptionHandling {

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════");
        System.out.println("                      EXCEPTION HANDLING DEMONSTRATION");
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════\n");

        // 1. Basic try-catch
        demonstrateBasicTryCatch();

        // 2. Multiple catch blocks
        demonstrateMultipleCatch();

        // 3. Try-with-resources
        demonstrateTryWithResources();

        // 4. Custom exceptions
        demonstrateCustomException();

        // 5. Exception chaining
        demonstrateExceptionChaining();

        // 6. Finally behavior
        demonstrateFinallyBehavior();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         1. BASIC TRY-CATCH
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateBasicTryCatch() {
        System.out.println("▶ 1. BASIC TRY-CATCH:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // ArithmeticException
        try {
            int result = 10 / 0;
        } catch (ArithmeticException e) {
            System.out.println("   Caught ArithmeticException: " + e.getMessage());
        }

        // NullPointerException
        try {
            String str = null;
            str.length();
        } catch (NullPointerException e) {
            System.out.println("   Caught NullPointerException: " + e.getMessage());
        }

        // ArrayIndexOutOfBoundsException
        try {
            int[] arr = {1, 2, 3};
            int value = arr[5];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("   Caught ArrayIndexOutOfBoundsException: " + e.getMessage());
        }

        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         2. MULTIPLE CATCH BLOCKS
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateMultipleCatch() {
        System.out.println("▶ 2. MULTIPLE CATCH BLOCKS:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // Multiple catch blocks
        try {
            String str = "abc";
            int num = Integer.parseInt(str);  // NumberFormatException
        } catch (NumberFormatException e) {
            System.out.println("   Caught NumberFormatException: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("   Caught IllegalArgumentException: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("   Caught generic Exception: " + e.getMessage());
        }

        // Multi-catch (Java 7+)
        try {
            // Some operation that might throw multiple types
            throw new IllegalArgumentException("Demo");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("   Multi-catch: " + e.getClass().getSimpleName());
        }

        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         3. TRY-WITH-RESOURCES
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateTryWithResources() {
        System.out.println("▶ 3. TRY-WITH-RESOURCES:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // Custom AutoCloseable resource
        try (MyResource resource = new MyResource("Demo Resource")) {
            resource.doSomething();
            // Resource automatically closed after this block
        } catch (Exception e) {
            System.out.println("   Exception: " + e.getMessage());
            // Access suppressed exceptions
            for (Throwable suppressed : e.getSuppressed()) {
                System.out.println("   Suppressed: " + suppressed.getMessage());
            }
        }

        // Multiple resources
        try (MyResource r1 = new MyResource("Resource1");
             MyResource r2 = new MyResource("Resource2")) {
            System.out.println("   Using both resources");
            // Resources closed in REVERSE order: r2 first, then r1
        } catch (Exception e) {
            System.out.println("   Exception: " + e.getMessage());
        }

        System.out.println();
    }

    static class MyResource implements AutoCloseable {
        private String name;

        public MyResource(String name) {
            this.name = name;
            System.out.println("   " + name + " opened");
        }

        public void doSomething() {
            System.out.println("   " + name + " doing something");
        }

        @Override
        public void close() {
            System.out.println("   " + name + " closed");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         4. CUSTOM EXCEPTIONS
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateCustomException() {
        System.out.println("▶ 4. CUSTOM EXCEPTIONS:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        try {
            validateAge(-5);
        } catch (InvalidAgeException e) {
            System.out.println("   Caught custom exception: " + e.getMessage());
            System.out.println("   Error code: " + e.getErrorCode());
        }

        try {
            processPayment(-100);
        } catch (PaymentException e) {
            System.out.println("   Caught PaymentException: " + e.getMessage());
        }

        System.out.println();
    }

    static void validateAge(int age) throws InvalidAgeException {
        if (age < 0) {
            throw new InvalidAgeException("Age cannot be negative: " + age, "AGE_001");
        }
        if (age > 150) {
            throw new InvalidAgeException("Age seems invalid: " + age, "AGE_002");
        }
    }

    static void processPayment(double amount) {
        if (amount <= 0) {
            throw new PaymentException("Invalid payment amount: " + amount);
        }
    }

    // Custom checked exception
    static class InvalidAgeException extends Exception {
        private String errorCode;

        public InvalidAgeException(String message, String errorCode) {
            super(message);
            this.errorCode = errorCode;
        }

        public String getErrorCode() {
            return errorCode;
        }
    }

    // Custom unchecked exception
    static class PaymentException extends RuntimeException {
        public PaymentException(String message) {
            super(message);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         5. EXCEPTION CHAINING
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateExceptionChaining() {
        System.out.println("▶ 5. EXCEPTION CHAINING:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        try {
            processData();
        } catch (ServiceException e) {
            System.out.println("   ServiceException: " + e.getMessage());
            System.out.println("   Root cause: " + e.getCause().getMessage());
            System.out.println();
            System.out.println("   Stack trace preview:");
            // Print first few lines of stack trace
            StackTraceElement[] trace = e.getStackTrace();
            for (int i = 0; i < Math.min(3, trace.length); i++) {
                System.out.println("      at " + trace[i]);
            }
        }

        System.out.println();
    }

    static void processData() throws ServiceException {
        try {
            readFromDatabase();
        } catch (DatabaseException e) {
            // Wrap lower-level exception with higher-level
            throw new ServiceException("Failed to process data", e);
        }
    }

    static void readFromDatabase() throws DatabaseException {
        try {
            // Simulate low-level exception
            throw new SQLException("Connection timeout");
        } catch (SQLException e) {
            throw new DatabaseException("Database read failed", e);
        }
    }

    static class DatabaseException extends Exception {
        public DatabaseException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    static class ServiceException extends Exception {
        public ServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    //                         6. FINALLY BEHAVIOR
    // ═══════════════════════════════════════════════════════════════════════════════════

    static void demonstrateFinallyBehavior() {
        System.out.println("▶ 6. FINALLY BEHAVIOR:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        // Finally always executes
        System.out.println("   Test 1: Finally always executes");
        try {
            System.out.println("   In try block");
            throw new RuntimeException("Test");
        } catch (RuntimeException e) {
            System.out.println("   In catch block");
        } finally {
            System.out.println("   In finally block (always runs)");
        }

        // Return in try vs finally
        System.out.println("\n   Test 2: Return from try with finally");
        int result = returnFromTry();
        System.out.println("   Result (try returns 1, finally executes): " + result);

        // ⚠️ Return in finally overrides try's return (BAD PRACTICE)
        System.out.println("\n   Test 3: Return in finally (BAD PRACTICE)");
        result = returnInFinally();
        System.out.println("   Result (finally return overrides try): " + result);

        System.out.println();
    }

    static int returnFromTry() {
        try {
            System.out.println("   Returning 1 from try");
            return 1;
        } finally {
            System.out.println("   Finally executes before return");
            // Don't return here! It would override try's return
        }
    }

    static int returnInFinally() {
        try {
            System.out.println("   Try returns 1");
            return 1;
        } finally {
            System.out.println("   Finally returns 2 (overrides!)");
            return 2;  // ⚠️ BAD PRACTICE - overrides try's return
        }
    }
}

