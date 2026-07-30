public class PlainTextInvoiceFormatter implements InvoiceFormatter {

    @Override
    public String format(Invoice invoice) {
        return "Invoice total: " + invoice.getTotal();
    }
}
