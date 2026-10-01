package com.atlasgrid.geoops.diagnostics.concurrency;

import com.atlasgrid.geoops.diagnostics.thread.JvmThreadSnapshot;
import com.atlasgrid.geoops.project.validation.ProjectValidationExecutorSnapshot;

public record ConcurrencyEnvironmentSnapshot(
        JvmThreadSnapshot jvmThreads,
        ProjectValidationExecutorSnapshot validationExecutor
) {
}
