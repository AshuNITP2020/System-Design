public class Whale extends LivingThings {

    // Duplicate of Dog's lung logic, reworded. Fix a bug in one and you must
    // remember the other.
    @Override
    public void breathe() {
        System.out.println("Whale: Breathes through lungs; Lives in water; Respiratory system: 2 lungs");
        System.out.println("Breathing Process: Inhales Oxygen from the water and Exhales Carbon Dioxide");
    }
}
