package models;

import constants.Direction;

public class Display {
    int currentFloorNumber;
    Direction direction = Direction.IDLE;

    public Display(int currentFloorNumber) {
        this.currentFloorNumber = currentFloorNumber;
    }
}
