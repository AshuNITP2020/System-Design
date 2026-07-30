public class Demo {
    public static void main(String[] args) {
        // The caller wires the collaborators together
        Invoice invoice = new Invoice(new Marker("name", "color", 10, 2020), 10);
        InvoiceDao invoiceDao = new InvoiceDao(invoice);
        InvoicePrinter invoicePrinter = new InvoicePrinter(invoice);

        invoice.calculateTotal();
        invoiceDao.saveToDB();
        invoicePrinter.print();
    }
}
