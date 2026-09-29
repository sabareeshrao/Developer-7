package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.domain.GeoProject;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Builds a sorted unique catalog of coordinate reference system codes used by
 * accepted GeoOps projects.
 *
 * <p>TreeSet is used because this view needs both Set uniqueness and natural
 * sorted iteration. This does not replace HashSet in ProjectCatalog, where the
 * primary requirement is fast identity membership rather than sorted output.</p>
 */
@Service
public class ProjectCrsCatalogService {

    public List<String> sortedUniqueCoordinateReferenceSystems(
            Collection<GeoProject> projects
    ) {
        Objects.requireNonNull(projects, "projects");

        SortedSet<String> coordinateReferenceSystems = new TreeSet<>();

        for (GeoProject project : projects) {
            coordinateReferenceSystems.add(
                    project.coordinateReferenceSystem()
            );
        }

        return List.copyOf(coordinateReferenceSystems);
    }
}
