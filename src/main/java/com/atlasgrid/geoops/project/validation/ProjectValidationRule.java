package com.atlasgrid.geoops.project.validation;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;

import java.util.List;

/**
 * Abstraction for one GeoOps project-intake validation rule.
 *
 * <p>Each implementation encapsulates one business rule. The validation
 * service depends on this interface, not on concrete rule classes.</p>
 */
public interface ProjectValidationRule {

    String code();

    List<ValidationIssue> validate(CreateProjectRequest request);
}
