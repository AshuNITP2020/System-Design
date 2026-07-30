public class DemoSolution {
    public static void main(String[] args) {
        WiredKeyboard wiredKeyboard = new WiredKeyboard("USB", "Dell", "F602", "Grey");
        WiredMouse wiredMouse = new WiredMouse("USB", "Dell", "F602", "Grey");
        BluetoothKeyboard bluetoothKeyboard =
                new BluetoothKeyboard("Bluetooth", "Logitech", "G102", "Black");
        BluetoothMouse bluetoothMouse =
                new BluetoothMouse("Bluetooth", "Logitech", "G102", "Black");

        // Any combination of parts can now be assembled

        MacBook macBookWithWiredParts = new MacBook(wiredMouse, wiredKeyboard);
        macBookWithWiredParts.getKeyboard().getSpecifications();
        macBookWithWiredParts.getMouse().getSpecifications();

        MacBook macBookWithBluetoothParts = new MacBook(bluetoothMouse, bluetoothKeyboard);
        macBookWithBluetoothParts.getKeyboard().getSpecifications();
        macBookWithBluetoothParts.getMouse().getSpecifications();
    }
}
