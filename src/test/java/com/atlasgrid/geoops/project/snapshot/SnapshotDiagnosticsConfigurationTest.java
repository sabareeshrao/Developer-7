package com.atlasgrid.geoops.project.snapshot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class SnapshotDiagnosticsConfigurationTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withUserConfiguration(
                            SnapshotDiagnosticsConfiguration.class
                    );

    @Test
    void diagnosticsBeanIsDisabledWhenPropertyIsAbsent() {
        contextRunner.run(context ->
                assertThat(context)
                        .doesNotHaveBean(
                                SnapshotReflectionInspector.class
                        )
        );
    }

    @Test
    void diagnosticsBeanIsEnabledWhenPropertyIsTrue() {
        contextRunner
                .withPropertyValues(
                        "geoops.snapshot.diagnostics.enabled=true"
                )
                .run(context ->
                        assertThat(context)
                                .hasSingleBean(
                                        SnapshotReflectionInspector.class
                                )
                );
    }

    @Test
    void diagnosticsBeanIsDisabledWhenPropertyIsFalse() {
        contextRunner
                .withPropertyValues(
                        "geoops.snapshot.diagnostics.enabled=false"
                )
                .run(context ->
                        assertThat(context)
                                .doesNotHaveBean(
                                        SnapshotReflectionInspector.class
                                )
                );
    }
}
