package kafkalab.stage2;

import kafkalab.common.Json;
import kafkalab.common.OrderPlaced;
import kafkalab.common.SimulatedService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * STAGE 2 — a worker that pulls work out of the table instead of having it pushed at it.
 *
 * <pre>
 *   ./gradlew :kafkalab:stage2-db-queue:runWorker -Pworker=payment
 * </pre>
 *
 * <p>This is the first <b>pull</b> consumer in the repo, and the first one with an
 * <b>interval</b>. Both of those words were meaningless in stages 0 and 1. The worker decides
 * when to look; the producer is long gone by then. That inversion is why a worker can be down for
 * an hour and still do the work afterwards — the thing stage 1 could not do at all.
 */
public final class Worker {

    /** One order id we have claimed, plus its row id so we can mark it later. */
    record Job(long rowId, OrderPlaced order) {}

    public static void main(String[] args) throws Exception {
        String name = args.length > 0 ? args[0] : "payment";
        long pollMs = args.length > 1 ? Long.parseLong(args[1]) : 200;

        SimulatedService svc = switch (name) {
            case "payment"   -> SimulatedService.payment();
            case "inventory" -> SimulatedService.inventory();
            case "email"     -> SimulatedService.email();
            case "analytics" -> SimulatedService.analytics();
            default -> throw new IllegalArgumentException("unknown worker: " + name);
        };

        Db.init();
        System.out.printf("stage2 worker '%s' polling every %dms%n", name, pollMs);

        while (true) {
            List<Job> batch = claim(name, 10);
            for (Job job : batch) {
                try {
                    svc.apply(job.order());
                    markDone(name, job.rowId());
                } catch (Exception e) {
                    System.out.printf("  [%s] FAILED row %d: %s%n", name, job.rowId(), e.getMessage());
                    // TODO(3): what should happen here? Leave it NEW and it retries forever
                    //          (including the poison ones). Mark it DONE and you've dropped it.
                    //          Add an attempts column and you're building a dead-letter queue by
                    //          hand. There is no right answer available to you at this stage —
                    //          write down which one you picked and what it costs.
                }
            }
            if (batch.isEmpty()) {
                Thread.sleep(pollMs);
            }
        }
    }

    /**
     * Claim up to {@code limit} un-processed rows for this worker.
     *
     * and build a {@link Job} per row with {@code Json.read(payload, OrderPlaced.class)}.
     *
     * <p>Start with exactly that query and <b>run only ONE worker</b>. It will work, and the event
     * will survive the worker being killed — which is genuine, real progress over stage 1. Sit
     * with that for a moment before breaking it.
     *
     * <p>Then climb:
     *
     * <ol>
     *   <li><b>Start all four workers.</b> Count the applications. With 12 orders you want 48
     *       (4 workers x 12). You will get something arbitrary — measured runs gave 39 and 36,
     *       with payment applying only 8 of 12 on one of them. <b>This is the wall</b>, and it is
     *       worse than "each order goes to exactly one consumer": it is <em>non-deterministic
     *       partial delivery</em>. Some customers are never charged, and which ones is decided by
     *       thread scheduling.
     *       <p>The cause is a race window. A row stays visible to every worker for as long as it
     *       is {@code 'NEW'} — and that is the <em>entire processing duration</em>, the gap
     *       between your {@code SELECT} and your {@code markDone}. With 20-200ms of work and a
     *       200ms poll, several workers grab the same row before anyone marks it done.
     *       <p>Two separate things are broken here, and it pays to name them apart: (a) one
     *       shared {@code status} column cannot express "payment is done but email is not", and
     *       (b) claiming is not atomic. Fixing only (b) gets you a <em>correct queue</em> — each
     *       order delivered once, to one consumer. But you never wanted a queue. You want all four
     *       to see everything, and no amount of locking will give you that.
     *   <li><b>Add per-consumer columns</b> — {@code payment_done}, {@code inventory_done}, … and
     *       make each worker filter on its own. It works. Now count what shipping a fifth consumer
     *       costs: an {@code ALTER TABLE} on the shared table, coordinated with a deploy. You have
     *       reinvented stage 1's coupling, in DDL.
     *   <li><b>Run two payment workers at once</b> (scale one consumer horizontally). Both
     *       {@code SELECT} the same rows before either {@code UPDATE}s. Watch the console for
     *       {@code DUPLICATE} — you just charged a card twice. Fix it with an atomic claim
     *       ({@code UPDATE … SET locked_by=? WHERE id IN (…) AND locked_by IS NULL}), and notice
     *       you are now writing a lease protocol, and that a worker which dies mid-job holds its
     *       lease until it expires.
     *   <li><b>Tune the interval.</b> Set {@code -Ppoll=10} and watch the DB take a hammering for
     *       an empty result set almost every time. Set {@code -Ppoll=2000} and you've bought a
     *       two-second latency floor. There is no value that is both cheap and fast. Remember this
     *       when you meet {@code consumer.poll()} in stage 4 and it turns out to be a *long* poll.
     *   <li><b>Ask for a replay.</b> Analytics wants to reprocess the last hour after a bug fix.
     *       With per-consumer columns you must {@code UPDATE analytics_done = 0} across a range —
     *       mutating shared state that three other consumers are actively reading. And if you'd
     *       deleted rows on completion to keep the table small, replay is simply impossible.
     * </ol>
     *
     * <p><b>The exit realization:</b> every problem above comes from storing <em>per-row, per-
     * consumer state</em> in the shared thing. Flip it around — let the row be immutable, and let
     * each consumer keep a single number saying how far it has read — and all five problems
     * dissolve at once. That number is an <b>offset</b>, and the immutable ordered rows are a
     * <b>log</b>. That's stage 3, and you'll be writing it yourself.
     */
    static List<Job> claim(String worker, int limit) throws Exception {
        String sql = "SELECT id, payload FROM outbox WHERE status = 'NEW' ORDER BY id LIMIT ?";
        List<Job> jobs = new ArrayList<>();
        try (Connection c = Db.open();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                     long rowId = rs.getLong(1);
                     String payload = rs.getString(2);
                     OrderPlaced order = Json.read(payload, OrderPlaced.class);
                     jobs.add(new Job(rowId, order));
                }
            }
        }
        return jobs;
    }

    /**
     * Mark the row as processed by this worker.
     *
     * 2 onward the signature's {@code worker} argument starts to matter — that's the hint.
     */
    static void markDone(String worker, long rowId) throws Exception {
        String sql = "UPDATE outbox SET status='DONE' WHERE id=?";
        try (Connection c = Db.open(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, rowId);
            ps.executeUpdate();
        }
    }
}
