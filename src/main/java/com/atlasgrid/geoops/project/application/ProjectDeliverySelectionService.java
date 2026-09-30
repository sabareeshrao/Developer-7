package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.domain.GeoProject;
import com.atlasgrid.geoops.project.validation.ProjectValidationStandards;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Uses a Stream pipeline to select delivery candidates for one CRS.
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

        String requestedCrs =
                ProjectValidationStandards.normalizeCrsIdentifier(
                        coordinateReferenceSystem
                );

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
