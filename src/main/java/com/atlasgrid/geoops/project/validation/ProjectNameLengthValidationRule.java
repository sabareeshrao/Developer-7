package com.atlasgrid.geoops.project.validation;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * A separate policy for excessive project display names.
 */
@Component
@Order(15)
public final class ProjectNameLengthValidationRule
        implements ProjectValidationRule {

    public static final int MAX_NAME_LENGTH =
            ProjectNameLengthPolicy.MAX_NAME_LENGTH;

    @Override
    public String code() {
        return "PROJECT_NAME_LENGTH";
    }

    @Override
    public List<ValidationIssue> validate(CreateProjectRequest request) {
        String name = request.name();

        if (!ProjectNameLengthPolicy.exceedsLimit(name)) {
            return List.of();
        }

        return List.of(new ValidationIssue(
                code(),
                "Project name must not exceed "
                        + MAX_NAME_LENGTH + " characters"
        ));
    }
}
