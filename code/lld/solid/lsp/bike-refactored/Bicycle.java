// GOOD: a Bicycle has no engine, so it simply does not implement Engine.
// Nothing forces it to fake a method it cannot support.
public class Bicycle extends Bike {
    String brand;
    Boolean hasGears;
    int speed;

    public Bicycle(String brand, Boolean hasGears, int speed) {
        this.brand = brand;
        this.hasGears = hasGears;
        this.speed = speed;
    }

    @Override
    public void accelerate() {
        this.speed = this.speed + 10;
        System.out.println("Bicycle Speed: " + this.speed);
    }

    @Override
    public void applyBrakes() {
        this.speed = this.speed - 5;
        System.out.println("Bicycle Speed: " + this.speed);
    }
}
