package com.atlasgrid.geoops.diagnostics.memory;

import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * Bounded in-memory history for lightweight JVM memory samples.
 *
 * <p>The fixed capacity prevents the diagnostic feature itself from retaining
 * every sample forever.</p>
 */
@Component
public class MemorySnapshotHistory {

    static final int CAPACITY = 120;

    private final Deque<JvmMemorySnapshot> snapshots =
            new ArrayDeque<>();

    public synchronized void record(JvmMemorySnapshot snapshot) {
        Objects.requireNonNull(snapshot, "snapshot");

        if (snapshots.size() == CAPACITY) {
            snapshots.removeFirst();
        }

        snapshots.addLast(snapshot);
    }

    public synchronized List<JvmMemorySnapshot> snapshot() {
        return List.copyOf(snapshots);
    }

    public synchronized int size() {
        return snapshots.size();
    }
}
