package com.atlasgrid.geoops.project.application;

import com.atlasgrid.geoops.project.domain.GeoProject;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Builds a plain-text GIS project manifest.
 *
 * <p>A new StringBuilder is created for every format call. That makes the
 * mutable buffer local to the current operation instead of sharing it across
 * concurrent web requests. StringBuffer synchronization is therefore not
 * needed here.</p>
 */
@Component
public class ProjectManifestFormatter {

    public String format(List<GeoProject> projects) {
        StringBuilder manifest = new StringBuilder();

        manifest.append("GEOOPS PROJECT MANIFEST").append(System.lineSeparator());
        manifest.append("projectCount=").append(projects.size()).append(System.lineSeparator());

        for (GeoProject project : projects) {
            manifest.append("---").append(System.lineSeparator())
                    .append("id=").append(project.id()).append(System.lineSeparator())
                    .append("projectCode=").append(project.projectCode()).append(System.lineSeparator())
                    .append("name=").append(project.name()).append(System.lineSeparator())
                    .append("crs=").append(project.coordinateReferenceSystem()).append(System.lineSeparator())
                    .append("createdAt=").append(project.createdAt()).append(System.lineSeparator());
        }

        return manifest.toString();
    }
}
