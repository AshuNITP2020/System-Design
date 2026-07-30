// ISP: a delivery role. Kept separate so a caller that only formats an
// invoice never depends on save() or notify().
public interface InvoiceNotifier {
    void notifyCustomer(Invoice invoice);
}
