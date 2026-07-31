// The reusable resource. Expensive to build - that is the whole reason a pool
// exists.
//
// Production code would open a real socket here:
//     mySQLConnection = DriverManager.getConnection(
//             "jdbc:mysql://localhost:3306/DB", "root", "root");
// This sample simulates the cost so it runs without a database.
public class DBConnection {

    private static int counter = 0;
    private final int id;

    public DBConnection() {
        this.id = ++counter;
        try {
            Thread.sleep(30);     // stand-in for the real handshake
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("  (expensive) opened DBConnection #" + id);
    }

    @Override
    public String toString() {
        return "DBConnection#" + id;
    }
}
