package dispatcher.strategy;

import constants.Direction;
import controller.ElevatorController;
import models.Elevator;

import java.util.List;

public class DirectionAwareStrategy implements ElevatorSelectionStrategy {

    //Can be Kept higher than the floor numbers in a Building
    private static final int WRONG_WAY_PENALTY = 1000;

    @Override
    public ElevatorController select(List<ElevatorController> controllers, int requestedFloor, Direction requestedDirection) {

        ElevatorController bestController = null;
        int minCost = Integer.MAX_VALUE;

        for (ElevatorController controller : controllers) {
            int cost = calculateCost(controller.elevator, requestedFloor, requestedDirection);

            if (cost < minCost) {
                minCost = cost;
                bestController = controller;
            }
        }

        return bestController;
    }

    private int calculateCost(Elevator elevator, int requestedFloor, Direction requestedDirection) {
        int currentFloor = elevator.getCurrentFloorNumber();
        int distance = Math.abs(requestedFloor - currentFloor);

        if (elevator.getDirection() == Direction.IDLE) {
            return distance;
        }

        if (isOnTheWay(elevator, requestedFloor, requestedDirection)) {
            return distance;
        }

        return distance + WRONG_WAY_PENALTY;
    }

    private boolean isOnTheWay(Elevator elevator, int requestedFloor, Direction requestedDirection) {

        int currentFloor = elevator.getCurrentFloorNumber();
        Direction elevatorDirection = elevator.getDirection();

        if (elevatorDirection != requestedDirection) {
            return false;
        } else if (elevatorDirection == Direction.UP) {
            return requestedFloor >= currentFloor;
        } else if (elevatorDirection == Direction.DOWN) {
            return requestedFloor <= currentFloor;
        }
        return false;
    }
}