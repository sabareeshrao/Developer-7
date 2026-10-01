package com.atlasgrid.geoops.project.validation;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Supplier;

/**
 * Internal operational diagnostics for the project-validation worker pool.
 */
@Service
public class ProjectValidationExecutorMonitor {

    private final Supplier<ProjectValidationExecutorSnapshot>
            snapshotSupplier;

    public ProjectValidationExecutorMonitor(
            @Qualifier("projectValidationExecutor")
            ThreadPoolExecutor executor
    ) {
        this.snapshotSupplier =
                () -> snapshot(executor);
    }

    public ProjectValidationExecutorSnapshot capture() {
        return snapshotSupplier.get();
    }

    private static ProjectValidationExecutorSnapshot snapshot(
            ThreadPoolExecutor executor
    ) {
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
