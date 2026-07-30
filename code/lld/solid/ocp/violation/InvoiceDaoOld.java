// The starting point produced by the SRP refactoring:
// one responsibility, one way to save.
public class InvoiceDaoOld {
    Invoice invoice;

    public InvoiceDaoOld(Invoice invoice) {
        this.invoice = invoice;
    }

    public void saveToDB() {
        System.out.println("Saving to DB...");
    }
}
