package models;

import constants.Direction;
import dispatcher.ExternalButtonDispatcher;

public class ExternalButton {

    private final int floor_number;
    private final Direction direction;

    public ExternalButton(int floor_number, Direction direction) {
        this.floor_number = floor_number;
        this.direction = direction;
    }

    public void press() {
        ExternalButtonDispatcher.getInstance().dispatch(floor_number, direction);
    }
}
