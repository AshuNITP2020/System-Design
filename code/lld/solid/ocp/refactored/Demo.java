public class Demo {
    public static void main(String[] args) {
        Invoice invoice = new Invoice(new Marker("name", "color", 10, 2020), 10);
        invoice.calculateTotal();

        // The client only ever talks to the InvoiceDao abstraction
        InvoiceDao databaseInvoiceDao = new DatabaseInvoiceDao(invoice);
        databaseInvoiceDao.save();   // save to DB

        InvoiceDao fileInvoiceDao = new FileInvoiceDao(invoice);
        fileInvoiceDao.save();       // save to file

        // The system is now:
        //   OPEN for extension      - a new InvoiceDao can be added any time
        //   CLOSED for modification - existing, tested code is untouched
    }
}
