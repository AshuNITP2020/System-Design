package controller;

import constants.Direction;
import models.Elevator;
import models.Request;

import java.util.Comparator;
import java.util.PriorityQueue;

public class ElevatorController {

    private final Elevator elevator;
    private final int minFloor;
    private final int maxFloor;

    private final PriorityQueue<Request> upQueue =
            new PriorityQueue<>(Comparator.comparingInt(Request::getFloorNumber));
    private final PriorityQueue<Request> downQueue =
            new PriorityQueue<>(Comparator.comparingInt(Request::getFloorNumber).reversed());

    public ElevatorController(Elevator elevator, int minFloor, int maxFloor) {
        this.elevator = elevator;
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
    }

    public void acceptHallCall(int floorNumber, Direction riderWants) {
        enqueue(Request.hallCall(floorNumber, riderWants));
    }

    public void acceptDestination(int floorNumber) {
        enqueue(Request.destination(floorNumber));
    }

    public Integer step() {
        if (!hasPendingRequests()) {
            elevator.setDirection(Direction.IDLE);
            return null;
        }

        fixQueues();
        if (!isStopAtCurrentFloor()) {
            elevator.move();
            fixQueues();
        }
        return isStopAtCurrentFloor() ? serveCurrentFloor() : null;
    }

    public boolean hasPendingRequests() {
        return !upQueue.isEmpty() || !downQueue.isEmpty();
    }

    public int pendingRequestCount() {
        return upQueue.size() + downQueue.size();
    }

    public Elevator getElevator() {
        return elevator;
    }

    private void enqueue(Request request) {
        int floorNumber = request.getFloorNumber();
        if (floorNumber < minFloor || floorNumber > maxFloor) {
            throw new IllegalArgumentException("Floor " + floorNumber
                    + " is outside the building (" + minFloor + ".." + maxFloor + ")");
        }
        PriorityQueue<Request> queue = chooseQueue(request);
        if (!queue.contains(request)) {
            queue.offer(request);
        }
    }

    private PriorityQueue<Request> chooseQueue(Request request) {
        int currentFloor = elevator.getCurrentFloorNumber();
        if (request.getFloorNumber() > currentFloor) {
            return upQueue;
        }
        if (request.getFloorNumber() < currentFloor) {
            return downQueue;
        }
        return request.getDesiredDirection() == Direction.DOWN ? downQueue : upQueue;
    }

    private void fixQueues() {
        pickDirectionIfNeeded();
        deferWrongDirectionRequests();
        pickDirectionIfNeeded();
    }

    private void pickDirectionIfNeeded() {
        Direction direction = elevator.getDirection();
        boolean needsNewDirection = direction == Direction.IDLE || queueFor(direction).isEmpty();
        if (needsNewDirection && hasPendingRequests()) {
            elevator.setDirection(upQueue.isEmpty() ? Direction.DOWN : Direction.UP);
        }
    }

    private void deferWrongDirectionRequests() {
        Direction direction = elevator.getDirection();
        if (direction == Direction.IDLE) {
            return;
        }
        PriorityQueue<Request> active = queueFor(direction);
        PriorityQueue<Request> opposite = oppositeQueueFor(direction);
        while (elevatorAtCurrentFloor(active) && !active.peek().servableDuring(direction)) {
            Request deferred = active.poll();
            if (!opposite.contains(deferred)) {
                opposite.offer(deferred);
            }
        }
    }

    private boolean isStopAtCurrentFloor() {
        Direction direction = elevator.getDirection();
        PriorityQueue<Request> active = queueFor(direction);
        return elevatorAtCurrentFloor(active) && active.peek().servableDuring(direction);
    }

    private Integer serveCurrentFloor() {
        Direction direction = elevator.getDirection();
        PriorityQueue<Request> active = queueFor(direction);
        int floorNumber = elevator.getCurrentFloorNumber();
        while (elevatorAtCurrentFloor(active) && active.peek().servableDuring(direction)) {
            active.poll();
        }
        return floorNumber;
    }

    private boolean elevatorAtCurrentFloor(PriorityQueue<Request> queue) {
        Request head = queue.peek();
        return head != null && head.getFloorNumber() == elevator.getCurrentFloorNumber();
    }

    private PriorityQueue<Request> queueFor(Direction direction) {
        return direction == Direction.UP ? upQueue : downQueue;
    }

    private PriorityQueue<Request> oppositeQueueFor(Direction direction) {
        return direction == Direction.UP ? downQueue : upQueue;
    }
}
