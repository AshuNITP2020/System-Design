package kafkalab.stage7;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

/**
 * STAGE 7 — your own idempotency table, which is where exactly-once actually comes from.
 *
 * <p>Kafka's idempotent producer stops the <em>producer</em> writing the same record twice.
 * Kafka's transactions make a read-process-write cycle atomic <em>within Kafka</em>. Neither of
 * them knows anything about the credit card in {@link ChargeLedger}, because that lives outside
 * Kafka entirely. The only thing that can stop a customer being charged twice is a record, kept
 * next to the charge itself, saying "this order has already been handled".
 *
 * <p>Every payment API you will ever integrate with calls this an <b>idempotency key</b>. This
 * class is that idea in twenty lines.
 */
public final class DedupeStore {

    private final Path file;
    private final Set<String> seen = new HashSet<>();

    /**
     * Loads whatever survived the last crash. Provided — the interesting part is what you do with
     * it, not how it is read off disk.
     */
    DedupeStore(Path file) {
        this.file = file;
        try {
            if (Files.exists(file)) {
                seen.addAll(Files.readAllLines(file));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static void clear(Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * <b>TODO(3a)</b> — has this order already been handled?
     * <i>STATUS: yours to write. One line.</i>
     */
    boolean alreadyDone(String orderId) {
        return seen.contains(orderId);
    }

    /**
     * <b>TODO(3b)</b> — remember that this order has been handled.
     * <i>STATUS: yours to write. Two lines, and one of them matters more than it looks.</i>
     *
     * <p>Add the id to {@link #seen} so the running process stops immediately, <b>and append it to
     * {@link #file}</b> so a restarted process still knows. An in-memory set alone is worthless
     * here: the whole scenario is that the process died.
     *
     * <p>Now the question this class exists to make you answer. In
     * {@link AtLeastOnceConsumer} you will call {@link #alreadyDone} and {@link ChargeLedger#charge}
     * and this method, in some order. There are two orders available and both are wrong:
     *
     * <ul>
     *   <li><b>Mark first, then charge.</b> Crash in between and the order is marked as handled
     *       but the customer was never charged. You have lost money.
     *   <li><b>Charge first, then mark.</b> Crash in between and the charge happened but nothing
     *       recorded it, so the restart charges again. You have charged twice — the very thing
     *       you built this store to prevent, now merely rarer.
     * </ul>
     *
     * <p>Pick the second one for the experiment, because "rarer" is genuinely the correct
     * engineering answer here and because the failure it leaves behind is the point of the stage.
     * Then answer this: what single property would you need from the charge and the mark, together,
     * to close that window completely? Where in a real system would you get it? That answer is the
     * boundary of exactly-once, and it is not inside Kafka.
     */
    void markDone(String orderId) {
        seen.add(orderId);
        append(orderId);
    }

    /** Provided, so your two methods above stay one-liners. */
    void append(String orderId) {
        try {
            Files.writeString(file, orderId + System.lineSeparator(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    int size() {
        return seen.size();
    }
}
