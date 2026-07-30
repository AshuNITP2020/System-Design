// The composition root: the ONLY place in the program that names concrete
// types. Swap either argument and nothing else in the codebase changes.
public class Application {
    public static void main(String[] args) {
        Invoice invoice = new Invoice(new Marker("name", "color", 10, 2020), 10);

        InvoiceService service = new InvoiceService(
                new DatabaseInvoiceRepository(),      // or FileInvoiceRepository
                new PlainTextInvoiceFormatter());     // or PdfInvoiceFormatter

        service.process(invoice);
    }
}
