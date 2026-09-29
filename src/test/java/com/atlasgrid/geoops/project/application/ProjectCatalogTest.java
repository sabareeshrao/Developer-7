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

    @Test
    void findsProjectByOneBasedIntakePosition() {
        ProjectCatalog catalog = new ProjectCatalog();
        catalog.add(project("TX-AUS-023"));
        catalog.add(project("TX-DAL-023"));
        catalog.add(project("TX-HOU-023"));

        assertThat(catalog.findByIntakePosition(2))
                .isPresent()
                .get()
                .extracting(GeoProject::projectCode)
                .isEqualTo("TX-DAL-023");
    }

    @Test
    void outOfRangeIntakePositionReturnsEmpty() {
        ProjectCatalog catalog = new ProjectCatalog();
        catalog.add(project("TX-AUS-023"));

        assertThat(catalog.findByIntakePosition(0)).isEmpty();
        assertThat(catalog.findByIntakePosition(2)).isEmpty();
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
