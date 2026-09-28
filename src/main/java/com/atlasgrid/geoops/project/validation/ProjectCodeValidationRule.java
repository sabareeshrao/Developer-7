package com.atlasgrid.geoops.project.validation;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Encapsulates the GeoOps project-code format rule.
 */
@Component
public class ProjectCodeValidationRule implements ProjectValidationRule {

    private static final Pattern PROJECT_CODE =
            Pattern.compile("[A-Z]{2,5}-[A-Z]{2,5}-\\d{3}");

    @Override
    public String code() {
        return "PROJECT_CODE_FORMAT";
    }

    @Override
    public List<ValidationIssue> validate(CreateProjectRequest request) {
        if (PROJECT_CODE.matcher(request.projectCode()).matches()) {
            return List.of();
        }

        return List.of(new ValidationIssue(
                code(),
                "Project code must follow a pattern such as TX-AUS-001"
        ));
    }
}
