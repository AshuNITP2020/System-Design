package kafkalab.common;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A stand-in for a real downstream service: costs time, sometimes fails, and — crucially —
 * <b>remembers every order id it has applied and how many times.</b>
 *
 * <p>That ledger is your instrument. "Did the event get lost?" and "did it get processed twice?"
 * are the two questions the next four stages keep asking, and {@link #timesApplied} answers both.
 */
public final class SimulatedService implements SideEffect {

    private final String name;
    private final long latencyMs;
    private final double failureRate;

    private final Map<String, AtomicLong> applied = new ConcurrentHashMap<>();
    private final AtomicLong failures = new AtomicLong();

    private SimulatedService(String name, long latencyMs, double failureRate) {
        this.name = name;
        this.latencyMs = latencyMs;
        this.failureRate = failureRate;
    }

    /** @param failureRate 0.0 = never fails, 0.3 = fails ~30% of calls */
    public static SimulatedService of(String name, long latencyMs, double failureRate) {
        return new SimulatedService(name, latencyMs, failureRate);
    }

    /** The four consumers this repo uses everywhere. Same costs in every stage. */
    public static SimulatedService payment()   { return of("payment",    120, 0.00); }
    public static SimulatedService inventory() { return of("inventory",   80, 0.00); }
    public static SimulatedService email()     { return of("email",      200, 0.25); }
    public static SimulatedService analytics() { return of("analytics",   20, 0.00); }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void apply(OrderPlaced order) throws Exception {
        Thread.sleep(latencyMs);
        if (ThreadLocalRandom.current().nextDouble() < failureRate) {
            failures.incrementAndGet();
            throw new RuntimeException(name + " is having a bad day (simulated failure)");
        }
        long n = applied.computeIfAbsent(order.orderId(), k -> new AtomicLong()).incrementAndGet();
        System.out.printf("  [%-9s] applied %s%s%n", name, order.orderId(),
                n > 1 ? "  <-- DUPLICATE, applied " + n + " times!" : "");
    }

    // --- the instrument ---

    /** 0 = the event never arrived. 2+ = it arrived more than once. */
    public long timesApplied(String orderId) {
        var c = applied.get(orderId);
        return c == null ? 0 : c.get();
    }

    public Set<String> seenOrderIds() {
        return applied.keySet();
    }

    public long distinctApplied() {
        return applied.size();
    }

    public long totalApplied() {
        return applied.values().stream().mapToLong(AtomicLong::get).sum();
    }

    public long failureCount() {
        return failures.get();
    }

    /** Print lost / duplicated counts against the set of orders you actually produced. */
    public void report(Set<String> produced) {
        long lost = produced.stream().filter(id -> timesApplied(id) == 0).count();
        long dupes = produced.stream().filter(id -> timesApplied(id) > 1).count();
        System.out.printf("%-10s produced=%d  applied=%d  lost=%d  duplicated=%d  failures=%d%n",
                name, produced.size(), distinctApplied(), lost, dupes, failureCount());
    }
}
