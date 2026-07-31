public class Client {
    public static void main(String[] args) {
        System.out.println("======= Bridge: the solution ======");

        // Mix and match at runtime - m animals x n mechanisms, m + n classes
        LivingThings dog = new Dog(new LungBreathing());
        LivingThings whale = new Whale(new LungBreathing());   // reuses the same mechanism
        LivingThings fish = new Fish(new GillBreathing());
        LivingThings tree = new Tree(new Photosynthesis());

        dog.breathe();
        whale.breathe();
        fish.breathe();
        tree.breathe();
    }
}
