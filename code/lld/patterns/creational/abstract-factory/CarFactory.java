// Abstract Factory: one creation method per product in the family.
// This is what makes it a "factory of factories" rather than a plain Factory.
public interface CarFactory {

    CarInterior createInterior();

    CarExterior createExterior();

    // Template method - guarantees interior and exterior come from the SAME family
    default void produceCompleteVehicle() {
        System.out.println("Starting complete vehicle production...");

        CarInterior interior = createInterior();
        CarExterior exterior = createExterior();

        interior.addInteriorComponents();
        exterior.addExteriorComponents();

        System.out.println("Vehicle production completed!");
    }
}
