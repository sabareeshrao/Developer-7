package com.atlasgrid.geoops.project.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Minimal domain model for a GIS project entering the GeoOps workflow.
 *
 * Future sets will grow this model with coordinate systems, source files,
 * validation results, spatial metadata and delivery status.
 */
public record GeoProject(
        UUID id,
        String projectCode,
        String name,
        String coordinateReferenceSystem,
        Instant createdAt
) {
}
