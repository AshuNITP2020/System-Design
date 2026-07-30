// DIP: the high-level service depends only on abstractions.
// It is policy. It knows nothing about databases, files or output formats.
public class InvoiceService {
    private final InvoiceRepository repository;   // abstraction
    private final InvoiceFormatter formatter;     // abstraction

    // Constructor injection - dependencies are explicit, final and mockable
    public InvoiceService(InvoiceRepository repository, InvoiceFormatter formatter) {
        this.repository = repository;
        this.formatter = formatter;
    }

    public void process(Invoice invoice) {
        invoice.calculateTotal();
        repository.save(invoice);
        System.out.println(formatter.format(invoice));
    }
}
