// GOOD: one responsibility - presentation.
// Changes only if the print/layout requirement changes.
public class InvoicePrinter {
    private Invoice invoice;

    public InvoicePrinter(Invoice invoice) {
        this.invoice = invoice;
    }

    public void print() {
        System.out.println("Printing Invoice...");
    }
}
