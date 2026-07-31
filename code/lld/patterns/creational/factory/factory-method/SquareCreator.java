// Adding a TriangleCreator later touches nothing above - that is the
// difference from Simple Factory.
public class SquareCreator extends ShapeFactory {

    @Override
    public Shape createShape() {
        return new Square();
    }
}
