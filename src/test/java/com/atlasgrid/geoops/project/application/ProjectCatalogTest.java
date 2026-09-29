package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.domain.GeoProject;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectCatalogTest {

    @Test
    void rejectsDuplicateLogicalProjectCode() {
        ProjectCatalog catalog = new ProjectCatalog();

        boolean firstAdded = catalog.add(project("TX-AUS-022"));
        boolean duplicateAdded = catalog.add(project("TX-AUS-022"));

        assertThat(firstAdded).isTrue();
        assertThat(duplicateAdded).isFalse();
        assertThat(catalog.size()).isEqualTo(1);
    }

    @Test
    void findAllReturnsImmutableSnapshotInsteadOfInternalMutableList() {
        ProjectCatalog catalog = new ProjectCatalog();
        catalog.add(project("TX-AUS-022"));

        var snapshot = catalog.findAll();
        catalog.add(project("TX-DAL-022"));

        assertThat(snapshot)
                .hasSize(1)
                .isUnmodifiable();
        assertThat(catalog.findAll()).hasSize(2);
    }

    private GeoProject project(String projectCode) {
        return new GeoProject(
                UUID.randomUUID(),
                projectCode,
                "Collection Best Practices",
                "EPSG:4326",
                Instant.parse("2026-09-29T12:00:00Z")
        );
    }
}
