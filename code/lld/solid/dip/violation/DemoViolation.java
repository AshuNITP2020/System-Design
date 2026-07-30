public class DemoViolation {
    public static void main(String[] args) {
        WiredKeyboard wiredKeyboard = new WiredKeyboard("USB", "Dell", "F602", "Grey");
        WiredMouse wiredMouse = new WiredMouse("USB", "Dell", "F602", "Grey");
        BluetoothKeyboard bluetoothKeyboard =
                new BluetoothKeyboard("Bluetooth", "Logitech", "G102", "Black");
        BluetoothMouse bluetoothMouse =
                new BluetoothMouse("Bluetooth", "Logitech", "G102", "Black");

        MacBook macBookWithWiredParts = new MacBook(wiredKeyboard, wiredMouse);
        macBookWithWiredParts.getKeyboard().getSpecifications();
        macBookWithWiredParts.getMouse().getSpecifications();

        // MacBook macBookWithBluetoothParts =
        //         new MacBook(bluetoothKeyboard, bluetoothMouse);
        // Uncomment the two lines above and this file no longer compiles:
        // MacBook is hard-wired to WiredKeyboard and WiredMouse.
        // Tight coupling - a violation of DIP.
    }
}
