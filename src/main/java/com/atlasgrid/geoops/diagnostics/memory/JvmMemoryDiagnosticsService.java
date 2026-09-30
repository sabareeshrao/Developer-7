package com.atlasgrid.geoops.diagnostics.memory;

import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.time.Instant;

/**
 * Captures lightweight JVM memory evidence without forcing GC or taking a
 * heap dump.
 */
@Service
public class JvmMemoryDiagnosticsService {

    private final MemoryMXBean memoryMXBean =
            ManagementFactory.getMemoryMXBean();
    private final MemorySnapshotHistory history;

    public JvmMemoryDiagnosticsService(
            MemorySnapshotHistory history
    ) {
        this.history = history;
    }

    public JvmMemorySnapshot capture() {
        MemoryUsage heap = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeap = memoryMXBean.getNonHeapMemoryUsage();

        JvmMemorySnapshot snapshot = new JvmMemorySnapshot(
                Instant.now(),
                heap.getUsed(),
                heap.getCommitted(),
                heap.getMax(),
                nonHeap.getUsed(),
                nonHeap.getCommitted(),
                memoryMXBean.getObjectPendingFinalizationCount()
        );

        history.record(snapshot);
        return snapshot;
    }
}
