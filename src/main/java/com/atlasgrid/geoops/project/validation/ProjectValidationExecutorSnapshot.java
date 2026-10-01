package com.atlasgrid.geoops.project.validation;

/**
 * Point-in-time operational view of the bounded validation executor.
 */
public record ProjectValidationExecutorSnapshot(
        int poolSize,
        int activeCount,
        int largestPoolSize,
        int queuedTaskCount,
        int remainingQueueCapacity,
        long completedTaskCount,
        long submittedTaskCount
) {
}
