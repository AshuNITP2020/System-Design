package kafkalab.common;

import java.util.List;

/**
 * The one event this whole repo is about.
 *
 * <p>Note {@code orderId} and {@code userId}. From stage 5 onward one of them becomes the
 * <em>partition key</em>, and which one you pick changes your ordering guarantees. Worth
 * remembering that the choice was already sitting here in stage 0.
 */
public record OrderPlaced(
        String orderId,
        String userId,
        List<Item> items,
        long totalCents,
        long placedAtEpochMs) {

    public record Item(String sku, int qty, long unitCents) {}

    public static OrderPlaced sample(String orderId, String userId) {
        var items = List.of(new Item("SKU-COFFEE", 2, 89900), new Item("SKU-MUG", 1, 24900));
        long total = items.stream().mapToLong(i -> i.qty() * i.unitCents()).sum();
        return new OrderPlaced(orderId, userId, items, total, System.currentTimeMillis());
    }
}
