package dispatcher;

import constants.Direction;
import controller.ElevatorController;
import models.Elevator;

import java.util.List;

public class InternalButtonDispatcher {
    List<ElevatorController> elevatorControllers;

    public InternalButtonDispatcher(List<ElevatorController> elevatorControllers) {
        this.elevatorControllers = elevatorControllers;
    }

    public void dispatch(int elevator_id, int floor_number) {
        ElevatorController elevatorController = elevatorControllers.get(elevator_id);
        elevatorController.acceptNewRequest(floor_number);
    }
}
