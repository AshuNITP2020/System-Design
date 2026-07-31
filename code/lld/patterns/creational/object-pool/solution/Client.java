public class Client {
    public static void main(String[] args) {
        System.out.println("===== Object Pool + Singleton =====");
        DBConnectionPoolManager poolManager = DBConnectionPoolManager.getInstance();

        DBConnection c1 = poolManager.getDBConnection();
        DBConnection c2 = poolManager.getDBConnection();
        DBConnection c3 = poolManager.getDBConnection();
        DBConnection c4 = poolManager.getDBConnection();
        DBConnection c5 = poolManager.getDBConnection();
        DBConnection c6 = poolManager.getDBConnection();

        DBConnection none = poolManager.getDBConnection();
        System.out.println(none == null ? "refused - pool is full." : "not null?");

        poolManager.releaseDBConnection(c6);
        poolManager.getDBConnection();          // reuses it, no new connection opened

        System.out.println("\nA second client asks for the pool:");
        DBConnectionPoolManager poolManager2 = DBConnectionPoolManager.getInstance();
        System.out.println("Same pool? " + (poolManager == poolManager2));
        // MAX_POOL_SIZE now means what it says.
    }
}
