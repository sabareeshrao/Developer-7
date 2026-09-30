package com.atlasgrid.geoops.project.validation;

import com.atlasgrid.geoops.project.api.CreateProjectRequest;

import java.util.List;

/**
 * Functional seam used to introduce lambda-based validation incrementally
 * without breaking the older ProjectValidationRule contract.
 */
@FunctionalInterface
public interface ProjectValidationCheck {

    List<ValidationIssue> validate(CreateProjectRequest request);
}
