package kafkalab.stage7;

import kafkalab.common.Json;
import kafkalab.common.OrderPlaced;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * STAGE 7 — a producer built to create duplicates, and then to stop creating them.
 *
 * <p>Stage 6 ended with a durable configuration that gets its durability from <b>retrying</b>.
 * This class shows the bill for that. It uses a deliberately short {@code request.timeout.ms}, so
 * the client frequently gives up waiting for a reply that the broker is in fact about to send. The
 * record was written. The producer does not know that. It sends it again.
 *
 * <p>Each order id is sent exactly once by your code, so <em>every</em> duplicate key found in the
 * topic afterwards was manufactured by the retry machinery, not by the loop below.
 *
 * <pre>
 *   ./gradlew :kafkalab:stage7-delivery-semantics:run -Pidem=false   # duplicates
 *   ./gradlew :kafkalab:stage7-delivery-semantics:run -Pidem=true    # none
 *   ./gradlew :kafkalab:stage7-delivery-semantics:audit -Ptopic=orders
 * </pre>
 */
public final class OrderProducer {

    static final String TOPIC = "orders";
    static final String OUT_TOPIC = "orders-validated";
    static final String BOOTSTRAP = System.getProperty("bootstrap", "localhost:9092");

    static final boolean IDEMPOTENCE = Boolean.parseBoolean(System.getProperty("idempotence", "false"));

    /** Short on purpose. Long enough that some sends succeed, short enough that many time out. */
    static final int REQUEST_TIMEOUT_MS = Integer.getInteger("request.timeout", 25);

    static final int COUNT = Integer.getInteger("count", 500);

    static final AtomicInteger acked = new AtomicInteger();
    static final AtomicInteger failed = new AtomicInteger();

    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true));

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps())) {
            System.out.printf("stage7 producer: idempotence=%s request.timeout.ms=%d count=%d%n",
                    IDEMPOTENCE, REQUEST_TIMEOUT_MS, COUNT);

            for (int i = 1; i <= COUNT; i++) {
                String key = "o-" + i;
                OrderPlaced order = OrderPlaced.sample(key, "user-" + (i % 50));

                // Sent ONCE. Anything that appears twice in the topic was put there by a retry.
                producer.send(new ProducerRecord<>(TOPIC, key, Json.write(order)), (md, e) -> {
                    if (e != null) {
                        if (failed.incrementAndGet() <= 3) {
                            System.out.printf("  send error: %s%n", e.getClass().getSimpleName());
                        }
                    } else {
                        acked.incrementAndGet();
                    }
                });
            }
            producer.flush();

            System.out.println();
            System.out.printf("attempted     %d%n", COUNT);
            System.out.printf("acked         %d%n", acked.get());
            System.out.printf("send errors   %d%n", failed.get());
            System.out.println();
            System.out.println("now count what is actually in the topic:");
            System.out.println("  ./gradlew :kafkalab:stage7-delivery-semantics:audit -Ptopic=orders");
        }
    }

    /**
     * <b>TODO(1)</b> — configure the producer so that this run either does or does not duplicate.
     * <i>STATUS: yours to write.</i>
     *
     * <p>Bootstrap and serializers are here already. Add four things:
     *
     * <ul>
     *   <li>{@code ACKS_CONFIG} — {@code "all"}. You settled this in stage 6; it is not the
     *       variable here.
     *   <li>{@code ENABLE_IDEMPOTENCE_CONFIG} ← {@link #IDEMPOTENCE}. The whole experiment. When
     *       it is on, the client stamps every record with a producer id and a per-partition
     *       sequence number, and the broker refuses to write a sequence it has already written.
     *       Ask yourself before running it: <em>whose</em> duplicates does that catch? Does it
     *       help if you restart the producer and send the same order again from your own code?
     *   <li>{@code RETRIES_CONFIG} — you need retries for this experiment to show anything at
     *       all, so give it a handful. Note that turning idempotence on would raise this for you.
     *   <li>{@code REQUEST_TIMEOUT_MS_CONFIG} ← {@link #REQUEST_TIMEOUT_MS}, and a
     *       {@code DELIVERY_TIMEOUT_MS_CONFIG} big enough to allow several attempts. Stage 6
     *       taught you the constraint between those two and {@code linger.ms} the hard way.
     * </ul>
     *
     * <p>One more worth setting explicitly and thinking about:
     * {@code MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION}. Without idempotence, more than one request in
     * flight plus retries means a resent batch can land <em>after</em> a batch that was sent later
     * — so retries cost you ordering as well as uniqueness. With idempotence on, the broker puts
     * them back in sequence order for you, up to 5 in flight. Try 5 and then 1, and see whether
     * the keys in the topic come back in the order you sent them.
     */
    static Properties producerProps() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, IDEMPOTENCE);
        props.put(ProducerConfig.RETRIES_CONFIG, 5);
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, REQUEST_TIMEOUT_MS);
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, REQUEST_TIMEOUT_MS + 1000);
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, IDEMPOTENCE ? 5 : 1);
        return props;
    }
}
