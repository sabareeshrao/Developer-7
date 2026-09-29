package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import com.atlasgrid.geoops.project.validation.ValidationIssue;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProjectServiceValidationExceptionTest {

    private final ProjectService service = ProjectServiceTestFactory.create();

    @Test
    void rejectsDomainInvalidProjectBeforeChangingServiceState() {
        CreateProjectRequest request = new CreateProjectRequest(
                "bad-code",
                "Invalid Survey Intake",
                "WGS84"
        );

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOfSatisfying(
                        InvalidProjectRequestException.class,
                        exception -> assertThat(exception.issues())
                                .extracting(ValidationIssue::ruleCode)
                                .containsExactlyInAnyOrder(
                                        "PROJECT_CODE_FORMAT",
                                        "CRS_FORMAT"
                                )
                );

        assertThat(service.findAll()).isEmpty();
    }
}
