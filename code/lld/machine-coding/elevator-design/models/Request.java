package models;

import constants.Direction;

import java.util.Objects;

public class Request {

    private final int floorNumber;
    private final Direction desiredDirection;

    private Request(int floorNumber, Direction desiredDirection) {
        this.floorNumber = floorNumber;
        this.desiredDirection = desiredDirection;
    }

    public static Request hallCall(int floorNumber, Direction riderDirection) {
        if (riderDirection != Direction.UP && riderDirection != Direction.DOWN) {
            throw new IllegalArgumentException("A hall call must be UP or DOWN, got: " + riderDirection);
        }
        return new Request(floorNumber, riderDirection);
    }

    public static Request destination(int floorNumber) {
        return new Request(floorNumber, null);
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public Direction getDesiredDirection() {
        return desiredDirection;
    }

    public boolean servableDuring(Direction sweepDirection) {
        return desiredDirection == null || desiredDirection == sweepDirection;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Request)) return false;
        Request request = (Request) other;
        return floorNumber == request.floorNumber && desiredDirection == request.desiredDirection;
    }

    @Override
    public int hashCode() {
        return Objects.hash(floorNumber, desiredDirection);
    }

    @Override
    public String toString() {
        return desiredDirection == null
                ? "destination(" + floorNumber + ")"
                : "hallCall(" + floorNumber + ", " + desiredDirection + ")";
    }
}
