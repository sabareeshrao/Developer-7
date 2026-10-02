package com.atlasgrid.geoops.project.validation;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProjectNameLengthValidationRuleIntegrationTest {

    @Autowired
    private ProjectValidationService validationService;

    @Test
    void springDiscoversNewRuleWithoutOrchestratorChange() {
        CreateProjectRequest tooLong = new CreateProjectRequest(
                "TX-AUS-840",
                "S".repeat(ProjectNameLengthValidationRule.MAX_NAME_LENGTH + 1),
                "EPSG:4326"
        );

        ProjectValidationReport report =
                validationService.validate(tooLong);

        assertThat(report.valid()).isFalse();
        assertThat(report.issues())
                .extracting(ValidationIssue::ruleCode)
                .contains("PROJECT_NAME_LENGTH");
    }

    @Test
    void boundaryLengthIsAcceptedWithoutBreakingOtherRules() {
        CreateProjectRequest boundary = new CreateProjectRequest(
                "TX-AUS-841",
                "S".repeat(ProjectNameLengthValidationRule.MAX_NAME_LENGTH),
                "EPSG:4326"
        );

        assertThat(validationService.validate(boundary).valid())
                .isTrue();
    }
}
