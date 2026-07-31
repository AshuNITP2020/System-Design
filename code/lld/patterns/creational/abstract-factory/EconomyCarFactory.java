// Concrete Factory - produces one complete family
public class EconomyCarFactory implements CarFactory {
    private final String brand;

    public EconomyCarFactory(String brand) {
        this.brand = brand;
    }

    @Override
    public CarInterior createInterior() {
        return new EconomyCarInterior();
    }

    @Override
    public CarExterior createExterior() {
        return new EconomyCarExterior();
    }
}
