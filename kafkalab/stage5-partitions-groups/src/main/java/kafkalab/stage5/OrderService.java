package kafkalab.stage5;

import kafkalab.common.HttpKit;
import kafkalab.common.Json;
import kafkalab.common.OrderPlaced;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Map;
import java.util.Properties;

/**
 * STAGE 5 — the producer, with the partition key as a knob you can turn.
 *
 * <p>Everything here is copied from stage 4 except {@link #partitionKey}, which is yours.
 * Run with {@code -Pkey=user|order|fixed|none}. See the README for what each one demonstrates.
 */
public final class OrderService {

    static final String TOPIC = "orders";
    static final String BOOTSTRAP = System.getProperty("bootstrap", "localhost:9092");
    static final String KEY_STRATEGY = System.getProperty("key.strategy", "user");

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            var server = HttpKit.server(8080);

            HttpKit.route(server, "/orders", exchange -> {
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, String> in = Json.read(HttpKit.body(exchange), Map.class);
                    OrderPlaced order = OrderPlaced.sample(
                            in.getOrDefault("orderId", "order-?"),
                            in.getOrDefault("userId", "user-?"));

                    String key = partitionKey(order);

                    // A null key is legal and means "no key" — Kafka then picks the partition
                    // itself. Passing the key is the ONLY thing that controls placement.
                    RecordMetadata md = producer
                            .send(new ProducerRecord<>(TOPIC, key, Json.write(order))).get();

                    HttpKit.respond(exchange, 200, Json.write(Map.of(
                            "key", String.valueOf(key),
                            "partition", md.partition(),
                            "offset", md.offset())));
                } catch (Exception e) {
                    HttpKit.respond(exchange, 500, Json.write(Map.of("error", String.valueOf(e.getMessage()))));
                }
            });

            server.start();
            System.out.printf("stage5 producer on :8080  key.strategy=%s  -> %s%n", KEY_STRATEGY, BOOTSTRAP);
            Thread.currentThread().join();
        }
    }

    /**
     * <b>TODO(1)</b> — return the key for this record, based on {@link #KEY_STRATEGY}.
     * <i>STATUS: yours to write.</i>
     *
     * <p>A {@code switch} on the strategy string, returning:
     * <ul>
     *   <li>{@code "user"}  → {@code order.userId()}   — one user always on the same partition
     *   <li>{@code "order"} → {@code order.orderId()}  — unique per record, spreads evenly
     *   <li>{@code "fixed"} → any constant string      — everything on ONE partition
     *   <li>{@code "none"}  → {@code null}             — no key; Kafka chooses
     *   <li>anything else   → default to {@code order.userId()}
     * </ul>
     *
     * <p>That's the whole exercise here — five lines. The interesting part is running each one
     * and looking at where the records land. Kafka computes
     * {@code murmur2(keyBytes) % partitionCount}; you never call that yourself.
     */
    static String partitionKey(OrderPlaced order) {
        switch (KEY_STRATEGY) {
            case "user":
                return order.userId();
            case "order":
                return order.orderId();
            case "fixed":
                return "fixed-key";
            case "none":
                return null;
            default:
                return order.userId();
        }
    }
}
