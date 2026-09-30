package com.atlasgrid.geoops.diagnostics.thread;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JvmThreadDiagnosticsServiceTest {

    @Autowired
    private JvmThreadDiagnosticsService diagnosticsService;

    @Autowired
    @Qualifier("projectValidationExecutor")
    private ExecutorService validationExecutor;

    @Test
    void seesActiveGeoOpsValidationWorkerInCurrentJvm()
            throws Exception {
        CountDownLatch workerStarted =
                new CountDownLatch(1);
        CountDownLatch release =
                new CountDownLatch(1);

        validationExecutor.submit(() -> {
            workerStarted.countDown();
            await(release);
        });

        try {
            assertThat(
                    workerStarted.await(
                            5,
                            TimeUnit.SECONDS
                    )
            ).isTrue();

            JvmThreadSnapshot snapshot =
                    diagnosticsService.capture();

            assertThat(snapshot.capturedAt()).isNotNull();
            assertThat(snapshot.liveThreadCount())
                    .isGreaterThan(0);
            assertThat(snapshot.peakThreadCount())
                    .isGreaterThanOrEqualTo(
                            snapshot.liveThreadCount()
                    );
            assertThat(snapshot.totalStartedThreadCount())
                    .isGreaterThanOrEqualTo(
                            snapshot.liveThreadCount()
                    );
            assertThat(snapshot.validationWorkerCount())
                    .isGreaterThanOrEqualTo(1);
            assertThat(snapshot.validationWorkerNames())
                    .allSatisfy(name ->
                            assertThat(name)
                                    .startsWith(
                                            "geoops-project-validation-"
                                    )
                    );
            assertThat(snapshot.deadlockedThreadCount())
                    .isGreaterThanOrEqualTo(0);
        } finally {
            release.countDown();
        }
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
