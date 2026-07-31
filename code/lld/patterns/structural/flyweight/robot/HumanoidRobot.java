// Concrete Flyweight: stores ONLY intrinsic (shared) state, and is immutable -
// final fields, getters only. It has to be: every caller shares this instance.
public class HumanoidRobot implements IRobot {

    private final String type;
    private final Sprites body;

    HumanoidRobot(String type, Sprites body) {
        this.type = type;
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public Sprites getBody() {
        return body;
    }

    @Override
    public void display(int x, int y) {
        System.out.println("Displaying " + type + " at " + x + ", " + y);
    }
}
