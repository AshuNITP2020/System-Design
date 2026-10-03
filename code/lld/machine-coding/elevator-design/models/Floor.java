package models;

import constants.Direction;

public class Floor {

    private final ExternalButton upButton;
    private final ExternalButton downButton;

    public Floor(int floorNumber) {
        this.upButton = new ExternalButton(floorNumber, Direction.UP);
        this.downButton = new ExternalButton(floorNumber, Direction.DOWN);
    }

    public void pressUp() {
        upButton.press();
    }

    public void pressDown() {
        downButton.press();
    }
}
