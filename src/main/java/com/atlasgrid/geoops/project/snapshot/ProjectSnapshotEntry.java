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
        @SnapshotField(description = "Stable project identifier")
        String projectId,
        @SnapshotField(description = "Business project code")
        String projectCode,
        @SnapshotField(description = "Human-readable project name")
        String name,
        @SnapshotField(description = "Canonical EPSG coordinate system")
        String coordinateReferenceSystem,
        @SnapshotField(description = "Project creation time in epoch milliseconds")
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
