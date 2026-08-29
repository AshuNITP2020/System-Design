package kafkalab.stage2;

import kafkalab.common.HttpKit;
import kafkalab.common.Json;
import kafkalab.common.Metrics;
import kafkalab.common.OrderPlaced;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;

/**
 * STAGE 2 — the producer writes the event to a table and returns. That's all it does.
 *
 * <p>Notice what is <b>absent</b> compared to stage 1: there is no {@code SUBSCRIBERS} list. The
 * producer no longer knows payment, inventory, email or analytics exist. It writes one row and
 * goes home. Adding a fifth consumer does not touch this file — the first time that's been true.
 *
 * <p>And notice what the response now means. In stages 0 and 1, 200 was a claim about work that
 * had been done. Here 200 means only <em>"I have durably recorded that this happened."</em> That
 * is a promise the producer can actually keep.
 */
public final class OrderService {

    static final Metrics REQUEST_LATENCY = new Metrics("POST /orders");

    public static void main(String[] args) throws Exception {
        Db.init();
        var server = HttpKit.server(8080);

        HttpKit.route(server, "/orders", exchange -> {
            try {
                @SuppressWarnings("unchecked")
                Map<String, String> in = Json.read(HttpKit.body(exchange), Map.class);
                OrderPlaced order = OrderPlaced.sample(
                        in.getOrDefault("orderId", "order-?"),
                        in.getOrDefault("userId", "user-?"));

                long t0 = System.nanoTime();
                enqueue(order);
                REQUEST_LATENCY.record((System.nanoTime() - t0) / 1_000_000);

                HttpKit.respond(exchange, 200, "{\"status\":\"queued\"}");
            } catch (Exception e) {
                HttpKit.respond(exchange, 500, Json.write(Map.of("error", String.valueOf(e.getMessage()))));
            }
        });

        // Instrumentation, written for you: current contents of the queue by status.
        HttpKit.route(server, "/queue", exchange -> {
            try {
                HttpKit.respond(exchange, 200, Json.write(Map.of(
                        "total", Db.total(),
                        "byStatus", Db.statusCounts())));
            } catch (Exception e) {
                HttpKit.respond(exchange, 500, Json.write(Map.of("error", String.valueOf(e.getMessage()))));
            }
        });

        HttpKit.route(server, "/stats", exchange -> {
            REQUEST_LATENCY.print();
            HttpKit.respond(exchange, 200, "{\"status\":\"printed to server console\"}");
        });

        server.start();
        System.out.println("stage2 order-service on http://localhost:8080  (POST /orders, GET /queue)");
    }

    /**
     * Append the order to the {@code outbox} table.
     *
     * with {@code Json.write(order)} as the payload, {@code 'NEW'} as the status, and
     * {@code System.currentTimeMillis()}. Use {@link Db#open()} and a {@link PreparedStatement},
     * both in try-with-resources.
     *
     * <p>Before you run it, predict the latency. In stage 1 the producer waited ~420ms for four
     * consumers. Here it waits for one local disk write. Measure it with {@code /stats} — the
     * difference is the single biggest number in this whole repo, and it is the entire argument
     * for putting a durable buffer between producer and consumer.
     *
     * <p>Then ask the harder question: <b>you just told the customer 200. Has the payment been
     * taken?</b> No. It will be, in a moment, by a worker. Is that acceptable? For payment,
     * probably not — you'd want a synchronous authorization. For email and analytics, obviously
     * yes. You have just discovered that the same event wants different delivery timing per
     * consumer, which is a thing stages 0 and 1 could not express at all.
     */
    static void enqueue(OrderPlaced order) throws Exception {
        String sql = "INSERT INTO outbox (payload, status, created_at) VALUES (?, 'NEW', ?)";
        try (Connection c = Db.open();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, Json.write(order));   // parameters are 1-indexed, not 0
            ps.setLong(2, System.currentTimeMillis());
            ps.executeUpdate();                   // executeUpdate for writes
        }
    }
}
