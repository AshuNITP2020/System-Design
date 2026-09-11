package kafkalab.stage6;

import kafkalab.common.Json;
import kafkalab.common.OrderPlaced;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.BufferedWriter;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * STAGE 6 — a producer whose only job is to be interrupted by a dying broker.
 *
 * <p>No HTTP server this time. It fires {@code -Pcount} records at the cluster with a small pause
 * between them, and — this is the whole trick — writes down the key of every record the broker
 * told it was <b>acknowledged</b>. {@link LossAudit} later reads the topic back and diffs the two
 * sets. Records in the "acked" file that are not in the topic are data loss you can count.
 *
 * <pre>
 *   ./gradlew :kafkalab:stage6-replication:run -Packs=1 -Pcount=2000
 *   docker stop kafkalab-kafka2     # in another terminal, while it runs
 *   ./gradlew :kafkalab:stage6-replication:audit
 * </pre>
 *
 * <p>Everything below the two TODOs is plumbing. The stage lives in {@link #producerProps()}, the
 * send callback, and the experiments in the README.
 */
public final class OrderProducer {

    static final String TOPIC = "orders";
    static final String BOOTSTRAP =
            System.getProperty("bootstrap", "localhost:9092,localhost:9093,localhost:9094");

    /** "0", "1" or "all". A string, not a number — {@code acks=all} has no numeric form. */
    static final String ACKS = System.getProperty("acks", "1");

    static final int COUNT = Integer.getInteger("count", 2000);
    static final int PAUSE_MS = Integer.getInteger("pause", 5);

    /** Shared with {@link LossAudit}. One key per line, written the moment the ack arrives. */
    static final Path ACKED_FILE = Path.of(System.getProperty("acked.file", "/tmp/s6-acked.txt"));

    /** Callbacks run on the producer's I/O thread, so every counter here is atomic. */
    static final AtomicInteger acked = new AtomicInteger();
    static final AtomicInteger failed = new AtomicInteger();

    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true));

        Files.deleteIfExists(ACKED_FILE);
        try (BufferedWriter ackLog = Files.newBufferedWriter(ACKED_FILE);
             KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps())) {

            System.out.printf("stage6 producer: acks=%s count=%d -> %s%n", ACKS, COUNT, BOOTSTRAP);
            System.out.println("kill a broker while this runs:  docker stop kafkalab-kafka2");

            for (int i = 1; i <= COUNT; i++) {
                String key = "o-" + i;
                OrderPlaced order = OrderPlaced.sample(key, "user-" + (i % 50));
                var record = new ProducerRecord<>(TOPIC, key, Json.write(order));

                producer.send(record, (md, e) -> onAck(ackLog, key, md, e));

                if (PAUSE_MS > 0) {
                    Thread.sleep(PAUSE_MS);
                }
                if (i % 200 == 0) {
                    System.out.printf("  sent %d/%d   acked=%d failed=%d%n",
                            i, COUNT, acked.get(), failed.get());
                }
            }

            // Block until every in-flight record has succeeded or given up, so the numbers
            // printed below are final. close() would flush anyway; being explicit is clearer.
            producer.flush();

            System.out.println();
            System.out.printf("attempted     %d%n", COUNT);
            System.out.printf("acked         %d   (written to %s)%n", acked.get(), ACKED_FILE);
            System.out.printf("send errors   %d%n", failed.get());
            System.out.println();
            System.out.println("now run:  ./gradlew :kafkalab:stage6-replication:audit");
        }
    }

    /**
     * <b>TODO(1)</b> — build the producer config for the {@code acks} experiment.
     * <i>STATUS: yours to write.</i>
     *
     * <p>Serializers and bootstrap are already here. What you add is the part being measured:
     *
     * <ul>
     *   <li>{@code ProducerConfig.ACKS_CONFIG} ← {@link #ACKS}. Three values, three different
     *       definitions of "written". Which one makes the broker wait for followers?
     *   <li>{@code ENABLE_IDEMPOTENCE_CONFIG} — defaults to <b>true</b> in kafka-clients 3.x+,
     *       and when it is on the client silently overrides acks to {@code all} and retries to
     *       {@code MAX_VALUE}. What does that do to a run you launched with {@code -Packs=1}?
     *       (You want it off here. Stage 7 is where you turn it back on and find out what it
     *       actually buys.)
     *   <li>{@code RETRIES_CONFIG} — a resend after a leader election can quietly repair the very
     *       loss you are trying to measure. Should this experiment retry? What would you set in
     *       production, and why is that a different answer?
     *   <li>{@code DELIVERY_TIMEOUT_MS_CONFIG} / {@code REQUEST_TIMEOUT_MS_CONFIG} — keep them
     *       short (a few seconds). The defaults are 2 minutes, and a dead broker would otherwise
     *       leave the demo hanging instead of failing.
     * </ul>
     *
     * <p>Delete the throw when you have written it.
     */
    static Properties producerProps() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, ACKS);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);//keeping this true overrides ACKS to all
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        // delivery.timeout.ms must be >= linger.ms + request.timeout.ms, and linger.ms is 5 by
        // default in Kafka 4.0 — so 5000/5000 is rejected at construction by five milliseconds.
        // Shortening the per-attempt wait rather than lengthening the budget, so a dead broker
        // fails fast and the demo does not stall.
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 5000);
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 2000);
        return props;
    }

    /**
     * <b>TODO(2)</b> — the send callback: exactly one of {@code md} and {@code e} is non-null.
     * <i>STATUS: yours to write.</i>
     *
     * <p>On <b>failure</b> ({@code e != null}): bump {@link #failed}, and print the exception —
     * but only for the first few, or a dead broker will bury the terminal. The class name is the
     * interesting part ({@code NotEnoughReplicasException}, {@code TimeoutException},
     * {@code NotLeaderOrFollowerException}), so {@code e.getClass().getSimpleName()} is enough.
     *
     * <p>On <b>success</b>: bump {@link #acked} and append {@code key} plus a newline to
     * {@code ackLog}. That line is your evidence — it is the record the cluster promised you it
     * had. Wrap the write in {@code synchronized (ackLog)}: several I/O threads can land here at
     * once and {@link BufferedWriter} is not thread-safe. Swallow the {@link IOException}; a
     * failure to write your own log file is not the thing under test.
     *
     * <p>Before you run it, predict: with {@code acks=0}, what does {@code md.offset()} contain,
     * and how many failures do you expect when the broker is <em>already dead</em>?
     */
    static void onAck(BufferedWriter ackLog, String key, RecordMetadata md, Exception e) {
        if (e != null) {
            int f = failed.incrementAndGet();
            if (f <= 5) {
                System.out.printf("send error: %s%n", e.getClass().getSimpleName());
            }
        } else {
            acked.incrementAndGet();
            synchronized (ackLog) {
                try {
                    ackLog.write(key);
                    ackLog.newLine();
                } catch (IOException ex) {
                    // swallow it; this is not the thing under test
                }
            }
        }
    }
}
