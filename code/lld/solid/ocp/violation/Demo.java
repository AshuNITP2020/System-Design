public class Demo {
    public static void main(String[] args) {
        Invoice invoice = new Invoice(new Marker("name", "color", 10, 2020), 10);
        invoice.calculateTotal();

        InvoiceDao databaseFileSave = new InvoiceDao(invoice);
        databaseFileSave.saveToDB();    // save to DB
        databaseFileSave.saveToFile();  // save to file

        // Problem: adding saveToMongoDB() means modifying InvoiceDao and every
        // class derived from it. That breaks "closed for modification".
    }
}
