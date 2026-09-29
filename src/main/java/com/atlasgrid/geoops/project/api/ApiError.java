package com.atlasgrid.geoops.project.api;

import com.atlasgrid.geoops.error.GeoOpsErrorCode;

import java.time.Instant;
import java.util.List;

/**
 * Consistent JSON error payload returned by GeoOps REST exception handling.
 */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        GeoOpsErrorCode code,
        String message,
        List<String> details
) {
    public ApiError {
        details = List.copyOf(details);
    }
}
