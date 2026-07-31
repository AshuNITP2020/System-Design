// 2. LAZY INITIALIZATION
// The instance is created on first request instead of at class-load time.
// NOT thread-safe: two threads can both pass the null check and each create
// an instance.
public class DBConnectionLazy {

    private static DBConnectionLazy instance = null;

    private DBConnectionLazy() {
    }

    public static DBConnectionLazy getInstance() {
        if (instance == null) {              // two threads can both be here at once
            instance = new DBConnectionLazy();
        }
        return instance;
    }

    public void displayMessage() {
        System.out.println("Lazy Initialization - Singleton - " + this);
    }
}
