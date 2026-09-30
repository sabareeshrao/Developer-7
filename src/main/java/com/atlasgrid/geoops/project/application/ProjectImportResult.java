package com.atlasgrid.geoops.project.application;

import java.util.List;

/**
 * Summary returned after a synchronous CSV project import.
 */
public record ProjectImportResult(
        int importedCount,
        List<String> projectCodes
) {
    public ProjectImportResult {
        projectCodes = List.copyOf(projectCodes);
    }
}
