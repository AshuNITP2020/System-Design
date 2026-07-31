public class SingletonDemo {
    public static void main(String[] args) {

        System.out.println("====== Eager Initialization ======");
        // DBConnectionEager x = new DBConnectionEager();   // compilation error
        DBConnectionEager eager1 = DBConnectionEager.getInstance();
        DBConnectionEager eager2 = DBConnectionEager.getInstance();
        eager1.displayMessage();
        eager2.displayMessage();
        System.out.println("Same instance? " + (eager1 == eager2));

        System.out.println("\n====== Lazy Initialization ======");
        DBConnectionLazy lazy1 = DBConnectionLazy.getInstance();
        DBConnectionLazy lazy2 = DBConnectionLazy.getInstance();
        lazy1.displayMessage();
        System.out.println("Same instance? " + (lazy1 == lazy2));

        System.out.println("\n====== Thread Safe ======");
        DBConnectionThreadSafe ts1 = DBConnectionThreadSafe.getInstance();
        DBConnectionThreadSafe ts2 = DBConnectionThreadSafe.getInstance();
        ts1.displayMessage();
        System.out.println("Same instance? " + (ts1 == ts2));

        System.out.println("\n====== Double-Checked Locking ======");
        DBConnectionDoubleLocking dcl1 = DBConnectionDoubleLocking.getInstance();
        DBConnectionDoubleLocking dcl2 = DBConnectionDoubleLocking.getInstance();
        dcl1.displayMessage();
        System.out.println("Same instance? " + (dcl1 == dcl2));

        System.out.println("\n====== DCL with volatile (correct) ======");
        DBConnectionDoubleCheckedLockFix fix1 =
                DBConnectionDoubleCheckedLockFix.getConnectionObj(5567);
        DBConnectionDoubleCheckedLockFix fix2 =
                DBConnectionDoubleCheckedLockFix.getConnectionObj(9999);
        fix1.displayMessage();
        // Note: the second call's argument is ignored - the instance already exists.
        fix2.displayMessage();
        System.out.println("Same instance? " + (fix1 == fix2));
    }
}
