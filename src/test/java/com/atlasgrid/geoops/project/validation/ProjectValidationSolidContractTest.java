package com.atlasgrid.geoops.project.validation;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectValidationSolidContractTest {

    @Test
    void newRuleCanBeInjectedWithoutChangingValidationService() {
        ProjectValidationRule customRule = new ProjectValidationRule() {
            @Override
            public String code() {
                return "SURVEY_ONLY";
            }

            @Override
            public List<ValidationIssue> validate(
                    CreateProjectRequest request
            ) {
                if (request.name().startsWith("Survey ")) {
                    return List.of();
                }
                return List.of(new ValidationIssue(
                        code(), "Only survey projects allowed in this workflow"
                ));
            }
        };

        ProjectValidationService ordinary =
                new ProjectValidationService(List.of(
                        new RequiredProjectFieldsValidationRule()
                ));

        ProjectValidationService extended =
                new ProjectValidationService(List.of(
                        new RequiredProjectFieldsValidationRule(),
                        customRule
                ));

        CreateProjectRequest candidate = new CreateProjectRequest(
                "TX-AUS-830", "Imagery Intake", "EPSG:4326"
        );

        assertThat(ordinary.validate(candidate).valid()).isTrue();
        assertThat(extended.validate(candidate).valid()).isFalse();
        assertThat(extended.validate(candidate).issues())
                .extracting(ValidationIssue::ruleCode)
                .containsExactly("SURVEY_ONLY");
    }

    @Test
    void ruleCompositionDefensivelyCopiesItsPolicyList() {
        java.util.List<ProjectValidationRule> rules =
                new java.util.ArrayList<>();
        rules.add(new RequiredProjectFieldsValidationRule());

        ProjectValidationService service =
                new ProjectValidationService(rules);

        rules.clear();

        assertThat(service.validate(new CreateProjectRequest(
                "TX-AUS-831", " ", "EPSG:4326"
        )).valid()).isFalse();
    }
}
