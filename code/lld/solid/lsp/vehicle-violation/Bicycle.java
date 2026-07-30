public class Bicycle extends Vehicle {
    // LSP violation: weakening the postcondition.
    // The parent always returns a real Boolean; this returns null.
    public Boolean hasEngine() {
        return null;
    }
}
