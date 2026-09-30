package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.domain.GeoProject;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectCatalogConcurrencyTest {

    @Test
    void concurrentAddsKeepListAndIdentitySetConsistent()
            throws InterruptedException {
        ProjectCatalog catalog = new ProjectCatalog();
        int projectCount = 100;
        CountDownLatch start = new CountDownLatch(1);
        List<Thread> threads = new ArrayList<>();

        for (int index = 0; index < projectCount; index++) {
            String projectCode = "TX-AUS-%03d".formatted(index);

            Thread thread = new Thread(() -> {
                await(start);
                catalog.add(project(projectCode));
            });
            threads.add(thread);
            thread.start();
        }

        start.countDown();

        for (Thread thread : threads) {
            thread.join();
        }

        assertThat(catalog.size()).isEqualTo(projectCount);
        assertThat(catalog.findAll())
                .extracting(GeoProject::projectCode)
                .doesNotHaveDuplicates();

        for (GeoProject project : catalog.findAll()) {
            assertThat(
                    catalog.containsProjectCode(project.projectCode())
            ).isTrue();
        }
    }

    private GeoProject project(String code) {
        return new GeoProject(
                UUID.randomUUID(),
                code,
                "Concurrent Project",
                "EPSG:4326",
                Instant.now()
        );
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
