package kafkalab.stage7;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * STAGE 7 — read from one topic, write to another, and make both plus the read position a single
 * all-or-nothing operation.
 *
 * <p>This is the pattern Kafka's transactions were actually built for, and it is narrower than the
 * phrase "exactly-once" suggests: <b>consume from Kafka, produce to Kafka</b>. The output records
 * and the consumer's committed offsets go into the same transaction, so it is impossible to end up
 * having produced the output without having recorded that you read the input, or the reverse.
 *
 * <p>What it does <em>not</em> cover is a side effect outside Kafka — the charge in
 * {@link ChargeLedger}. No Kafka transaction can roll back a credit card. That gap is the whole
 * point of {@link DedupeStore}, and finding its edge is the last exercise of the stage.
 *
 * <pre>
 *   ./gradlew :kafkalab:stage7-delivery-semantics:runProcessor
 *   ./gradlew :kafkalab:stage7-delivery-semantics:runProcessor -Pabort=3   # abort every 3rd batch
 * </pre>
 */
public final class TransactionalProcessor {

    static final String IN_TOPIC = OrderProducer.TOPIC;
    static final String OUT_TOPIC = OrderProducer.OUT_TOPIC;
    static final String GROUP = "validator";

    /** Abort every nth transaction, so the output topic contains records that never committed. */
    static final int ABORT_EVERY = Integer.getInteger("abort.every", 0);

    /**
     * The name that makes a producer transactional. It must be <b>stable across restarts</b> —
     * that is how a restarted instance is recognised as the same logical writer and any
     * transaction the previous instance left half-finished gets fenced off and rolled back.
     */
    static final String TX_ID = System.getProperty("tx.id", "validator-1");

    static final AtomicInteger committed = new AtomicInteger();
    static final AtomicInteger aborted = new AtomicInteger();
    static final AtomicInteger batches = new AtomicInteger();

    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true));

        Properties cprops = new Properties();
        cprops.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, OrderProducer.BOOTSTRAP);
        cprops.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        cprops.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        cprops.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP);
        cprops.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        cprops.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 20);

        // Both of these are required, and for the same reason: inside a transaction, the producer
        // owns the offsets. An auto-commit running on a timer would commit them outside the
        // transaction and quietly destroy the guarantee you are here to build.
        cprops.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        cprops.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");

        Properties pprops = new Properties();
        pprops.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, OrderProducer.BOOTSTRAP);
        pprops.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        pprops.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        pprops.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, TX_ID);
        // Setting a transactional.id turns idempotence on implicitly; being explicit costs nothing.
        pprops.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(cprops);
        KafkaProducer<String, String> producer = new KafkaProducer<>(pprops);

        // Same latch pattern as every consumer since stage 4, and it earns its keep here too:
        // a hook that only calls wakeup() lets the JVM exit before the finally block below has
        // printed the batch counts — on a kill you would see no summary at all.
        CountDownLatch closed = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            consumer.wakeup();
            try {
                closed.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }));

        System.out.printf("stage7 processor  %s -> %s  transactional.id=%s abort.every=%s%n",
                IN_TOPIC, OUT_TOPIC, TX_ID, ABORT_EVERY == 0 ? "never" : ABORT_EVERY);

        try {
            consumer.subscribe(List.of(IN_TOPIC));
            processLoop(consumer, producer);
        } catch (WakeupException expected) {
            System.out.println("shutting down");
        } finally {
            producer.close();
            consumer.close();
            System.out.printf("%nbatches %d, committed %d, aborted %d%n",
                    batches.get(), committed.get(), aborted.get());
            System.out.println("compare the two views of the output topic:");
            System.out.println("  ...:audit -Ptopic=orders-validated -Pisolation=read_committed");
            System.out.println("  ...:audit -Ptopic=orders-validated -Pisolation=read_uncommitted");
            closed.countDown();   // last: releasing the hook lets the JVM exit
        }
    }

    /**
     * <b>TODO(4)</b> — the transaction lifecycle.
     * <i>STATUS: yours to write. Five method calls, in an order that matters.</i>
     *
     * <p>Once, before the loop starts: {@code producer.initTransactions()}. This registers the
     * transactional id with the broker and rolls back anything a previous run of this same id left
     * dangling. Calling it inside the loop is a common and expensive mistake.
     *
     * <p>Then, for each non-empty batch:
     *
     * <ol>
     *   <li>{@code producer.beginTransaction()}
     *   <li>For each record, send the transformed output to {@link #OUT_TOPIC}. Keep the same key,
     *       and for this exercise the same value — the stage is about atomicity, not about
     *       inventing a transformation.
     *   <li>{@code producer.sendOffsetsToTransaction(offsetsOf(records), consumer.groupMetadata())}.
     *       <b>This is the line that makes it exactly-once.</b> The consumer's read position is
     *       written by the producer, inside the transaction, alongside the output. Without it you
     *       have atomic output and a separately-committed offset, which is two-phase wishful
     *       thinking.
     *   <li>If {@link #shouldAbort()} says so, {@code producer.abortTransaction()} and count it in
     *       {@link #aborted}; otherwise {@code producer.commitTransaction()} and count it in
     *       {@link #committed}. Do not commit the consumer separately anywhere — that is the whole
     *       idea.
     *   <li>Wrap the body in a try/catch and {@code abortTransaction()} on any exception.
     * </ol>
     *
     * <p>Predict before you run: after aborting a batch, what happens to those input records? Are
     * they re-read on the next poll, and if so, are the outputs you already sent for them still
     * sitting in the output topic? Then check both with the audit tool at each isolation level.
     */
    static void processLoop(KafkaConsumer<String, String> consumer,
                            KafkaProducer<String, String> producer) {
        producer.initTransactions();
        while (true) {
            ConsumerRecords<String, String> records = poll(consumer);
            if (records.isEmpty()) {
                continue;
            }

            try {
                producer.beginTransaction();
                for (ConsumerRecord<String, String> rec : records) {
                    producer.send(new org.apache.kafka.clients.producer.ProducerRecord<>(
                            OUT_TOPIC, rec.key(), rec.value()));
                }
                producer.sendOffsetsToTransaction(offsetsOf(records), consumer.groupMetadata());
                if (shouldAbort()) {
                    producer.abortTransaction();
                    aborted.incrementAndGet();
                } else {
                    producer.commitTransaction();
                    committed.incrementAndGet();
                }
            } catch (Exception e) {
                producer.abortTransaction();
                aborted.incrementAndGet();
                System.out.printf("transaction failed: %s%n", e.getMessage());
            } finally {
                batches.incrementAndGet();
            }

        }
    }

    /** Provided. The read position to record is the offset AFTER the last record consumed. */
    static Map<TopicPartition, OffsetAndMetadata> offsetsOf(ConsumerRecords<String, String> records) {
        Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
        for (TopicPartition p : records.partitions()) {
            List<ConsumerRecord<String, String>> forPartition = records.records(p);
            long last = forPartition.get(forPartition.size() - 1).offset();
            offsets.put(p, new OffsetAndMetadata(last + 1));
        }
        return offsets;
    }

    /** Provided. True on every nth batch when -Pabort is set. */
    static boolean shouldAbort() {
        return ABORT_EVERY > 0 && batches.get() % ABORT_EVERY == 0;
    }

    /** Provided, so the TODO above is about the transaction and not about the poll. */
    static ConsumerRecords<String, String> poll(KafkaConsumer<String, String> consumer) {
        return consumer.poll(Duration.ofSeconds(1));
    }
}
