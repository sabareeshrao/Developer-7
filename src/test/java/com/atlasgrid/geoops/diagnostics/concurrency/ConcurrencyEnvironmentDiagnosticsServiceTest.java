package com.atlasgrid.geoops.diagnostics.concurrency;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ConcurrencyEnvironmentDiagnosticsServiceTest {

    @Autowired
    private ConcurrencyEnvironmentDiagnosticsService diagnostics;

    @Test
    void capturesJvmAndValidationExecutorConcurrencyState() {
        ConcurrencyEnvironmentSnapshot snapshot =
                diagnostics.capture();

        assertThat(snapshot.jvmThreads()).isNotNull();
        assertThat(snapshot.validationExecutor()).isNotNull();
        assertThat(snapshot.jvmThreads().liveThreadCount())
                .isGreaterThan(0);
        assertThat(snapshot.validationExecutor().poolSize())
                .isGreaterThanOrEqualTo(0);
        assertThat(snapshot.validationExecutor().queuedTaskCount())
                .isGreaterThanOrEqualTo(0);
    }
}
