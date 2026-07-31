// Simple Factory: one static method switching on a type parameter.
// Not a true GoF pattern - an idiom. Note the OCP problem: every new shape
// edits this switch.
public class ShapeFactory {

    public static Shape createShapeInstance(ShapeType shapeType) {
        if (shapeType == null) {
            return null;
        }
        return switch (shapeType) {
            case CIRCLE -> new Circle();
            case RECTANGLE -> new Rectangle();
            case SQUARE -> new Square();
        };
    }
}
