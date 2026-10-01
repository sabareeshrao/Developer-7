package com.atlasgrid.geoops.project.validation;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class ParallelValidationWrittenCodeTest {

    @Test
    void actualValidationCodeRunsInParallelAndKeepsInputOrder()
            throws Exception {
        CountDownLatch bothWorkersStarted = new CountDownLatch(2);
        Set<String> threadNames = ConcurrentHashMap.newKeySet();
        ProjectValidationRule rule = new ProjectValidationRule() {
            @Override
            public String code() {
                return "VERIFY_PARALLEL_CODE";
            }

            @Override
            public List<ValidationIssue> validate(
                    CreateProjectRequest request
            ) {
                threadNames.add(Thread.currentThread().getName());
                bothWorkersStarted.countDown();
                try {
                    if (!bothWorkersStarted.await(5, TimeUnit.SECONDS)) {
                        throw new IllegalStateException(
                                "Workers did not run concurrently"
                        );
                    }
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException(exception);
                }
                return List.of();
            }
        };

        ExecutorService workers = Executors.newFixedThreadPool(2);
        try {
            ParallelProjectValidationService service =
                    new ParallelProjectValidationService(
                            new ProjectValidationService(List.of(rule)),
                            workers
                    );
            List<ProjectBatchValidationResult> results =
                    service.validateAll(List.of(
                            new CreateProjectRequest(
                                    "TX-AUS-760", "First", "EPSG:4326"
                            ),
                            new CreateProjectRequest(
                                    "TX-AUS-761", "Second", "EPSG:4326"
                            )
                    ));

            assertThat(results)
                    .extracting(ProjectBatchValidationResult::projectCode)
                    .containsExactly("TX-AUS-760", "TX-AUS-761");
            assertThat(threadNames).hasSize(2);
        } finally {
            workers.shutdownNow();
        }
    }
}
