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

    public static final int MAX_NAME_LENGTH = 120;

    @Override
    public String code() {
        return "PROJECT_NAME_LENGTH";
    }

    @Override
    public List<ValidationIssue> validate(CreateProjectRequest request) {
        String name = request.name();

        if (name == null || name.length() <= MAX_NAME_LENGTH) {
            return List.of();
        }

        return List.of(new ValidationIssue(
                code(),
                "Project name must not exceed "
                        + MAX_NAME_LENGTH + " characters"
        ));
    }
}
