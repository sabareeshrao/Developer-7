package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.domain.GeoProject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Demonstrates practical Java Collections Framework usage in GeoOps.
 *
 * <p>The public method accepts the Collection interface so callers are not
 * coupled to one concrete collection implementation. Internally an ArrayList
 * preserves project-code intake order, a HashSet tracks distinct CRS values,
 * and a LinkedHashMap counts projects per CRS while retaining first-seen CRS
 * order for predictable API output.</p>
 */
@Service
public class ProjectCollectionSummaryService {

    public ProjectCollectionSummary summarize(Collection<GeoProject> projects) {
        List<String> projectCodesInIntakeOrder = new ArrayList<>();
        Set<String> distinctCoordinateReferenceSystems = new HashSet<>();
        Map<String, Integer> projectCountByCrs = new LinkedHashMap<>();

        for (GeoProject project : projects) {
            String crs = project.coordinateReferenceSystem();

            projectCodesInIntakeOrder.add(project.projectCode());
            distinctCoordinateReferenceSystems.add(crs);
            projectCountByCrs.merge(crs, 1, Integer::sum);
        }

        return new ProjectCollectionSummary(
                projects.size(),
                distinctCoordinateReferenceSystems.size(),
                projectCodesInIntakeOrder,
                projectCountByCrs
        );
    }
}
