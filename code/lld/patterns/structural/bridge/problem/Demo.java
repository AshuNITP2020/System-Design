public class Demo {
    public static void main(String[] args) {
        System.out.println("======= Bridge: the problem ======");
        new Dog().breathe();
        new Fish().breathe();
        new Whale().breathe();
        new Tree().breathe();
        // Add "amphibian that breathes through skin" and you add a class.
        // Add a fifth breathing mechanism and you edit every class that uses it.
    }
}
