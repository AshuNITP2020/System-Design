// THE FIX: one keyword - volatile.
//
// volatile gives two guarantees that are exactly what double-checked locking
// was missing:
//   * Visibility  - reads and writes go to main memory, so a value written by
//                   one thread is immediately visible to every other thread.
//                   [fixes the CPU-caching issue]
//   * Ordering    - it establishes a happens-before edge, a memory barrier the
//                   compiler and CPU may not reorder across, so the reference
//                   cannot be published before the object is fully built.
//                   [fixes the instruction-reordering issue]
public class DBConnectionDoubleCheckedLockFix {

    private static volatile DBConnectionDoubleCheckedLockFix connectionObj = null;

    int portNumber;

    private DBConnectionDoubleCheckedLockFix(int portNumberValue) {
        portNumber = portNumberValue;
    }

    public static DBConnectionDoubleCheckedLockFix getConnectionObj(int portNumberValue) {
        if (connectionObj == null) {
            synchronized (DBConnectionDoubleCheckedLockFix.class) {
                if (connectionObj == null) {
                    connectionObj = new DBConnectionDoubleCheckedLockFix(portNumberValue);
                }
            }
        }
        return connectionObj;
    }

    public void displayMessage() {
        System.out.println("DCL - Fixed version - port " + portNumber + " - " + this);
    }
}
