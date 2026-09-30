package com.atlasgrid.geoops.project.validation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProjectValidationWorkerThreadTest {

    @Autowired
    @Qualifier("projectValidationExecutor")
    private ExecutorService executorService;

    @Test
    void springManagedExecutorRunsWorkOnNamedGeoOpsThreads()
            throws Exception {
        Set<String> threadNames =
                ConcurrentHashMap.newKeySet();
        CountDownLatch workersReady =
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
            executorService.submit(() -> {
                threadNames.add(
                        Thread.currentThread().getName()
                );
                workersReady.countDown();
                await(release);
            });
        }

        assertThat(
                workersReady.await(
                        5,
                        TimeUnit.SECONDS
                )
        ).isTrue();

        release.countDown();

        assertThat(threadNames)
                .hasSize(
                        ProjectValidationExecutorConfiguration
                                .VALIDATION_THREADS
                )
                .allSatisfy(name ->
                        assertThat(name)
                                .startsWith(
                                        "geoops-project-validation-"
                                )
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
