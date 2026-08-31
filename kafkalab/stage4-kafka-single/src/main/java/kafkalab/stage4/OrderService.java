package kafkalab.stage4;

import kafkalab.common.HttpKit;
import kafkalab.common.Json;
import kafkalab.common.Metrics;
import kafkalab.common.OrderPlaced;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Map;
import java.util.Properties;

/**
 * STAGE 4 — the producer, now writing to real Kafka.
 *
 * <h2>What vanished</h2>
 *
 * Your entire {@code Log.java} is gone. {@code append()}, the length prefix, the O(n) scan to work
 * out the next offset, the torn-write guard — all of it replaced by {@code producer.send()}. What
 * you hand-rolled in stage 3 is now three lines of configuration.
 *
 * <p>You still get an offset back, and it still means exactly what yours meant: the position this
 * record landed at in an ordered, immutable log. The only new word is <b>partition</b> — because
 * this log is split into three of them, which is stage 3's wall #6 already solved for you.
 */
public final class OrderService {

    static final String TOPIC = "orders";
    static final String BOOTSTRAP = System.getProperty("bootstrap", "localhost:9092");

    static final Metrics REQUEST_LATENCY = new Metrics("POST /orders");

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // acks=all: the write is not acknowledged until every in-sync replica has it. On this
        // single-broker cluster that is one machine, so it buys nothing yet — stage 6 is where
        // this line starts to matter, and where you get to watch acks=1 lose data.
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            var server = HttpKit.server(8080);

            HttpKit.route(server, "/orders", exchange -> {
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, String> in = Json.read(HttpKit.body(exchange), Map.class);
                    OrderPlaced order = OrderPlaced.sample(
                            in.getOrDefault("orderId", "order-?"),
                            in.getOrDefault("userId", "user-?"));

                    // TODO(1) — build a ProducerRecord(topic, key, value) and send it. The KEY
                    //           decides the partition: murmur2(key) % partitionCount.
                    //           STATUS: implemented for you. This one line replaces the whole
                    //           of stage 3's Log.append().
                    long t0 = System.nanoTime();

                    // The KEY decides the partition: murmur2(key) % partitionCount. Same key,
                    // same partition, always — which is what makes per-key ordering possible.
                    // We key by userId, so one user's orders are always in order relative to
                    // each other, while different users spread across all three partitions.
                    var record = new ProducerRecord<>(TOPIC, order.userId(), Json.write(order));

                    // .get() makes this synchronous so we can report the offset to the caller.
                    // Real producers fire-and-forget with a callback and batch behind the scenes.
                    RecordMetadata md = producer.send(record).get();

                    REQUEST_LATENCY.record((System.nanoTime() - t0) / 1_000_000);
                    HttpKit.respond(exchange, 200, Json.write(Map.of(
                            "status", "appended",
                            "partition", md.partition(),
                            "offset", md.offset())));
                } catch (Exception e) {
                    HttpKit.respond(exchange, 500, Json.write(Map.of("error", String.valueOf(e.getMessage()))));
                }
            });

            HttpKit.route(server, "/stats", exchange -> {
                REQUEST_LATENCY.print();
                HttpKit.respond(exchange, 200, "{\"status\":\"printed to server console\"}");
            });

            server.start();
            System.out.println("stage4 order-service on http://localhost:8080 -> kafka " + BOOTSTRAP);
            System.out.println("watch it live:  http://localhost:8081   or   ./kafkalab/infra/watch.sh");
            Thread.currentThread().join();
        }
    }
}
