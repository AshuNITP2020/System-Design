public class ECommerceApp {
    public static void main(String[] args) {
        System.out.println("====== Facade Design Pattern ======");

        // One call. The client never sees InventoryService or PaymentService.
        OrderFacade orderFacade = new OrderFacade();

        orderFacade.placeOrder("MacBook Pro", "Credit Card");
        System.out.println();
        orderFacade.placeOrder("Cricket Bat", "UPI");

        // A facade does not BLOCK access - a caller with a genuine need can
        // still talk to a subsystem directly. It just removes the need to.
    }
}
