// Roles compose: an employee who genuinely does two jobs implements two
// interfaces - and only then.
public class Manager implements ChefTasks, MaintenanceTasks {

    @Override
    public void prepareFood() {
        System.out.println("Preparing food...");
    }

    @Override
    public void decideMenu() {
        System.out.println("Deciding menu...");
    }

    @Override
    public void cleanTheKitchen() {
        System.out.println("Cleaning the kitchen...");
    }

    @Override
    public void reStockGroceries() {
        System.out.println("Restocking groceries...");
    }
}
