package com.atlasgrid.geoops.project.validation;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class ParallelProjectValidationInterruptionTest {

    @Test
    void callerInterruptionIsPreservedAndCancelsWorker()
            throws Exception {
        ExecutorService executor =
                Executors.newSingleThreadExecutor();

        CountDownLatch workerStarted =
                new CountDownLatch(1);
        CountDownLatch workerInterrupted =
                new CountDownLatch(1);

        ProjectValidationRule blockingRule =
                new ProjectValidationRule() {
                    @Override
                    public String code() {
                        return "BLOCK_FOR_INTERRUPT_TEST";
                    }

                    @Override
                    public List<ValidationIssue> validate(
                            CreateProjectRequest request
                    ) {
                        workerStarted.countDown();

                        try {
                            Thread.sleep(30_000L);
                        } catch (InterruptedException exception) {
                            workerInterrupted.countDown();
                            Thread.currentThread().interrupt();
                        }

                        return List.of();
                    }
                };

        ParallelProjectValidationService service =
                new ParallelProjectValidationService(
                        new ProjectValidationService(
                                List.of(blockingRule)
                        ),
                        executor
                );

        AtomicBoolean callerInterruptPreserved =
                new AtomicBoolean();
        AtomicReference<Throwable> callerFailure =
                new AtomicReference<>();

        Thread caller = new Thread(() -> {
            try {
                service.validateAll(List.of(
                        new CreateProjectRequest(
                                "TX-AUS-690",
                                "Interrupt Test",
                                "EPSG:4326"
                        )
                ));
            } catch (IllegalStateException exception) {
                callerInterruptPreserved.set(
                        Thread.currentThread().isInterrupted()
                );
                callerFailure.set(exception);
            }
        }, "geoops-validation-caller-test");

        try {
            caller.start();

            assertThat(
                    workerStarted.await(
                            5,
                            TimeUnit.SECONDS
                    )
            ).isTrue();

            caller.interrupt();
            caller.join(5_000L);

            assertThat(caller.isAlive()).isFalse();
            assertThat(callerInterruptPreserved).isTrue();
            assertThat(callerFailure.get())
                    .isInstanceOf(IllegalStateException.class);

            assertThat(
                    workerInterrupted.await(
                            5,
                            TimeUnit.SECONDS
                    )
            ).isTrue();
        } finally {
            executor.shutdownNow();
        }
    }
}
