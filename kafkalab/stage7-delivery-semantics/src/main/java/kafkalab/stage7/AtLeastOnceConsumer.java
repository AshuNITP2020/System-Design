package kafkalab.stage7;

import kafkalab.common.Json;
import kafkalab.common.OrderPlaced;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * STAGE 7 — the consumer half of the duplicate problem, which the producer settings cannot touch.
 *
 * <p>You met this in stage 3 as "commit before or after processing?" and answered it correctly:
 * committing after processing means a crash reprocesses records, which is at-least-once. Back then
 * the side effect was a counter in memory, so reprocessing looked harmless. Here the side effect is
 * {@link ChargeLedger}, which is a file, and reprocessing charges a real customer a second time.
 *
 * <pre>
 *   # charge 20 orders, then die before committing:
 *   ./gradlew :kafkalab:stage7-delivery-semantics:runConsumer -Pcrash=20
 *   # restart and watch it redo work it already did:
 *   ./gradlew :kafkalab:stage7-delivery-semantics:runConsumer
 *   # now the same crash, with an idempotency store in the way:
 *   ./gradlew :kafkalab:stage7-delivery-semantics:runConsumer -Pcrash=20 -Pdedupe=true
 * </pre>
 */
public final class AtLeastOnceConsumer {

    static final String GROUP = System.getProperty("group", "payment");
    static final int CRASH_AFTER = Integer.getInteger("crash.after", 0);
    static final boolean DEDUPE = Boolean.parseBoolean(System.getProperty("dedupe", "false"));
    static final Path STORE_FILE = Path.of(System.getProperty("dedupe.file", "/tmp/s7-dedupe.txt"));

    static final AtomicInteger applied = new AtomicInteger();
    static final AtomicInteger skipped = new AtomicInteger();

    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true));

        DedupeStore store = new DedupeStore(STORE_FILE);

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, OrderProducer.BOOTSTRAP);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);

        // The crash in this stage is halt(9) — no LeaveGroup is ever sent, so the broker waits
        // out the session timeout before evicting the dead member and freeing its partitions.
        // At the 45s default, a restarted consumer sits assigned NOTHING for 45 seconds and the
        // demo looks broken. Same fix as stage 5: shorter timeout, faster eviction.
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 6000);
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 2000);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        CountDownLatch closed = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            consumer.wakeup();
            try {
                closed.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }));

        System.out.printf("stage7 consumer group='%s' dedupe=%s crash.after=%s (store has %d ids)%n",
                GROUP, DEDUPE, CRASH_AFTER == 0 ? "never" : CRASH_AFTER, store.size());

        try {
            consumer.subscribe(List.of(OrderProducer.TOPIC));
            consumeLoop(consumer, store);
        } catch (WakeupException expected) {
            System.out.println("shutting down");
        } finally {
            consumer.close();
            // The report must print BEFORE countDown(). The shutdown hook is blocked on this
            // latch, and the JVM exits the instant its hooks finish — releasing the latch first
            // would let the JVM die mid-report, and on a kill you would see no numbers at all.
            System.out.printf("%napplied %d, skipped as already-done %d%n", applied.get(), skipped.get());
            ChargeLedger.report();
            closed.countDown();
        }
    }

    /**
     * <b>TODO(2)</b> — the at-least-once loop, with an idempotency check you can switch off.
     * <i>STATUS: yours to write.</i>
     *
     * <p>The shape, per {@code poll}:
     *
     * <ol>
     *   <li>Poll for up to a second. Skip the rest if nothing came back.
     *   <li>For each record: parse it with {@code Json.read(rec.value(), OrderPlaced.class)}.
     *   <li>If {@link #DEDUPE} is on and {@code store.alreadyDone(orderId)}, count it in
     *       {@link #skipped} and move on <em>without charging</em>.
     *   <li>Otherwise call {@link ChargeLedger#charge}, then {@code store.markDone(orderId)} when
     *       dedupe is on, then bump {@link #applied}. Print the id so you can watch it work.
     *   <li>Call {@link #maybeCrash()} after each record.
     *   <li>After the whole batch, {@code consumer.commitSync()}.
     * </ol>
     *
     * <p>Step 6 is the one to think about rather than type. You are committing after processing,
     * which you already know makes this at-least-once. Move the commit to the top of the batch
     * instead and you have at-most-once, where a crash loses orders rather than repeating them.
     * Every system you will ever build picks one of those two. There is no third choice, and the
     * dedupe store does not create one — it only makes the repetition invisible to the customer.
     *
     * <p>Note what {@link #maybeCrash()} does not do: no {@code close()}, no commit, no shutdown
     * hooks. That is deliberate. A tidy shutdown is exactly what a real crash denies you, and if
     * this loop exited politely there would be nothing to see.
     */
    static void consumeLoop(KafkaConsumer<String, String> consumer, DedupeStore store) {

        while (true) {
            ConsumerRecords<String, String> records = poll(consumer);
            if (records.isEmpty()) {
                continue;
            }

            for (ConsumerRecord<String, String> rec : records) {
                String orderId = orderIdOf(rec);

                if (DEDUPE && store.alreadyDone(orderId)) {
                    skipped.incrementAndGet();
                    continue;
                }

                ChargeLedger.charge(orderId);

                if (DEDUPE) {
                    store.markDone(orderId);
                }
                applied.incrementAndGet();
            }
            maybeCrash();
            consumer.commitSync();
        }
    }

    /** Provided. Pulls the plug the instant the quota of processed records is reached. */
    static void maybeCrash() {
        if (CRASH_AFTER > 0 && applied.get() >= CRASH_AFTER) {
            System.out.printf("%n*** simulated crash after %d charges, BEFORE the commit ***%n",
                    applied.get());
            System.out.flush();
            Runtime.getRuntime().halt(9);
        }
    }

    /** Provided, so the loop above stays readable. */
    static String orderIdOf(ConsumerRecord<String, String> rec) {
        OrderPlaced order = Json.read(rec.value(), OrderPlaced.class);
        return order.orderId();
    }

    /** Provided. The poll call, factored out only so the TODO above is about logic, not typing. */
    static ConsumerRecords<String, String> poll(KafkaConsumer<String, String> consumer) {
        return consumer.poll(Duration.ofSeconds(1));
    }
}
