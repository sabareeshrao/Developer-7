package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.domain.GeoProject;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectManifestFormatterTest {

    private final ProjectManifestFormatter formatter = new ProjectManifestFormatter();

    @Test
    void formatsMultipleProjectsIntoOneManifest() {
        GeoProject first = new GeoProject(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "TX-AUS-001",
                "Austin Survey Intake",
                "EPSG:4326",
                Instant.parse("2026-09-28T20:00:00Z")
        );

        GeoProject second = new GeoProject(
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                "TX-AUS-002",
                "Austin Control Network",
                "EPSG:2277",
                Instant.parse("2026-09-28T21:00:00Z")
        );

        String manifest = formatter.format(List.of(first, second));

        assertThat(manifest)
                .contains("GEOOPS PROJECT MANIFEST")
                .contains("projectCount=2")
                .contains("projectCode=TX-AUS-001")
                .contains("crs=EPSG:4326")
                .contains("projectCode=TX-AUS-002")
                .contains("crs=EPSG:2277");
    }

    @Test
    void formatsAnEmptyManifestWithoutFailure() {
        String manifest = formatter.format(List.of());

        assertThat(manifest)
                .contains("GEOOPS PROJECT MANIFEST")
                .contains("projectCount=0");
    }
}
