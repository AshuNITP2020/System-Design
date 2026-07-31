// Adapter: implements the Target, holds the Adaptee, does the translation.
// The client never learns that pounds were ever involved.
public class WeightMachineAdapterImpl implements WeighingMachineAdapter {

    // Adaptee reference - composition, not inheritance
    ImperialWeighingMachine imperialWeighingMachine;

    public WeightMachineAdapterImpl(ImperialWeighingMachine weightMachineInPounds) {
        this.imperialWeighingMachine = weightMachineInPounds;
    }

    @Override
    public double getWeightInKg() {
        double weightInPound = imperialWeighingMachine.getWeightInPounds();
        return weightInPound * 0.45359237;      // 1 lb = 0.45359237 kg
    }
}
