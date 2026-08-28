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

        HttpKit.route(server, "/events/order-placed", exchange -> {
            // TODO(1): deserialize the body into OrderPlaced (Json.read(raw, OrderPlaced.class)),
            //          call svc.apply(order), respond 200 on success.
            //          On failure respond 500 — and think about what the *producer* should do with
            //          that 500. Whatever you decide, you are hand-rolling retry policy that
            //          Kafka would hand you as consumer-side offset management.
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
