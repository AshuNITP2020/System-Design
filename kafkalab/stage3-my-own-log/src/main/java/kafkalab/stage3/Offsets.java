package kafkalab.stage3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Each consumer group's read position. Written for you — storing a number in a file is not the
 * lesson. <em>When you write it</em> is the lesson, and that decision lives in {@link Consumer}.
 *
 * <p>Look at how small this is, then go back and look at stage 2's {@code outbox} table. Stage 2
 * stored "has payment processed row 7?" as state <b>inside the shared row</b>, which is why four
 * workers fought over it. Here, payment's progress is one number in payment's own file. Nobody
 * else reads it, nobody else writes it, and nothing payment does can hide a record from analytics.
 *
 * <p>Adding a fifth consumer costs: one new file, created automatically, starting at 0. No
 * {@code ALTER TABLE}, no roster, no change to the producer.
 */
public final class Offsets {

    private static final Path DIR =
            Path.of(System.getProperty("log.dir", "kafkalab/stage3-my-own-log/data")).resolve("offsets");

    private Offsets() {}

    private static Path fileFor(String group) throws IOException {
        Files.createDirectories(DIR);
        return DIR.resolve(group + ".offset");
    }

    /** Where this group should read from next. A group that has never committed starts at 0. */
    public static long position(String group) throws IOException {
        Path f = fileFor(group);
        if (!Files.exists(f)) {
            return 0;
        }
        String s = Files.readString(f).trim();
        return s.isEmpty() ? 0 : Long.parseLong(s);
    }

    /** Record that this group has consumed everything before {@code nextOffset}. */
    public static void commit(String group, long nextOffset) throws IOException {
        Files.writeString(fileFor(group), Long.toString(nextOffset));
    }

    /** Rewind (or fast-forward) a group. This one line is what "replay" means. */
    public static void seek(String group, long offset) throws IOException {
        commit(group, offset);
    }
}
