// BAD: this class violates SRP by holding three unrelated responsibilities.
// Three different teams (business, DBA, design) all have a reason to edit it.
public class Invoice {
    private Marker marker;
    private int quantity;
    private int total;

    public Invoice(Marker marker, int quantity) {
        this.marker = marker;
        this.quantity = quantity;
    }

    // Responsibility 1: calculate the total (business logic)
    public void calculateTotal() {
        System.out.println("Calculating total...");
        this.total = this.marker.price * this.quantity;
    }

    // Responsibility 2: database operations
    public void saveToDB() {
        System.out.println("Saving to DB...");
    }

    // Responsibility 3: print the invoice
    public void printInvoice() {
        System.out.println("Printing Invoice...");
    }
}
