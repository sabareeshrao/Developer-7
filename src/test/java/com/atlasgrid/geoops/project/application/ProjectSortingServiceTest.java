package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.domain.GeoProject;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectSortingServiceTest {

    private final ProjectSortingService service =
            new ProjectSortingService();

    @Test
    void usesArrayAndCollectionSortingWithoutMutatingSourceOrder() {
        List<GeoProject> source = new ArrayList<>(List.of(
                project("TX-HOU-029", "EPSG:4326"),
                project("TX-AUS-029", "EPSG:3857"),
                project("TX-DAL-029", "EPSG:4326")
        ));

        ProjectSortingView view = service.sort(source);

        assertThat(view.projectCodesAlphabetically())
                .containsExactly(
                        "TX-AUS-029",
                        "TX-DAL-029",
                        "TX-HOU-029"
                );

        assertThat(view.projectsByCrsThenCode())
                .extracting(GeoProject::projectCode)
                .containsExactly(
                        "TX-AUS-029",
                        "TX-DAL-029",
                        "TX-HOU-029"
                );

        assertThat(source)
                .extracting(GeoProject::projectCode)
                .containsExactly(
                        "TX-HOU-029",
                        "TX-AUS-029",
                        "TX-DAL-029"
                );
    }

    @Test
    void returnedSortingViewsAreImmutable() {
        ProjectSortingView view = service.sort(List.of(
                project("TX-AUS-029", "EPSG:4326")
        ));

        assertThat(view.projectCodesAlphabetically()).isUnmodifiable();
        assertThat(view.projectsByCrsThenCode()).isUnmodifiable();
    }

    private GeoProject project(String projectCode, String crs) {
        return new GeoProject(
                UUID.randomUUID(),
                projectCode,
                "Sorting Project",
                crs,
                Instant.parse("2026-09-29T12:00:00Z")
        );
    }
}
