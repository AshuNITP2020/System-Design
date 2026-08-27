package kafkalab.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Latency recorder. Stage 0's whole lesson is a number: what the caller waits for when four
 * side effects run inline. Measure it, don't guess it.
 */
public final class Metrics {

    private final String label;
    private final List<Long> samplesMs = Collections.synchronizedList(new ArrayList<>());

    public Metrics(String label) {
        this.label = label;
    }

    /** Time a block and record how long it took. */
    public <T> T time(java.util.concurrent.Callable<T> body) throws Exception {
        long t0 = System.nanoTime();
        try {
            return body.call();
        } finally {
            samplesMs.add((System.nanoTime() - t0) / 1_000_000);
        }
    }

    public void record(long ms) {
        samplesMs.add(ms);
    }

    public void print() {
        List<Long> s;
        synchronized (samplesMs) {
            s = new ArrayList<>(samplesMs);
        }
        if (s.isEmpty()) {
            System.out.println(label + ": no samples");
            return;
        }
        Collections.sort(s);
        System.out.printf("%s: n=%d  p50=%dms  p95=%dms  p99=%dms  max=%dms%n",
                label, s.size(), pct(s, 50), pct(s, 95), pct(s, 99), s.get(s.size() - 1));
    }

    private static long pct(List<Long> sorted, int p) {
        int idx = (int) Math.ceil(p / 100.0 * sorted.size()) - 1;
        return sorted.get(Math.max(0, Math.min(idx, sorted.size() - 1)));
    }
}
