package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.domain.GeoProject;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Set 28 evidence for why ProjectCatalog uses both List and HashSet.
 *
 * <p>The List is the ordered record store. The HashSet-backed identity index
 * answers uniqueness/membership questions. The test deliberately verifies both
 * responsibilities against the same catalog state.</p>
 */
class ProjectCatalogCollectionRoleTest {

    @Test
    void listPreservesRecordsWhileHashSetProtectsLogicalIdentity() {
        ProjectCatalog catalog = new ProjectCatalog();

        assertThat(catalog.add(project("TX-AUS-028"))).isTrue();
        assertThat(catalog.add(project("TX-DAL-028"))).isTrue();

        // List / ArrayList responsibility: keep accepted records in intake order.
        assertThat(catalog.findAll())
                .extracting(GeoProject::projectCode)
                .containsExactly("TX-AUS-028", "TX-DAL-028");

        // Set / HashSet responsibility: answer identity membership efficiently.
        assertThat(catalog.containsProjectCode("TX-AUS-028")).isTrue();
        assertThat(catalog.containsProjectCode("TX-HOU-028")).isFalse();

        // Same logical identity is rejected and the ordered record list is unchanged.
        assertThat(catalog.add(project("TX-AUS-028"))).isFalse();
        assertThat(catalog.findAll())
                .extracting(GeoProject::projectCode)
                .containsExactly("TX-AUS-028", "TX-DAL-028");
        assertThat(catalog.size()).isEqualTo(2);
    }

    private GeoProject project(String projectCode) {
        return new GeoProject(
                UUID.randomUUID(),
                projectCode,
                "List and HashSet Situation",
                "EPSG:4326",
                Instant.parse("2026-09-29T12:00:00Z")
        );
    }
}
