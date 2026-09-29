package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.error.GeoOpsErrorCode;

/**
 * Raised when a logical project identity already exists in the current
 * in-memory GeoOps registry.
 */
public class DuplicateProjectException extends GeoOpsProjectException {

    public static final GeoOpsErrorCode ERROR_CODE =
            GeoOpsErrorCode.PROJECT_DUPLICATE;

    public DuplicateProjectException(String projectCode) {
        super(
                ERROR_CODE,
                "Project already exists for projectCode=" + projectCode
        );
    }
}
