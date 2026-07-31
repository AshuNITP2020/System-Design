// Facade: one entry point that owns the subsystems AND the order of the steps.
// The sequence matters - check stock BEFORE taking payment - and that knowledge
// now lives in one place instead of in every caller.
public class OrderFacade {

    private final InventoryService inventory;
    private final PaymentService payment;
    private final ShippingService shipping;
    private final NotificationService notification;

    public OrderFacade() {
        this.inventory = new InventoryService();
        this.payment = new PaymentService();
        this.shipping = new ShippingService();
        this.notification = new NotificationService();
    }

    public void placeOrder(String productId, String paymentMethod) {
        System.out.println("Placing order for product: " + productId);

        if (!inventory.checkStock(productId)) {
            System.out.println("Product out of stock!");
            return;
        }
        if (!payment.makePayment(paymentMethod)) {
            System.out.println("Payment failed!");
            return;
        }
        shipping.shipProduct(productId);
        notification.sendConfirmation(productId);

        System.out.println("Order placed successfully!");
    }
}
