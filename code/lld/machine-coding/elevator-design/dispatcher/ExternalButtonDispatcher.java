package dispatcher;

import constants.Direction;
import controller.ElevatorController;
import dispatcher.strategy.ElevatorSelectionStrategy;
import dispatcher.strategy.NearestCarStrategy;

import java.util.List;

public class ExternalButtonDispatcher {
    private static List<ElevatorController> elevatorControllers;
    private static ExternalButtonDispatcher externalButtonDispatcher;
    private ElevatorSelectionStrategy selectionStrategy = new NearestCarStrategy();

    public ExternalButtonDispatcher(List<ElevatorController> elevatorControllers) {
        ExternalButtonDispatcher.elevatorControllers = elevatorControllers;
    }

    public static ExternalButtonDispatcher getInstance() {
        if (externalButtonDispatcher == null) {
            externalButtonDispatcher = new ExternalButtonDispatcher(elevatorControllers);
        }
        return externalButtonDispatcher;
    }

    public void setSelectionStrategy(ElevatorSelectionStrategy selectionStrategy) {
        this.selectionStrategy = selectionStrategy;
    }

    public void dispatch(int floor_number, Direction direction) {
        ElevatorController elevatorController = selectionStrategy.select(elevatorControllers, floor_number, direction);
        elevatorController.acceptNewRequest(floor_number);
    }
}
