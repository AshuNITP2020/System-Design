// BAD: this class violates OCP.
// "Also save to a file" arrived, and the existing, tested class was edited.
public class InvoiceDao {
    Invoice invoice;

    public InvoiceDao(Invoice invoice) {
        this.invoice = invoice;
    }

    public void saveToDB() {
        System.out.println("Saving to DB...");
    }

    // BAD: every new save target forces another edit to this same class.
    public void saveToFile() {
        System.out.println("Saving to file...");
    }
}
