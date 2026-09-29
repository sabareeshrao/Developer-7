package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.domain.GeoProject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Demonstrates practical Java Collections Framework usage in GeoOps.
 *
 * <p>The public method accepts the Collection interface so callers are not
 * coupled to one concrete collection implementation. Internally an ArrayList
 * preserves project-code intake order, while a HashSet removes duplicate CRS
 * values when calculating the distinct CRS count.</p>
 */
@Service
public class ProjectCollectionSummaryService {

    public ProjectCollectionSummary summarize(Collection<GeoProject> projects) {
        List<String> projectCodesInIntakeOrder = new ArrayList<>();
        Set<String> distinctCoordinateReferenceSystems = new HashSet<>();

        for (GeoProject project : projects) {
            projectCodesInIntakeOrder.add(project.projectCode());
            distinctCoordinateReferenceSystems.add(
                    project.coordinateReferenceSystem()
            );
        }

        return new ProjectCollectionSummary(
                projects.size(),
                distinctCoordinateReferenceSystems.size(),
                projectCodesInIntakeOrder
        );
    }
}
