package com.atlasgrid.geoops.project.validation;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class ParallelProjectValidationServiceConcurrencyTest {

    private final ExecutorService executor =
            Executors.newFixedThreadPool(4);

    @AfterEach
    void shutdownExecutor() {
        executor.shutdownNow();
    }

    @Test
    void independentRequestsActuallyRunOnMultipleWorkerThreads()
            throws Exception {
        Set<String> workerThreads =
                ConcurrentHashMap.newKeySet();
        CountDownLatch fourWorkersEntered =
                new CountDownLatch(4);
        CountDownLatch releaseWorkers =
                new CountDownLatch(1);

        ProjectValidationRule observingRule = request -> {
            workerThreads.add(Thread.currentThread().getName());
            fourWorkersEntered.countDown();
            await(releaseWorkers);
            return List.of();
        };

        ParallelProjectValidationService service =
                new ParallelProjectValidationService(
                        new ProjectValidationService(
                                List.of(observingRule)
                        ),
                        executor
                );

        List<CreateProjectRequest> requests =
                java.util.stream.IntStream.range(0, 8)
                        .mapToObj(index ->
                                new CreateProjectRequest(
                                        "TX-AUS-%03d".formatted(index),
                                        "Concurrent Validation " + index,
                                        "EPSG:4326"
                                )
                        )
                        .toList();

        java.util.concurrent.CompletableFuture<
                List<ProjectBatchValidationResult>
                > resultFuture =
                java.util.concurrent.CompletableFuture.supplyAsync(
                        () -> service.validateAll(requests)
                );

        assertThat(
                fourWorkersEntered.await(
                        5,
                        TimeUnit.SECONDS
                )
        ).isTrue();

        releaseWorkers.countDown();

        assertThat(resultFuture.get(5, TimeUnit.SECONDS))
                .hasSize(8)
                .allMatch(ProjectBatchValidationResult::valid);
        assertThat(workerThreads).hasSizeGreaterThan(1);
    }

    private void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(exception);
        }
    }
}
