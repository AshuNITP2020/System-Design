// BAD: this contract assumes every Bike has an engine.
public interface Bike {
    void turnOnEngine();
    void turnOffEngine();
    void accelerate();
    void applyBrakes();
}
