package com.atlasgrid.geoops.project.validation;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Encapsulates the GeoOps project-code format rule.
 *
 * <p>This concrete rule is final because extension should happen by creating a
 * new ProjectValidationRule implementation, not by subclassing this rule.</p>
 */
@Component
public final class ProjectCodeValidationRule implements ProjectValidationRule {

    @Override
    public String code() {
        return "PROJECT_CODE_FORMAT";
    }

    @Override
    public List<ValidationIssue> validate(CreateProjectRequest request) {
        if (ProjectValidationStandards.isValidProjectCode(request.projectCode())) {
            return List.of();
        }

        return List.of(new ValidationIssue(
                code(),
                "Project code must follow a pattern such as "
                        + ProjectValidationStandards.PROJECT_CODE_EXAMPLE
        ));
    }
}
