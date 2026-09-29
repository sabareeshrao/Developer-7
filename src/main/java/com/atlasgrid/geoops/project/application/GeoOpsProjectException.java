package com.atlasgrid.geoops.project.application;

import java.util.Objects;

/**
 * Base type for GeoOps project-intake application exceptions.
 *
 * <p>The hierarchy gives project failures a common domain type and a stable
 * error code while keeping HTTP concerns out of the application layer.</p>
 */
public abstract class GeoOpsProjectException extends RuntimeException {

    private final String errorCode;

    protected GeoOpsProjectException(String errorCode, String message) {
        super(message);
        this.errorCode = Objects.requireNonNull(errorCode, "errorCode");
    }

    protected GeoOpsProjectException(
            String errorCode,
            String message,
            Throwable cause
    ) {
        super(message, cause);
        this.errorCode = Objects.requireNonNull(errorCode, "errorCode");
    }

    public String errorCode() {
        return errorCode;
    }
}
