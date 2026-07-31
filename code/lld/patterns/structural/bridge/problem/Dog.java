public class Dog extends LivingThings {

    // The breathing process is tightly coupled to the abstraction
    @Override
    public void breathe() {
        System.out.println("Dog: Breathes through its nose; Lives on land; Respiratory system: 2 lungs");
        System.out.println("Breathing Process: Inhales Oxygen from the air and Exhales Carbon Dioxide.");
    }
}
