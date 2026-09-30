package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.domain.GeoProject;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProjectOptionalSemanticsTest {

    @Test
    void ofRequiresKnownNonNullProjectWhileOfNullableAcceptsNull() {
        GeoProject project = project("TX-AUS-641");

        assertThat(Optional.of(project)).contains(project);
        assertThat(Optional.ofNullable(project)).contains(project);
        assertThat(Optional.ofNullable(null)).isEmpty();

        assertThatThrownBy(() -> Optional.of(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void getOnEmptyOptionalThrowsAndShouldNotDriveNormalLookupFlow() {
        Optional<GeoProject> missing = Optional.empty();

        assertThatThrownBy(missing::get)
                .isInstanceOf(java.util.NoSuchElementException.class);
    }

    private GeoProject project(String projectCode) {
        return new GeoProject(
                UUID.randomUUID(),
                projectCode,
                "Optional Semantics Project",
                "EPSG:4326",
                Instant.parse("2026-09-29T12:00:00Z")
        );
    }
}
