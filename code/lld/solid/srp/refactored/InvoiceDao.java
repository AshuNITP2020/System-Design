// GOOD: one responsibility - persistence.
// Changes only if the storage mechanism changes.
public class InvoiceDao {
    Invoice invoice;

    public InvoiceDao(Invoice invoice) {
        this.invoice = invoice;
    }

    public void saveToDB() {
        System.out.println("Saving to DB...");
    }
}
