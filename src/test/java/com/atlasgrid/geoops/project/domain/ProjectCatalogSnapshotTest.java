package com.atlasgrid.geoops.project.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProjectCatalogSnapshotTest {

    @Test
    void defensivelyCopiesMutableProjectList() {
        List<GeoProject> source = new ArrayList<>();
        source.add(project("TX-AUS-001"));

        ProjectCatalogSnapshot snapshot = new ProjectCatalogSnapshot(
                Instant.parse("2026-09-29T00:00:00Z"),
                source
        );

        source.add(project("TX-AUS-002"));

        assertThat(snapshot.projectCount()).isEqualTo(1);
        assertThat(snapshot.projects())
                .extracting(GeoProject::projectCode)
                .containsExactly("TX-AUS-001");
    }

    @Test
    void exposedProjectListCannotBeModified() {
        ProjectCatalogSnapshot snapshot = new ProjectCatalogSnapshot(
                Instant.parse("2026-09-29T00:00:00Z"),
                List.of(project("TX-AUS-001"))
        );

        assertThatThrownBy(() -> snapshot.projects().clear())
                .isInstanceOf(UnsupportedOperationException.class);

        assertThat(snapshot.projectCount()).isEqualTo(1);
    }

    private GeoProject project(String projectCode) {
        return new GeoProject(
                UUID.randomUUID(),
                projectCode,
                "Snapshot Project",
                "EPSG:4326",
                Instant.parse("2026-09-29T00:00:00Z")
        );
    }
}
