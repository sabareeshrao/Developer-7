package com.atlasgrid.geoops.project.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectNameLengthPolicyTest {

    @Test
    void policyHandlesMissingBoundaryAndTooLongNames() {
        assertThat(ProjectNameLengthPolicy.exceedsLimit(null))
                .isFalse();
        assertThat(ProjectNameLengthPolicy.exceedsLimit(
                "G".repeat(ProjectNameLengthPolicy.MAX_NAME_LENGTH)
        )).isFalse();
        assertThat(ProjectNameLengthPolicy.exceedsLimit(
                "G".repeat(ProjectNameLengthPolicy.MAX_NAME_LENGTH + 1)
        )).isTrue();
    }

    @Test
    void existingRuleStillMapsPolicyFailureToTypedIssue() {
        ProjectNameLengthValidationRule rule =
                new ProjectNameLengthValidationRule();
        var request = new com.atlasgrid.geoops.project.api.CreateProjectRequest(
                "TX-AUS-850",
                "G".repeat(ProjectNameLengthPolicy.MAX_NAME_LENGTH + 1),
                "EPSG:4326"
        );

        assertThat(rule.validate(request))
                .singleElement()
                .extracting(ValidationIssue::ruleCode)
                .isEqualTo("PROJECT_NAME_LENGTH");
    }
}
