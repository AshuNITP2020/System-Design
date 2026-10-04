package models;

import constants.Direction;

public class Display {

    private int currentFloorNumber;
    private Direction direction = Direction.IDLE;

    void update(int currentFloorNumber, Direction direction) {
        this.currentFloorNumber = currentFloorNumber;
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "[floor " + currentFloorNumber + " | " + direction + "]";
    }
}
