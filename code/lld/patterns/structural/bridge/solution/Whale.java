public class Whale extends LivingThings {

    public Whale(BreathingProcess breathingProcess) {
        super(breathingProcess);
    }

    @Override
    public void breathe() {
        System.out.print("Whale: ");
        breathingProcess.breathe();
    }
}
