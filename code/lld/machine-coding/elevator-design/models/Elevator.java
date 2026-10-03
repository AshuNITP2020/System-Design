package models;

import constants.Direction;

public class Elevator {
    private int elevator_id;
    private int currentFloorNumber;
    private Direction direction = Direction.IDLE;

    public int getCurrentFloorNumber() {
        return currentFloorNumber;
    }

    public void setCurrentFloorNumber(int currentFloorNumber) {
        this.currentFloorNumber = currentFloorNumber;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Elevator(int elevator_id, int currentFloorNumber, Direction direction) {
        this.elevator_id = elevator_id;
        this.currentFloorNumber = currentFloorNumber;
        this.direction = direction;
    }

    public int getElevatorId() {
        return elevator_id;
    }

    public void move() {
        if (direction == Direction.UP) {
            currentFloorNumber++;
        } else if (direction == Direction.DOWN) {
            currentFloorNumber--;
        }
    }

}
