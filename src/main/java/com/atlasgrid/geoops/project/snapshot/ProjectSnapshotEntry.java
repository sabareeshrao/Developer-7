package com.atlasgrid.geoops.project.snapshot;

import com.atlasgrid.geoops.project.domain.GeoProject;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Serializable transfer representation used only by the trusted internal
 * GeoOps catalog-snapshot utility.
 *
 * <p>The core GeoProject domain type deliberately remains independent of Java
 * native serialization.</p>
 */
public record ProjectSnapshotEntry(
        String projectId,
        String projectCode,
        String name,
        String coordinateReferenceSystem,
        long createdAtEpochMilli
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public ProjectSnapshotEntry {
        Objects.requireNonNull(projectId, "projectId");
        Objects.requireNonNull(projectCode, "projectCode");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(
                coordinateReferenceSystem,
                "coordinateReferenceSystem"
        );
    }

    public static ProjectSnapshotEntry from(GeoProject project) {
        Objects.requireNonNull(project, "project");

        return new ProjectSnapshotEntry(
                project.id().toString(),
                project.projectCode(),
                project.name(),
                project.coordinateReferenceSystem(),
                project.createdAt().toEpochMilli()
        );
    }
}
