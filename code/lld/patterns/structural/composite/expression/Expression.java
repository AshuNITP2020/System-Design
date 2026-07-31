// Composite: two child expressions and an operator. Each child may itself be
// a Numeral or another Expression - the client cannot tell, and does not need to.
public class Expression implements ArithmeticExpression {

    ArithmeticExpression leftExpression;
    ArithmeticExpression rightExpression;
    OperationType operation;

    public Expression(ArithmeticExpression leftPart, ArithmeticExpression rightPart,
                      OperationType operation) {
        this.leftExpression = leftPart;
        this.rightExpression = rightPart;
        this.operation = operation;
    }

    @Override
    public int evaluate() {
        int value = 0;
        switch (operation) {
            case ADD:
                value = leftExpression.evaluate() + rightExpression.evaluate();
                break;
            case SUBTRACT:
                value = leftExpression.evaluate() - rightExpression.evaluate();
                break;
            case DIVIDE:
                value = leftExpression.evaluate() / rightExpression.evaluate();
                break;
            case MULTIPLY:
                value = leftExpression.evaluate() * rightExpression.evaluate();
                break;
        }
        System.out.println("Expression value is: " + value);
        return value;
    }
}
