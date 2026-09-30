package com.atlasgrid.geoops.error;

/**
 * Stable machine-readable error codes used across GeoOps exception handling.
 */
public enum GeoOpsErrorCode {

    PROJECT_DUPLICATE,
    PROJECT_VALIDATION_FAILED,
    PROJECT_DATA_TRANSFER_FAILED,
    PROJECT_DATA_TRANSFER_IO_FAILED,
    REQUEST_VALIDATION_FAILED,
    REQUEST_MALFORMED,
    REQUEST_TOO_LARGE,
    INTERNAL_ERROR
}
