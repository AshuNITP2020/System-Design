package dispatcher;

import constants.Direction;
import controller.ElevatorController;
import dispatcher.strategy.ElevatorSelectionStrategy;
import dispatcher.strategy.NearestCarStrategy;

import java.util.List;

public class ExternalButtonDispatcher {

    private static ExternalButtonDispatcher externalButtonDispatcher;

    private final List<ElevatorController> elevatorControllers;
    private ElevatorSelectionStrategy selectionStrategy = new NearestCarStrategy();

    private ExternalButtonDispatcher(List<ElevatorController> elevatorControllers) {
        this.elevatorControllers = elevatorControllers;
    }

    public static void init(List<ElevatorController> elevatorControllers) {
        externalButtonDispatcher = new ExternalButtonDispatcher(elevatorControllers);
    }

    public static ExternalButtonDispatcher getInstance() {
        if (externalButtonDispatcher == null) {
            throw new IllegalStateException("ExternalButtonDispatcher.init(fleet) must be called before use");
        }
        return externalButtonDispatcher;
    }

    public void setSelectionStrategy(ElevatorSelectionStrategy selectionStrategy) {
        this.selectionStrategy = selectionStrategy;
    }

    public void dispatch(int floor_number, Direction direction) {
        ElevatorController elevatorController = selectionStrategy.select(elevatorControllers, floor_number, direction);
        elevatorController.acceptHallCall(floor_number, direction);
    }
}
