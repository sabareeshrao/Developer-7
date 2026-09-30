package com.atlasgrid.geoops.project.validation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Bounded worker pool for independent GIS request validation.
 */
@Configuration(proxyBeanMethods = false)
public class ProjectValidationExecutorConfiguration {

    static final int VALIDATION_THREADS = 4;

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

        return Executors.newFixedThreadPool(
                VALIDATION_THREADS,
                threadFactory
        );
    }
}
