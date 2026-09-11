package kafkalab.stage6;

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
import java.nio.file.Files;
import java.time.Duration;
import java.util.*;

/**
 * STAGE 6 — did the cluster keep what it promised to keep?
 *
 * <p>{@link OrderProducer} wrote down the key of every record it was told was acknowledged.
 * This reads the topic back from offset 0 and diffs the two. A key that is in the acked file and
 * not in the topic was <b>acknowledged and then lost</b> — the producer's callback reported
 * success, and the record is not there.
 *
 * <pre>
 *   ./gradlew :kafkalab:stage6-replication:audit
 * </pre>
 *
 * <p>Run this after the killed broker is back up. With a partition still offline the read is
 * incomplete and every one of its records would look "lost" — a false positive worth being able
 * to recognise, since it is exactly what a monitoring dashboard would show you at 3am.
 */
public final class LossAudit {

    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true));

        if (!Files.exists(OrderProducer.ACKED_FILE)) {
            System.out.println("no " + OrderProducer.ACKED_FILE + " — run the producer first.");
            return;
        }
        Set<String> ackedKeys = new LinkedHashSet<>(Files.readAllLines(OrderProducer.ACKED_FILE));
        List<String> present = drainTopic();

        report(ackedKeys, present);
    }

    /**
     * <b>TODO(3)</b> — turn the two collections into the numbers that settle the argument.
     * <i>STATUS: yours to write.</i>
     *
     * <p>Compute and print, roughly in this shape:
     *
     * <pre>
     *   acks=1
     *     acked by the cluster   1873
     *     present in the topic   1841
     *     ACKED BUT ABSENT         32     &lt;- data loss
     *     present, never acked      0
     *     duplicates in topic       0
     * </pre>
     *
     * <ul>
     *   <li><b>Acked but absent</b> — {@code ackedKeys} minus the distinct keys in {@code present}.
     *       {@code Set::removeAll} on a copy is the whole computation. This is the number the
     *       stage exists to produce; print it in red if you like, it has earned it.
     *   <li><b>Present but never acked</b> — the reverse difference. Not a bug: a record can reach
     *       the log and the ack be lost on the way back. Which {@code acks} setting makes this
     *       common, and what does its existence say about ever retrying a failed send?
     *   <li><b>Duplicates</b> — {@code present.size() - distinct.size()}. Retries put them there.
     *       You cannot remove them at this layer, which is the sentence stage 7 begins from.
     * </ul>
     *
     * <p>Then run the same thing at {@code acks=0}, {@code 1} and {@code all} and fill in the
     * table in the README. Predict each row before you run it.
     */
    static void report(Set<String> ackedKeys, List<String> present) {
        System.out.println("acked by the cluster " + ackedKeys.size());
        System.out.println("present in the topic " + present.size());
        System.out.println("ACKED BUT ABSENT         " + getAckedButAbsent(ackedKeys, present));
        System.out.println("present, never acked      " + getPresentButNeverAcked(ackedKeys, present));
        System.out.println("duplicates in topic       " + getDuplicates(present));

    }

    private static String getDuplicates(List<String> present) {
        Set<String> distinctPresent = new HashSet<>(present);
        return String.valueOf(present.size() - distinctPresent.size());
    }

    private static String getPresentButNeverAcked(Set<String> ackedKeys, List<String> present) {
        Set<String> distinctPresent = new HashSet<>(present);
        distinctPresent.removeAll(ackedKeys);
        // distinctPresent is now exactly "in the topic, never acknowledged" — its own size is
        // the answer. Subtracting it from ackedKeys.size() mixed two unrelated quantities.
        return String.valueOf(distinctPresent.size());
    }

    private static String getAckedButAbsent(Set<String> ackedKeys, List<String> present) {
        // removeAll mutates the set it is called on, so this works on a COPY of ackedKeys.
        // Calling it on the caller's set emptied it out and made every later line wrong.
        Set<String> absent = new HashSet<>(ackedKeys);
        absent.removeAll(new HashSet<>(present));
        return String.valueOf(absent.size());
    }

    // ---------------------------------------------------------------------------------------
    // Plumbing below. Reading a topic end-to-end is not the subject of this stage.
    // ---------------------------------------------------------------------------------------

    /**
     * Reads every record currently in the topic and returns the keys, in partition order.
     *
     * <p>No {@code group.id} and no commits: this uses {@code assign()} rather than
     * {@code subscribe()}, so it is not a group member, joins no rebalance, and leaves no trace
     * in {@code __consumer_offsets}. That is what you want for an auditor — it must be able to
     * run twice and read the same thing both times. (Stage 5, experiment on {@code assign()} vs
     * {@code subscribe()}: this is the case where {@code assign()} is right.)
     */
    static List<String> drainTopic() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, OrderProducer.BOOTSTRAP);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);

        // On this topic, which uses no transactions, this line changes NOTHING. It is here so
        // that the audit still means the same thing after stage 7 introduces transactions, and
        // because it is a good excuse to be clear about what does the work instead.
        //
        // What actually limits the audit is the HIGH WATER MARK: a consumer cannot read a record
        // until that record has been replicated to every broker in the partition's current ISR.
        // No isolation level lets you past it. So the audit sees the cluster's committed view for
        // free, not because of the setting below.
        //
        // That is also why the records you lose at acks=1 are records nobody ever read. They sat
        // on the leader's disk, un-replicated, below the high water mark, invisible — and then
        // the leader died and they were gone. The producer was told "stored" all the same.
        //
        // read_committed's real job (stage 7) is to hide records belonging to transactions that
        // have not committed yet, by stopping the read at the last stable offset instead.
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");

        List<String> keys = new ArrayList<>();
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            List<PartitionInfo> infos = consumer.partitionsFor(OrderProducer.TOPIC);
            if (infos == null || infos.isEmpty()) {
                System.out.println("topic " + OrderProducer.TOPIC + " has no partitions — is the cluster up?");
                return keys;
            }
            List<TopicPartition> parts = infos.stream()
                    .map(i -> new TopicPartition(i.topic(), i.partition()))
                    .toList();

            consumer.assign(parts);
            Map<TopicPartition, Long> ends = consumer.endOffsets(parts);
            consumer.seekToBeginning(parts);

            System.out.println("reading " + parts.size() + " partitions to their end offsets " + ends.values());

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
