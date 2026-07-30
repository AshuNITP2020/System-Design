// SRP: invoice data and its own business rule. Nothing else.
public class Invoice {
    private final Marker marker;
    private final int quantity;
    private int total;

    public Invoice(Marker marker, int quantity) {
        this.marker = marker;
        this.quantity = quantity;
    }

    public void calculateTotal() {
        this.total = marker.price * quantity;
    }

    public int getTotal() {
        return total;
    }
}
