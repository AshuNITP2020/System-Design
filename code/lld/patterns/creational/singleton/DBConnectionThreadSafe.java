// 3. THREAD-SAFE / SYNCHRONIZED
// Correct, but every single call pays for the lock - even the millionth one,
// long after the instance exists. 100 threads calling getInstance() means 99
// of them waiting.
public class DBConnectionThreadSafe {

    private static DBConnectionThreadSafe instance = null;

    private DBConnectionThreadSafe() {
    }

    public static synchronized DBConnectionThreadSafe getInstance() {
        if (instance == null) {
            instance = new DBConnectionThreadSafe();
        }
        return instance;
    }

    public void displayMessage() {
        System.out.println("Thread Safe Singleton - " + this);
    }
}
