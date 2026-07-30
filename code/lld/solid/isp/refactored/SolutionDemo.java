public class SolutionDemo {
    public static void main(String[] args) {
        Chef chef = new Chef();
        Waiter waiter = new Waiter();
        Manager manager = new Manager();

        // Every call is valid - no forced implementations, nothing throws
        chef.prepareFood();
        chef.decideMenu();

        waiter.takeOrder();
        waiter.serveFoodAndDrinks();

        manager.decideMenu();
        manager.reStockGroceries();
    }
}
