package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.validation.ValidationIssue;

import java.util.List;

/**
 * Raised when a project request violates one or more GeoOps domain-validation
 * rules.
 */
public class InvalidProjectRequestException extends GeoOpsProjectException {

    public static final String ERROR_CODE = "PROJECT_VALIDATION_FAILED";

    private final List<ValidationIssue> issues;

    public InvalidProjectRequestException(List<ValidationIssue> issues) {
        super(ERROR_CODE, "Project request failed domain validation");
        this.issues = List.copyOf(issues);
    }

    public List<ValidationIssue> issues() {
        return issues;
    }
}
