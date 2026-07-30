// ISP: a persistence role. A client that only stores invoices depends on
// exactly one method - nothing about formatting or delivery.
public interface InvoiceRepository {
    void save(Invoice invoice);
}
