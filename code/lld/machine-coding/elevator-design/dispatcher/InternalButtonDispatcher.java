package dispatcher;

import controller.ElevatorController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InternalButtonDispatcher {

    private static InternalButtonDispatcher internalButtonDispatcher;

    private final Map<Integer, ElevatorController> controllersByElevatorId = new HashMap<>();

    private InternalButtonDispatcher(List<ElevatorController> elevatorControllers) {
        for (ElevatorController elevatorController : elevatorControllers) {
            controllersByElevatorId.put(elevatorController.getElevator().getElevatorId(), elevatorController);
        }
    }

    public static void init(List<ElevatorController> elevatorControllers) {
        internalButtonDispatcher = new InternalButtonDispatcher(elevatorControllers);
    }

    public static InternalButtonDispatcher getInstance() {
        return internalButtonDispatcher;
    }

    public void dispatch(int elevator_id, int floor_number) {
        ElevatorController elevatorController = controllersByElevatorId.get(elevator_id);
        elevatorController.acceptDestination(floor_number);
    }
}
