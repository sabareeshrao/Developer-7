package com.atlasgrid.geoops.diagnostics.memory;

import java.time.Instant;

/**
 * Point-in-time JVM memory evidence for leak investigations.
 */
public record JvmMemorySnapshot(
        Instant capturedAt,
        long heapUsedBytes,
        long heapCommittedBytes,
        long heapMaxBytes,
        long nonHeapUsedBytes,
        long nonHeapCommittedBytes,
        int pendingFinalizationCount
) {
}
