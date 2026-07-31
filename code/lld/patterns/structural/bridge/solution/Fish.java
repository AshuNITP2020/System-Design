public class Fish extends LivingThings {

    public Fish(BreathingProcess breathingProcess) {
        super(breathingProcess);
    }

    @Override
    public void breathe() {
        System.out.print("Fish: ");
        breathingProcess.breathe();
    }
}
