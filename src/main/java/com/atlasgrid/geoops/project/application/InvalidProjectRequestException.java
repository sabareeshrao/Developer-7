package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.validation.ValidationIssue;

import java.util.List;

/**
 * Raised when a project request violates one or more GeoOps domain-validation
 * rules.
 */
public class InvalidProjectRequestException extends RuntimeException {

    private final List<ValidationIssue> issues;

    public InvalidProjectRequestException(List<ValidationIssue> issues) {
        super("Project request failed domain validation");
        this.issues = List.copyOf(issues);
    }

    public List<ValidationIssue> issues() {
        return issues;
    }
}
