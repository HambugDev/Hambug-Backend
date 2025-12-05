package com.hambug.Hambug.global.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Periodically samples JVM CPU usage and, when high, logs top CPU-consuming threads with stack traces.
 * This is intended to help diagnose unexpectedly high CPU usage in production with minimal overhead.
 */
@Component
@Slf4j
public class HighCpuDiagnosticsScheduler {

    // Threshold for logging diagnostics (e.g., 0.80 = 80% of a single core for this JVM process)
    private static final double CPU_LOAD_THRESHOLD = 0.80;

    // Number of top threads to report
    private static final int TOP_N_THREADS = 7;

    // State across runs to compute deltas
    private final Map<Long, Long> lastThreadCpuNs = new HashMap<>();
    private long lastSampleWallTimeNs = System.nanoTime();

    private static com.sun.management.OperatingSystemMXBean getOsBean() {
        try {
            return (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        } catch (Throwable t) {
            return null;
        }
    }

    @Scheduled(fixedDelay = 30000) // every 30 seconds
    public void logCpuDiagnosticsIfHigh() {
        com.sun.management.OperatingSystemMXBean osBean = getOsBean();
        if (osBean == null) {
            return; // Not supported on this JVM
        }

        double processCpu = osBean.getProcessCpuLoad(); // 0.0 ~ 1.0, or < 0 if not available
        if (processCpu < 0) {
            return; // metric not available, avoid heavy work
        }

        if (processCpu < CPU_LOAD_THRESHOLD) {
            // Below threshold: still update baseline so deltas remain meaningful
            updateBaselineOnly();
            return;
        }

        // When high, compute per-thread deltas and log details
        ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
        if (threadMxBean.isThreadCpuTimeSupported() && !threadMxBean.isThreadCpuTimeEnabled()) {
            try {
                threadMxBean.setThreadCpuTimeEnabled(true);
            } catch (SecurityException ignored) {
            }
        }

        long nowNs = System.nanoTime();
        long intervalNs = Math.max(1, nowNs - lastSampleWallTimeNs);

        // Gather thread CPU deltas
        long[] threadIds = threadMxBean.getAllThreadIds();
        List<ThreadUsage> usages = new ArrayList<>();
        for (long tid : threadIds) {
            long cpuNs = -1;
            try {
                cpuNs = threadMxBean.getThreadCpuTime(tid);
            } catch (UnsupportedOperationException ignored) {
            }
            if (cpuNs < 0) continue; // not available for this thread

            long prev = lastThreadCpuNs.getOrDefault(tid, 0L);
            long delta = cpuNs - prev;
            if (delta <= 0) continue;

            ThreadInfo info = threadMxBean.getThreadInfo(tid, 30);
            String name = (info != null) ? info.getThreadName() : ("tid-" + tid);
            Thread.State state = (info != null) ? info.getThreadState() : Thread.State.RUNNABLE;
            String topFrame = "<no stack>";
            if (info != null && info.getStackTrace() != null && info.getStackTrace().length > 0) {
                topFrame = info.getStackTrace()[0].toString();
            }

            usages.add(new ThreadUsage(tid, name, state, delta, topFrame));
        }

        // Sort by CPU delta desc
        usages.sort(Comparator.comparingLong((ThreadUsage u) -> u.cpuDeltaNs).reversed());

        // Prepare log
        StringBuilder sb = new StringBuilder();
        sb.append("[High CPU DETECTED] ")
          .append(LocalDateTime.now())
          .append(" processCpu=")
          .append(String.format("%.1f%%", processCpu * 100))
          .append(", interval=")
          .append(TimeUnit.NANOSECONDS.toMillis(intervalNs)).append(" ms\n");

        int limit = Math.min(TOP_N_THREADS, usages.size());
        for (int i = 0; i < limit; i++) {
            ThreadUsage u = usages.get(i);
            double pctOfCore = (double) u.cpuDeltaNs / intervalNs * 100.0; // percentage of a single core during interval
            sb.append(String.format(" #%d tid=%d name=\"%s\" state=%s cpu=%.1f%% (%d ms) top=%s\n",
                    i + 1,
                    u.threadId,
                    u.name,
                    u.state,
                    pctOfCore,
                    TimeUnit.NANOSECONDS.toMillis(u.cpuDeltaNs),
                    u.topFrame));
        }

        log.warn(sb.toString());

        // Update baseline after logging
        updateBaseline(threadIds, threadMxBean);
        lastSampleWallTimeNs = nowNs;
    }

    private void updateBaselineOnly() {
        ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
        long[] ids = threadMxBean.getAllThreadIds();
        updateBaseline(ids, threadMxBean);
        lastSampleWallTimeNs = System.nanoTime();
    }

    private void updateBaseline(long[] threadIds, ThreadMXBean threadMxBean) {
        for (long tid : threadIds) {
            long cpuNs = -1;
            try {
                cpuNs = threadMxBean.getThreadCpuTime(tid);
            } catch (UnsupportedOperationException ignored) {
            }
            if (cpuNs >= 0) {
                lastThreadCpuNs.put(tid, cpuNs);
            }
        }
        // Clean up entries for dead threads to avoid unbounded growth
        lastThreadCpuNs.keySet().removeIf(id -> !contains(threadIds, id));
    }

    private boolean contains(long[] arr, long v) {
        for (long x : arr) if (x == v) return true;
        return false;
    }

    private record ThreadUsage(long threadId, String name, Thread.State state, long cpuDeltaNs, String topFrame) {}
}
