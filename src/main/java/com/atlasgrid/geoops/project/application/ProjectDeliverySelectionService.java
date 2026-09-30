package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.domain.GeoProject;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Uses a Stream pipeline to select delivery candidates for one CRS.
 *
 * <p>The pipeline is read-only: filter selects matching projects, map converts
 * them to project codes, sorted produces deterministic output, and toList is
 * the terminal operation. ProjectCatalog intake order is not modified.</p>
 */
@Service
public class ProjectDeliverySelectionService {

    public ProjectDeliverySelection select(
            Collection<GeoProject> projects,
            String coordinateReferenceSystem
    ) {
        Objects.requireNonNull(projects, "projects");
        Objects.requireNonNull(
                coordinateReferenceSystem,
                "coordinateReferenceSystem"
        );

        String requestedCrs = coordinateReferenceSystem.trim();

        List<String> projectCodes = projects.stream()
                .filter(project -> requestedCrs.equals(
                        project.coordinateReferenceSystem()
                ))
                .map(GeoProject::projectCode)
                .sorted()
                .toList();

        return new ProjectDeliverySelection(
                requestedCrs,
                projectCodes.size(),
                projectCodes
        );
    }
}
