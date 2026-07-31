// THE BUG in plain double-checked locking.
//
// `connectionObj = new DBConnectionDoubleCheckedLockIssue(5567)` is not one
// step. It is three:
//     1. allocate memory
//     2. run the constructor, initialising portNumber
//     3. assign the reference to connectionObj
//
// Issue 1 - INSTRUCTION REORDERING: the JVM is allowed to reorder 2 and 3.
// If it assigns the reference first, another thread passing the outer null
// check sees a non-null object whose portNumber is still 0.
//
// Issue 2 - CPU CACHING: each core has its own L1 cache. A fully built object
// written by T1 may not be visible to T2 yet, so T2 builds a second instance.
public class DBConnectionDoubleCheckedLockIssue {

    private static DBConnectionDoubleCheckedLockIssue connectionObj = null;

    int portNumber;

    private DBConnectionDoubleCheckedLockIssue(int portNumberValue) {
        portNumber = portNumberValue;
    }

    public static DBConnectionDoubleCheckedLockIssue getConnectionObj() {
        if (connectionObj == null) {                                    // first check
            synchronized (DBConnectionDoubleCheckedLockIssue.class) {
                if (connectionObj == null) {                            // second check
                    connectionObj = new DBConnectionDoubleCheckedLockIssue(5567);
                }
            }
        }
        return connectionObj;
    }

    public void displayMessage() {
        System.out.println("DCL - Issue version - port " + portNumber + " - " + this);
    }
}
