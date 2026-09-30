package com.atlasgrid.geoops.project.validation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Bounded worker pool for independent GIS request validation.
 */
@Configuration(proxyBeanMethods = false)
public class ProjectValidationExecutorConfiguration {

    static final int VALIDATION_THREADS = 4;
    static final int VALIDATION_QUEUE_CAPACITY = 64;

    @Bean(name = "projectValidationExecutor", destroyMethod = "shutdown")
    ExecutorService projectValidationExecutor() {
        AtomicInteger sequence = new AtomicInteger();

        ThreadFactory threadFactory = task -> {
            Thread thread = new Thread(
                    task,
                    "geoops-project-validation-"
                            + sequence.incrementAndGet()
            );
            thread.setDaemon(false);
            return thread;
        };

        return new ThreadPoolExecutor(
                VALIDATION_THREADS,
                VALIDATION_THREADS,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(
                        VALIDATION_QUEUE_CAPACITY
                ),
                threadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
