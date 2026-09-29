package com.atlasgrid.geoops.project.application;

/**
 * Raised when a logical project identity already exists in the current
 * in-memory GeoOps registry.
 *
 * <p>This application exception intentionally has no HTTP annotation. The API
 * layer maps it to an HTTP response centrally through GeoOpsExceptionHandler.</p>
 */
public class DuplicateProjectException extends RuntimeException {

    public DuplicateProjectException(String projectCode) {
        super("Project already exists for projectCode=" + projectCode);
    }
}
