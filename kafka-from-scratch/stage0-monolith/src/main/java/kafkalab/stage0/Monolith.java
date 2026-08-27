package kafkalab.stage0;

import kafkalab.common.HttpKit;
import kafkalab.common.Json;
import kafkalab.common.Metrics;
import kafkalab.common.OrderPlaced;
import kafkalab.common.SideEffect;
import kafkalab.common.SimulatedService;

import java.util.List;
import java.util.Map;

/**
 * STAGE 0 — everything in one process, called inline.
 *
 * <p>No queue, no broker, no network. The HTTP request that places the order also runs payment,
 * inventory, email and analytics before it returns. This is where most systems genuinely start,
 * and it is completely correct — until it isn't.
 *
 * <p>Read stage0-monolith/README.md and run the three experiments. Your job is the TODOs below.
 */
public final class Monolith {

    /** The four things that must happen. Order of this list is the order they run in. */
    static final List<SideEffect> SIDE_EFFECTS = List.of(
            SimulatedService.payment(),
            SimulatedService.inventory(),
            SimulatedService.email(),
            SimulatedService.analytics());

    static final Metrics REQUEST_LATENCY = new Metrics("POST /orders");

    public static void main(String[] args) throws Exception {
        var server = HttpKit.server(8080);

        HttpKit.route(server, "/orders", exchange -> {
            try {
                String raw = HttpKit.body(exchange);
                @SuppressWarnings("unchecked")
                Map<String, String> in = Json.read(raw, Map.class);
                OrderPlaced order = OrderPlaced.sample(
                        in.getOrDefault("orderId", "order-?"),
                        in.getOrDefault("userId", "user-?"));

                long t0 = System.nanoTime();
                // TODO(1): call placeOrder(order) and respond 200 with {"status":"ok"}.
                //          If it throws, respond 500 with the error message.
                //          Before you write it, predict: what status does the caller get when
                //          `email` fails? Is that the behaviour you actually want for an order?
                placeOrder(order);
                REQUEST_LATENCY.record((System.nanoTime() - t0) / 1_000_000);

                HttpKit.respond(exchange, 200, "{\"status\":\"ok\"}");
            } catch (Exception e) {
                HttpKit.respond(exchange, 500, Json.write(Map.of("error", String.valueOf(e.getMessage()))));
            }
        });

        // Handy while experimenting: GET /stats prints latency percentiles + per-service ledgers.
        HttpKit.route(server, "/stats", exchange -> {
            REQUEST_LATENCY.print();
            SIDE_EFFECTS.forEach(s -> {
                var svc = (SimulatedService) s;
                System.out.printf("%-10s distinct=%d total=%d failures=%d%n",
                        svc.name(), svc.distinctApplied(), svc.totalApplied(), svc.failureCount());
            });
            HttpKit.respond(exchange, 200, "{\"status\":\"printed to server console\"}");
        });

        server.start();
        System.out.println("stage0 monolith on http://localhost:8080  (POST /orders, GET /stats)");
    }

    /**
     * Apply every side effect for this order, inline, in this thread.
     *
     * <p>TODO(2): implement. Simplest possible loop over SIDE_EFFECTS calling apply(order).
     *
     * <p>Then sit with these questions — they are the entire content of stage 0:
     * <ul>
     *   <li>What is the caller's latency? Is it the max of the four, or the sum? Why?
     *   <li>{@code email} fails ~25% of the time. When it throws on order X, what happened to
     *       payment and inventory for X? Did the customer get charged? Does your HTTP 500 tell
     *       the truth?
     *   <li>You want to add a 5th consumer (fraud detection). Which file do you edit, and whose
     *       code do you have to redeploy to ship it?
     *   <li>Analytics wants to recompute last week's revenue after a bug fix. Can it? Where would
     *       last week's orders even come from?
     * </ul>
     *
     * <p>TODO(3, optional): try wrapping the loop so one failure doesn't abort the rest
     * (catch per side effect, log, continue). Notice you have now *invented* a policy question:
     * is a failed email an acceptable loss? Is a failed payment? A monolith forces one answer for
     * all four. Write down what you'd want instead — you're describing per-consumer independence,
     * which is exactly what stage 4 gives you for free.
     */
    static void placeOrder(OrderPlaced order) throws Exception {
        throw new UnsupportedOperationException("TODO(2): implement placeOrder");
    }
}
