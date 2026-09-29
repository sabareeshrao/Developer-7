package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProjectServiceDuplicateTest {

    private final ProjectService service = ProjectServiceTestFactory.create();

    @Test
    void rejectsSecondIntakeWithSameLogicalProjectCode() {
        service.create(new CreateProjectRequest(
                "TX-AUS-001",
                "Austin Survey Intake",
                "EPSG:4326"
        ));

        assertThatThrownBy(() -> service.create(new CreateProjectRequest(
                "TX-AUS-001",
                "Renamed Austin Survey",
                "EPSG:2277"
        )))
                .isInstanceOf(DuplicateProjectException.class)
                .hasMessageContaining("TX-AUS-001");

        assertThat(service.findAll()).hasSize(1);
    }
}
