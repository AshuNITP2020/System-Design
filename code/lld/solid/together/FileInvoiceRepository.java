// Adding MongoInvoiceRepository later touches nothing above (OCP).
public class FileInvoiceRepository implements InvoiceRepository {

    @Override
    public void save(Invoice invoice) {
        System.out.println("Saving to file...");
    }
}
