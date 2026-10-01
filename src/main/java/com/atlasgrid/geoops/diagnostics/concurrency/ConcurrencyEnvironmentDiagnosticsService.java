package com.atlasgrid.geoops.diagnostics.concurrency;

import com.atlasgrid.geoops.diagnostics.thread.JvmThreadDiagnosticsService;
import com.atlasgrid.geoops.project.validation.ProjectValidationExecutorMonitor;
import org.springframework.stereotype.Service;

@Service
public class ConcurrencyEnvironmentDiagnosticsService {

    private final JvmThreadDiagnosticsService threadDiagnostics;
    private final ProjectValidationExecutorMonitor executorMonitor;

    public ConcurrencyEnvironmentDiagnosticsService(
            JvmThreadDiagnosticsService threadDiagnostics,
            ProjectValidationExecutorMonitor executorMonitor
    ) {
        this.threadDiagnostics = threadDiagnostics;
        this.executorMonitor = executorMonitor;
    }

    public ConcurrencyEnvironmentSnapshot capture() {
        return new ConcurrencyEnvironmentSnapshot(
                threadDiagnostics.capture(),
                executorMonitor.capture()
        );
    }
}
