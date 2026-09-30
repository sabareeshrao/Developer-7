package com.atlasgrid.geoops.error;

/**
 * Stable machine-readable error codes used across GeoOps exception handling.
 *
 * <p>The enum contains identifiers only. HTTP status mapping remains in the
 * REST layer so application/domain exceptions stay independent from transport
 * concerns.</p>
 */
public enum GeoOpsErrorCode {

    PROJECT_DUPLICATE,
    PROJECT_VALIDATION_FAILED,
    PROJECT_DATA_TRANSFER_FAILED,
    REQUEST_VALIDATION_FAILED,
    REQUEST_MALFORMED,
    INTERNAL_ERROR
}
