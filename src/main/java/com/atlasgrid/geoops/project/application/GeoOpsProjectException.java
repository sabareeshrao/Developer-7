package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.error.GeoOpsErrorCode;

/**
 * Base type for GeoOps project-intake application exceptions.
 *
 * <p>The hierarchy gives project failures a common domain type and a stable
 * typed error code while keeping HTTP concerns out of the application layer.</p>
 */
public abstract class GeoOpsProjectException extends RuntimeException {

    private final GeoOpsErrorCode errorCode;

    protected GeoOpsProjectException(
            GeoOpsErrorCode errorCode,
            String message
    ) {
        super(message);
        this.errorCode = errorCode;
    }

    protected GeoOpsProjectException(
            GeoOpsErrorCode errorCode,
            String message,
            Throwable cause
    ) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public GeoOpsErrorCode errorCode() {
        return errorCode;
    }
}
