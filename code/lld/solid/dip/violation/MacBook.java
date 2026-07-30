// VIOLATION OF DIP
// The high-level module depends directly on low-level modules.
// Note that the Keyboard / Mouse interfaces already exist - declaring an
// abstraction is not enough, the DEPENDENCY has to point at it.
public class MacBook {
    private final WiredKeyboard keyboard;   // concrete type
    private final WiredMouse mouse;         // concrete type

    public MacBook(WiredKeyboard wiredKeyboard, WiredMouse wiredMouse) {
        keyboard = wiredKeyboard;   // tight coupling
        mouse = wiredMouse;         // tight coupling
    }

    public Mouse getMouse() {
        return mouse;
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }
}
