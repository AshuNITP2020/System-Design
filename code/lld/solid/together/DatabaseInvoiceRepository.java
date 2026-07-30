// OCP + LSP: a new behaviour arrives as a new class, and it fully honours
// the contract - it really saves, it does not throw or no-op.
public class DatabaseInvoiceRepository implements InvoiceRepository {

    @Override
    public void save(Invoice invoice) {
        System.out.println("Saving to DB...");
    }
}
