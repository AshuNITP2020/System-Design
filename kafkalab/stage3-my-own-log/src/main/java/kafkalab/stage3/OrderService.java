package kafkalab.stage3;

import kafkalab.common.HttpKit;
import kafkalab.common.Json;
import kafkalab.common.Metrics;
import kafkalab.common.OrderPlaced;

import java.util.Map;

/**
 * STAGE 3 — the producer appends to the log and returns the offset. Written for you; it is four
 * lines and identical in spirit to stage 2's {@code enqueue}.
 *
 * <p>The response now carries the <b>offset</b>. That number is the receipt: "your order is
 * record #47 in the log." It is a durable, orderable, replayable identity for the event, and it
 * exists independently of every consumer. Neither stage 1 nor stage 2 could give the caller
 * anything like it — stage 2's row id came closest, but it was a mutable row that consumers
 * rewrote, not an immutable position.
 */
public final class OrderService {

    static final Metrics REQUEST_LATENCY = new Metrics("POST /orders");

    public static void main(String[] args) throws Exception {
        Log.file();
        var server = HttpKit.server(8080);

        HttpKit.route(server, "/orders", exchange -> {
            try {
                @SuppressWarnings("unchecked")
                Map<String, String> in = Json.read(HttpKit.body(exchange), Map.class);
                OrderPlaced order = OrderPlaced.sample(
                        in.getOrDefault("orderId", "order-?"),
                        in.getOrDefault("userId", "user-?"));

                long t0 = System.nanoTime();
                long offset = Log.append(Json.write(order));
                REQUEST_LATENCY.record((System.nanoTime() - t0) / 1_000_000);

                HttpKit.respond(exchange, 200, Json.write(Map.of("status", "appended", "offset", offset)));
            } catch (Exception e) {
                HttpKit.respond(exchange, 500, Json.write(Map.of("error", String.valueOf(e.getMessage()))));
            }
        });

        // Instrumentation: log size plus every group's position, i.e. consumer lag.
        HttpKit.route(server, "/log", exchange -> {
            try {
                long size = Log.size();
                var positions = new java.util.LinkedHashMap<String, Object>();
                for (String g : new String[]{"payment", "inventory", "email", "analytics"}) {
                    long p = Offsets.position(g);
                    positions.put(g, Map.of("offset", p, "lag", size - p));
                }
                HttpKit.respond(exchange, 200, Json.write(Map.of("logSize", size, "groups", positions)));
            } catch (Exception e) {
                HttpKit.respond(exchange, 500, Json.write(Map.of("error", String.valueOf(e.getMessage()))));
            }
        });

        HttpKit.route(server, "/stats", exchange -> {
            REQUEST_LATENCY.print();
            HttpKit.respond(exchange, 200, "{\"status\":\"printed to server console\"}");
        });

        server.start();
        System.out.println("stage3 order-service on http://localhost:8080  (POST /orders, GET /log)");
    }
}
