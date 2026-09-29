package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.domain.GeoProject;

import java.util.List;

/**
 * Read-only demonstration of the two sorting APIs used by GeoOps.
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
