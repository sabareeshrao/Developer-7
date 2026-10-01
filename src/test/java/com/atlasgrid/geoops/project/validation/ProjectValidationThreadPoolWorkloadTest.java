package com.atlasgrid.geoops.project.validation;

import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectValidationThreadPoolWorkloadTest {

    @Test
    void boundedPoolRunsSaturatedWorkInCallerAndDrainsCleanly()
            throws Exception {
        ThreadPoolExecutor pool =
                new ProjectValidationExecutorConfiguration()
                        .projectValidationExecutor();
        CountDownLatch workersStarted = new CountDownLatch(4);
        CountDownLatch release = new CountDownLatch(1);
        try {
            for (int i = 0; i < 4; i++) {
                pool.execute(() -> {
                    workersStarted.countDown();
                    try {
                        release.await();
                    } catch (InterruptedException exception) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
            assertThat(workersStarted.await(5, TimeUnit.SECONDS))
                    .isTrue();

            for (int i = 0; i < 64; i++) {
                pool.execute(() -> { });
            }
            ProjectValidationExecutorSnapshot before =
                    new ProjectValidationExecutorMonitor(pool).capture();
            assertThat(before.activeCount()).isEqualTo(4);
            assertThat(before.queuedTaskCount()).isEqualTo(64);
            assertThat(before.remainingQueueCapacity()).isZero();

            AtomicBoolean ranInCaller = new AtomicBoolean();
            Thread caller = Thread.currentThread();
            pool.execute(() -> ranInCaller.set(
                    Thread.currentThread() == caller
            ));
            assertThat(ranInCaller).isTrue();
            assertThat(pool.getLargestPoolSize()).isEqualTo(4);
        } finally {
            release.countDown();
            pool.shutdown();
            assertThat(pool.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
        }
    }
}
