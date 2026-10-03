package controller;

import constants.Direction;
import models.Elevator;

import java.util.Collections;
import java.util.PriorityQueue;

public class ElevatorController {
    public Elevator elevator;
    public PriorityQueue<Integer> downPQ = new PriorityQueue<>(Collections.reverseOrder());
    public PriorityQueue<Integer> upPQ = new PriorityQueue<>();

    public ElevatorController(Elevator elevator) {
        this.elevator = elevator;
    }

    public void acceptNewRequest(int floor_number) {
        int current = elevator.getCurrentFloorNumber();
        if (floor_number > current) {
            upPQ.offer(floor_number);
        } else if (floor_number < current) {
            downPQ.offer(floor_number);
        } else {
            upPQ.offer(floor_number);
        }
    }

    public Integer step() {
        if (upPQ.isEmpty() && downPQ.isEmpty()) {
            elevator.setDirection(Direction.IDLE);
            return null;
        }

        Direction direction = elevator.getDirection();
        if (direction == Direction.IDLE || queueFor(direction).isEmpty()) {
            direction = upPQ.isEmpty() ? Direction.DOWN : Direction.UP;
            elevator.setDirection(direction);
        }

        if (!isStopAtCurrentFloor()) {
            elevator.move();
        }

        if (isStopAtCurrentFloor()) {
            return queueFor(elevator.getDirection()).poll();
        }
        return null;
    }

    private boolean isStopAtCurrentFloor() {
        Integer nextStop = queueFor(elevator.getDirection()).peek();
        return nextStop != null && nextStop == elevator.getCurrentFloorNumber();
    }

    private PriorityQueue<Integer> queueFor(Direction direction) {
        return direction == Direction.UP ? upPQ : downPQ;
    }

    public boolean hasPendingRequests() {
        return !upPQ.isEmpty() || !downPQ.isEmpty();
    }

    public int pendingRequestCount() {
        return upPQ.size() + downPQ.size();
    }
}
