public class AbstractFactoryDemo {
    public static void main(String[] args) {
        System.out.println("===== Abstract Factory =====");

        CarFactoryProvider carFactoryProvider = new CarFactoryProvider();

        CarFactory economyCar = carFactoryProvider.getFactory(CarType.ECONOMY, "Honda");
        economyCar.produceCompleteVehicle();

        CarFactory luxuryCar = carFactoryProvider.getFactory(CarType.LUXURY, "Mercedes");
        luxuryCar.produceCompleteVehicle();

        CarFactory premiumCar = carFactoryProvider.getFactory(CarType.PREMIUM, "Rolls Royce");
        premiumCar.produceCompleteVehicle();
    }
}
