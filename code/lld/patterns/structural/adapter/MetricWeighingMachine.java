// Client: only knows the Target interface.
public class MetricWeighingMachine {
    public static void main(String[] args) {
        System.out.println("======= Adapter Design Pattern ======");

        double weighingScaleReading = 25.0;     // the baby weighs 25 pounds
        ImperialWeighingMachineImpl imperialWeighingMachine =
                new ImperialWeighingMachineImpl(weighingScaleReading);

        WeighingMachineAdapter weightMachineAdapter =
                new WeightMachineAdapterImpl(imperialWeighingMachine);

        System.out.println("Weight in KG: " + weightMachineAdapter.getWeightInKg());
    }
}
