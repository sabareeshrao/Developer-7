package com.atlasgrid.geoops.project.validation;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Application-level required-field validation shared by every project intake
 * path, including JSON REST requests and file imports.
 *
 * <p>Bean Validation still protects the HTTP boundary, but business-required
 * fields must also be enforced below MVC so callers such as CSV import cannot
 * bypass them.</p>
 */
@Component
@Order(0)
public final class RequiredProjectFieldsValidationRule
        implements ProjectValidationRule {

    @Override
    public String code() {
        return "REQUIRED_FIELDS";
    }

    @Override
    public List<ValidationIssue> validate(CreateProjectRequest request) {
        Objects.requireNonNull(request, "request");

        List<ValidationIssue> issues = new ArrayList<>();

        addIfBlank(issues, "projectCode", request.projectCode());
        addIfBlank(issues, "name", request.name());
        addIfBlank(
                issues,
                "coordinateReferenceSystem",
                request.coordinateReferenceSystem()
        );

        return List.copyOf(issues);
    }

    private void addIfBlank(
            List<ValidationIssue> issues,
            String field,
            String value
    ) {
        if (value == null || value.isBlank()) {
            issues.add(new ValidationIssue(
                    code(),
                    field + " must not be blank"
            ));
        }
    }
}
