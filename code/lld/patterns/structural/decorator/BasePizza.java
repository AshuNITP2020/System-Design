// Component: the contract shared by the base objects AND every decorator.
// That shared type is what lets decorators wrap decorators.
public interface BasePizza {
    String getDescription();
    double getCost();
}
