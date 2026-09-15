package kafkalab.stage7;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * STAGE 7 — the customer's credit card, as a file. <b>Written for you.</b>
 *
 * <p>Every previous stage measured side effects in memory, which quietly hid the thing this stage
 * is about: when your consumer crashes, its memory goes with it, but <em>the charge already
 * happened</em>. A real payment provider does not forget just because your process did.
 *
 * <p>So this ledger is a file. One line per charge, surviving any crash. Two lines with the same
 * order id means one customer was charged twice, and no amount of restarting will undo it.
 */
public final class ChargeLedger {

    static final Path FILE = Path.of(System.getProperty("charges.file", "/tmp/s7-charges.txt"));

    private ChargeLedger() {}

    /** Charges the card. Appends immediately — this is the irreversible part. */
    static void charge(String orderId) {
        try {
            Files.writeString(FILE, orderId + System.lineSeparator(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static void clear() {
        try {
            Files.deleteIfExists(FILE);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Prints total charges, distinct customers charged, and how many were charged twice. */
    static void report() {
        List<String> lines;
        try {
            lines = Files.exists(FILE) ? Files.readAllLines(FILE) : List.of();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        Map<String, Integer> counts = new HashMap<>();
        for (String line : lines) {
            counts.merge(line.strip(), 1, Integer::sum);
        }
        long doubled = counts.values().stream().filter(c -> c > 1).count();

        System.out.println();
        System.out.printf("charges written        %d%n", lines.size());
        System.out.printf("distinct orders        %d%n", counts.size());
        System.out.printf("CHARGED MORE THAN ONCE %d%n", doubled);
        counts.entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .limit(5)
                .forEach(e -> System.out.printf("   %s charged %d times%n", e.getKey(), e.getValue()));
    }
}
