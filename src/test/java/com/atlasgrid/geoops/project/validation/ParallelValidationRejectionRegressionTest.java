package com.atlasgrid.geoops.project.validation;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ParallelValidationRejectionRegressionTest {

    @Test
    void closedGeoOpsPoolRejectsInsteadOfSilentlyDroppingTasks() {
        ThreadPoolExecutor executor =
                new ProjectValidationExecutorConfiguration()
                        .projectValidationExecutor();
        executor.shutdown();

        assertThatThrownBy(() -> executor.submit(() -> "never"))
                .isInstanceOf(RejectedExecutionException.class);
    }

    @Test
    void rejectedLaterSubmissionCancelsPreviouslyQueuedWork()
            throws Exception {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1, 1, 0, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1),
                new ThreadPoolExecutor.AbortPolicy()
        );
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);

        try {
            executor.submit(() -> {
                started.countDown();
                try {
                    release.await();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                }
            });
            assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();

            ParallelProjectValidationService service =
                    new ParallelProjectValidationService(
                            new ProjectValidationService(List.of()),
                            executor
                    );

            assertThatThrownBy(() -> service.validateAll(List.of(
                    new CreateProjectRequest(
                            "TX-AUS-780", "Queued", "EPSG:4326"
                    ),
                    new CreateProjectRequest(
                            "TX-AUS-781", "Rejected", "EPSG:4326"
                    )
            )))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("rejected");

            Future<?> queued = (Future<?>) executor.getQueue().peek();
            assertThat(queued).isNotNull();
            assertThat(queued.isCancelled()).isTrue();
        } finally {
            release.countDown();
            executor.shutdownNow();
        }
    }
}
