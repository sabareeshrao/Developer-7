package com.atlasgrid.geoops.project.validation;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectValidationServiceTest {

    private final ProjectValidationService service = new ProjectValidationService(
            List.of(
                    new ProjectValidationRuleConfiguration()
                            .projectCodeValidationRule(),
                    new CoordinateReferenceSystemValidationRule()
            )
    );

    @Test
    void acceptsAValidGeoOpsProjectRequest() {
        ProjectValidationReport report = service.validate(
                new CreateProjectRequest(
                        "TX-AUS-001",
                        "Austin Survey Intake",
                        "EPSG:4326"
                )
        );

        assertThat(report.valid()).isTrue();
        assertThat(report.issues()).isEmpty();
    }

    @Test
    void runsLegacyAndFunctionalRuleStylesTogether() {
        ProjectValidationReport report = service.validate(
                new CreateProjectRequest(
                        "bad-code",
                        "Austin Survey Intake",
                        "WGS84"
                )
        );

        assertThat(report.valid()).isFalse();
        assertThat(report.issues())
                .extracting(ValidationIssue::ruleCode)
                .containsExactlyInAnyOrder(
                        "PROJECT_CODE_FORMAT",
                        "CRS_FORMAT"
                );
    }
}
