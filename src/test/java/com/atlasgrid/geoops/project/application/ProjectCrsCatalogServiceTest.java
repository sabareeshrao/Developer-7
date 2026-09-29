package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.domain.GeoProject;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectCrsCatalogServiceTest {

    private final ProjectCrsCatalogService service =
            new ProjectCrsCatalogService();

    @Test
    void returnsUniqueCoordinateReferenceSystemsInNaturalOrder() {
        List<String> result =
                service.sortedUniqueCoordinateReferenceSystems(List.of(
                        project("TX-AUS-033", "EPSG:4326"),
                        project("TX-DAL-033", "EPSG:3857"),
                        project("TX-HOU-033", "EPSG:4326"),
                        project("TX-SAT-033", "EPSG:26914")
                ));

        assertThat(result)
                .containsExactly(
                        "EPSG:26914",
                        "EPSG:3857",
                        "EPSG:4326"
                )
                .isUnmodifiable();
    }

    private GeoProject project(String projectCode, String crs) {
        return new GeoProject(
                UUID.randomUUID(),
                projectCode,
                "TreeSet CRS Project",
                crs,
                Instant.parse("2026-09-29T12:00:00Z")
        );
    }
}
