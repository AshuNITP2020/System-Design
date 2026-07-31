// Flyweight interface. Note display() TAKES the coordinates rather than
// storing them - that is the extrinsic state, supplied per call.
public interface IRobot {
    void display(int x, int y);
}
