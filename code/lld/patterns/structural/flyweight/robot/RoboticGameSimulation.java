public class RoboticGameSimulation {
    public static void main(String[] args) {
        System.out.println("====== Flyweight Design Pattern ======");
        // Factory creates; Flyweight reuses.

        IRobot humanoidRobot1 = RoboticFactory.createRobot("HUMANOID");
        humanoidRobot1.display(1, 2);
        IRobot humanoidRobot2 = RoboticFactory.createRobot("HUMANOID");
        humanoidRobot2.display(10, 30);

        IRobot roboDog1 = RoboticFactory.createRobot("ROBOTIC_DOG");
        roboDog1.display(2, 9);
        IRobot roboDog2 = RoboticFactory.createRobot("ROBOTIC_DOG");
        roboDog2.display(11, 19);

        System.out.println("Same humanoid instance? " + (humanoidRobot1 == humanoidRobot2));
        System.out.println("Robot objects actually created: " + RoboticFactory.getTotalRobots());
        // 4 robots drawn on screen, 2 objects in memory. At 10 lakh robots the
        // naive version needs ~40 GB; this needs two Sprites.
    }
}
