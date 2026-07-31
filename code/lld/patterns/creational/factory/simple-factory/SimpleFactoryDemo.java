public class SimpleFactoryDemo {
    public static void main(String[] args) {
        System.out.println("======= Simple Factory =======");

        // set the type you want
        ShapeType shapeType = ShapeType.SQUARE;

        // get the shape - the client never calls new Square()
        Shape shape = ShapeFactory.createShapeInstance(shapeType);
        shape.draw();
        shape.computeArea();
    }
}
