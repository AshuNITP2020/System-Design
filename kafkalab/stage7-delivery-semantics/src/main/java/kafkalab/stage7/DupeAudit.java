package kafkalab.stage7;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * STAGE 7 — how many copies of each record are actually in a topic. <b>Written for you.</b>
 *
 * <p>You wrote the equivalent of this in stage 6, so it is plumbing now rather than an exercise.
 * The one new knob is the isolation level, which is worth running both ways against a topic that
 * a transactional processor has been aborting into:
 *
 * <pre>
 *   ./gradlew :kafkalab:stage7-delivery-semantics:audit -Ptopic=orders-validated -Pisolation=read_committed
 *   ./gradlew :kafkalab:stage7-delivery-semantics:audit -Ptopic=orders-validated -Pisolation=read_uncommitted
 * </pre>
 *
 * <p>The difference between those two numbers is the set of records that were written to the log
 * and then abandoned. They are physically present in the segment files either way. One setting
 * shows them to you and the other stops at the last stable offset and pretends they are not there.
 */
public final class DupeAudit {

    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true));

        String topic = System.getProperty("topic", "orders");
        String isolation = System.getProperty("isolation", "read_committed");

        List<String> keys = drain(topic, isolation);

        Map<String, Integer> counts = new HashMap<>();
        for (String k : keys) {
            counts.merge(String.valueOf(k), 1, Integer::sum);
        }
        long duplicated = counts.values().stream().filter(c -> c > 1).count();
        long extra = keys.size() - counts.size();

        System.out.println();
        System.out.printf("topic                 %s  (%s)%n", topic, isolation);
        System.out.printf("records read          %d%n", keys.size());
        System.out.printf("distinct keys         %d%n", counts.size());
        System.out.printf("keys appearing twice+ %d%n", duplicated);
        System.out.printf("EXTRA COPIES          %d%n", extra);
        counts.entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .limit(5)
                .forEach(e -> System.out.printf("   %s appears %d times%n", e.getKey(), e.getValue()));
    }

    static List<String> drain(String topic, String isolation) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, OrderProducer.BOOTSTRAP);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, isolation);

        List<String> keys = new ArrayList<>();
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            List<PartitionInfo> infos = consumer.partitionsFor(topic);
            if (infos == null || infos.isEmpty()) {
                System.out.println("topic " + topic + " has no partitions — is the broker up?");
                return keys;
            }
            List<TopicPartition> parts = infos.stream()
                    .map(i -> new TopicPartition(i.topic(), i.partition()))
                    .toList();

            consumer.assign(parts);
            Map<TopicPartition, Long> ends = consumer.endOffsets(parts);
            consumer.seekToBeginning(parts);

            int emptyPolls = 0;
            while (emptyPolls < 3 && !atEnd(consumer, parts, ends)) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(2));
                if (records.isEmpty()) {
                    emptyPolls++;
                    continue;
                }
                emptyPolls = 0;
                for (ConsumerRecord<String, String> rec : records) {
                    keys.add(rec.key());
                }
            }
        }
        return keys;
    }

    private static boolean atEnd(KafkaConsumer<String, String> consumer,
                                 List<TopicPartition> parts,
                                 Map<TopicPartition, Long> ends) {
        for (TopicPartition p : parts) {
            if (consumer.position(p) < ends.getOrDefault(p, 0L)) {
                return false;
            }
        }
        return true;
    }
}
