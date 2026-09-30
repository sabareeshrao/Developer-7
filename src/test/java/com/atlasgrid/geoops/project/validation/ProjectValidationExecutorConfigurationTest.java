package com.atlasgrid.geoops.project.validation;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectValidationExecutorConfigurationTest {

    @Test
    void configuresFixedWorkersBoundedQueueAndCallerBackpressure()
            throws Exception {
        ExecutorService service =
                new ProjectValidationExecutorConfiguration()
                        .projectValidationExecutor();

        ThreadPoolExecutor executor =
                (ThreadPoolExecutor) service;

        try {
            assertThat(executor.getCorePoolSize())
                    .isEqualTo(
                            ProjectValidationExecutorConfiguration
                                    .VALIDATION_THREADS
                    );
            assertThat(executor.getMaximumPoolSize())
                    .isEqualTo(
                            ProjectValidationExecutorConfiguration
                                    .VALIDATION_THREADS
                    );
            assertThat(executor.getQueue().remainingCapacity())
                    .isEqualTo(
                            ProjectValidationExecutorConfiguration
                                    .VALIDATION_QUEUE_CAPACITY
                    );
            assertThat(executor.getRejectedExecutionHandler())
                    .isInstanceOf(
                            ThreadPoolExecutor.CallerRunsPolicy.class
                    );

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

            assertThat(
                    workersStarted.await(
                            5,
                            TimeUnit.SECONDS
                    )
            ).isTrue();

            for (int index = 0;
                 index
                         < ProjectValidationExecutorConfiguration
                                 .VALIDATION_QUEUE_CAPACITY;
                 index++) {
                executor.execute(() -> await(release));
            }

            String callerThread =
                    Thread.currentThread().getName();
            AtomicReference<String> executionThread =
                    new AtomicReference<>();

            executor.execute(() ->
                    executionThread.set(
                            Thread.currentThread().getName()
                    )
            );

            assertThat(executionThread)
                    .hasValue(callerThread);

            release.countDown();
        } finally {
            executor.shutdownNow();
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
