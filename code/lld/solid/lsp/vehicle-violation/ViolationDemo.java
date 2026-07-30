import java.util.ArrayList;
import java.util.List;

// Running this CRASHES on purpose with a NullPointerException.
public class ViolationDemo {
    public static void main(String[] args) {
        // Happy flow
        List<Vehicle> vehicleList = new ArrayList<>();
        vehicleList.add(new MotorCycle());
        vehicleList.add(new Car());
        for (Vehicle vehicle : vehicleList) {
            System.out.println(vehicle.hasEngine().toString());
        }

        // Add a Bicycle - the same loop now breaks
        List<Vehicle> vehicleList2 = new ArrayList<>();
        vehicleList2.add(new MotorCycle());
        vehicleList2.add(new Car());
        vehicleList2.add(new Bicycle());
        for (Vehicle vehicle : vehicleList2) {
            System.out.println(vehicle.hasEngine().toString());  // throws NPE
            // Client code breaks for Bicycle
        }
    }
}
