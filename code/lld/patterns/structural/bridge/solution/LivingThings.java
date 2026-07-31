// Abstraction: the "what". Holds a reference to an Implementor - that
// reference IS the bridge.
public abstract class LivingThings {

    protected BreathingProcess breathingProcess;

    public LivingThings(BreathingProcess breathingProcess) {
        this.breathingProcess = breathingProcess;
    }

    public abstract void breathe();
}
