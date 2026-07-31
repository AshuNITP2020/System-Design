// 1. EAGER INITIALIZATION
// The instance is created when the class is loaded.
public class DBConnectionEager {

    // static -> one instance shared by everyone
    // final  -> can never be reassigned
    private static final DBConnectionEager instance = new DBConnectionEager();

    // private constructor -> nobody else can call new
    private DBConnectionEager() {
    }

    public static DBConnectionEager getInstance() {
        return instance;
    }

    public void displayMessage() {
        System.out.println("Eager Initialization - Singleton - " + this);
    }
}
