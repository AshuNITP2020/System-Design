// BAD: a "fat" interface.
// One large contract forcing every implementer to define methods it never uses.
public interface RestaurantEmployee {
    void prepareFood();
    void decideMenu();
    void serveFoodAndDrinks();
    void takeOrder();
    void cleanTheKitchen();
}
