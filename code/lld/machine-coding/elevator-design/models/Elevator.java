package models;

import constants.Direction;

public class Elevator {

    private final int elevator_id;
    private int currentFloorNumber;
    private Direction direction;
    private final Display display = new Display();

    public Elevator(int elevator_id, int currentFloorNumber, Direction direction) {
        this.elevator_id = elevator_id;
        this.currentFloorNumber = currentFloorNumber;
        this.direction = direction;
        display.update(currentFloorNumber, direction);
    }

    public int getElevatorId() {
        return elevator_id;
    }

    public int getCurrentFloorNumber() {
        return currentFloorNumber;
    }

    public Direction getDirection() {
        return direction;
    }

    public Display getDisplay() {
        return display;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
        display.update(currentFloorNumber, direction);
    }

    public void move() {
        if (direction == Direction.UP) {
            currentFloorNumber++;
        } else if (direction == Direction.DOWN) {
            currentFloorNumber--;
        }
        display.update(currentFloorNumber, direction);
    }

}
