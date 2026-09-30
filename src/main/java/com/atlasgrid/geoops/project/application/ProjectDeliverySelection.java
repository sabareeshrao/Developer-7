package com.atlasgrid.geoops.project.application;

import java.util.List;

/**
 * Immutable delivery-preparation selection for one coordinate reference system.
 */
public record ProjectDeliverySelection(
        String coordinateReferenceSystem,
        int projectCount,
        List<String> projectCodes
) {
    public ProjectDeliverySelection {
        projectCodes = List.copyOf(projectCodes);
    }
}
