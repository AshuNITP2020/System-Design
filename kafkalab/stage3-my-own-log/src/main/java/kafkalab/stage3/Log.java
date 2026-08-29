package kafkalab.stage3;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * THE LOG. One append-only file. This class is stage 3, and you write it.
 *
 * <h2>The file format</h2>
 *
 * Records are length-prefixed and packed back to back, with nothing else in the file:
 *
 * <pre>
 *   [4-byte length][payload bytes][4-byte length][payload bytes]...
 *        int            UTF-8          int            UTF-8
 * </pre>
 *
 * The length prefix is what makes the file parseable at all. Without it you cannot tell where one
 * record ends and the next begins — and you will meet the second reason it matters (torn writes)
 * in the walls below.
 *
 * <p>{@link DataOutputStream#writeInt} and {@link DataInputStream#readInt} handle the 4 bytes for
 * you, big-endian, so you never touch bit-shifting.
 *
 * <h2>What an offset is</h2>
 *
 * The <b>index of a record</b>: the first record appended is offset 0, the second is 1, and so on.
 * Not a byte position. That is a deliberate choice and it costs you something — finding offset
 * 500 means walking 500 records from the start of the file. Feel that cost; it is why real Kafka
 * splits a partition into segments with a companion {@code .index} file
 * ({@code storage-internals.html}).
 */
public final class Log {

    /** One record, and where it sits in the log. */
    public record Record(long offset, String value) {}

    private static final Path DIR =
            Path.of(System.getProperty("log.dir", "kafkalab/stage3-my-own-log/data"));
    private static final Path FILE = DIR.resolve("orders.log");

    private Log() {}

    static Path file() throws IOException {
        Files.createDirectories(DIR);
        if (!Files.exists(FILE)) {
            Files.createFile(FILE);
        }
        return FILE;
    }

    /**
     * Append one record and return the offset it landed at.
     *
     * <p>TODO(1): open the file <b>in append mode</b> — {@code new FileOutputStream(file().toFile(),
     * true)} wrapped in a {@link DataOutputStream} — then {@code writeInt(bytes.length)} followed
     * by {@code write(bytes)}, where {@code bytes} is {@code value.getBytes(UTF_8)}.
     *
     * <p>The offset to return is the number of records already in the file, which you can get from
     * {@code readFrom(0, Integer.MAX_VALUE).size()} before you write. That is O(n) and wasteful —
     * leave it wasteful for now, it becomes wall #4.
     *
     * <p>Two things to notice once it works:
     * <ul>
     *   <li>Appending never modifies an existing byte. Stage 2's whole problem was four workers
     *       <em>updating</em> a shared row. Here there is nothing to update, so there is nothing
     *       to race over.
     *   <li>There is exactly one writer and it only ever moves forward. That is what makes the
     *       ordering guarantee possible — and it is also the ceiling you hit in wall #3.
     * </ul>
     */
    public static long append(String value) throws IOException {
        throw new UnsupportedOperationException("TODO(1): implement append");
    }

    /**
     * Read up to {@code max} records starting at {@code fromOffset}.
     *
     * <p>TODO(2): open a {@link DataInputStream} over a {@link BufferedInputStream} of the file,
     * then loop: {@code readInt()} to get the length, {@code readNBytes(len)} to get the payload,
     * counting records as you go. Skip records whose index is below {@code fromOffset}; collect
     * the rest until you have {@code max}. Stop when {@code readInt()} throws {@link EOFException}
     * — that is the normal end of file, not an error.
     *
     * <p>Return a list of {@link Record}, each carrying its own offset. The consumer needs that
     * offset to know what to commit.
     *
     * <p><b>Then break it, in this order:</b>
     *
     * <ol>
     *   <li><b>Run all four consumers at once.</b> Every one of them sees all 12 orders — 48 of
     *       48, deterministically, every time. Compare that to stage 2's 36-of-48 race. Nothing is
     *       shared, so there is nothing to race over. This is the moment the stage pays off.
     *   <li><b>Replay.</b> {@code -Pfrom=0} on analytics and watch it reprocess history while the
     *       other three carry on undisturbed. In stage 2 this meant UPDATE-ing rows that three
     *       other consumers were actively reading. Here it is one number in one file that belongs
     *       to one consumer.
     *   <li><b>Commit before or after processing?</b> Look at {@link Consumer} — you choose. Commit
     *       first and a crash mid-batch loses records (<b>at-most-once</b>). Commit after and a
     *       crash reprocesses them (<b>at-least-once</b>). Kill the consumer mid-batch with
     *       {@code kill -9} and prove which one you built. There is no third option available to
     *       you, and that is not a gap in your code — it is the actual taxonomy
     *       ({@code delivery-guarantees.html}).
     *   <li><b>The O(n) read.</b> Every poll rescans the file from byte 0. Append 50,000 records
     *       and watch a consumer at offset 49,000 crawl. Real Kafka splits the log into
     *       <b>segments</b> and keeps a sparse <b>.index</b> mapping offset to byte position. You
     *       just derived the need for both.
     *   <li><b>Torn writes.</b> {@code kill -9} the producer mid-append, or truncate the file by a
     *       few bytes by hand. Your reader now dies on startup, forever, because {@code readInt()}
     *       returns a length that runs past the end of the file. Real logs survive this with a
     *       length prefix <em>plus a CRC</em>, and recover by truncating back to the last complete
     *       record. Try writing that recovery.
     *   <li><b>One file, one writer, one disk.</b> Throughput has a ceiling and you cannot spread
     *       it. Split the log into N files by {@code hash(key) % N} and you have invented
     *       <b>partitions</b> — and immediately lost total ordering, keeping it only
     *       <em>within</em> each file. That sentence from your notes finally has teeth.
     *       Then ask what happens when the disk dies, and you have invented <b>replication</b>.
     * </ol>
     */
    public static List<Record> readFrom(long fromOffset, int max) throws IOException {
        throw new UnsupportedOperationException("TODO(2): implement readFrom");
    }

    /** Total records in the log. Handy for demos and for `append` to compute its offset. */
    public static long size() throws IOException {
        return readFrom(0, Integer.MAX_VALUE).size();
    }
}
