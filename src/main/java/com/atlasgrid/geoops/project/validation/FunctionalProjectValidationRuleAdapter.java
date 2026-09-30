package com.atlasgrid.geoops.project.validation;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;

import java.util.List;
import java.util.Objects;

/**
 * Adapts one lambda-compatible ProjectValidationCheck to the existing
 * ProjectValidationRule contract, which still carries separate rule metadata.
 */
public final class FunctionalProjectValidationRuleAdapter
        implements ProjectValidationRule {

    private final String code;
    private final ProjectValidationCheck check;

    public FunctionalProjectValidationRuleAdapter(
            String code,
            ProjectValidationCheck check
    ) {
        this.code = Objects.requireNonNull(code, "code");
        this.check = Objects.requireNonNull(check, "check");
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public List<ValidationIssue> validate(CreateProjectRequest request) {
        return check.validate(request);
    }
}
