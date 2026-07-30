// NEW file-save behaviour: an EXTENSION.
// Adding this file changed no existing line anywhere.
public class FileInvoiceDao implements InvoiceDao {
    Invoice invoice;

    public FileInvoiceDao(Invoice invoice) {
        this.invoice = invoice;
    }

    @Override
    public void save() {
        System.out.println("Saving to file...");
    }
}
