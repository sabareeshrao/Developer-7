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
 * Produces deterministic sorted views without mutating ProjectCatalog order.
 *
 * <p>Arrays.sort() is used for a project-code array. Collections.sort() is
 * used for a mutable copy of GeoProject records with an explicit Comparator,
 * because GeoProject does not define one global natural ordering.</p>
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
