package kafkalab.stage0;

import kafkalab.common.HttpKit;
import kafkalab.common.Json;
import kafkalab.common.Metrics;
import kafkalab.common.OrderPlaced;
import kafkalab.common.SideEffect;
import kafkalab.common.SimulatedService;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private static final Logger LOG = Logger.getLogger(Monolith.class.getName());

    /** The four things that must happen. Order of this list is the order they run in. */
    static final List<SideEffect> SIDE_EFFECTS = List.of(
            SimulatedService.payment(),
            SimulatedService.inventory(),
            SimulatedService.email(),
            SimulatedService.analytics());

    static final Metrics REQUEST_LATENCY = new Metrics("POST /orders");

    public static void main(String[] args) throws Exception {
        var server = HttpKit.server(8080);

        // TODO(1) — call placeOrder(order), respond 200 {"status":"ok"}, respond 500 with the
        //           message if it throws. Exactly one respond per path.
        //           STATUS: implemented by you.
        HttpKit.route(server, "/orders", exchange -> {
            try {
                String raw = HttpKit.body(exchange);
                @SuppressWarnings("unchecked")
                Map<String, String> in = Json.read(raw, Map.class);
                OrderPlaced order = OrderPlaced.sample(
                        in.getOrDefault("orderId", "order-?"),
                        in.getOrDefault("userId", "user-?"));

                long t0 = System.nanoTime();
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
     * <b>TODO(2)</b> — the simplest possible loop over SIDE_EFFECTS calling apply(order).
     * No try/catch. Let the first failure propagate, so you actually see the 500s.<br>
     * <b>TODO(3)</b> — only AFTER running the experiments: catch per side effect, <b>log</b>,
     * continue.<br>
     * <i>STATUS: both implemented by you.</i>
     *
     * Apply every side effect for this order, inline, in this thread.
     *
     * <p>A plain loop over SIDE_EFFECTS. That is the whole implementation, and it is the point:
     * four independent concerns sharing one call stack.
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
     * <p>The catch below is the "continue past failures" policy. Note it invents a question:
     * is a failed email an acceptable loss? Is a failed payment? A monolith forces one answer for
     * all four. Write down what you'd want instead — you're describing per-consumer independence,
     * which is exactly what stage 4 gives you for free.
     */
    static void placeOrder(OrderPlaced order) throws Exception {
        for (SideEffect effect : SIDE_EFFECTS) {
            try {
                effect.apply(order);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "side effect failed: " + effect.name(), e);
            }
        }
    }
}
