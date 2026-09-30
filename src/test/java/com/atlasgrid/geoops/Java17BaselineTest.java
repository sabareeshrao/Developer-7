package com.atlasgrid.geoops;

import com.atlasgrid.geoops.project.domain.GeoProject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Set 46 executable evidence for the GeoOps Java 17 baseline.
 */
class Java17BaselineTest {

    @Test
    void runtimeAndLanguageModelMatchJava17Baseline() {
        assertThat(Runtime.version().feature())
                .isEqualTo(17);

        assertThat(GeoProject.class.isRecord())
                .isTrue();
    }
}
