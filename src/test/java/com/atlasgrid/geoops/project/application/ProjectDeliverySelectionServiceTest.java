package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.domain.GeoProject;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectDeliverySelectionServiceTest {

    private final ProjectDeliverySelectionService service =
            new ProjectDeliverySelectionService();

    @Test
    void filtersMapsSortsAndCollectsProjectCodesForRequestedCrs() {
        List<GeoProject> projects = new ArrayList<>(List.of(
                project("TX-HOU-040", "EPSG:4326"),
                project("TX-AUS-040", "EPSG:3857"),
                project("TX-DAL-040", "EPSG:4326")
        ));

        ProjectDeliverySelection selection =
                service.select(projects, "EPSG:4326");

        assertThat(selection.coordinateReferenceSystem())
                .isEqualTo("EPSG:4326");
        assertThat(selection.projectCount()).isEqualTo(2);
        assertThat(selection.projectCodes())
                .containsExactly("TX-DAL-040", "TX-HOU-040")
                .isUnmodifiable();

        assertThat(projects)
                .extracting(GeoProject::projectCode)
                .containsExactly(
                        "TX-HOU-040",
                        "TX-AUS-040",
                        "TX-DAL-040"
                );
    }

    @Test
    void returnsEmptySelectionWhenNoProjectsMatchRequestedCrs() {
        ProjectDeliverySelection selection = service.select(
                List.of(project("TX-AUS-040", "EPSG:3857")),
                "EPSG:4326"
        );

        assertThat(selection.projectCount()).isZero();
        assertThat(selection.projectCodes()).isEmpty();
    }

    private GeoProject project(String projectCode, String crs) {
        return new GeoProject(
                UUID.randomUUID(),
                projectCode,
                "Stream Delivery Project",
                crs,
                Instant.parse("2026-09-29T12:00:00Z")
        );
    }
}
