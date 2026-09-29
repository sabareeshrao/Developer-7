package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import com.atlasgrid.geoops.project.domain.GeoProject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProjectServiceFinalReferenceTest {

    private final ProjectService service = ProjectServiceTestFactory.create();

    @Test
    void finalListReferenceCanStillPointToAMutableList() {
        service.create(new CreateProjectRequest(
                "TX-AUS-001",
                "Austin Survey Intake",
                "EPSG:4326"
        ));

        service.create(new CreateProjectRequest(
                "TX-AUS-002",
                "Austin Control Network",
                "EPSG:2277"
        ));

        assertThat(service.findAll())
                .extracting(GeoProject::projectCode)
                .containsExactly("TX-AUS-001", "TX-AUS-002");
    }

    @Test
    void callersCannotMutateTheInternalProjectCollectionThroughFindAll() {
        service.create(new CreateProjectRequest(
                "TX-AUS-001",
                "Austin Survey Intake",
                "EPSG:4326"
        ));

        List<GeoProject> snapshot = service.findAll();

        assertThatThrownBy(snapshot::clear)
                .isInstanceOf(UnsupportedOperationException.class);

        assertThat(service.findAll()).hasSize(1);
    }
}
