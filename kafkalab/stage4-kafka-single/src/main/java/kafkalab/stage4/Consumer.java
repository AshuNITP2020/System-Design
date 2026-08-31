package kafkalab.stage4;

import kafkalab.common.Json;
import kafkalab.common.OrderPlaced;
import kafkalab.common.SimulatedService;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * STAGE 4 — the consumer, now reading from real Kafka.
 *
 * <h2>What vanished</h2>
 *
 * Your entire {@code Offsets.java} is gone — no {@code position()}, no {@code commit()}, no
 * {@code payment.offset} file. Kafka keeps each group's position in an internal topic called
 * {@code __consumer_offsets}. Same idea as your file, stored in a log instead.
 *
 * <p>Your {@code readFrom(position, 10)} became {@code poll(Duration)}. The difference worth
 * understanding: yours was a <em>busy poll</em> that rescanned the file and slept 200ms when empty.
 * {@code poll()} is a <b>long poll</b> — it blocks on the broker until data arrives or the timeout
 * expires. That is why stage 2's "no interval is both cheap and fast" dilemma simply does not
 * exist here.
 *
 * <p>The line that carries the most meaning is {@code group.id}. Two processes with the same
 * group.id <em>share</em> the partitions between them. Two with different group.ids each get
 * <em>everything</em>. Your four "groups" in stage 3 were four offset files; here they are four
 * values of one config key.
 */
public final class Consumer {

    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true));

        String group = args.length > 0 ? args[0] : "payment";
        SimulatedService svc = switch (group) {
            case "payment"   -> SimulatedService.payment();
            case "inventory" -> SimulatedService.inventory();
            case "email"     -> SimulatedService.email();
            case "analytics" -> SimulatedService.analytics();
            default -> throw new IllegalArgumentException("unknown group: " + group);
        };

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, OrderService.BOOTSTRAP);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        // THE line. Four different values = four independent readers of the same records.
        props.put(ConsumerConfig.GROUP_ID_CONFIG, group);

        // A group with no committed offset starts at the beginning. This is your stage 3
        // "a group that has never committed starts at 0", spelled as a config value.
        // Change it to "latest" and a new group ignores all history — try both.
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Off, so the commit below is explicit and you can see where it happens. Turn this on
        // and Kafka commits on a timer in the background — convenient, and it quietly converts
        // your delivery guarantee into "at-least-once, but the window is auto.commit.interval.ms".
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        // Clean shutdown matters more than it looks. close() sends a LeaveGroup request so the
        // broker knows at once that this member is gone. Without it the group stays "active"
        // until session.timeout.ms (45s by default) — and while it is active you CANNOT reset
        // its offsets: `kafka-consumer-groups --reset-offsets` refuses with "Assignments can
        // only be reset if the group is inactive".
        //
        // The latch is not decoration. wakeup() is the only KafkaConsumer method safe to call
        // from another thread, but a hook that merely calls it and returns creates a race: once
        // every shutdown hook has finished, the JVM exits WITHOUT waiting for the main thread —
        // so close() never runs and LeaveGroup is never sent. The hook has to block until the
        // consume loop has actually closed. (Diagnosed the hard way: the reset kept failing.)
        CountDownLatch closed = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            consumer.wakeup();
            try {
                closed.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }));

        try {
            consumer.subscribe(List.of(OrderService.TOPIC));
            System.out.printf("stage4 consumer group '%s' subscribed to '%s'%n", group, OrderService.TOPIC);

            // TODO(2) — poll, apply each record, then commit. Same at-least-once choice you
            //           made in stage 3, now spelled commitSync().
            //           STATUS: implemented for you. group.id + commitSync() replace the whole
            //           of stage 3's Offsets.java.
            while (true) {
                // Long poll: blocks up to 1s waiting for records rather than spinning.
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                if (records.isEmpty()) {
                    continue;
                }

                for (ConsumerRecord<String, String> rec : records) {
                    try {
                        OrderPlaced order = Json.read(rec.value(), OrderPlaced.class);
                        svc.apply(order);
                    } catch (Exception e) {
                        // Logged and skipped. Stage 3 blocked the group instead; both are wrong
                        // in different ways, and stage 7 is where the real answer (dead-letter
                        // topic + idempotent consumer) shows up.
                        System.out.printf("  [%-9s] FAILED p%d@%d: %s%n",
                                group, rec.partition(), rec.offset(), e.getMessage());
                    }
                }

                // Commit AFTER processing — at-least-once, exactly the choice you made in
                // stage 3. Move this above the for-loop and you have at-most-once again.
                consumer.commitSync();
            }
        } catch (org.apache.kafka.common.errors.WakeupException expectedOnShutdown) {
            System.out.printf("consumer group '%s' shutting down%n", group);
        } finally {
            consumer.close();     // sends LeaveGroup
            closed.countDown();   // releases the shutdown hook
        }
    }
}
