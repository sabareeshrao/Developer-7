package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import com.atlasgrid.geoops.project.domain.GeoProject;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProjectOptionalPracticalUsageTest {

    private final ProjectService service = ProjectServiceTestFactory.create();

    @Test
    void intakePositionLookupUsesOptionalForNormalPresenceAndAbsence() {
        service.create(new CreateProjectRequest(
                "TX-AUS-042",
                "Optional Practical Austin",
                "EPSG:4326"
        ));
        service.create(new CreateProjectRequest(
                "TX-DAL-042",
                "Optional Practical Dallas",
                "EPSG:3857"
        ));

        Optional<GeoProject> secondProject =
                service.findByIntakePosition(2);

        assertThat(secondProject)
                .hasValueSatisfying(project ->
                        assertThat(project.projectCode())
                                .isEqualTo("TX-DAL-042")
                );

        assertThat(service.findByIntakePosition(0)).isEmpty();
        assertThat(service.findByIntakePosition(99)).isEmpty();
    }

    @Test
    void practicalOptionalPipelineFiltersMapsAndRequiresExpectedValue() {
        service.create(new CreateProjectRequest(
                "TX-HOU-042",
                "Optional Practical Houston",
                "EPSG:4326"
        ));

        String projectCode = service.findByIntakePosition(1)
                .filter(project ->
                        project.coordinateReferenceSystem()
                                .equals("EPSG:4326"))
                .map(GeoProject::projectCode)
                .orElseThrow();

        assertThat(projectCode).isEqualTo("TX-HOU-042");
    }

    @Test
    void orElseThrowIsAppropriateOnlyWhenCallerRequiresPresence() {
        assertThatThrownBy(() ->
                service.findByIntakePosition(1).orElseThrow()
        ).isInstanceOf(java.util.NoSuchElementException.class);
    }
}
