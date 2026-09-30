package com.atlasgrid.geoops.project.snapshot;

/**
 * Stable failure categories for the internal GeoOps snapshot boundary.
 */
public enum ProjectSnapshotFailure {
    SERIALIZATION_FAILED,
    CORRUPT_STREAM,
    INCOMPATIBLE_CLASS,
    MISSING_CLASS,
    REJECTED_TYPE,
    UNSUPPORTED_SCHEMA,
    WRONG_ROOT_TYPE,
    IO_FAILURE
}
