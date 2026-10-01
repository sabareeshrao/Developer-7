package com.atlasgrid.geoops.project.validation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProjectValidationExecutorMonitorTest {

    @Autowired
    private ProjectValidationExecutorMonitor monitor;

    @Autowired
    @Qualifier("projectValidationExecutor")
    private ThreadPoolExecutor executor;

    @Test
    void capturesActiveWorkersAndQueuedTasks() throws Exception {
        CountDownLatch workersStarted =
                new CountDownLatch(
                        ProjectValidationExecutorConfiguration
                                .VALIDATION_THREADS
                );
        CountDownLatch release =
                new CountDownLatch(1);

        for (int index = 0;
             index
                     < ProjectValidationExecutorConfiguration
                             .VALIDATION_THREADS;
             index++) {
            executor.execute(() -> {
                workersStarted.countDown();
                await(release);
            });
        }

        try {
            assertThat(
                    workersStarted.await(
                            5,
                            TimeUnit.SECONDS
                    )
            ).isTrue();

            executor.execute(() -> await(release));

            ProjectValidationExecutorSnapshot snapshot =
                    monitor.capture();

            assertThat(snapshot.poolSize())
                    .isEqualTo(
                            ProjectValidationExecutorConfiguration
                                    .VALIDATION_THREADS
                    );
            assertThat(snapshot.activeCount())
                    .isEqualTo(
                            ProjectValidationExecutorConfiguration
                                    .VALIDATION_THREADS
                    );
            assertThat(snapshot.queuedTaskCount())
                    .isGreaterThanOrEqualTo(1);
            assertThat(
                    snapshot.queuedTaskCount()
                            + snapshot.remainingQueueCapacity()
            ).isEqualTo(
                    ProjectValidationExecutorConfiguration
                            .VALIDATION_QUEUE_CAPACITY
            );
            assertThat(snapshot.submittedTaskCount())
                    .isGreaterThanOrEqualTo(
                            snapshot.completedTaskCount()
                    );
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
