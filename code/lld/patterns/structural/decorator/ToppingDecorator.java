// Base Decorator. Two relationships at once, and both are needed:
//   IS-A  BasePizza  -> a decorated pizza can be used wherever a pizza can
//   HAS-A BasePizza  -> it wraps another pizza and delegates to it
public abstract class ToppingDecorator implements BasePizza {

    BasePizza pizza;

    public ToppingDecorator(BasePizza pizza) {
        this.pizza = pizza;
    }
}
