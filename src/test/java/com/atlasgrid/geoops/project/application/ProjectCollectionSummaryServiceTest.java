package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.domain.GeoProject;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectCollectionSummaryServiceTest {

    private final ProjectCollectionSummaryService service =
            new ProjectCollectionSummaryService();

    @Test
    void preservesProjectOrderAndCountsDistinctCoordinateReferenceSystems() {
        List<GeoProject> projects = List.of(
                project("TX-AUS-020", "EPSG:4326"),
                project("TX-DAL-020", "EPSG:3857"),
                project("TX-HOU-020", "EPSG:4326")
        );

        ProjectCollectionSummary summary = service.summarize(projects);

        assertThat(summary.projectCount()).isEqualTo(3);
        assertThat(summary.distinctCoordinateReferenceSystemCount())
                .isEqualTo(2);
        assertThat(summary.projectCodesInIntakeOrder())
                .containsExactly(
                        "TX-AUS-020",
                        "TX-DAL-020",
                        "TX-HOU-020"
                );
    }

    @Test
    void returnedProjectCodeListIsImmutable() {
        ProjectCollectionSummary summary =
                service.summarize(List.of(project("TX-AUS-021", "EPSG:4326")));

        assertThat(summary.projectCodesInIntakeOrder())
                .isUnmodifiable();
    }

    private GeoProject project(String projectCode, String crs) {
        return new GeoProject(
                UUID.randomUUID(),
                projectCode,
                "Collection Summary Project",
                crs,
                Instant.parse("2026-09-29T12:00:00Z")
        );
    }
}
