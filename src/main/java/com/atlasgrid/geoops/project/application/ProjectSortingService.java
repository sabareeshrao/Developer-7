package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.domain.GeoProject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Produces the deterministic project order used during delivery preparation
 * without mutating ProjectCatalog intake order.
 *
 * <p>The delivery report includes an alphabetic project-code index plus a
 * business-specific project ordering grouped by CRS and then by project code.
 * GeoProject intentionally has no universal natural ordering, so this use case
 * supplies an explicit Comparator.</p>
 */
@Service
public class ProjectSortingService {

    private static final Comparator<GeoProject> BY_CRS_THEN_PROJECT_CODE =
            Comparator.comparing(GeoProject::coordinateReferenceSystem)
                    .thenComparing(GeoProject::projectCode);

    public ProjectSortingView sort(Collection<GeoProject> projects) {
        Objects.requireNonNull(projects, "projects");

        String[] projectCodes = projects.stream()
                .map(GeoProject::projectCode)
                .toArray(String[]::new);

        Arrays.sort(projectCodes);

        List<GeoProject> projectsByCrsThenCode =
                new ArrayList<>(projects);

        Collections.sort(
                projectsByCrsThenCode,
                BY_CRS_THEN_PROJECT_CODE
        );

        return new ProjectSortingView(
                Arrays.asList(projectCodes),
                projectsByCrsThenCode
        );
    }
}
