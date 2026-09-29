package com.atlasgrid.geoops.project.application;

import java.util.List;

/**
 * Read-only summary derived from the current in-memory GeoOps project
 * collection.
 */
public record ProjectCollectionSummary(
        int projectCount,
        int distinctCoordinateReferenceSystemCount,
        List<String> projectCodesInIntakeOrder
) {
    public ProjectCollectionSummary {
        projectCodesInIntakeOrder = List.copyOf(projectCodesInIntakeOrder);
    }
}
