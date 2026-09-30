package com.atlasgrid.geoops.project.validation;

import java.util.List;

/**
 * Validation result for one request in a parallel batch preflight.
 */
public record ProjectBatchValidationResult(
        String projectCode,
        boolean valid,
        List<ValidationIssue> issues
) {
    public ProjectBatchValidationResult {
        issues = List.copyOf(issues);
    }
}
