package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import com.atlasgrid.geoops.project.validation.ParallelProjectValidationService;
import com.atlasgrid.geoops.review.ProjectReviewQueue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ConcurrentGeoOpsWorkflowIntegrationTest {

    @Autowired
    private ProjectService projects;

    @Autowired
    private ParallelProjectValidationService preflight;

    @Autowired
    private ProjectReviewQueue reviews;

    @Test
    void independentPreflightAndConcurrentPublicationRemainIsolated()
            throws Exception {
        ExecutorService callers = Executors.newFixedThreadPool(12);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> results = new ArrayList<>();
        try {
            for (int index = 0; index < 20; index++) {
                int task = index;
                results.add(callers.submit(() -> {
                    await(start);
                    preflight.validateAll(List.of(new CreateProjectRequest(
                            "TX-VAL-%03d".formatted(task),
                            "Read-only preflight " + task,
                            "EPSG:4326"
                    )));
                }));
                results.add(callers.submit(() -> {
                    await(start);
                    projects.create(new CreateProjectRequest(
                            "TX-SYS-%03d".formatted(task),
                            "Catalog publication " + task,
                            "EPSG:4326"
                    ));
                }));
            }
            start.countDown();
            for (Future<?> result : results) {
                result.get();
            }

            assertThat(projects.findAll())
                    .hasSize(20)
                    .extracting(project -> project.projectCode())
                    .doesNotHaveDuplicates()
                    .allSatisfy(code -> assertThat(code)
                            .startsWith("TX-SYS-"));
            assertThat(reviews.size()).isEqualTo(20);
        } finally {
            start.countDown();
            callers.shutdownNow();
        }
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(exception);
        }
    }
}
