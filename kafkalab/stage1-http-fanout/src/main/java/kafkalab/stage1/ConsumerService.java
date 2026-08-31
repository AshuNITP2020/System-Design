package kafkalab.stage1;

import kafkalab.common.HttpKit;
import kafkalab.common.Json;
import kafkalab.common.OrderPlaced;
import kafkalab.common.SimulatedService;

import java.util.Map;

/**
 * STAGE 1 — one standalone consumer service. Run four of these on four ports.
 *
 * <pre>
 *   ./gradlew -q --console=plain :kafkalab:stage1-http-fanout:runConsumer \
 *       -Pname=payment -Pport=9001
 *
 *   # or all five processes at once:
 *   ./kafkalab/stage1-http-fanout/run-all.sh
 * </pre>
 *
 * Notice what this class already gives you that stage 0 could not: this process can be deployed,
 * restarted, scaled and killed on its own. Killing it is Experiment B, and it is the whole reason
 * stage 2 exists.
 */
public final class ConsumerService {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("usage: ConsumerService <payment|inventory|email|analytics> <port>");
            System.exit(2);
        }
        String name = args[0];
        int port = Integer.parseInt(args[1]);

        SimulatedService svc = switch (name) {
            case "payment"   -> SimulatedService.payment();
            case "inventory" -> SimulatedService.inventory();
            case "email"     -> SimulatedService.email();
            case "analytics" -> SimulatedService.analytics();
            default -> throw new IllegalArgumentException("unknown consumer: " + name);
        };

        var server = HttpKit.server(port);

        // TODO(1) — deserialize with Json.read(raw, OrderPlaced.class), call svc.apply(order),
        //           respond 200; respond 500 on failure. One respond per path, and keep the
        //           parse INSIDE the try or a malformed body kills the connection silently.
        //           STATUS: implemented by you.
        //
        // The 500 you return is the producer's problem, and whatever it does with it is retry
        // policy you are hand-rolling. Kafka gives you that as offset management instead.
        HttpKit.route(server, "/events/order-placed", exchange -> {
            try {
                String raw = HttpKit.body(exchange);
                OrderPlaced order = Json.read(raw, OrderPlaced.class);
                svc.apply(order);
                HttpKit.respond(exchange, 200, "{\"status\":\"ok\"}");
            } catch (Exception e) {
                HttpKit.respond(exchange, 500, Json.write(Map.of("error", String.valueOf(e.getMessage()))));
            }
        });

        HttpKit.route(server, "/stats", exchange ->
                HttpKit.respond(exchange, 200, Json.write(Map.of(
                        "name", svc.name(),
                        "distinctApplied", svc.distinctApplied(),
                        "totalApplied", svc.totalApplied(),
                        "failures", svc.failureCount()))));

        server.start();
        System.out.printf("stage1 consumer '%s' on http://localhost:%d%n", name, port);
    }
}
