import java.util.ArrayList;
import java.util.List;

// Object Pool + Singleton. The pool is only a real limit if there is exactly
// one of it, and borrow/return must be thread-safe because callers race.
public class DBConnectionPoolManager {

    private static volatile DBConnectionPoolManager instance = null;

    List<DBConnection> freeConnections = new ArrayList<>();
    List<DBConnection> inUseConnections = new ArrayList<>();
    int INITIAL_POOL_SIZE = 3;
    int MAX_POOL_SIZE = 6;

    private DBConnectionPoolManager() {
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            freeConnections.add(new DBConnection());
        }
    }

    // Double-checked locking, with the volatile field above
    public static DBConnectionPoolManager getInstance() {
        if (instance == null) {
            synchronized (DBConnectionPoolManager.class) {
                if (instance == null) {
                    instance = new DBConnectionPoolManager();
                }
            }
        }
        return instance;
    }

    // synchronized: two threads must not hand out the same connection
    public synchronized DBConnection getDBConnection() {
        if (freeConnections.isEmpty() && inUseConnections.size() < MAX_POOL_SIZE) {
            freeConnections.add(new DBConnection());
        } else if (freeConnections.isEmpty() && inUseConnections.size() >= MAX_POOL_SIZE) {
            System.out.println("Pool is full. Cannot create new DBConnection.");
            return null;
        }
        DBConnection dbConnection = freeConnections.remove(freeConnections.size() - 1);
        inUseConnections.add(dbConnection);
        System.out.println("borrowed " + dbConnection
                + "  free=" + freeConnections.size() + " inUse=" + inUseConnections.size());
        return dbConnection;
    }

    public synchronized void releaseDBConnection(DBConnection dbConnection) {
        if (dbConnection != null) {
            inUseConnections.remove(dbConnection);
            freeConnections.add(dbConnection);
            System.out.println("returned " + dbConnection
                    + "  free=" + freeConnections.size() + " inUse=" + inUseConnections.size());
        } else {
            System.out.println("DBConnection is null. Cannot release.");
        }
    }
}
