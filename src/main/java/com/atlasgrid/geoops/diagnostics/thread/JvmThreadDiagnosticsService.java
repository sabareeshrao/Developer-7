package com.atlasgrid.geoops.diagnostics.thread;

import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Captures lightweight thread information from the current GeoOps JVM.
 */
@Service
public class JvmThreadDiagnosticsService {

    private static final String VALIDATION_THREAD_PREFIX =
            "geoops-project-validation-";

    private final ThreadMXBean threadMXBean =
            ManagementFactory.getThreadMXBean();

    public JvmThreadSnapshot capture() {
        ThreadInfo[] infos = threadMXBean.getThreadInfo(
                threadMXBean.getAllThreadIds(),
                0
        );

        List<String> validationWorkers =
                Arrays.stream(infos)
                        .filter(Objects::nonNull)
                        .map(ThreadInfo::getThreadName)
                        .filter(name ->
                                name.startsWith(
                                        VALIDATION_THREAD_PREFIX
                                )
                        )
                        .sorted()
                        .toList();

        long[] deadlocked =
                threadMXBean.findDeadlockedThreads();

        return new JvmThreadSnapshot(
                Instant.now(),
                threadMXBean.getThreadCount(),
                threadMXBean.getDaemonThreadCount(),
                threadMXBean.getPeakThreadCount(),
                threadMXBean.getTotalStartedThreadCount(),
                deadlocked == null ? 0 : deadlocked.length,
                validationWorkers.size(),
                validationWorkers
        );
    }
}
