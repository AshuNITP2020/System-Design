import constants.Direction;
import controller.ElevatorController;
import dispatcher.ExternalButtonDispatcher;
import dispatcher.strategy.DirectionAwareStrategy;
import dispatcher.strategy.ElevatorSelectionStrategy;
import dispatcher.strategy.LeastLoadedStrategy;
import dispatcher.strategy.NearestCarStrategy;
import models.Building;
import models.Elevator;
import models.InternalButton;

import java.util.List;

public class DemoElevatorDesign {

    private static final int MIN_FLOOR = 0;
    private static final int MAX_FLOOR = 10;

    public static void main(String[] args) {
        scenarioMidFlightPickup();
        System.out.println();
        scenarioFinishDirectionBeforeReversing();
        System.out.println();
        scenarioServeOnMatchingSweep();
        System.out.println();
        scenarioOperatorPicksStrategy();
        System.out.println();
        scenarioFullyWiredBuilding();
    }

    private static void scenarioMidFlightPickup() {
        System.out.println("=== car starts idle at 3, rider inside presses 8 ===");
        Elevator elevator = new Elevator(1, 3, Direction.IDLE);
        ElevatorController controller = new ElevatorController(elevator, MIN_FLOOR, MAX_FLOOR);
        controller.acceptDestination(8);

        boolean injected = false;
        while (controller.hasPendingRequests() || elevator.getDirection() != Direction.IDLE) {
            Integer servedFloor = controller.step();
            System.out.println("tick -> floor " + elevator.getCurrentFloorNumber()
                    + ", direction " + elevator.getDirection()
                    + (servedFloor != null ? "   *** doors open at " + servedFloor + " ***" : ""));

            if (!injected && elevator.getCurrentFloorNumber() == 5) {
                injected = true;
                System.out.println("   << rider on floor 6 presses UP >>");
                controller.acceptHallCall(6, Direction.UP);
            }
        }
    }

    private static void scenarioFinishDirectionBeforeReversing() {
        System.out.println("=== car at 7 descending, pickup at 5 (DOWN), pickup at 9 (UP) ===");
        Elevator elevator = new Elevator(2, 7, Direction.DOWN);
        ElevatorController controller = new ElevatorController(elevator, MIN_FLOOR, MAX_FLOOR);
        controller.acceptHallCall(5, Direction.DOWN);
        controller.acceptHallCall(9, Direction.UP);

        runUntilIdle(controller, elevator);
    }

    private static void scenarioServeOnMatchingSweep() {
        System.out.println("=== car idle at 3 with destination 8; rider on floor 6 presses DOWN ===");
        Elevator elevator = new Elevator(3, 3, Direction.IDLE);
        ElevatorController controller = new ElevatorController(elevator, MIN_FLOOR, MAX_FLOOR);
        controller.acceptDestination(8);
        controller.acceptHallCall(6, Direction.DOWN);
        controller.acceptHallCall(6, Direction.DOWN);

        runUntilIdle(controller, elevator);
    }

    private static void scenarioOperatorPicksStrategy() {
        System.out.println("=== rider on floor 6 presses UP; operator tries each dispatch strategy ===");
        List<ElevatorSelectionStrategy> strategies = List.of(
                new NearestCarStrategy(),
                new DirectionAwareStrategy(),
                new LeastLoadedStrategy());

        for (ElevatorSelectionStrategy strategy : strategies) {
            ElevatorController car1 = new ElevatorController(new Elevator(1, 7, Direction.DOWN), MIN_FLOOR, MAX_FLOOR);
            ElevatorController car2 = new ElevatorController(new Elevator(2, 3, Direction.IDLE), MIN_FLOOR, MAX_FLOOR);
            ElevatorController car3 = new ElevatorController(new Elevator(3, 5, Direction.UP), MIN_FLOOR, MAX_FLOOR);
            car1.acceptDestination(1);
            car3.acceptDestination(10);
            List<ElevatorController> fleet = List.of(car1, car2, car3);

            int[] loadBefore = new int[fleet.size()];
            for (int i = 0; i < fleet.size(); i++) {
                loadBefore[i] = fleet.get(i).pendingRequestCount();
            }

            ExternalButtonDispatcher.init(fleet);
            ExternalButtonDispatcher dispatcher = ExternalButtonDispatcher.getInstance();
            dispatcher.setSelectionStrategy(strategy);
            dispatcher.dispatch(6, Direction.UP);

            for (int i = 0; i < fleet.size(); i++) {
                if (fleet.get(i).pendingRequestCount() > loadBefore[i]) {
                    System.out.println(strategy.getClass().getSimpleName()
                            + " -> sends elevator " + fleet.get(i).getElevator().getElevatorId());
                }
            }
        }
    }

    private static void scenarioFullyWiredBuilding() {
        System.out.println("=== building with floors 0..10; presses go through the real buttons ===");
        Elevator car1 = new Elevator(1, 0, Direction.IDLE);
        Elevator car2 = new Elevator(2, 9, Direction.IDLE);
        Building building = new Building(MAX_FLOOR + 1, List.of(car1, car2));

        building.getFloor(6).pressDown();
        new InternalButton(1, 4).press();

        List<ElevatorController> fleet = building.getElevatorControllers();
        boolean busy = true;
        while (busy) {
            busy = false;
            for (ElevatorController controller : fleet) {
                Elevator elevator = controller.getElevator();
                if (controller.hasPendingRequests() || elevator.getDirection() != Direction.IDLE) {
                    Integer servedFloor = controller.step();
                    System.out.println("car " + elevator.getElevatorId() + " " + elevator.getDisplay()
                            + (servedFloor != null ? "   *** doors open at " + servedFloor + " ***" : ""));
                    busy = busy || controller.hasPendingRequests() || elevator.getDirection() != Direction.IDLE;
                }
            }
        }

        try {
            fleet.get(0).acceptDestination(42);
        } catch (IllegalArgumentException e) {
            System.out.println("request for floor 42 rejected: " + e.getMessage());
        }
    }

    private static void runUntilIdle(ElevatorController controller, Elevator elevator) {
        while (controller.hasPendingRequests() || elevator.getDirection() != Direction.IDLE) {
            Integer servedFloor = controller.step();
            System.out.println("tick -> floor " + elevator.getCurrentFloorNumber()
                    + ", direction " + elevator.getDirection()
                    + (servedFloor != null ? "   *** doors open at " + servedFloor + " ***" : ""));
        }
    }
}
