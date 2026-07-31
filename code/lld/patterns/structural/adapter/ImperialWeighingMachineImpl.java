// Adaptee: an existing, incompatible class. You do not own it and cannot
// change it - a third-party US-model scale that only speaks pounds.
public class ImperialWeighingMachineImpl implements ImperialWeighingMachine {

    double weightInPounds = 0;

    public ImperialWeighingMachineImpl(double weighingScaleReading) {
        this.weightInPounds = weighingScaleReading;
    }

    @Override
    public double getWeightInPounds() {
        return weightInPounds;
    }
}
