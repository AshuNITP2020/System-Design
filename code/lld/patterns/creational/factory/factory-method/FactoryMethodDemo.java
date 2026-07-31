public class FactoryMethodDemo {

    public static void main(String[] args) {
        System.out.println("======= Factory Method =======");

        ShapeType shapeType = ShapeType.SQUARE;
        Shape shape = getShapeInstance(shapeType);

        shape.draw();
        shape.computeArea();
    }

    private static Shape getShapeInstance(ShapeType shapeType) {
        if (shapeType == null) {
            return null;
        }
        ShapeFactory creator = switch (shapeType) {
            case CIRCLE -> new CircleCreator();
            case RECTANGLE -> new RectangleCreator();
            case SQUARE -> new SquareCreator();
        };
        return creator.createShape();
    }
}
