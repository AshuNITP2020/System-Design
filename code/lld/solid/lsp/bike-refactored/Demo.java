public class Demo {
    public static void main(String[] args) {
        MotorCycle motorCycle = new MotorCycle("HeroHonda", 10);
        Bicycle bicycle = new Bicycle("Hercules", true, 10);

        motorCycle.turnOnEngine();
        motorCycle.accelerate();
        motorCycle.applyBrakes();
        motorCycle.turnOffEngine();

        // Every call below is guaranteed to work
        bicycle.accelerate();
        bicycle.applyBrakes();
    }
}
