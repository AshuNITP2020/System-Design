package models;

import constants.Direction;
import dispatcher.ExternalButtonDispatcher;

public class ExternalButton {
    int floor_number;
    Direction direction;
    ExternalButtonDispatcher externalButtonDispatcher;

    public ExternalButton(int floor_number, Direction direction) {
        this.floor_number = floor_number;
        this.direction = direction;
        this.externalButtonDispatcher = ExternalButtonDispatcher.getInstance();
    }

    public void press() {
        externalButtonDispatcher.dispatch(floor_number, direction);
    }
}
