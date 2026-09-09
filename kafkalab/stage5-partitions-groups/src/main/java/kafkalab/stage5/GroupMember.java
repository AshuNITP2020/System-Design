package kafkalab.stage5;

import kafkalab.common.Json;
import kafkalab.common.OrderPlaced;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * STAGE 5 — one MEMBER of a consumer group.
 *
 * <pre>
 *   ./gradlew :kafkalab:stage5-partitions-groups:runMember -Pgroup=payment -Pid=A
 *   ./gradlew :kafkalab:stage5-partitions-groups:runMember -Pgroup=payment -Pid=B
 * </pre>
 *
 * <p>Config and shutdown are copied from stage 4. The one new thing is the
 * {@link ConsumerRebalanceListener} — see TODO(2). Full API notes are in the README.
 */
public final class GroupMember {

    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true));

        String group = args.length > 0 ? args[0] : "payment";
        String id = args.length > 1 ? args[1] : "A";
        String me = group + "/" + id;

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, OrderService.BOOTSTRAP);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "member-" + id);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        // Shorter than the 45s default so a killed member is evicted quickly and the experiments
        // don't require sitting and waiting. Leave these alone in production.
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000);
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        // Same shutdown pattern as stage 4: wakeup() from the hook, latch so the JVM waits for
        // close() to send LeaveGroup. Without it a killed member lingers for session.timeout.ms.
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
            // TODO(2) — subscribe WITH a ConsumerRebalanceListener so you can see the assignment.
            //           STATUS: yours to write. This is the only new API in the stage.
            //
            // Replace the plain subscribe below with the two-argument form:
            //
             consumer.subscribe(List.of(OrderService.TOPIC), new ConsumerRebalanceListener() {
                 @Override
                 public void onPartitionsRevoked(Collection<TopicPartition> parts) {
                     System.out.printf("member '%s' revoked partitions: %s%n", id, fmt(parts));
                 }
                 @Override
                 public void onPartitionsAssigned(Collection<TopicPartition> parts) {
                     System.out.printf("member '%s' assigned partitions: %s%n", id, fmt(parts));
                 }
             });

            System.out.printf("member '%s' joined group '%s'%n", id, group);

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                if (records.isEmpty()) {
                    continue;
                }
                for (ConsumerRecord<String, String> rec : records) {
                    OrderPlaced order = Json.read(rec.value(), OrderPlaced.class);
                    System.out.printf("  [%-11s] p%d@%-3d key=%-10s %s%n",
                            me, rec.partition(), rec.offset(), rec.key(), order.orderId());
                }
                consumer.commitSync();
            }
        } catch (WakeupException expected) {
            System.out.printf("member '%s' leaving group '%s'%n", id, group);
        } finally {
            consumer.close();
            closed.countDown();
        }
    }

    /** Provided. Renders a partition collection as "p0 p1 p2", or "(nothing)" when empty. */
    static String fmt(Collection<TopicPartition> parts) {
        if (parts.isEmpty()) {
            return "(nothing)";
        }
        return parts.stream().map(p -> "p" + p.partition()).sorted().collect(Collectors.joining(" "));
    }
}
