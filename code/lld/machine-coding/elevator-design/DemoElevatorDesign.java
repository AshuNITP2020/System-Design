import constants.Direction;
import controller.ElevatorController;
import dispatcher.ExternalButtonDispatcher;
import dispatcher.strategy.DirectionAwareStrategy;
import dispatcher.strategy.ElevatorSelectionStrategy;
import dispatcher.strategy.LeastLoadedStrategy;
import dispatcher.strategy.NearestCarStrategy;
import models.Elevator;

import java.util.List;

public class DemoElevatorDesign {

    public static void main(String[] args) {
        scenarioMidFlightPickup();
        System.out.println();
        scenarioFinishDirectionBeforeReversing();
        System.out.println();
        scenarioOperatorPicksStrategy();
    }

    private static void scenarioMidFlightPickup() {
        System.out.println("=== car starts idle at 3, rider inside presses 8 ===");
        Elevator elevator = new Elevator(1, 3, Direction.IDLE);
        ElevatorController controller = new ElevatorController(elevator);
        controller.acceptNewRequest(8);

        boolean injected = false;
        while (controller.hasPendingRequests() || elevator.getDirection() != Direction.IDLE) {
            Integer servedFloor = controller.step();
            System.out.println("tick -> floor " + elevator.getCurrentFloorNumber()
                    + ", direction " + elevator.getDirection()
                    + (servedFloor != null ? "   *** doors open at " + servedFloor + " ***" : ""));

            if (!injected && elevator.getCurrentFloorNumber() == 5) {
                injected = true;
                System.out.println("   << rider on floor 6 presses UP >>");
                controller.acceptNewRequest(6);
            }
        }
    }

    private static void scenarioOperatorPicksStrategy() {
        System.out.println("=== rider on floor 6 presses UP; operator tries each dispatch strategy ===");
        List<ElevatorSelectionStrategy> strategies = List.of(
                new NearestCarStrategy(),
                new DirectionAwareStrategy(),
                new LeastLoadedStrategy());

        for (ElevatorSelectionStrategy strategy : strategies) {
            // fresh fleet each round: car 1 at 7 heading down (stop at 1),
            // car 2 idle at 3, car 3 at 5 heading up (stop at 10)
            ElevatorController car1 = new ElevatorController(new Elevator(1, 7, Direction.DOWN));
            ElevatorController car2 = new ElevatorController(new Elevator(2, 3, Direction.IDLE));
            ElevatorController car3 = new ElevatorController(new Elevator(3, 5, Direction.UP));
            car1.acceptNewRequest(1);
            car3.acceptNewRequest(10);
            List<ElevatorController> fleet = List.of(car1, car2, car3);

            int[] loadBefore = new int[fleet.size()];
            for (int i = 0; i < fleet.size(); i++) {
                loadBefore[i] = fleet.get(i).pendingRequestCount();
            }

            ExternalButtonDispatcher dispatcher = new ExternalButtonDispatcher(fleet);
            dispatcher.setSelectionStrategy(strategy);
            dispatcher.dispatch(6, Direction.UP);

            for (int i = 0; i < fleet.size(); i++) {
                if (fleet.get(i).pendingRequestCount() > loadBefore[i]) {
                    System.out.println(strategy.getClass().getSimpleName()
                            + " -> sends elevator " + fleet.get(i).elevator.getElevatorId());
                }
            }
        }
    }

    private static void scenarioFinishDirectionBeforeReversing() {
        System.out.println("=== car at 7 descending, pickup at 5, rider presses 9 ===");
        Elevator elevator = new Elevator(2, 7, Direction.DOWN);
        ElevatorController controller = new ElevatorController(elevator);
        controller.acceptNewRequest(5);
        controller.acceptNewRequest(9);

        while (controller.hasPendingRequests() || elevator.getDirection() != Direction.IDLE) {
            Integer servedFloor = controller.step();
            System.out.println("tick -> floor " + elevator.getCurrentFloorNumber()
                    + ", direction " + elevator.getDirection()
                    + (servedFloor != null ? "   *** doors open at " + servedFloor + " ***" : ""));
        }
    }
}
