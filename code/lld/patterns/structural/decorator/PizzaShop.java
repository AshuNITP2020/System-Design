public class PizzaShop {
    public static void main(String[] args) {
        System.out.println("======= Decorator Design Pattern ======");

        BasePizza pizza1 = new PlainPizza();
        print(1, pizza1);

        BasePizza pizza2 = new ExtraCheeseTopping(new PlainPizza());
        print(2, pizza2);

        BasePizza pizza3 = new VeggiesTopping(new ExtraCheeseTopping(new PlainPizza()));
        print(3, pizza3);

        BasePizza pizza4 = new PepperoniTopping(new ExtraCheeseTopping(new PlainPizza()));
        print(4, pizza4);

        BasePizza pizza5 = new MushroomTopping(
                new PepperoniTopping(new ExtraCheeseTopping(new PlainPizza())));
        print(5, pizza5);

        print(6, new Farmhouse());

        BasePizza pizza7 = new MushroomTopping(new ExtraCheeseTopping(new Farmhouse()));
        print(7, pizza7);

        print(8, new TandooriPaneerDelight());
        print(9, new ChickenDominator());

        BasePizza pizza10 = new MushroomTopping(new ChickenDominator());
        print(10, pizza10);

        // Ten different products. Four base classes and four toppings - no
        // ExtraCheeseAndMushroomFarmhousePizza class anywhere.
    }

    private static void print(int n, BasePizza pizza) {
        System.out.println("Order " + n + ": " + pizza.getDescription() + " = Rs." + pizza.getCost());
    }
}
