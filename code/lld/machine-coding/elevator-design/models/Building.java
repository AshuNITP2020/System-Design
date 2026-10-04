package models;

import controller.ElevatorController;
import dispatcher.ExternalButtonDispatcher;
import dispatcher.InternalButtonDispatcher;

import java.util.ArrayList;
import java.util.List;

public class Building {

    private final List<Floor> floors = new ArrayList<>();
    private final List<ElevatorController> elevatorControllers = new ArrayList<>();

    public Building(int numberOfFloors, List<Elevator> elevators) {
        for (Elevator elevator : elevators) {
            elevatorControllers.add(new ElevatorController(elevator, 0, numberOfFloors - 1));
        }
        ExternalButtonDispatcher.init(elevatorControllers);
        InternalButtonDispatcher.init(elevatorControllers);
        for (int floor_number = 0; floor_number < numberOfFloors; floor_number++) {
            floors.add(new Floor(floor_number));
        }
    }

    public Floor getFloor(int floor_number) {
        return floors.get(floor_number);
    }

    public List<ElevatorController> getElevatorControllers() {
        return elevatorControllers;
    }
}
