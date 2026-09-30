package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProjectImportAtomicityTest {

    private final ProjectService service = ProjectServiceTestFactory.create();

    @Test
    void invalidLaterRowLeavesCatalogUnchanged() {
        assertThatThrownBy(() -> service.createAllAtomically(List.of(
                new CreateProjectRequest(
                        "TX-AUS-501",
                        "Valid Project",
                        "EPSG:4326"
                ),
                new CreateProjectRequest(
                        "TX-DAL-501",
                        "   ",
                        "EPSG:4326"
                )
        )))
                .isInstanceOf(InvalidProjectRequestException.class);

        assertThat(service.findAll()).isEmpty();
    }

    @Test
    void duplicateInsideBatchLeavesCatalogUnchanged() {
        assertThatThrownBy(() -> service.createAllAtomically(List.of(
                new CreateProjectRequest(
                        "TX-AUS-502",
                        "First",
                        "EPSG:4326"
                ),
                new CreateProjectRequest(
                        "TX-AUS-502",
                        "Duplicate",
                        "EPSG:3857"
                )
        )))
                .isInstanceOf(DuplicateProjectException.class);

        assertThat(service.findAll()).isEmpty();
    }
}
