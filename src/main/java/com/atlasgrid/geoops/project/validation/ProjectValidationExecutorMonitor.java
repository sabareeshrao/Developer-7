package com.atlasgrid.geoops.project.validation;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Internal operational diagnostics for the project-validation worker pool.
 */
@Service
public class ProjectValidationExecutorMonitor {

    private final ThreadPoolExecutor executor;

    public ProjectValidationExecutorMonitor(
            @Qualifier("projectValidationExecutor")
            ThreadPoolExecutor executor
    ) {
        this.executor = executor;
    }

    public ProjectValidationExecutorSnapshot capture() {
        return new ProjectValidationExecutorSnapshot(
                executor.getPoolSize(),
                executor.getActiveCount(),
                executor.getLargestPoolSize(),
                executor.getQueue().size(),
                executor.getQueue().remainingCapacity(),
                executor.getCompletedTaskCount(),
                executor.getTaskCount()
        );
    }
}
