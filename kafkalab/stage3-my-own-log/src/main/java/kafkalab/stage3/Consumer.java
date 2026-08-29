package kafkalab.stage3;

import kafkalab.common.Json;
import kafkalab.common.OrderPlaced;
import kafkalab.common.SimulatedService;

import java.util.List;

/**
 * STAGE 3 — a consumer group reading forward from its own offset.
 *
 * <pre>
 *   ./gradlew :kafkalab:stage3-my-own-log:runConsumer -Pgroup=payment
 *   ./gradlew :kafkalab:stage3-my-own-log:runConsumer -Pgroup=analytics -Pfrom=0    # replay
 * </pre>
 *
 * <p>Compare this loop to stage 2's {@code Worker}. That one had to <em>claim</em> rows, worry
 * about other workers, and write state back into a shared table. This one reads forward from a
 * number that belongs to it alone and tells nobody. There is no claim, no lock, no lease, and no
 * contention — because <b>reading does not consume</b>.
 */
public final class Consumer {

    public static void main(String[] args) throws Exception {
        // Autoflush. Java block-buffers System.out when it is redirected to a file, so a consumer
        // killed with Ctrl-C or SIGTERM loses everything it "printed". That cost me a debugging
        // session on demo.sh — the consumers were working fine and their logs were empty.
        System.setOut(new java.io.PrintStream(
                new java.io.FileOutputStream(java.io.FileDescriptor.out), true));

        String group = args.length > 0 ? args[0] : "payment";
        long from = args.length > 1 ? Long.parseLong(args[1]) : -1;
        long pollMs = args.length > 2 ? Long.parseLong(args[2]) : 200;

        SimulatedService svc = switch (group) {
            case "payment"   -> SimulatedService.payment();
            case "inventory" -> SimulatedService.inventory();
            case "email"     -> SimulatedService.email();
            case "analytics" -> SimulatedService.analytics();
            default -> throw new IllegalArgumentException("unknown group: " + group);
        };

        // -Pfrom=0 rewinds this group to the start. One line, one file, nobody else affected.
        // That is the whole of "replay", and it was impossible in stages 0, 1 and 2.
        if (from >= 0) {
            Offsets.seek(group, from);
            System.out.printf("group '%s' rewound to offset %d%n", group, from);
        }

        Log.file();
        System.out.printf("stage3 consumer '%s' starting at offset %d, polling every %dms%n",
                group, Offsets.position(group), pollMs);

        while (true) {
            long position = Offsets.position(group);
            List<Log.Record> batch = Log.readFrom(position, 10);

            if (batch.isEmpty()) {
                Thread.sleep(pollMs);
                continue;
            }

            // We advance the offset only past records we have actually applied. Crash mid-batch
            // and the uncommitted records are read again on restart — you'll see DUPLICATE in the
            // console. That is at-least-once, and it is the deliberate choice here.
            //
            // On failure we STOP the batch without advancing past the failed record, so it is
            // retried on the next poll. That buys retry (impossible in stage 1) and costs
            // head-of-line blocking: one permanently-failing record halts this group forever
            // while the other three sail past. Real systems escape with a dead-letter topic.
            long lastHandled = -1;
            for (Log.Record rec : batch) {
                try {
                    OrderPlaced order = Json.read(rec.value(), OrderPlaced.class);
                    svc.apply(order);
                    lastHandled = rec.offset();
                } catch (Exception e) {
                    System.out.printf("  [%-9s] FAILED at offset %d: %s — will retry%n",
                            group, rec.offset(), e.getMessage());
                    break;
                }
            }

            if (lastHandled >= 0) {
                // "next offset to read", hence +1. Move this line ABOVE the loop and you have
                // built at-most-once instead: a crash would skip the batch entirely.
                Offsets.commit(group, lastHandled + 1);
            } else {
                Thread.sleep(pollMs);   // nothing succeeded; back off before retrying
            }

            // For each record: Json.read(rec.value(), OrderPlaced.class), then svc.apply(order).
            // Then commit with Offsets.commit(group, <the offset AFTER the last one you handled>)
            // — note it is "next offset to read", so it is lastHandled + 1.
            //
            // THE DECISION. You can commit before you process the batch, or after. Pick one, then
            // prove which you built by killing the consumer mid-batch with `kill -9`:
            //
            //   commit BEFORE processing -> a crash loses that batch.        AT-MOST-ONCE
            //   commit AFTER  processing -> a crash reprocesses that batch.  AT-LEAST-ONCE
            //                               (watch for DUPLICATE in the console)
            //
            // There is no arrangement of these two lines that gives you exactly-once, and that is
            // not a limitation of your code. Exactly-once needs the *processing* and the *offset
            // commit* to be one atomic operation — which means they must share a transaction, and
            // yours are in a file and a service that know nothing about each other. Stage 7 shows
            // how Kafka does it and exactly where the guarantee stops.
            //
            // Also decide: what happens when svc.apply throws? (email fails 25% of the time.)
            // Skip it and commit past it, or retry forever and block the group? Stage 2 asked you
            // the same question and it has the same non-answer. Real systems reach for a
            // dead-letter topic here.
        }
    }
}
