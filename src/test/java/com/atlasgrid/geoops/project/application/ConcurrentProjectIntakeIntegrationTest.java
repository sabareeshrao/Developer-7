package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import com.atlasgrid.geoops.review.ProjectReviewQueue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ConcurrentProjectIntakeIntegrationTest {

    private final ExecutorService callers =
            Executors.newFixedThreadPool(16);

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectReviewQueue reviewQueue;

    @AfterEach
    void shutdownCallers() {
        callers.shutdownNow();
    }

    @Test
    void concurrentUniqueUsersAllPublishConsistentProjects()
            throws Exception {
        int requestCount = 50;
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>();

        for (int index = 0; index < requestCount; index++) {
            int requestIndex = index;

            futures.add(callers.submit(() -> {
                await(start);
                projectService.create(new CreateProjectRequest(
                        "TX-CU-%03d".formatted(requestIndex),
                        "Concurrent User " + requestIndex,
                        "EPSG:4326"
                ));
            }));
        }

        start.countDown();
        awaitAll(futures);

        assertThat(projectService.findAll())
                .hasSize(requestCount)
                .extracting(project -> project.projectCode())
                .doesNotHaveDuplicates();
        assertThat(reviewQueue.size()).isEqualTo(requestCount);
    }

    @Test
    void concurrentDuplicateUsersPublishExactlyOneProject()
            throws Exception {
        int requestCount = 20;
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger successes = new AtomicInteger();
        AtomicInteger duplicates = new AtomicInteger();
        List<Future<?>> futures = new ArrayList<>();

        for (int index = 0; index < requestCount; index++) {
            futures.add(callers.submit(() -> {
                await(start);

                try {
                    projectService.create(new CreateProjectRequest(
                            "TX-RACE-001",
                            "Same Project",
                            "EPSG:4326"
                    ));
                    successes.incrementAndGet();
                } catch (DuplicateProjectException exception) {
                    duplicates.incrementAndGet();
                }
            }));
        }

        start.countDown();
        awaitAll(futures);

        assertThat(successes).hasValue(1);
        assertThat(duplicates).hasValue(requestCount - 1);
        assertThat(projectService.findAll())
                .singleElement()
                .extracting(project -> project.projectCode())
                .isEqualTo("TX-RACE-001");
        assertThat(reviewQueue.size()).isEqualTo(1);
    }

    private void awaitAll(List<Future<?>> futures)
            throws Exception {
        for (Future<?> future : futures) {
            future.get();
        }
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
