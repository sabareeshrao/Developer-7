package com.atlasgrid.geoops.diagnostics.memory;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JvmMemoryDiagnosticsServiceTest {

    private final JvmMemoryDiagnosticsService diagnostics =
            new JvmMemoryDiagnosticsService();

    @Test
    void capturesSaneJvmMemoryEvidence() {
        JvmMemorySnapshot snapshot = diagnostics.capture();

        assertThat(snapshot.capturedAt()).isNotNull();
        assertThat(snapshot.heapUsedBytes()).isGreaterThanOrEqualTo(0L);
        assertThat(snapshot.heapCommittedBytes())
                .isGreaterThanOrEqualTo(snapshot.heapUsedBytes());

        if (snapshot.heapMaxBytes() >= 0L) {
            assertThat(snapshot.heapMaxBytes())
                    .isGreaterThanOrEqualTo(
                            snapshot.heapCommittedBytes()
                    );
        }

        assertThat(snapshot.nonHeapUsedBytes())
                .isGreaterThanOrEqualTo(0L);
        assertThat(snapshot.nonHeapCommittedBytes())
                .isGreaterThanOrEqualTo(
                        snapshot.nonHeapUsedBytes()
                );
        assertThat(snapshot.pendingFinalizationCount())
                .isGreaterThanOrEqualTo(0);
    }
}
