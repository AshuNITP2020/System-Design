package dispatcher.strategy;

import constants.Direction;
import controller.ElevatorController;

import java.util.List;

public class NearestCarStrategy implements ElevatorSelectionStrategy {

    @Override
    public ElevatorController select(List<ElevatorController> elevatorControllers, int floor_number, Direction direction) {
        ElevatorController best = elevatorControllers.get(0);
        int bestDistance = Integer.MAX_VALUE;
        for (ElevatorController elevatorController : elevatorControllers) {
            int distance = Math.abs(elevatorController.getElevator().getCurrentFloorNumber() - floor_number);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = elevatorController;
            }
        }
        return best;
    }
}
