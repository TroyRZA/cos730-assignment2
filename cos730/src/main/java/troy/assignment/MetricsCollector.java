package troy.assignment;

import java.util.ArrayList;
import java.util.List;

public class MetricsCollector {

    private static final MetricsCollector INSTANCE = new MetricsCollector();

    private final List<Long> runTimes = new ArrayList<>();

    private MetricsCollector() {}

    public static MetricsCollector getInstance() {
        return INSTANCE;
    }

    public void recordRun(long nanoseconds) {
        runTimes.add(nanoseconds);
        int run = runTimes.size();
        System.out.printf("%n[Metrics] Run #%d: %.3f ms%n", run, nanoseconds / 1_000_000.0);

        if (run > 1) {
            long min = runTimes.stream().mapToLong(Long::longValue).min().orElse(0);
            long max = runTimes.stream().mapToLong(Long::longValue).max().orElse(0);
            double avg = runTimes.stream().mapToLong(Long::longValue).average().orElse(0);
            System.out.printf("[Metrics] %d runs — min: %.3f ms | max: %.3f ms | avg: %.3f ms%n",
                    run, min / 1_000_000.0, max / 1_000_000.0, avg / 1_000_000.0);
        }
    }

    public void reset() {
        runTimes.clear();
    }
}
