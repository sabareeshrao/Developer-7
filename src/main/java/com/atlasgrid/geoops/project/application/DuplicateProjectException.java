package com.atlasgrid.geoops.project.application;

/**
 * Raised when a logical project identity already exists in the current
 * in-memory GeoOps registry.
 */
public class DuplicateProjectException extends GeoOpsProjectException {

    public static final String ERROR_CODE = "PROJECT_DUPLICATE";

    public DuplicateProjectException(String projectCode) {
        super(
                ERROR_CODE,
                "Project already exists for projectCode=" + projectCode
        );
    }
}
