// Following DIP - the high-level module depends only on abstractions.
// Compare with the violation: only the field and constructor TYPES changed,
// and that is the whole principle.
public class MacBook {
    private final Keyboard keyboard;   // abstraction
    private final Mouse mouse;         // abstraction

    // Dependency injection through the constructor
    public MacBook(Mouse mouse, Keyboard keyboard) {
        this.keyboard = keyboard;   // works with ANY keyboard
        this.mouse = mouse;         // works with ANY mouse
    }

    public Mouse getMouse() {
        return mouse;
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }
}
