package com.atlasgrid.geoops.project.application;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Read-only summary derived from the current in-memory GeoOps project
 * collection.
 */
public record ProjectCollectionSummary(
        int projectCount,
        int distinctCoordinateReferenceSystemCount,
        List<String> projectCodesInIntakeOrder,
        Map<String, Integer> projectCountByCoordinateReferenceSystem
) {
    public ProjectCollectionSummary {
        projectCodesInIntakeOrder = List.copyOf(projectCodesInIntakeOrder);
        projectCountByCoordinateReferenceSystem =
                Collections.unmodifiableMap(
                        new LinkedHashMap<>(projectCountByCoordinateReferenceSystem)
                );
    }
}
