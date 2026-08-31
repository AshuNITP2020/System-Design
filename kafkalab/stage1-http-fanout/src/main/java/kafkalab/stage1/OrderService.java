package kafkalab.stage1;

import kafkalab.common.HttpKit;
import kafkalab.common.Json;
import kafkalab.common.Metrics;
import kafkalab.common.OrderPlaced;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * STAGE 1 — the producer. Fans an order out to four consumer services over HTTP.
 *
 * <p>This is the classic microservice refactor, and it genuinely fixes real problems: each
 * consumer now deploys, scales and fails on its own schedule. But look hard at
 * {@link #SUBSCRIBERS} — the producer still holds a hardcoded list of everyone who cares.
 * That list is the N×M mesh, just expressed in config instead of method calls.
 */
public final class OrderService {

    private static Logger logger = Logger.getLogger(OrderService.class.getName());
    /**
     * TODO(0): the smell. Who owns this list? What happens when the fraud team wants in?
     *
     * <p>In stage 4 this list disappears entirely and is replaced by one topic name. That single
     * deletion is the most important diff in this whole repo — flag this spot in your head now so
     * you notice it when it happens.
     */
    static final List<String> SUBSCRIBERS = List.of(
            "http://localhost:9001/events/order-placed",  // payment
            "http://localhost:9002/events/order-placed",  // inventory
            "http://localhost:9003/events/order-placed",  // email
            "http://localhost:9004/events/order-placed"); // analytics

    static final Metrics REQUEST_LATENCY = new Metrics("POST /orders");

    public static void main(String[] args) throws Exception {
        var server = HttpKit.server(8080);

        // TODO(1) — call publish(order), respond 200 on success, 500 on failure.
        //           STATUS: implemented by you.
        HttpKit.route(server, "/orders", exchange -> {
            try {
                @SuppressWarnings("unchecked")
                Map<String, String> in = Json.read(HttpKit.body(exchange), Map.class);
                OrderPlaced order = OrderPlaced.sample(
                        in.getOrDefault("orderId", "order-?"),
                        in.getOrDefault("userId", "user-?"));

                long t0 = System.nanoTime();
                publish(order);
                REQUEST_LATENCY.record((System.nanoTime() - t0) / 1_000_000);

                HttpKit.respond(exchange, 200, "{\"status\":\"ok\"}");
            } catch (Exception e) {
                HttpKit.respond(exchange, 500, Json.write(Map.of("error", String.valueOf(e.getMessage()))));
            }
        });

        HttpKit.route(server, "/stats", exchange -> {
            REQUEST_LATENCY.print();
            HttpKit.respond(exchange, 200, "{\"status\":\"printed to server console\"}");
        });

        server.start();
        System.out.println("stage1 order-service on http://localhost:8080");
        System.out.println("subscribers: " + SUBSCRIBERS);
    }

    /**
     * <b>TODO(2)</b> — the five-rung ladder below. Climb ONE rung at a time and run the
     * experiment between each; each rung fixes the previous one's problem and creates a new one.<br>
     * <i>STATUS: rungs 1-2 implemented by you. <b>Rungs 3, 4 and 5 are still open.</b></i><br>
     * <i>Rung 3 is the one worth doing — retries produce duplicates, which stage 0 could not.</i>
     *
     * POST the order to every subscriber.
     *
     * <p>Currently at <b>rung 2</b>: each {@code postJson} wrapped in its own try/catch, failure
     * logged with the URL, loop continues. Rungs 3-5 below are still to do. Each one fixes the
     * previous one's problem and introduces a new one, and that ladder is the lesson of stage 1:
     *
     * <ol>
     *   <li><b>Sequential, abort on failure.</b> Latency is the sum again (stage 0 all over
     *       again, now with network hops added). If subscriber #3 is down, #4 never hears about
     *       the order at all — and #1 and #2 already ran. Partial fan-out, silently.
     *   <li><b>Sequential, catch per subscriber and continue.</b> Better: #4 gets it. But the
     *       event is now permanently lost for #3. Nothing anywhere remembers it should have been
     *       delivered. <b>This is the wall.</b> Sit here for a minute.
     *   <li><b>Add retries</b> (3 attempts, small backoff). Now #3 gets it if it comes back within
     *       seconds. Check the consumer log for {@code DUPLICATE} — a timeout where the request
     *       *did* land means you delivered twice. You cannot tell the two cases apart from the
     *       producer side. Ever.
     *   <li><b>Parallel fan-out</b> (virtual threads / {@code CompletableFuture}). Latency becomes
     *       the max instead of the sum. Now: if 3 succeed and 1 fails, what do you return to the
     *       customer? There is no good answer, and that is the point.
     *   <li><b>Retry forever, in the background.</b> Congratulations — you now need somewhere to
     *       *store* undelivered events across a producer restart. You have just derived the need
     *       for a durable buffer. That is stage 2, and stage 2 is where you'll discover a database
     *       table is a bad one.
     * </ol>
     */
    static void publish(OrderPlaced order) throws Exception {
        for (String subscriber : SUBSCRIBERS) {
            try {
                HttpKit.postJson(subscriber, Json.write(order));
            } catch (Exception e) {
                logger.severe("Failed to publish order to " + subscriber + ": " + e.getMessage());
            }
        }
    }
}
