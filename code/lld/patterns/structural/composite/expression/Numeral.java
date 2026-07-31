// Leaf: a literal value. (Named Numeral rather than Number to stay clear of
// java.lang.Number.)
public class Numeral implements ArithmeticExpression {
    int value;

    public Numeral(int value) {
        this.value = value;
    }

    @Override
    public int evaluate() {
        System.out.println("Number value is: " + value);
        return value;
    }
}
