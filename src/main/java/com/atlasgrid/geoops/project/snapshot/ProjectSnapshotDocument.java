package com.atlasgrid.geoops.project.snapshot;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * Versioned serializable document for trusted internal GeoOps snapshots.
 */
public final class ProjectSnapshotDocument implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final int CURRENT_SCHEMA_VERSION = 1;

    private final int schemaVersion;
    private final long capturedAtEpochMilli;
    private final ProjectSnapshotEntry[] projects;

    public ProjectSnapshotDocument(
            int schemaVersion,
            long capturedAtEpochMilli,
            ProjectSnapshotEntry[] projects
    ) {
        this.schemaVersion = schemaVersion;
        this.capturedAtEpochMilli = capturedAtEpochMilli;
        this.projects = Objects.requireNonNull(
                projects,
                "projects"
        ).clone();
    }

    public int schemaVersion() {
        return schemaVersion;
    }

    public long capturedAtEpochMilli() {
        return capturedAtEpochMilli;
    }

    public ProjectSnapshotEntry[] projects() {
        return projects.clone();
    }

    public int projectCount() {
        return projects.length;
    }

    @Override
    public String toString() {
        return "ProjectSnapshotDocument{" +
                "schemaVersion=" + schemaVersion +
                ", capturedAtEpochMilli=" + capturedAtEpochMilli +
                ", projects=" + Arrays.toString(projects) +
                '}';
    }
}
