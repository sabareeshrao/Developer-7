package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectServiceEqualityOperatorTest {

    private final ProjectService service = ProjectServiceTestFactory.create();

    @Test
    void findsProjectCodeByStringContentEvenWhenReferencesDiffer() {
        service.create(new CreateProjectRequest(
                "TX-AUS-001",
                "Austin Survey Intake",
                "EPSG:4326"
        ));

        String requestProjectCode = new String("TX-AUS-001");

        assertThat(requestProjectCode == "TX-AUS-001").isFalse();
        assertThat(requestProjectCode.equals("TX-AUS-001")).isTrue();
        assertThat(service.containsProjectCode(requestProjectCode)).isTrue();
    }

    @Test
    void doesNotMatchDifferentProjectCodeContent() {
        service.create(new CreateProjectRequest(
                "TX-AUS-001",
                "Austin Survey Intake",
                "EPSG:4326"
        ));

        assertThat(service.containsProjectCode("TX-AUS-002")).isFalse();
    }
}
