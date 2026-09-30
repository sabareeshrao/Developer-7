package com.atlasgrid.geoops.project.snapshot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Optional runtime diagnostics for the trusted snapshot subsystem.
 */
@Configuration(proxyBeanMethods = false)
public class SnapshotDiagnosticsConfiguration {

    @Bean
    @ConditionalOnProperty(
            prefix = "geoops.snapshot.diagnostics",
            name = "enabled",
            havingValue = "true"
    )
    SnapshotReflectionInspector snapshotReflectionInspector() {
        return new SnapshotReflectionInspector();
    }
}
