package models;

import dispatcher.InternalButtonDispatcher;

public class InternalButton {

    private final int elevator_id;
    private final int floor_number;

    public InternalButton(int elevator_id, int floor_number) {
        this.elevator_id = elevator_id;
        this.floor_number = floor_number;
    }

    public void press() {
        InternalButtonDispatcher.getInstance().dispatch(elevator_id, floor_number);
    }
}
