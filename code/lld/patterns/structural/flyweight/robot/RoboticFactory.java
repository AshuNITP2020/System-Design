import java.util.HashMap;
import java.util.Map;

// Flyweight Factory: the cache. Callers must go through it - if they can call
// the constructor directly, sharing breaks.
public class RoboticFactory {

    private static final Map<String, IRobot> roboticObjectCache = new HashMap<>();

    public static IRobot createRobot(String robotType) {
        if (roboticObjectCache.containsKey(robotType)) {
            return roboticObjectCache.get(robotType);          // reuse
        }
        if (robotType.equals("HUMANOID")) {
            IRobot humanoidObject = new HumanoidRobot(robotType, new Sprites());
            roboticObjectCache.put(robotType, humanoidObject);
            return humanoidObject;
        } else if (robotType.equals("ROBOTIC_DOG")) {
            IRobot roboticDogObject = new RoboticDog(robotType, new Sprites());
            roboticObjectCache.put(robotType, roboticDogObject);
            return roboticDogObject;
        }
        throw new IllegalArgumentException("Invalid robot type: " + robotType);
    }

    public static int getTotalRobots() {
        return roboticObjectCache.size();
    }
}
