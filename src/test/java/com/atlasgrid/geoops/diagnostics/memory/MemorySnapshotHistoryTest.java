package com.atlasgrid.geoops.diagnostics.memory;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class MemorySnapshotHistoryTest {

    @Test
    void retainsOnlyTheNewestBoundedWindow() {
        MemorySnapshotHistory history =
                new MemorySnapshotHistory();

        for (int index = 0; index < 1_000; index++) {
            history.record(sample(index));
        }

        assertThat(history.size())
                .isEqualTo(MemorySnapshotHistory.CAPACITY);
        assertThat(history.snapshot())
                .extracting(JvmMemorySnapshot::heapUsedBytes)
                .first()
                .isEqualTo(880L);
        assertThat(history.snapshot())
                .extracting(JvmMemorySnapshot::heapUsedBytes)
                .last()
                .isEqualTo(999L);
    }

    private JvmMemorySnapshot sample(long value) {
        return new JvmMemorySnapshot(
                Instant.ofEpochMilli(value),
                value,
                1_000L,
                2_000L,
                value,
                1_000L,
                0
        );
    }
}
