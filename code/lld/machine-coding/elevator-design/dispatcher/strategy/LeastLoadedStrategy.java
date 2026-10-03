package dispatcher.strategy;

import constants.Direction;
import controller.ElevatorController;

import java.util.List;

// Sends the car with the fewest pending stops, spreading work evenly across the fleet.
public class LeastLoadedStrategy implements ElevatorSelectionStrategy {

    @Override
    public ElevatorController select(List<ElevatorController> elevatorControllers, int floor_number, Direction direction) {
        ElevatorController best = elevatorControllers.get(0);
        int bestLoad = Integer.MAX_VALUE;
        for (ElevatorController elevatorController : elevatorControllers) {
            int load = elevatorController.pendingRequestCount();
            if (load < bestLoad) {
                bestLoad = load;
                best = elevatorController;
            }
        }
        return best;
    }
}
