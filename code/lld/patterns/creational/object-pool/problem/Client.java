public class Client {
    public static void main(String[] args) {
        System.out.println("===== Object Pool: the problem =====");
        DBConnectionPoolManager poolManager = new DBConnectionPoolManager();

        // Borrow up to MAX_POOL_SIZE (6)
        DBConnection c1 = poolManager.getDBConnection();
        DBConnection c2 = poolManager.getDBConnection();
        DBConnection c3 = poolManager.getDBConnection();
        DBConnection c4 = poolManager.getDBConnection();
        DBConnection c5 = poolManager.getDBConnection();
        DBConnection c6 = poolManager.getDBConnection();

        // The 7th is correctly refused
        DBConnection none = poolManager.getDBConnection();
        System.out.println(none == null ? "refused - pool is full." : "not null?");

        poolManager.releaseDBConnection(c6);
        poolManager.getDBConnection();          // reuses the returned one

        // ****** The hole in this design ******
        System.out.println("\nA second client just does this:");
        DBConnectionPoolManager poolManager2 = new DBConnectionPoolManager();
        System.out.println("Same pool? " + (poolManager == poolManager2));
        // ...and MAX_POOL_SIZE is now meaningless: 6 + 6 connections, two sets
        // of tracking lists, and nothing reconciling them.
    }
}
