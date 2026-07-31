// 4. DOUBLE-CHECKED LOCKING
// Only the first call enters the synchronized block; later calls short-circuit
// on the first null check. Widely used in industry.
//
// WARNING: as written below it is still subtly broken - see
// DBConnectionDoubleCheckedLockFix for the missing keyword.
public class DBConnectionDoubleLocking {

    private static DBConnectionDoubleLocking instance = null;

    private DBConnectionDoubleLocking() {
    }

    public static DBConnectionDoubleLocking getInstance() {
        if (instance == null) {                                  // first check - no lock
            synchronized (DBConnectionDoubleLocking.class) {
                if (instance == null) {                          // second check - under lock
                    instance = new DBConnectionDoubleLocking();
                }
            }
        }
        return instance;
    }

    public void displayMessage() {
        System.out.println("Double Locking Singleton - " + this);
    }
}
