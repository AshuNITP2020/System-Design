// Running this CRASHES on purpose at the first forced implementation.
public class ViolationDemo {
    public static void main(String[] args) {
        Waiter waiter = new Waiter();

        waiter.takeOrder();            // works
        waiter.serveFoodAndDrinks();   // works

        waiter.prepareFood();          // throws - forced implementation
        waiter.decideMenu();           // throws - forced implementation
        waiter.cleanTheKitchen();      // throws - forced implementation
    }
}
