package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectServiceLookupStrategyTest {

    private final ProjectService service = ProjectServiceTestFactory.create();

    @Test
    void expectedMissingProjectUsesOptionalInsteadOfExceptionControlFlow() {
        assertThat(service.findByProjectCode("TX-AUS-404")).isEmpty();
    }

    @Test
    void existingProjectIsReturnedThroughOptional() {
        service.create(new CreateProjectRequest(
                "TX-AUS-616",
                "Lookup Strategy Project",
                "EPSG:4326"
        ));

        assertThat(service.findByProjectCode("TX-AUS-616"))
                .isPresent()
                .get()
                .extracting(project -> project.projectCode())
                .isEqualTo("TX-AUS-616");
    }
}
