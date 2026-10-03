package dispatcher.strategy;

import constants.Direction;
import controller.ElevatorController;

import java.util.List;

public interface ElevatorSelectionStrategy {
    ElevatorController select(List<ElevatorController> elevatorControllers, int floor_number, Direction direction);
}
