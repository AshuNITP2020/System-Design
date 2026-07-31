// Concrete Creator - knows how to build exactly one product.
public class CircleCreator extends ShapeFactory {

    @Override
    public Shape createShape() {
        return new Circle();
    }
}
