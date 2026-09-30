package com.atlasgrid.geoops.project.validation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Incremental functional-interface migration for validation rules.
 *
 * <p>The legacy ProjectValidationRule interface has two abstract methods, so it
 * cannot be a lambda target. New functional rules can be expressed as
 * ProjectValidationCheck lambdas and adapted back to the existing service
 * contract while older rule components continue to work unchanged.</p>
 */
@Configuration
public class ProjectValidationRuleConfiguration {

    @Bean
    public ProjectValidationRule projectCodeValidationRule() {
        String ruleCode = "PROJECT_CODE_FORMAT";
        String example = ProjectValidationStandards.PROJECT_CODE_EXAMPLE;

        ProjectValidationCheck check = request -> {
            if (ProjectValidationStandards
                    .isValidProjectCode(request.projectCode())) {
                return List.of();
            }

            return List.of(new ValidationIssue(
                    ruleCode,
                    "Project code must follow a pattern such as " + example
            ));
        };

        return new FunctionalProjectValidationRuleAdapter(
                ruleCode,
                check
        );
    }
}
