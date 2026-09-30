package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.domain.GeoProject;

import java.util.List;

/**
 * Read-only delivery-preparation sorting result.
 *
 * <p>Project codes are available as an alphabetic index while full projects
 * use the business-specific CRS-then-project-code delivery order.</p>
 */
public record ProjectSortingView(
        List<String> projectCodesAlphabetically,
        List<GeoProject> projectsByCrsThenCode
) {
    public ProjectSortingView {
        projectCodesAlphabetically = List.copyOf(projectCodesAlphabetically);
        projectsByCrsThenCode = List.copyOf(projectsByCrsThenCode);
    }
}
