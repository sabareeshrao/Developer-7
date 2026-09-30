package com.atlasgrid.geoops.project.validation;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FunctionalProjectValidationRuleAdapterTest {

    @Test
    void adaptsLambdaCheckToLegacyRuleContract() {
        String ruleCode = "NAME_LENGTH";
        int maximumLength = 12;

        ProjectValidationCheck check = request ->
                request.name().length() <= maximumLength
                        ? List.of()
                        : List.of(new ValidationIssue(
                                ruleCode,
                                "Project name is too long"
                        ));

        ProjectValidationRule rule =
                new FunctionalProjectValidationRuleAdapter(
                        ruleCode,
                        check
                );

        assertThat(rule.code()).isEqualTo("NAME_LENGTH");
        assertThat(rule.validate(request("Short name"))).isEmpty();
        assertThat(rule.validate(request("A very long project name")))
                .extracting(ValidationIssue::ruleCode)
                .containsExactly("NAME_LENGTH");
    }

    private CreateProjectRequest request(String name) {
        return new CreateProjectRequest(
                "TX-AUS-039",
                name,
                "EPSG:4326"
        );
    }
}
