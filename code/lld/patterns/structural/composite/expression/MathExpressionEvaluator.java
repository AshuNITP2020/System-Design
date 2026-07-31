public class MathExpressionEvaluator {
    public static void main(String[] args) {
        System.out.println("======= Composite: expression tree ======");

        //  2 * (1 + 7)
        //
        //          *
        //         / \
        //        2   +
        //           / \
        //          1   7

        ArithmeticExpression two = new Numeral(2);
        ArithmeticExpression one = new Numeral(1);
        ArithmeticExpression seven = new Numeral(7);

        ArithmeticExpression addExpression = new Expression(one, seven, OperationType.ADD);
        ArithmeticExpression parentExpression =
                new Expression(two, addExpression, OperationType.MULTIPLY);

        System.out.println("Result: " + parentExpression.evaluate());
    }
}
