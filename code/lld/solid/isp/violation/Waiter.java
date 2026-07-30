// BAD: the Waiter is forced to implement methods it does not need,
// producing a bloated class full of error-throwing methods.
public class Waiter implements RestaurantEmployee {

    @Override
    public void takeOrder() {
        System.out.println("Taking order...");
    }

    @Override
    public void serveFoodAndDrinks() {
        System.out.println("Serving food and drinks...");
    }

    @Override
    public void cleanTheKitchen() {
        // Forced to implement, but meaningless for a waiter
        throw new AssertionError("Detail Message: Waiter cannot clean the kitchen!");
    }

    @Override
    public void prepareFood() {
        // Forced to implement, but meaningless for a waiter
        throw new AssertionError("Detail Message: Waiter cannot prepare food!");
    }

    @Override
    public void decideMenu() {
        // Forced to implement, but meaningless for a waiter
        throw new AssertionError("Detail Message: Waiter cannot decide the menu!");
    }
}
