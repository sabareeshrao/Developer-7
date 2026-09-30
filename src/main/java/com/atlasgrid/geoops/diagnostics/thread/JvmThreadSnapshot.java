package com.atlasgrid.geoops.diagnostics.thread;

import java.time.Instant;
import java.util.List;

/**
 * Point-in-time JVM thread evidence for the GeoOps process.
 */
public record JvmThreadSnapshot(
        Instant capturedAt,
        int liveThreadCount,
        int daemonThreadCount,
        int peakThreadCount,
        long totalStartedThreadCount,
        int deadlockedThreadCount,
        int validationWorkerCount,
        List<String> validationWorkerNames
) {
    public JvmThreadSnapshot {
        validationWorkerNames =
                List.copyOf(validationWorkerNames);
    }
}
