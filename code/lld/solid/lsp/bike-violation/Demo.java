// Running this CRASHES on purpose - that is the lesson.
// Expected output: the MotorCycle lines print, then an AssertionError is thrown.
public class Demo {
    public static void main(String[] args) {
        MotorCycle motorCycle = new MotorCycle("HeroHonda", 10);
        Bicycle bicycle = new Bicycle("Hercules", true, 10);

        // Works fine - MotorCycle implements all Bike behaviour
        motorCycle.turnOnEngine();
        motorCycle.accelerate();
        motorCycle.applyBrakes();
        motorCycle.turnOffEngine();

        // The client expects the same behaviour from any Bike
        bicycle.turnOnEngine();   // throws - fails to implement Bike behaviour
        bicycle.accelerate();
        bicycle.applyBrakes();
        bicycle.turnOffEngine();  // throws - fails to implement Bike behaviour
    }
}
