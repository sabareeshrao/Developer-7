package com.atlasgrid.geoops.project.validation;

/**
 * Immutable validation issue returned by a project validation rule.
 */
public record ValidationIssue(
        String ruleCode,
        String message
) {
}
