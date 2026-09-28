package com.atlasgrid.geoops.project.validation;

import java.util.List;

/**
 * Immutable result returned after all project validation rules execute.
 */
public record ProjectValidationReport(
        boolean valid,
        List<ValidationIssue> issues
) {
    public ProjectValidationReport {
        issues = List.copyOf(issues);
    }
}
